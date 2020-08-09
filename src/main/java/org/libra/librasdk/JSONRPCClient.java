package org.libra.librasdk;

import com.google.gson.Gson;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

class JSONRPCClient implements RPC{

    private URL serverURL;
    private JSONRPC2Session mySession;

    public JSONRPCClient(String url) {
        init(url);
    }

    private void init(String url) {
        try {
            serverURL = new URL(url);
            //JSONRPCClient is protected and is instantiated only with LibraNetwork.url
        } catch (MalformedURLException ignored) {
        }

        mySession = new JSONRPC2Session(serverURL);
    }

    public String call(Method method, List<Object> params) throws JSONRPC2Error, JSONRPC2SessionException {
        int requestId = 0;
        JSONRPC2Request request = new JSONRPC2Request(method.name(), params, requestId);

        JSONRPC2Response response;
        String result = null;

            response = mySession.send(request);

            if (response.indicatesSuccess()) {
                Object resultObj = response.getResult();
                result = new Gson().toJson(resultObj);
                System.out.println(result);
            } else {
                throw new JSONRPC2Error(response.getError().getCode(), response.getError().getMessage());
            }

        return result;
    }
}
