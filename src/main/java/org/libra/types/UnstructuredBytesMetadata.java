package org.libra.types;

import java.math.BigInteger;


public final class UnstructuredBytesMetadata {
    public final java.util.Optional<com.novi.serde.Bytes> metadata;

    public UnstructuredBytesMetadata(java.util.Optional<com.novi.serde.Bytes> metadata) {
        assert metadata != null;
        this.metadata = metadata;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.increase_container_depth();
        TraitHelpers.serialize_option_bytes(metadata, serializer);
        serializer.decrease_container_depth();
    }

    public byte[] lcsSerialize() throws com.novi.serde.SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.lcs.LcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static UnstructuredBytesMetadata deserialize(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        deserializer.increase_container_depth();
        Builder builder = new Builder();
        builder.metadata = TraitHelpers.deserialize_option_bytes(deserializer);
        deserializer.decrease_container_depth();
        return builder.build();
    }

    public static UnstructuredBytesMetadata lcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
             throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.lcs.LcsDeserializer(input);
        UnstructuredBytesMetadata value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new com.novi.serde.DeserializationError("Some input bytes were not read");
        }
        return value;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        UnstructuredBytesMetadata other = (UnstructuredBytesMetadata) obj;
        if (!java.util.Objects.equals(this.metadata, other.metadata)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.metadata != null ? this.metadata.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public java.util.Optional<com.novi.serde.Bytes> metadata;

        public UnstructuredBytesMetadata build() {
            return new UnstructuredBytesMetadata(
                metadata
            );
        }
    }
}
