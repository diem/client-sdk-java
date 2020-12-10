// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem;

import com.diem.types.AccountAddress;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import com.diem.utils.Hex;

import java.security.SecureRandom;

public class LocalAccount {
    public static LocalAccount generate() {
        return generate(new Ed25519PrivateKeyParameters(new SecureRandom()));
    }

    public static LocalAccount generate(String privateKeyHex) {
        byte[] privateKeyBytes = Hex.decode(privateKeyHex);
        return generate(new Ed25519PrivateKeyParameters(privateKeyBytes, 0));
    }

    private static LocalAccount generate(
            Ed25519PrivateKeyParameters privateKeyParams) {
        PrivateKey privateKey = new Ed25519PrivateKey(privateKeyParams);

        AuthKey authKey = AuthKey.ed24419(privateKey.publicKey());
        AccountAddress address = authKey.accountAddress();

        return new LocalAccount(address, authKey, privateKey);
    }

    public AccountAddress address;
    public AuthKey authKey;
    public PrivateKey privateKey;

    public LocalAccount(AccountAddress address, AuthKey authKey, PrivateKey privateKey) {
        this.address = address;
        this.authKey = authKey;
        this.privateKey = privateKey;
    }

}
