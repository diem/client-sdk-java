// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem.jsonrpc;

import com.diem.Constants;
import com.diem.DiemException;
import org.junit.Test;

import static org.junit.Assert.assertThrows;

public class LedgerStateTest {

    @Test
    public void testHandleLedgerState_validState() throws DiemException {
        LedgerState ledgerState = new LedgerState(Constants.DEVNET_CHAIN_ID);
        ledgerState.save(Constants.DEVNET_CHAIN_ID.value, 2713598, 1595549134500031L);
        ledgerState.save(Constants.DEVNET_CHAIN_ID.value, 2713599, 1595549134500032L);
    }

    @Test
    public void testHandleLedgerState_invalidChainId() {
        LedgerState ledgerState = new LedgerState(Constants.TESTING_CHAIN_ID);
        assertThrows(DiemException.class, () -> ledgerState.save(Constants.MAINNET_CHAIN_ID.value, 2713598, 1595549134500031L));
    }

    @Test
    public void testHandleLedgerState_invalidVersion() throws DiemException {
        LedgerState ledgerState = new LedgerState(Constants.TESTING_CHAIN_ID);
        ledgerState.save(Constants.TESTING_CHAIN_ID.value, 2713598, 1595549134500031L);
        assertThrows(DiemException.class, () -> ledgerState.save(Constants.TESTING_CHAIN_ID.value, 2713597, 1595549134500031L));
    }

    @Test
    public void testHandleLedgerState_invalidTimestampUsecs() throws DiemException {
        LedgerState ledgerState = new LedgerState(Constants.TESTING_CHAIN_ID);
        ledgerState.save(Constants.TESTING_CHAIN_ID.value, 2713598, 1595549134500031L);
        assertThrows(DiemException.class, () -> ledgerState.save(Constants.TESTING_CHAIN_ID.value, 2713599
                , 1595549134500030L));
    }

}