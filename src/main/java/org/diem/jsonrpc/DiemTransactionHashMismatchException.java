// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.diem.jsonrpc;

import org.diem.jsonrpctypes.JsonRpc;
import org.diem.DiemException;

public class DiemTransactionHashMismatchException extends DiemException {
    private JsonRpc.Transaction txn;
    private String expectedTransactionHash;

    public DiemTransactionHashMismatchException(JsonRpc.Transaction txn, String expectedTransactionHash) {
        super(String.format("found transaction, but hash does not match, expected %s, but got %s",
                expectedTransactionHash, txn.getHash()));
        this.txn = txn;
        this.expectedTransactionHash = expectedTransactionHash;
    }

    public JsonRpc.Transaction getTransaction() {
        return txn;
    }

    public String getExpectedTransactionHash() {
        return expectedTransactionHash;
    }
}
