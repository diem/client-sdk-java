// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra;

import com.novi.serde.Bytes;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.libra.jsonrpc.JsonRpcError;
import org.libra.jsonrpc.StaleResponseException;
import org.libra.jsonrpctypes.JsonRpc;
import org.libra.stdlib.Helpers;
import org.libra.types.*;
import org.libra.utils.CurrencyCode;
import org.libra.utils.Hex;
import org.libra.utils.TransactionUtils;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.libra.Constants.ROOT_ACCOUNT_ADDRESS;

public class TestNetIntegrationTest {

    public static final int DEFAULT_TIMEOUT = 10 * 1000; // 10 seconds

    private static LocalAccount account1;
    private static LocalAccount account2;
    private static LibraClient libraClient;

    @BeforeClass
    public static void globalSetup() {
        // setup account globally for improving test performance,
        // we also need setup client globally for making sure each
        // test won't get stale response, which may cause we get stale
        // account sequence number.
        libraClient = Testnet.createClient();
        account1 = LocalAccount.generate(
                "76e3de861d516283dc285e12ddadc95245a9e98f351c910b0ad722f790bac273");
        account2 = LocalAccount.generate(
                "b13968ad5722ee203968f7deea565b2f4266f923b3292065b6e190c368f91036");

        Testnet.mintCoins(libraClient, coins(10000), account1.authKey.hex(), CurrencyCode.LBR);
        Testnet.mintCoins(libraClient, coins(10000), account2.authKey.hex(), CurrencyCode.LBR);
    }

    @Test
    public void testGetMetadata() throws Exception {
        JsonRpc.BlockMetadata response = libraClient.getMetadata();
        Assert.assertNotNull(response);
        Assert.assertTrue(response.getTimestamp() > new Date().getTime() - 600);
        Assert.assertTrue(response.getVersion() > 1000);
    }

    @Test
    public void testGetMetadataByVersion() throws Exception {
        JsonRpc.BlockMetadata response = libraClient.getMetadata(1);
        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getVersion());
    }

    @Test
    public void testGetCurrencies() throws Exception {
        List<JsonRpc.CurrencyInfo> response = libraClient.getCurrencies();
        Assert.assertNotNull(response);
        Assert.assertEquals(3, response.size());
        Assert.assertEquals("Coin1", response.get(0).getCode());
        Assert.assertEquals("Coin2", response.get(1).getCode());
        Assert.assertEquals("LBR", response.get(2).getCode());
    }

    @Test
    public void testGetAccount() throws Exception {
        JsonRpc.Account response = libraClient.getAccount(ROOT_ACCOUNT_ADDRESS);
        Assert.assertNotNull(response);
        Assert.assertEquals("unknown", response.getRole().getType());
        Assert.assertFalse(response.getAuthenticationKey().isEmpty());
        Assert.assertFalse(response.getReceivedEventsKey().isEmpty());
        Assert.assertFalse(response.getDelegatedKeyRotationCapability());
        Assert.assertFalse(response.getDelegatedWithdrawalCapability());
        Assert.assertFalse(response.getIsFrozen());
        Assert.assertFalse(response.getSentEventsKey().isEmpty());
        Assert.assertEquals(0, response.getBalancesCount());
    }

    @Test
    public void testGetAccountTransaction() throws LibraException {
        long seq = libraClient.getAccount(account1.address).getSequenceNumber();
        submitTransaction(account1, createP2PScript(account2.address, CurrencyCode.LBR, coins(1)));
        JsonRpc.Transaction response = libraClient.getAccountTransaction(account1.address, seq, true);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.getVersion() > 0);
        Assert.assertTrue(response.getHash().length() > 0);
        Assert.assertTrue(response.getGasUsed() > 0);
        Assert.assertEquals("user", response.getTransaction().getType());
        Assert.assertTrue(TransactionUtils.isExecuted(response));
    }

    @Test
    public void testGetAccountTransactions() throws LibraException {
        long seq = libraClient.getAccount(account1.address).getSequenceNumber();
        submitTransaction(account1, createP2PScript(account2.address, CurrencyCode.LBR, coins(1)));
        List<JsonRpc.Transaction> txns = libraClient.getAccountTransactions(account1.address, seq, 1, true);
        Assert.assertNotNull(txns);
        Assert.assertEquals(txns.size(), 1);

        JsonRpc.Transaction txn = txns.get(0);
        Assert.assertTrue(txn.getVersion() > 0);
        Assert.assertTrue(txn.getHash().length() > 0);
        Assert.assertTrue(txn.getGasUsed() > 0);
        Assert.assertEquals("user", txn.getTransaction().getType());
        Assert.assertTrue(TransactionUtils.isExecuted(txn));
    }

    @Test
    public void testSubmitTransaction() throws Exception {
        TransactionAndSigned transactionAndSigned = submitTransaction(account1, createP2PScript(account2.address, CurrencyCode.LBR, coins(2)));
        SignedTransaction st = transactionAndSigned.signedTransaction;
        JsonRpc.Transaction p2p = transactionAndSigned.transaction;

        assertNotNull(p2p);
        assertTrue(TransactionUtils.isExecuted(p2p));
        assertEquals(Hex.encode(((TransactionAuthenticator.Ed25519) st.authenticator).signature.value),
                p2p.getTransaction().getSignature().toUpperCase());
        assertEquals(2, p2p.getEventsList().size());
        assertEquals("sentpayment", p2p.getEvents(0).getData().getType());
        assertEquals("receivedpayment", p2p.getEvents(1).getData().getType());
    }

    @Test
    public void testSubmitExpiredTransaction() throws Exception {
        TransactionPayload script = createP2PScript(account2.address, CurrencyCode.LBR, coins(1));
        JsonRpc.Account account1Data = libraClient.getAccount(account1.address);
        SignedTransaction st = Signer.sign(account1.privateKey,
                new RawTransaction(account1.address, account1Data.getSequenceNumber(), script, coins(1), 0L,
                        CurrencyCode.LBR, 0L, Testnet.CHAIN_ID));
        assertThrows("Server error: VM Validation error: TRANSACTION_EXPIRED", JsonRpcError.class,
                () -> libraClient.submit(st));
    }

    @Test
    public void testSubmitTransactionAndExecuteFailed() throws Exception {
        LocalAccount account3 = LocalAccount.generate();
        TransactionPayload script = createP2PScript(account3.address, CurrencyCode.LBR, coins(1));
        JsonRpc.Account account1Data = libraClient.getAccount(account1.address);
        SignedTransaction st = Signer.sign(account1.privateKey,
                new RawTransaction(account1.address, account1Data.getSequenceNumber(), script,
                        coins(1), 0L, CurrencyCode.LBR, 100000000000L, Testnet.CHAIN_ID));
        libraClient.submit(st);

        assertThrows("transaction execution failed", LibraException.class,
                () -> libraClient.waitForTransaction(st, DEFAULT_TIMEOUT));
    }

    @Test
    public void testGetTransactions() throws LibraException {
        List<JsonRpc.Transaction> transactions = libraClient.getTransactions(0L, 1000, true);
        Assert.assertNotNull(transactions);
        Assert.assertTrue(transactions.size() > 0);
    }

    @Test
    public void testGetEvents() throws LibraException {
        List<JsonRpc.CurrencyInfo> currencies = libraClient.getCurrencies();

        List<JsonRpc.Event> events = libraClient.getEvents(currencies.get(0).getMintEventsKey(), 0L, 1000L);
        Assert.assertNotNull(events);
        Assert.assertTrue(events.size() > 0);
    }

    @Test
    public void testWaitForTransaction_invalidSignedTransactionHexString() {
        assertThrows("Deserialize given hex string as SignedTransaction LCS failed", IllegalArgumentException.class,
                () -> libraClient.waitForTransaction(
                        "28f8151939d68b692d296028ca54bb7e9e92a2f9543effd2a424a79df67d007f", 5));
    }

    @Test
    public void testWaitForTransaction_hashMismatch() throws LibraException {
        long expirationTimeSec = System.currentTimeMillis() / 1000 + 5;
        submitTransaction(account1, createP2PScript(account2.address, CurrencyCode.LBR, coins(3)));
        assertThrows("found transaction, but hash does not match", LibraException.class,
                () -> {
                    libraClient.waitForTransaction(account1.address, 1,
                            "28f8151939d68b692d296028ca54bb7e9e92a2f9543effd2a424a79df67d0071",
                            expirationTimeSec, 5);
                });

    }

    @Test
    public void testWaitForTransaction_timeout() {
        long expirationTimeSec = System.currentTimeMillis() / 1000 + 5;
        assertThrows("transaction not found within timeout period", LibraException.class,
                () -> libraClient.waitForTransaction(ROOT_ACCOUNT_ADDRESS, 1,
                        "28f8151939d68b692d296028ca54bb7e9e92a2f9543effd2a424a79df67d0071",
                        expirationTimeSec, 0));
    }

    @Test
    public void testWaitForTransaction_transactionExpired() {
        long expirationTimeSec = System.currentTimeMillis() / 1000;
        assertThrows("transaction not found within timeout period", LibraException.class,
                () -> libraClient.waitForTransaction(ROOT_ACCOUNT_ADDRESS, 1,
                        "28f8151939d68b692d296028ca54bb7e9e92a2f9543effd2a424a79df67d0071",
                        expirationTimeSec, 5));
    }

    private static long coins(long n) {
        return n * 1000000;
    }

    private TransactionAndSigned submitTransaction(LocalAccount sender, TransactionPayload payload) throws LibraException {
        long seqNum = libraClient.getAccount(sender.address).getSequenceNumber();
        SignedTransaction st = Signer.sign(sender.privateKey,
                new RawTransaction(sender.address, seqNum, payload, coins(1), 0L, CurrencyCode.LBR,
                        100000000000L, Testnet.CHAIN_ID));
        try {
            libraClient.submit(st);
        } catch (StaleResponseException e) {
            // ignore
        }
        JsonRpc.Transaction transaction = libraClient.waitForTransaction(st, DEFAULT_TIMEOUT);
        return new TransactionAndSigned(transaction, st);
    }

    private static class TransactionAndSigned {
        JsonRpc.Transaction transaction;
        SignedTransaction signedTransaction;

        public TransactionAndSigned(JsonRpc.Transaction transaction, SignedTransaction signedTransaction) {
            this.transaction = transaction;
            this.signedTransaction = signedTransaction;
        }

    }

    private TransactionPayload createP2PScript(AccountAddress address, String currencyCode, long amount) {
        return new TransactionPayload.Script(Helpers.encode_peer_to_peer_with_metadata_script(
                CurrencyCode.typeTag(currencyCode), address, amount, new Bytes(new byte[0]), new Bytes(new byte[0])));
    }
}
