// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem;

import com.diem.jsonrpc.JsonRpc;
import com.diem.types.AccountAddress;
import com.novi.serde.Unsigned;
import com.diem.types.SignedTransaction;

import java.util.List;

/**
 * <p>
 * DiemClient is interface of Diem JSON-RPC service client.
 * @see <a href="https://github.com/diem/diem/blob/master/json-rpc/json-rpc-spec.md">Diem JSON-RPC SPEC</a>
 * for API documents.
 * </p>
 *
 * <p>Initialize a client:</p>
 * <code>
 * DiemClient client = new DiemJsonRpcClient(fullNodeServerURL, Constaints.MAINNET_CHAIN_ID);
 * </code>
 */
public interface DiemClient {
    JsonRpc.Metadata getMetadata() throws DiemException;

    JsonRpc.Metadata getMetadata(@Unsigned long version) throws DiemException;

    List<JsonRpc.CurrencyInfo> getCurrencies() throws DiemException;

    JsonRpc.Account getAccount(String address) throws DiemException;

    JsonRpc.Account getAccount(AccountAddress address) throws DiemException;

    JsonRpc.Transaction getAccountTransaction(String address, @Unsigned long sequence, boolean includeEvents) throws DiemException;
    JsonRpc.Transaction getAccountTransaction(AccountAddress address, @Unsigned long sequence, boolean includeEvents) throws DiemException;

    List<JsonRpc.Transaction> getAccountTransactions(String address, @Unsigned long start, @Unsigned int limit, boolean includeEvents) throws DiemException;
    List<JsonRpc.Transaction> getAccountTransactions(AccountAddress address, @Unsigned long start, @Unsigned int limit, boolean includeEvents) throws DiemException;

    void submit(String txnHex) throws DiemException;

    void submit(SignedTransaction txn) throws DiemException;

    JsonRpc.Transaction waitForTransaction(String signedTxnHex, int timeout) throws DiemException;
    JsonRpc.Transaction waitForTransaction(SignedTransaction signedTransaction, int timeout) throws DiemException;
    JsonRpc.Transaction waitForTransaction(String address, @Unsigned long sequence, String transactionHash, @Unsigned long expirationTimeSec, int timeout) throws DiemException;
    JsonRpc.Transaction waitForTransaction(AccountAddress address, @Unsigned long sequence, String transactionHash, @Unsigned long expirationTimeSec, int timeout) throws DiemException;

    List<JsonRpc.Transaction> getTransactions(@Unsigned long fromVersion, int limit, boolean includeEvents) throws DiemException;

    List<JsonRpc.Event> getEvents(String events_key, @Unsigned long start, @Unsigned long limit) throws DiemException;
}
