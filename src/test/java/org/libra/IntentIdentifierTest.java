// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra;

import org.bitcoinj.core.AddressFormatException;
import org.junit.Before;
import org.junit.Test;
import org.libra.types.AccountAddress;
import org.libra.utils.AccountAddressUtils;

import static org.junit.Assert.*;
import static org.libra.AccountIdentifier.NetworkPrefix.MainnetPrefix;
import static org.libra.IntentIdentifier.decode;

public class IntentIdentifierTest {
    private AccountIdentifier accountIdentifier;
    private String encodedAccount;

    @Before
    public void setUp() {
        AccountAddress accountAddress = AccountAddressUtils.create("f72589b71ff4f8d139674a3f7369c69b");
        SubAddress subAddress = new SubAddress("cf64428bdeb62af2");
        accountIdentifier = new AccountIdentifier(MainnetPrefix, accountAddress, subAddress);
        encodedAccount = accountIdentifier.encodeV1();
    }

    @Test
    public void decodeToIntent_withoutParams() {
        IntentIdentifier intentIdentifier = new IntentIdentifier(accountIdentifier, null, 0);
        String intentEncoded = intentIdentifier.encode();
        assertEquals(String.format("libra://%s", encodedAccount), intentEncoded);

        IntentIdentifier decodeToIntentIdentifier = decode(MainnetPrefix, intentEncoded);
        assertTrue(intentIdentifier.equals(decodeToIntentIdentifier));
    }

    @Test
    public void decodeToIntent_withParams() {
        IntentIdentifier intentIdentifier = new IntentIdentifier(accountIdentifier, "LBR", 666);
        String intentEncoded = intentIdentifier.encode();
        assertEquals(String.format("libra://%s?am=666&c=LBR", encodedAccount), intentEncoded);

        IntentIdentifier decodeToIntentIdentifier = decode(MainnetPrefix, intentEncoded);
        assertTrue(intentIdentifier.equals(decodeToIntentIdentifier));
    }

    @Test
    public void decodeToIntent_invalidUrl() {
        assertThrows("Illegal character in fragment at index", IllegalArgumentException.class, () -> {
            decode(MainnetPrefix, "s/s/###...");
        });
    }

    @Test
    public void decodeToIntent_invalidScheme() {
        assertThrows("invalid intent identifier scheme", IllegalArgumentException.class, () -> {
            decode(MainnetPrefix, "http://account");
        });
    }

    @Test
    public void decodeToIntent_invalidIdentifier() {
        assertThrows("Missing human-readable part", AddressFormatException.class, () -> {
            decode(MainnetPrefix, "libra://accountid");
        });
    }
}