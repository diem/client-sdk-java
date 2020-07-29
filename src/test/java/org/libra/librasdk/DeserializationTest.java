// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DeserializationTest {

    @Test
    public void testDeserialization() throws IOException {
        TestData data = TestData.get();
        assertNotNull(data.currencies);
        assertTrue(data.currencies.length > 0);
        assertNotNull(data.root_example);
        assertNotNull(data.child_vasp_account_example);
        assertNotNull(data.local_account);
    }
}
