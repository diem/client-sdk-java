// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.examples;

import org.junit.Assert;
import org.junit.Test;
import org.libra.LibraClient;
import org.libra.LibraException;
import org.libra.LocalAccount;
import org.libra.Testnet;
import org.libra.jsonrpctypes.JsonRpc;
import org.libra.stdlib.Helpers;
import org.libra.utils.CurrencyCode;

public class CreateChildVASP {
    @Test
    public void createChildVASP() throws LibraException {
        LibraClient client = Testnet.createClient();
        LocalAccount parent = Utils.genAccount(client);
        JsonRpc.Account account = client.getAccount(parent.address);
        Assert.assertNotNull(account);
        System.out.println("Parent VASP account:\n" + account);

        LocalAccount childVASP = LocalAccount.generate();
        Utils.submitAndWait(client, parent, Helpers.encode_create_child_vasp_account_script(
                CurrencyCode.LBR_TYPE,
                childVASP.address,
                childVASP.authKey.prefix(),
                false,
                1000000l
        ));

        JsonRpc.Account childAccount = client.getAccount(childVASP.address);
        Assert.assertNotNull(childAccount);
        System.out.println("Child VASP account:\n" + childAccount);
    }
}
