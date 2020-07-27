// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.jsonrpc;

import org.libra.LibraException;

import java.io.IOException;

/**
 * RemoteCallException is threw when client sends request failed.
 */
public class RemoteCallException extends LibraException {
    public RemoteCallException(IOException e) {
        super(e);
    }
}
