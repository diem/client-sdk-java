package org.libra.types;

import java.math.BigInteger;


public final class ChangeSet {
    public final WriteSet write_set;
    public final java.util.List<ContractEvent> events;

    public ChangeSet(WriteSet write_set, java.util.List<ContractEvent> events) {
        java.util.Objects.requireNonNull(write_set, "write_set must not be null");
        java.util.Objects.requireNonNull(events, "events must not be null");
        this.write_set = write_set;
        this.events = events;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.increase_container_depth();
        write_set.serialize(serializer);
        TraitHelpers.serialize_vector_ContractEvent(events, serializer);
        serializer.decrease_container_depth();
    }

    public byte[] lcsSerialize() throws com.novi.serde.SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.lcs.LcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static ChangeSet deserialize(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        deserializer.increase_container_depth();
        Builder builder = new Builder();
        builder.write_set = WriteSet.deserialize(deserializer);
        builder.events = TraitHelpers.deserialize_vector_ContractEvent(deserializer);
        deserializer.decrease_container_depth();
        return builder.build();
    }

    public static ChangeSet lcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
             throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.lcs.LcsDeserializer(input);
        ChangeSet value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new com.novi.serde.DeserializationError("Some input bytes were not read");
        }
        return value;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ChangeSet other = (ChangeSet) obj;
        if (!java.util.Objects.equals(this.write_set, other.write_set)) { return false; }
        if (!java.util.Objects.equals(this.events, other.events)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.write_set != null ? this.write_set.hashCode() : 0);
        value = 31 * value + (this.events != null ? this.events.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public WriteSet write_set;
        public java.util.List<ContractEvent> events;

        public ChangeSet build() {
            return new ChangeSet(
                write_set,
                events
            );
        }
    }
}
