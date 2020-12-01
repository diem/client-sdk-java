// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.diem.jsonrpc;

import org.diem.DiemException;

public class ChainIdMismatchException extends DiemException {
    public ChainIdMismatchException(String msg) {
        super(msg);
    }
}
