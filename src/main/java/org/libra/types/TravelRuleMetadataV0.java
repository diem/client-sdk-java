package org.libra.types;

import java.math.BigInteger;


public final class TravelRuleMetadataV0 {
    public final java.util.Optional<String> off_chain_reference_id;

    public TravelRuleMetadataV0(java.util.Optional<String> off_chain_reference_id) {
        assert off_chain_reference_id != null;
        this.off_chain_reference_id = off_chain_reference_id;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception {
        TraitHelpers.serialize_option_str(off_chain_reference_id, serializer);
    }

    public byte[] lcsSerialize() throws java.lang.Exception {
        com.novi.serde.Serializer serializer = new com.novi.lcs.LcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static TravelRuleMetadataV0 deserialize(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
        Builder builder = new Builder();
        builder.off_chain_reference_id = TraitHelpers.deserialize_option_str(deserializer);
        return builder.build();
    }

    public static TravelRuleMetadataV0 lcsDeserialize(byte[] input) throws java.lang.Exception {
        com.novi.serde.Deserializer deserializer = new com.novi.lcs.LcsDeserializer(input);
        TravelRuleMetadataV0 value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new Exception("Some input bytes were not read");
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
