package org.libra.librasdk.libraid;

import org.apache.commons.lang3.ArrayUtils;
import org.bitcoinj.core.Bech32;
import org.libra.librasdk.BechBenny32;
import org.libra.librasdk.LibraSDKException;
import org.libra.librasdk.Utils;
import org.libra.types.AccountAddress;

import java.util.Arrays;

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

    public static Account decodeToAccount(NetworkPrefix prefix, String uri) throws LibraSDKException {
        Bech32.Bech32Data hrpAndDp = Utils.Bech32Decode(uri);
        String hrp = hrpAndDp.hrp;

        if (!hrp.equals(prefix.value)) {
            throw new LibraSDKException("FIXME");
        }

        byte[] dp = hrpAndDp.data;

        if (dp.length != ACCOUNT_ADDRESS_LENGTH + SUB_ADDRESS_LENGTH) {
            throw new LibraSDKException("invalid account identifier, account address and " + "sub"
                    + "-address length does not match");
        }

        byte[] addressChars = Arrays.copyOfRange(dp, 0, ACCOUNT_ADDRESS_LENGTH);
        byte[] subAddressChars = Arrays.copyOfRange(dp, ACCOUNT_ADDRESS_LENGTH,
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
        byte[] data = BechBenny32.convertBits(program, 0, program.length, 8, 5, true);

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


}
