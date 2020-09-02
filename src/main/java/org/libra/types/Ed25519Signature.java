package org.libra.types;

import java.math.BigInteger;


public final class Ed25519Signature {
    public final com.novi.serde.Bytes value;

    public Ed25519Signature(com.novi.serde.Bytes value) {
        assert value != null;
        this.value = value;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception {
        serializer.serialize_bytes(value);
    }

    public byte[] lcsSerialize() throws java.lang.Exception {
        com.novi.serde.Serializer serializer = new com.novi.lcs.LcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static Ed25519Signature deserialize(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
        Builder builder = new Builder();
        builder.value = deserializer.deserialize_bytes();
        return builder.build();
    }

    public static Ed25519Signature lcsDeserialize(byte[] input) throws java.lang.Exception {
        com.novi.serde.Deserializer deserializer = new com.novi.lcs.LcsDeserializer(input);
        Ed25519Signature value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new Exception("Some input bytes were not read");
        }
        return value;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Ed25519Signature other = (Ed25519Signature) obj;
        if (!java.util.Objects.equals(this.value, other.value)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public com.novi.serde.Bytes value;

        public Ed25519Signature build() {
            return new Ed25519Signature(
                value
            );
        }
    }
}
