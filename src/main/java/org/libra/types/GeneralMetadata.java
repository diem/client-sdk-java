package org.libra.types;

import java.math.BigInteger;


public abstract class GeneralMetadata {

    abstract public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception;

    public static GeneralMetadata deserialize(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
        int index = deserializer.deserialize_variant_index();
        switch (index) {
            case 0: return GeneralMetadataVersion0.load(deserializer);
            default: throw new java.lang.Exception("Unknown variant index for GeneralMetadata: " + index);
        }
    }

    public byte[] lcsSerialize() throws java.lang.Exception {
        com.novi.serde.Serializer serializer = new com.novi.lcs.LcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static GeneralMetadata lcsDeserialize(byte[] input) throws java.lang.Exception {
        com.novi.serde.Deserializer deserializer = new com.novi.lcs.LcsDeserializer(input);
        GeneralMetadata value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new Exception("Some input bytes were not read");
        }
        return value;
    }

    public static final class GeneralMetadataVersion0 extends GeneralMetadata {
        public final GeneralMetadataV0 value;

        public GeneralMetadataVersion0(GeneralMetadataV0 value) {
            assert value != null;
            this.value = value;
        }

        public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(0);
            value.serialize(serializer);
        }

        static GeneralMetadataVersion0 load(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = GeneralMetadataV0.deserialize(deserializer);
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            GeneralMetadataVersion0 other = (GeneralMetadataVersion0) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public GeneralMetadataV0 value;

            public GeneralMetadataVersion0 build() {
                return new GeneralMetadataVersion0(
                    value
                );
            }
        }
    }
}

