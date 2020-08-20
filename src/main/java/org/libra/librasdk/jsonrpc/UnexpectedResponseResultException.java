// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk.jsonrpc;

import com.google.gson.JsonSyntaxException;
import org.libra.librasdk.LibraSDKException;

public class UnexpectedResponseResultException extends LibraSDKException {
    public UnexpectedResponseResultException(JsonSyntaxException e) {
        super(e);
    }
}
