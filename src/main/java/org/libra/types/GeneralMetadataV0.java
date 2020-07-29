package org.libra.types;

import java.math.BigInteger;

public final class GeneralMetadataV0 {
    public final java.util.Optional<com.facebook.serde.Bytes> to_subaddress;
    public final java.util.Optional<com.facebook.serde.Bytes> from_subaddress;
    public final java.util.Optional<@com.facebook.serde.Unsigned Long> referenced_event;

    public GeneralMetadataV0(java.util.Optional<com.facebook.serde.Bytes> to_subaddress, java.util.Optional<com.facebook.serde.Bytes> from_subaddress, java.util.Optional<@com.facebook.serde.Unsigned Long> referenced_event) {
       assert to_subaddress != null;
       assert from_subaddress != null;
       assert referenced_event != null;
       this.to_subaddress = to_subaddress;
       this.from_subaddress = from_subaddress;
       this.referenced_event = referenced_event;
    }

    public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        TraitHelpers.serialize_option_bytes(to_subaddress, serializer);
        TraitHelpers.serialize_option_bytes(from_subaddress, serializer);
        TraitHelpers.serialize_option_u64(referenced_event, serializer);
    }

    public static GeneralMetadataV0 deserialize(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        Builder builder = new Builder();
        builder.to_subaddress = TraitHelpers.deserialize_option_bytes(deserializer);
        builder.from_subaddress = TraitHelpers.deserialize_option_bytes(deserializer);
        builder.referenced_event = TraitHelpers.deserialize_option_u64(deserializer);
        return builder.build();
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        GeneralMetadataV0 other = (GeneralMetadataV0) obj;
        if (!java.util.Objects.equals(this.to_subaddress, other.to_subaddress)) { return false; }
        if (!java.util.Objects.equals(this.from_subaddress, other.from_subaddress)) { return false; }
        if (!java.util.Objects.equals(this.referenced_event, other.referenced_event)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.to_subaddress != null ? this.to_subaddress.hashCode() : 0);
        value = 31 * value + (this.from_subaddress != null ? this.from_subaddress.hashCode() : 0);
        value = 31 * value + (this.referenced_event != null ? this.referenced_event.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public java.util.Optional<com.facebook.serde.Bytes> to_subaddress;
        public java.util.Optional<com.facebook.serde.Bytes> from_subaddress;
        public java.util.Optional<@com.facebook.serde.Unsigned Long> referenced_event;

        public GeneralMetadataV0 build() {
            return new GeneralMetadataV0(
                to_subaddress,
                from_subaddress,
                referenced_event
            );
        }
    }
}
