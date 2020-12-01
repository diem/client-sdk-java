// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.diem.jsonrpc;

import org.diem.DiemException;

/**
 * LibraJsonRpcClient maintains a state of last seen server ledger info state version and timestamp,
 * when client received a response that is from older version, this exception is threw.
 * It basically means the request hits a server that has lag of syncing latest ledger info.
 * It happens when client connects to a cluster of full nodes.
 * You can always retry when get this exception. However, when you submit transaction, the transaction may be
 * submitted successfully even with stale response, as mempool will sync the transaction with validator.
 * But it's fine to retry submit too, just need to be away you may receive an error for re-submit same transaction,
 * and the first submitted transaction will be synced to validator for execution.
 */
public class StaleResponseException extends DiemException {
    public StaleResponseException(String msg) {
        super(msg);
    }
}
