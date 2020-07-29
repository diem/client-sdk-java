package org.libra.types;

import java.math.BigInteger;

public final class BlockMetadata {
    public final org.libra.types.HashValue id;
    public final @com.facebook.serde.Unsigned Long round;
    public final @com.facebook.serde.Unsigned Long timestamp_usecs;
    public final java.util.List<org.libra.types.AccountAddress> previous_block_votes;
    public final org.libra.types.AccountAddress proposer;

    public BlockMetadata(org.libra.types.HashValue id, @com.facebook.serde.Unsigned Long round, @com.facebook.serde.Unsigned Long timestamp_usecs, java.util.List<org.libra.types.AccountAddress> previous_block_votes, org.libra.types.AccountAddress proposer) {
       assert id != null;
       assert round != null;
       assert timestamp_usecs != null;
       assert previous_block_votes != null;
       assert proposer != null;
       this.id = id;
       this.round = round;
       this.timestamp_usecs = timestamp_usecs;
       this.previous_block_votes = previous_block_votes;
       this.proposer = proposer;
    }

    public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        id.serialize(serializer);
        serializer.serialize_u64(round);
        serializer.serialize_u64(timestamp_usecs);
        TraitHelpers.serialize_vector_AccountAddress(previous_block_votes, serializer);
        proposer.serialize(serializer);
    }

    public static BlockMetadata deserialize(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        Builder builder = new Builder();
        builder.id = org.libra.types.HashValue.deserialize(deserializer);
        builder.round = deserializer.deserialize_u64();
        builder.timestamp_usecs = deserializer.deserialize_u64();
        builder.previous_block_votes = TraitHelpers.deserialize_vector_AccountAddress(deserializer);
        builder.proposer = org.libra.types.AccountAddress.deserialize(deserializer);
        return builder.build();
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
        public org.libra.types.HashValue id;
        public @com.facebook.serde.Unsigned Long round;
        public @com.facebook.serde.Unsigned Long timestamp_usecs;
        public java.util.List<org.libra.types.AccountAddress> previous_block_votes;
        public org.libra.types.AccountAddress proposer;

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
