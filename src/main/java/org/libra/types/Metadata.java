package org.libra.types;

import java.math.BigInteger;

public abstract class Metadata {
    abstract public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception;

    public static Metadata deserialize(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        Metadata obj;
        int index = deserializer.deserialize_variant_index();
        switch (index) {
            case 0: return Undefined.load(deserializer);
            case 1: return GeneralMetadata.load(deserializer);
            case 2: return TravelRuleMetadata.load(deserializer);
            case 3: return UnstructuredBytesMetadata.load(deserializer);
            default: throw new java.lang.Exception("Unknown variant index for Metadata: " + index);
        }
    }

    public static final class Undefined extends Metadata {
        public Undefined() {
        }

        public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(0);
        }

        static Undefined load(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Undefined other = (Undefined) obj;
            return true;
        }

        public int hashCode() {
            int value = 7;
            return value;
        }

        public static final class Builder {
            public Undefined build() {
                return new Undefined(
                );
            }
        }
    }

    public static final class GeneralMetadata extends Metadata {
        public final org.libra.types.GeneralMetadata value;

        public GeneralMetadata(org.libra.types.GeneralMetadata value) {
            assert value != null;
            this.value = value;
        }

        public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(1);
            value.serialize(serializer);
        }

        static GeneralMetadata load(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = org.libra.types.GeneralMetadata.deserialize(deserializer);
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            GeneralMetadata other = (GeneralMetadata) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public org.libra.types.GeneralMetadata value;

            public GeneralMetadata build() {
                return new GeneralMetadata(
                    value
                );
            }
        }
    }

    public static final class TravelRuleMetadata extends Metadata {
        public final org.libra.types.TravelRuleMetadata value;

        public TravelRuleMetadata(org.libra.types.TravelRuleMetadata value) {
            assert value != null;
            this.value = value;
        }

        public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(2);
            value.serialize(serializer);
        }

        static TravelRuleMetadata load(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = org.libra.types.TravelRuleMetadata.deserialize(deserializer);
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            TravelRuleMetadata other = (TravelRuleMetadata) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public org.libra.types.TravelRuleMetadata value;

            public TravelRuleMetadata build() {
                return new TravelRuleMetadata(
                    value
                );
            }
        }
    }

    public static final class UnstructuredBytesMetadata extends Metadata {
        public final org.libra.types.UnstructuredBytesMetadata value;

        public UnstructuredBytesMetadata(org.libra.types.UnstructuredBytesMetadata value) {
            assert value != null;
            this.value = value;
        }

        public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(3);
            value.serialize(serializer);
        }

        static UnstructuredBytesMetadata load(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = org.libra.types.UnstructuredBytesMetadata.deserialize(deserializer);
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            UnstructuredBytesMetadata other = (UnstructuredBytesMetadata) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public org.libra.types.UnstructuredBytesMetadata value;

            public UnstructuredBytesMetadata build() {
                return new UnstructuredBytesMetadata(
                    value
                );
            }
        }
    }
}

