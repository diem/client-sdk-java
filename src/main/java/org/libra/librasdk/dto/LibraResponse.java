package org.libra.librasdk.dto;

import com.google.gson.JsonElement;

import java.util.Objects;

public class LibraResponse {
    int id;
    String jsonrpc;
    int libra_chain_id;
    long libra_ledger_timestampusec;
    long libra_ledger_version;
    JsonElement result;

    public int getId() {
        return id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public int getLibra_chain_id() {
        return libra_chain_id;
    }

    public long getLibra_ledger_timestampusec() {
        return libra_ledger_timestampusec;
    }

    public long getLibra_ledger_version() {
        return libra_ledger_version;
    }

    public JsonElement getResult() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibraResponse that = (LibraResponse) o;
        return id == that.id &&
                libra_chain_id == that.libra_chain_id &&
                libra_ledger_timestampusec == that.libra_ledger_timestampusec &&
                libra_ledger_version == that.libra_ledger_version &&
                Objects.equals(jsonrpc, that.jsonrpc) &&
                Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, jsonrpc, libra_chain_id, libra_ledger_timestampusec, libra_ledger_version, result);
    }
}
