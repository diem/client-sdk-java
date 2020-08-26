// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

import com.facebook.lcs.LcsDeserializer;
import com.facebook.serde.Bytes;
import com.facebook.serde.Deserializer;
import org.junit.Test;
import org.libra.librasdk.dto.LocalAccount;
import org.libra.stdlib.Helpers;
import org.libra.types.RawTransaction;
import org.libra.types.Script;
import org.libra.types.TypeTag;

import static org.junit.Assert.*;

public class UtilsTest {
    @Test
    public void testHexToBytes() {
        String key = "b13968ad5722ee203968f7deea565b2f4266f923b3292065b6e190c368f91036";
        byte[] bytes = Utils.hexToBytes(key);
        assertEquals(key.toUpperCase(), Utils.bytesToHex(bytes));
    }
    @Test
    public void testHexToAddress() {
        String address = "1668f6be25668c1a17cd8caf6b8d2f25";
        assertEquals(address.toUpperCase(), Utils.addressToHex(Utils.hexToAddress(address)));
    }

    @Test
    public void testCreateRawTransaction() throws Exception {
        String sender = "e1b3d22871989e9fd9dc6814b2f4fc41";
        String recipient = "71e931795d23e9634fd24a5992065f6b";
        long maxGasAmount = 1000000;
        long gasUnitPrice = 0;
        long seq = 42;
        long expiration = 1593189628;
        long amount = 100;
        String currencyCode = "LBR";

        TypeTag token = Utils.createCurrencyCodeTypeTag(currencyCode);
        Script script = Helpers.encode_peer_to_peer_with_metadata_script(
                token,
                Utils.hexToAddress(recipient),
                amount,
                new Bytes(new byte[]{}),
                new Bytes(new byte[]{})
        );
        String expectedScript = "01e101a11ceb0b010000000701000202020403061004160205181d0735610896011000000001010000020001000003020301010004010300010501060c0108000506080005030a020a020005060c05030a020a020109000c4c696272614163636f756e741257697468647261774361706162696c6974791b657874726163745f77697468647261775f6361706162696c697479087061795f66726f6d1b726573746f72655f77697468647261775f6361706162696c69747900000000000000000000000000000001010104010c0b0011000c050e050a010a020b030b0438000b05110202010700000000000000000000000000000001034c4252034c425200040371e931795d23e9634fd24a5992065f6b01640000000000000004000400";
        assertEquals(expectedScript.toUpperCase(), Utils.bytesToHex(Utils.toLCS(script)));
        String expected =                                              "e1b3d22871989e9fd9dc6814b2f4fc412a0000000000000001e101a11ceb0b010000000701000202020403061004160205181d0735610896011000000001010000020001000003020301010004010300010501060c0108000506080005030a020a020005060c05030a020a020109000c4c696272614163636f756e741257697468647261774361706162696c6974791b657874726163745f77697468647261775f6361706162696c697479087061795f66726f6d1b726573746f72655f77697468647261775f6361706162696c69747900000000000000000000000000000001010104010c0b0011000c050e050a010a020b030b0438000b05110202010700000000000000000000000000000001034c4252034c425200040371e931795d23e9634fd24a5992065f6b0164000000000000000400040040420f00000000000000000000000000034c4252fc24f65e0000000004";

        assertEquals(expected.toUpperCase(), Utils.bytesToHex(Utils.toLCS(Utils.createRawTransaction(
                Utils.hexToAddress(sender),
                seq,
                script,
                maxGasAmount, gasUnitPrice, currencyCode,
                expiration,
                Constants.TESTING_CHAIN_ID
        ))));
    }

    @Test
    public void testSignTransaction() throws Exception {
        String rawTransaction = "e1b3d22871989e9fd9dc6814b2f4fc412a0000000000000001e101a11ceb0b010000000701000202020403061004160205181d0735610896011000000001010000020001000003020301010004010300010501060c0108000506080005030a020a020005060c05030a020a020109000c4c696272614163636f756e741257697468647261774361706162696c6974791b657874726163745f77697468647261775f6361706162696c697479087061795f66726f6d1b726573746f72655f77697468647261775f6361706162696c69747900000000000000000000000000000001010104010c0b0011000c050e050a010a020b030b0438000b05110202010700000000000000000000000000000001034c4252034c425200040371e931795d23e9634fd24a5992065f6b0164000000000000000400040040420f00000000000000000000000000034c4252fc24f65e0000000004";
        Deserializer deserializer = new LcsDeserializer(Utils.hexToBytes(rawTransaction));
        RawTransaction txn = RawTransaction.deserialize(deserializer);
        byte[] hash = Utils.hashRawTransaction(txn);
        String expectedHash = "a55742d83cb3ca87cdf8f231f22dd75534a2588b174b20e6dc41292e92ce79e5e1b3d22871989e9fd9dc6814b2f4fc412a0000000000000001e101a11ceb0b010000000701000202020403061004160205181d0735610896011000000001010000020001000003020301010004010300010501060c0108000506080005030a020a020005060c05030a020a020109000c4c696272614163636f756e741257697468647261774361706162696c6974791b657874726163745f77697468647261775f6361706162696c697479087061795f66726f6d1b726573746f72655f77697468647261775f6361706162696c69747900000000000000000000000000000001010104010c0b0011000c050e050a010a020b030b0438000b05110202010700000000000000000000000000000001034c4252034c425200040371e931795d23e9634fd24a5992065f6b0164000000000000000400040040420f00000000000000000000000000034c4252fc24f65e0000000004";
        assertEquals(expectedHash.toUpperCase(), Utils.bytesToHex(hash));

        byte[] sig = Utils.sign(Utils.hexToKey("b2f7f581d6de3c06a822fd6e7e8265fbc00f8401696a5bdc34f5a6d2ff3f922f"), hash);
        String expectedSig = "193eabce444d5cca25bb18591a2dca11688a2cc513852bca52016cab309be67a4fd409f1c9c162a7f2ab9265faeb22eb6e03a52196592d3bf7f96195ce08ae08";
        assertEquals(expectedSig.toUpperCase(), Utils.bytesToHex(sig));
    }

    @Test
    public void testBech32Encode() {
        char[] data = {14, 15, 3, 31, 13};
        String humanReadablePart = "hello";
        String encoded = Utils.Bech32Encode(humanReadablePart, data);
        assertEquals(encoded, "hello1w0rldcs7fw6");
    }

    @Test
    public void testGenerateLocalAccount() {
        LocalAccount localAccount = Utils.generateLocalAccount();

        assertNotNull(localAccount.private_key);
        assertNotNull(localAccount.public_key);
        assertNotNull(localAccount.libra_auth_key);
        assertNotNull(localAccount.libra_account_address);
    }

    @Test
    public void testGenerateLocalAccountFromSeed() {
        LocalAccount localAccount = Utils.generateLocalAccountFromSeed("76e3de861d516283dc285e12ddadc95245a9e98f351c910b0ad722f790bac273");

        assertTrue(localAccount.private_key.equalsIgnoreCase("76e3de861d516283dc285e12ddadc95245a9e98f351c910b0ad722f790bac273"));
        assertTrue(localAccount.public_key.equalsIgnoreCase("f549a91fb9989883fb4d38b463308f3ea82074fb39ea74dae61f62e11bf55d25"));
        assertTrue(localAccount.libra_auth_key.equalsIgnoreCase("d939b0214b484bf4d71d08d0247b755a1668f6be25668c1a17cd8caf6b8d2f25"));
        assertTrue(localAccount.libra_account_address.equalsIgnoreCase("1668f6be25668c1a17cd8caf6b8d2f25"));
    }
}
