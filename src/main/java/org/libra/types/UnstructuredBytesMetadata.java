package org.libra.types;

import java.math.BigInteger;

public final class UnstructuredBytesMetadata {
    public final java.util.Optional<com.facebook.serde.Bytes> metadata;

    public UnstructuredBytesMetadata(java.util.Optional<com.facebook.serde.Bytes> metadata) {
        assert metadata != null;
        this.metadata = metadata;
    }

    public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        TraitHelpers.serialize_option_bytes(metadata, serializer);
    }

    public static UnstructuredBytesMetadata deserialize(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        Builder builder = new Builder();
        builder.metadata = TraitHelpers.deserialize_option_bytes(deserializer);
        return builder.build();
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        UnstructuredBytesMetadata other = (UnstructuredBytesMetadata) obj;
        if (!java.util.Objects.equals(this.metadata, other.metadata)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.metadata != null ? this.metadata.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public java.util.Optional<com.facebook.serde.Bytes> metadata;

        public UnstructuredBytesMetadata build() {
            return new UnstructuredBytesMetadata(
                metadata
            );
        }
    }
}
