package org.libra.types;

import java.math.BigInteger;


public final class Module {
    public final com.novi.serde.Bytes code;

    public Module(com.novi.serde.Bytes code) {
        assert code != null;
        this.code = code;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception {
        serializer.serialize_bytes(code);
    }

    public byte[] lcsSerialize() throws java.lang.Exception {
        com.novi.serde.Serializer serializer = new com.novi.lcs.LcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static Module deserialize(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
        Builder builder = new Builder();
        builder.code = deserializer.deserialize_bytes();
        return builder.build();
    }

    public static Module lcsDeserialize(byte[] input) throws java.lang.Exception {
        com.novi.serde.Deserializer deserializer = new com.novi.lcs.LcsDeserializer(input);
        Module value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new Exception("Some input bytes were not read");
        }
        return value;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Module other = (Module) obj;
        if (!java.util.Objects.equals(this.code, other.code)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.code != null ? this.code.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public com.novi.serde.Bytes code;

        public Module build() {
            return new Module(
                code
            );
        }
    }
}
