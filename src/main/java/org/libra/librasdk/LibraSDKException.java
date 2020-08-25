// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

/**
 * LibraSDKException is root exception of all exceptions created in this
 * and sub packages.
 */
public class LibraSDKException extends Exception {
    public LibraSDKException(Throwable e) {
        super((e));
    }

    public LibraSDKException(String msg) {
        super(msg);
    }
}
