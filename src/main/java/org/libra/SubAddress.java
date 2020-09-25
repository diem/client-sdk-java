// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra;

import org.libra.utils.Hex;

import java.util.Arrays;
import java.util.Random;

/**
 * SubAddress is 8 bytes.
 */
public class SubAddress {

    public static final int SUB_ADDRESS_LENGTH = 8;

    public static SubAddress generate() {
        byte[] b = new byte[SUB_ADDRESS_LENGTH];
        new Random().nextBytes(b);
        return new SubAddress(b);
    }

    private final byte[] bytes;

    public SubAddress(byte[] bytes) {
        if (bytes.length != SUB_ADDRESS_LENGTH) {
            throw new IllegalArgumentException(String.format("Sub address should be %d bytes, but given %d bytes", SUB_ADDRESS_LENGTH, bytes.length));
        }
        this.bytes = bytes;
    }

    public SubAddress(String subAddress) {
        this(Hex.decode(subAddress));
    }

    public String toHex() {
        return Hex.encode(this.bytes);
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubAddress that = (SubAddress) o;
        return Arrays.equals(bytes, that.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }
}
