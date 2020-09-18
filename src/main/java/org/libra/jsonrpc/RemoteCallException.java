// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.jsonrpc;

import org.libra.librasdk.LibraSDKException;

import java.io.IOException;

public class RemoteCallException extends LibraSDKException {
    public RemoteCallException(IOException e) {
        super(e);
    }
}
