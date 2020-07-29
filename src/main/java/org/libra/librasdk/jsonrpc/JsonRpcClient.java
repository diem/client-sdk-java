// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk.jsonrpc;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.libra.librasdk.Client;
import org.libra.librasdk.LibraSDKException;
import org.libra.librasdk.Method;
import org.libra.librasdk.dto.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JsonRpcClient implements Client {
    private final HttpClient httpClient;
    private URI uri;

    public JsonRpcClient(String uri) {
        this(URI.create(uri));
    }

    public JsonRpcClient(URI uri) {
        this(HttpClient.newHttpClient(), uri);
    }

    public JsonRpcClient(HttpClient httpClient, URI uri) {
        this.httpClient = httpClient;
        this.uri = uri;
    }

    @Override
    public Metadata getMetadata() throws LibraSDKException {
        return this.call(Method.get_metadata, Metadata.class);
    }

    @Override
    public Metadata getMetadata(long version) throws LibraSDKException {
        return this.call(Method.get_metadata, Metadata.class, version);
    }

    @Override
    public Currency[] getCurrencies() throws LibraSDKException {
        return this.call(Method.get_currencies, Currency[].class);
    }

    @Override
    public Account getAccount(String address) throws LibraSDKException {
        return this.call(Method.get_account, Account.class, address);
    }

    @Override
    public Transaction getAccountTransaction(String address, long sequence, boolean includeEvents) throws LibraSDKException {
        return this.call(Method.get_account_transaction, Transaction.class, address, sequence, includeEvents);
    }

    @Override
    public void submit(String data) throws LibraSDKException {
        this.call(Method.submit, null, data);
    }

    @Override
    public Transaction waitForTransaction(String address, long sequence, boolean includeEvents, long timeoutMillis) throws LibraSDKException, InterruptedException {
        for (long millis = 0, step = 100; millis < timeoutMillis; millis += step) {
            Transaction transaction = this.getAccountTransaction(address, sequence, includeEvents);
            if (transaction != null) {
                return transaction;
            }
            Thread.sleep(step);
        }

        return null;
    }

    @Override
    public Transaction[] getTransactions(long start_version, long limit, boolean includeEvents) throws LibraSDKException {
        return this.call(Method.get_transactions, Transaction[].class, start_version, limit, includeEvents);
    }

    @Override
    public Event[] getEvents(String events_key, long start, long limit) throws LibraSDKException {
        return this.call(Method.get_events, Event[].class, events_key, start, limit);
    }

    @Override
    public <T> T call(Method method, Class<T> responseType, Object... args) throws LibraSDKException {
        return this.call(1, method, responseType, args);
    }

    public <T> T call(int id, Method method, Class<T> responseType, Object... args) throws LibraSDKException {
        Request req = new Request(id, method.name(), args);
        Gson gson = new Gson();
        HttpResponse<String> response = httpCall(gson.toJson(req));
        if (response.statusCode() != 200) {
            throw new UnexpectedResponseException(response.toString());
        }
        String body = response.body();
        Response resp = gson.fromJson(body, Response.class);
        if (resp.error != null) {
            throw new JsonRpcErrorException(resp);
        }
        if (responseType == null) {
            return null;
        }
        try {
            return gson.fromJson(resp.result, responseType);
        } catch (JsonSyntaxException e) {
            throw new UnexpectedResponseResultException(body, e);
        }
    }

    private HttpResponse<String> httpCall(String requestBody) throws UnexpectedRemoteCallException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        try {
            return this.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new UnexpectedRemoteCallException(e);
        } catch (InterruptedException e) {
            throw new UnexpectedRemoteCallException(e);
        }
    }

}
