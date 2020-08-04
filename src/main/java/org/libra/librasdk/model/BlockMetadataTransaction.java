
package org.libra.librasdk.model;

public class BlockMetadataTransaction extends BaseTransaction {

    public long getTimestampUsecs() {
        return rawTransaction.transaction.timestamp_usecs;
    }
}
