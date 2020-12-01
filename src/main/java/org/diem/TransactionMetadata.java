// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.diem;

import com.novi.lcs.LcsDeserializer;
import com.novi.lcs.LcsSerializer;
import com.novi.serde.Bytes;
import com.novi.serde.DeserializationError;
import com.novi.serde.SerializationError;
import com.novi.serde.Unsigned;
import org.diem.jsonrpctypes.JsonRpc;
import org.diem.types.*;
import org.diem.utils.Hex;

import java.util.Optional;

import static com.google.common.primitives.Bytes.concat;

/**
 * <p>Utils for creating peer to peer transaction metadata.</p>
 *
 * @see <a href="https://github.com/libra/lip/blob/master/lips/lip-4.md">LIP-4 Transaction Metadata Specification</a>
 */
public class TransactionMetadata {
    /**
     * createTravelRuleMetadata creates metadata and signature message for given
     * offChainReferenceID.
     * This is used for peer to peer transfer between 2 custodial accounts when the amount is over threshold.
     *
     * @param offChainReferenceId  Get this id from off-chain API communication.
     * @param senderAccountAddress sender account address
     * @param amount               transfer amount
     * @return TransactionMetadata
     */
    public static TransactionMetadata createTravelRuleMetadata(
            String offChainReferenceId,
            AccountAddress senderAccountAddress, @Unsigned long amount
    ) {
        Metadata.TravelRuleMetadata travelRuleMetadata = new Metadata.TravelRuleMetadata(
                new TravelRuleMetadata.TravelRuleMetadataVersion0(
                        new TravelRuleMetadataV0(Optional.of(offChainReferenceId))));

        // receiver_lcs_data = lcs(metadata, sender_address, amount, "@@$$LIBRA_ATTEST$$@@" /*ASCII-encoded string*/);
        LcsSerializer serializer = new LcsSerializer();
        try {
            travelRuleMetadata.serialize(serializer);

            byte[] metadataBytes = serializer.get_bytes();

            senderAccountAddress.serialize(serializer);
            serializer.serialize_u64(amount);

            byte[] signatureMessage = concat(serializer.get_bytes(), "@@$$LIBRA_ATTEST$$@@".getBytes());
            return new TransactionMetadata(metadataBytes, signatureMessage);
        } catch (SerializationError e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * creates metadata for creating peer to peer transaction script with toSubAddress.
     * This is used for peer to peer transfer from non-custodial account to custodial account.
     *
     * @param toSubAddress
     * @return TransactionMetadata
     */
    public static TransactionMetadata createGeneralMetadataToSubAddress(SubAddress toSubAddress)   {
        return createGeneralMetadata(Optional.empty(), Optional.of(new Bytes(toSubAddress.getBytes())), Optional.empty());
    }

    /**
     * Creates metadata for creating peer to peer transaction script with fromSubAddress.
     * This is used for peer to peer transfer from custodial account to non-custodial account.
     *
     * @param fromSubAddress
     * @return TransactionMetadata
     */
    public static TransactionMetadata createGeneralMetadataFromSubAddress(SubAddress fromSubAddress)   {
        return createGeneralMetadata(Optional.of(new Bytes(fromSubAddress.getBytes())), Optional.empty(),
                Optional.empty());
    }

    /**
     * Creates metadata for creating peer to peer transaction script with fromSubAddress and toSubAddress.
     * Use this function to create metadata with from and to sub-addresses for peer to peer transfer
     * from custodial account to custodial account under travel rule threshold.
     * @param fromSubAddress
     * @param toSubAddress
     * @return TransactionMetadata
     */
    public static TransactionMetadata createGeneralMetadataWithFromToSubAddresses(SubAddress fromSubAddress, SubAddress toSubAddress) {
        return createGeneralMetadata(Optional.of(new Bytes(fromSubAddress.getBytes())),
                Optional.of(new Bytes(toSubAddress.getBytes())), Optional.empty());
    }

    /**
     * This function looks for `receivedpayment` event of the given receiver account address in given transaction#events list.
     * The event found can be used for creating refund transaction metadata with refund event reference sequence number.
     *
     * @param transaction
     * @param receiver
     * @return event object found, or null if given transaction is null or not found.
     */
    public static JsonRpc.Event findRefundReferenceEventFromTransaction(JsonRpc.Transaction transaction,
                                                                        AccountAddress receiver) {
        if (transaction == null) {
            return null;
        }

        String address = Hex.encode(receiver.value);

        for (JsonRpc.Event event : transaction.getEventsList()) {
            if ("receivedpayment".equalsIgnoreCase(event.getData().getType()) && address.equalsIgnoreCase(event.getData().getReceiver())) {
                return event;
            }
        }

        return null;
    }

    /**
     * @param event
     * @return Metadata deserialized from event#data#metadata string
     * @throws IllegalArgumentException if given event is null
     * @throws DeserializationError     if event metadata deserialization failed
     */
    public static Metadata deserializeMetadata(JsonRpc.Event event) throws IllegalArgumentException, DeserializationError {
        if (event == null) {
            throw new IllegalArgumentException("must provide refund reference event");
        }

        String metadata = event.getData().getMetadata();
        if (metadata == null || "".equals(metadata)) {
            return null;
        }

        byte[] bytes = Hex.decode(metadata);
        return Metadata.deserialize(new LcsDeserializer(bytes));
    }

    /**
     * Create TransactionMetadata for refund a transaction by it's event sequence number and transaction metadata.
     * If a transaction uses TravelRule metadata, it should refund in same way (off-chain communication + TravelRule metadata).
     *
     * @param eventSequenceNumber
     * @param generalMetadata
     * @return TransactionMetadata
     * @throws IllegalArgumentException if given generalMetadata is null or it is not an instance of GeneralMetadata.GeneralMetadataVersion0
     */
    public static TransactionMetadata createRefundMetadataFromEvent(long eventSequenceNumber, Metadata generalMetadata) throws IllegalArgumentException {
        if (generalMetadata == null) {
            throw new IllegalArgumentException("must provide refund event general metadata");
        }
        if (!(generalMetadata instanceof Metadata.GeneralMetadata)) {
            throw new IllegalArgumentException(String.format(
                    "Given Metadata is not Metadata.GeneralMetadata: %s",
                    generalMetadata.getClass()));
        }
        Metadata.GeneralMetadata gm = (Metadata.GeneralMetadata) generalMetadata;
        if (!(gm.value instanceof GeneralMetadata.GeneralMetadataVersion0)) {
            throw new IllegalArgumentException(String.format(
                    "Given GeneralMetadata is not GeneralMetadata.GeneralMetadataVersion0: %s",
                    generalMetadata.getClass()));
        }
        GeneralMetadata.GeneralMetadataVersion0 metadata = (GeneralMetadata.GeneralMetadataVersion0) gm.value;

        // refund need swap from and to sub-address for new metadata
        return createGeneralMetadata(metadata.value.to_subaddress, metadata.value.from_subaddress, Optional.of(eventSequenceNumber));
    }

    public static TransactionMetadata createGeneralMetadata(
            Optional<Bytes> byteFromSubAddress, Optional<Bytes> toSubAddress, Optional<@Unsigned Long> referencedEvent) {
        Metadata.GeneralMetadata generalMetadata = new Metadata.GeneralMetadata(
                new GeneralMetadata.GeneralMetadataVersion0(
                        new GeneralMetadataV0(toSubAddress, byteFromSubAddress, referencedEvent)));

        try {
            return new TransactionMetadata(generalMetadata.lcsSerialize(), new byte[0]);
        } catch (SerializationError e) {
            throw new RuntimeException(e);
        }
    }

    private final byte[] metadata;
    private final byte[] signatureMessage;

    public TransactionMetadata(byte[] metadata, byte[] signatureMessage) {
        if (metadata == null) {
            throw new IllegalArgumentException("metadata is not provided");
        }
        if (signatureMessage == null) {
            throw new IllegalArgumentException("metadata signature message is not provided");
        }
        this.metadata = metadata;
        this.signatureMessage = signatureMessage;
    }

    public Bytes getMetadata() {
        return new Bytes(metadata);
    }

    /**
     * Signature message is not metadata signature, use account private key sign the message bytes to get metadata signature.
     * Metadata signature is only required for TravelRule metadata, general metadata won't need it.
     *
     * @return signature message bytes.
     */
    public byte[] getSignatureMessage() {
        return signatureMessage;
    }
}
