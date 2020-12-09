// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem.jsonrpc;

import com.diem.Constants;
import org.junit.Test;

public class ClientTest {

    @Test(expected = IllegalArgumentException.class)
    public void testDiemClient_invalidUrl() {
        new DiemJsonRpcClient("invalidUrl", Constants.MAINNET_CHAIN_ID);
    }
}
