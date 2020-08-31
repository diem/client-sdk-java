package org.libra.librasdk;

public class Net {
    public String uri;
    public int chainId;

     public Net(String uri, int chainId) {
        this.uri = uri;
        this.chainId = chainId;
    }

    public static Net TestNet(){
         return new Net(Constants.TEST_NET_JSON_RPC_V1_URL, Constants.TEST_NET_CHAIN_ID);
     }
}
