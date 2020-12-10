// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem.jsonrpc;

import com.diem.DiemException;

import java.io.IOException;

/**
 * RemoteCallException is threw when client sends request failed.
 */
public class RemoteCallException extends DiemException {
    public RemoteCallException(IOException e) {
        super(e);
    }
}
