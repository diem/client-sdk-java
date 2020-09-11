package org.libra.librasdk;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.libra.types.SignedTransaction;
import org.libra.types.Transaction;

import static org.libra.librasdk.Utils.bytesToHex;

public class TransactionHash {

    public static byte[] hashPrefix(String name) {
        return hash("LIBRA::".getBytes(), name.getBytes());
    }

    public static byte[] hash(byte[] prefix, byte[] bytes) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();

        digestSHA3.update(prefix);
        digestSHA3.update(bytes);

        return digestSHA3.digest();
    }

    public static String hashTransaction(SignedTransaction signedTransaction) throws LibraSDKException {
        try {
            Transaction.UserTransaction userTransaction = new Transaction.UserTransaction(signedTransaction);
            byte[] transactions = hash(hashPrefix("Transaction"), userTransaction.lcsSerialize());
            return bytesToHex(transactions);
        } catch (Exception e) {
            throw new LibraSDKException(e);
        }
    }

}
