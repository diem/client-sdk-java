// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.jsonrpc;

import org.libra.LibraException;

/**
 * InvalidResponseException is threw when server response status code != 200 or the result can't be parsed.
 */
public class InvalidResponseException extends LibraException {
    public InvalidResponseException(int statusCode, String body) {
        super("status code: " + String.valueOf(statusCode) + ", body: " + body);
    }

    public InvalidResponseException(Throwable e) {
        super(e);
    }

    public InvalidResponseException(String msg) {
        super(msg);
    }
}
