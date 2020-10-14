// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.jsonrpc;

import org.libra.LibraException;
import org.libra.jsonrpctypes.JsonRpc;

public class LibraTransactionHashMismatchException extends LibraException {
    private JsonRpc.Transaction txn;
    private String expectedTransactionHash;

    public LibraTransactionHashMismatchException(JsonRpc.Transaction txn, String expectedTransactionHash) {
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
