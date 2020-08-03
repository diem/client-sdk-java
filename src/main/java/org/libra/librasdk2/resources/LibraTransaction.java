
package org.libra.librasdk2.resources;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.libra.librasdk.dto.Event;
import org.libra.librasdk.dto.Transaction;

public class LibraTransaction {
    private BaseTransaction transaction;

    public Event[] getEvents() {
        return transaction.rawTransaction.events;
    }

    public long getGasUsed() {
        return transaction.rawTransaction.gas_used;
    }

    public String getHash() {
        return transaction.rawTransaction.hash;
    }

    public BaseTransaction getBaseTransaction() {
        return transaction;
    }

    public void setTransaction(BaseTransaction transaction) {
        this.transaction = transaction;
    }

    public long getVersion() {
        return transaction.rawTransaction.version;
    }

    public String getVmStatus() {
        return transaction.rawTransaction.vmStatus();
    }

    public Transaction getRawTransaction() {
        return transaction.rawTransaction;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
