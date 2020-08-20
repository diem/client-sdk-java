// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk.jsonrpc;

import org.libra.librasdk.LibraSDKException;

import java.io.IOException;

public class UnexpectedResponseException extends LibraSDKException {
    public UnexpectedResponseException(String msg) {
        super(msg);
    }

    public UnexpectedResponseException(IOException e) {
        super(e);
    }
}
