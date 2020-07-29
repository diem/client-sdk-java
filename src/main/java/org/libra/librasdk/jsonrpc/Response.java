// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk.jsonrpc;

import com.google.gson.JsonElement;

import java.util.Objects;

public class Response {
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
        Response response = (Response) o;
        return libra_chain_id == response.libra_chain_id &&
                libra_ledger_version == response.libra_ledger_version &&
                libra_ledger_timestampusec == response.libra_ledger_timestampusec &&
                id == response.id &&
                Objects.equals(error, response.error) &&
                Objects.equals(result, response.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(libra_chain_id, libra_ledger_version, libra_ledger_timestampusec, id, error, result);
    }
}
