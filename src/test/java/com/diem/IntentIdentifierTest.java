// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem;

import com.diem.types.AccountAddress;
import com.diem.utils.AccountAddressUtils;
import org.bitcoinj.core.AddressFormatException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IntentIdentifierTest {
    private AccountIdentifier accountIdentifier;
    private String encodedAccount;

    @Before
    public void setUp() {
        AccountAddress accountAddress = AccountAddressUtils.create("f72589b71ff4f8d139674a3f7369c69b");
        SubAddress subAddress = new SubAddress("cf64428bdeb62af2");
        accountIdentifier = new AccountIdentifier(AccountIdentifier.NetworkPrefix.MainnetPrefix, accountAddress, subAddress);
        encodedAccount = accountIdentifier.encodeV1();
    }

    @Test
    public void decodeToIntent_withoutParams() {
        IntentIdentifier intentIdentifier = new IntentIdentifier(accountIdentifier, null, 0);
        String intentEncoded = intentIdentifier.encode();
        assertEquals(String.format("diem://%s", encodedAccount), intentEncoded);

        IntentIdentifier decodeToIntentIdentifier = IntentIdentifier.decode(AccountIdentifier.NetworkPrefix.MainnetPrefix, intentEncoded);
        assertTrue(intentIdentifier.equals(decodeToIntentIdentifier));
    }

    @Test
    public void decodeToIntent_withParams() {
        IntentIdentifier intentIdentifier = new IntentIdentifier(accountIdentifier, "XDX", 666);
        String intentEncoded = intentIdentifier.encode();
        assertEquals(String.format("diem://%s?am=666&c=XDX", encodedAccount), intentEncoded);

        IntentIdentifier decodeToIntentIdentifier = IntentIdentifier.decode(AccountIdentifier.NetworkPrefix.MainnetPrefix, intentEncoded);
        assertTrue(intentIdentifier.equals(decodeToIntentIdentifier));
    }

    @Test
    public void decodeToIntent_invalidUrl() {
        assertThrows("Illegal character in fragment at index", IllegalArgumentException.class, () -> {
            IntentIdentifier.decode(AccountIdentifier.NetworkPrefix.MainnetPrefix, "s/s/###...");
        });
    }

    @Test
    public void decodeToIntent_invalidScheme() {
        assertThrows("invalid intent identifier scheme", IllegalArgumentException.class, () -> {
            IntentIdentifier.decode(AccountIdentifier.NetworkPrefix.MainnetPrefix, "http://account");
        });
    }

    @Test
    public void decodeToIntent_invalidIdentifier() {
        assertThrows("Missing human-readable part", AddressFormatException.class, () -> {
            IntentIdentifier.decode(AccountIdentifier.NetworkPrefix.MainnetPrefix, "diem://accountid");
        });
    }
}