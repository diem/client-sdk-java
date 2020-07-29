package org.libra.types;

import java.math.BigInteger;

public abstract class WriteOp {
    abstract public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception;

    public static WriteOp deserialize(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        WriteOp obj;
        int index = deserializer.deserialize_variant_index();
        switch (index) {
            case 0: return Deletion.load(deserializer);
            case 1: return Value.load(deserializer);
            default: throw new java.lang.Exception("Unknown variant index for WriteOp: " + index);
        }
    }

    public static final class Deletion extends WriteOp {
        public Deletion() {
        }

        public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(0);
        }

        static Deletion load(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Deletion other = (Deletion) obj;
            return true;
        }

        public int hashCode() {
            int value = 7;
            return value;
        }

        public static final class Builder {
            public Deletion build() {
                return new Deletion(
                );
            }
        }
    }

    public static final class Value extends WriteOp {
        public final com.facebook.serde.Bytes value;

        public Value(com.facebook.serde.Bytes value) {
           assert value != null;
           this.value = value;
        }

        public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(1);
            serializer.serialize_bytes(value);
        }

        static Value load(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = deserializer.deserialize_bytes();
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Value other = (Value) obj;
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

            public Value build() {
                return new Value(
                    value
                );
            }
        }
    }
}

