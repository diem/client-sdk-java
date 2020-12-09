// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem;

import com.diem.types.*;
import com.novi.serde.Bytes;
import com.diem.utils.HashUtils;

public class Signer {
    public static SignedTransaction sign(PrivateKey privateKey, RawTransaction raw) {
        return new SignedTransaction(raw,
                new TransactionAuthenticator.Ed25519(
                        new Ed25519PublicKey(new Bytes(privateKey.publicKey())),
                        new Ed25519Signature(new Bytes(privateKey.sign(HashUtils.signatureMessage(raw))))
                )
        );
    }
}
