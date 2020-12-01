// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.diem.utils;

import org.diem.Constants;
import org.diem.types.Identifier;
import org.diem.types.StructTag;
import org.diem.types.TypeTag;

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
