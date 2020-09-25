// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.utils;

import org.libra.Constants;
import org.libra.types.Identifier;
import org.libra.types.StructTag;
import org.libra.types.TypeTag;

import java.util.ArrayList;

public class CurrencyCode {
    public static String LBR = "LBR";
    public static TypeTag LBR_TYPE = typeTag("LBR");

    public static StructTag structTag(String currencyCode) {
        return new StructTag(Constants.CORE_CODE_ADDRESS,
                new Identifier(currencyCode), new Identifier(currencyCode), new ArrayList<>());
    }

    public static TypeTag typeTag(String currencyCode) {
        return new TypeTag.Struct(structTag(currencyCode));
    }
}
