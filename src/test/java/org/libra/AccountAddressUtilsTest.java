// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra;

import org.junit.Test;
import org.libra.types.AccountAddress;
import org.libra.utils.AccountAddressUtils;
import org.libra.utils.Hex;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class AccountAddressUtilsTest {
    @Test
    public void testHex() {
        AccountAddress address = AccountAddressUtils.create("f72589b71ff4f8d139674a3f7369c69b");
        assertEquals("f72589b71ff4f8d139674a3f7369c69b", AccountAddressUtils.hex(address).toLowerCase());
    }

    @Test
    public void testBytes() {
        AccountAddress address = AccountAddressUtils.create("f72589b71ff4f8d139674a3f7369c69b");
        byte[] bytes = Hex.decode("f72589b71ff4f8d139674a3f7369c69b");
        assertArrayEquals(bytes, AccountAddressUtils.bytes(address));
    }
}
