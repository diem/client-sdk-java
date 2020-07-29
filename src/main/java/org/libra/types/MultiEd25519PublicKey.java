package org.libra.types;

import java.math.BigInteger;

public final class MultiEd25519PublicKey {
    public final com.facebook.serde.Bytes value;

    public MultiEd25519PublicKey(com.facebook.serde.Bytes value) {
       assert value != null;
       this.value = value;
    }

    public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        serializer.serialize_bytes(value);
    }

    public static MultiEd25519PublicKey deserialize(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        Builder builder = new Builder();
        builder.value = deserializer.deserialize_bytes();
        return builder.build();
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MultiEd25519PublicKey other = (MultiEd25519PublicKey) obj;
        if (!java.util.Objects.equals(this.value, other.value)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public com.facebook.serde.Bytes value;

        public MultiEd25519PublicKey build() {
            return new MultiEd25519PublicKey(
                value
            );
        }
    }
}
