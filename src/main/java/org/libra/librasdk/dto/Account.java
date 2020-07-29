// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk.dto;

import com.google.gson.JsonElement;

import java.util.Arrays;
import java.util.Objects;

public class Account {
    public Amount[] balances;
    public long sequence_number;
    public String authentication_key;
    public String sent_events_key;
    public String received_events_key;
    public boolean delegated_key_rotation_capability;
    public boolean delegated_withdrawal_capability;
    public boolean is_frozen;
    public JsonElement role;

    @Override
    public String toString() {
        return "Account{" +
                "balances=" + Arrays.toString(balances) +
                ", sequence_number=" + sequence_number +
                ", authentication_key='" + authentication_key + '\'' +
                ", sent_events_key='" + sent_events_key + '\'' +
                ", received_events_key='" + received_events_key + '\'' +
                ", delegated_key_rotation_capability=" + delegated_key_rotation_capability +
                ", delegated_withdrawal_capability=" + delegated_withdrawal_capability +
                ", is_frozen=" + is_frozen +
                ", role=" + role +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return sequence_number == account.sequence_number &&
                delegated_key_rotation_capability == account.delegated_key_rotation_capability &&
                delegated_withdrawal_capability == account.delegated_withdrawal_capability &&
                is_frozen == account.is_frozen &&
                Arrays.equals(balances, account.balances) &&
                Objects.equals(authentication_key, account.authentication_key) &&
                Objects.equals(sent_events_key, account.sent_events_key) &&
                Objects.equals(received_events_key, account.received_events_key) &&
                Objects.equals(role, account.role);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(sequence_number, authentication_key, sent_events_key, received_events_key, delegated_key_rotation_capability, delegated_withdrawal_capability, is_frozen, role);
        result = 31 * result + Arrays.hashCode(balances);
        return result;
    }
}
