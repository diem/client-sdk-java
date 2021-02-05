package com.diem.types;


public final class RefundMetadataV0 {
    public final @com.novi.serde.Unsigned Long transaction_version;
    public final RefundReason reason;

    public RefundMetadataV0(@com.novi.serde.Unsigned Long transaction_version, RefundReason reason) {
        java.util.Objects.requireNonNull(transaction_version, "transaction_version must not be null");
        java.util.Objects.requireNonNull(reason, "reason must not be null");
        this.transaction_version = transaction_version;
        this.reason = reason;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.increase_container_depth();
        serializer.serialize_u64(transaction_version);
        reason.serialize(serializer);
        serializer.decrease_container_depth();
    }

    public byte[] bcsSerialize() throws com.novi.serde.SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.bcs.BcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static RefundMetadataV0 deserialize(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        deserializer.increase_container_depth();
        Builder builder = new Builder();
        builder.transaction_version = deserializer.deserialize_u64();
        builder.reason = RefundReason.deserialize(deserializer);
        deserializer.decrease_container_depth();
        return builder.build();
    }

    public static RefundMetadataV0 bcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
             throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.bcs.BcsDeserializer(input);
        RefundMetadataV0 value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new com.novi.serde.DeserializationError("Some input bytes were not read");
        }
        return value;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        RefundMetadataV0 other = (RefundMetadataV0) obj;
        if (!java.util.Objects.equals(this.transaction_version, other.transaction_version)) { return false; }
        if (!java.util.Objects.equals(this.reason, other.reason)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.transaction_version != null ? this.transaction_version.hashCode() : 0);
        value = 31 * value + (this.reason != null ? this.reason.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public @com.novi.serde.Unsigned Long transaction_version;
        public RefundReason reason;

        public RefundMetadataV0 build() {
            return new RefundMetadataV0(
                transaction_version,
                reason
            );
        }
    }
}
