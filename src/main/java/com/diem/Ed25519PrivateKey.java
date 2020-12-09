// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem;

import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import com.diem.utils.Hex;

/**
 * Ed25519 single PrivateKey implementation.
 */
public class Ed25519PrivateKey implements PrivateKey {
    private Ed25519PrivateKeyParameters key;

    public Ed25519PrivateKey(Ed25519PrivateKeyParameters key) {
        this.key = key;
    }

    public Ed25519PrivateKey(String hex) {
        this(new Ed25519PrivateKeyParameters(Hex.decode(hex), 0));
    }

    @Override
    public byte[] sign(byte[] data) {
        Ed25519Signer signer = new Ed25519Signer();
        signer.init(true, key);

        signer.update(data, 0, data.length);
        return signer.generateSignature();
    }

    @Override
    public byte[] publicKey() {
        return key.generatePublicKey().getEncoded();
    }
}
