package com.diem.types;


public final class ContractEventV0 {
    public final EventKey key;
    public final @com.novi.serde.Unsigned Long sequence_number;
    public final TypeTag type_tag;
    public final com.novi.serde.Bytes event_data;

    public ContractEventV0(EventKey key, @com.novi.serde.Unsigned Long sequence_number, TypeTag type_tag, com.novi.serde.Bytes event_data) {
        java.util.Objects.requireNonNull(key, "key must not be null");
        java.util.Objects.requireNonNull(sequence_number, "sequence_number must not be null");
        java.util.Objects.requireNonNull(type_tag, "type_tag must not be null");
        java.util.Objects.requireNonNull(event_data, "event_data must not be null");
        this.key = key;
        this.sequence_number = sequence_number;
        this.type_tag = type_tag;
        this.event_data = event_data;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.increase_container_depth();
        key.serialize(serializer);
        serializer.serialize_u64(sequence_number);
        type_tag.serialize(serializer);
        serializer.serialize_bytes(event_data);
        serializer.decrease_container_depth();
    }

    public byte[] bcsSerialize() throws com.novi.serde.SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.bcs.BcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static ContractEventV0 deserialize(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        deserializer.increase_container_depth();
        Builder builder = new Builder();
        builder.key = EventKey.deserialize(deserializer);
        builder.sequence_number = deserializer.deserialize_u64();
        builder.type_tag = TypeTag.deserialize(deserializer);
        builder.event_data = deserializer.deserialize_bytes();
        deserializer.decrease_container_depth();
        return builder.build();
    }

    public static ContractEventV0 bcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
             throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.bcs.BcsDeserializer(input);
        ContractEventV0 value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new com.novi.serde.DeserializationError("Some input bytes were not read");
        }
        return value;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ContractEventV0 other = (ContractEventV0) obj;
        if (!java.util.Objects.equals(this.key, other.key)) { return false; }
        if (!java.util.Objects.equals(this.sequence_number, other.sequence_number)) { return false; }
        if (!java.util.Objects.equals(this.type_tag, other.type_tag)) { return false; }
        if (!java.util.Objects.equals(this.event_data, other.event_data)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.key != null ? this.key.hashCode() : 0);
        value = 31 * value + (this.sequence_number != null ? this.sequence_number.hashCode() : 0);
        value = 31 * value + (this.type_tag != null ? this.type_tag.hashCode() : 0);
        value = 31 * value + (this.event_data != null ? this.event_data.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public EventKey key;
        public @com.novi.serde.Unsigned Long sequence_number;
        public TypeTag type_tag;
        public com.novi.serde.Bytes event_data;

        public ContractEventV0 build() {
            return new ContractEventV0(
                key,
                sequence_number,
                type_tag,
                event_data
            );
        }
    }
}
