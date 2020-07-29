// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk.dto;

import com.google.gson.JsonElement;

import java.util.Arrays;
import java.util.Objects;

public class Transaction {

    public static final String VM_STATUS_EXECUTED = "executed";
    public static final String VM_STATUS_OUT_OF_GAS = "out_of_gas";
    public static final String VM_STATUS_MOVE_ABORT = "move_abort";
    public static final String VM_STATUS_EXECUTION_FAILURE = "execution_failure";
    public static final String VM_STATUS_VERIFICATION_ERROR = "verification_error";
    public static final String VM_STATUS_DESERIALIZATION_ERROR = "deserialization_error";
    public static final String VM_STATUS_PUBLISHING_FAILURE = "publishing_failure";

    public static class Script {
        public String type;
        public String receiver;
        public long amount;
        public String currency;
        public String metadata;
        public String metadata_signature;

        @Override
        public String toString() {
            return "Script{" +
                    "type='" + type + '\'' +
                    ", receiver='" + receiver + '\'' +
                    ", amount=" + amount +
                    ", currency='" + currency + '\'' +
                    ", metadata='" + metadata + '\'' +
                    ", metadata_signature='" + metadata_signature + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Script script = (Script) o;
            return amount == script.amount &&
                    Objects.equals(type, script.type) &&
                    Objects.equals(receiver, script.receiver) &&
                    Objects.equals(currency, script.currency) &&
                    Objects.equals(metadata, script.metadata) &&
                    Objects.equals(metadata_signature, script.metadata_signature);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, receiver, amount, currency, metadata, metadata_signature);
        }
    }

    public static class Data {
        public String type;
        // blockmetadata
        public long timestamp_usecs;
        // user
        public String sender;
        public String signature_scheme;
        public String signature;
        public String public_key;
        public long sequence_number;
        public int chain_id;
        public long max_gas_amount;
        public long gas_unit_price;
        public String gas_currency;
        public long expiration_timestamp_secs;
        public String script_hash;
        public Script script;

        @Override
        public String toString() {
            return "Data{" +
                    "type='" + type + '\'' +
                    ", timestamp_usecs=" + timestamp_usecs +
                    ", sender='" + sender + '\'' +
                    ", signature_scheme='" + signature_scheme + '\'' +
                    ", signature='" + signature + '\'' +
                    ", public_key='" + public_key + '\'' +
                    ", sequence_number=" + sequence_number +
                    ", chain_id=" + chain_id +
                    ", max_gas_amount=" + max_gas_amount +
                    ", gas_unit_price=" + gas_unit_price +
                    ", gas_currency='" + gas_currency + '\'' +
                    ", expiration_timestamp_secs=" + expiration_timestamp_secs +
                    ", script_hash='" + script_hash + '\'' +
                    ", script=" + script +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Data data = (Data) o;
            return timestamp_usecs == data.timestamp_usecs &&
                    sequence_number == data.sequence_number &&
                    chain_id == data.chain_id &&
                    max_gas_amount == data.max_gas_amount &&
                    gas_unit_price == data.gas_unit_price &&
                    expiration_timestamp_secs == data.expiration_timestamp_secs &&
                    Objects.equals(type, data.type) &&
                    Objects.equals(sender, data.sender) &&
                    Objects.equals(signature_scheme, data.signature_scheme) &&
                    Objects.equals(signature, data.signature) &&
                    Objects.equals(public_key, data.public_key) &&
                    Objects.equals(gas_currency, data.gas_currency) &&
                    Objects.equals(script_hash, data.script_hash) &&
                    Objects.equals(script, data.script);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, timestamp_usecs, sender, signature_scheme, signature, public_key, sequence_number, chain_id, max_gas_amount, gas_unit_price, gas_currency, expiration_timestamp_secs, script_hash, script);
        }
    }

    public long version;
    public Data transaction;
    public String hash;
    public Event[] events;
    public JsonElement vm_status;
    public long gas_used;

    public boolean isExecuted() {
        return VM_STATUS_EXECUTED.equalsIgnoreCase(this.vmStatus());
    }

    public String vmStatus() {
        if (this.vm_status.isJsonObject()) {
            return this.vm_status.getAsJsonObject().keySet().iterator().next();
        }
        return this.vm_status.getAsString();
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "version=" + version +
                ", transaction=" + transaction +
                ", hash='" + hash + '\'' +
                ", events=" + Arrays.toString(events) +
                ", vm_status=" + vm_status +
                ", gas_used=" + gas_used +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return version == that.version &&
                gas_used == that.gas_used &&
                Objects.equals(transaction, that.transaction) &&
                Objects.equals(hash, that.hash) &&
                Arrays.equals(events, that.events) &&
                Objects.equals(vm_status, that.vm_status);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(version, transaction, hash, vm_status, gas_used);
        result = 31 * result + Arrays.hashCode(events);
        return result;
    }
}
