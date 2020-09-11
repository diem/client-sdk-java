// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk.dto;

import com.novi.serde.Unsigned;

import java.util.Objects;

public class Event {
    public Event(String type, String receiver) {
        this.data = new Data();
        this.data.type = type;
        this.data.receiver = receiver;
    }

    public Event(String metadata, @Unsigned long sequenceNumber) {
        this.data = new Data();
        this.data.metadata = metadata;
        this.sequence_number = sequenceNumber;
    }


    public static class Data {
        public String type;
        // preburn, burn, cancelburn, mint, receivedpayment, sentpayment, receivedmint
        public Amount amount;

        // preburn burn, cancelburn
        public String preburn_address;

        // to_lbr_exchange_rate_update
        public String currency_code;
        public float new_to_lbr_exchange_rate;

        // receivedpayment, sentpayment
        public String sender;
        public String receiver;
        public String metadata;

        // upgrade
        public String write_set;

        // newepoch
        public @Unsigned long epoch;

        // newblock
        public @Unsigned long round;
        public String proposer;
        public @Unsigned long proposed_time;

        // receivedmint
        public String destination_address;

        @Override
        public String toString() {
            return "Data{" + "type='" + type + '\'' + ", amount=" + amount + ", preburn_address='" +
                    preburn_address + '\'' + ", currency_code='" + currency_code + '\'' +
                    ", new_to_lbr_exchange_rate=" + new_to_lbr_exchange_rate + ", sender='" +
                    sender + '\'' + ", receiver='" + receiver + '\'' + ", metadata='" + metadata +
                    '\'' + ", write_set='" + write_set + '\'' + ", epoch=" + epoch + ", round=" +
                    round + ", proposer='" + proposer + '\'' + ", proposed_time=" + proposed_time +
                    ", destination_address='" + destination_address + '\'' + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Data data = (Data) o;
            return Float.compare(data.new_to_lbr_exchange_rate, new_to_lbr_exchange_rate) == 0 &&
                    epoch == data.epoch && round == data.round &&
                    proposed_time == data.proposed_time && Objects.equals(type, data.type) &&
                    Objects.equals(amount, data.amount) &&
                    Objects.equals(preburn_address, data.preburn_address) &&
                    Objects.equals(currency_code, data.currency_code) &&
                    Objects.equals(sender, data.sender) &&
                    Objects.equals(receiver, data.receiver) &&
                    Objects.equals(metadata, data.metadata) &&
                    Objects.equals(write_set, data.write_set) &&
                    Objects.equals(proposer, data.proposer) &&
                    Objects.equals(destination_address, data.destination_address);
        }

        @Override
        public int hashCode() {
            return Objects
                    .hash(type, amount, preburn_address, currency_code, new_to_lbr_exchange_rate,
                            sender, receiver, metadata, write_set, epoch, round, proposer,
                            proposed_time, destination_address);
        }
    }

    public String key;
    public @Unsigned long sequence_number;
    public @Unsigned long transaction_version;
    public Data data;

    @Override
    public String toString() {
        return "Event{" + "key='" + key + '\'' + ", sequence_number=" + sequence_number +
                ", transaction_version=" + transaction_version + ", data=" + data + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return sequence_number == event.sequence_number &&
                transaction_version == event.transaction_version &&
                Objects.equals(key, event.key) && Objects.equals(data, event.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, sequence_number, transaction_version, data);
    }
}
