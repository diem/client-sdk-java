// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

import com.novi.serde.Bytes;
import com.google.common.io.BaseEncoding;
import org.apache.commons.lang3.ArrayUtils;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Bech32;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.libra.librasdk.dto.LocalAccount;
import org.libra.types.*;

import java.io.ByteArrayOutputStream;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Utils {
    public static SignedTransaction signTransaction(LocalAccount sender, long sequence_number,
                                                    Script script, long
                                                            maxGasAmount, long gasPriceUnit,
                                                    String currencyCode,
                                                    long expirationTimestampSecs,
                                                    byte chainId) throws LibraSDKException {
        RawTransaction rt = createRawTransaction(sender.getAccountAddress(), sequence_number,
                script, maxGasAmount, gasPriceUnit, currencyCode, expirationTimestampSecs, chainId);

        byte[] hash = hashRawTransaction(rt);
        byte[] sign = sign(sender.getPrivateKey(), hash);
        SignedTransaction st = new SignedTransaction(rt,
                new TransactionAuthenticator.Ed25519(new Ed25519PublicKey(new Bytes(hexToBytes(sender.public_key))), new Ed25519Signature(new Bytes(sign))));
        return st;
    }

    public static RawTransaction createRawTransaction(AccountAddress senderAddress,
                                                      long sequence_number, Script script,
                                                      long maxGasAmount, long gasPriceUnit,
                                                      String currencyCode,
                                                      long expirationTimestampSecs, byte chainId) {
        return new RawTransaction(senderAddress, sequence_number,
                new TransactionPayload.Script(script), maxGasAmount, gasPriceUnit, currencyCode,
                expirationTimestampSecs, new ChainId(chainId));
    }

    private static void verify(String publicKey, byte[] sig) {
        byte[] privateKeyBytes = hexToBytes(publicKey);
        Ed25519PublicKeyParameters key = new Ed25519PublicKeyParameters(privateKeyBytes, 0);
        Ed25519Signer signer = new Ed25519Signer();
        signer.init(false, key);

        if (!signer.verifySignature(sig)) {
            throw new RuntimeException("invalid");
        }
    }

    public static byte[] sha3Hash(byte[] data) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        return digestSHA3.digest(data);
    }

    public static byte[] createAuthKeyFromEd25519PublicKey(byte[] data) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        byte[] singleSign = {(byte) 0};

        digestSHA3.update(data);
        digestSHA3.update(singleSign);

        return digestSHA3.digest();
    }

    private static byte[] concat(byte[] part1, byte[] part2) {
        byte[] ret = new byte[part1.length + part2.length];
        System.arraycopy(part1, 0, ret, 0, part1.length);
        System.arraycopy(part2, 0, ret, part1.length, part2.length);
        return ret;
    }

    public static byte[] sign(Ed25519PrivateKeyParameters privateKey, byte[] data) {
        Ed25519Signer signer = new Ed25519Signer();
        signer.init(true, privateKey);

        signer.update(data, 0, data.length);
        return signer.generateSignature();
    }

    public static Ed25519PrivateKeyParameters hexToKey(String hex) {
        byte[] privateKeyBytes = hexToBytes(hex);
        return new Ed25519PrivateKeyParameters(privateKeyBytes, 0);
    }

    public static AccountAddress hexToAddress(String hex) {
        return bytesToAddress(hexToBytes(hex));
    }

    static AccountAddress bytesToAddress(byte[] values) {
        assert values.length == 16;
        Byte[] address = new Byte[16];
        for (int i = 0; i < 16; i++) {
            address[i] = Byte.valueOf(values[i]);
        }
        return new AccountAddress(address);
    }

    public static byte[] hexToBytes(String hex) {
        return BaseEncoding.base16().decode(hex.toUpperCase());
    }

    public static String bytesToHex(byte[] bytes) {
        return BaseEncoding.base16().encode(bytes);
    }

    public static String bytesToHex(Bytes bytes) {
        return bytesToHex(bytes.content());
    }

    public static String toLCSHex(SignedTransaction st) throws LibraSDKException {
        try {
            return bytesToHex(st.lcsSerialize());
        } catch (Exception e) {
            throw new LibraSDKException(e);
        }
    }

    public static String addressToHex(AccountAddress address) {
        byte[] bytes = new byte[16];
        for (int i = 0; i < 16; i++) {
            bytes[i] = Byte.valueOf(address.value[i]);
        }
        return bytesToHex(bytes);
    }

    public static TypeTag createCurrencyCodeTypeTag(String currencyCode) {
        return new TypeTag.Struct(createCurrencyCodeStructTAg(currencyCode));
    }

    public static StructTag createCurrencyCodeStructTAg(String currencyCode) {
        return new StructTag(Utils.hexToAddress(Constants.CORE_CODE_ADDRESS),
                new Identifier(currencyCode), new Identifier(currencyCode), new ArrayList<>());
    }

    public static byte[] hashRawTransaction(RawTransaction txn) throws LibraSDKException {
        try {
            return concat(sha3Hash("LIBRA::RawTransaction".getBytes()), txn.lcsSerialize());
        } catch (Exception e) {
            throw new LibraSDKException(e);
        }
    }

    public static String Bech32Encode(String humanReadablePart, Byte[] data) {
        return Bech32.encode(humanReadablePart, ArrayUtils.toPrimitive(data));
    }

    public static String Bech32Encode(String humanReadablePart, byte[] data) {
        return Bech32.encode(humanReadablePart, data);
    }

    public static Bech32.Bech32Data Bech32Decode(String data) {
        Bech32.Bech32Data decode = Bech32.decode(data);
        return decode;
    }

    public static LocalAccount generateLocalAccount() {
        Ed25519PrivateKeyParameters privateK = new Ed25519PrivateKeyParameters(new SecureRandom());
        return generateLocalAccountInner(privateK);
    }

    public static LocalAccount generateLocalAccountFromPrivateKey(String privateKey) {
        byte[] privateKeyBytes = hexToBytes(privateKey);

        Ed25519PrivateKeyParameters privateKeyParameters =
                new Ed25519PrivateKeyParameters(privateKeyBytes, 0);
        return generateLocalAccountInner(privateKeyParameters);
    }

    private static LocalAccount generateLocalAccountInner(Ed25519PrivateKeyParameters privateKeyParameters) {
        Ed25519PublicKeyParameters ed25519PublicKeyParameters =
                privateKeyParameters.generatePublicKey();
        byte[] privateKeyBytes = privateKeyParameters.getEncoded();
        String privateKey = bytesToHex(privateKeyBytes);

        byte[] publicKeyBytes = ed25519PublicKeyParameters.getEncoded();
        String publicKey = bytesToHex(publicKeyBytes);

        byte[] authKeyBytes = createAuthKeyFromEd25519PublicKey(publicKeyBytes);
        byte[] accountAddressBytes = Arrays.copyOfRange(authKeyBytes, authKeyBytes.length - 16,
                authKeyBytes.length);

        String authKey = bytesToHex(authKeyBytes);
        String accountAddress = bytesToHex(accountAddressBytes);

        return new LocalAccount(accountAddress, authKey, privateKey, publicKey);
    }

    public static Integer[] byteToUInt8Array(Byte[] bytes) {
        Integer[] uInt8 = new Integer[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            uInt8[i] = byteToUInt8(bytes[i]);
        }

        return uInt8;
    }

    public static Integer[] byteToUInt8Array(byte[] bytes) {
        Integer[] uInt8 = new Integer[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            uInt8[i] = byteToUInt8(bytes[i]);
        }

        return uInt8;
    }

    public static int intToUInt8(int i) {
        return i & 0xFF;
    }

    public static int byteToUInt8(Byte aByte) {
        return aByte & 0xFF;
    }

    public static byte byteToUInt8Byte(Byte aByte) {
        return (byte) (aByte & 0xFF);
    }

    public static Integer[] mergeArrays(Integer[]... arrays) {
        return Stream.of(arrays).flatMap(Stream::of).toArray(Integer[]::new);
    }

    public static Byte[] mergeArrays(Byte[]... arrays) {
        return Stream.of(arrays).flatMap(Stream::of).toArray(Byte[]::new);
    }

    public static int[] mergeArrays(int[]... arrays) {
        return Stream.of(arrays).flatMapToInt(IntStream::of).toArray();

    }
    public static byte[] convertBits(final Integer[] in, final int inStart, final int inLen,
                                     final int fromBits, final int toBits, final boolean pad) throws
            AddressFormatException {
        int acc = 0;
        int bits = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream(64);
        final int maxv = (1 << toBits) - 1;
        final int max_acc = (1 << (fromBits + toBits - 1)) - 1;
        for (int i = 0; i < inLen; i++) {
            int value = in[i + inStart] & 0xff;
            if ((value >>> fromBits) != 0) {
                throw new AddressFormatException(String.format("Input value '%X' exceeds '%d' bit" +
                        " size", value, fromBits));
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
            throw new AddressFormatException("Could not convert bits, invalid padding");
        }
        return out.toByteArray();
    }
}
