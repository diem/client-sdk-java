// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.jsonrpc;

import org.libra.LibraException;

public class LibraTransactionWaitTimeoutException extends LibraException {
    private int timeoutSecond;

    public LibraTransactionWaitTimeoutException(int timeoutSecond) {
        super(String.format("transaction not found within timeout period: %d (seconds)", timeoutSecond));
        this.timeoutSecond = timeoutSecond;
    }

    public int getTimeoutSecond() {
        return timeoutSecond;
    }
}
