package org.libra.librasdk.libraid;

import org.apache.commons.lang3.ArrayUtils;
import org.bitcoinj.core.Bech32;
import org.libra.librasdk.LibraSDKException;
import org.libra.librasdk.Utils;
import org.libra.types.AccountAddress;

import java.util.Arrays;
import java.util.Objects;

import static org.libra.librasdk.Utils.byteToUInt8Array;
import static org.libra.librasdk.Utils.mergeArrays;
import static org.libra.librasdk.libraid.SubAddress.SUB_ADDRESS_LENGTH;

public class Account {

    public static final int ACCOUNT_ADDRESS_LENGTH = 16;
    public static final byte V1 = 1;

    NetworkPrefix prefix;
    byte version;
    AccountAddress accountAddress;
    SubAddress subAddress;


    public Account(NetworkPrefix prefix, AccountAddress accountAddress, SubAddress subAddress) {
        this.prefix = prefix;
        this.version = V1;
        this.accountAddress = accountAddress;
        this.subAddress = subAddress;
    }

    public Account(NetworkPrefix prefix, AccountAddress accountAddress) {
        this.prefix = prefix;
        this.version = V1;
        this.accountAddress = accountAddress;
        this.subAddress = new SubAddress(new byte[SUB_ADDRESS_LENGTH]);
    }

    public static String encodeAccount(NetworkPrefix prefix, AccountAddress accountAddress,
                                       SubAddress subAddress) {
        return new Account(prefix, accountAddress, subAddress).encode();
    }

    public static Account decodeToAccount(NetworkPrefix prefix, String uri)
    throws LibraSDKException {
        Bech32.Bech32Data hrpAndData = Utils.Bech32Decode(uri);

        byte[] dataNoVersion = Arrays.copyOfRange(hrpAndData.data, 1, hrpAndData.data.length);

        Integer[] accountAddressUInt8 = byteToUInt8Array(dataNoVersion);
        byte[] bytes =
                Utils.convertBits(accountAddressUInt8, 0, dataNoVersion.length, 5, 8, false);

        if (bytes.length != ACCOUNT_ADDRESS_LENGTH + SUB_ADDRESS_LENGTH) {
            throw new LibraSDKException("invalid account identifier, account address and " + "sub" +
                    "-address length does not match");
        }

        byte[] addressChars = Arrays.copyOfRange(bytes, 0, ACCOUNT_ADDRESS_LENGTH);
        byte[] subAddressChars = Arrays.copyOfRange(bytes, ACCOUNT_ADDRESS_LENGTH,
                ACCOUNT_ADDRESS_LENGTH + SUB_ADDRESS_LENGTH);

        Byte[] addressBytes = ArrayUtils.toObject(addressChars);
        AccountAddress accountAddress = new AccountAddress(addressBytes);
        SubAddress subAddress = new SubAddress(subAddressChars);

        return new Account(prefix, accountAddress, subAddress);
    }

    public String encode() {
        Byte[] accountAddressBytes = this.accountAddress.value;
        byte[] subAddressBytes = this.subAddress.getSubAddress();

        Integer[] accountAddressUInt8 = byteToUInt8Array(accountAddressBytes);
        Integer[] subAddressUInt8 = byteToUInt8Array(ArrayUtils.toObject(subAddressBytes));
        Integer[] program = mergeArrays(accountAddressUInt8, subAddressUInt8);

        // convert to bytes with max size 32
        byte[] data = Utils.convertBits(program, 0, program.length, 8, 5, true);

        Byte[] versionByte = new Byte[]{this.version};
        Byte[] dataAndVersion = mergeArrays(versionByte, ArrayUtils.toObject(data));

        return Utils.Bech32Encode(this.prefix.value, dataAndVersion);
    }


    enum NetworkPrefix {
        MainnetPrefix("lbr"), TestnetPrefix("tlb");

        private final String value;

        NetworkPrefix(String value) {
            this.value = value;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return version == account.version && prefix == account.prefix &&
                Objects.equals(accountAddress, account.accountAddress) &&
                Objects.equals(subAddress, account.subAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, version, accountAddress, subAddress);
    }

    public boolean isValuesEqual(Account account) {
        return (account.version == this.version && account.prefix == this.prefix &&
                Arrays.equals(account.accountAddress.value, this.accountAddress.value) &&
                Arrays.equals(account.subAddress.getSubAddress(), this.subAddress.getSubAddress()));
    }
}