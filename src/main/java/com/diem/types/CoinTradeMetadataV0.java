package com.diem.types;


public final class CoinTradeMetadataV0 {
    public final java.util.List<String> trade_ids;

    public CoinTradeMetadataV0(java.util.List<String> trade_ids) {
        java.util.Objects.requireNonNull(trade_ids, "trade_ids must not be null");
        this.trade_ids = trade_ids;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.increase_container_depth();
        TraitHelpers.serialize_vector_str(trade_ids, serializer);
        serializer.decrease_container_depth();
    }

    public byte[] bcsSerialize() throws com.novi.serde.SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.bcs.BcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static CoinTradeMetadataV0 deserialize(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        deserializer.increase_container_depth();
        Builder builder = new Builder();
        builder.trade_ids = TraitHelpers.deserialize_vector_str(deserializer);
        deserializer.decrease_container_depth();
        return builder.build();
    }

    public static CoinTradeMetadataV0 bcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
             throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.bcs.BcsDeserializer(input);
        CoinTradeMetadataV0 value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new com.novi.serde.DeserializationError("Some input bytes were not read");
        }
        return value;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        CoinTradeMetadataV0 other = (CoinTradeMetadataV0) obj;
        if (!java.util.Objects.equals(this.trade_ids, other.trade_ids)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.trade_ids != null ? this.trade_ids.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public java.util.List<String> trade_ids;

        public CoinTradeMetadataV0 build() {
            return new CoinTradeMetadataV0(
                trade_ids
            );
        }
    }
}
