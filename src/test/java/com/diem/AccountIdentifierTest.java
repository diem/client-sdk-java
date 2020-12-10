// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem;

import com.diem.types.AccountAddress;
import com.diem.utils.AccountAddressUtils;
import org.bitcoinj.core.Bech32;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
        String idStr = new AccountIdentifier(AccountIdentifier.NetworkPrefix.MainnetPrefix, accountAddress, subAddress).encodeV1();

        assertEquals("dm1p7ujcndcl7nudzwt8fglhx6wxn08kgs5tm6mz4us2vfufk", idStr);

        AccountIdentifier identifier = AccountIdentifier.decode(AccountIdentifier.NetworkPrefix.MainnetPrefix, idStr);

        Assert.assertEquals(identifier.getAccountAddress(), accountAddress);
        assertArrayEquals(identifier.getSubAddress().getBytes(), subAddress.getBytes());
        Assert.assertEquals(identifier.getPrefix(), AccountIdentifier.NetworkPrefix.MainnetPrefix);
    }

    @Test
    public void testEncodeDecodeAccountIdentifierWithoutSubAddress_success() {
        String idStr = new AccountIdentifier(AccountIdentifier.NetworkPrefix.MainnetPrefix, accountAddress).encodeV1();

        assertEquals("dm1p7ujcndcl7nudzwt8fglhx6wxnvqqqqqqqqqqqqqd8p9cq", idStr);

        AccountIdentifier identifier = AccountIdentifier.decode(AccountIdentifier.NetworkPrefix.MainnetPrefix, idStr);

        Assert.assertEquals(identifier.getAccountAddress(), accountAddress);
        assertArrayEquals(identifier.getSubAddress().getBytes(), new byte[8]);
        Assert.assertEquals(identifier.getPrefix(), AccountIdentifier.NetworkPrefix.MainnetPrefix);
    }

    @Test
    public void testEncodeDecodeAccountIdentifierString_invalidChecksum() {
        String encodeAccount = new AccountIdentifier(AccountIdentifier.NetworkPrefix.MainnetPrefix, accountAddress, subAddress).encodeV1();
        String invalidID = encodeAccount.substring(0, encodeAccount.length() - 1);

        assertThrows("Checksum does not validate", IllegalArgumentException.class, () -> {
            AccountIdentifier.decode(AccountIdentifier.NetworkPrefix.MainnetPrefix, invalidID);
        });
    }

    @Test
    public void testEncodeDecodeAccountIdentifierString_invalidAccountAddressLength() {
        byte[] bytes = AccountAddressUtils.bytes(accountAddress);
        byte[] data = AccountIdentifier.convertBits(bytes, 8, 5, true);
        String bech32Encode = Bech32.encode(AccountIdentifier.NetworkPrefix.MainnetPrefix.name(), data);

        assertThrows("Invalid network prefix", IllegalArgumentException.class, () -> {
            AccountIdentifier.decode(AccountIdentifier.NetworkPrefix.MainnetPrefix, bech32Encode);
        });
    }

    @Test
    public void testEncodeDecodeAccountIdentifierString_invalidVersion() {
        AccountIdentifier id = new AccountIdentifier(AccountIdentifier.NetworkPrefix.TestnetPrefix, accountAddress, subAddress);
        String encodedId = id.encodeV1();
        String invalidVersionId = encodedId.substring(0, 4) + "x" + encodedId.substring(5);
        assertThrows("unknown account identifier format version: 3", IllegalArgumentException.class, () -> {
            AccountIdentifier.decode(AccountIdentifier.NetworkPrefix.MainnetPrefix, invalidVersionId);
        });
    }
}