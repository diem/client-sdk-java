// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.examples;

import org.libra.*;
import org.libra.jsonrpc.StaleResponseException;
import org.libra.jsonrpctypes.JsonRpc;
import org.libra.stdlib.Helpers;
import org.libra.types.RawTransaction;
import org.libra.types.Script;
import org.libra.types.SignedTransaction;
import org.libra.types.TransactionPayload;
import org.libra.utils.CurrencyCode;

import java.util.Calendar;

public class Utils {
    /**
     * SubmitAndWait creates transaction for given script, then submit and wait for
     * the transaction executed.
     * To keep logic simple:
     * - this function simply throws unexpected error.
     * - assume sender account has "LBR" currency and use it as gas currency
     * - always use 0 gasUnitPrice
     * This function returns back executed transaction.
     */
    public static long submitAndWait(LibraClient client, LocalAccount localAccount, Script script) throws LibraException {
        JsonRpc.Account account = client.getAccount(localAccount.address);
        long seq = account.getSequenceNumber();
        // it is recommended to set short expiration time for peer to peer transaction,
        // as Libra blockchain transaction execution is fast.
        Calendar expiration = Calendar.getInstance();
        expiration.add(Calendar.SECOND, 30);
        SignedTransaction txn = Signer.sign(localAccount.privateKey, new RawTransaction(
                localAccount.address,
                seq,
                new TransactionPayload.Script(script),
                1000000l,
                0l,
                "LBR",
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
    public static LocalAccount genAccount(LibraClient client) {
        return genAccount(client, 100000000);
    }

    public static LocalAccount genAccount(LibraClient client, int amount) {
        LocalAccount parent = LocalAccount.generate();
        Testnet.mintCoins(client, amount, parent.authKey.hex(), CurrencyCode.LBR);
        return parent;
    }

    public static LocalAccount genChildVASPAccount(LibraClient client, LocalAccount senderParentVASPAccount, long initAmount) throws LibraException {
        LocalAccount account = LocalAccount.generate();
        Utils.submitAndWait(client, senderParentVASPAccount,
                Helpers.encode_create_child_vasp_account_script(
                        CurrencyCode.LBR_TYPE, account.address, account.authKey.prefix(),
                        false, initAmount));
        return account;
    }

    public static long getAccountBalance(LibraClient client, LocalAccount account) throws LibraException {
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
