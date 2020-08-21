package org.libra.types;

import java.math.BigInteger;

public final class AccountAddress {
    public final @com.facebook.serde.Unsigned Byte @com.facebook.serde.ArrayLen(length=16) [] value;

    public AccountAddress(@com.facebook.serde.Unsigned Byte @com.facebook.serde.ArrayLen(length=16) [] value) {
        assert value != null;
        this.value = value;
    }

    public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        TraitHelpers.serialize_array16_u8_array(value, serializer);
    }

    public static AccountAddress deserialize(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        Builder builder = new Builder();
        builder.value = TraitHelpers.deserialize_array16_u8_array(deserializer);
        return builder.build();
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
        public @com.facebook.serde.Unsigned Byte @com.facebook.serde.ArrayLen(length=16) [] value;

        public AccountAddress build() {
            return new AccountAddress(
                value
            );
        }
    }
}
