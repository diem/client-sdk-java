// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.diem.jsonrpc;

import org.diem.DiemException;

public class DiemTransactionWaitTimeoutException extends DiemException {
    private int timeoutSecond;

    public DiemTransactionWaitTimeoutException(int timeoutSecond) {
        super(String.format("transaction not found within timeout period: %d (seconds)", timeoutSecond));
        this.timeoutSecond = timeoutSecond;
    }

    public int getTimeoutSecond() {
        return timeoutSecond;
    }
}
