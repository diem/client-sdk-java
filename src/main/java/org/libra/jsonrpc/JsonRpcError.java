// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.jsonrpc;

import org.libra.LibraException;

/**
 * This exception is threw when JSON-RPC server response error.
 */
public class JsonRpcError extends LibraException {
    public JsonRpcError(String msg) {
        super(msg);
    }
}
