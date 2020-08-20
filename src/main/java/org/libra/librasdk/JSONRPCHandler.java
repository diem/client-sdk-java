package org.libra.librasdk;

import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import org.libra.librasdk.jsonrpc.JSONRPCClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

class JSONRPCHandler implements RPC {
    private URL serverURL;
    private final JSONRPC2Session mySession;
    private final JSONRPCClient jsonRpcClient;

    public JSONRPCHandler(String url) {
        try {
            serverURL = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        mySession = new JSONRPC2Session(serverURL);
        jsonRpcClient = new JSONRPCClient(url);

    }

    public String call(Method method, List<Object> params) throws LibraSDKException {
        int requestId = 0;

        String result;
        try {
            result = jsonRpcClient.call(method, params);
        } catch (Exception e) {
            throw new LibraSDKException(e);
        }

//        if (response.indicatesSuccess()) {
//            Object resultObj = response.getResult();
//            result = new Gson().toJson(result);
//        } else {
//            throw new LibraSDKException(response.getError());
//        }

        return result;
    }
}
