// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

import com.google.gson.Gson;
import com.novi.serde.Unsigned;
import org.libra.jsonrpc.Client;
import org.libra.jsonrpc.Response;
import org.libra.librasdk.dto.Currency;
import org.libra.librasdk.dto.*;
import org.libra.types.SignedTransaction;

import java.util.*;

import static org.libra.librasdk.TransactionHash.hashTransaction;
import static org.libra.librasdk.Utils.*;

public class LibraClient implements org.libra.librasdk.Client {

    private Client jsonRpcClient;
    private LibraLedgerState libraLedgerState;

    public LibraClient(String url, int chainId) {
        this.jsonRpcClient = new Client(url);
        this.libraLedgerState = new LibraLedgerState(chainId);
    }

    public List<Transaction> getTransactions(@Unsigned long fromVersion, int limit, boolean includeEvents)
            throws LibraSDKException {
        List<Object> params = new ArrayList<>();
        params.add(fromVersion);
        params.add(limit);
        params.add(includeEvents);

        Transaction[] transactions = executeCall(Method.get_transactions, params, Transaction[].class);
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
        Currency[] currencies = executeCall(Method.get_currencies, new ArrayList<>(), Currency[].class);

        return currencies;
    }

    public Transaction getAccountTransaction(String address, @Unsigned long sequence, boolean includeEvents)
            throws LibraSDKException {
        List<Object> params = new ArrayList<>();
        params.add(address);
        params.add(sequence);
        params.add(includeEvents);

        return executeCall(Method.get_account_transaction, params, Transaction.class);
    }

    public void submit(String data) throws LibraSDKException {
        List<Object> params = new ArrayList<>();
        params.add(data);

        executeCall(Method.submit, params, null);
    }

    public Transaction waitForTransaction(String signedTransactionHash, int timeout) throws LibraSDKException {
        byte[] bytes = hexToBytes(signedTransactionHash);
        SignedTransaction signedTransaction;

        try {
            signedTransaction = SignedTransaction.lcsDeserialize(bytes);
        } catch (Exception e) {
            throw new LibraSDKException(
                    String.format("Deserialize given hex string as SignedTransaction LCS failed: %s", e.getMessage()));
        }

        return waitForTransaction(signedTransaction, timeout);
    }

    public Transaction waitForTransaction(SignedTransaction signedTransaction, int timeout) throws LibraSDKException {
        return waitForTransaction(addressToHex(signedTransaction.raw_txn.sender),
                signedTransaction.raw_txn.sequence_number, hashTransaction(signedTransaction),
                signedTransaction.raw_txn.expiration_timestamp_secs, timeout);
    }

    public Transaction waitForTransaction(String address, @Unsigned long sequence, String transactionHash,
                                          @Unsigned long expirationTimeSec, int timeout) throws LibraSDKException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, timeout);
        Date maxTime = calendar.getTime();

        while (Calendar.getInstance().getTime().before(maxTime)) {
            Transaction accountTransaction = getAccountTransaction(address, sequence, true);

            if (accountTransaction != null) {
                if (!accountTransaction.hash.equalsIgnoreCase(transactionHash)) {
                    throw new LibraSDKException(
                            String.format("found transaction, but hash does not match, given %s, but got %s",
                                    transactionHash, accountTransaction.hash));
                }
                if (!accountTransaction.isExecuted()) {
                    throw new LibraSDKException(
                            String.format("transaction execution failed: %s", accountTransaction.vm_status));
                }

                return accountTransaction;
            }
            if (expirationTimeSec * 1000000 <= libraLedgerState.getTimestampUsecs()) {
                throw new LibraSDKException("transaction expired");
            }
            waitAWhile(500);
        }

        throw new LibraSDKException(
                String.format("transaction not found within timeout period: %d (seconds)", timeout));
    }

    public List<Event> getEvents(String eventsKey, @Unsigned long start, @Unsigned long limit)
            throws LibraSDKException {
        List<Object> params = new ArrayList<>();
        params.add(eventsKey);
        params.add(start);
        params.add(limit);

        Event[] events = executeCall(Method.get_events, params, Event[].class);
        return Arrays.asList(events);
    }

    private <T> T executeCall(Method method, List<Object> params, Class<T> responseType) throws LibraSDKException {
        Response response = jsonRpcClient.call(method, params);

        libraLedgerState.handleLedgerState(response.getLibraChainId(), response.getLibraLedgerVersion(),
                response.getLibraLedgerTimestampusec());
        if (responseType != null && response.getResult() != null) {
            return new Gson().fromJson(response.getResult(), responseType);
        }

        return null;
    }

}

