// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra;

import com.google.common.primitives.Bytes;
import org.apache.commons.lang3.ArrayUtils;
import org.bitcoinj.core.Bech32;
import org.libra.types.AccountAddress;
import org.libra.utils.AccountAddressUtils;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Objects;

import static org.libra.utils.AccountAddressUtils.ACCOUNT_ADDRESS_LENGTH;
import static org.libra.SubAddress.SUB_ADDRESS_LENGTH;

/**
 * AccountIdentifier implements Libra Account Identifier encoding and decoding.
 * @see <a href="https://github.com/libra/lip/blob/master/lips/lip-5.md">LIP-5 Address formatting</a>
 */
public class AccountIdentifier {

    public static final byte VERSION_1 = 1;

    /**
     * Decode account identifier string
     *
     * @param prefix                   network prefix, this is used for validating given encodedAccountIdentifier network prefix.
     * @param encodedAccountIdentifier encoded account identifier string
     * @return AccountIdentifier
     * @throws IllegalArgumentException if given encoded account identifier is invalid of it's network prefix does not match given network prefix.
     */
    public static AccountIdentifier decode(NetworkPrefix prefix, String encodedAccountIdentifier) throws IllegalArgumentException {
        Bech32.Bech32Data data = Bech32.decode(encodedAccountIdentifier);
        if (!prefix.value.equals(data.hrp)) {
            throw new IllegalArgumentException(String.format("Invalid network prefix : %s != %s", prefix.value, data.hrp));
        }

        byte version = data.data[0];
        if (version != VERSION_1) {
            throw new IllegalArgumentException(String.format("unknown account identifier format version: $d", version));
        }

        byte[] dataNoVersion = Arrays.copyOfRange(data.data, 1, data.data.length);
        byte[] bytes = convertBits(dataNoVersion, 5, 8, false);

        if (bytes.length != ACCOUNT_ADDRESS_LENGTH + SUB_ADDRESS_LENGTH) {
            throw new IllegalArgumentException("invalid account identifier, account address and sub-address length does not match");
        }

        byte[] addressChars = Arrays.copyOfRange(bytes, 0, ACCOUNT_ADDRESS_LENGTH);
        byte[] subAddressChars = Arrays.copyOfRange(bytes, ACCOUNT_ADDRESS_LENGTH, ACCOUNT_ADDRESS_LENGTH + SUB_ADDRESS_LENGTH);

        Byte[] addressBytes = ArrayUtils.toObject(addressChars);
        AccountAddress accountAddress = new AccountAddress(addressBytes);
        SubAddress subAddress = new SubAddress(subAddressChars);

        return new AccountIdentifier(prefix, accountAddress, subAddress);
    }

    /**
     * <p>convertBits is copied from org.bitcoinj.core.SegwitAddress#convertBits with the following modifications:</p>
     *
     * <ul>
     * <li>removed start and length parameters.</li>
     * <li>throw IllegalArgumentException instead of AddressFormatException defined in bitcoinj.</li>
     * </ul>
     *
     * <p>bitcoinj is released under Apache License 2.</p>
     */
    public static byte[] convertBits(final byte[] inputs, final int fromBits, final int toBits, final boolean pad) throws IllegalArgumentException {
        int acc = 0;
        int bits = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream(64);
        final int maxv = (1 << toBits) - 1;
        final int max_acc = (1 << (fromBits + toBits - 1)) - 1;
        for (int i = 0; i < inputs.length; i++) {
            int value = inputs[i] & 0xff;
            if ((value >>> fromBits) != 0) {
                throw new IllegalArgumentException(
                        String.format("Input value '%X' exceeds '%d' bit" + " size", value,
                                fromBits));
            }
            acc = ((acc << fromBits) | value) & max_acc;
            bits += fromBits;
            while (bits >= toBits) {
                bits -= toBits;
                out.write((acc >>> bits) & maxv);
            }
        }
        if (pad) {
            if (bits > 0) out.write((acc << (toBits - bits)) & maxv);
        } else if (bits >= fromBits || ((acc << (toBits - bits)) & maxv) != 0) {
            throw new IllegalArgumentException("Could not convert bits, invalid padding");
        }
        return out.toByteArray();
    }

    /**
     * NetworkPrefix is network prefix of a Libra Account Identifier.
     */
    public enum NetworkPrefix {
        MainnetPrefix("lbr"), TestnetPrefix("tlb");

        private final String value;

        NetworkPrefix(String value) {
            this.value = value;
        }
    }

    private final NetworkPrefix prefix;
    private final AccountAddress accountAddress;
    private final SubAddress subAddress;


    /**
     * Create account address with default empty sub-address(new byte[8])
     */
    public AccountIdentifier(NetworkPrefix prefix, AccountAddress address) {
        this(prefix, address, new SubAddress(new byte[SUB_ADDRESS_LENGTH]));
    }

    /**
     * Create account address with sub-address
     */
    public AccountIdentifier(NetworkPrefix prefix, AccountAddress accountAddress, SubAddress subAddress) {
        this.prefix = prefix;
        this.accountAddress = accountAddress;
        this.subAddress = subAddress;
    }

    /**
     * Encode account identifier by version 1 format
     * @return bech32 encoded account identifier string
     */
    public String encodeV1() {
        byte[] accountAddressBytes = AccountAddressUtils.bytes(this.accountAddress);
        byte[] subAddressBytes = this.subAddress.getBytes();
        byte[] program = Bytes.concat(accountAddressBytes, subAddressBytes);

        byte[] data = AccountIdentifier.convertBits(program, 8, 5, true);

        byte[] versionByte = new byte[]{VERSION_1};
        byte[] versionAndData = Bytes.concat(versionByte, data);

        return Bech32.encode(this.prefix.value, versionAndData);
    }

    public NetworkPrefix getPrefix() {
        return prefix;
    }

    public AccountAddress getAccountAddress() {
        return accountAddress;
    }

    public SubAddress getSubAddress() {
        return subAddress;
    }

    @Override
    public String toString() {
        return "AccountIdentifier{" +
                "prefix=" + prefix +
                ", accountAddress=" + accountAddress +
                ", subAddress=" + subAddress +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountIdentifier that = (AccountIdentifier) o;
        return prefix == that.prefix &&
                Arrays.equals(accountAddress.value, that.accountAddress.value) &&
                Arrays.equals(subAddress.getBytes(), that.subAddress.getBytes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, accountAddress, subAddress);
    }
}