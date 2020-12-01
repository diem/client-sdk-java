// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.diem.jsonrpc;

import org.diem.DiemException;

/**
 * This exception is threw when JSON-RPC server response error.
 */
public class JsonRpcError extends DiemException {
    public JsonRpcError(String msg) {
        super(msg);
    }
}
