package org.libra;

import org.junit.Before;
import org.junit.Test;
import org.libra.librasdk.LibraSDKException;
import org.libra.librasdk.Utils;
import org.libra.types.AccountAddress;

import static org.junit.Assert.assertEquals;
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
        TransactionMetadata transactionMetadata = TransactionMetadata
                .getNewTravelRuleMetadata("off chain reference id", accountAddress, 1000);

        assertEquals("020001166f666620636861696e207265666572656e6365206964",
                integersToHex(transactionMetadata.getMetadata()));
        assertEquals(
                "020001166f666620636861696e207265666572656e6365206964f72589b71ff4f8d139674a3f7369c69be803000000000000404024244c494252415f41545445535424244040",
                integersToHex(transactionMetadata.getSignatureMessage()));
    }

    @Test
    public void testNewGeneralMetadataToSubAddress() throws LibraSDKException {
        SubAddress subAddress = new SubAddress("8f8b82153010a1bd");
        Integer[] newGeneralMetadataToSubAddress =
                TransactionMetadata.getNewGeneralMetadataToSubAddress(subAddress);


        assertEquals("010001088f8b82153010a1bd0000", integersToHex(newGeneralMetadataToSubAddress));

    }

    @Test
    public void testNewGeneralMetadataFromSubAddress() throws LibraSDKException {
        SubAddress subAddress = new SubAddress("8f8b82153010a1bd");
        Integer[] newGeneralMetadataToSubAddress =
                TransactionMetadata.getNewGeneralMetadataFromSubAddress(subAddress);

        assertEquals("01000001088f8b82153010a1bd00", integersToHex(newGeneralMetadataToSubAddress));
    }

    @Test
    public void testNewGeneralMetadataWithFromToSubaddresses() throws LibraSDKException {
        SubAddress fromSubAddress = new SubAddress("8f8b82153010a1bd");
        SubAddress toSubAddress = new SubAddress("111111153010a111");
        Integer[] newGeneralMetadataToSubAddress =
                TransactionMetadata.getNewGeneralMetadataWithFromToSubAddresses(fromSubAddress, toSubAddress);

        assertEquals("01000108111111153010a11101088f8b82153010a1bd00", integersToHex(newGeneralMetadataToSubAddress)); }

}