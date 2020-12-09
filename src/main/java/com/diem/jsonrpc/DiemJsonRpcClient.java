// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem.jsonrpc;

import com.diem.utils.TransactionUtils;
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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.diem.DiemClient;
import com.diem.types.AccountAddress;
import com.diem.utils.AccountAddressUtils;
import com.diem.utils.HashUtils;
import com.diem.utils.Hex;
import com.diem.DiemException;
import com.diem.types.ChainId;
import com.diem.types.SignedTransaction;

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

public class DiemJsonRpcClient implements DiemClient {

    /**
     * Default retry for handling StaleResponseException.
     * Submit will not be retried, the stale response exception will be threw and client should
     * decide what to do.
     */
    public static final Retry<Response> DEFAULT_RETRY_ON_STALE_RESPONSE = new Retry<>(
            5, 200, StaleResponseException.class);

    private static CloseableHttpClient createDefaultHttpClient() {
        return HttpClients.custom().setKeepAliveStrategy((response, context) -> 30 * 1000).build();
    }

    private final LedgerState state;
    private final URI serverURL;
    private final HttpClient httpClient;
    private Retry<Response> retryGetMethods;

    public DiemJsonRpcClient(String serverURL, ChainId chainId) {
        this(serverURL, createDefaultHttpClient(), chainId, DEFAULT_RETRY_ON_STALE_RESPONSE);
    }

    public DiemJsonRpcClient(String serverURL, HttpClient httpClient, ChainId chainId, Retry<Response> retryGetMethods) {
        try {
            this.serverURL = new URL(serverURL).toURI();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
        this.httpClient = httpClient;
        this.state = new LedgerState(chainId);
        this.retryGetMethods = retryGetMethods;
    }

    @Override
    public List<JsonRpc.Transaction> getTransactions(@Unsigned long fromVersion, int limit, boolean includeEvents)
            throws DiemException {
        List<Object> params = new ArrayList<>();
        params.add(fromVersion);
        params.add(limit);
        params.add(includeEvents);

        Response resp = call(Method.get_transactions, params);
        return new Builder<JsonRpc.Transaction>().parseList(resp, JsonRpc.Transaction.newBuilder());
    }

    @Override
    public JsonRpc.Account getAccount(String address) throws DiemException {
        List<Object> params = new ArrayList<>();
        params.add(address);

        Response resp = call(Method.get_account, params);
        return new Builder<JsonRpc.Account>().parse(resp, JsonRpc.Account.newBuilder());
    }

    @Override
    public JsonRpc.Account getAccount(AccountAddress address) throws DiemException {
        return getAccount(AccountAddressUtils.hex(address));
    }

    @Override
    public JsonRpc.Metadata getMetadata() throws DiemException {
        Response resp = call(Method.get_metadata, new ArrayList<>());
        return new Builder<JsonRpc.Metadata>().parse(resp, JsonRpc.Metadata.newBuilder());
    }

    @Override
    public JsonRpc.Metadata getMetadata(@Unsigned long version) throws DiemException {
        List<Object> params = new ArrayList<>();
        params.add(version);

        Response resp = call(Method.get_metadata, params);
        return new Builder<JsonRpc.Metadata>().parse(resp, JsonRpc.Metadata.newBuilder());
    }

    @Override
    public List<JsonRpc.CurrencyInfo> getCurrencies() throws DiemException {
        Response resp = call(Method.get_currencies, new ArrayList<>());
        return new Builder<JsonRpc.CurrencyInfo>().parseList(resp, JsonRpc.CurrencyInfo.newBuilder());
    }

    @Override
    public JsonRpc.Transaction getAccountTransaction(String address, @Unsigned long sequence, boolean includeEvents)
            throws DiemException {
        List<Object> params = new ArrayList<>();
        params.add(address);
        params.add(sequence);
        params.add(includeEvents);

        Response resp = call(Method.get_account_transaction, params);
        return new Builder<JsonRpc.Transaction>().parse(resp, JsonRpc.Transaction.newBuilder());
    }

    @Override
    public JsonRpc.Transaction getAccountTransaction(AccountAddress address, @Unsigned long sequence, boolean includeEvents)
            throws DiemException {
        return getAccountTransaction(Hex.encode(address.value), sequence, includeEvents);
    }

    @Override
    public List<JsonRpc.Transaction> getAccountTransactions(String address, @Unsigned long start, int limit, boolean includeEvents)
            throws DiemException {
        return this.getAccountTransactions(AccountAddressUtils.create(address), start, limit, includeEvents);
    }

    @Override
    public List<JsonRpc.Transaction> getAccountTransactions(AccountAddress address, @Unsigned long start, int limit, boolean includeEvents)
            throws DiemException {
        List<Object> params = new ArrayList<>();
        params.add(Hex.encode(address.value));
        params.add(start);
        params.add(limit);
        params.add(includeEvents);

        Response resp = call(Method.get_account_transactions, params);
        return new Builder<JsonRpc.Transaction>().parseList(resp, JsonRpc.Transaction.newBuilder());
    }

    @Override
    public void submit(String data) throws DiemException {
        List<Object> params = new ArrayList<>();
        params.add(data);

        callWithoutRetry(Method.submit, params);
    }

    @Override
    public void submit(SignedTransaction txn) throws DiemException {
        String hex;
        try {
            hex = Hex.encode(txn.lcsSerialize());
        } catch (SerializationError e) {
            throw new RuntimeException(e);
        }
        submit(hex);
    }

    @Override
    public JsonRpc.Transaction waitForTransaction(String signedTxnHex, int timeout) throws DiemException {
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
    public JsonRpc.Transaction waitForTransaction(SignedTransaction signedTransaction, int timeout) throws DiemException {
        return waitForTransaction(signedTransaction.raw_txn.sender,
                signedTransaction.raw_txn.sequence_number, HashUtils.transactionHash(signedTransaction),
                signedTransaction.raw_txn.expiration_timestamp_secs, timeout);
    }

    @Override
    public JsonRpc.Transaction waitForTransaction(String address, @Unsigned long sequence, String transactionHash,
                                                  @Unsigned long expirationTimeSec, int timeout) throws DiemException {
        return this.waitForTransaction(AccountAddressUtils.create(address), sequence, transactionHash, expirationTimeSec, timeout);
    }

    @Override
    public JsonRpc.Transaction waitForTransaction(AccountAddress address, @Unsigned long sequence, String transactionHash,
                                                  @Unsigned long expirationTimeSec, int timeout) throws DiemException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, timeout);
        Date maxTime = calendar.getTime();

        while (Calendar.getInstance().getTime().before(maxTime)) {
            JsonRpc.Transaction txn = getAccountTransaction(address, sequence, true);

            if (txn != null) {
                if (!txn.getHash().equalsIgnoreCase(transactionHash)) {
                    throw new DiemTransactionHashMismatchException(txn, transactionHash);
                }
                if (!TransactionUtils.isExecuted(txn)) {
                    throw new DiemTransactionExecutionFailedException(txn);
                }

                return txn;
            }
            if (expirationTimeSec * 1_000_000 <= state.getTimestampUsecs()) {
                throw new DiemTransactionExpiredException(expirationTimeSec, state.getTimestampUsecs());
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        throw new DiemTransactionWaitTimeoutException(timeout);
    }

    @Override
    public List<JsonRpc.Event> getEvents(String eventsKey, @Unsigned long start, @Unsigned long limit)
            throws DiemException {
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

        @SuppressWarnings(value = "unchecked")
        private T parse(JsonElement ele, Message.Builder factory) throws InvalidResponseException {
            try {
                PARSER.merge(ele.toString(), factory);
            } catch (InvalidProtocolBufferException e) {
                throw new InvalidResponseException(e);
            }

            return (T) factory.build();
        }
    }

    public Response call(final Method method, final List<Object> params) throws DiemException {
        try {
            return this.retryGetMethods.execute(() -> DiemJsonRpcClient.this.callWithoutRetry(method, params));
        } catch (DiemException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Response callWithoutRetry(final Method method, final List<Object> params) throws DiemException {
        Request request = new Request(0, method.name(), params.toArray());

        String body = makeHttpCall(new Gson().toJson(request));
        Response resp = new Gson().fromJson(body, Response.class);
        if (resp.getError() != null) {
            throw new JsonRpcError(resp.getError().toString());
        }

        state.save(resp.getDiemChainId(), resp.getDiemLedgerVersion(), resp.getDiemLedgerTimestampusec());

        return resp;
    }

    private String makeHttpCall(String requestJson) throws DiemException {
        HttpPost post = new HttpPost(serverURL);
        post.addHeader("content-type", "application/json");
        try {
            post.setEntity(new StringEntity(requestJson));
        } catch (UnsupportedEncodingException e) {
            throw new DiemException(e);
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
            throw new DiemException(e);
        }
        if (statusCode != 200) {
            throw new InvalidResponseException(statusCode, body);
        }
        return body;
    }
}
