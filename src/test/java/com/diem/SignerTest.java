// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem;

import com.diem.types.RawTransaction;
import com.diem.types.TransactionAuthenticator;
import com.diem.utils.HashUtils;
import com.novi.serde.Bytes;
import org.junit.Test;
import com.diem.types.SignedTransaction;
import com.diem.utils.Hex;

import static org.junit.Assert.assertEquals;

public class SignerTest {

    @Test
    public void testSignTransaction() throws Exception {
        String rawTxnHex = "44634381fab71b7d6fa411108eda3e9d0000000000000000018f01a11ceb0b010000000501000203020a050c0d07193d08561000000001000100000200010002060c0a020003060c0a020a020f4475616c4174746573746174696f6e0f726f746174655f626173655f75726c1c726f746174655f636f6d706c69616e63655f7075626c69635f6b657900000000000000000000000000000001000201070a000b0111000b000b021101020002040e687474703a2f2f6261736575726c04208416d1c943bb13dd7dfa74b49c0d4bd8b57de4f2b51a2c3591faee18a824daf240420f0000000000000000000000000003585553e71fd05f0000000004";
        RawTransaction txn = RawTransaction.bcsDeserialize(Hex.decode(rawTxnHex));

        assertEquals("e74c3978c4493b06fec031b3b5b97fee945b2d7628528d85d19509dab9f4189c44634381fab71b7d6fa411108eda3e9d0000000000000000018f01a11ceb0b010000000501000203020a050c0d07193d08561000000001000100000200010002060c0a020003060c0a020a020f4475616c4174746573746174696f6e0f726f746174655f626173655f75726c1c726f746174655f636f6d706c69616e63655f7075626c69635f6b657900000000000000000000000000000001000201070a000b0111000b000b021101020002040e687474703a2f2f6261736575726c04208416d1c943bb13dd7dfa74b49c0d4bd8b57de4f2b51a2c3591faee18a824daf240420f0000000000000000000000000003585553e71fd05f0000000004", Hex.encode(HashUtils.signatureMessage(txn)).toLowerCase());
        Ed25519PrivateKey key = new Ed25519PrivateKey("b2f7f581d6de3c06a822fd6e7e8265fbc00f8401696a5bdc34f5a6d2ff3f922f");
        SignedTransaction st = Signer.sign(key, txn);

        String expectedSig = "23876D05763738C69D28A07B8B552FC2947E1D99817B7FC4212F5652DD2DC99558C0EC5DDFF98B6C0EB4ECF223F919B5AA4BE0384A477FDB9D3F941D53FA7203";
        Bytes sig = ((TransactionAuthenticator.Ed25519) st.authenticator).signature.value;
        assertEquals(expectedSig, Hex.encode(sig));
    }
}
