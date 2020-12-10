// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem.utils;

import com.google.common.io.BaseEncoding;
import com.novi.serde.Bytes;
import com.novi.serde.Unsigned;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

public class Hex {
    public static byte[] decode(String hex) {
        return BaseEncoding.base16().decode(hex.toUpperCase());
    }

    public static String encode(byte[] bytes) {
        return BaseEncoding.base16().encode(bytes);
    }

    public static String encode(List<Byte> bytes) {
        return encode(bytes.toArray(new Byte[0]));
    }

    public static String encode(@Unsigned Byte[] bytes) {
        return encode(ArrayUtils.toPrimitive(bytes));
    }

    public static String encode(Bytes bytes) {
        return encode(bytes.content());
    }
}
