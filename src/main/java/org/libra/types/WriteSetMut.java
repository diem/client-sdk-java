package org.libra.types;

import java.math.BigInteger;


public final class WriteSetMut {
    public final java.util.List<com.novi.serde.Tuple2<AccessPath, WriteOp>> write_set;

    public WriteSetMut(java.util.List<com.novi.serde.Tuple2<AccessPath, WriteOp>> write_set) {
        assert write_set != null;
        this.write_set = write_set;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.increase_container_depth();
        TraitHelpers.serialize_vector_tuple2_AccessPath_WriteOp(write_set, serializer);
        serializer.decrease_container_depth();
    }

    public byte[] lcsSerialize() throws com.novi.serde.SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.lcs.LcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static WriteSetMut deserialize(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        deserializer.increase_container_depth();
        Builder builder = new Builder();
        builder.write_set = TraitHelpers.deserialize_vector_tuple2_AccessPath_WriteOp(deserializer);
        deserializer.decrease_container_depth();
        return builder.build();
    }

    public static WriteSetMut lcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
             throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.lcs.LcsDeserializer(input);
        WriteSetMut value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new com.novi.serde.DeserializationError("Some input bytes were not read");
        }
        return value;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        WriteSetMut other = (WriteSetMut) obj;
        if (!java.util.Objects.equals(this.write_set, other.write_set)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.write_set != null ? this.write_set.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public java.util.List<com.novi.serde.Tuple2<AccessPath, WriteOp>> write_set;

        public WriteSetMut build() {
            return new WriteSetMut(
                write_set
            );
        }
    }
}
