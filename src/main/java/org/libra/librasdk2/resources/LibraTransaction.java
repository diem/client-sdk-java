
package org.libra.librasdk2.resources;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.libra.librasdk.dto.Event;
import org.libra.librasdk.dto.Transaction;

import java.util.Objects;

public class LibraTransaction {
    private BaseTransaction transaction;
    private Transaction rawTransaction;

    public LibraTransaction(Transaction rawTransaction) {
        this.rawTransaction = rawTransaction;
    }

    public Event[] getEvents() {
        return rawTransaction.events;
    }

    public long getGasUsed() {
        return rawTransaction.gas_used;
    }

    public String getHash() {
        return rawTransaction.hash;
    }

    public BaseTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(BaseTransaction transaction) {
        this.transaction = transaction;
    }

    public long getVersion() {
        return rawTransaction.version;
    }

    public String getVmStatus() {
        return rawTransaction.vmStatus();
    }

    public Transaction getRawTransaction() {
        return rawTransaction;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibraTransaction that = (LibraTransaction) o;
        return transaction.equals(that.transaction) &&
                rawTransaction.equals(that.rawTransaction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transaction, rawTransaction);
    }
}
