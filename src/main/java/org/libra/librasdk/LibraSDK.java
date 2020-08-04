package org.libra.librasdk;

import org.libra.librasdk.dto.Currency;
import org.libra.librasdk.dto.Event;
import org.libra.librasdk.dto.Metadata;
import org.libra.librasdk.dto.Transaction;
import org.libra.librasdk.resources.LibraAccount;
import org.libra.librasdk.resources.LibraTransaction;

import java.util.ArrayList;
import java.util.List;

public class LibraSDK {

    private LibraClient client;

    public LibraSDK(LibraNetwork network) {
        this.client = new LibraClient(network);
    }

    public List<LibraTransaction> getTransactions(int fromVersion, int limit, boolean includeEvents) throws Exception {
        List<Transaction> transactions = client.getTransactions(fromVersion, limit, includeEvents);
        List<LibraTransaction> libraTransactions = convertList(transactions);
        return libraTransactions;
    }

    public List<LibraTransaction> convertList(List<Transaction> transactions) throws Exception {
        List<LibraTransaction> libraTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            LibraTransaction libraTransaction = LibraTransactionFactory.create(transaction);
            libraTransactions.add(libraTransaction);
        }

        return libraTransactions;
    }

    public LibraTransaction getTransaction(int version, boolean includeEvents) throws Exception {
        List<Transaction> transactions = client.getTransactions(version, 1, includeEvents);
        List<LibraTransaction> libraTransactions = convertList(transactions);

        return libraTransactions.get(0);
    }

    public LibraAccount getAccount(String address) {
        LibraAccount account = client.getAccount(address);

        return account;
    }

    public Metadata getMetadata(long version) {
        Metadata metadata = client.getMetadata(version);

        return metadata;
    }

    public Metadata getMetadata() {
        Metadata metadata = client.getMetadata();

        return metadata;
    }

    public Currency[] getCurrencies() {
        Currency[] currencies = client.getCurrencies();

        return currencies;
    }

    public void submit(String data) {
        client.submit(data);
    }

    public LibraTransaction waitForTransaction(String address, long sequence, boolean includeEvents, long timeoutMillis) throws Exception {
        Transaction transaction = client.waitForTransaction(address, sequence, includeEvents, timeoutMillis);
        LibraTransaction libraTransaction = LibraTransactionFactory.create(transaction);

        return libraTransaction;
    }

    public LibraTransaction getAccountTransaction(String address, long sequence, boolean includeEvents) throws Exception {
        Transaction transaction = client.getAccountTransaction(address, sequence, includeEvents);
        LibraTransaction libraTransaction = LibraTransactionFactory.create(transaction);

        return libraTransaction;
    }

    public List<Event> getEvents(String eventsKey, long start, long limit) {
        List<Event> events = client.getEvents(eventsKey, start, limit);

        return events;
    }

}
