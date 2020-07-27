// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra;

import com.novi.serde.Bytes;
import org.libra.types.AccountAddress;
import org.libra.utils.AccountAddressUtils;
import org.libra.utils.HashUtils;
import org.libra.utils.Hex;

import java.util.Arrays;

/**
 * AuthKey provides on-chain account authentication key utility functions.
 */
public class AuthKey {

    private static byte[] ED25519_KEY_SCHEME = new byte[]{0};
    private static byte[] MULTI_ED25519_KEY_SCHEME = new byte[]{1};

    public static AuthKey ed24419(byte[] publicKey) {
        return new AuthKey(HashUtils.hash(publicKey, ED25519_KEY_SCHEME));
    }

    private byte[] bytes;

    public AuthKey(byte[] bytes) throws IllegalArgumentException {
        if (bytes.length != AccountAddressUtils.ACCOUNT_ADDRESS_LENGTH * 2) {
            throw new IllegalArgumentException("invalid authentication key bytes");
        }
        this.bytes = bytes;
    }

    /**
     * Generate account address from authentication key.
     *
     * @return AccountAddress
     */
    public AccountAddress accountAddress() {
        byte[] address = Arrays.copyOfRange(bytes, bytes.length - AccountAddressUtils.ACCOUNT_ADDRESS_LENGTH, bytes.length);
        return AccountAddressUtils.create(address);
    }

    /**
     * Prefix of authentication key is first 16 bytes of the authentication key bytes.
     *
     * @return
     */
    public Bytes prefix() {
        return new Bytes(Arrays.copyOfRange(bytes, 0, bytes.length - AccountAddressUtils.ACCOUNT_ADDRESS_LENGTH));
    }

    /**
     * @return hex-encoded authentication key bytes string.
     */
    public String hex() {
        return Hex.encode(this.bytes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthKey authKey = (AuthKey) o;
        return Arrays.equals(bytes, authKey.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }
}
