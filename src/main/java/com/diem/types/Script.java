package com.diem.types;


public final class Script {
    public final com.novi.serde.Bytes code;
    public final java.util.List<TypeTag> ty_args;
    public final java.util.List<TransactionArgument> args;

    public Script(com.novi.serde.Bytes code, java.util.List<TypeTag> ty_args, java.util.List<TransactionArgument> args) {
        java.util.Objects.requireNonNull(code, "code must not be null");
        java.util.Objects.requireNonNull(ty_args, "ty_args must not be null");
        java.util.Objects.requireNonNull(args, "args must not be null");
        this.code = code;
        this.ty_args = ty_args;
        this.args = args;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.increase_container_depth();
        serializer.serialize_bytes(code);
        TraitHelpers.serialize_vector_TypeTag(ty_args, serializer);
        TraitHelpers.serialize_vector_TransactionArgument(args, serializer);
        serializer.decrease_container_depth();
    }

    public byte[] bcsSerialize() throws com.novi.serde.SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.bcs.BcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static Script deserialize(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        deserializer.increase_container_depth();
        Builder builder = new Builder();
        builder.code = deserializer.deserialize_bytes();
        builder.ty_args = TraitHelpers.deserialize_vector_TypeTag(deserializer);
        builder.args = TraitHelpers.deserialize_vector_TransactionArgument(deserializer);
        deserializer.decrease_container_depth();
        return builder.build();
    }

    public static Script bcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
             throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.bcs.BcsDeserializer(input);
        Script value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new com.novi.serde.DeserializationError("Some input bytes were not read");
        }
        return value;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Script other = (Script) obj;
        if (!java.util.Objects.equals(this.code, other.code)) { return false; }
        if (!java.util.Objects.equals(this.ty_args, other.ty_args)) { return false; }
        if (!java.util.Objects.equals(this.args, other.args)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.code != null ? this.code.hashCode() : 0);
        value = 31 * value + (this.ty_args != null ? this.ty_args.hashCode() : 0);
        value = 31 * value + (this.args != null ? this.args.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public com.novi.serde.Bytes code;
        public java.util.List<TypeTag> ty_args;
        public java.util.List<TransactionArgument> args;

        public Script build() {
            return new Script(
                code,
                ty_args,
                args
            );
        }
    }
}
