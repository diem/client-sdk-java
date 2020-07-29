package org.libra.types;

import java.math.BigInteger;

public abstract class TransactionArgument {
    abstract public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception;

    public static TransactionArgument deserialize(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        TransactionArgument obj;
        int index = deserializer.deserialize_variant_index();
        switch (index) {
            case 0: return U8.load(deserializer);
            case 1: return U64.load(deserializer);
            case 2: return U128.load(deserializer);
            case 3: return Address.load(deserializer);
            case 4: return U8Vector.load(deserializer);
            case 5: return Bool.load(deserializer);
            default: throw new java.lang.Exception("Unknown variant index for TransactionArgument: " + index);
        }
    }

    public static final class U8 extends TransactionArgument {
        public final @com.facebook.serde.Unsigned Byte value;

        public U8(@com.facebook.serde.Unsigned Byte value) {
           assert value != null;
           this.value = value;
        }

        public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(0);
            serializer.serialize_u8(value);
        }

        static U8 load(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = deserializer.deserialize_u8();
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            U8 other = (U8) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public @com.facebook.serde.Unsigned Byte value;

            public U8 build() {
                return new U8(
                    value
                );
            }
        }
    }

    public static final class U64 extends TransactionArgument {
        public final @com.facebook.serde.Unsigned Long value;

        public U64(@com.facebook.serde.Unsigned Long value) {
           assert value != null;
           this.value = value;
        }

        public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(1);
            serializer.serialize_u64(value);
        }

        static U64 load(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = deserializer.deserialize_u64();
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            U64 other = (U64) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public @com.facebook.serde.Unsigned Long value;

            public U64 build() {
                return new U64(
                    value
                );
            }
        }
    }

    public static final class U128 extends TransactionArgument {
        public final @com.facebook.serde.Unsigned @com.facebook.serde.Int128 BigInteger value;

        public U128(@com.facebook.serde.Unsigned @com.facebook.serde.Int128 BigInteger value) {
           assert value != null;
           this.value = value;
        }

        public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(2);
            serializer.serialize_u128(value);
        }

        static U128 load(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = deserializer.deserialize_u128();
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            U128 other = (U128) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public @com.facebook.serde.Unsigned @com.facebook.serde.Int128 BigInteger value;

            public U128 build() {
                return new U128(
                    value
                );
            }
        }
    }

    public static final class Address extends TransactionArgument {
        public final org.libra.types.AccountAddress value;

        public Address(org.libra.types.AccountAddress value) {
           assert value != null;
           this.value = value;
        }

        public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(3);
            value.serialize(serializer);
        }

        static Address load(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = org.libra.types.AccountAddress.deserialize(deserializer);
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Address other = (Address) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public org.libra.types.AccountAddress value;

            public Address build() {
                return new Address(
                    value
                );
            }
        }
    }

    public static final class U8Vector extends TransactionArgument {
        public final com.facebook.serde.Bytes value;

        public U8Vector(com.facebook.serde.Bytes value) {
           assert value != null;
           this.value = value;
        }

        public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(4);
            serializer.serialize_bytes(value);
        }

        static U8Vector load(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = deserializer.deserialize_bytes();
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            U8Vector other = (U8Vector) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public com.facebook.serde.Bytes value;

            public U8Vector build() {
                return new U8Vector(
                    value
                );
            }
        }
    }

    public static final class Bool extends TransactionArgument {
        public final Boolean value;

        public Bool(Boolean value) {
           assert value != null;
           this.value = value;
        }

        public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(5);
            serializer.serialize_bool(value);
        }

        static Bool load(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = deserializer.deserialize_bool();
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Bool other = (Bool) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public Boolean value;

            public Bool build() {
                return new Bool(
                    value
                );
            }
        }
    }
}

