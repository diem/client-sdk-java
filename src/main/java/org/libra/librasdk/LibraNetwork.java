package org.libra.librasdk;

public enum LibraNetwork {
    MAINNET("https://client.libra.org"),
    TESTNET("https://client.testnet.libra.org/v1");

    public final String url;

    LibraNetwork(String url) {
        this.url = url;
    }
}
