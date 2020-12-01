// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.diem;

import org.bitcoinj.core.Bech32;
import org.diem.AccountIdentifier;
import org.diem.SubAddress;
import org.junit.Before;
import org.junit.Test;
import org.diem.types.AccountAddress;
import org.diem.utils.AccountAddressUtils;

import static org.junit.Assert.*;
import static org.diem.AccountIdentifier.NetworkPrefix.MainnetPrefix;
import static org.diem.AccountIdentifier.NetworkPrefix.TestnetPrefix;

public class AccountIdentifierTest {
    private AccountAddress accountAddress;
    private SubAddress subAddress;

    @Before
    public void setUp() {
        accountAddress = AccountAddressUtils.create("f72589b71ff4f8d139674a3f7369c69b");
        subAddress = new SubAddress("cf64428bdeb62af2");
    }

    @Test
    public void testEncodeDecodeAccountIdentifier_success() {
        String idStr = new AccountIdentifier(MainnetPrefix, accountAddress, subAddress).encodeV1();

        assertEquals("lbr1p7ujcndcl7nudzwt8fglhx6wxn08kgs5tm6mz4usw5p72t", idStr);

        AccountIdentifier identifier = AccountIdentifier.decode(MainnetPrefix, idStr);

        assertArrayEquals(identifier.getAccountAddress().value, accountAddress.value);
        assertArrayEquals(identifier.getSubAddress().getBytes(), subAddress.getBytes());
        assertEquals(identifier.getPrefix(), MainnetPrefix);
    }

    @Test
    public void testEncodeDecodeAccountIdentifierWithoutSubAddress_success() {
        String idStr = new AccountIdentifier(MainnetPrefix, accountAddress).encodeV1();

        assertEquals("lbr1p7ujcndcl7nudzwt8fglhx6wxnvqqqqqqqqqqqqqflf8ma", idStr);

        AccountIdentifier identifier = AccountIdentifier.decode(MainnetPrefix, idStr);

        assertArrayEquals(identifier.getAccountAddress().value, accountAddress.value);
        assertArrayEquals(identifier.getSubAddress().getBytes(), new byte[8]);
        assertEquals(identifier.getPrefix(), MainnetPrefix);
    }

    @Test
    public void testEncodeDecodeAccountIdentifierString_invalidChecksum() {
        String encodeAccount = new AccountIdentifier(MainnetPrefix, accountAddress, subAddress).encodeV1();
        String invalidID = encodeAccount.substring(0, encodeAccount.length() - 1);

        assertThrows("Checksum does not validate", IllegalArgumentException.class, () -> {
            AccountIdentifier.decode(MainnetPrefix, invalidID);
        });
    }

    @Test
    public void testEncodeDecodeAccountIdentifierString_invalidAccountAddressLength() {
        byte[] bytes = AccountAddressUtils.bytes(accountAddress);
        byte[] data = AccountIdentifier.convertBits(bytes, 8, 5, true);
        String bech32Encode = Bech32.encode(MainnetPrefix.name(), data);

        assertThrows("Invalid network prefix", IllegalArgumentException.class, () -> {
            AccountIdentifier.decode(MainnetPrefix, bech32Encode);
        });
    }

    @Test
    public void testEncodeDecodeAccountIdentifierString_invalidVersion() {
        AccountIdentifier id = new AccountIdentifier(TestnetPrefix, accountAddress, subAddress);
        String encodedId = id.encodeV1();
        String invalidVersionId = encodedId.substring(0, 4) + "x" + encodedId.substring(5);
        assertThrows("unknown account identifier format version: 3", IllegalArgumentException.class, () -> {
            AccountIdentifier.decode(MainnetPrefix, invalidVersionId);
        });
    }
}