// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem;

/**
 * PrivateKey is interface for hiding details of how we handle signing message.
 * @see Signer for how it is used.
 */
public interface PrivateKey {
    byte[] sign(byte[] data);

    byte[] publicKey();
}
