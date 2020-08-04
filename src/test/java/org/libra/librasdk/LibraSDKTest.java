package org.libra.librasdk;


import com.facebook.serde.Bytes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.libra.librasdk.dto.*;
import org.libra.librasdk.resources.*;
import org.libra.stdlib.Stdlib;
import org.libra.types.AccountAddress;
import org.libra.types.SignedTransaction;
import org.libra.types.TransactionAuthenticator;
import org.libra.types.TypeTag;

import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LibraSDKTest {
    public static final int DEFAULT_TIMEOUT = 10 * 1000; // 10 seconds

    LibraSDK libraSDK;

    @Before
    public void init() {
        LibraNetwork testnet = LibraNetwork.TESTNET;
        libraSDK = new LibraSDK(testnet);
    }

    @Test
    public void getTransactions() throws Exception {
        List<LibraTransaction> transactions = libraSDK.getTransactions(1754993, 2, false);
        assertEquals(2, transactions.size());
    }

    @Test
    public void getTransaction() throws Exception {
        LibraTransaction transaction = libraSDK.getTransaction(123, false);

        assertNotNull(transaction);
        assertNotNull(transaction.getRawTransaction());

        BaseTransaction baseTransaction = transaction.getBaseTransaction();
        if(baseTransaction instanceof PeerToPeerTransaction) {
            PeerToPeerTransaction peerToPeerTransaction = (PeerToPeerTransaction) baseTransaction;
            assertNotNull(peerToPeerTransaction.getScript());
                peerToPeerTransaction.getAmount();
                peerToPeerTransaction.getSender();
        }else if(baseTransaction instanceof BlockMetadataTransaction){
            ((BlockMetadataTransaction)baseTransaction).getTimestampUsecs();
        }

    }

    @Test
    public void testGetAccount() {
        LibraAccount response = libraSDK.getAccount(Constants.ROOT_ACCOUNT_ADDRESS);
        Assert.assertNotNull(response);
        Assert.assertEquals("unknown", response.getRole().getAsString());
        Assert.assertFalse(response.getAuthenticationKey().isEmpty());
        Assert.assertFalse(response.getReceivedEventsKey().isEmpty());
        Assert.assertFalse(response.getDelegatedKeyRotationCapability());
        Assert.assertFalse(response.getDelegatedWithdrawalCapability());
        Assert.assertFalse(response.getIsFrozen());
        Assert.assertFalse(response.getSentEventsKey().isEmpty());
        Assert.assertEquals(0, response.getBalances().size());
    }

    @Test
    public void testGetAccountTransaction() throws Exception {
        LibraTransaction response = libraSDK.getAccountTransaction(Constants.ROOT_ACCOUNT_ADDRESS, 1, true);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.getVersion() > 0);
        Assert.assertTrue(response.getHash().length() > 0);
        Assert.assertTrue(response.getGasUsed() > 0);
        Assert.assertEquals("user", response.getBaseTransaction().getType());
        Assert.assertTrue(response.getRawTransaction().isExecuted());
    }

    // FIXME
    @Test
    public void testSubmitTransaction() throws Exception {
        TestData data = TestData.get();
        LocalAccount account1 = data.local_account;
        LocalAccount account2 = data.local_account2;
        String currencyCode = "LBR";

        TestNetFaucetService.mintCoins(libraSDK, coins(100), account1.libra_auth_key, currencyCode);
        TestNetFaucetService.mintCoins(libraSDK, coins(100), account2.libra_auth_key, currencyCode);

        org.libra.types.Script script = createP2PScript(account2.getAccountAddress(), currencyCode, coins(1));
        LibraAccount account1Data = libraSDK.getAccount(account1.libra_account_address);
        SignedTransaction st = Utils.signTransaction(account1,
                account1Data.getSequenceNumber().longValue(),
                script,
                coins(1),
                0,
                currencyCode,
                100000000000L,
                Constants.TEST_NET_CHAIN_ID);
        libraSDK.submit(Utils.toLCSHex(st));
        LibraTransaction p2p = libraSDK.waitForTransaction(
                Utils.addressToHex(st.raw_txn.sender),
                st.raw_txn.sequence_number,
                true,
                DEFAULT_TIMEOUT);

        PeerToPeerTransaction p2pTransaction = (PeerToPeerTransaction) p2p.getBaseTransaction();
        Assert.assertNotNull(p2p);
        assertTrue(p2p.getVmStatus(), p2p.getRawTransaction().isExecuted());
        Assert.assertEquals(
                Utils.bytesToHex(((TransactionAuthenticator.Ed25519) st.authenticator).signature.value),
                p2pTransaction.getSignature().toUpperCase()
        );
        Assert.assertEquals(2, p2p.getEvents().length);
        Assert.assertEquals("sentpayment", p2p.getEvents()[0].data.type);
        Assert.assertEquals("receivedpayment", p2p.getEvents()[1].data.type);
    }

    @Test
    public void testSubmitExpiredTransaction() throws Exception {
        TestData data = TestData.get();
        LocalAccount account1 = data.local_account;
        LocalAccount account2 = data.local_account2;

        String currencyCode = "LBR";
        TestNetFaucetService.mintCoins(libraSDK, coins(100), account1.libra_auth_key, currencyCode);
        TestNetFaucetService.mintCoins(libraSDK, coins(100), account2.libra_auth_key, currencyCode);

        org.libra.types.Script script = createP2PScript(account2.getAccountAddress(), currencyCode, coins(1));
        LibraAccount account1Data = libraSDK.getAccount(account1.libra_account_address);
        SignedTransaction st = Utils.signTransaction(account1,
                account1Data.getSequenceNumber().longValue(),
                script,
                coins(1),
                0,
                currencyCode,
                0L,
                Constants.TEST_NET_CHAIN_ID);
//        assertThrows("Server error: VM Validation error: TRANSACTION_EXPIRED", JsonRpcErrorException.class, () -> {
//            libraSDK.submit(Utils.toLCSHex(st));
//        });
    }

    @Test
    public void testSubmitTransactionAndExecuteFailed() throws Exception {
        TestData data = TestData.get();
        LocalAccount account1 = data.local_account;
        LocalAccount account3 = data.local_account3;

        String currencyCode = "LBR";
        TestNetFaucetService.mintCoins(libraSDK, coins(100), account1.libra_auth_key, currencyCode);

        org.libra.types.Script script = createP2PScript(account3.getAccountAddress(), currencyCode, coins(1));
        LibraAccount account1Data = libraSDK.getAccount(account1.libra_account_address);
        SignedTransaction st = Utils.signTransaction(account1,
                account1Data.getSequenceNumber().longValue(),
                script,
                coins(1),
                0,
                currencyCode,
                100000000000L,
                Constants.TEST_NET_CHAIN_ID);
        libraSDK.submit(Utils.toLCSHex(st));

        LibraTransaction p2p = libraSDK.waitForTransaction(
                Utils.addressToHex(st.raw_txn.sender),
                st.raw_txn.sequence_number,
                true,
                DEFAULT_TIMEOUT);
        Assert.assertNotNull(p2p);
        Assert.assertEquals(Transaction.VM_STATUS_MOVE_ABORT, p2p.getVmStatus());
    }

    @Test
    public void testGetTransactions() throws Exception {
        List<LibraTransaction> transactions = libraSDK.getTransactions(0, 1000, true);
        Assert.assertNotNull(transactions);
        Assert.assertTrue(transactions.size() > 0);
    }

    @Test
    public void testGetEvents(){
        Currency[] currencies = libraSDK.getCurrencies();

        List<Event> events = libraSDK.getEvents(currencies[0].mint_events_key, 0L, 1000L);
        Assert.assertNotNull(events);
        Assert.assertTrue(events.size() > 0);
    }

    private org.libra.types.Script createP2PScript(AccountAddress address, String currencyCode, long amount) {
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

    @Test
    public void getAccount() {
        LibraAccount account = libraSDK.getAccount(Constants.ROOT_ACCOUNT_ADDRESS);
        assertNotNull(account);
    }

    @Test
    public void testGetMetadata() {
        Metadata response = libraSDK.getMetadata();
        Assert.assertNotNull(response);
        Assert.assertTrue(response.timestamp > new Date().getTime() - 600);
        Assert.assertTrue(response.version > 1000);
    }


    @Test
    public void testGetMetadataByVersion() {
        Metadata response = libraSDK.getMetadata(1);
        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.version);
        Assert.assertEquals(0, response.timestamp);
    }

    @Test
    public void testGetCurrencies() {
        Currency[] response = libraSDK.getCurrencies();
        Assert.assertNotNull(response);
        Assert.assertEquals(3, response.length);
        Assert.assertEquals("Coin1", response[0].code);
        Assert.assertEquals("Coin2", response[1].code);
        Assert.assertEquals("LBR", response[2].code);
    }

}