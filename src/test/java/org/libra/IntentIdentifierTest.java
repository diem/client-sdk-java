package org.libra;

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
import static org.libra.AccountIdentifier.NetworkPrefix.MainnetPrefix;
import static org.libra.IntentIdentifier.decodeToIntent;

public class IntentIdentifierTest {
    AccountIdentifier accountIdentifier;
    String encodedAccount;

    @Before
    public void setUp() throws Exception {
        AccountAddress accountAddress = Utils.hexToAddress("f72589b71ff4f8d139674a3f7369c69b");
        SubAddress subAddress = new SubAddress("cf64428bdeb62af2");
        accountIdentifier = new AccountIdentifier(MainnetPrefix, accountAddress, subAddress);
        encodedAccount = accountIdentifier.encode();
    }

    @Test
    public void decodeToIntent_withoutParams() throws LibraSDKException {
        IntentIdentifier intentIdentifier = new IntentIdentifier(accountIdentifier);
        String intentEncoded = intentIdentifier.encode();
        assertEquals(String.format("libra://%s", encodedAccount), intentEncoded);

        IntentIdentifier decodeToIntentIdentifier = decodeToIntent(MainnetPrefix, intentEncoded);
        assertTrue(intentIdentifier.isValuesEqual(decodeToIntentIdentifier));
    }

    @Test
    public void decodeToIntent_withParams() throws LibraSDKException {
        IntentIdentifier intentIdentifier = new IntentIdentifier(accountIdentifier, "LBR", 666);
        String intentEncoded = intentIdentifier.encode();
        assertEquals(String.format("libra://%s?am=666&c=LBR", encodedAccount), intentEncoded);

        IntentIdentifier decodeToIntentIdentifier = decodeToIntent(MainnetPrefix, intentEncoded);
        assertTrue(intentIdentifier.isValuesEqual(decodeToIntentIdentifier));
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
        exceptionRule.expectMessage("invalid intentIdentifier scheme");
        decodeToIntent(MainnetPrefix, "http://account");
    }

    @Test
    public void decodeToIntent_invalidIdentifier() throws LibraSDKException {
        exceptionRule.expect(AddressFormatException.class);
        exceptionRule.expectMessage("Missing human-readable part");
        decodeToIntent(MainnetPrefix, "libra://accountid");
    }
}