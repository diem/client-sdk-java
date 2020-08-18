package org.libra.librasdk;

import com.google.gson.Gson;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

class JSONRPCClient implements RPC {
    private URL serverURL;
    private final JSONRPC2Session mySession;

    public JSONRPCClient(String url) {
        try {
            serverURL = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        mySession = new JSONRPC2Session(serverURL);
    }

    public String call(Method method, List<Object> params) throws LibraSDKException {
        int requestId = 0;
        JSONRPC2Request request = new JSONRPC2Request(method.name(), params, requestId);

        JSONRPC2Response response;
        String result;

        try {
            response = mySession.send(request);
        } catch (JSONRPC2SessionException e) {
            throw new LibraSDKException(e);
        }

        if (response.indicatesSuccess()) {
            Object resultObj = response.getResult();
            result = new Gson().toJson(resultObj);
        } else {
            throw new LibraSDKException(response.getError());
        }

        return result;
    }
}
