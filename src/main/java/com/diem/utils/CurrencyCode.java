// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem.utils;

import com.diem.Constants;
import com.diem.types.StructTag;
import com.diem.types.Identifier;
import com.diem.types.TypeTag;

import java.util.ArrayList;

public class CurrencyCode {
    public static StructTag structTag(String currencyCode) {
        return new StructTag(Constants.CORE_CODE_ADDRESS,
                new Identifier(currencyCode), new Identifier(currencyCode), new ArrayList<>());
    }

    public static TypeTag typeTag(String currencyCode) {
        return new TypeTag.Struct(structTag(currencyCode));
    }
}
