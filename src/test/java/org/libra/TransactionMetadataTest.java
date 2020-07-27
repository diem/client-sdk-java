// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra;

import com.novi.lcs.LcsDeserializer;
import com.novi.serde.Bytes;
import com.novi.serde.DeserializationError;
import com.novi.serde.Unsigned;
import org.junit.Before;
import org.junit.Test;
import org.libra.jsonrpctypes.JsonRpc;
import org.libra.types.AccountAddress;
import org.libra.types.GeneralMetadata;
import org.libra.types.GeneralMetadataV0;
import org.libra.types.Metadata;
import org.libra.utils.AccountAddressUtils;
import org.libra.utils.Hex;

import java.util.Optional;

import static org.junit.Assert.*;

public class TransactionMetadataTest {
    public static final String ACCOUNT_ADDRESS = "f72589b71ff4f8d139674a3f7369c69b";

    private AccountAddress accountAddress;

    @Before
    public void setUp() {
        accountAddress = AccountAddressUtils.create(ACCOUNT_ADDRESS);
    }

    @Test
    public void getNewTravelRuleMetadata() {
        TransactionMetadata transactionMetadata =
                TransactionMetadata.createTravelRuleMetadata("off chain reference id", accountAddress, 1000);

        assertEquals("020001166f666620636861696e207265666572656e6365206964",
                Hex.encode(transactionMetadata.getMetadata()).toLowerCase());
        assertEquals(
                "020001166f666620636861696e207265666572656e6365206964f72589b71ff4f8d139674a3f7369c69be803000000000000404024244c494252415f41545445535424244040",
                Hex.encode(transactionMetadata.getSignatureMessage()).toLowerCase());
    }

    @Test
    public void testNewGeneralMetadataToSubAddress() {
        SubAddress subAddress = new SubAddress("8f8b82153010a1bd");
        TransactionMetadata metadata = TransactionMetadata.createGeneralMetadataToSubAddress(subAddress);
        byte[] newGeneralMetadataToSubAddress = metadata.getMetadata().content();
        assertEquals("010001088f8b82153010a1bd0000", Hex.encode(newGeneralMetadataToSubAddress).toLowerCase());
        assertArrayEquals(new byte[0], metadata.getSignatureMessage());
    }

    @Test
    public void testNewGeneralMetadataFromSubAddress() {
        SubAddress subAddress = new SubAddress("8f8b82153010a1bd");
        TransactionMetadata metadata = TransactionMetadata.createGeneralMetadataFromSubAddress(subAddress);
        byte[] newGeneralMetadataToSubAddress = metadata.getMetadata().content();

        assertEquals("01000001088f8b82153010a1bd00", Hex.encode(newGeneralMetadataToSubAddress).toLowerCase());
        assertArrayEquals(new byte[0], metadata.getSignatureMessage());
    }

    @Test
    public void testNewGeneralMetadataWithFromToSubAddresses() {
        SubAddress fromSubAddress = new SubAddress("8f8b82153010a1bd");
        SubAddress toSubAddress = new SubAddress("111111153010a111");
        TransactionMetadata metadata = TransactionMetadata.createGeneralMetadataWithFromToSubAddresses(fromSubAddress, toSubAddress);
        byte[] newGeneralMetadataToSubAddress = metadata.getMetadata().content();

        assertEquals("01000108111111153010a11101088f8b82153010a1bd00", Hex.encode(newGeneralMetadataToSubAddress).toLowerCase());
        assertArrayEquals(new byte[0], metadata.getSignatureMessage());
    }

    @Test
    public void testFindRefundReferenceEventFromTransaction_returnsNull() {
        assertNull(TransactionMetadata.findRefundReferenceEventFromTransaction(null, accountAddress));
    }

    @Test
    public void testFindRefundReferenceEventFromTransaction_success() {
        JsonRpc.Event event1 = newEvent("unknowntype", ACCOUNT_ADDRESS);
        JsonRpc.Event event2 = newEvent("receivedpayment", "unknown address");
        JsonRpc.Event event3 = newEvent("receivedpayment", ACCOUNT_ADDRESS);
        JsonRpc.Transaction transaction = JsonRpc.Transaction.newBuilder().addEvents(event1).addEvents(event2).addEvents(event3).build();

        JsonRpc.Event event = TransactionMetadata.findRefundReferenceEventFromTransaction(transaction, accountAddress);

        assertNotNull(event);
        assertEquals(event.getData().getType(), "receivedpayment");
        assertEquals(event.getData().getReceiver(), ACCOUNT_ADDRESS);
    }

    @Test
    public void testFindRefundReferenceEventFromTransaction_notFound() {
        JsonRpc.Event event1 = newEvent("unknowntype", ACCOUNT_ADDRESS);
        JsonRpc.Event event2 = newEvent("receivedpayment", "unknown address");
        JsonRpc.Transaction transaction = JsonRpc.Transaction.newBuilder().addEvents(event1).addEvents(event2).build();

        JsonRpc.Event event = TransactionMetadata.findRefundReferenceEventFromTransaction(transaction, accountAddress);

        assertNull(event);
    }

    @Test
    public void testGetRefundMetadataFromEvent_fromAndToSubAddresses() throws Exception {
        SubAddress fromSubAddress = new SubAddress(new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
        SubAddress toSubAddress = new SubAddress(new byte[]{8, 7, 6, 5, 4, 3, 2, 1});
        TransactionMetadata txnMetadata = TransactionMetadata.createGeneralMetadataWithFromToSubAddresses(fromSubAddress, toSubAddress);

        String metadata = Hex.encode(txnMetadata.getMetadata());

        long sequenceNumber = 123;
        JsonRpc.Event event = newEvent(metadata, sequenceNumber);

        Metadata eventMetadata = TransactionMetadata.deserializeMetadata(event);

        Metadata.GeneralMetadata generalMetadata = (Metadata.GeneralMetadata) eventMetadata;
        assertNotNull(generalMetadata);

        TransactionMetadata refundMetadata = TransactionMetadata.createRefundMetadataFromEvent(sequenceNumber, generalMetadata);

        GeneralMetadata expected = new GeneralMetadata.GeneralMetadataVersion0(
                new GeneralMetadataV0(Optional.of(new Bytes(fromSubAddress.getBytes())),
                        Optional.of(new Bytes(toSubAddress.getBytes())), Optional.of(sequenceNumber)));

        Metadata refundMetadataDeserialized = Metadata.deserialize(new LcsDeserializer(refundMetadata.getMetadata().content()));
        Metadata.GeneralMetadata metadataDeserialized = (Metadata.GeneralMetadata) refundMetadataDeserialized;
        assertEquals(expected, metadataDeserialized.value);
    }

    @Test
    public void testGetRefundMetadataFromEvent_fromSubAddresses() throws Exception {
        SubAddress fromSubAddress = new SubAddress(new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
        TransactionMetadata txnMetadata = TransactionMetadata.createGeneralMetadataFromSubAddress(fromSubAddress);
        String metadata = Hex.encode(txnMetadata.getMetadata());

        long sequenceNumber = 123;
        JsonRpc.Event event = newEvent(metadata, sequenceNumber);

        Metadata eventMetadata = TransactionMetadata.deserializeMetadata(event);
        Metadata.GeneralMetadata generalMetadata = (Metadata.GeneralMetadata) eventMetadata;
        assertNotNull(generalMetadata);

        TransactionMetadata refundMetadata = TransactionMetadata.createRefundMetadataFromEvent(sequenceNumber, generalMetadata);

        GeneralMetadata expected = new GeneralMetadata.GeneralMetadataVersion0(
                new GeneralMetadataV0(Optional.of(new Bytes(fromSubAddress.getBytes())), Optional.empty(),
                        Optional.of(sequenceNumber)));

        Metadata refundMetadataDeserialized = Metadata.deserialize(new LcsDeserializer(refundMetadata.getMetadata().content()));
        Metadata.GeneralMetadata metadataDeserialized = (Metadata.GeneralMetadata) refundMetadataDeserialized;
        assertEquals(expected, metadataDeserialized.value);
    }

    @Test
    public void testGetRefundMetadataFromEvent_toSubAddresses() throws Exception {
        SubAddress toSubAddress = new SubAddress(new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
        TransactionMetadata txnMetadata = TransactionMetadata.createGeneralMetadataToSubAddress(toSubAddress);
        String metadata = Hex.encode(txnMetadata.getMetadata());

        long sequenceNumber = 123;
        JsonRpc.Event event = newEvent(metadata, sequenceNumber);

        Metadata eventMetadata = TransactionMetadata.deserializeMetadata(event);
        Metadata.GeneralMetadata generalMetadata = (Metadata.GeneralMetadata) eventMetadata;
        assertNotNull(generalMetadata);

        TransactionMetadata refundMetadata = TransactionMetadata.createRefundMetadataFromEvent(sequenceNumber, generalMetadata);

        GeneralMetadata expected = new GeneralMetadata.GeneralMetadataVersion0(
                new GeneralMetadataV0(Optional.empty(), Optional.of(new Bytes(toSubAddress.getBytes())),
                        Optional.of(sequenceNumber)));

        Metadata refundMetadataDeserialized = Metadata.deserialize(new LcsDeserializer(refundMetadata.getMetadata().content()));
        Metadata.GeneralMetadata metadataDeserialized = (Metadata.GeneralMetadata) refundMetadataDeserialized;
        assertEquals(expected, metadataDeserialized.value);
    }

    @Test
    public void testGetRefundMetadataFromEvent_nullEvent() {
        assertThrows("must provide refund reference event", IllegalArgumentException.class,
                () -> TransactionMetadata.deserializeMetadata(null));
    }

    @Test
    public void testGetRefundMetadataFromEvent_invalidHexString() {
        JsonRpc.Event event = newEvent("invalid metadata", 123);
        assertThrows("Unrecognized character", IllegalArgumentException.class,
                () -> TransactionMetadata.deserializeMetadata(event));
    }

    @Test
    public void testGetRefundMetadataFromEvent_invalidLCSBytes() {
        JsonRpc.Event event = newEvent("1112233333", 123);
        assertThrows("Unknown variant index for Metadata: 17", DeserializationError.class,
                () -> TransactionMetadata.deserializeMetadata(event));
    }

    @Test
    public void testGetRefundMetadataFromEvent_invalidLCSBytes3() throws Exception {
        JsonRpc.Event event = newEvent("", 123);
        Metadata metadata = TransactionMetadata.deserializeMetadata(event);
        assertNull(metadata);
    }

    private JsonRpc.Event newEvent(String type, String receiverAddress) {
        return JsonRpc.Event.newBuilder().setData(JsonRpc.EventData.newBuilder().setType(type).setReceiver(receiverAddress)).build();
    }

    private JsonRpc.Event newEvent(String metadata, @Unsigned long sequenceNumber) {
        return JsonRpc.Event.newBuilder().setSequenceNumber(sequenceNumber).setData(JsonRpc.EventData.newBuilder().setMetadata(metadata)).build();
    }
}
