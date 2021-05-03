package com.diem.types;


public final class ScriptFunction {
    public final ModuleId module;
    public final Identifier function;
    public final java.util.List<TypeTag> ty_args;
    public final java.util.List<com.novi.serde.Bytes> args;

    public ScriptFunction(ModuleId module, Identifier function, java.util.List<TypeTag> ty_args, java.util.List<com.novi.serde.Bytes> args) {
        java.util.Objects.requireNonNull(module, "module must not be null");
        java.util.Objects.requireNonNull(function, "function must not be null");
        java.util.Objects.requireNonNull(ty_args, "ty_args must not be null");
        java.util.Objects.requireNonNull(args, "args must not be null");
        this.module = module;
        this.function = function;
        this.ty_args = ty_args;
        this.args = args;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.increase_container_depth();
        module.serialize(serializer);
        function.serialize(serializer);
        TraitHelpers.serialize_vector_TypeTag(ty_args, serializer);
        TraitHelpers.serialize_vector_bytes(args, serializer);
        serializer.decrease_container_depth();
    }

    public byte[] bcsSerialize() throws com.novi.serde.SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.bcs.BcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static ScriptFunction deserialize(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        deserializer.increase_container_depth();
        Builder builder = new Builder();
        builder.module = ModuleId.deserialize(deserializer);
        builder.function = Identifier.deserialize(deserializer);
        builder.ty_args = TraitHelpers.deserialize_vector_TypeTag(deserializer);
        builder.args = TraitHelpers.deserialize_vector_bytes(deserializer);
        deserializer.decrease_container_depth();
        return builder.build();
    }

    public static ScriptFunction bcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
             throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.bcs.BcsDeserializer(input);
        ScriptFunction value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new com.novi.serde.DeserializationError("Some input bytes were not read");
        }
        return value;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ScriptFunction other = (ScriptFunction) obj;
        if (!java.util.Objects.equals(this.module, other.module)) { return false; }
        if (!java.util.Objects.equals(this.function, other.function)) { return false; }
        if (!java.util.Objects.equals(this.ty_args, other.ty_args)) { return false; }
        if (!java.util.Objects.equals(this.args, other.args)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.module != null ? this.module.hashCode() : 0);
        value = 31 * value + (this.function != null ? this.function.hashCode() : 0);
        value = 31 * value + (this.ty_args != null ? this.ty_args.hashCode() : 0);
        value = 31 * value + (this.args != null ? this.args.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public ModuleId module;
        public Identifier function;
        public java.util.List<TypeTag> ty_args;
        public java.util.List<com.novi.serde.Bytes> args;

        public ScriptFunction build() {
            return new ScriptFunction(
                module,
                function,
                ty_args,
                args
            );
        }
    }
}
