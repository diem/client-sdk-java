// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem.examples;

import com.diem.*;
import com.diem.jsonrpc.StaleResponseException;
import com.diem.jsonrpc.JsonRpc;
import com.diem.types.RawTransaction;
import org.junit.Assert;
import org.junit.Test;
import com.diem.stdlib.Helpers;
import com.diem.types.SignedTransaction;
import com.diem.types.TransactionPayload;

import java.util.Calendar;

public class CreateChildVASP {
    @Test
    public void createChildVASP() throws DiemException {
        DiemClient client = Testnet.createClient();
        LocalAccount parent = Utils.genAccount(client);
        JsonRpc.Account account = client.getAccount(parent.address);
        Assert.assertNotNull(account);
        System.out.println("Parent VASP account:\n" + account);

        LocalAccount childVASP = LocalAccount.generate();
        long seq = client.getAccount(parent.address).getSequenceNumber();
        // it is recommended to set short expiration time for peer to peer transaction,
        // as Diem blockchain transaction execution is fast.
        Calendar expiration = Calendar.getInstance();
        expiration.add(Calendar.SECOND, 30);
        SignedTransaction txn = Signer.sign(parent.privateKey, new RawTransaction(
                parent.address,
                seq,
                new TransactionPayload.Script(Helpers.encode_create_child_vasp_account_script(
                        Testnet.XUS_TYPE,
                        childVASP.address,
                        childVASP.authKey.prefix(),
                        false,
                        1000000l
                )),
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

        JsonRpc.Account childAccount = client.getAccount(childVASP.address);
        Assert.assertNotNull(childAccount);
        System.out.println("Child VASP account:\n" + childAccount);
    }
}
