// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk.jsonrpc;

import com.google.gson.JsonElement;

import java.util.Objects;

public class JSONRPCResponse {
    public int libra_chain_id;
    public long libra_ledger_version;
    public long libra_ledger_timestampusec;

    public int id;
    public Error error;
    public JsonElement result;

    @Override
    public String toString() {
        return "Response{" +
                "libra_chain_id=" + libra_chain_id +
                ", libra_ledger_version=" + libra_ledger_version +
                ", libra_ledger_timestampusec=" + libra_ledger_timestampusec +
                ", id=" + id +
                ", error=" + error +
                ", result=" + result +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSONRPCResponse JSONRPCResponse = (JSONRPCResponse) o;
        return libra_chain_id == JSONRPCResponse.libra_chain_id &&
                libra_ledger_version == JSONRPCResponse.libra_ledger_version &&
                libra_ledger_timestampusec == JSONRPCResponse.libra_ledger_timestampusec &&
                id == JSONRPCResponse.id &&
                Objects.equals(error, JSONRPCResponse.error) &&
                Objects.equals(result, JSONRPCResponse.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(libra_chain_id, libra_ledger_version, libra_ledger_timestampusec, id, error, result);
    }
}
