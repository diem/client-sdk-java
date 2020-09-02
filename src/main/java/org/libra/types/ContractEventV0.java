package org.libra.types;

import java.math.BigInteger;


public final class ContractEventV0 {
    public final EventKey key;
    public final @com.novi.serde.Unsigned Long sequence_number;
    public final TypeTag type_tag;
    public final com.novi.serde.Bytes event_data;

    public ContractEventV0(EventKey key, @com.novi.serde.Unsigned Long sequence_number, TypeTag type_tag, com.novi.serde.Bytes event_data) {
        assert key != null;
        assert sequence_number != null;
        assert type_tag != null;
        assert event_data != null;
        this.key = key;
        this.sequence_number = sequence_number;
        this.type_tag = type_tag;
        this.event_data = event_data;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception {
        key.serialize(serializer);
        serializer.serialize_u64(sequence_number);
        type_tag.serialize(serializer);
        serializer.serialize_bytes(event_data);
    }

    public byte[] lcsSerialize() throws java.lang.Exception {
        com.novi.serde.Serializer serializer = new com.novi.lcs.LcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static ContractEventV0 deserialize(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
        Builder builder = new Builder();
        builder.key = EventKey.deserialize(deserializer);
        builder.sequence_number = deserializer.deserialize_u64();
        builder.type_tag = TypeTag.deserialize(deserializer);
        builder.event_data = deserializer.deserialize_bytes();
        return builder.build();
    }

    public static ContractEventV0 lcsDeserialize(byte[] input) throws java.lang.Exception {
        com.novi.serde.Deserializer deserializer = new com.novi.lcs.LcsDeserializer(input);
        ContractEventV0 value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new Exception("Some input bytes were not read");
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
