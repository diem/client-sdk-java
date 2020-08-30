package org.libra.librasdk.libraid;

import org.libra.librasdk.LibraSDKException;
import org.libra.librasdk.Utils;

import java.util.Random;

public class SubAddress {

    public static final int SUB_ADDRESS_LENGTH = 8;
    private byte[] subAddress;

    public SubAddress(byte[] subAddress) {
        this.subAddress = subAddress;
    }

    public SubAddress(String subAddress) throws LibraSDKException {
        byte[] bytes = Utils.hexToBytes(subAddress);
        if (bytes.length != SUB_ADDRESS_LENGTH){
            throw new LibraSDKException(String.format("Sub address should be 8 bytes, but given %d bytes", bytes.length));
        }

        this.subAddress = bytes;
    }

    public static SubAddress generate(){
        byte[] b = new byte[SUB_ADDRESS_LENGTH];
        new Random().nextBytes(b);
        return new SubAddress(b);
    }

    public String toHex(){
        return Utils.bytesToHex(this.subAddress);
    }
}
