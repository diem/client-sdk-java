// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.examples;

import com.novi.serde.Bytes;
import com.novi.serde.DeserializationError;
import org.junit.Assert;
import org.junit.Test;
import org.libra.*;
import org.libra.jsonrpc.StaleResponseException;
import org.libra.jsonrpctypes.JsonRpc;
import org.libra.stdlib.Helpers;
import org.libra.types.Metadata;
import org.libra.utils.CurrencyCode;

import java.util.List;

import static org.libra.examples.Utils.genChildVASPAccount;
import static org.libra.examples.Utils.getAccountBalance;

public class Refund {
    @Test
    public void custodialAccountToCustodialAccountUnderThreshold() throws LibraException {
        LibraClient client = Testnet.createClient();
        LocalAccount senderParentVASPAccount = Utils.genAccount(client, 1_000_000);
        LocalAccount senderChildVASPAccount = genChildVASPAccount(client, senderParentVASPAccount, 500_000l);

        SubAddress senderCustodialUserSubAddress = SubAddress.generate();

        LocalAccount receiverParentVASPAccount = Utils.genAccount(client, 1_000_000);
        LocalAccount receiverChildVASPAccount = genChildVASPAccount(client, receiverParentVASPAccount, 500_000l);

        SubAddress receiverCustodialUserSubAddress = SubAddress.generate();

        // make a transfer
        long transactionVersion = Utils.submitAndWait(client, senderChildVASPAccount, Helpers.encode_peer_to_peer_with_metadata_script(
                CurrencyCode.LBR_TYPE, receiverChildVASPAccount.address, 500_000L,
                TransactionMetadata.createGeneralMetadataWithFromToSubAddresses(
                        senderCustodialUserSubAddress, receiverCustodialUserSubAddress).getMetadata(),
                new Bytes(new byte[0]) // no metadata signature for GeneralMetadata
        ));

        Assert.assertEquals(500_000, getAccountBalance(client, senderParentVASPAccount));
        Assert.assertEquals(0, getAccountBalance(client, senderChildVASPAccount));
        Assert.assertEquals(500_000, getAccountBalance(client, receiverParentVASPAccount));
        Assert.assertEquals(1_000_000, getAccountBalance(client, receiverChildVASPAccount));

        // refund start: for a given transaction version
        List<JsonRpc.Transaction> txns = null;
        for (int retry = 100; retry > 0; retry--) {
            try {
                // include events, we will need event for refund reference id
                txns = client.getTransactions(transactionVersion, 1, true);
            } catch (StaleResponseException e) {
                // testnet is a cluster of full node, so we need handle stale response exception
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    throw new RuntimeException(ie);
                }
                continue;
            }
        }
        if (txns == null || txns.size() != 1) {
            throw new RuntimeException("could not find transaction: " + transactionVersion);
        }

        JsonRpc.Event event = TransactionMetadata.findRefundReferenceEventFromTransaction(txns.get(0), receiverChildVASPAccount.address);
        // event#data#currency is not set for receivedpayment event, should use amount#currency
        JsonRpc.Amount amount = event.getData().getAmount();
        Metadata metadata;
        try {
            metadata = TransactionMetadata.deserializeMetadata(event);
        } catch (DeserializationError e) {
            throw new RuntimeException("Can't deserialize received event metadata", e);
        }

        TransactionMetadata refundMetadata = TransactionMetadata.createRefundMetadataFromEvent(event.getSequenceNumber(), metadata);
        Utils.submitAndWait(client, receiverChildVASPAccount, Helpers.encode_peer_to_peer_with_metadata_script(
                CurrencyCode.typeTag(amount.getCurrency()),
                senderChildVASPAccount.address,
                amount.getAmount(),
                refundMetadata.getMetadata(),
                new Bytes(new byte[0])
        ));
        Assert.assertEquals(500_000, getAccountBalance(client, senderParentVASPAccount));
        Assert.assertEquals(500_000, getAccountBalance(client, senderChildVASPAccount));
        Assert.assertEquals(500_000, getAccountBalance(client, receiverParentVASPAccount));
        Assert.assertEquals(500_000, getAccountBalance(client, receiverChildVASPAccount));
    }
}
