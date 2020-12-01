// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.diem;

import org.diem.types.AccountAddress;
import org.diem.utils.AccountAddressUtils;
import org.diem.types.ChainId;

/**
 * Defines constants for static data and enum type values.
 *
 * See the following Libra JSON-RPC response type documents for more details of enum type values:
 * <a href="https://github.com/libra/libra/blob/master/json-rpc/docs/type_account.md#type-account">Account</a>
 * <a href="https://github.com/libra/libra/blob/master/json-rpc/docs/type_event.md#event-data">EventData</a>
 * <a href="https://github.com/libra/libra/blob/master/json-rpc/docs/type_transaction.md#type-vmstatus">VmStatus</a>
 * <a href="https://github.com/libra/libra/blob/master/json-rpc/docs/type_transaction.md#type-transactiondata">TransactionData</a>
 */
public interface Constants {
    // static account addresses
    AccountAddress ROOT_ACCOUNT_ADDRESS = AccountAddressUtils.create("0000000000000000000000000A550C18");
    AccountAddress CORE_CODE_ADDRESS = AccountAddressUtils.create("00000000000000000000000000000001");

    // ChainIDs, Testnet id see Testnet#CHAIN_ID
    ChainId MAINNET_CHAIN_ID = new ChainId((byte) 1);
    ChainId DEVNET_CHAIN_ID = new ChainId((byte) 3);
    ChainId TESTING_CHAIN_ID = new ChainId((byte) 4);

    // AccountRole#type field values
    String ACCOUNT_ROLE_UNKNOWN = "unknown";
    String ACCOUNT_ROLE_CHILD_VASP = "child_vasp";
    String ACCOUNT_ROLE_PARENT_VASP = "parent_vasp";
    String ACCOUNT_ROLE_DESIGNATED_DEALER = "designated_dealer";

    // EventData#type field values
    String EVENT_DATA_UNKNOWN = "unknown";
    String EVENT_DATA_BURN = "burn";
    String EVENT_DATA_CANCEL_BURN = "cancelburn";
    String EVENT_DATA_MINT = "mint";
    String EVENT_DATA_TO_LBR_EXCHANGE_RATE_UPDATE = "to_lbr_exchange_rate_update";
    String EVENT_DATA_PREBURN = "preburn";
    String EVENT_DATA_RECEIVED_PAYMENT = "receivedpayment";
    String EVENT_DATA_SENT_PAYMENT = "sentpayment";
    String EVENT_DATA_NEW_EPOCH = "newepoch";
    String EVENT_DATA_NEW_BLOCK = "newblock";
    String EVENT_DATA_RECEIVED_MINT = "receivedmint";
    String EVENT_DATA_COMPLIANCE_KEY_ROTATION = "compliancekeyrotation";
    String EVENT_DATA_BASE_URL_ROTATION = "baseurlrotation";
    String EVENT_DATA_CREATE_ACCOUNT = "createaccount";
    String EVENT_DATA_ADMIN_TRANSACTION = "admintransaction";

    // VMStatus#type field values
    String VM_STATUS_EXECUTED = "executed";
    String VM_STATUS_OUT_OF_GAS = "out_of_gas";
    String VM_STATUS_MOVE_ABORT = "move_abort";
    String VM_STATUS_EXECUTION_FAILURE = "execution_failure";
    String VM_STATUS_MISC_ERROR = "miscellaneous_error";

    // TransactionData#type field values
    String TRANSACTION_DATA_BLOCK_METADATA = "blockmetadata";
    String TRANSACTION_DATA_WRITE_SET = "writeset";
    String TRANSACTION_DATA_USER = "user";
    String TRANSACTION_DATA_UNKNOWN = "unknown";

    // Script#type field values, only set unknown type here,
    // other types, please see https://github.com/libra/libra/blob/master/language/stdlib/transaction_scripts/doc/transaction_script_documentation.md for all available script names.
    String SCRIPT_UNKNOWN = "unknown";

}
