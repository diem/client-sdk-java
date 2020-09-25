// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra;

import com.novi.serde.Bytes;
import org.libra.types.*;
import org.libra.utils.HashUtils;

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
