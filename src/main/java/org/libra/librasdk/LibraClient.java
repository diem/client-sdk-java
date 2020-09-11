// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.novi.serde.Bytes;
import com.novi.serde.Unsigned;
import org.libra.librasdk.dto.*;
import org.libra.librasdk.jsonrpc.JSONRPCClient;
import org.libra.librasdk.jsonrpc.JSONRPCErrorException;
import org.libra.librasdk.jsonrpc.UnexpectedResponseResultException;
import org.libra.types.AccountAddress;
import org.libra.types.Script;
import org.libra.types.SignedTransaction;
import org.libra.types.TypeTag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.libra.stdlib.Helpers.encode_peer_to_peer_with_metadata_script;

public class LibraClient implements Client {

    private JSONRPCClient jsonRpcClient;
    private LibraLedgerState libraLedgerState;

    public LibraClient(String url, int chainId) {
        this.jsonRpcClient = new JSONRPCClient(url);
        this.libraLedgerState = new LibraLedgerState(chainId);
    }

    public List<Transaction> getTransactions(@Unsigned long fromVersion, int limit, boolean includeEvents) throws LibraSDKException {
        List<Object> params = new ArrayList<>();
        params.add(fromVersion);
        params.add(limit);
        params.add(includeEvents);

        Transaction[] transactions = executeCall(Method.get_transactions, params,
                Transaction[].class);
        return Arrays.asList(transactions);
    }

    public Account getAccount(String address) throws LibraSDKException {
        List<Object> params = new ArrayList<>();
        params.add(address);

        Account account = executeCall(Method.get_account, params, Account.class);
        return account;
    }


    public Metadata getMetadata() throws LibraSDKException {
        Metadata metadata = executeCall(Method.get_metadata, new ArrayList<>(), Metadata.class);
        return metadata;
    }

    public Metadata getMetadata(@Unsigned long version) throws LibraSDKException {
        List<Object> params = new ArrayList<>();
        params.add(version);

        Metadata metadata = executeCall(Method.get_metadata, params, Metadata.class);
        return metadata;
    }

    public Currency[] getCurrencies() throws LibraSDKException {
        Currency[] currencies = executeCall(Method.get_currencies, new ArrayList<>(),
                Currency[].class);

        return currencies;
    }

    public Transaction getAccountTransaction(String address, @Unsigned long sequence,
                                             boolean includeEvents) throws LibraSDKException {
        List<Object> params = new ArrayList<>();
        params.add(address);
        params.add(sequence);
        params.add(includeEvents);

        Transaction transaction = executeCall(Method.get_account_transaction, params,
                Transaction.class);

        return transaction;
    }

    public void submit(String data) throws LibraSDKException {
        List<Object> params = new ArrayList<>();
        params.add(data);

        executeCall(Method.submit, params, null);
    }

    @Override
    public SignedTransaction transfer(String senderAccountAddress, String libraAuthKey,
                                      String privateKey,
                                      String publicKey, String receiverAccountAddress,
                                      @Unsigned long amount, @Unsigned long maxGasAmount, @Unsigned long gasPriceUnit,
                                      String currencyCode,
                                      @Unsigned long expirationTimestampSecs, byte chainId,
                                      byte[] metadata, byte[] metadataSignature) throws LibraSDKException {

        LocalAccount localAccount = new LocalAccount(senderAccountAddress, libraAuthKey,
                privateKey, publicKey);
        AccountAddress accountAddressObject = Utils.hexToAddress(receiverAccountAddress);
        Script script = createP2PScript(accountAddressObject, currencyCode, amount, metadata,
                metadataSignature);
        Account account = getAccount(localAccount.libra_account_address);

        SignedTransaction signedTransaction;
        signedTransaction = Utils.signTransaction(localAccount,
                account.sequence_number,
                script,
                maxGasAmount,
                gasPriceUnit,
                currencyCode,
                expirationTimestampSecs,
                chainId);
        String lcsHex = Utils.toLCSHex(signedTransaction);
        submit(lcsHex);

        return signedTransaction;

    }

    public Transaction waitForTransaction(String address, @Unsigned long sequence, boolean includeEvents,
                                          @Unsigned long timeoutMillis) throws InterruptedException,
            LibraSDKException {
        long timeoutAt = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < timeoutAt) {
            try {
                Transaction transaction = this.getAccountTransaction(address, sequence, includeEvents);
                if (transaction != null) {
                    return transaction;
                }
            } catch (StaleResponseException e) {
                // ignore stale response exception and retry
                continue;
            }
            Thread.sleep(500);
        }

        return null;
    }

    public List<Event> getEvents(String eventsKey, @Unsigned long start, @Unsigned long limit) throws LibraSDKException {
        List<Object> params = new ArrayList<>();
        params.add(eventsKey);
        params.add(start);
        params.add(limit);

        Event[] events = executeCall(Method.get_events, params, Event[].class);
        return Arrays.asList(events);
    }

    @Override
    public void close() throws IOException {
        this.jsonRpcClient.close();
    }

    private <T> T executeCall(Method method, List<Object> params, Class<T> responseType) throws LibraSDKException {
        String response = jsonRpcClient.call(method, params);
        T result;

        try {
            LibraResponse libraResponse = new Gson().fromJson(response, LibraResponse.class);

            if (libraResponse.getError() != null) {
                throw new JSONRPCErrorException(libraResponse.getError().toString());
            }

            libraLedgerState.handleLedgerState(libraResponse.getLibraChainId(),
                    libraResponse.getLibraLedgerVersion(),
                    libraResponse.getLibraLedgerTimestampusec());
            result = null;

            if (responseType != null) {
                result = new Gson().fromJson(libraResponse.getResult(), responseType);
            }
        } catch (JsonSyntaxException e) {
            throw new UnexpectedResponseResultException(e);
        }

        return result;
    }

    private Script createP2PScript(AccountAddress address, String currencyCode, @Unsigned long amount,
                                   byte[] metadata, byte[] metadataSignature) {
        TypeTag token = Utils.createCurrencyCodeTypeTag(currencyCode);
        return encode_peer_to_peer_with_metadata_script(
                token,
                address,
                amount,
                new Bytes(metadata),
                new Bytes(metadataSignature)
        );
    }

}

