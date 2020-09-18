// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.jsonrpc;

import org.libra.librasdk.LibraSDKException;

/**
 * This exception is threw when JSON-RPC server response error.
 */
public class JsonRpcError extends LibraSDKException {
    public JsonRpcError(String msg) {
        super(msg);
    }
}
