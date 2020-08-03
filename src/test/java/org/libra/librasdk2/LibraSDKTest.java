package org.libra.librasdk2;


import org.junit.Before;
import org.junit.Test;
import org.libra.librasdk2.resources.*;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class LibraSDKTest {

    LibraSDK libraSDK;

    @Before
    public void init() {
        LibraNetwork testnet = LibraNetwork.TESTNET;
        libraSDK = new LibraSDK(testnet);
    }

    @Test
    public void getTransactions() {
        List<LibraTransaction> transactions = libraSDK.getTransactions(1754994, 1, false);
        assertEquals(2, transactions.size());
    }

    @Test
    public void getTransaction() {
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
    public void getAccount() {
        LibraAccount account = libraSDK.getAccount("747f3bdf4afcb4e4237ad8eaa1654a52");
        assertNotNull(account);
    }
}