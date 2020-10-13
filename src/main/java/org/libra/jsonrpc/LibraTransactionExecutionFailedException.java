// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.jsonrpc;

import org.libra.LibraException;
import org.libra.jsonrpctypes.JsonRpc;

public class LibraTransactionExecutionFailedException extends LibraException {
    private JsonRpc.Transaction txn;

    public LibraTransactionExecutionFailedException(JsonRpc.Transaction txn) {
        super(String.format("transaction execution failed: %s", txn.getVmStatus()));
        this.txn = txn;
    }

    public JsonRpc.Transaction getTransaction() {
        return txn;
    }
}
