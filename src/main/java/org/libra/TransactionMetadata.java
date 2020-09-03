package org.libra;

import com.novi.lcs.LcsSerializer;
import com.novi.serde.Bytes;
import com.novi.serde.Unsigned;
import org.libra.librasdk.LibraSDKException;
import org.libra.types.*;

import java.util.Optional;

import static org.libra.librasdk.Utils.byteToUInt8Array;
import static org.libra.librasdk.Utils.mergeArrays;

public class TransactionMetadata {
    private Integer[] metadata;
    private Integer[] signatureMessage;

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

    public static TransactionMetadata getNewTravelRuleMetadata(String offChainReferenceId,
                                                               AccountAddress senderAccountAddress,
                                                               @Unsigned long amount)
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

            Integer[] signatureMessageUInt8 =
                    mergeArrays(metadataAddressUInt8, accountAddressUInt8, amountAddressUInt8,
                            byteToUInt8Array("@@$$LIBRA_ATTEST$$@@".getBytes()));

            return new TransactionMetadata(metadataAddressUInt8, signatureMessageUInt8);
        } catch (Exception e) {
            throw new LibraSDKException(e);
        }
    }


    public static Integer[] getNewGeneralMetadataToSubAddress(SubAddress toSubAddress)
    throws LibraSDKException {
        return getNewGeneralMetadata(Optional.empty(),
                Optional.of(new Bytes(toSubAddress.getBytes())), Optional.empty());
    }

    public static Integer[] getNewGeneralMetadataFromSubAddress(SubAddress fromSubAddress)
    throws LibraSDKException {
        return getNewGeneralMetadata(Optional.of(new Bytes(fromSubAddress.getBytes())),
                Optional.empty(), Optional.empty());
    }

    public static Integer[] getNewGeneralMetadataWithFromToSubAddresses(SubAddress fromSubAddress,
                                                                        SubAddress toSubAddress)
    throws LibraSDKException {
        return getNewGeneralMetadata(Optional.of(new Bytes(fromSubAddress.getBytes())),
                Optional.of(new Bytes(toSubAddress.getBytes())), Optional.empty());
    }

    private static Integer[] getNewGeneralMetadata(
            Optional<com.novi.serde.Bytes> byteFromSubAddress,
            Optional<com.novi.serde.Bytes> toSubAddress, Optional<@Unsigned Long> referencedEvent)
    throws LibraSDKException {
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


}
