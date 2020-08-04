package org.libra.librasdk;

import org.junit.Before;
import org.junit.Test;
import org.libra.librasdk.dto.Transaction;
import org.libra.librasdk.resources.LibraAccount;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

public class LibraClientTest {

    @InjectMocks
    private LibraClient libraClient = new LibraClient(LibraNetwork.TESTNET); // mocking this class

    @Mock
    private JSONRPCClient jsonrpcClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getTransactions() throws IOException {
        String path = "./src/test/java/org/libra/librasdk/resources/libra_transactions.json";
        String content = Files.readString(Paths.get(path), StandardCharsets.US_ASCII);

        when(jsonrpcClient.call(any(), anyList())).thenReturn(content);
        List<Transaction> transactions = libraClient.getTransactions(1, 3, false);
        assertEquals(2, transactions.size());
    }

    @Test
    public void getAccount() throws IOException {
        String path = "./src/test/java/org/libra/librasdk/resources/libra_account.json";
        String content = Files.readString(Paths.get(path), StandardCharsets.US_ASCII);

        when(jsonrpcClient.call(any(), anyList())).thenReturn(content);
        LibraAccount account = libraClient.getAccount("");
        assertFalse(account.getIsFrozen());
    }
}