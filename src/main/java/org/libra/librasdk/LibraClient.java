package org.libra.librasdk;

import com.google.gson.Gson;
import org.libra.librasdk.dto.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LibraClient implements Client {

    private JSONRPCHandler jsonrpcHandler;
    private LibraLedgerState libraLedgerState;

    public LibraClient(String url, int chainId) {
        this.jsonrpcHandler = new JSONRPCHandler(url);
        this.libraLedgerState = new LibraLedgerState(chainId);
    }

    public List<Transaction> getTransactions(long fromVersion, int limit, boolean includeEvents) throws LibraSDKException {
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

    public Metadata getMetadata(long version) throws LibraSDKException {
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

    public Transaction getAccountTransaction(String address, long sequence,
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

    public Transaction waitForTransaction(String address, long sequence, boolean includeEvents,
                                          long timeoutMillis) throws InterruptedException,
            LibraSDKException {
        for (long millis = 0, step = 100; millis < timeoutMillis; millis += step) {
            Transaction transaction = this.getAccountTransaction(address, sequence, includeEvents);
            if (transaction != null) {
                return transaction;
            }
            Thread.sleep(step);
        }

        return null;
    }

    public List<Event> getEvents(String eventsKey, long start, long limit) throws LibraSDKException {
        List<Object> params = new ArrayList<>();
        params.add(eventsKey);
        params.add(start);
        params.add(limit);

        Event[] events = executeCall(Method.get_events, params, Event[].class);
        return Arrays.asList(events);
    }

    private <T> T executeCall(Method method, List<Object> params, Class<T> responseType) throws LibraSDKException {
        String response = jsonrpcHandler.call(method, params);
        LibraResponse libraResponse = new Gson().fromJson(response, LibraResponse.class);

        if (libraResponse.getError() != null) {
//            JsonRpcErrorException
            throw new LibraSDKException(libraResponse.getError().toString());
        }

        libraLedgerState.handleLedgerState(libraResponse.getLibra_chain_id(),
                libraResponse.getLibra_ledger_version(),
                libraResponse.getLibra_ledger_timestampusec(), null);
        T result = null;

        if (responseType != null) {
            result = new Gson().fromJson(libraResponse.getResult(), responseType);
        }

        return result;
    }
}
