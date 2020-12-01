// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.diem.jsonrpc;

import java.util.Arrays;
import java.util.Objects;

public class Request {
    public String jsonrpc = "2.0";
    public String method;
    public Object[] params;
    public int id;

    public Request(int id, String method, Object[] params) {
        this.id = id;
        this.method = method;
        this.params = params;
    }

    @Override
    public String toString() {
        return "Request{" +
                "jsonrpc='" + jsonrpc + '\'' +
                ", method='" + method + '\'' +
                ", params=" + Arrays.toString(params) +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request Request = (Request) o;
        return id == Request.id &&
                Objects.equals(jsonrpc, Request.jsonrpc) &&
                Objects.equals(method, Request.method) &&
                Arrays.equals(params, Request.params);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(jsonrpc, method, id);
        result = 31 * result + Arrays.hashCode(params);
        return result;
    }
}
