// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.jsonrpc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import com.novi.serde.DeserializationError;
import com.novi.serde.SerializationError;
import com.novi.serde.Unsigned;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.libra.LibraClient;
import org.libra.LibraException;
import org.libra.jsonrpctypes.JsonRpc;
import org.libra.types.AccountAddress;
import org.libra.types.ChainId;
import org.libra.types.SignedTransaction;
import org.libra.utils.AccountAddressUtils;
import org.libra.utils.HashUtils;
import org.libra.utils.Hex;
import org.libra.utils.TransactionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LibraJsonRpcClient implements LibraClient {

    private final LedgerState state;
    private final URI serverURL;
    private final HttpClient httpClient;

    public LibraJsonRpcClient(String serverURL, ChainId chainId) {
        this(serverURL, HttpClients.createDefault(), chainId);
    }

    public LibraJsonRpcClient(String serverURL, HttpClient httpClient, ChainId chainId) {
        try {
            this.serverURL = new URL(serverURL).toURI();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
        this.httpClient = httpClient;
        this.state = new LedgerState(chainId);
    }

    @Override
    public List<JsonRpc.Transaction> getTransactions(@Unsigned long fromVersion, int limit, boolean includeEvents)
            throws LibraException {
        List<Object> params = new ArrayList<>();
        params.add(fromVersion);
        params.add(limit);
        params.add(includeEvents);

        Response resp = call(Method.get_transactions, params);
        return new Builder<JsonRpc.Transaction>().parseList(resp, JsonRpc.Transaction.newBuilder());
    }

    @Override
    public JsonRpc.Account getAccount(String address) throws LibraException {
        List<Object> params = new ArrayList<>();
        params.add(address);

        Response resp = call(Method.get_account, params);
        return new Builder<JsonRpc.Account>().parse(resp, JsonRpc.Account.newBuilder());
    }

    @Override
    public JsonRpc.Account getAccount(AccountAddress address) throws LibraException {
        return getAccount(AccountAddressUtils.hex(address));
    }

    @Override
    public JsonRpc.BlockMetadata getMetadata() throws LibraException {
        Response resp = call(Method.get_metadata, new ArrayList<>());
        return new Builder<JsonRpc.BlockMetadata>().parse(resp, JsonRpc.BlockMetadata.newBuilder());
    }

    @Override
    public JsonRpc.BlockMetadata getMetadata(@Unsigned long version) throws LibraException {
        List<Object> params = new ArrayList<>();
        params.add(version);

        Response resp = call(Method.get_metadata, params);
        return new Builder<JsonRpc.BlockMetadata>().parse(resp, JsonRpc.BlockMetadata.newBuilder());
    }

    @Override
    public List<JsonRpc.CurrencyInfo> getCurrencies() throws LibraException {
        Response resp = call(Method.get_currencies, new ArrayList<>());
        return new Builder<JsonRpc.CurrencyInfo>().parseList(resp, JsonRpc.CurrencyInfo.newBuilder());
    }

    @Override
    public JsonRpc.Transaction getAccountTransaction(String address, @Unsigned long sequence, boolean includeEvents)
            throws LibraException {
        List<Object> params = new ArrayList<>();
        params.add(address);
        params.add(sequence);
        params.add(includeEvents);

        Response resp = call(Method.get_account_transaction, params);
        return new Builder<JsonRpc.Transaction>().parse(resp, JsonRpc.Transaction.newBuilder());
    }

    @Override
    public JsonRpc.Transaction getAccountTransaction(AccountAddress address, @Unsigned long sequence, boolean includeEvents)
            throws LibraException {
        return getAccountTransaction(Hex.encode(address.value), sequence, includeEvents);
    }

    @Override
    public List<JsonRpc.Transaction> getAccountTransactions(AccountAddress address, @Unsigned long start, int limit, boolean includeEvents)
            throws LibraException {
        List<Object> params = new ArrayList<>();
        params.add(Hex.encode(address.value));
        params.add(start);
        params.add(limit);
        params.add(includeEvents);

        Response resp = call(Method.get_account_transactions, params);
        return new Builder<JsonRpc.Transaction>().parseList(resp, JsonRpc.Transaction.newBuilder());
    }

    @Override
    public void submit(String data) throws LibraException {
        List<Object> params = new ArrayList<>();
        params.add(data);

        call(Method.submit, params);
    }

    @Override
    public void submit(SignedTransaction txn) throws LibraException {
        String hex;
        try {
            hex = Hex.encode(txn.lcsSerialize());
        } catch (SerializationError e) {
            throw new RuntimeException(e);
        }
        submit(hex);
    }

    @Override
    public JsonRpc.Transaction waitForTransaction(String signedTxnHex, int timeout) throws LibraException {
        byte[] bytes = Hex.decode(signedTxnHex);
        SignedTransaction signedTransaction;

        try {
            signedTransaction = SignedTransaction.lcsDeserialize(bytes);
        } catch (DeserializationError e) {
            throw new IllegalArgumentException(
                    String.format("Deserialize given hex string as SignedTransaction LCS failed: %s", e.getMessage()));
        }

        return waitForTransaction(signedTransaction, timeout);
    }

    @Override
    public JsonRpc.Transaction waitForTransaction(SignedTransaction signedTransaction, int timeout) throws LibraException {
        return waitForTransaction(signedTransaction.raw_txn.sender,
                signedTransaction.raw_txn.sequence_number, HashUtils.transactionHash(signedTransaction),
                signedTransaction.raw_txn.expiration_timestamp_secs, timeout);
    }

    @Override
    public JsonRpc.Transaction waitForTransaction(AccountAddress address, @Unsigned long sequence, String transactionHash,
                                                           @Unsigned long expirationTimeSec, int timeout) throws LibraException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, timeout);
        Date maxTime = calendar.getTime();

        while (Calendar.getInstance().getTime().before(maxTime)) {
            JsonRpc.Transaction accountTransaction = getAccountTransaction(address, sequence, true);

            if (accountTransaction != null) {
                if (!accountTransaction.getHash().equalsIgnoreCase(transactionHash)) {
                    throw new LibraException(
                            String.format("found transaction, but hash does not match, given %s, but got %s",
                                    transactionHash, accountTransaction.getHash()));
                }
                if (!TransactionUtils.isExecuted(accountTransaction)) {
                    throw new LibraException(
                            String.format("transaction execution failed: %s", accountTransaction.getVmStatus()));
                }

                return accountTransaction;
            }
            if (expirationTimeSec * 1_000_000 <= state.getTimestampUsecs()) {
                throw new LibraException("transaction expired");
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        throw new LibraException(
                String.format("transaction not found within timeout period: %d (seconds)", timeout));
    }

    @Override
    public List<JsonRpc.Event> getEvents(String eventsKey, @Unsigned long start, @Unsigned long limit)
            throws LibraException {
        List<Object> params = new ArrayList<>();
        params.add(eventsKey);
        params.add(start);
        params.add(limit);

        Response resp = call(Method.get_events, params);
        return new Builder<JsonRpc.Event>().parseList(resp, JsonRpc.Event.newBuilder());
    }

    private static class Builder<T extends MessageOrBuilder> {

        public static final JsonFormat.Parser PARSER = JsonFormat.parser().ignoringUnknownFields();

        private List<T> parseList(Response response, Message.Builder factory) throws InvalidResponseException {
            List<T> ret = new ArrayList<>();
            if (response.getResult() != null && !response.getResult().isJsonNull()) {
                if (!response.getResult().isJsonArray()) {
                    throw new InvalidResponseException(String.format("expect array but got something else: %s", response.getResult()));
                }
                JsonArray array = response.getResult().getAsJsonArray();
                for (JsonElement ele : array) {
                    ret.add(parse(ele, factory.clone()));
                }
            }
            return ret;
        }

        private T parse(Response response, Message.Builder factory) throws InvalidResponseException {
            if (response.getResult() != null && !response.getResult().isJsonNull()) {
                if (!response.getResult().isJsonObject()) {
                    throw new InvalidResponseException(String.format("expect array but got something else: %s", response.getResult()));
                }
                return parse(response.getResult(), factory);
            }
            return null;
        }

        private T parse(JsonElement ele, Message.Builder factory) throws InvalidResponseException {
            try {
                PARSER.merge(ele.toString(), factory);
            } catch (InvalidProtocolBufferException e) {
                throw new InvalidResponseException(e);
            }
            return (T) factory.build();
        }
    }

    public Response call(Method method, List<Object> params) throws LibraException {
        Request request = new Request(0, method.name(), params.toArray());
        String body = makeHttpCall(new Gson().toJson(request));
        Response resp = new Gson().fromJson(body, Response.class);
        if (resp.getError() != null) {
            throw new JsonRpcError(resp.getError().toString());
        }

        state.save(resp.getLibraChainId(), resp.getLibraLedgerVersion(), resp.getLibraLedgerTimestampusec());

        return resp;
    }

    private String makeHttpCall(String requestJson) throws LibraException {
        HttpPost post = new HttpPost(serverURL);
        post.addHeader("content-type", "application/json");
        try {
            post.setEntity(new StringEntity(requestJson));
        } catch (UnsupportedEncodingException e) {
            throw new LibraException(e);
        }
        HttpResponse response;
        try {
            response = httpClient.execute(post);
        } catch (IOException e) {
            throw new RemoteCallException(e);
        }
        int statusCode = response.getStatusLine().getStatusCode();
        String body;
        try {
            body = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new LibraException(e);
        }
        if (statusCode != 200) {
            throw new InvalidResponseException(statusCode, body);
        }
        return body;
    }
}
