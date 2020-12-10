package com.diem.types;


public final class TravelRuleMetadataV0 {
    public final java.util.Optional<String> off_chain_reference_id;

    public TravelRuleMetadataV0(java.util.Optional<String> off_chain_reference_id) {
        java.util.Objects.requireNonNull(off_chain_reference_id, "off_chain_reference_id must not be null");
        this.off_chain_reference_id = off_chain_reference_id;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.increase_container_depth();
        TraitHelpers.serialize_option_str(off_chain_reference_id, serializer);
        serializer.decrease_container_depth();
    }

    public byte[] lcsSerialize() throws com.novi.serde.SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.lcs.LcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static TravelRuleMetadataV0 deserialize(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        deserializer.increase_container_depth();
        Builder builder = new Builder();
        builder.off_chain_reference_id = TraitHelpers.deserialize_option_str(deserializer);
        deserializer.decrease_container_depth();
        return builder.build();
    }

    public static TravelRuleMetadataV0 lcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
             throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.lcs.LcsDeserializer(input);
        TravelRuleMetadataV0 value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new com.novi.serde.DeserializationError("Some input bytes were not read");
        }
        return value;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TravelRuleMetadataV0 other = (TravelRuleMetadataV0) obj;
        if (!java.util.Objects.equals(this.off_chain_reference_id, other.off_chain_reference_id)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.off_chain_reference_id != null ? this.off_chain_reference_id.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public java.util.Optional<String> off_chain_reference_id;

        public TravelRuleMetadataV0 build() {
            return new TravelRuleMetadataV0(
                off_chain_reference_id
            );
        }
    }
}
