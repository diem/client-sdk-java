package org.libra.types;

import java.math.BigInteger;

public abstract class TransactionPayload {
    abstract public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception;

    public static TransactionPayload deserialize(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        TransactionPayload obj;
        int index = deserializer.deserialize_variant_index();
        switch (index) {
            case 0: return WriteSet.load(deserializer);
            case 1: return Script.load(deserializer);
            case 2: return Module.load(deserializer);
            default: throw new java.lang.Exception("Unknown variant index for TransactionPayload: " + index);
        }
    }

    public static final class WriteSet extends TransactionPayload {
        public final WriteSetPayload value;

        public WriteSet(WriteSetPayload value) {
            assert value != null;
            this.value = value;
        }

        public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(0);
            value.serialize(serializer);
        }

        static WriteSet load(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = WriteSetPayload.deserialize(deserializer);
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            WriteSet other = (WriteSet) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public WriteSetPayload value;

            public WriteSet build() {
                return new WriteSet(
                    value
                );
            }
        }
    }

    public static final class Script extends TransactionPayload {
        public final org.libra.types.Script value;

        public Script(org.libra.types.Script value) {
            assert value != null;
            this.value = value;
        }

        public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(1);
            value.serialize(serializer);
        }

        static Script load(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = org.libra.types.Script.deserialize(deserializer);
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Script other = (Script) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public org.libra.types.Script value;

            public Script build() {
                return new Script(
                    value
                );
            }
        }
    }

    public static final class Module extends TransactionPayload {
        public final org.libra.types.Module value;

        public Module(org.libra.types.Module value) {
            assert value != null;
            this.value = value;
        }

        public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(2);
            value.serialize(serializer);
        }

        static Module load(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = org.libra.types.Module.deserialize(deserializer);
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Module other = (Module) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public org.libra.types.Module value;

            public Module build() {
                return new Module(
                    value
                );
            }
        }
    }
}

