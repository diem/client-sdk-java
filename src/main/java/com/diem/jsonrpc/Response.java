// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem.jsonrpc;

import com.google.gson.JsonElement;

import java.util.Objects;

/**
 * JSON-RPC response with <a href="https://github.com/diem/diem/blob/master/json-rpc/json-rpc-spec.md#diem-extensions">Diem extension</a>
 */
public class Response {
    private int id;
    private String jsonrpc;
    private byte diem_chain_id;
    private long diem_ledger_timestampusec;
    private long diem_ledger_version;
    private JsonElement result;
    private Error error;

    public int getId() {
        return id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public byte getDiemChainId() {
        return diem_chain_id;
    }

    public long getDiemLedgerTimestampusec() {
        return diem_ledger_timestampusec;
    }

    public long getDiemLedgerVersion() {
        return diem_ledger_version;
    }

    public JsonElement getResult() {
        return result;
    }

    public Error getError() {
        return error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response that = (Response) o;
        return id == that.id &&
                diem_chain_id == that.diem_chain_id &&
                diem_ledger_timestampusec == that.diem_ledger_timestampusec &&
                diem_ledger_version == that.diem_ledger_version &&
                Objects.equals(jsonrpc, that.jsonrpc) &&
                Objects.equals(result, that.result) &&
                Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, jsonrpc, diem_chain_id, diem_ledger_timestampusec, diem_ledger_version, result, error);
    }
}
