package org.libra.types;

import java.math.BigInteger;


public abstract class TypeTag {

    abstract public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception;

    public static TypeTag deserialize(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
        int index = deserializer.deserialize_variant_index();
        switch (index) {
            case 0: return Bool.load(deserializer);
            case 1: return U8.load(deserializer);
            case 2: return U64.load(deserializer);
            case 3: return U128.load(deserializer);
            case 4: return Address.load(deserializer);
            case 5: return Signer.load(deserializer);
            case 6: return Vector.load(deserializer);
            case 7: return Struct.load(deserializer);
            default: throw new java.lang.Exception("Unknown variant index for TypeTag: " + index);
        }
    }

    public byte[] lcsSerialize() throws java.lang.Exception {
        com.novi.serde.Serializer serializer = new com.novi.lcs.LcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static TypeTag lcsDeserialize(byte[] input) throws java.lang.Exception {
        com.novi.serde.Deserializer deserializer = new com.novi.lcs.LcsDeserializer(input);
        TypeTag value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new Exception("Some input bytes were not read");
        }
        return value;
    }

    public static final class Bool extends TypeTag {
        public Bool() {
        }

        public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(0);
        }

        static Bool load(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Bool other = (Bool) obj;
            return true;
        }

        public int hashCode() {
            int value = 7;
            return value;
        }

        public static final class Builder {
            public Bool build() {
                return new Bool(
                );
            }
        }
    }

    public static final class U8 extends TypeTag {
        public U8() {
        }

        public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(1);
        }

        static U8 load(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            U8 other = (U8) obj;
            return true;
        }

        public int hashCode() {
            int value = 7;
            return value;
        }

        public static final class Builder {
            public U8 build() {
                return new U8(
                );
            }
        }
    }

    public static final class U64 extends TypeTag {
        public U64() {
        }

        public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(2);
        }

        static U64 load(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            U64 other = (U64) obj;
            return true;
        }

        public int hashCode() {
            int value = 7;
            return value;
        }

        public static final class Builder {
            public U64 build() {
                return new U64(
                );
            }
        }
    }

    public static final class U128 extends TypeTag {
        public U128() {
        }

        public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(3);
        }

        static U128 load(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            U128 other = (U128) obj;
            return true;
        }

        public int hashCode() {
            int value = 7;
            return value;
        }

        public static final class Builder {
            public U128 build() {
                return new U128(
                );
            }
        }
    }

    public static final class Address extends TypeTag {
        public Address() {
        }

        public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(4);
        }

        static Address load(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Address other = (Address) obj;
            return true;
        }

        public int hashCode() {
            int value = 7;
            return value;
        }

        public static final class Builder {
            public Address build() {
                return new Address(
                );
            }
        }
    }

    public static final class Signer extends TypeTag {
        public Signer() {
        }

        public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(5);
        }

        static Signer load(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Signer other = (Signer) obj;
            return true;
        }

        public int hashCode() {
            int value = 7;
            return value;
        }

        public static final class Builder {
            public Signer build() {
                return new Signer(
                );
            }
        }
    }

    public static final class Vector extends TypeTag {
        public final TypeTag value;

        public Vector(TypeTag value) {
            assert value != null;
            this.value = value;
        }

        public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(6);
            value.serialize(serializer);
        }

        static Vector load(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = TypeTag.deserialize(deserializer);
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Vector other = (Vector) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public TypeTag value;

            public Vector build() {
                return new Vector(
                    value
                );
            }
        }
    }

    public static final class Struct extends TypeTag {
        public final StructTag value;

        public Struct(StructTag value) {
            assert value != null;
            this.value = value;
        }

        public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(7);
            value.serialize(serializer);
        }

        static Struct load(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = StructTag.deserialize(deserializer);
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Struct other = (Struct) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public StructTag value;

            public Struct build() {
                return new Struct(
                    value
                );
            }
        }
    }
}

