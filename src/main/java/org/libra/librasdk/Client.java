// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
import org.libra.librasdk.dto.*;

import java.util.List;

public interface Client {
    Metadata getMetadata() throws  JSONRPC2Error, JSONRPC2SessionException;

    Metadata getMetadata(long version) throws  JSONRPC2Error, JSONRPC2SessionException;

    Currency[] getCurrencies() throws  JSONRPC2Error, JSONRPC2SessionException;

    Account getAccount(String address) throws  JSONRPC2Error, JSONRPC2SessionException;

    Transaction getAccountTransaction(String address, long sequence, boolean includeEvents) throws  JSONRPC2Error, JSONRPC2SessionException;

    void submit(String data) throws  JSONRPC2Error, JSONRPC2SessionException;

    Transaction waitForTransaction(String address, long sequence, boolean includeEvents, long timeout) throws  InterruptedException, JSONRPC2Error, JSONRPC2SessionException;

    List<Transaction> getTransactions(int fromVersion, int limit, boolean includeEvents) throws JSONRPC2Error, JSONRPC2SessionException;

    List<Event> getEvents(String events_key, long start, long limit) throws  JSONRPC2Error, JSONRPC2SessionException;
}
