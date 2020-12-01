// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.diem.jsonrpc;

import org.diem.jsonrpc.DiemJsonRpcClient;
import org.junit.Test;
import org.diem.Constants;

public class ClientTest {

    @Test(expected = IllegalArgumentException.class)
    public void testDiemClient_invalidUrl() {
        new DiemJsonRpcClient("invalidUrl", Constants.MAINNET_CHAIN_ID);
    }
}
