// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem.jsonrpc;

import com.diem.DiemException;

public class ChainIdMismatchException extends DiemException {
    public ChainIdMismatchException(String msg) {
        super(msg);
    }
}
