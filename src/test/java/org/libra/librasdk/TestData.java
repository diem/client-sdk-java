// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import org.libra.librasdk.dto.Account;
import org.libra.librasdk.dto.Currency;
import org.libra.librasdk.dto.LocalAccount;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TestData {
    public static TestData get() {
        String text;
        try {
            text = Resources.toString(Resources.getResource("test-data.json"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Gson gson = new Gson();
        return gson.fromJson(text, TestData.class);
    }

    public Currency[] currencies;
    public Account root_example;
    public Account child_vasp_account_example;
    public LocalAccount local_account;
    public LocalAccount local_account2;
    public LocalAccount local_account3;
    public GenRawTxn gen_raw_transaction;

    private static class GenRawTxn {

    }
}
