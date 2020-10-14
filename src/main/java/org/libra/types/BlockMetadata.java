package org.libra.types;

import java.math.BigInteger;


public final class BlockMetadata {
    public final HashValue id;
    public final @com.novi.serde.Unsigned Long round;
    public final @com.novi.serde.Unsigned Long timestamp_usecs;
    public final java.util.List<AccountAddress> previous_block_votes;
    public final AccountAddress proposer;

    public BlockMetadata(HashValue id, @com.novi.serde.Unsigned Long round, @com.novi.serde.Unsigned Long timestamp_usecs, java.util.List<AccountAddress> previous_block_votes, AccountAddress proposer) {
        java.util.Objects.requireNonNull(id, "id must not be null");
        java.util.Objects.requireNonNull(round, "round must not be null");
        java.util.Objects.requireNonNull(timestamp_usecs, "timestamp_usecs must not be null");
        java.util.Objects.requireNonNull(previous_block_votes, "previous_block_votes must not be null");
        java.util.Objects.requireNonNull(proposer, "proposer must not be null");
        this.id = id;
        this.round = round;
        this.timestamp_usecs = timestamp_usecs;
        this.previous_block_votes = previous_block_votes;
        this.proposer = proposer;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.increase_container_depth();
        id.serialize(serializer);
        serializer.serialize_u64(round);
        serializer.serialize_u64(timestamp_usecs);
        TraitHelpers.serialize_vector_AccountAddress(previous_block_votes, serializer);
        proposer.serialize(serializer);
        serializer.decrease_container_depth();
    }

    public byte[] lcsSerialize() throws com.novi.serde.SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.lcs.LcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static BlockMetadata deserialize(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        deserializer.increase_container_depth();
        Builder builder = new Builder();
        builder.id = HashValue.deserialize(deserializer);
        builder.round = deserializer.deserialize_u64();
        builder.timestamp_usecs = deserializer.deserialize_u64();
        builder.previous_block_votes = TraitHelpers.deserialize_vector_AccountAddress(deserializer);
        builder.proposer = AccountAddress.deserialize(deserializer);
        deserializer.decrease_container_depth();
        return builder.build();
    }

    public static BlockMetadata lcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
             throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.lcs.LcsDeserializer(input);
        BlockMetadata value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new com.novi.serde.DeserializationError("Some input bytes were not read");
        }
        return value;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BlockMetadata other = (BlockMetadata) obj;
        if (!java.util.Objects.equals(this.id, other.id)) { return false; }
        if (!java.util.Objects.equals(this.round, other.round)) { return false; }
        if (!java.util.Objects.equals(this.timestamp_usecs, other.timestamp_usecs)) { return false; }
        if (!java.util.Objects.equals(this.previous_block_votes, other.previous_block_votes)) { return false; }
        if (!java.util.Objects.equals(this.proposer, other.proposer)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.id != null ? this.id.hashCode() : 0);
        value = 31 * value + (this.round != null ? this.round.hashCode() : 0);
        value = 31 * value + (this.timestamp_usecs != null ? this.timestamp_usecs.hashCode() : 0);
        value = 31 * value + (this.previous_block_votes != null ? this.previous_block_votes.hashCode() : 0);
        value = 31 * value + (this.proposer != null ? this.proposer.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public HashValue id;
        public @com.novi.serde.Unsigned Long round;
        public @com.novi.serde.Unsigned Long timestamp_usecs;
        public java.util.List<AccountAddress> previous_block_votes;
        public AccountAddress proposer;

        public BlockMetadata build() {
            return new BlockMetadata(
                id,
                round,
                timestamp_usecs,
                previous_block_votes,
                proposer
            );
        }
    }
}
