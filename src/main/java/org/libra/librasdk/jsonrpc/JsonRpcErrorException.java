// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk.jsonrpc;

import org.libra.librasdk.LibraSDKException;

/**
 * This exception is threw when JSON-RPC server response error.
 */
public class JsonRpcErrorException extends LibraSDKException {
    private Response resp;

    public JsonRpcErrorException(Response resp) {
        super(resp.error.toString());
        this.resp = resp;
    }

    public Response getResponse() {
        return resp;
    }
}
