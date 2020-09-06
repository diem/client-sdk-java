// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.libra.librasdk.Client;
import org.libra.librasdk.Constants;
import org.libra.librasdk.LibraClient;
import org.libra.librasdk.dto.Transaction;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Testnet {
    public static String JSON_RPC_URL = "https://testnet.libra.org/v1";
    public static String FAUCET_SERVER_URL = "testnet.libra.org";
    public static byte CHAIN_ID = Constants.TEST_NET_CHAIN_ID;

    private static final long DEFAULT_TIMEOUT = 10 * 1000;

    public static Client createClient() {
        return new LibraClient(JSON_RPC_URL, CHAIN_ID);
    }

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
        URIBuilder builder = new URIBuilder();
        builder.setScheme("https").setHost(FAUCET_SERVER_URL).setPath("/mint")
                .setParameter("amount", String.valueOf(amount)).setParameter("auth_key", authKey)
                .setParameter("currency_code", currencyCode);

        URI build = null;
        try {
            build = builder.build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpPost post = new HttpPost(build);

        int retry = 10;
        while (true) {
            retry--;

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(post)) {
                String result = EntityUtils.toString(response.getEntity());

                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException(response.toString());
                }

                return Long.parseLong(result);
            } catch (IOException e) {
                if (retry == 0) {
                    throw new RuntimeException(e);
                }
            }
            waitAWhile();
        }
    }

    private static void waitAWhile() {
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
