// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.jsonrpc;

import com.google.gson.JsonElement;

import java.util.Objects;

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
