// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

import org.libra.librasdk.model.LibraTransaction;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestNetFaucetService {
    private static final long DEFAULT_TIMEOUT = 10 * 1000;
    public static String SERVER_URL = "http://faucet.testnet.libra.org/";

    public static void mintCoins(LibraSDK client, long amount, String authKey, String currencyCode) {
        long nextAccountSeq = mintCoinsAsync(amount, authKey, currencyCode);
        LibraTransaction txn = null;
        try {
            txn = client.waitForTransaction(Constants.DD_ADDRESS, nextAccountSeq - 1, false, DEFAULT_TIMEOUT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (txn == null) {
            throw new RuntimeException("mint coins transaction does not exist / failed, sequence: ");
        }
        if (!txn.getRawTransaction().isExecuted()) {
            throw new RuntimeException("mint coins transaction failed: " + txn.toString());
        }
    }

    public static long mintCoinsAsync(long amount, String authKey, String currencyCode) {
        HttpClient httpClient = HttpClient.newHttpClient();

        URI uri = URI.create(SERVER_URL + "?amount=" + amount + "&auth_key=" + authKey + "&currency_code=" + currencyCode);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        int retry = 3;
        for (int i = 0; i <= retry; i++) {
            try {
                HttpResponse<String> resp = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                if (resp.statusCode() != 200) {
                    if (i < retry) {
                        waitAWhile();
                        continue;
                    }
                    throw new RuntimeException(resp.toString());
                }
                return Long.parseLong(resp.body());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException();
    }

    private static void waitAWhile() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
