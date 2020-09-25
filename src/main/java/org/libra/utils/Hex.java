// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.utils;

import com.google.common.io.BaseEncoding;
import com.novi.serde.Bytes;
import com.novi.serde.Unsigned;
import org.apache.commons.lang3.ArrayUtils;

public class Hex {
    public static byte[] decode(String hex) {
        return BaseEncoding.base16().decode(hex.toUpperCase());
    }

    public static String encode(byte[] bytes) {
        return BaseEncoding.base16().encode(bytes);
    }

    public static String encode(@Unsigned Byte[] bytes) {
        return encode(ArrayUtils.toPrimitive(bytes));
    }

    public static String encode(Bytes bytes) {
        return encode(bytes.content());
    }
}
