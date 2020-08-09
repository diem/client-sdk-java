// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import org.libra.librasdk.dto.*;

import java.util.List;

public interface Client {
    Metadata getMetadata() throws LibraSDKException, JSONRPC2Error;

    Metadata getMetadata(long version) throws LibraSDKException, JSONRPC2Error;

    Currency[] getCurrencies() throws LibraSDKException, JSONRPC2Error;

    Account getAccount(String address) throws LibraSDKException, JSONRPC2Error;

    Transaction getAccountTransaction(String address, long sequence, boolean includeEvents) throws LibraSDKException, JSONRPC2Error;

    void submit(String data) throws LibraSDKException, JSONRPC2Error;

    Transaction waitForTransaction(String address, long sequence, boolean includeEvents, long timeout) throws LibraSDKException, InterruptedException, JSONRPC2Error;

    List<Transaction> getTransactions(int fromVersion, int limit, boolean includeEvents) throws JSONRPC2Error;

    List<Event> getEvents(String events_key, long start, long limit) throws LibraSDKException, JSONRPC2Error;
}
