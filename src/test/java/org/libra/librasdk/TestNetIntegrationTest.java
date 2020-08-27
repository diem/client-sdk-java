// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

import com.facebook.serde.Bytes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.libra.librasdk.dto.Metadata;
import org.libra.librasdk.dto.Transaction;
import org.libra.librasdk.dto.*;
import org.libra.librasdk.jsonrpc.JSONRPCErrorException;
import org.libra.stdlib.Helpers;
import org.libra.types.*;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.libra.librasdk.Net.TestNet;


public class TestNetIntegrationTest {

    public static final int DEFAULT_TIMEOUT = 10 * 1000; // 10 seconds
    private LibraClient libraClient;
    private LocalAccount account1;
    private LocalAccount account2;
    private LocalAccount account3;

    @Before
    public void setup() {
        libraClient = new LibraClient(TestNet());
        account1 = Utils.generateLocalAccountFromPrivateKey(
                "76e3de861d516283dc285e12ddadc95245a9e98f351c910b0ad722f790bac273");
        account2 = Utils.generateLocalAccountFromPrivateKey(
                "b13968ad5722ee203968f7deea565b2f4266f923b3292065b6e190c368f91036");
        account3 = Utils.generateLocalAccountFromPrivateKey(
                "aceb051a2c02ebe6493000613bd467ea97a6051988637440c918584043a769dd");
    }

    @Test
    public void testGetMetadata() throws Exception {
        libraClient = new LibraClient("https://client.testnet.libra.org/v1", 2);

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
        Assert.assertEquals(1598492123453462L, response.timestamp);
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
        Account response = libraClient.getAccount(Constants.ROOT_ACCOUNT_ADDRESS);
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
    public void testGetAccountTransaction() throws LibraSDKException {
        Transaction response = libraClient.getAccountTransaction(Constants.ROOT_ACCOUNT_ADDRESS,
                1, true);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.version > 0);
        Assert.assertTrue(response.hash.length() > 0);
        Assert.assertTrue(response.gas_used > 0);
        Assert.assertEquals("user", response.transaction.type);
        Assert.assertTrue(response.isExecuted());
    }

    @Test
    public void testSubmitTransaction() throws Exception {
        String currencyCode = "LBR";
        TestNetFaucetService.mintCoins(libraClient, coins(100), account1.libra_auth_key,
                currencyCode);
        TestNetFaucetService.mintCoins(libraClient, coins(100), account2.libra_auth_key,
                currencyCode);

        Script script = createP2PScript(account2.getAccountAddress(), currencyCode, coins(1));
        Account account1Data = libraClient.getAccount(account1.libra_account_address);
        SignedTransaction st = Utils.signTransaction(account1,
                account1Data.sequence_number,
                script,
                coins(1),
                0,
                currencyCode,
                100000000000L,
                Constants.TEST_NET_CHAIN_ID);
        libraClient.submit(Utils.toLCSHex(st));
        Transaction p2p = libraClient.waitForTransaction(
                Utils.addressToHex(st.raw_txn.sender),
                st.raw_txn.sequence_number,
                true,
                DEFAULT_TIMEOUT);
        assertNotNull(p2p);
        assertTrue(p2p.vm_status.toString(), p2p.isExecuted());
        assertEquals(
                Utils.bytesToHex(((TransactionAuthenticator.Ed25519) st.authenticator).signature.value),
                p2p.transaction.signature.toUpperCase()
        );
        assertEquals(2, p2p.events.length);
        assertEquals("sentpayment", p2p.events[0].data.type);
        assertEquals("receivedpayment", p2p.events[1].data.type);
    }

    @Test
    public void testTransferTransaction() throws Exception {
        String currencyCode = "LBR";
        TestNetFaucetService.mintCoins(libraClient, coins(100), account1.libra_auth_key,
                currencyCode);
        TestNetFaucetService.mintCoins(libraClient, coins(100), account2.libra_auth_key,
                currencyCode);

        SignedTransaction signedTransaction = libraClient.transfer(account1.libra_account_address,
                account1.libra_auth_key,
                account1.private_key, account1.public_key,
                account2.libra_account_address, 1000000L,
                1000000L, 0L, currencyCode, 100000000000L, Constants.TEST_NET_CHAIN_ID,
                new byte[]{}, new byte[]{});
        Transaction p2p = libraClient.waitForTransaction(
                Utils.addressToHex(signedTransaction.raw_txn.sender),
                signedTransaction.raw_txn.sequence_number,
                true,
                DEFAULT_TIMEOUT);
        assertNotNull(p2p);
        assertTrue(p2p.vm_status.toString(), p2p.isExecuted());
        assertEquals(
                Utils.bytesToHex(((TransactionAuthenticator.Ed25519) signedTransaction.authenticator).signature.value),
                p2p.transaction.signature.toUpperCase()
        );
        assertEquals(2, p2p.events.length);
        assertEquals("sentpayment", p2p.events[0].data.type);
        assertEquals("receivedpayment", p2p.events[1].data.type);
    }

    @Test
    public void testSubmitExpiredTransaction() throws Exception {
        String currencyCode = "LBR";
        TestNetFaucetService.mintCoins(libraClient, coins(100), account1.libra_auth_key,
                currencyCode);
        TestNetFaucetService.mintCoins(libraClient, coins(100), account2.libra_auth_key,
                currencyCode);

        Script script = createP2PScript(account2.getAccountAddress(), currencyCode, coins(1));
        Account account1Data = libraClient.getAccount(account1.libra_account_address);
        SignedTransaction st = Utils.signTransaction(account1,
                account1Data.sequence_number,
                script,
                coins(1),
                0,
                currencyCode,
                0L,
                Constants.TEST_NET_CHAIN_ID);
        assertThrows("Server error: VM Validation error: TRANSACTION_EXPIRED",
                JSONRPCErrorException.class, () -> libraClient.submit(Utils.toLCSHex(st)));
    }

    @Test
    public void testSubmitTransactionAndExecuteFailed() throws Exception {
        String currencyCode = "LBR";
        TestNetFaucetService.mintCoins(libraClient, coins(100), account1.libra_auth_key,
                currencyCode);

        Script script = createP2PScript(account3.getAccountAddress(), currencyCode, coins(1));
        Account account1Data = libraClient.getAccount(account1.libra_account_address);
        SignedTransaction st = Utils.signTransaction(account1,
                account1Data.sequence_number,
                script,
                coins(1),
                0,
                currencyCode,
                100000000000L,
                Constants.TEST_NET_CHAIN_ID);
        libraClient.submit(Utils.toLCSHex(st));

        Transaction p2p = libraClient.waitForTransaction(
                Utils.addressToHex(st.raw_txn.sender),
                st.raw_txn.sequence_number,
                true,
                DEFAULT_TIMEOUT);
        assertNotNull(p2p);
        assertEquals(Transaction.VM_STATUS_MOVE_ABORT, p2p.vmStatus());
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

    private Script createP2PScript(AccountAddress address, String currencyCode, long amount) {
        TypeTag token = Utils.createCurrencyCodeTypeTag(currencyCode);
        return Helpers.encode_peer_to_peer_with_metadata_script(
                token,
                address,
                amount,
                new Bytes(new byte[]{}),
                new Bytes(new byte[]{})
        );
    }

    private static long coins(long n) {
        return n * 1000000;
    }
}
