package org.libra;

import org.bitcoinj.core.AddressFormatException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.libra.librasdk.LibraSDKException;
import org.libra.librasdk.Utils;
import org.libra.types.AccountAddress;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.libra.AccountIdentifier.NetworkPrefix.MainnetPrefix;
import static org.libra.AccountIdentifier.decodeToAccount;
import static org.libra.AccountIdentifier.encodeAccount;

public class AccountIdentifierTest {
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
                encodeAccount(AccountIdentifier.NetworkPrefix.MainnetPrefix, accountAddress,
                        subAddress);

        assertEquals("lbr1p7ujcndcl7nudzwt8fglhx6wxn08kgs5tm6mz4usw5p72t", encodeAccount);

        AccountIdentifier accountIdentifier =
                decodeToAccount(AccountIdentifier.NetworkPrefix.MainnetPrefix, encodeAccount);

        assertArrayEquals(accountIdentifier.getAccountAddress().value, accountAddress.value);
        assertArrayEquals(accountIdentifier.getSubAddress().getBytes(), subAddress.getBytes());
        assertEquals(accountIdentifier.getPrefix(), AccountIdentifier.NetworkPrefix.MainnetPrefix);
        assertEquals(accountIdentifier.getVersion(), 1);
    }

    @Test
    public void testEncodeDecodeAccountIdentifierWithoutSubAddress_success()
    throws LibraSDKException {
        accountAddress = Utils.hexToAddress("f72589b71ff4f8d139674a3f7369c69b");
        byte[] subAddress = {0, 0, 0, 0, 0, 0, 0, 0};
        String encodeAccount =
                encodeAccount(AccountIdentifier.NetworkPrefix.MainnetPrefix, accountAddress,
                        new SubAddress(subAddress));

        assertEquals("lbr1p7ujcndcl7nudzwt8fglhx6wxnvqqqqqqqqqqqqqflf8ma", encodeAccount);

        AccountIdentifier accountIdentifier =
                decodeToAccount(AccountIdentifier.NetworkPrefix.MainnetPrefix, encodeAccount);

        assertArrayEquals(accountIdentifier.getAccountAddress().value, accountAddress.value);
        assertArrayEquals(accountIdentifier.getSubAddress().getBytes(), subAddress);
        assertEquals(accountIdentifier.getPrefix(), AccountIdentifier.NetworkPrefix.MainnetPrefix);
        assertEquals(accountIdentifier.getVersion(), 1);
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testEncodeDecodeAccountIdentifierString_invalidChecksum() throws LibraSDKException {
        accountAddress = Utils.hexToAddress("f72589b71ff4f8d139674a3f7369c69b");
        SubAddress subAddress = new SubAddress("cf64428bdeb62af2");

        String encodeAccount =
                encodeAccount(AccountIdentifier.NetworkPrefix.MainnetPrefix, accountAddress,
                        subAddress);

        exceptionRule.expect(AddressFormatException.class);
        exceptionRule.expectMessage("Checksum does not validate");
        decodeToAccount(AccountIdentifier.NetworkPrefix.MainnetPrefix,
                encodeAccount.substring(0, encodeAccount.length() - 1));
    }

    @Test
    public void testEncodeDecodeAccountIdentifierString_invalidAccountAddressLength()
    throws LibraSDKException {
        accountAddress = Utils.hexToAddress("f72589b71ff4f8d139674a3f7369c69b");
        Integer[] integers = Utils.byteToUInt8Array(accountAddress.value);

        byte[] data = Utils.convertBits(integers, 0, integers.length, 8, 5, true);
        String bech32Encode = Utils.Bech32Encode(MainnetPrefix.name(), data);

        exceptionRule.expect(LibraSDKException.class);
        exceptionRule.expectMessage("Invalid network prefix");
        decodeToAccount(MainnetPrefix, bech32Encode);
    }

}