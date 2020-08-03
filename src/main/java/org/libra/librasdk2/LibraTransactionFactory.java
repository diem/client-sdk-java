package org.libra.librasdk2;

import org.libra.librasdk.dto.Transaction;
import org.libra.librasdk2.resources.*;

public class LibraTransactionFactory {


    public static LibraTransaction create(Transaction transaction) {

        String type = transaction.transaction.type;
        if(transaction.transaction.script != null){
            type = transaction.transaction.script.type;
        }


        BaseTransaction baseTransaction = null;
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
            default:
                System.out.println("No such transaction type");
                break;
        }

        baseTransaction.setRawTransaction(transaction);
        libraTransaction.setTransaction(baseTransaction);
        return libraTransaction;
    }
}
