package org.libra.librasdk;

import java.util.HashMap;
import java.util.Map;

public enum LibraNetwork {
    TESTNET("https://client.testnet.libra.org/v1");

    public final String url;
    public static final Map<Integer, LibraNetwork> map = new HashMap<>();

    LibraNetwork(String url) {
        this.url = url;
    }

    public static LibraNetwork getChainIdUrl(int chainId) {
        if(map.isEmpty()){
            initializeMapping();
        }

        map.get(chainId);
        if(!map.containsKey(chainId)){
            throw new RuntimeException(String.format("%s : %s", "Invalid chain id can't initialize client", chainId));
        }

        return map.get(chainId);
    }

    private static void initializeMapping() {
        map.put(2, TESTNET);
    }
}
