// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk.dto;

import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.libra.librasdk.Utils;
import org.libra.types.AccountAddress;

import java.util.Objects;

public class LocalAccount {
    public String libra_account_address;
    public String libra_auth_key;
    public String private_key;
    public String public_key;

    public AccountAddress getAccountAddress() {
        return Utils.hexToAddress(this.libra_account_address);
    }

    public Ed25519PrivateKeyParameters getPrivateKey() {
        return Utils.hexToKey(this.private_key);
    }

    @Override
    public String toString() {
        return "LocalAccount{" +
                "libra_account_address='" + libra_account_address + '\'' +
                ", libra_auth_key='" + libra_auth_key + '\'' +
                ", private_key='" + private_key + '\'' +
                ", public_key='" + public_key + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalAccount that = (LocalAccount) o;
        return Objects.equals(libra_account_address, that.libra_account_address) &&
                Objects.equals(libra_auth_key, that.libra_auth_key) &&
                Objects.equals(private_key, that.private_key) &&
                Objects.equals(public_key, that.public_key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(libra_account_address, libra_auth_key, private_key, public_key);
    }
}
