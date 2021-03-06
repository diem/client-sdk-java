// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem.jsonrpc;

import com.diem.types.ChainId;

/**
 * LedgerState records server respond ledger info version, timestamp and chain id for validating stale response.
 */
public class LedgerState {

    private ChainId chainId;
    private long version;
    private long timestampUsecs;

    public LedgerState(ChainId chainId) {
        this.chainId = chainId;
    }

    public LedgerState(ChainId chainId, long version, long timestampUsecs) {
        this.chainId = chainId;
        this.version = version;
        this.timestampUsecs = timestampUsecs;
    }

    public synchronized ChainId getChainId() {
        return chainId;
    }

    public synchronized long getVersion() {
        return version;
    }

    /**
     * @return timestamp in micro-seconds
     */
    public synchronized long getTimestampUsecs() {
        return this.timestampUsecs;
    }

    /**
     * Save new ledger version and timestamp.
     * It validates chain id and checks whether given version and timestamp is too old.
     *
     * @param chainId
     * @param version
     * @param timestampUsecs
     * @throws StaleResponseException   if given version and timestamp is too old (default is 30 versions or 30 seconds).
     * @throws ChainIdMismatchException if chain id is not match the chain id initialized.
     */
    public synchronized void save(byte chainId, long version, long timestampUsecs) throws StaleResponseException, ChainIdMismatchException {
        if (!validateLedgerState(chainId, version, timestampUsecs)) {
            return;
        }
        // will be called only if ledger state validation passed
        updateLedgerState(version, timestampUsecs);
    }

    private boolean validateLedgerState(byte chainId, long version, long timestampUsecs) throws StaleResponseException, ChainIdMismatchException {
        if (this.chainId.value.byteValue() != chainId) {
            throw new ChainIdMismatchException(String.format("chainId mismatch! Expected: %s Received: " +
                    "%s", this.chainId, chainId));
        }

        if (this.version > version || this.timestampUsecs > timestampUsecs) {
            throw new StaleResponseException(String.format("Current ledger state stale:\n" +
                            "current blockchain version: %s last seen blockchain version: %s " +
                            "current blockchain timestamp usecs: %s last seen blockchain " +
                            "timestamp usecs: %s"
                    , this.version, version, this.timestampUsecs, timestampUsecs));
        }
        return this.version < version || this.timestampUsecs < timestampUsecs;
    }

    private void updateLedgerState(long version, long timestampUsecs) {
        this.version = version;
        this.timestampUsecs = timestampUsecs;
    }

}
