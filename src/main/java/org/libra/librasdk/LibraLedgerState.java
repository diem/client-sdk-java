// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

public class LibraLedgerState {

    int chainId;
    long version;
    long timestampUsecs;

    public long getTimestampUsecs() {
        return timestampUsecs;
    }

    public LibraLedgerState(int chainId) {
        this.chainId = chainId;
    }

    private void validateLedgerState(int chainId, long version, long timestampUsecs) throws LibraSDKException {
        if (this.chainId != chainId) {
            throw new StaleResponseException(String.format("chainId mismatch! Expected: %s Received: " +
                    "%s", this.chainId, chainId));
        }

        if (this.version > version || this.timestampUsecs > timestampUsecs) {
            throw new StaleResponseException(String.format("Current ledger state stale:\n" +
                            "current blockchain version: %s last seen blockchain version: %s " +
                            "current blockchain timestamp usecs: %s last seen blockchain " +
                            "timestamp usecs: %s"
                    , this.version, version, this.timestampUsecs, timestampUsecs));
        }
    }

    private void updateLedgerState(long version, long timestampUsecs) {
        this.version = version;
        this.timestampUsecs = timestampUsecs;
    }

    private boolean isSet() {
        return this.chainId > 0;
    }

    public void handleLedgerState(int chainId, long version, long timestampUsecs) throws LibraSDKException {
        // dont validate on first call
        if (isSet()) {
            validateLedgerState(chainId, version, timestampUsecs);
        }
        // will be called only if ledger state validation passed
        updateLedgerState(version, timestampUsecs);
    }

}
