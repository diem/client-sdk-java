package org.libra;

import org.apache.commons.lang3.ArrayUtils;
import org.bitcoinj.core.Bech32;
import org.libra.librasdk.LibraSDKException;
import org.libra.librasdk.Utils;
import org.libra.types.AccountAddress;

import java.util.Arrays;
import java.util.Objects;

import static org.libra.SubAddress.SUB_ADDRESS_LENGTH;
import static org.libra.librasdk.Utils.byteToUInt8Array;
import static org.libra.librasdk.Utils.mergeArrays;

public class AccountIdentifier {

    public static final int ACCOUNT_ADDRESS_LENGTH = 16;
    public static final byte V1 = 1;

    private final NetworkPrefix prefix;
    private final byte version;
    private final AccountAddress accountAddress;
    private final SubAddress subAddress;


    public AccountIdentifier(NetworkPrefix prefix, AccountAddress accountAddress, SubAddress subAddress) {
        this.prefix = prefix;
        this.version = V1;
        this.accountAddress = accountAddress;
        this.subAddress = subAddress;
    }

    public AccountIdentifier(NetworkPrefix prefix, AccountAddress accountAddress)
            throws LibraSDKException {
        this.prefix = prefix;
        this.version = V1;
        this.accountAddress = accountAddress;
        this.subAddress = new SubAddress(new byte[SUB_ADDRESS_LENGTH]);
    }

    public static String encodeAccount(NetworkPrefix prefix, AccountAddress accountAddress,
                                       SubAddress subAddress) {
        return new AccountIdentifier(prefix, accountAddress, subAddress).encode();
    }

    public static AccountIdentifier decodeToAccount(NetworkPrefix prefix, String uri)
            throws LibraSDKException {
        Bech32.Bech32Data hrpAndData = Utils.Bech32Decode(uri);
        String hrp = hrpAndData.hrp;

        if (!prefix.value.equals(hrp)) {
            throw new LibraSDKException(String.format("Invalid network prefix : %s != %s", prefix.value, hrp));
        }

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

        return new AccountIdentifier(prefix, accountAddress, subAddress);
    }

    public String encode() {
        Byte[] accountAddressBytes = this.accountAddress.value;
        byte[] subAddressBytes = this.subAddress.getBytes();

        Integer[] accountAddressUInt8 = byteToUInt8Array(accountAddressBytes);
        Integer[] subAddressUInt8 = byteToUInt8Array(ArrayUtils.toObject(subAddressBytes));
        Integer[] program = mergeArrays(accountAddressUInt8, subAddressUInt8);

        // convert to bytes with max size 32
        byte[] data = Utils.convertBits(program, 0, program.length, 8, 5, true);

        Byte[] versionByte = new Byte[]{this.version};
        Byte[] dataAndVersion = mergeArrays(versionByte, ArrayUtils.toObject(data));

        return Utils.Bech32Encode(this.prefix.value, dataAndVersion);
    }

    public NetworkPrefix getPrefix() {
        return prefix;
    }

    public byte getVersion() {
        return version;
    }

    public AccountAddress getAccountAddress() {
        return accountAddress;
    }

    public SubAddress getSubAddress() {
        return subAddress;
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
        AccountIdentifier that = (AccountIdentifier) o;
        return version == that.version && prefix == that.prefix &&
                Arrays.equals(accountAddress.value, that.accountAddress.value) &&
                Arrays.equals(subAddress.getBytes(), that.subAddress.getBytes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, version, accountAddress, subAddress);
    }
}