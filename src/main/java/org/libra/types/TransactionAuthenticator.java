package org.libra.types;

import java.math.BigInteger;


public abstract class TransactionAuthenticator {

    abstract public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError;

    public static TransactionAuthenticator deserialize(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        int index = deserializer.deserialize_variant_index();
        switch (index) {
            case 0: return Ed25519.load(deserializer);
            case 1: return MultiEd25519.load(deserializer);
            default: throw new com.novi.serde.DeserializationError("Unknown variant index for TransactionAuthenticator: " + index);
        }
    }

    public byte[] lcsSerialize() throws com.novi.serde.SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.lcs.LcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static TransactionAuthenticator lcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
             throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.lcs.LcsDeserializer(input);
        TransactionAuthenticator value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new com.novi.serde.DeserializationError("Some input bytes were not read");
        }
        return value;
    }

    public static final class Ed25519 extends TransactionAuthenticator {
        public final Ed25519PublicKey public_key;
        public final Ed25519Signature signature;

        public Ed25519(Ed25519PublicKey public_key, Ed25519Signature signature) {
            assert public_key != null;
            assert signature != null;
            this.public_key = public_key;
            this.signature = signature;
        }

        public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
            serializer.increase_container_depth();
            serializer.serialize_variant_index(0);
            public_key.serialize(serializer);
            signature.serialize(serializer);
            serializer.decrease_container_depth();
        }

        static Ed25519 load(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
            deserializer.increase_container_depth();
            Builder builder = new Builder();
            builder.public_key = Ed25519PublicKey.deserialize(deserializer);
            builder.signature = Ed25519Signature.deserialize(deserializer);
            deserializer.decrease_container_depth();
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Ed25519 other = (Ed25519) obj;
            if (!java.util.Objects.equals(this.public_key, other.public_key)) { return false; }
            if (!java.util.Objects.equals(this.signature, other.signature)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.public_key != null ? this.public_key.hashCode() : 0);
            value = 31 * value + (this.signature != null ? this.signature.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public Ed25519PublicKey public_key;
            public Ed25519Signature signature;

            public Ed25519 build() {
                return new Ed25519(
                    public_key,
                    signature
                );
            }
        }
    }

    public static final class MultiEd25519 extends TransactionAuthenticator {
        public final MultiEd25519PublicKey public_key;
        public final MultiEd25519Signature signature;

        public MultiEd25519(MultiEd25519PublicKey public_key, MultiEd25519Signature signature) {
            assert public_key != null;
            assert signature != null;
            this.public_key = public_key;
            this.signature = signature;
        }

        public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
            serializer.increase_container_depth();
            serializer.serialize_variant_index(1);
            public_key.serialize(serializer);
            signature.serialize(serializer);
            serializer.decrease_container_depth();
        }

        static MultiEd25519 load(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
            deserializer.increase_container_depth();
            Builder builder = new Builder();
            builder.public_key = MultiEd25519PublicKey.deserialize(deserializer);
            builder.signature = MultiEd25519Signature.deserialize(deserializer);
            deserializer.decrease_container_depth();
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            MultiEd25519 other = (MultiEd25519) obj;
            if (!java.util.Objects.equals(this.public_key, other.public_key)) { return false; }
            if (!java.util.Objects.equals(this.signature, other.signature)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.public_key != null ? this.public_key.hashCode() : 0);
            value = 31 * value + (this.signature != null ? this.signature.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public MultiEd25519PublicKey public_key;
            public MultiEd25519Signature signature;

            public MultiEd25519 build() {
                return new MultiEd25519(
                    public_key,
                    signature
                );
            }
        }
    }
}

