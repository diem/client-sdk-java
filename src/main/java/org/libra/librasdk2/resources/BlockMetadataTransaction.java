
package org.libra.librasdk2.resources;

public class BlockMetadataTransaction extends BaseTransaction {

    public long getTimestampUsecs() {
        return rawTransaction.transaction.timestamp_usecs;
    }
}
