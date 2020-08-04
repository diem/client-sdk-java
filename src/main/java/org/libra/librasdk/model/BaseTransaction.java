package org.libra.librasdk.model;

import org.libra.librasdk.dto.Transaction;

import java.util.Objects;

public abstract class BaseTransaction {
    Transaction rawTransaction = null;

    public String getType(){
        return rawTransaction.transaction.type;
    };

    public Transaction getRawTransaction(){
        return rawTransaction;
    };

    public void setRawTransaction(Transaction transaction){
        rawTransaction = transaction;
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseTransaction that = (BaseTransaction) o;
        return rawTransaction.equals(that.rawTransaction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawTransaction);
    }
}
