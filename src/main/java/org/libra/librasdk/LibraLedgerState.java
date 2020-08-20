package org.libra.librasdk;

public class LibraLedgerState {

    Integer chainId;
    Long version;
    Long timestampUsecs;

    public LibraLedgerState(int chainId) {
        this.chainId = chainId;
    }

    private void validateLedgerState(Integer chainId, long version, long timestampUsecs,
                                     Long minimumBlockchainTimestampUsecs) throws LibraSDKException {
        if (chainId != null && this.chainId != chainId) {
            throw new LibraSDKException(String.format("chainId mismatch! Expected: %s Received: " +
                    "%s", this.chainId, chainId));
        }

        if (this.version > version || this.timestampUsecs > timestampUsecs) {
            throw new LibraSDKException(String.format("Current ledger state stale:\n" +
                            "current blockchain version: %s last seen blockchain version: %s " +
                            "current blockchain timestamp usecs: %s last seen blockchain " +
                            "timestamp usecs: %s"
                    , this.version, version, this.timestampUsecs, timestampUsecs));
        }

        if (minimumBlockchainTimestampUsecs != null && this.timestampUsecs > minimumBlockchainTimestampUsecs) {
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

    private boolean isSet() {
        return this.chainId != null && this.timestampUsecs != null && this.version != null;
    }

    public void handleLedgerState(Integer chainId, long version, long timestampUsecs,
                                  Long minimumBlockchainTimestampUsecs) throws LibraSDKException {
        // dont validate on first call
        if (isSet()) {
            validateLedgerState(chainId, version, timestampUsecs, minimumBlockchainTimestampUsecs);
        }
        // will be called only if ledger state validation passed
        updateLedgerState(version, timestampUsecs);
    }

}
