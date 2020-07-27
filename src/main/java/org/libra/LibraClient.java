// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra;

import com.novi.serde.Unsigned;
import org.libra.jsonrpctypes.JsonRpc;
import org.libra.types.AccountAddress;
import org.libra.types.SignedTransaction;

import java.util.List;

/**
 * <p>
 * LibraClient is interface of Libra JSON-RPC service client.
 * @see [Libra JSON-RPC SPEC](https://github.com/libra/libra/blob/master/json-rpc/json-rpc-spec.md)
 * for API documents.
 * </p>
 *
 * <p>Initialize a client:</p>
 * <code>
 * LibraClient client = new LibraJsonRpcClient(fullNodeServerURL, Constaints.MAINNET_CHAIN_ID);
 * </code>
 */
public interface LibraClient {
    JsonRpc.BlockMetadata getMetadata() throws LibraException;

    JsonRpc.BlockMetadata getMetadata(@Unsigned long version) throws LibraException;

    List<JsonRpc.CurrencyInfo> getCurrencies() throws LibraException;

    JsonRpc.Account getAccount(String address) throws LibraException;

    JsonRpc.Account getAccount(AccountAddress address) throws LibraException;

    JsonRpc.Transaction getAccountTransaction(String address, @Unsigned long sequence, boolean includeEvents) throws LibraException;

    JsonRpc.Transaction getAccountTransaction(AccountAddress address, @Unsigned long sequence, boolean includeEvents) throws LibraException;
    List<JsonRpc.Transaction> getAccountTransactions(AccountAddress address, @Unsigned long start, @Unsigned int limit, boolean includeEvents) throws LibraException;

    void submit(String txnHex) throws LibraException;

    void submit(SignedTransaction txn) throws LibraException;

    JsonRpc.Transaction waitForTransaction(String signedTxnHex, int timeout) throws LibraException;

    JsonRpc.Transaction waitForTransaction(SignedTransaction signedTransaction, int timeout) throws LibraException;

    JsonRpc.Transaction waitForTransaction(AccountAddress address, @Unsigned long sequence, String transactionHash, @Unsigned long expirationTimeSec, int timeout) throws LibraException;

    List<JsonRpc.Transaction> getTransactions(@Unsigned long fromVersion, int limit, boolean includeEvents) throws LibraException;

    List<JsonRpc.Event> getEvents(String events_key, @Unsigned long start, @Unsigned long limit) throws LibraException;
}
