// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.utils;

import org.libra.types.AccountAddress;

public class AccountAddressUtils {
    public static int ACCOUNT_ADDRESS_LENGTH = 16;

    /**
     * Create AccountAddress from given bytes.
     *
     * @param bytes address bytes
     * @return AccountAddress
     * @throws IllegalArgumentException if given bytes array length is not equal to `ACCOUNT_ADDRESS_LENGTH`
     */
    public static AccountAddress create(byte[] bytes) {
        if (bytes.length != ACCOUNT_ADDRESS_LENGTH) {
            throw new IllegalArgumentException(String.format("account address bytes length must be {}", ACCOUNT_ADDRESS_LENGTH));
        }
        Byte[] address = new Byte[ACCOUNT_ADDRESS_LENGTH];
        for (int i = 0; i < ACCOUNT_ADDRESS_LENGTH; i++) {
            address[i] = Byte.valueOf(bytes[i]);
        }
        return new AccountAddress(address);
    }

    /**
     * Create AccountAddress from given hex-encoded bytes string
     *
     * @param address hex-encoded bytes string
     * @return AccountAddress
     */
    public static AccountAddress create(String address) {
        return create(Hex.decode(address));
    }

    /**
     * Convert given AccountAddress into hex-encoded bytes string.
     *
     * @param address
     * @return hex-encoded bytes string
     */
    public static String hex(AccountAddress address) {
        return Hex.encode(address.value);
    }

    /**
     * @param address
     * @return byte array of the address
     */
    public static byte[] bytes(AccountAddress address) {
        byte[] ret = new byte[ACCOUNT_ADDRESS_LENGTH];
        for (int i = 0; i < ACCOUNT_ADDRESS_LENGTH; i++) {
            ret[i] = address.value[i];
        }
        return ret;
    }
}
