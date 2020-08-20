package org.libra.librasdk;

import org.libra.librasdk.jsonrpc.JSONRPCClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

class JSONRPCHandler implements RPC {
    private final JSONRPCClient jsonRpcClient;

    public JSONRPCHandler(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }

        jsonRpcClient = new JSONRPCClient(url);
    }

    public String call(Method method, List<Object> params) throws LibraSDKException {
        String result;
        try {
            result = jsonRpcClient.call(method, params);
        } catch (Exception e) {
            throw new LibraSDKException(e);
        }

        return result;
    }
}
