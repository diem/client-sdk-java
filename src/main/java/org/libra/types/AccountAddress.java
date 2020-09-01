package org.libra.types;

import java.math.BigInteger;


public final class AccountAddress {
    public final @com.novi.serde.Unsigned Byte @com.novi.serde.ArrayLen(length=16) [] value;

    public AccountAddress(@com.novi.serde.Unsigned Byte @com.novi.serde.ArrayLen(length=16) [] value) {
        assert value != null;
        this.value = value;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception {
        TraitHelpers.serialize_array16_u8_array(value, serializer);
    }

    public byte[] lcsSerialize() throws java.lang.Exception {
        com.novi.serde.Serializer serializer = new com.novi.lcs.LcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static AccountAddress deserialize(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
        Builder builder = new Builder();
        builder.value = TraitHelpers.deserialize_array16_u8_array(deserializer);
        return builder.build();
    }

    public static AccountAddress lcsDeserialize(byte[] input) throws java.lang.Exception {
        com.novi.serde.Deserializer deserializer = new com.novi.lcs.LcsDeserializer(input);
        AccountAddress value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new Exception("Some input bytes were not read");
        }
        return value;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        AccountAddress other = (AccountAddress) obj;
        if (!java.util.Objects.equals(this.value, other.value)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public @com.novi.serde.Unsigned Byte @com.novi.serde.ArrayLen(length=16) [] value;

        public AccountAddress build() {
            return new AccountAddress(
                value
            );
        }
    }
}
