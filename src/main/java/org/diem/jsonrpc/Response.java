// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.diem.jsonrpc;

import com.google.gson.JsonElement;

import java.util.Objects;

/**
 * JSON-RPC response with <a href="https://github.com/libra/libra/blob/master/json-rpc/json-rpc-spec.md#libra-extensions">Libra extension</a>
 */
public class Response {
    private int id;
    private String jsonrpc;
    private byte libra_chain_id;
    private long libra_ledger_timestampusec;
    private long libra_ledger_version;
    private JsonElement result;
    private Error error;

    public int getId() {
        return id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public byte getLibraChainId() {
        return libra_chain_id;
    }

    public long getLibraLedgerTimestampusec() {
        return libra_ledger_timestampusec;
    }

    public long getLibraLedgerVersion() {
        return libra_ledger_version;
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
                libra_chain_id == that.libra_chain_id &&
                libra_ledger_timestampusec == that.libra_ledger_timestampusec &&
                libra_ledger_version == that.libra_ledger_version &&
                Objects.equals(jsonrpc, that.jsonrpc) &&
                Objects.equals(result, that.result) &&
                Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, jsonrpc, libra_chain_id, libra_ledger_timestampusec, libra_ledger_version, result, error);
    }
}
