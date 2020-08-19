// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk.jsonrpc;

import com.google.gson.Gson;
import com.squareup.okhttp.*;
import org.libra.librasdk.LibraSDKException;
import org.libra.librasdk.Method;

import java.io.IOException;
import java.util.List;

public class JsonRpcClient {
    private final OkHttpClient httpClient;
    private final Request.Builder builder;

    public JsonRpcClient(String uri) {
        httpClient = new OkHttpClient();
        builder = new Request.Builder().url(uri);
    }

    public String call(Method method, List<Object> params) throws LibraSDKException {
        int id = 0;
        JSONRPCRequest JSONRPCRequest = new JSONRPCRequest(id, method.name(), new List[]{params});
        String requestJson = new Gson().toJson(JSONRPCRequest);

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, requestJson);

        Request request = builder.post(body).build();

        String s = httpCall(request);
        return s;


//        if (response.statusCode() != 200) {
//            throw new UnexpectedResponseException(response.toString());
//        }

//        if (resp.error != null) {
//            throw new JsonRpcErrorException(resp);
//        }
//        if (responseType == null) {
//            return null;
//        }

    }

    private String httpCall(Request requestBody) {
        String result = null;
        try {
            Response response = httpClient.newCall(requestBody).execute();
            result = response.body().string();
        } catch (IOException e) {
//            TODO
        }

        return result;
    }

}
