package org.libra.librasdk.libraid;

import org.libra.librasdk.Utils;
import org.libra.types.AccountAddress;

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
        this.subAddress = new SubAddress(new byte[SubAddress.SUB_ADDRESS_LENGTH]);
    }

    public Account() {
        this.version = V1;
    }

    public static String encodeAccount(NetworkPrefix prefix, AccountAddress accountAddress,
                                       SubAddress subAddress) {
        return new Account(prefix, accountAddress, subAddress).encode();
    }

    public static Account decodeToAccount(String uri) {

        return new Account();
    }

    public String encode() {
        return Utils.Bech32Encode("", new char[]{});
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
