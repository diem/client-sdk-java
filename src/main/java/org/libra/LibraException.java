// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra;

/**
 * LibraException is root exception of all exceptions created in this
 * and sub packages.
 */
public class LibraException extends Exception {
    public LibraException(Throwable e) {
        super(e);
    }

    public LibraException(String msg) {
        super(msg);
    }
}
