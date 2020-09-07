// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

import com.novi.serde.Unsigned;
import org.libra.librasdk.dto.*;
import org.libra.types.SignedTransaction;

import java.util.List;

public interface Client {
    Metadata getMetadata() throws LibraSDKException;

    Metadata getMetadata(@Unsigned long version) throws LibraSDKException;

    Currency[] getCurrencies() throws LibraSDKException;

    Account getAccount(String address) throws LibraSDKException;

    Transaction getAccountTransaction(String address, @Unsigned long sequence, boolean includeEvents)
    throws LibraSDKException;

    void submit(String data) throws LibraSDKException;

    SignedTransaction transfer(String senderAccountAddress, String libraAuthKey, String privateKey, String publicKey,
                               String receiverAccountAddress, @Unsigned long amount, @Unsigned long maxGasAmount,
                               @Unsigned long gasPriceUnit, String currencyCode, @Unsigned long expirationTimestampSecs,
                               byte chainId, byte[] metadata, byte[] metadataSignature) throws LibraSDKException;

    Transaction waitForTransaction(String address, @Unsigned long sequence, boolean includeEvents,
                                   @Unsigned long timeout) throws InterruptedException, LibraSDKException;

    Transaction waitForTransaction(String signedTransactionHash, int timeout) throws LibraSDKException;

    Transaction waitForTransaction(SignedTransaction signedTransaction, int timeout) throws LibraSDKException;

    Transaction waitForTransaction(String address, @Unsigned long sequence, String transactionHash,
                                   @Unsigned long expirationTimeSec, int timeout) throws LibraSDKException;

    List<Transaction> getTransactions(@Unsigned long fromVersion, int limit, boolean includeEvents)
    throws LibraSDKException;

    List<Event> getEvents(String events_key, @Unsigned long start, @Unsigned long limit) throws LibraSDKException;
}
