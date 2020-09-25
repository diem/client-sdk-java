// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.jsonrpc;

import org.junit.Test;
import org.libra.Constants;

public class ClientTest {

    @Test(expected = IllegalArgumentException.class)
    public void testLibraClient_invalidUrl() {
        new LibraJsonRpcClient("invalidUrl", Constants.MAINNET_CHAIN_ID);
    }
}
