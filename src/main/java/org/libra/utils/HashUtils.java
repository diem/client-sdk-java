// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.utils;

import com.novi.serde.SerializationError;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.libra.types.RawTransaction;
import org.libra.types.SignedTransaction;
import org.libra.types.Transaction;

import static com.google.common.primitives.Bytes.concat;

public class HashUtils {

    public static byte[] hashPrefix(String name) {
        return hash("LIBRA::".getBytes(), name.getBytes());
    }

    /**
     * @param prefix
     * @param bytes
     * @return sha3 256 hash of given 2 bytes array
     */
    public static byte[] hash(byte[] prefix, byte[] bytes) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();

        digestSHA3.update(prefix);
        digestSHA3.update(bytes);

        return digestSHA3.digest();
    }

    /**
     * Create transaction hash of the given SignedTransaction. This hash is same with the one returned
     * from getTransactions / getAccountTransaction Transaction#hash.
     *
     * @param signedTransaction
     * @return hex encoded Transaction#hash for the SignedTransaction.
     * @link com.libra.jsonrpctypes.JsonRpc.Transaction#getHash
     */
    public static String transactionHash(SignedTransaction signedTransaction) {
        Transaction.UserTransaction userTransaction = new Transaction.UserTransaction(signedTransaction);
        try {
            byte[] transactions = hash(hashPrefix("Transaction"), userTransaction.lcsSerialize());
            return Hex.encode(transactions);
        } catch (SerializationError e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] sha3Hash(byte[] data) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        return digestSHA3.digest(data);
    }

    /**
     * @param txn
     * @return hash bytes of given RawTransaction for creating it's signature.
     */
    public static byte[] signatureMessage(RawTransaction txn) {
        try {
            return concat(sha3Hash("LIBRA::RawTransaction".getBytes()), txn.lcsSerialize());
        } catch (SerializationError e) {
            throw new RuntimeException(e);
        }
    }
}
