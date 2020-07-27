// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.jsonrpc;

import org.libra.LibraException;

public class ChainIdMismatchException extends LibraException {
    public ChainIdMismatchException(String msg) {
        super(msg);
    }
}
