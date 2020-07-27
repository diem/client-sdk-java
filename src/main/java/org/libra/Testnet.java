// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra;

import com.novi.serde.Unsigned;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.libra.jsonrpc.LibraJsonRpcClient;
import org.libra.jsonrpctypes.JsonRpc;
import org.libra.types.ChainId;
import org.libra.utils.TransactionUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Testnet is utility class for handing Testnet specific data and functions.
 */
public class Testnet {
    public static String JSON_RPC_URL = "https://testnet.libra.org/v1";
    public static String FAUCET_SERVER_URL = "https://testnet.libra.org/mint";
    public static ChainId CHAIN_ID = new ChainId((byte) 2);
    public static String DD_ADDRESS = "000000000000000000000000000000DD";

    private static final long DEFAULT_TIMEOUT = 10 * 1000;

    public static LibraClient createClient() {
        return new LibraJsonRpcClient(JSON_RPC_URL, CHAIN_ID);
    }

    public static void mintCoins(LibraClient client, long amount, String authKey, String currencyCode) {
        long nextAccountSeq = mintCoinsAsync(amount, authKey.toLowerCase(), currencyCode);
        JsonRpc.Transaction txn;
        try {
            txn = waitForTransaction(DD_ADDRESS, nextAccountSeq - 1, false, DEFAULT_TIMEOUT, client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (txn == null) {
            throw new RuntimeException("mint coins transaction does not exist / failed, sequence: " + nextAccountSeq);
        }
        if (!TransactionUtils.isExecuted(txn)) {
            throw new RuntimeException("mint coins transaction failed: " + txn.toString());
        }
    }

    public static long mintCoinsAsync(long amount, String authKey, String currencyCode) {
        URI build;
        try {
            URIBuilder builder = new URIBuilder(FAUCET_SERVER_URL);
            builder.setParameter("amount", String.valueOf(amount)).setParameter("auth_key", authKey)
                    .setParameter("currency_code", currencyCode);
            build = builder.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpPost post = new HttpPost(build);

        int retry = 10;
        while (true) {
            retry--;

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(post)) {
                String result = EntityUtils.toString(response.getEntity());

                if (response.getStatusLine().getStatusCode() != 200) {
                    continue;
                }

                return Long.parseLong(result);
            } catch (IOException e) {
                if (retry == 0) {
                    throw new RuntimeException(e);
                }
            }
            try {
                Thread.sleep(1100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static JsonRpc.Transaction waitForTransaction(String address, @Unsigned long sequence, boolean includeEvents,
                                                                   @Unsigned long timeoutMillis, LibraClient client) throws InterruptedException, LibraException {
        for (long millis = 0, step = 100; millis < timeoutMillis; millis += step) {
            JsonRpc.Transaction transaction = client.getAccountTransaction(address, sequence, includeEvents);
            if (transaction != null) {
                return transaction;
            }
            Thread.sleep(step);
        }

        return null;
    }
}
