package org.libra.librasdk.libraid;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.libra.librasdk.LibraSDKException;
import org.libra.librasdk.Utils;
import org.libra.types.AccountAddress;

import static org.junit.Assert.assertEquals;
import static org.libra.librasdk.libraid.Account.NetworkPrefix.MainnetPrefix;

public class IntentTest {


    Account account;
    String encodedAccount;

    @Before
    public void setUp() throws Exception {
        AccountAddress accountAddress = Utils.hexToAddress("f72589b71ff4f8d139674a3f7369c69b");
        SubAddress subAddress = new SubAddress("cf64428bdeb62af2");
        account = new Account(MainnetPrefix, accountAddress, subAddress);
        encodedAccount = account.encode();
    }

    @Ignore
    @Test
    public void decodeToIntent_withoutParams() throws LibraSDKException {
        Intent intent = new Intent(account);
        String encode = intent.encode();
        assertEquals(encodedAccount, encode);


    }
}