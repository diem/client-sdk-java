package org.libra.librasdk2;

import org.libra.librasdk.dto.Transaction;
import org.libra.librasdk2.resources.LibraAccount;
import org.libra.librasdk2.resources.LibraTransaction;

import java.util.ArrayList;
import java.util.List;

public class LibraSDK {

    private LibraClient client;

    public LibraSDK(LibraNetwork network) {
        this.client = new LibraClient(network);
    }

    public List<LibraTransaction> getTransactions(int fromVersion, int limit, boolean includeEvents){
        List<Transaction> transactions = client.getTransactions(fromVersion, limit, includeEvents);
        List<LibraTransaction> libraTransactions = convert(transactions);
        return libraTransactions;
    }

    private List<LibraTransaction> convert(List<Transaction> transactions) {
        List<LibraTransaction> libraTransactions = new ArrayList<>();
        transactions.forEach(transaction -> {
            LibraTransaction libraTransaction = LibraTransactionFactory.create(transaction);
            libraTransactions.add(libraTransaction);
        });

        return libraTransactions;
    }

    public LibraTransaction getTransaction(int fromVersion, boolean includeEvents){
        List<Transaction> transactions = client.getTransactions(fromVersion, 1, includeEvents);
        List<LibraTransaction> libraTransactions = convert(transactions);

        return libraTransactions.get(0);
    }

    public LibraAccount getAccount(String address){
        LibraAccount account = client.getAccount(address);

        return account;
    }

}
