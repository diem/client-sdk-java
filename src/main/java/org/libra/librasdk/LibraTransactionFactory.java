package org.libra.librasdk;

import org.libra.librasdk.dto.Transaction;
import org.libra.librasdk.resources.*;

public class LibraTransactionFactory {

    public static LibraTransaction create(Transaction transaction) throws Exception {

        String type = transaction.transaction.type;
        if(transaction.transaction.script != null){
            type = transaction.transaction.script.type;
        }


        BaseTransaction baseTransaction;
        LibraTransaction libraTransaction = new LibraTransaction();

        switch (type) {
            case "peer_to_peer_transaction":
                baseTransaction = new PeerToPeerTransaction();
                break;
            case "blockmetadata":
                baseTransaction = new BlockMetadataTransaction();
                break;
            case "writeset":
                baseTransaction = new WritesetTransaction();
                break;
            case "unknown_transaction":
                baseTransaction = new BaseTransaction() {
                };
                break;
            default:
                throw new Exception("No such transaction type");
        }

        baseTransaction.setRawTransaction(transaction);
        libraTransaction.setTransaction(baseTransaction);
        return libraTransaction;
    }
}
