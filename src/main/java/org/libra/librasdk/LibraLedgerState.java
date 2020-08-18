package org.libra.librasdk;

public class LibraLedgerState {

    int chainId;
    long version;
    long timestampUsecs;

    public LibraLedgerState(int chainId, long version, long timestampUsecs) {
        this.chainId = chainId;
        this.version = version;
        this.timestampUsecs = timestampUsecs;
    }

    private void validateLedgerState(Integer chainId, long version, long timestampUsecs,
                                     Long minimumBlockchainTimestampUsecs) throws LibraSDKException {
        if (chainId != null && this.chainId != chainId) {
            throw new LibraSDKException(String.format("chainId mismatch! Expected: %s Received: " +
                    "%s", this.chainId, chainId));
        }

        if (this.version < version || this.timestampUsecs < timestampUsecs) {
            throw new LibraSDKException(String.format("Current ledger state stale:\n" +
                            "current blockchain version: %s last seen blockchain version: %s " +
                            "current blockchain timestamp usecs: %s last seen blockchain " +
                            "timestamp usecs: %s"
                    , this.version, version, this.timestampUsecs, timestampUsecs));
        }

        if (minimumBlockchainTimestampUsecs != null && this.timestampUsecs < minimumBlockchainTimestampUsecs) {
            throw new LibraSDKException(String.format("Current ledger state stale:\n" +
                            "current blockchain timestamp usecs: %s " +
                            " is less than minimum blockchain timestamp usecs: %s",
                    this.timestampUsecs,
                    minimumBlockchainTimestampUsecs));
        }
    }

    private void updateLedgerState(long version, long timestampUsecs) {
        this.version = version;
        this.timestampUsecs = timestampUsecs;
    }

    public void handleLedgerState(Integer chainId, long version, long timestampUsecs,
                                  Long minimumBlockchainTimestampUsecs) throws LibraSDKException {
        validateLedgerState(chainId, version, timestampUsecs, minimumBlockchainTimestampUsecs);
        // will be called only if ledger state validation passed
        updateLedgerState(version, timestampUsecs);
    }


}
