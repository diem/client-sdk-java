package org.libra;

import org.libra.librasdk.Utils;

import java.util.Arrays;
import java.util.Random;

public class SubAddress {

    public static final int SUB_ADDRESS_LENGTH = 8;
    private final byte[] bytes;

    public SubAddress(byte[] bytes) {
        if (bytes.length != SUB_ADDRESS_LENGTH) {
            throw new IllegalArgumentException(String.format("Sub address should be 8 bytes, but given %d bytes", bytes.length));
        }
        this.bytes = bytes;
    }

    public SubAddress(String subAddress) {
        this(Utils.hexToBytes(subAddress));
    }

    public static SubAddress generate() {
        byte[] b = new byte[SUB_ADDRESS_LENGTH];
        new Random().nextBytes(b);
        return new SubAddress(b);
    }

    public String toHex() {
        return Utils.bytesToHex(this.bytes);
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
