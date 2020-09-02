// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;


import okhttp3.*;
import org.libra.librasdk.dto.Transaction;

public class TestNetFaucetService {
    private static final long DEFAULT_TIMEOUT = 10 * 1000;
    public static String SERVER_URL = "https://testnet.libra.org/mint";

    public static void mintCoins(Client client, long amount, String authKey, String currencyCode) {
        long nextAccountSeq = mintCoinsAsync(amount, authKey.toLowerCase(), currencyCode);
        Transaction txn = null;
        try {
            txn = client.waitForTransaction(Constants.DD_ADDRESS, nextAccountSeq - 1, false, DEFAULT_TIMEOUT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (txn == null) {
            throw new RuntimeException("mint coins transaction does not exist / failed, sequence: " + nextAccountSeq);
        }
        if (!txn.isExecuted()) {
            throw new RuntimeException("mint coins transaction failed: " + txn.toString());
        }
    }

    public static long mintCoinsAsync(long amount, String authKey, String currencyCode) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder builder = HttpUrl.parse(SERVER_URL).newBuilder();
        builder.addQueryParameter("amount", String.valueOf(amount));
        builder.addQueryParameter("auth_key", authKey);
        builder.addQueryParameter("currency_code", currencyCode);

        HttpUrl url = builder.build();
        RequestBody emptyBody = RequestBody.create(null, new byte[0]);
        Request request = new Request.Builder().url(url).post(emptyBody).build();

        int retry = 10;
        for (int i = 0; i <= retry; i++) {
            try {
                Response response = client.newCall(request).execute();
                if (response.code() != 200) {
                    if (i < retry) {
                        waitAWhile();
                        continue;
                    }
                    throw new RuntimeException(response.toString());
                }
                String body = response.body().string();
                return Long.parseLong(body);
            } catch (Exception e) {
                // ignore errors and retry
            }
        }
        throw new RuntimeException("mint coins failed");
    }

    private static void waitAWhile() {
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
