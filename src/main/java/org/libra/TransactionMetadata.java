package org.libra;

import com.novi.lcs.LcsDeserializer;
import com.novi.lcs.LcsSerializer;
import com.novi.serde.Bytes;
import com.novi.serde.Unsigned;
import org.libra.librasdk.LibraSDKException;
import org.libra.librasdk.dto.Event;
import org.libra.types.*;

import java.util.Optional;

import static org.libra.librasdk.Utils.*;

public class TransactionMetadata {
    private final Integer[] metadata;
    private final Integer[] signatureMessage;

    public TransactionMetadata(Integer[] metadata, Integer[] signatureMessage) {
        this.metadata = metadata;
        this.signatureMessage = signatureMessage;
    }

    public Integer[] getMetadata() {
        return metadata;
    }

    public Integer[] getSignatureMessage() {
        return signatureMessage;
    }

    public static TransactionMetadata getTravelRuleMetadata(String offChainReferenceId,
                                                            AccountAddress senderAccountAddress, @Unsigned long amount)
    throws LibraSDKException {
        Metadata.TravelRuleMetadata travelRuleMetadata = new Metadata.TravelRuleMetadata(
                new TravelRuleMetadata.TravelRuleMetadataVersion0(
                        new TravelRuleMetadataV0(Optional.of(offChainReferenceId))));

        try {
            LcsSerializer serializer = new LcsSerializer();
            travelRuleMetadata.serialize(serializer);
            byte[] metadataBytes = serializer.get_bytes();
            Integer[] metadataAddressUInt8 = byteToUInt8Array(metadataBytes);

            Integer[] accountAddressUInt8 = byteToUInt8Array(senderAccountAddress.value);

            serializer = new LcsSerializer();
            serializer.serialize_u64(amount);
            byte[] amountBytes = serializer.get_bytes();
            Integer[] amountAddressUInt8 = byteToUInt8Array(amountBytes);

            Integer[] signatureMessageUInt8 = mergeArrays(metadataAddressUInt8, accountAddressUInt8, amountAddressUInt8,
                    byteToUInt8Array("@@$$LIBRA_ATTEST$$@@".getBytes()));

            return new TransactionMetadata(metadataAddressUInt8, signatureMessageUInt8);
        } catch (Exception e) {
            throw new LibraSDKException(e);
        }
    }


    public static Integer[] getGeneralMetadataToSubAddress(SubAddress toSubAddress) throws LibraSDKException {
        return getGeneralMetadata(Optional.empty(), Optional.of(new Bytes(toSubAddress.getBytes())), Optional.empty());
    }

    public static Integer[] getGeneralMetadataFromSubAddress(SubAddress fromSubAddress) throws LibraSDKException {
        return getGeneralMetadata(Optional.of(new Bytes(fromSubAddress.getBytes())), Optional.empty(),
                Optional.empty());
    }

    public static Integer[] getGeneralMetadataWithFromToSubAddresses(SubAddress fromSubAddress, SubAddress toSubAddress)
    throws LibraSDKException {
        return getGeneralMetadata(Optional.of(new Bytes(fromSubAddress.getBytes())),
                Optional.of(new Bytes(toSubAddress.getBytes())), Optional.empty());
    }

    private static Integer[] getGeneralMetadata(Optional<com.novi.serde.Bytes> byteFromSubAddress,
                                                Optional<com.novi.serde.Bytes> toSubAddress,
                                                Optional<@Unsigned Long> referencedEvent) throws LibraSDKException {
        Metadata.GeneralMetadata generalMetadata = new Metadata.GeneralMetadata(
                new GeneralMetadata.GeneralMetadataVersion0(
                        new GeneralMetadataV0(toSubAddress, byteFromSubAddress, referencedEvent)));

        try {
            LcsSerializer serializer = new LcsSerializer();
            generalMetadata.serialize(serializer);
            byte[] metadataBytes = serializer.get_bytes();

            return byteToUInt8Array(metadataBytes);
        } catch (Exception e) {
            throw new LibraSDKException(e);
        }

    }

    public static Event findRefundReferenceEventFromTransaction(org.libra.librasdk.dto.Transaction transaction,
                                                                AccountAddress receiver) {
        Event result = null;

        if (transaction == null) {
            return null;
        }

        String address = bytesToHex(receiver.value);

        for (Event event : transaction.events) {
            if (event.data.type.equalsIgnoreCase("receivedpayment") && event.data.receiver.equalsIgnoreCase(address)) {
                result = event;
                break;
            }
        }

        return result;
    }

    public static Metadata deserializeMetadata(Event event) throws LibraSDKException {
        if (event == null) {
            throw new LibraSDKException("must provide refund reference event");
        }

        String metadata = event.data.metadata;
        if (metadata.equals("")) {
            return null;
        }

        byte[] bytes = hexToBytes(metadata);
        Metadata deserialize;

        try {
            deserialize = Metadata.deserialize(new LcsDeserializer(bytes));
        } catch (Exception e) {
            throw new LibraSDKException(String.format("can't deserialize metadata: %s", metadata));
        }

        return deserialize;
    }

    public static byte[] getRefundMetadataFromEvent(long eventSequenceNumber, GeneralMetadata generalMetadata)
    throws LibraSDKException {
        if (generalMetadata == null) {
            throw new LibraSDKException("must provide refund event general metadata");
        }

        GeneralMetadata.GeneralMetadataVersion0 generalMetadataVersion0 =
                (GeneralMetadata.GeneralMetadataVersion0) generalMetadata;

        Metadata metadata = new Metadata.GeneralMetadata(new GeneralMetadata.GeneralMetadataVersion0(
                new GeneralMetadataV0(generalMetadataVersion0.value.from_subaddress,
                        generalMetadataVersion0.value.to_subaddress, Optional.of(eventSequenceNumber))));

        byte[] bytes;
        try {
            bytes = metadata.lcsSerialize();
        } catch (Exception e) {
            throw new LibraSDKException("lcs serialize failed");
        }

        return bytes;
    }

}
