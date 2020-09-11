// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

import org.junit.Test;

import static org.junit.Assert.assertThrows;

public class LibraLedgerStateTest {

    @Test(expected = Test.None.class)
    public void testHandleLedgerState_validState() throws LibraSDKException {
        LibraLedgerState libraLedgerState = new LibraLedgerState(2);
        libraLedgerState.handleLedgerState(2, 2713598, 1595549134500031L);
        libraLedgerState.handleLedgerState(2, 2713599, 1595549134500032L);
    }

    @Test
    public void testHandleLedgerState_invalidChainId() {
        LibraLedgerState libraLedgerState = new LibraLedgerState(2);
        assertThrows(LibraSDKException.class, () -> libraLedgerState.handleLedgerState(3, 2713598, 1595549134500031L));
    }

    @Test
    public void testHandleLedgerState_invalidVersion() throws LibraSDKException {
        LibraLedgerState libraLedgerState = new LibraLedgerState(2);
        libraLedgerState.handleLedgerState(2, 2713598, 1595549134500031L);
        assertThrows(LibraSDKException.class, () -> libraLedgerState.handleLedgerState(2, 2713597, 1595549134500031L));
    }

    @Test
    public void testHandleLedgerState_invalidTimestampUsecs() throws LibraSDKException {
        LibraLedgerState libraLedgerState = new LibraLedgerState(2);
        libraLedgerState.handleLedgerState(2, 2713598, 1595549134500031L);
        assertThrows(LibraSDKException.class, () -> libraLedgerState.handleLedgerState(2, 2713599
                , 1595549134500030L));
    }

}