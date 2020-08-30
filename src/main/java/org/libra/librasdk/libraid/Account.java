package org.libra.librasdk.libraid;

import design.contract.bech32.HrpAndDp;
import org.apache.commons.lang3.ArrayUtils;
import org.libra.librasdk.LibraSDKException;
import org.libra.librasdk.Utils;
import org.libra.types.AccountAddress;

import java.util.Arrays;

import static org.libra.librasdk.libraid.SubAddress.SUB_ADDRESS_LENGTH;

public class Account {

    public static final int ACCOUNT_ADDRESS_LENGTH = 16;
    public static final byte V1 = 1;

    NetworkPrefix prefix;
    byte version;
    AccountAddress accountAddress;
    SubAddress subAddress;


    public Account(NetworkPrefix prefix, AccountAddress accountAddress,
                   SubAddress subAddress) {
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

    public Account() {
        this.version = V1;
    }

    public static String encodeAccount(NetworkPrefix prefix, AccountAddress accountAddress,
                                       SubAddress subAddress) {
        return new Account(prefix, accountAddress, subAddress).encode();
    }

    public static Account decodeToAccount(NetworkPrefix prefix, String uri) throws LibraSDKException {
        HrpAndDp hrpAndDp = Utils.Bech32Decode(uri);
        String hrp = hrpAndDp.getHrp();

        if(!hrp.equals(prefix.name())){
            throw new LibraSDKException("FIXME");
        }

        char[] dp = hrpAndDp.getDp();

        if (dp.length != ACCOUNT_ADDRESS_LENGTH + SUB_ADDRESS_LENGTH) {
            throw new LibraSDKException("invalid account identifier, account address and sub-address length does not match");
        }

        char[] addressChars = Arrays.copyOfRange(dp, 0, ACCOUNT_ADDRESS_LENGTH);
        char[] subAddressChars = Arrays.copyOfRange(dp, ACCOUNT_ADDRESS_LENGTH,
                ACCOUNT_ADDRESS_LENGTH + SUB_ADDRESS_LENGTH);

        Byte[] addressBytes = ArrayUtils.toObject(new String(addressChars).getBytes());
        AccountAddress accountAddress = new AccountAddress(addressBytes);
        SubAddress subAddress = new SubAddress(new String(subAddressChars).getBytes());

        return new Account(prefix, accountAddress, subAddress);
    }

    public String encode() {
        byte[] accountAddressBytes = ArrayUtils.toPrimitive(this.accountAddress.value);
        byte[] subAddressBytes = this.subAddress.getSubAddress();

        char[] accountAddressChars = new String(accountAddressBytes).toCharArray();
        char[] subAddressChars = new String(subAddressBytes).toCharArray();
        char[] chars = ArrayUtils.addAll(accountAddressChars, subAddressChars);
        return Utils.Bech32Encode(this.prefix.name(), chars);
    }

    enum NetworkPrefix {
        MainnetPrefix("lbr"),
        TestnetPrefix("tlb");

        private final String prefix;

        NetworkPrefix(String prefix) {
            this.prefix = prefix;
        }
    }


}
