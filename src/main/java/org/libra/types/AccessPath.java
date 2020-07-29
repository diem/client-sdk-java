package org.libra.types;

import java.math.BigInteger;

public final class AccessPath {
    public final org.libra.types.AccountAddress address;
    public final com.facebook.serde.Bytes path;

    public AccessPath(org.libra.types.AccountAddress address, com.facebook.serde.Bytes path) {
       assert address != null;
       assert path != null;
       this.address = address;
       this.path = path;
    }

    public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        address.serialize(serializer);
        serializer.serialize_bytes(path);
    }

    public static AccessPath deserialize(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        Builder builder = new Builder();
        builder.address = org.libra.types.AccountAddress.deserialize(deserializer);
        builder.path = deserializer.deserialize_bytes();
        return builder.build();
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        AccessPath other = (AccessPath) obj;
        if (!java.util.Objects.equals(this.address, other.address)) { return false; }
        if (!java.util.Objects.equals(this.path, other.path)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.address != null ? this.address.hashCode() : 0);
        value = 31 * value + (this.path != null ? this.path.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public org.libra.types.AccountAddress address;
        public com.facebook.serde.Bytes path;

        public AccessPath build() {
            return new AccessPath(
                address,
                path
            );
        }
    }
}
