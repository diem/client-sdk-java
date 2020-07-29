package org.libra.types;

import java.math.BigInteger;

public final class ChangeSet {
    public final org.libra.types.WriteSet write_set;
    public final java.util.List<org.libra.types.ContractEvent> events;

    public ChangeSet(org.libra.types.WriteSet write_set, java.util.List<org.libra.types.ContractEvent> events) {
       assert write_set != null;
       assert events != null;
       this.write_set = write_set;
       this.events = events;
    }

    public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        write_set.serialize(serializer);
        TraitHelpers.serialize_vector_ContractEvent(events, serializer);
    }

    public static ChangeSet deserialize(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        Builder builder = new Builder();
        builder.write_set = org.libra.types.WriteSet.deserialize(deserializer);
        builder.events = TraitHelpers.deserialize_vector_ContractEvent(deserializer);
        return builder.build();
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ChangeSet other = (ChangeSet) obj;
        if (!java.util.Objects.equals(this.write_set, other.write_set)) { return false; }
        if (!java.util.Objects.equals(this.events, other.events)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.write_set != null ? this.write_set.hashCode() : 0);
        value = 31 * value + (this.events != null ? this.events.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public org.libra.types.WriteSet write_set;
        public java.util.List<org.libra.types.ContractEvent> events;

        public ChangeSet build() {
            return new ChangeSet(
                write_set,
                events
            );
        }
    }
}
