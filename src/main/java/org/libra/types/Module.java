package org.libra.types;

import java.math.BigInteger;

public final class Module {
    public final com.facebook.serde.Bytes code;

    public Module(com.facebook.serde.Bytes code) {
       assert code != null;
       this.code = code;
    }

    public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        serializer.serialize_bytes(code);
    }

    public static Module deserialize(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        Builder builder = new Builder();
        builder.code = deserializer.deserialize_bytes();
        return builder.build();
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
        public com.facebook.serde.Bytes code;

        public Module build() {
            return new Module(
                code
            );
        }
    }
}
