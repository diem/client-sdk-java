// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem;

import com.diem.jsonrpc.*;
import com.diem.jsonrpc.JsonRpc;
import com.diem.types.RawTransaction;
import com.diem.types.Script;
import com.diem.types.TransactionPayload;
import com.diem.utils.CurrencyCode;
import com.diem.utils.TransactionUtils;
import com.novi.serde.Bytes;
import com.diem.types.SignedTransaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.diem.stdlib.Helpers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TestNetIntegrationTest {

    public static final int DEFAULT_TIMEOUT = 10 * 1000; // 10 seconds

    private LocalAccount account1;
    private LocalAccount account2;
    private DiemClient client;

    @Before
    public void setup() {
        client = Testnet.createClient();
        account1 = LocalAccount.generate();
        account2 = LocalAccount.generate();
    }

    @Test
    public void testGetMetadata() throws Exception {
        JsonRpc.Metadata response = client.getMetadata();
        assertNotNull(response);
        assertTrue(response.getTimestamp() > new Date().getTime() - 600);
        assertTrue(response.getVersion() > 1000);
    }

    @Test
    public void testGetMetadataByVersion() throws Exception {
        JsonRpc.Metadata response = client.getMetadata(1);
        assertNotNull(response);
        assertEquals(1, response.getVersion());
    }

    @Test
    public void testGetCurrencies() throws Exception {
        List<JsonRpc.CurrencyInfo> response = client.getCurrencies();
        Assert.assertNotNull(response);
        Map<String, JsonRpc.CurrencyInfo> map = new HashMap<>();
        for (JsonRpc.CurrencyInfo curr : response) {
            map.put(curr.getCode(), curr);
        }
        assertTrue(map.containsKey("XDX"));
        assertTrue(map.containsKey("XUS"));
    }

    @Test
    public void testGetAccount() throws Exception {
        JsonRpc.Account response = client.getAccount(Constants.ROOT_ACCOUNT_ADDRESS);
        assertNotNull(response);
        assertEquals("unknown", response.getRole().getType());
        assertFalse(response.getAuthenticationKey().isEmpty());
        assertFalse(response.getReceivedEventsKey().isEmpty());
        assertFalse(response.getDelegatedKeyRotationCapability());
        assertFalse(response.getDelegatedWithdrawalCapability());
        assertFalse(response.getIsFrozen());
        assertFalse(response.getSentEventsKey().isEmpty());
        assertEquals(0, response.getBalancesCount());
    }

    @Test
    public void testGetAccountTransaction() throws DiemException {
        JsonRpc.Transaction response = client.getAccountTransaction(Testnet.DD_ADDRESS, 0, true);
        assertNotNull(response);
        assertTrue(response.getVersion() > 0);
        assertTrue(response.getHash().length() > 0);
        assertTrue(response.getGasUsed() > 0);
        assertEquals("user", response.getTransaction().getType());
        assertFalse(response.getVmStatus().getType().isEmpty());
    }

    @Test
    public void testGetAccountTransactions() throws DiemException {
        List<JsonRpc.Transaction> txns = client.getAccountTransactions(Testnet.DD_ADDRESS, 0, 1, true);
        assertNotNull(txns);
        assertEquals(txns.size(), 1);

        JsonRpc.Transaction txn = txns.get(0);
        assertTrue(txn.getVersion() > 0);
        assertTrue(txn.getHash().length() > 0);
        assertTrue(txn.getGasUsed() > 0);
        assertEquals("user", txn.getTransaction().getType());
        assertFalse(txn.getVmStatus().getType().isEmpty());
    }

    @Test
    public void testGetTransactions() throws DiemException {
        List<JsonRpc.Transaction> transactions = client.getTransactions(0L, 1000, true);
        assertNotNull(transactions);
        assertTrue(transactions.size() > 0);
    }

    @Test
    public void testGetEvents() throws DiemException {
        List<JsonRpc.CurrencyInfo> currencies = client.getCurrencies();

        List<JsonRpc.Event> events = client.getEvents(currencies.get(0).getMintEventsKey(), 0L, 1000L);
        assertNotNull(events);
        assertTrue(events.size() > 0);
    }


    @Test
    public void testSubmitPeerToPeerTransaction() throws Exception {
        Testnet.mintCoins(client, coins(10000), account1.authKey.hex(), Testnet.XUS);
        Testnet.mintCoins(client, coins(10000), account2.authKey.hex(), Testnet.XUS);

        long seqNum = client.getAccount(account1.address).getSequenceNumber();
        Script script = Helpers.encode_peer_to_peer_with_metadata_script(
                CurrencyCode.typeTag(Testnet.XUS), account2.address, coins(2), new Bytes(new byte[0]), new Bytes(new byte[0]));
        RawTransaction rawTxn = new RawTransaction(
                account1.address, seqNum,
                new TransactionPayload.Script(script),
                coins(1), 0L, Testnet.XUS,
                100000000000L, Testnet.CHAIN_ID);
        SignedTransaction signedTxn = Signer.sign(account1.privateKey, rawTxn);
        try {
            client.submit(signedTxn);
        } catch (StaleResponseException e) {
            // ignore
        }
        JsonRpc.Transaction transaction = client.waitForTransaction(signedTxn, DEFAULT_TIMEOUT);

        assertNotNull(transaction);
        assertTrue(TransactionUtils.isExecuted(transaction));
        assertEquals(2, transaction.getEventsList().size());
        assertEquals("sentpayment", transaction.getEvents(0).getData().getType());
        assertEquals("receivedpayment", transaction.getEvents(1).getData().getType());
    }

    @Test
    public void testSubmitExpiredTransaction() throws Exception {
        Testnet.mintCoins(client, coins(10000), account1.authKey.hex(), Testnet.XUS);
        TransactionPayload script = new TransactionPayload.Script(Helpers.encode_peer_to_peer_with_metadata_script(
                CurrencyCode.typeTag(Testnet.XUS), account2.address, coins(1), new Bytes(new byte[0]), new Bytes(new byte[0])));
        JsonRpc.Account account1Data = client.getAccount(account1.address);
        SignedTransaction st = Signer.sign(account1.privateKey,
                new RawTransaction(account1.address, account1Data.getSequenceNumber(), script, coins(1), 0L,
                        Testnet.XUS, 0L, Testnet.CHAIN_ID));
        assertThrows("Server error: VM Validation error: TRANSACTION_EXPIRED", JsonRpcError.class,
                () -> client.submit(st));
    }

    @Test
    public void testSubmitTransactionAndExecuteFailed() throws Exception {
        Testnet.mintCoins(client, coins(10000), account1.authKey.hex(), Testnet.XUS);
        TransactionPayload script = new TransactionPayload.Script(Helpers.encode_peer_to_peer_with_metadata_script(
                CurrencyCode.typeTag(Testnet.XUS), account2.address, coins(1), new Bytes(new byte[0]), new Bytes(new byte[0])));
        JsonRpc.Account account1Data = client.getAccount(account1.address);
        SignedTransaction st = Signer.sign(account1.privateKey,
                new RawTransaction(account1.address, account1Data.getSequenceNumber(), script,
                        coins(1), 0L, Testnet.XUS, 100000000000L, Testnet.CHAIN_ID));
        client.submit(st);

        assertThrows("transaction execution failed", DiemTransactionExecutionFailedException.class,
                () -> client.waitForTransaction(st, DEFAULT_TIMEOUT));
    }

    @Test
    public void testWaitForTransaction_invalidSignedTransactionHexString() {
        assertThrows("Deserialize given hex string as SignedTransaction LCS failed", IllegalArgumentException.class,
                () -> client.waitForTransaction(
                        "28f8151939d68b692d296028ca54bb7e9e92a2f9543effd2a424a79df67d007f", 5));
    }

    @Test
    public void testWaitForTransaction_hashMismatch() {
        assertThrows("found transaction, but hash does not match", DiemTransactionHashMismatchException.class,
                () -> {
                    client.waitForTransaction(Testnet.DD_ADDRESS, 1,
                            "mismatched hash",
                            nowInSeconds(30), 5);
                });

    }

    @Test
    public void testWaitForTransaction_timeout() throws DiemException {
        long expirationTimeSec = nowInSeconds(5);
        JsonRpc.Account account = client.getAccount(Constants.ROOT_ACCOUNT_ADDRESS);
        assertThrows("transaction not found within timeout period", DiemTransactionWaitTimeoutException.class,
                () -> client.waitForTransaction(Constants.ROOT_ACCOUNT_ADDRESS, account.getSequenceNumber() + 1000,
                        "28f8151939d68b692d296028ca54bb7e9e92a2f9543effd2a424a79df67d0071",
                        expirationTimeSec, 0));
    }

    @Test
    public void testWaitForTransaction_transactionExpired() throws DiemException {
        long expirationTimeSec = nowInSeconds(0);
        JsonRpc.Account account = client.getAccount(Constants.ROOT_ACCOUNT_ADDRESS);
        assertThrows("transaction not found within timeout period", DiemTransactionExpiredException.class,
                () -> client.waitForTransaction(Constants.ROOT_ACCOUNT_ADDRESS, account.getSequenceNumber() + 1000,
                        "28f8151939d68b692d296028ca54bb7e9e92a2f9543effd2a424a79df67d0071",
                        expirationTimeSec, 5));
    }

    @Test
    public void testSubmitShouldIgnoreStaleResponseError() throws Exception {
        Testnet.mintCoins(client, coins(10000), account1.authKey.hex(), Testnet.XUS);
        Testnet.mintCoins(client, coins(10000), account2.authKey.hex(), Testnet.XUS);

        long seqNum = client.getAccount(account1.address).getSequenceNumber();
        Script script = Helpers.encode_peer_to_peer_with_metadata_script(
                CurrencyCode.typeTag(Testnet.XUS), account2.address, coins(2), new Bytes(new byte[0]), new Bytes(new byte[0]));
        RawTransaction rawTxn = new RawTransaction(
                account1.address, seqNum,
                new TransactionPayload.Script(script),
                coins(1), 0L, Testnet.XUS,
                100000000000L, Testnet.CHAIN_ID);
        DiemJsonRpcClient dclient = (DiemJsonRpcClient) client;
        LedgerState state = dclient.getState();
        dclient.setState(new LedgerState(state.getChainId(), state.getVersion(), state.getTimestampUsecs()));
        SignedTransaction signedTxn = Signer.sign(account1.privateKey, rawTxn);

        client.submit(signedTxn);

        dclient.setState(state);
        JsonRpc.Transaction transaction = client.waitForTransaction(signedTxn, DEFAULT_TIMEOUT);

        assertNotNull(transaction);
        assertTrue(TransactionUtils.isExecuted(transaction));
        assertEquals(2, transaction.getEventsList().size());
        assertEquals("sentpayment", transaction.getEvents(0).getData().getType());
        assertEquals("receivedpayment", transaction.getEvents(1).getData().getType());
    }

    private long nowInSeconds(int plus) {
        return System.currentTimeMillis() / 1000 + plus;
    }

    private static long coins(long n) {
        return n * 1000000;
    }

}
