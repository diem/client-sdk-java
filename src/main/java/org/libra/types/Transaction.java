package org.libra.types;

import java.math.BigInteger;


public abstract class Transaction {

    abstract public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception;

    public static Transaction deserialize(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
        int index = deserializer.deserialize_variant_index();
        switch (index) {
            case 0: return UserTransaction.load(deserializer);
            case 1: return GenesisTransaction.load(deserializer);
            case 2: return BlockMetadata.load(deserializer);
            default: throw new java.lang.Exception("Unknown variant index for Transaction: " + index);
        }
    }

    public byte[] lcsSerialize() throws java.lang.Exception {
        com.novi.serde.Serializer serializer = new com.novi.lcs.LcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static Transaction lcsDeserialize(byte[] input) throws java.lang.Exception {
        com.novi.serde.Deserializer deserializer = new com.novi.lcs.LcsDeserializer(input);
        Transaction value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new Exception("Some input bytes were not read");
        }
        return value;
    }

    public static final class UserTransaction extends Transaction {
        public final SignedTransaction value;

        public UserTransaction(SignedTransaction value) {
            assert value != null;
            this.value = value;
        }

        public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(0);
            value.serialize(serializer);
        }

        static UserTransaction load(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = SignedTransaction.deserialize(deserializer);
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            UserTransaction other = (UserTransaction) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public SignedTransaction value;

            public UserTransaction build() {
                return new UserTransaction(
                    value
                );
            }
        }
    }

    public static final class GenesisTransaction extends Transaction {
        public final WriteSetPayload value;

        public GenesisTransaction(WriteSetPayload value) {
            assert value != null;
            this.value = value;
        }

        public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(1);
            value.serialize(serializer);
        }

        static GenesisTransaction load(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = WriteSetPayload.deserialize(deserializer);
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            GenesisTransaction other = (GenesisTransaction) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public WriteSetPayload value;

            public GenesisTransaction build() {
                return new GenesisTransaction(
                    value
                );
            }
        }
    }

    public static final class BlockMetadata extends Transaction {
        public final org.libra.types.BlockMetadata value;

        public BlockMetadata(org.libra.types.BlockMetadata value) {
            assert value != null;
            this.value = value;
        }

        public void serialize(com.novi.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(2);
            value.serialize(serializer);
        }

        static BlockMetadata load(com.novi.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = org.libra.types.BlockMetadata.deserialize(deserializer);
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            BlockMetadata other = (BlockMetadata) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public org.libra.types.BlockMetadata value;

            public BlockMetadata build() {
                return new BlockMetadata(
                    value
                );
            }
        }
    }
}

