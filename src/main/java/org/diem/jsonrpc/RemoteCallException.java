// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.diem.jsonrpc;

import org.diem.DiemException;

import java.io.IOException;

/**
 * RemoteCallException is threw when client sends request failed.
 */
public class RemoteCallException extends DiemException {
    public RemoteCallException(IOException e) {
        super(e);
    }
}
