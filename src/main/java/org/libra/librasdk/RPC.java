// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import java.util.List;

public interface RPC {
    String call(Method method, List<Object> params) throws JSONRPC2Error, JSONRPC2SessionException;
}
