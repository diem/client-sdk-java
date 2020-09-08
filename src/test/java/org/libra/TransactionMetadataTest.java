package org.libra;

import com.novi.lcs.LcsDeserializer;
import com.novi.serde.Bytes;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.libra.librasdk.LibraSDKException;
import org.libra.librasdk.Utils;
import org.libra.librasdk.dto.Event;
import org.libra.librasdk.dto.Transaction;
import org.libra.types.AccountAddress;
import org.libra.types.GeneralMetadata;
import org.libra.types.GeneralMetadataV0;
import org.libra.types.Metadata;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.libra.librasdk.Utils.intToUInt8;
import static org.libra.librasdk.Utils.integersToHex;

public class TransactionMetadataTest {
    public static final String ACCOUNT_ADDRESS = "f72589b71ff4f8d139674a3f7369c69b";

    AccountAddress accountAddress;

    @Before
    public void setUp() {
        accountAddress = Utils.hexToAddress(ACCOUNT_ADDRESS);
    }

    @Test
    public void getNewTravelRuleMetadata() throws LibraSDKException {
        TransactionMetadata transactionMetadata =
                TransactionMetadata.getTravelRuleMetadata("off chain reference id", accountAddress, 1000);

        assertEquals("020001166f666620636861696e207265666572656e6365206964",
                integersToHex(transactionMetadata.getMetadata()));
        assertEquals(
                "020001166f666620636861696e207265666572656e6365206964f72589b71ff4f8d139674a3f7369c69be803000000000000404024244c494252415f41545445535424244040",
                integersToHex(transactionMetadata.getSignatureMessage()));
    }

    @Test
    public void testNewGeneralMetadataToSubAddress() throws LibraSDKException {
        SubAddress subAddress = new SubAddress("8f8b82153010a1bd");
        Integer[] newGeneralMetadataToSubAddress = TransactionMetadata.getGeneralMetadataToSubAddress(subAddress);


        assertEquals("010001088f8b82153010a1bd0000", integersToHex(newGeneralMetadataToSubAddress));

    }

    @Test
    public void testNewGeneralMetadataFromSubAddress() throws LibraSDKException {
        SubAddress subAddress = new SubAddress("8f8b82153010a1bd");
        Integer[] newGeneralMetadataToSubAddress = TransactionMetadata.getGeneralMetadataFromSubAddress(subAddress);

        assertEquals("01000001088f8b82153010a1bd00", integersToHex(newGeneralMetadataToSubAddress));
    }

    @Test
    public void testNewGeneralMetadataWithFromToSubAddresses() throws LibraSDKException {
        SubAddress fromSubAddress = new SubAddress("8f8b82153010a1bd");
        SubAddress toSubAddress = new SubAddress("111111153010a111");
        Integer[] newGeneralMetadataToSubAddress =
                TransactionMetadata.getGeneralMetadataWithFromToSubAddresses(fromSubAddress, toSubAddress);

        assertEquals("01000108111111153010a11101088f8b82153010a1bd00", integersToHex(newGeneralMetadataToSubAddress));
    }

    @Test
    public void testFindRefundReferenceEventFromTransaction_returnsNull() {
        Event event = TransactionMetadata.findRefundReferenceEventFromTransaction(null, accountAddress);

        assertNull(event);
    }

    @Test
    public void testFindRefundReferenceEventFromTransaction_success() {
        Event event1 = new Event("unknowntype", ACCOUNT_ADDRESS);
        Event event2 = new Event("receivedpayment", "unknown address");
        Event event3 = new Event("receivedpayment", ACCOUNT_ADDRESS);
        Transaction transaction = new Transaction(event1, event2, event3);

        Event event = TransactionMetadata.findRefundReferenceEventFromTransaction(transaction, accountAddress);

        assertNotNull(event);
        assertEquals(event.data.type, "receivedpayment");
        assertEquals(event.data.receiver, ACCOUNT_ADDRESS);
    }

    @Test
    public void testFindRefundReferenceEventFromTransaction_notFound() {
        Event event1 = new Event("unknowntype", ACCOUNT_ADDRESS);
        Event event2 = new Event("receivedpayment", "unknown address");
        Transaction transaction = new Transaction(event1, event2);

        Event event = TransactionMetadata.findRefundReferenceEventFromTransaction(transaction, accountAddress);

        assertNull(event);
    }

    @Test
    public void testGetRefundMetadataFromEvent_fromAndToSubAddresses() throws Exception {
        SubAddress fromSubAddress = new SubAddress(new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
        SubAddress toSubAddress = new SubAddress(new byte[]{8, 7, 6, 5, 4, 3, 2, 1});
        Integer[] metadataInts =
                TransactionMetadata.getGeneralMetadataWithFromToSubAddresses(fromSubAddress, toSubAddress);
        String metadata = integersToHex(metadataInts);

        long sequenceNumber = intToUInt8(123);
        Event event = new Event(metadata, sequenceNumber);

        Metadata eventMetadata = TransactionMetadata.deserializeMetadata(event);


        Metadata.GeneralMetadata generalMetadata = (Metadata.GeneralMetadata) eventMetadata;
        assertNotNull(generalMetadata);

        byte[] refundMetadata = TransactionMetadata.getRefundMetadataFromEvent(sequenceNumber, generalMetadata.value);

        GeneralMetadata expected = new GeneralMetadata.GeneralMetadataVersion0(
                new GeneralMetadataV0(Optional.of(new Bytes(fromSubAddress.getBytes())),
                        Optional.of(new Bytes(toSubAddress.getBytes())), Optional.of(sequenceNumber)));

        Metadata refundMetadataDeserialized = Metadata.deserialize(new LcsDeserializer(refundMetadata));
        Metadata.GeneralMetadata metadataDeserialized = (Metadata.GeneralMetadata) refundMetadataDeserialized;
        assertEquals(expected, metadataDeserialized.value);
    }

    @Test
    public void testGetRefundMetadataFromEvent_fromSubAddresses() throws Exception {
        SubAddress fromSubAddress = new SubAddress(new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
        Integer[] generalMetadataInts = TransactionMetadata.getGeneralMetadataFromSubAddress(fromSubAddress);
        String metadata = integersToHex(generalMetadataInts);

        long sequenceNumber = intToUInt8(123);
        Event event = new Event(metadata, sequenceNumber);

        Metadata eventMetadata = TransactionMetadata.deserializeMetadata(event);
        Metadata.GeneralMetadata generalMetadata = (Metadata.GeneralMetadata) eventMetadata;
        assertNotNull(generalMetadata);

        byte[] refundMetadata = TransactionMetadata.getRefundMetadataFromEvent(sequenceNumber, generalMetadata.value);

        GeneralMetadata expected = new GeneralMetadata.GeneralMetadataVersion0(
                new GeneralMetadataV0(Optional.of(new Bytes(fromSubAddress.getBytes())), Optional.empty(),
                        Optional.of(sequenceNumber)));

        Metadata refundMetadataDeserialized = Metadata.deserialize(new LcsDeserializer(refundMetadata));
        Metadata.GeneralMetadata metadataDeserialized = (Metadata.GeneralMetadata) refundMetadataDeserialized;
        assertEquals(expected, metadataDeserialized.value);
    }

    @Test
    public void testGetRefundMetadataFromEvent_toSubAddresses() throws Exception {
        SubAddress toSubAddress = new SubAddress(new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
        Integer[] generalMetadataInts = TransactionMetadata.getGeneralMetadataToSubAddress(toSubAddress);
        String metadata = integersToHex(generalMetadataInts);

        long sequenceNumber = intToUInt8(123);
        Event event = new Event(metadata, sequenceNumber);

        Metadata eventMetadata = TransactionMetadata.deserializeMetadata(event);
        Metadata.GeneralMetadata generalMetadata = (Metadata.GeneralMetadata) eventMetadata;
        assertNotNull(generalMetadata);

        byte[] refundMetadata = TransactionMetadata.getRefundMetadataFromEvent(sequenceNumber, generalMetadata.value);

        GeneralMetadata expected = new GeneralMetadata.GeneralMetadataVersion0(
                new GeneralMetadataV0(Optional.empty(), Optional.of(new Bytes(toSubAddress.getBytes())),
                        Optional.of(sequenceNumber)));

        Metadata refundMetadataDeserialized = Metadata.deserialize(new LcsDeserializer(refundMetadata));
        Metadata.GeneralMetadata metadataDeserialized = (Metadata.GeneralMetadata) refundMetadataDeserialized;
        assertEquals(expected, metadataDeserialized.value);
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testGetRefundMetadataFromEvent_nullEvent() throws Exception {
        exceptionRule.expect(LibraSDKException.class);
        exceptionRule.expectMessage("must provide refund reference event");
        TransactionMetadata.deserializeMetadata(null);
    }

    @Test
    public void testGetRefundMetadataFromEvent_invalidHexString() throws Exception {
        Event event = new Event("invalid metadata", 123);
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("Unrecognized character");
        TransactionMetadata.deserializeMetadata(event);
    }

    @Test
    public void testGetRefundMetadataFromEvent_invalidLCSBytes() throws Exception {
        Event event = new Event("1112233333", 123);
        exceptionRule.expect(LibraSDKException.class);
        exceptionRule.expectMessage("can't deserialize metadata");
        TransactionMetadata.deserializeMetadata(event);
    }

    @Test
    public void testGetRefundMetadataFromEvent_invalidLCSBytes3() throws Exception {
        Event event = new Event("", 123);
        Metadata metadata = TransactionMetadata.deserializeMetadata(event);
        assertNull(metadata);
    }

}
