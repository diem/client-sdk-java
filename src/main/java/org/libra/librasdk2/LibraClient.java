package org.libra.librasdk2;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.libra.librasdk.dto.Transaction;
import org.libra.librasdk2.resources.LibraAccount;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LibraClient {

    public static final String GET_TRANSACTIONS = "get_transactions";
    public static final String GET_ACCOUNT = "get_account";
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

        String transactionJson = jsonrpcClient.call(GET_TRANSACTIONS, params);
        // get the type of a list of LibraTransaction to ease deserialization
        Type listType = new TypeToken<List<Transaction>>() {}.getType();

        List<Transaction> libraTransactions = new Gson().fromJson(transactionJson, listType);

        return libraTransactions;
    }

    public LibraAccount getAccount(String address) {
        List<Object> params = new ArrayList<>();
        params.add(address);
        String accountJson = jsonrpcClient.call(GET_ACCOUNT, params);
        LibraAccount libraAccount = new Gson().fromJson(accountJson, LibraAccount.class);

        return libraAccount;
    }

}
