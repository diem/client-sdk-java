package org.libra.types;

import java.math.BigInteger;

public final class WriteSet {
    public final WriteSetMut value;

    public WriteSet(WriteSetMut value) {
        assert value != null;
        this.value = value;
    }

    public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        value.serialize(serializer);
    }

    public static WriteSet deserialize(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        Builder builder = new Builder();
        builder.value = WriteSetMut.deserialize(deserializer);
        return builder.build();
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        WriteSet other = (WriteSet) obj;
        if (!java.util.Objects.equals(this.value, other.value)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public WriteSetMut value;

        public WriteSet build() {
            return new WriteSet(
                value
            );
        }
    }
}
