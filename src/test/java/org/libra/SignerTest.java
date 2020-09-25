// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra;

import com.novi.serde.Bytes;
import org.junit.Test;
import org.libra.types.RawTransaction;
import org.libra.types.SignedTransaction;
import org.libra.types.TransactionAuthenticator;
import org.libra.utils.Hex;

import static org.junit.Assert.assertEquals;

public class SignerTest {

    @Test
    public void testSignTransaction() throws Exception {
        String rawTxnHex = "e1b3d22871989e9fd9dc6814b2f4fc412a0000000000000001e101a11ceb0b010000000701000202020403061004160205181d0735610896011000000001010000020001000003020301010004010300010501060c0108000506080005030a020a020005060c05030a020a020109000c4c696272614163636f756e741257697468647261774361706162696c6974791b657874726163745f77697468647261775f6361706162696c697479087061795f66726f6d1b726573746f72655f77697468647261775f6361706162696c69747900000000000000000000000000000001010104010c0b0011000c050e050a010a020b030b0438000b05110202010700000000000000000000000000000001034c4252034c425200040371e931795d23e9634fd24a5992065f6b0164000000000000000400040040420f00000000000000000000000000034c4252fc24f65e0000000004";
        RawTransaction txn = RawTransaction.lcsDeserialize(Hex.decode(rawTxnHex));

        Ed25519PrivateKey key = new Ed25519PrivateKey("b2f7f581d6de3c06a822fd6e7e8265fbc00f8401696a5bdc34f5a6d2ff3f922f");
        SignedTransaction st = Signer.sign(key, txn);

        String expectedSig = "193eabce444d5cca25bb18591a2dca11688a2cc513852bca52016cab309be67a4fd409f1c9c162a7f2ab9265faeb22eb6e03a52196592d3bf7f96195ce08ae08";
        Bytes sig = ((TransactionAuthenticator.Ed25519) st.authenticator).signature.value;
        assertEquals(expectedSig.toUpperCase(), Hex.encode(sig));
    }
}
