package org.libra.librasdk2;

import com.google.gson.Gson;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
import org.libra.librasdk.Method;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

class JSONRPCClient {

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

    public String call(Method method, List<Object> params) {
        int requestId = 0;
        JSONRPC2Request request = new JSONRPC2Request(method.name(), params, requestId);

        JSONRPC2Response response;
        String result = null;

        try {
            response = mySession.send(request);

            if (response.indicatesSuccess()) {
                Object resultObj = response.getResult();
                result = new Gson().toJson(resultObj);
                System.out.println(result);
            } else {
                System.out.println(response.getError().getMessage());
            }

        } catch (JSONRPC2SessionException e) {
            System.err.println(e.getMessage());
        }

        return result;
    }
}
