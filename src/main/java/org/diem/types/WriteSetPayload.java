package org.diem.types;


public abstract class WriteSetPayload {

    abstract public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError;

    public static WriteSetPayload deserialize(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        int index = deserializer.deserialize_variant_index();
        switch (index) {
            case 0: return Direct.load(deserializer);
            case 1: return Script.load(deserializer);
            default: throw new com.novi.serde.DeserializationError("Unknown variant index for WriteSetPayload: " + index);
        }
    }

    public byte[] lcsSerialize() throws com.novi.serde.SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.lcs.LcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static WriteSetPayload lcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
             throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.lcs.LcsDeserializer(input);
        WriteSetPayload value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new com.novi.serde.DeserializationError("Some input bytes were not read");
        }
        return value;
    }

    public static final class Direct extends WriteSetPayload {
        public final ChangeSet value;

        public Direct(ChangeSet value) {
            java.util.Objects.requireNonNull(value, "value must not be null");
            this.value = value;
        }

        public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
            serializer.increase_container_depth();
            serializer.serialize_variant_index(0);
            value.serialize(serializer);
            serializer.decrease_container_depth();
        }

        static Direct load(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
            deserializer.increase_container_depth();
            Builder builder = new Builder();
            builder.value = ChangeSet.deserialize(deserializer);
            deserializer.decrease_container_depth();
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Direct other = (Direct) obj;
            if (!java.util.Objects.equals(this.value, other.value)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.value != null ? this.value.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public ChangeSet value;

            public Direct build() {
                return new Direct(
                    value
                );
            }
        }
    }

    public static final class Script extends WriteSetPayload {
        public final AccountAddress execute_as;
        public final org.diem.types.Script script;

        public Script(AccountAddress execute_as, org.diem.types.Script script) {
            java.util.Objects.requireNonNull(execute_as, "execute_as must not be null");
            java.util.Objects.requireNonNull(script, "script must not be null");
            this.execute_as = execute_as;
            this.script = script;
        }

        public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
            serializer.increase_container_depth();
            serializer.serialize_variant_index(1);
            execute_as.serialize(serializer);
            script.serialize(serializer);
            serializer.decrease_container_depth();
        }

        static Script load(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
            deserializer.increase_container_depth();
            Builder builder = new Builder();
            builder.execute_as = AccountAddress.deserialize(deserializer);
            builder.script = org.diem.types.Script.deserialize(deserializer);
            deserializer.decrease_container_depth();
            return builder.build();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Script other = (Script) obj;
            if (!java.util.Objects.equals(this.execute_as, other.execute_as)) { return false; }
            if (!java.util.Objects.equals(this.script, other.script)) { return false; }
            return true;
        }

        public int hashCode() {
            int value = 7;
            value = 31 * value + (this.execute_as != null ? this.execute_as.hashCode() : 0);
            value = 31 * value + (this.script != null ? this.script.hashCode() : 0);
            return value;
        }

        public static final class Builder {
            public AccountAddress execute_as;
            public org.diem.types.Script script;

            public Script build() {
                return new Script(
                    execute_as,
                    script
                );
            }
        }
    }
}

