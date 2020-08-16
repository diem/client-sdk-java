package org.libra.librasdk;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.libra.librasdk.dto.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LibraClient implements Client {

    private JSONRPCClient jsonrpcClient;

    public LibraClient(LibraNetwork libraNetwork) {
        jsonrpcClient = new JSONRPCClient(libraNetwork.url);
    }

    public LibraClient(int chainId) {
        this(LibraNetwork.getChainIdUrl(chainId));
    }

    public List<Transaction> getTransactions(long fromVersion, int limit, boolean includeEvents) throws LibraSDKException {
        List<Object> params = new ArrayList<>();
        params.add(fromVersion);
        params.add(limit);
        params.add(includeEvents);

        String transactionJson = jsonrpcClient.call(Method.get_transactions, params);
        // get the type of a list of LibraTransaction to ease deserialization
        Type listType = new TypeToken<List<Transaction>>() {
        }.getType();

        List<Transaction> libraTransactions = new Gson().fromJson(transactionJson, listType);

        return libraTransactions;
    }

    public Account getAccount(String address) throws LibraSDKException {
        List<Object> params = new ArrayList<>();
        params.add(address);
        return executeCall(Method.get_account, params, Account.class);
    }

    private <T> T executeCall(Method method, List<Object> params, Class<T> responseType) throws LibraSDKException {
        String response = jsonrpcClient.call(method, params);
        T result = null;

        if (responseType != null) {
            result = new Gson().fromJson(response, responseType);
        }
        return result;
    }

    public Metadata getMetadata() throws LibraSDKException {
        return executeCall(Method.get_metadata, new ArrayList<>(), Metadata.class);
    }

    public Metadata getMetadata(long version) throws LibraSDKException {
        List<Object> params = new ArrayList<>();
        params.add(version);

        return executeCall(Method.get_metadata, params, Metadata.class);
    }

    public Currency[] getCurrencies() throws LibraSDKException {
        return executeCall(Method.get_currencies, new ArrayList<>(), Currency[].class);
    }

    public Transaction getAccountTransaction(String address, long sequence,
                                             boolean includeEvents) throws LibraSDKException {
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

        Type listType = new TypeToken<List<Event>>() {
        }.getType();

        String eventsJson = jsonrpcClient.call(Method.get_events, params);
        List<Event> libraEvents = new Gson().fromJson(eventsJson, listType);

        return libraEvents;
    }
}
