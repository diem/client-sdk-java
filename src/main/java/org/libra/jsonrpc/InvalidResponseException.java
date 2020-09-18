// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.jsonrpc;

import org.libra.librasdk.LibraSDKException;

import java.io.IOException;

public class InvalidResponseException extends LibraSDKException {
    public InvalidResponseException(int statusCode, String body) {
        super("status code: " + String.valueOf(statusCode) + ", body: " + body);
    }
}
