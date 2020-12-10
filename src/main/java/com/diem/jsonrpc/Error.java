// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem.jsonrpc;

import com.google.gson.JsonElement;

import java.util.Objects;

/**
 * JSON-RPC Error object
 */
public class Error {
    public int code;
    public String message;
    public JsonElement data;

    @Override
    public String toString() {
        return "Error{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Error error = (Error) o;
        return code == error.code &&
                Objects.equals(message, error.message) &&
                Objects.equals(data, error.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, data);
    }
}
