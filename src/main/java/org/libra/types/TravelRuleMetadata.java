package org.libra.types;

import java.math.BigInteger;

public abstract class TravelRuleMetadata {
    abstract public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception;

    public static TravelRuleMetadata deserialize(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        TravelRuleMetadata obj;
        int index = deserializer.deserialize_variant_index();
        switch (index) {
            case 0: return TravelRuleMetadataVersion0.load(deserializer);
            default: throw new java.lang.Exception("Unknown variant index for TravelRuleMetadata: " + index);
        }
    }

    public static final class TravelRuleMetadataVersion0 extends TravelRuleMetadata {
        public final TravelRuleMetadataV0 value;

        public TravelRuleMetadataVersion0(TravelRuleMetadataV0 value) {
            assert value != null;
            this.value = value;
        }

        public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(0);
            value.serialize(serializer);
        }

        static TravelRuleMetadataVersion0 load(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = TravelRuleMetadataV0.deserialize(deserializer);
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            TravelRuleMetadataVersion0 other = (TravelRuleMetadataVersion0) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public TravelRuleMetadataV0 value;

            public TravelRuleMetadataVersion0 build() {
                return new TravelRuleMetadataVersion0(
                    value
                );
            }
        }
    }
}

