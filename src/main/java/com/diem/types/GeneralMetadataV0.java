package com.diem.types;


public final class GeneralMetadataV0 {
    public final java.util.Optional<com.novi.serde.Bytes> to_subaddress;
    public final java.util.Optional<com.novi.serde.Bytes> from_subaddress;
    public final java.util.Optional<@com.novi.serde.Unsigned Long> referenced_event;

    public GeneralMetadataV0(java.util.Optional<com.novi.serde.Bytes> to_subaddress, java.util.Optional<com.novi.serde.Bytes> from_subaddress, java.util.Optional<@com.novi.serde.Unsigned Long> referenced_event) {
        java.util.Objects.requireNonNull(to_subaddress, "to_subaddress must not be null");
        java.util.Objects.requireNonNull(from_subaddress, "from_subaddress must not be null");
        java.util.Objects.requireNonNull(referenced_event, "referenced_event must not be null");
        this.to_subaddress = to_subaddress;
        this.from_subaddress = from_subaddress;
        this.referenced_event = referenced_event;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.increase_container_depth();
        TraitHelpers.serialize_option_bytes(to_subaddress, serializer);
        TraitHelpers.serialize_option_bytes(from_subaddress, serializer);
        TraitHelpers.serialize_option_u64(referenced_event, serializer);
        serializer.decrease_container_depth();
    }

    public byte[] lcsSerialize() throws com.novi.serde.SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.lcs.LcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static GeneralMetadataV0 deserialize(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        deserializer.increase_container_depth();
        Builder builder = new Builder();
        builder.to_subaddress = TraitHelpers.deserialize_option_bytes(deserializer);
        builder.from_subaddress = TraitHelpers.deserialize_option_bytes(deserializer);
        builder.referenced_event = TraitHelpers.deserialize_option_u64(deserializer);
        deserializer.decrease_container_depth();
        return builder.build();
    }

    public static GeneralMetadataV0 lcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
             throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.lcs.LcsDeserializer(input);
        GeneralMetadataV0 value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new com.novi.serde.DeserializationError("Some input bytes were not read");
        }
        return value;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        GeneralMetadataV0 other = (GeneralMetadataV0) obj;
        if (!java.util.Objects.equals(this.to_subaddress, other.to_subaddress)) { return false; }
        if (!java.util.Objects.equals(this.from_subaddress, other.from_subaddress)) { return false; }
        if (!java.util.Objects.equals(this.referenced_event, other.referenced_event)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.to_subaddress != null ? this.to_subaddress.hashCode() : 0);
        value = 31 * value + (this.from_subaddress != null ? this.from_subaddress.hashCode() : 0);
        value = 31 * value + (this.referenced_event != null ? this.referenced_event.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public java.util.Optional<com.novi.serde.Bytes> to_subaddress;
        public java.util.Optional<com.novi.serde.Bytes> from_subaddress;
        public java.util.Optional<@com.novi.serde.Unsigned Long> referenced_event;

        public GeneralMetadataV0 build() {
            return new GeneralMetadataV0(
                to_subaddress,
                from_subaddress,
                referenced_event
            );
        }
    }
}
