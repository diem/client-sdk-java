// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

import org.libra.librasdk.dto.*;
import org.libra.types.SignedTransaction;

import java.util.List;

public interface Client {
    Metadata getMetadata() throws LibraSDKException;

    Metadata getMetadata(long version) throws LibraSDKException;

    Currency[] getCurrencies() throws LibraSDKException;

    Account getAccount(String address) throws LibraSDKException;

    Transaction getAccountTransaction(String address, long sequence, boolean includeEvents) throws LibraSDKException;

    void submit(String data) throws LibraSDKException;

    SignedTransaction transfer(String senderAccountAddress, String libraAuthKey,
                               String privateKey,
                               String publicKey, String receiverAccountAddress,
                               long amount, long maxGasAmount, long gasPriceUnit,
                               String currencyCode,
                               long expirationTimestampSecs, byte chainId,
                               byte[] metadata, byte[] metadataSignature) throws LibraSDKException;

    Transaction waitForTransaction(String address, long sequence, boolean includeEvents,
                                   long timeout) throws InterruptedException, LibraSDKException;

    List<Transaction> getTransactions(long fromVersion, int limit, boolean includeEvents) throws LibraSDKException;

    List<Event> getEvents(String events_key, long start, long limit) throws LibraSDKException;
}
