// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

public interface RPC {
    <T> T call(Method method, Class<T> responseType, Object... args) throws LibraSDKException;
}
