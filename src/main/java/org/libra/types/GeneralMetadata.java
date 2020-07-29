package org.libra.types;

import java.math.BigInteger;

public abstract class GeneralMetadata {
    abstract public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception;

    public static GeneralMetadata deserialize(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        GeneralMetadata obj;
        int index = deserializer.deserialize_variant_index();
        switch (index) {
            case 0: return GeneralMetadataVersion0.load(deserializer);
            default: throw new java.lang.Exception("Unknown variant index for GeneralMetadata: " + index);
        }
    }

    public static final class GeneralMetadataVersion0 extends GeneralMetadata {
        public final org.libra.types.GeneralMetadataV0 value;

        public GeneralMetadataVersion0(org.libra.types.GeneralMetadataV0 value) {
           assert value != null;
           this.value = value;
        }

        public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(0);
            value.serialize(serializer);
        }

        static GeneralMetadataVersion0 load(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = org.libra.types.GeneralMetadataV0.deserialize(deserializer);
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
            public org.libra.types.GeneralMetadataV0 value;

            public GeneralMetadataVersion0 build() {
                return new GeneralMetadataVersion0(
                    value
                );
            }
        }
    }
}

