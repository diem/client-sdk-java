// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

import com.facebook.serde.Bytes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.libra.librasdk.dto.*;
import org.libra.librasdk.dto.Metadata;
import org.libra.librasdk.dto.Transaction;
import org.libra.librasdk.jsonrpc.JsonRpcClient;
import org.libra.librasdk.jsonrpc.JsonRpcErrorException;
import org.libra.stdlib.Stdlib;
import org.libra.types.*;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.*;


public class TestNetIntegrationTest {

    public static final int DEFAULT_TIMEOUT = 10 * 1000; // 10 seconds
    private Client client;

    @Before
    public void setup() throws IOException {
        client = new JsonRpcClient(Constants.TEST_NET_JSON_RPC_V1_URL);
    }

    @Test
    public void testGetMetadata() throws Exception {
        Metadata response = client.getMetadata();
        Assert.assertNotNull(response);
        Assert.assertTrue(response.timestamp > new Date().getTime() - 600);
        Assert.assertTrue(response.version > 1000);
    }

    @Test
    public void testGetMetadataByVersion() throws Exception {
        Metadata response = client.getMetadata(1);
        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.version);
        Assert.assertEquals(0, response.timestamp);
    }

    @Test
    public void testGetCurrencies() throws Exception {
        Currency[] response = client.getCurrencies();
        Assert.assertNotNull(response);
        Assert.assertEquals(3, response.length);
        Assert.assertEquals("Coin1", response[0].code);
        Assert.assertEquals("Coin2", response[1].code);
        Assert.assertEquals("LBR", response[2].code);
    }

    @Test
    public void testHandleRPCCallError() {
        assertThrows(JsonRpcErrorException.class, () -> {
            client.call(Method.get_currencies, Currency[].class, "invalid argument");
        });
    }

    @Test
    public void testGetAccount() throws Exception {
        Account response = client.getAccount(Constants.ROOT_ACCOUNT_ADDRESS);
        Assert.assertNotNull(response);
        Assert.assertEquals("unknown", response.role.getAsString());
        Assert.assertFalse(response.authentication_key.isEmpty());
        Assert.assertFalse(response.received_events_key.isEmpty());
        Assert.assertFalse(response.delegated_key_rotation_capability);
        Assert.assertFalse(response.delegated_withdrawal_capability);
        Assert.assertFalse(response.is_frozen);
        Assert.assertFalse(response.sent_events_key.isEmpty());
        Assert.assertEquals(2, response.balances.length);
    }

    @Test
    public void testGetAccountTransaction() throws LibraSDKException {
        Transaction response = client.getAccountTransaction(Constants.ROOT_ACCOUNT_ADDRESS, 1, true);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.version > 0);
        Assert.assertTrue(response.hash.length() > 0);
        Assert.assertTrue(response.gas_used > 0);
        Assert.assertEquals("user", response.transaction.type);
        Assert.assertTrue(response.isExecuted());
    }

    @Test
    public void testSubmitTransaction() throws Exception {
        TestData data = TestData.get();
        LocalAccount account1 = data.local_account;
        LocalAccount account2 = data.local_account2;
        String currencyCode = "LBR";

        TestNetFaucetService.mintCoins(client, coins(100), account1.libra_auth_key, currencyCode);
        TestNetFaucetService.mintCoins(client, coins(100), account2.libra_auth_key, currencyCode);

        Script script = createP2PScript(account2.getAccountAddress(), currencyCode, coins(1));
        Account account1Data = client.getAccount(account1.libra_account_address);
        SignedTransaction st = Utils.signTransaction(account1,
                account1Data.sequence_number,
                script,
                coins(1),
                0,
                currencyCode,
                100000000000l,
                Constants.TEST_NET_CHAIN_ID);
        client.submit(Utils.toLCSHex(st));
        Transaction p2p = client.waitForTransaction(
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
    public void testSubmitExpiredTransaction() throws Exception {
        TestData data = TestData.get();
        LocalAccount account1 = data.local_account;
        LocalAccount account2 = data.local_account2;

        String currencyCode = "LBR";
        TestNetFaucetService.mintCoins(client, coins(100), account1.libra_auth_key, currencyCode);
        TestNetFaucetService.mintCoins(client, coins(100), account2.libra_auth_key, currencyCode);

        Script script = createP2PScript(account2.getAccountAddress(), currencyCode, coins(1));
        Account account1Data = client.getAccount(account1.libra_account_address);
        SignedTransaction st = Utils.signTransaction(account1,
                account1Data.sequence_number,
                script,
                coins(1),
                0,
                currencyCode,
                0l,
                Constants.TEST_NET_CHAIN_ID);
        assertThrows("Server error: VM Validation error: TRANSACTION_EXPIRED", JsonRpcErrorException.class, () -> {
            client.submit(Utils.toLCSHex(st));
        });
    }

    @Test
    public void testSubmitTransactionAndExecuteFailed() throws Exception {
        TestData data = TestData.get();
        LocalAccount account1 = data.local_account;
        LocalAccount account3 = data.local_account3;

        String currencyCode = "LBR";
        TestNetFaucetService.mintCoins(client, coins(100), account1.libra_auth_key, currencyCode);

        Script script = createP2PScript(account3.getAccountAddress(), currencyCode, coins(1));
        Account account1Data = client.getAccount(account1.libra_account_address);
        SignedTransaction st = Utils.signTransaction(account1,
                account1Data.sequence_number,
                script,
                coins(1),
                0,
                currencyCode,
                100000000000l,
                Constants.TEST_NET_CHAIN_ID);
        client.submit(Utils.toLCSHex(st));

        Transaction p2p = client.waitForTransaction(
                Utils.addressToHex(st.raw_txn.sender),
                st.raw_txn.sequence_number,
                true,
                DEFAULT_TIMEOUT);
        assertNotNull(p2p);
        assertEquals(Transaction.VM_STATUS_MOVE_ABORT, p2p.vmStatus());
    }

    @Test
    public void testGetTransactions() throws LibraSDKException {
        Transaction[] transactions = client.getTransactions(0, 1000, true);
        Assert.assertNotNull(transactions);
        Assert.assertTrue(transactions.length > 0);
    }

    @Test
    public void testGetEvents() throws LibraSDKException {
        Currency[] currencies = client.getCurrencies();

        Event[] events = client.getEvents(currencies[0].mint_events_key, 0l, 1000l);
        Assert.assertNotNull(events);
        Assert.assertTrue(events.length > 0);
    }

    private Script createP2PScript(AccountAddress address, String currencyCode, long amount) {
        TypeTag token = Utils.createCurrencyCodeTypeTag(currencyCode);
        return Stdlib.encode_peer_to_peer_with_metadata_script(
                token,
                address,
                amount,
                new Bytes(new byte[]{}),
                new Bytes(new byte[]{})
        );
    }

    public static long coins(long n) {
        return n * 1000000;
    }
}
