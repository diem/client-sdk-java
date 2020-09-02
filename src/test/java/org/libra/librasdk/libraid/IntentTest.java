package org.libra.librasdk.libraid;

import org.bitcoinj.core.AddressFormatException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.libra.librasdk.LibraSDKException;
import org.libra.librasdk.Utils;
import org.libra.types.AccountAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.libra.librasdk.libraid.Account.NetworkPrefix.MainnetPrefix;
import static org.libra.librasdk.libraid.Intent.decodeToIntent;

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

    @Test
    public void decodeToIntent_withoutParams() throws LibraSDKException {
        Intent intent = new Intent(account);
        String intentEncoded = intent.encode();
        assertEquals(String.format("libra://%s", encodedAccount), intentEncoded);

        Intent decodeToIntent = decodeToIntent(MainnetPrefix, intentEncoded);
        assertTrue(intent.isValuesEqual(decodeToIntent));
    }

    @Test
    public void decodeToIntent_withParams() throws LibraSDKException {
        Intent intent = new Intent(account, "LBR", 666);
        String intentEncoded = intent.encode();
        assertEquals(String.format("libra://%s?am=666&c=LBR", encodedAccount), intentEncoded);

        Intent decodeToIntent = decodeToIntent(MainnetPrefix, intentEncoded);
        assertTrue(intent.isValuesEqual(decodeToIntent));
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void decodeToIntent_invalidUrl() throws LibraSDKException {
        exceptionRule.expect(LibraSDKException.class);
        exceptionRule.expectMessage("Illegal character in fragment at index");
        decodeToIntent(MainnetPrefix, "s/s/###...");
    }

    @Test
    public void decodeToIntent_invalidScheme() throws LibraSDKException {
        exceptionRule.expect(LibraSDKException.class);
        exceptionRule.expectMessage("invalid intent scheme");
        decodeToIntent(MainnetPrefix, "http://account");
    }

    @Test
    public void decodeToIntent_invalidIdentifier() throws LibraSDKException {
        exceptionRule.expect(AddressFormatException.class);
        exceptionRule.expectMessage("Missing human-readable part");
        decodeToIntent(MainnetPrefix, "libra://accountid");
    }
}