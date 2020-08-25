package org.libra.librasdk;

public class Net {
    public String uri;
    public int chainId;

     public Net(String uri, int chainId) {
        this.uri = uri;
        this.chainId = chainId;
    }

    public static Net TestNet(){
         return new Net("https://client.testnet.libra.org/v1", 2);
     }
}
