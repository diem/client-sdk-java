// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra;

import org.libra.types.AccountAddress;
import org.libra.types.ChainId;
import org.libra.utils.AccountAddressUtils;

public interface Constants {
    AccountAddress ROOT_ACCOUNT_ADDRESS = AccountAddressUtils.create("0000000000000000000000000A550C18");
    AccountAddress CORE_CODE_ADDRESS = AccountAddressUtils.create("00000000000000000000000000000001");

    ChainId MAINNET_CHAIN_ID = new ChainId((byte) 1);
    ChainId DEVNET_CHAIN_ID = new ChainId((byte) 3);
    ChainId TESTING_CHAIN_ID = new ChainId((byte) 4);
}
