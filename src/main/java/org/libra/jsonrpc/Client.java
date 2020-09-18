// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.jsonrpc;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.libra.librasdk.LibraSDKException;
import org.libra.librasdk.Method;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class Client {

    private URI serverURL;
    private HttpClient httpClient;

    public Client(String serverURL) {
        this(serverURL, HttpClients.createDefault());
    }

    public Client(String serverURL, HttpClient httpClient) {
        try {
            this.serverURL = new URL(serverURL).toURI();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
        this.httpClient = httpClient;
    }

    public Response call(Method method, List<Object> params) throws LibraSDKException {
        Request request = new Request(0, method.name(), params.toArray());
        String body = makeHttpCall(new Gson().toJson(request));
        Response resp = new Gson().fromJson(body, Response.class);
        if (resp.getError() != null) {
            throw new JsonRpcError(resp.getError().toString());
        }

        return resp;
    }

    private String makeHttpCall(String requestJson) throws LibraSDKException {
        HttpPost post = new HttpPost(serverURL);
        post.addHeader("content-type", "application/json");
        try {
            post.setEntity(new StringEntity(requestJson));
        } catch (UnsupportedEncodingException e) {
            throw new LibraSDKException(e);
        }
        HttpResponse response = null;
        try {
            response = httpClient.execute(post);
        } catch (IOException e) {
            throw new RemoteCallException(e);
        }
        int statusCode = response.getStatusLine().getStatusCode();
        String body = null;
        try {
            body = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new LibraSDKException(e);
        }
        if (statusCode != 200) {
            throw new InvalidResponseException(statusCode, body);
        }
        return body;
    }
}
