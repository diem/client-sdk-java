// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.diem.jsonrpc;

import java.util.concurrent.Callable;

public class Retry<T> {
    private final int maxRetries;
    private final long waitDurationMills;
    private Class<? extends Exception> retryExceptionClass;

    public Retry(int maxRetries, long waitDurationMills, Class<? extends Exception> retryExceptionClass) {
        this.maxRetries = maxRetries;
        this.waitDurationMills = waitDurationMills;
        this.retryExceptionClass = retryExceptionClass;
    }

    public T execute(Callable<T> callable) throws Exception {
        int tries = 0;
        while(true) {
            tries++;
            try {
                return callable.call();
            } catch (Exception e) {
                if (e.getClass().equals(this.retryExceptionClass) && tries < this.maxRetries) {
                    Thread.sleep(this.getWaitDuration(tries));
                } else {
                    throw e;
                }
            }
        }
    }

    private long getWaitDuration(int tries) {
        return this.waitDurationMills * tries;
    }
}
