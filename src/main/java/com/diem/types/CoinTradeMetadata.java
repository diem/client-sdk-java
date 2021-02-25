package com.diem.types;


public abstract class CoinTradeMetadata {

    abstract public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError;

    public static CoinTradeMetadata deserialize(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        int index = deserializer.deserialize_variant_index();
        switch (index) {
            case 0: return CoinTradeMetadataV0.load(deserializer);
            default: throw new com.novi.serde.DeserializationError("Unknown variant index for CoinTradeMetadata: " + index);
        }
    }

    public byte[] bcsSerialize() throws com.novi.serde.SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.bcs.BcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static CoinTradeMetadata bcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
             throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.bcs.BcsDeserializer(input);
        CoinTradeMetadata value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new com.novi.serde.DeserializationError("Some input bytes were not read");
        }
        return value;
    }

    public static final class CoinTradeMetadataV0 extends CoinTradeMetadata {
        public final com.diem.types.CoinTradeMetadataV0 value;

        public CoinTradeMetadataV0(com.diem.types.CoinTradeMetadataV0 value) {
            java.util.Objects.requireNonNull(value, "value must not be null");
            this.value = value;
        }

        public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
            serializer.increase_container_depth();
            serializer.serialize_variant_index(0);
            value.serialize(serializer);
            serializer.decrease_container_depth();
        }

        static CoinTradeMetadataV0 load(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
            deserializer.increase_container_depth();
            Builder builder = new Builder();
            builder.value = com.diem.types.CoinTradeMetadataV0.deserialize(deserializer);
            deserializer.decrease_container_depth();
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            CoinTradeMetadataV0 other = (CoinTradeMetadataV0) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.diem.types.CoinTradeMetadataV0 value;

            public CoinTradeMetadataV0 build() {
                return new CoinTradeMetadataV0(
                    value
                );
            }
        }
    }
}

