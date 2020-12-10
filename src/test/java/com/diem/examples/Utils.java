// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem.examples;

import com.diem.*;
import com.diem.jsonrpc.StaleResponseException;
import com.diem.jsonrpc.JsonRpc;
import com.diem.types.RawTransaction;
import com.diem.types.Script;
import com.diem.stdlib.Helpers;
import com.diem.types.SignedTransaction;
import com.diem.types.TransactionPayload;

import java.util.Calendar;

public class Utils {
    /**
     * SubmitAndWait creates transaction for given script, then submit and wait for
     * the transaction executed.
     * To keep logic simple:
     * - this function simply throws unexpected error.
     * - assume sender account has "XUS" currency and use it as gas currency
     * - always use 0 gasUnitPrice
     * This function returns back executed transaction.
     */
    public static long submitAndWait(DiemClient client, LocalAccount localAccount, Script script) throws DiemException {
        long seq = client.getAccount(localAccount.address).getSequenceNumber();
        // it is recommended to set short expiration time for peer to peer transaction,
        // as Diem blockchain transaction execution is fast.
        Calendar expiration = Calendar.getInstance();
        expiration.add(Calendar.SECOND, 30);
        SignedTransaction txn = Signer.sign(localAccount.privateKey, new RawTransaction(
                localAccount.address,
                seq,
                new TransactionPayload.Script(script),
                1000000l,
                0l,
                Testnet.XUS,
                expiration.getTimeInMillis(),
                Testnet.CHAIN_ID
        ));
        try {
            client.submit(txn);
        } catch (StaleResponseException e) {
            // ignore stale response exception for submit.
            // submit probably succeed even hit a stale server.
        }


        // WaitForTransaction retried for StaleResponseException
        // already, hence here we panic if got error (including timeout error)
        JsonRpc.Transaction transaction = client.waitForTransaction(txn, 30 * 1000);
        System.out.println("version: " + transaction.getVersion() + ", status: " + transaction.getVmStatus().getType());
        return transaction.getVersion();
    }

    /**
     * Generate an local account, then create on-chain account with enough coins for the example usage.
     */
    public static LocalAccount genAccount(DiemClient client) {
        return genAccount(client, 100000000);
    }

    public static LocalAccount genAccount(DiemClient client, int amount) {
        LocalAccount parent = LocalAccount.generate();
        Testnet.mintCoins(client, amount, parent.authKey.hex(), Testnet.XUS);
        return parent;
    }

    public static LocalAccount genChildVASPAccount(DiemClient client, LocalAccount senderParentVASPAccount, long initAmount) throws DiemException {
        LocalAccount account = LocalAccount.generate();
        Utils.submitAndWait(client, senderParentVASPAccount,
                Helpers.encode_create_child_vasp_account_script(
                        Testnet.XUS_TYPE, account.address, account.authKey.prefix(),
                        false, initAmount));
        return account;
    }

    public static long getAccountBalance(DiemClient client, LocalAccount account) throws DiemException {
        StaleResponseException staleResponseException = null;
        for (int retry = 100; retry > 0; retry--) {
            try {
                return client.getAccount(account.address).getBalances(0).getAmount();
            } catch (StaleResponseException e) {
                staleResponseException = e;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException interruptedException) {
                    throw new RuntimeException(interruptedException);
                }
                continue;
            }
        }
        throw staleResponseException;
    }
}
