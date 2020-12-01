// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.diem.jsonrpc;

import org.diem.jsonrpctypes.JsonRpc;
import org.diem.DiemException;

public class DiemTransactionExecutionFailedException extends DiemException {
    private JsonRpc.Transaction txn;

    public DiemTransactionExecutionFailedException(JsonRpc.Transaction txn) {
        super(String.format("transaction execution failed: %s", txn.getVmStatus()));
        this.txn = txn;
    }

    public JsonRpc.Transaction getTransaction() {
        return txn;
    }
}
