// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

import com.novi.serde.Bytes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.libra.Testnet;
import org.libra.librasdk.dto.Metadata;
import org.libra.librasdk.dto.Transaction;
import org.libra.librasdk.dto.*;
import org.libra.librasdk.jsonrpc.JSONRPCErrorException;
import org.libra.stdlib.Helpers;
import org.libra.types.*;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.libra.librasdk.Constants.ROOT_ACCOUNT_ADDRESS;


public class TestNetIntegrationTest {

    public static final int DEFAULT_TIMEOUT = 10 * 1000; // 10 seconds
    private Client libraClient;
    private LocalAccount account1;
    private LocalAccount account2;
    private LocalAccount account3;

    @Before
    public void setup() {
        libraClient = Testnet.createClient();
        account1 = Utils.generateLocalAccountFromPrivateKey(
                "76e3de861d516283dc285e12ddadc95245a9e98f351c910b0ad722f790bac273");
        account2 = Utils.generateLocalAccountFromPrivateKey(
                "b13968ad5722ee203968f7deea565b2f4266f923b3292065b6e190c368f91036");
        account3 = Utils.generateLocalAccountFromPrivateKey(
                "aceb051a2c02ebe6493000613bd467ea97a6051988637440c918584043a769dd");
    }

    @Test
    public void testGetMetadata() throws Exception {
        Metadata response = libraClient.getMetadata();
        Assert.assertNotNull(response);
        Assert.assertTrue(response.timestamp > new Date().getTime() - 600);
        Assert.assertTrue(response.version > 1000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLibraClient_invalidUrl() {
        libraClient = new LibraClient("invalidUrl", 2);
    }

    @Test
    public void testGetMetadataByVersion() throws Exception {
        Metadata response = libraClient.getMetadata(1);
        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.version);
    }

    @Test
    public void testGetCurrencies() throws Exception {
        Currency[] response = libraClient.getCurrencies();
        Assert.assertNotNull(response);
        Assert.assertEquals(3, response.length);
        Assert.assertEquals("Coin1", response[0].code);
        Assert.assertEquals("Coin2", response[1].code);
        Assert.assertEquals("LBR", response[2].code);
    }

    @Test
    public void testGetAccount() throws Exception {
        Account response = libraClient.getAccount(ROOT_ACCOUNT_ADDRESS);
        Assert.assertNotNull(response);
        Assert.assertEquals("unknown", response.role.getAsJsonObject().get("type").getAsString());
        Assert.assertFalse(response.authentication_key.isEmpty());
        Assert.assertFalse(response.received_events_key.isEmpty());
        Assert.assertFalse(response.delegated_key_rotation_capability);
        Assert.assertFalse(response.delegated_withdrawal_capability);
        Assert.assertFalse(response.is_frozen);
        Assert.assertFalse(response.sent_events_key.isEmpty());
        Assert.assertEquals(0, response.balances.length);
    }

    @Test
    public void testGetAccountTransaction() throws LibraSDKException, InterruptedException {
        submitTransaction(1);
        Transaction response = libraClient.getAccountTransaction(account1.libra_account_address, 1, true);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.version > 0);
        Assert.assertTrue(response.hash.length() > 0);
        Assert.assertTrue(response.gas_used > 0);
        Assert.assertEquals("user", response.transaction.type);
        Assert.assertTrue(response.isExecuted());
    }

    @Test
    public void testSubmitTransaction() throws Exception {
        TransactionAndSigned transactionAndSigned = submitTransaction(2);
        SignedTransaction st = transactionAndSigned.signedTransaction;
        Transaction p2p = transactionAndSigned.transaction;
        assertNotNull(p2p);
        assertTrue(p2p.vm_status.toString(), p2p.isExecuted());
        assertEquals(Utils.bytesToHex(((TransactionAuthenticator.Ed25519) st.authenticator).signature.value),
                p2p.transaction.signature.toUpperCase());
        assertEquals(2, p2p.events.length);
        assertEquals("sentpayment", p2p.events[0].data.type);
        assertEquals("receivedpayment", p2p.events[1].data.type);
    }

    @Test
    public void testTransferTransaction() throws Exception {
        String currencyCode = "LBR";
        Testnet.mintCoins(libraClient, coins(100), account1.libra_auth_key, currencyCode);
        Testnet.mintCoins(libraClient, coins(100), account2.libra_auth_key, currencyCode);
        SignedTransaction signedTransaction = libraClient
                .transfer(account1.libra_account_address, account1.libra_auth_key, account1.private_key,
                        account1.public_key, account2.libra_account_address, 1000000L, 1000000L, 0L, currencyCode,
                        100000000000L, Constants.TEST_NET_CHAIN_ID, new byte[]{}, new byte[]{});
        Transaction p2p = libraClient.waitForTransaction(signedTransaction, DEFAULT_TIMEOUT);
        assertNotNull(p2p);
        assertTrue(p2p.vm_status.toString(), p2p.isExecuted());
        assertEquals(
                Utils.bytesToHex(((TransactionAuthenticator.Ed25519) signedTransaction.authenticator).signature.value),
                p2p.transaction.signature.toUpperCase());
        assertEquals(2, p2p.events.length);
        assertEquals("sentpayment", p2p.events[0].data.type);
        assertEquals("receivedpayment", p2p.events[1].data.type);
    }

    @Test
    public void testSubmitExpiredTransaction() throws Exception {
        String currencyCode = "LBR";
        Testnet.mintCoins(libraClient, coins(100), account1.libra_auth_key, currencyCode);
        Testnet.mintCoins(libraClient, coins(100), account2.libra_auth_key, currencyCode);

        Script script = createP2PScript(account2.getAccountAddress(), currencyCode, coins(1));
        Account account1Data = libraClient.getAccount(account1.libra_account_address);
        SignedTransaction st =
                Utils.signTransaction(account1, account1Data.sequence_number, script, coins(1), 0, currencyCode, 0L,
                        Constants.TEST_NET_CHAIN_ID);
        assertThrows("Server error: VM Validation error: TRANSACTION_EXPIRED", JSONRPCErrorException.class,
                () -> libraClient.submit(Utils.toLCSHex(st)));
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testSubmitTransactionAndExecuteFailed() throws Exception {
        String currencyCode = "LBR";
        Testnet.mintCoins(libraClient, coins(100), account1.libra_auth_key, currencyCode);

        Script script = createP2PScript(account3.getAccountAddress(), currencyCode, coins(1));
        Account account1Data = libraClient.getAccount(account1.libra_account_address);
        SignedTransaction st =
                Utils.signTransaction(account1, account1Data.sequence_number, script, coins(1), 0, currencyCode,
                        100000000000L, Constants.TEST_NET_CHAIN_ID);
        libraClient.submit(Utils.toLCSHex(st));

        exceptionRule.expect(LibraSDKException.class);
        exceptionRule.expectMessage("transaction execution failed");

        libraClient.waitForTransaction(st, DEFAULT_TIMEOUT);
    }

    @Test
    public void testGetTransactions() throws LibraSDKException {
        List<Transaction> transactions = libraClient.getTransactions(0L, 1000, true);
        Assert.assertNotNull(transactions);
        Assert.assertTrue(transactions.size() > 0);
    }

    @Test
    public void testGetEvents() throws LibraSDKException {
        Currency[] currencies = libraClient.getCurrencies();

        List<Event> events = libraClient.getEvents(currencies[0].mint_events_key, 0L, 1000L);
        Assert.assertNotNull(events);
        Assert.assertTrue(events.size() > 0);
    }

    @Test
    public void testWaitForTransactionFromHash_invalidHexString() throws LibraSDKException {
        exceptionRule.expect(LibraSDKException.class);
        exceptionRule.expectMessage("Deserialize given hex string as SignedTransaction LCS failed");
        Transaction transaction =
                libraClient.waitForTransaction("28f8151939d68b692d296028ca54bb7e9e92a2f9543effd2a424a79df67d007f", 5);
        assertNull(transaction);
    }

    @Test
    public void testWaitForTransactionFromAddress_hashMismatch() throws LibraSDKException {
        exceptionRule.expect(LibraSDKException.class);
        exceptionRule.expectMessage("found transaction, but hash does not match");
        libraClient.waitForTransaction(ROOT_ACCOUNT_ADDRESS, 1,
                "28f8151939d68b692d296028ca54bb7e9e92a2f9543effd2a424a79df67d0071", System.currentTimeMillis(), 5);
    }

    @Test
    public void testWaitForTransactionFromAddress_timeout() throws LibraSDKException {
        exceptionRule.expect(LibraSDKException.class);
        exceptionRule.expectMessage("transaction not found within timeout period");
        libraClient.waitForTransaction(ROOT_ACCOUNT_ADDRESS, 1,
                "28f8151939d68b692d296028ca54bb7e9e92a2f9543effd2a424a79df67d0071", System.currentTimeMillis(), 0);
    }

    private Script createP2PScript(AccountAddress address, String currencyCode, long amount) {
        TypeTag token = Utils.createCurrencyCodeTypeTag(currencyCode);
        return Helpers.encode_peer_to_peer_with_metadata_script(token, address, amount, new Bytes(new byte[]{}),
                new Bytes(new byte[]{}));
    }

    private static long coins(long n) {
        return n * 1000000;
    }

    private synchronized TransactionAndSigned submitTransaction(int transactionAmount) throws LibraSDKException, InterruptedException {
        String currencyCode = "LBR";
        Testnet.mintCoins(libraClient, coins(100), account1.libra_auth_key, currencyCode);
        Testnet.mintCoins(libraClient, coins(100), account2.libra_auth_key, currencyCode);

        Script script = createP2PScript(account2.getAccountAddress(), currencyCode, coins(transactionAmount));
        Account account1Data = libraClient.getAccount(account1.libra_account_address);
        SignedTransaction st =
                Utils.signTransaction(account1, account1Data.sequence_number, script, coins(1), 0, currencyCode,
                        100000000000L, Constants.TEST_NET_CHAIN_ID);
        libraClient.submit(Utils.toLCSHex(st));
        Transaction transaction = libraClient.waitForTransaction(st, DEFAULT_TIMEOUT);
        return new TransactionAndSigned(transaction, st);
    }

    public static class TransactionAndSigned {
        Transaction transaction;
        SignedTransaction signedTransaction;

        public TransactionAndSigned(Transaction transaction, SignedTransaction signedTransaction) {
            this.transaction = transaction;
            this.signedTransaction = signedTransaction;
        }

    }
}
