package org.libra.types;

import java.math.BigInteger;

public final class Script {
    public final com.facebook.serde.Bytes code;
    public final java.util.List<org.libra.types.TypeTag> ty_args;
    public final java.util.List<org.libra.types.TransactionArgument> args;

    public Script(com.facebook.serde.Bytes code, java.util.List<org.libra.types.TypeTag> ty_args, java.util.List<org.libra.types.TransactionArgument> args) {
       assert code != null;
       assert ty_args != null;
       assert args != null;
       this.code = code;
       this.ty_args = ty_args;
       this.args = args;
    }

    public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        serializer.serialize_bytes(code);
        TraitHelpers.serialize_vector_TypeTag(ty_args, serializer);
        TraitHelpers.serialize_vector_TransactionArgument(args, serializer);
    }

    public static Script deserialize(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        Builder builder = new Builder();
        builder.code = deserializer.deserialize_bytes();
        builder.ty_args = TraitHelpers.deserialize_vector_TypeTag(deserializer);
        builder.args = TraitHelpers.deserialize_vector_TransactionArgument(deserializer);
        return builder.build();
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
        public com.facebook.serde.Bytes code;
        public java.util.List<org.libra.types.TypeTag> ty_args;
        public java.util.List<org.libra.types.TransactionArgument> args;

        public Script build() {
            return new Script(
                code,
                ty_args,
                args
            );
        }
    }
}
