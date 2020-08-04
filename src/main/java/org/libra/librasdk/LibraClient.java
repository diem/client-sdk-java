package org.libra.librasdk;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.libra.librasdk.dto.Currency;
import org.libra.librasdk.dto.Event;
import org.libra.librasdk.dto.Metadata;
import org.libra.librasdk.dto.Transaction;
import org.libra.librasdk.resources.LibraAccount;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LibraClient {

    private JSONRPCClient jsonrpcClient;

    public LibraClient(LibraNetwork libraNetwork) {
        init(libraNetwork);
    }

    private void init(LibraNetwork libraNetwork) {
        jsonrpcClient = new JSONRPCClient(libraNetwork.url);
    }

    public List<Transaction> getTransactions(int fromVersion, int limit, boolean includeEvents) {
        List<Object> params = new ArrayList<>();
        params.add(fromVersion);
        params.add(limit);
        params.add(includeEvents);

        String transactionJson = jsonrpcClient.call(Method.get_transactions, params);
        // get the type of a list of LibraTransaction to ease deserialization
        Type listType = new TypeToken<List<Transaction>>() {}.getType();

        List<Transaction> libraTransactions = new Gson().fromJson(transactionJson, listType);

        return libraTransactions;
    }

    public LibraAccount getAccount(String address) {
        List<Object> params = new ArrayList<>();
        params.add(address);
        return getCall(params, Method.get_account, LibraAccount.class);
    }

    private <T> T getCall(List<Object> params, Method method, Class<T> responseType) {
        String accountJson = jsonrpcClient.call(method, params);
        T result = new Gson().fromJson(accountJson, responseType);

        return result;
    }

    public Metadata getMetadata(){
        return getCall(null, Method.get_metadata, Metadata.class);
    }

    public Metadata getMetadata(long version) {
        List<Object> params = new ArrayList<>();
        params.add(version);

        return getCall(params, Method.get_metadata, Metadata.class);
    }

    public Currency[] getCurrencies() {
        return getCall(null, Method.get_currencies, Currency[].class);
    }

    public Transaction getAccountTransaction(String address, long sequence, boolean includeEvents) {
        List<Object> params = new ArrayList<>();
        params.add(address);
        params.add(sequence);
        params.add(includeEvents);

        return getCall(params, Method.get_account_transaction, Transaction.class);
    }

    public void submit(String data) {
        List<Object> params = new ArrayList<>();
        params.add(data);

        getCall(params, null, Transaction.class);
    }

    public Transaction waitForTransaction(String address, long sequence, boolean includeEvents, long timeoutMillis) throws InterruptedException {
        for (long millis = 0, step = 100; millis < timeoutMillis; millis += step) {
            Transaction transaction = this.getAccountTransaction(address, sequence, includeEvents);
            if (transaction != null) {
                return transaction;
            }
            Thread.sleep(step);
        }

        return null;
    }

    public List<Event> getEvents(String eventsKey, long start, long limit) {
        List<Object> params = new ArrayList<>();
        params.add(eventsKey);
        params.add(start);
        params.add(limit);

        Type listType = new TypeToken<List<Event>>() {}.getType();

        String eventsJson = jsonrpcClient.call(Method.get_events, params);
        List<Event> libraEvents = new Gson().fromJson(eventsJson, listType);
        return libraEvents;
    }

}
