package org.libra.librasdk.libraid;

import org.bitcoinj.core.AddressFormatException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.libra.librasdk.BechBenny32;
import org.libra.librasdk.LibraSDKException;
import org.libra.librasdk.Utils;
import org.libra.types.AccountAddress;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.libra.librasdk.libraid.Account.NetworkPrefix.MainnetPrefix;

public class AccountTest {
    public static final String ACCOUNT_ADDRESS = "f72589b71ff4f8d139674a3f7369c69b";
    AccountAddress accountAddress;
    SubAddress subAddress;

    @Before
    public void setUp() throws Exception {
        accountAddress = Utils.hexToAddress(ACCOUNT_ADDRESS);
        subAddress = new SubAddress("cf64428bdeb62af2");
    }

    @Test
    public void testEncodeDecodeAccountIdentifier_success() throws LibraSDKException {
        String encodeAccount =
                Account.encodeAccount(Account.NetworkPrefix.MainnetPrefix, accountAddress,
                        subAddress);

        assertEquals("lbr1p7ujcndcl7nudzwt8fglhx6wxn08kgs5tm6mz4usw5p72t", encodeAccount);

        Account account =
                Account.decodeToAccount(Account.NetworkPrefix.MainnetPrefix, encodeAccount);

        assertArrayEquals(account.accountAddress.value, accountAddress.value);
        assertArrayEquals(account.subAddress.getSubAddress(), subAddress.getSubAddress());
        assertEquals(account.prefix, Account.NetworkPrefix.MainnetPrefix);
        assertEquals(account.version, 1);
    }

    @Test
    public void testEncodeDecodeAccountIdentifierWithoutSubAddress_success()
    throws LibraSDKException {
        AccountAddress accountAddress = Utils.hexToAddress("f72589b71ff4f8d139674a3f7369c69b");
        byte[] subAddress = {0, 0, 0, 0, 0, 0, 0, 0};
        String encodeAccount =
                Account.encodeAccount(Account.NetworkPrefix.MainnetPrefix, accountAddress,
                        new SubAddress(subAddress));

        assertEquals("lbr1p7ujcndcl7nudzwt8fglhx6wxnvqqqqqqqqqqqqqflf8ma", encodeAccount);

        Account account =
                Account.decodeToAccount(Account.NetworkPrefix.MainnetPrefix, encodeAccount);

        assertArrayEquals(account.accountAddress.value, accountAddress.value);
        assertArrayEquals(account.subAddress.getSubAddress(), subAddress);
        assertEquals(account.prefix, Account.NetworkPrefix.MainnetPrefix);
        assertEquals(account.version, 1);
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testEncodeDecodeAccountIdentifierString_invalidChecksum() throws LibraSDKException {
        AccountAddress accountAddress = Utils.hexToAddress("f72589b71ff4f8d139674a3f7369c69b");
        SubAddress subAddress = new SubAddress("cf64428bdeb62af2");

        String encodeAccount =
                Account.encodeAccount(Account.NetworkPrefix.MainnetPrefix, accountAddress,
                        subAddress);

        exceptionRule.expect(AddressFormatException.class);
        exceptionRule.expectMessage("Checksum does not validate");
        Account.decodeToAccount(Account.NetworkPrefix.MainnetPrefix,
                encodeAccount.substring(0, encodeAccount.length() - 1));
    }

    @Test
    public void testEncodeDecodeAccountIdentifierString_invalidAccountAddressLength()
    throws LibraSDKException {
        AccountAddress accountAddress = Utils.hexToAddress("f72589b71ff4f8d139674a3f7369c69b");
        Integer[] integers = Utils.byteToUInt8Array(accountAddress.value);

        byte[] data = BechBenny32.convertBits(integers, 0, integers.length, 8, 5, true);
        String bech32Encode = Utils.Bech32Encode(MainnetPrefix.name(), data);

        exceptionRule.expect(AddressFormatException.class);
        exceptionRule.expectMessage("Could not convert bits, invalid padding");
        Account.decodeToAccount(MainnetPrefix, bech32Encode);
    }

}