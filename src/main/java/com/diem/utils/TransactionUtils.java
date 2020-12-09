// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem.utils;

import com.diem.Constants;
import com.diem.jsonrpc.JsonRpc;

/**
 * Transaction utility functions.
 */
public class TransactionUtils {

    public static final String VM_STATUS_EXECUTED = Constants.VM_STATUS_EXECUTED;

    /**
     * Check given transaction is executed successfully.
     *
     * @param txn
     * @return true for executed successfully, otherwise failed.
     */
    public static boolean isExecuted(JsonRpc.TransactionOrBuilder txn) {
        return txn.getVmStatus() != null && VM_STATUS_EXECUTED.equalsIgnoreCase(txn.getVmStatus().getType());
    }
}
