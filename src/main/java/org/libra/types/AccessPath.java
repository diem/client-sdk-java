package org.libra.types;

import java.math.BigInteger;


public final class AccessPath {
    public final AccountAddress address;
    public final com.novi.serde.Bytes path;

    public AccessPath(AccountAddress address, com.novi.serde.Bytes path) {
        assert address != null;
        assert path != null;
        this.address = address;
        this.path = path;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception {
        address.serialize(serializer);
        serializer.serialize_bytes(path);
    }

    public byte[] lcsSerialize() throws java.lang.Exception {
        com.novi.serde.Serializer serializer = new com.novi.lcs.LcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static AccessPath deserialize(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
        Builder builder = new Builder();
        builder.address = AccountAddress.deserialize(deserializer);
        builder.path = deserializer.deserialize_bytes();
        return builder.build();
    }

    public static AccessPath lcsDeserialize(byte[] input) throws java.lang.Exception {
        com.novi.serde.Deserializer deserializer = new com.novi.lcs.LcsDeserializer(input);
        AccessPath value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new Exception("Some input bytes were not read");
        }
        return value;
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
        public AccountAddress address;
        public com.novi.serde.Bytes path;

        public AccessPath build() {
            return new AccessPath(
                address,
                path
            );
        }
    }
}
