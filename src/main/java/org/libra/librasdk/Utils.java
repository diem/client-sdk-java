// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

import com.novi.lcs.LcsSerializer;
import com.novi.serde.Bytes;
import com.novi.serde.Serializer;
import com.google.common.io.BaseEncoding;
import design.contract.bech32.Bech32;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.libra.librasdk.dto.LocalAccount;
import org.libra.types.*;

import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Utils {
    public static SignedTransaction signTransaction(LocalAccount sender, long sequence_number,
                                                    Script script, long
                                                            maxGasAmount, long gasPriceUnit,
                                                    String currencyCode,
                                                    long expirationTimestampSecs,
                                                    byte chainId) throws Exception {
        RawTransaction rt = createRawTransaction(sender.getAccountAddress(), sequence_number,
                script, maxGasAmount, gasPriceUnit, currencyCode, expirationTimestampSecs, chainId);

        byte[] hash = hashRawTransaction(rt);
        byte[] sign = sign(sender.getPrivateKey(), hash);
        SignedTransaction st = new SignedTransaction(rt, new TransactionAuthenticator.Ed25519(
                new Ed25519PublicKey(new Bytes(hexToBytes(sender.public_key))),
                new Ed25519Signature(new Bytes(sign))
        ));
        return st;
    }

    public static RawTransaction createRawTransaction(AccountAddress senderAddress,
                                                      long sequence_number, Script script,
                                                      long maxGasAmount, long gasPriceUnit,
                                                      String currencyCode,
                                                      long expirationTimestampSecs, byte chainId) {
        return new RawTransaction(senderAddress, sequence_number,
                new TransactionPayload.Script(script),
                maxGasAmount, gasPriceUnit, currencyCode,
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

    public static byte[] sign(Ed25519PrivateKeyParameters privateKey, byte[] data) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
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

    public static String toLCSHex(SignedTransaction st) throws Exception {
        return bytesToHex(st.lcsSerialize());
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
        return new StructTag(
                Utils.hexToAddress(Constants.CORE_CODE_ADDRESS),
                new Identifier(currencyCode),
                new Identifier(currencyCode),
                new ArrayList<>()
        );
    }

    public static byte[] hashRawTransaction(RawTransaction txn) throws Exception {
        return concat(sha3Hash("LIBRA::RawTransaction".getBytes()), txn.lcsSerialize());
    }

    public static String Bech32Encode(String humanReadablePart, char[] data) {
        return Bech32.encode(humanReadablePart, data);
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
}
