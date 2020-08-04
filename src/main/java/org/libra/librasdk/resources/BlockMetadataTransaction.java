
package org.libra.librasdk.resources;

public class BlockMetadataTransaction extends BaseTransaction {

    public long getTimestampUsecs() {
        return rawTransaction.transaction.timestamp_usecs;
    }
}
