// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

public class LibraLedgerState {
    private static long DEFAULT_DELTA = 30;

    private int chainId;
    private long version;
    private long timestampUsecs;

    public long getTimestampUsecs() {
        return timestampUsecs;
    }

    public LibraLedgerState(int chainId) {
        this.chainId = chainId;
    }

    public synchronized void handleLedgerState(int chainId, long version, long timestampUsecs) throws LibraSDKException {
        if (!validateLedgerState(chainId, version, timestampUsecs)) {
            return;
        }
        // will be called only if ledger state validation passed
        updateLedgerState(version, timestampUsecs);
    }

    private boolean validateLedgerState(int chainId, long version, long timestampUsecs) throws LibraSDKException {
        if (this.chainId != chainId) {
            throw new StaleResponseException(String.format("chainId mismatch! Expected: %s Received: " +
                    "%s", this.chainId, chainId));
        }

        if (this.version > version + DEFAULT_DELTA || this.timestampUsecs > timestampUsecs + DEFAULT_DELTA * 1000) {
            throw new StaleResponseException(String.format("Current ledger state stale:\n" +
                            "current blockchain version: %s last seen blockchain version: %s " +
                            "current blockchain timestamp usecs: %s last seen blockchain " +
                            "timestamp usecs: %s"
                    , this.version, version, this.timestampUsecs, timestampUsecs));
        }
        return this.version > version || this.timestampUsecs > timestampUsecs;
    }

    private void updateLedgerState(long version, long timestampUsecs) {
        this.version = version;
        this.timestampUsecs = timestampUsecs;
    }
}
