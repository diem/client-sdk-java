package org.libra.types;

import java.math.BigInteger;

public final class RawTransaction {
    public final org.libra.types.AccountAddress sender;
    public final @com.facebook.serde.Unsigned Long sequence_number;
    public final org.libra.types.TransactionPayload payload;
    public final @com.facebook.serde.Unsigned Long max_gas_amount;
    public final @com.facebook.serde.Unsigned Long gas_unit_price;
    public final String gas_currency_code;
    public final @com.facebook.serde.Unsigned Long expiration_timestamp_secs;
    public final org.libra.types.ChainId chain_id;

    public RawTransaction(org.libra.types.AccountAddress sender, @com.facebook.serde.Unsigned Long sequence_number, org.libra.types.TransactionPayload payload, @com.facebook.serde.Unsigned Long max_gas_amount, @com.facebook.serde.Unsigned Long gas_unit_price, String gas_currency_code, @com.facebook.serde.Unsigned Long expiration_timestamp_secs, org.libra.types.ChainId chain_id) {
       assert sender != null;
       assert sequence_number != null;
       assert payload != null;
       assert max_gas_amount != null;
       assert gas_unit_price != null;
       assert gas_currency_code != null;
       assert expiration_timestamp_secs != null;
       assert chain_id != null;
       this.sender = sender;
       this.sequence_number = sequence_number;
       this.payload = payload;
       this.max_gas_amount = max_gas_amount;
       this.gas_unit_price = gas_unit_price;
       this.gas_currency_code = gas_currency_code;
       this.expiration_timestamp_secs = expiration_timestamp_secs;
       this.chain_id = chain_id;
    }

    public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        sender.serialize(serializer);
        serializer.serialize_u64(sequence_number);
        payload.serialize(serializer);
        serializer.serialize_u64(max_gas_amount);
        serializer.serialize_u64(gas_unit_price);
        serializer.serialize_str(gas_currency_code);
        serializer.serialize_u64(expiration_timestamp_secs);
        chain_id.serialize(serializer);
    }

    public static RawTransaction deserialize(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        Builder builder = new Builder();
        builder.sender = org.libra.types.AccountAddress.deserialize(deserializer);
        builder.sequence_number = deserializer.deserialize_u64();
        builder.payload = org.libra.types.TransactionPayload.deserialize(deserializer);
        builder.max_gas_amount = deserializer.deserialize_u64();
        builder.gas_unit_price = deserializer.deserialize_u64();
        builder.gas_currency_code = deserializer.deserialize_str();
        builder.expiration_timestamp_secs = deserializer.deserialize_u64();
        builder.chain_id = org.libra.types.ChainId.deserialize(deserializer);
        return builder.build();
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        RawTransaction other = (RawTransaction) obj;
        if (!java.util.Objects.equals(this.sender, other.sender)) { return false; }
        if (!java.util.Objects.equals(this.sequence_number, other.sequence_number)) { return false; }
        if (!java.util.Objects.equals(this.payload, other.payload)) { return false; }
        if (!java.util.Objects.equals(this.max_gas_amount, other.max_gas_amount)) { return false; }
        if (!java.util.Objects.equals(this.gas_unit_price, other.gas_unit_price)) { return false; }
        if (!java.util.Objects.equals(this.gas_currency_code, other.gas_currency_code)) { return false; }
        if (!java.util.Objects.equals(this.expiration_timestamp_secs, other.expiration_timestamp_secs)) { return false; }
        if (!java.util.Objects.equals(this.chain_id, other.chain_id)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.sender != null ? this.sender.hashCode() : 0);
        value = 31 * value + (this.sequence_number != null ? this.sequence_number.hashCode() : 0);
        value = 31 * value + (this.payload != null ? this.payload.hashCode() : 0);
        value = 31 * value + (this.max_gas_amount != null ? this.max_gas_amount.hashCode() : 0);
        value = 31 * value + (this.gas_unit_price != null ? this.gas_unit_price.hashCode() : 0);
        value = 31 * value + (this.gas_currency_code != null ? this.gas_currency_code.hashCode() : 0);
        value = 31 * value + (this.expiration_timestamp_secs != null ? this.expiration_timestamp_secs.hashCode() : 0);
        value = 31 * value + (this.chain_id != null ? this.chain_id.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public org.libra.types.AccountAddress sender;
        public @com.facebook.serde.Unsigned Long sequence_number;
        public org.libra.types.TransactionPayload payload;
        public @com.facebook.serde.Unsigned Long max_gas_amount;
        public @com.facebook.serde.Unsigned Long gas_unit_price;
        public String gas_currency_code;
        public @com.facebook.serde.Unsigned Long expiration_timestamp_secs;
        public org.libra.types.ChainId chain_id;

        public RawTransaction build() {
            return new RawTransaction(
                sender,
                sequence_number,
                payload,
                max_gas_amount,
                gas_unit_price,
                gas_currency_code,
                expiration_timestamp_secs,
                chain_id
            );
        }
    }
}
