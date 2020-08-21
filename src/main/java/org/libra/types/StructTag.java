package org.libra.types;

import java.math.BigInteger;

public final class StructTag {
    public final AccountAddress address;
    public final Identifier module;
    public final Identifier name;
    public final java.util.List<TypeTag> type_params;

    public StructTag(AccountAddress address, Identifier module, Identifier name, java.util.List<TypeTag> type_params) {
        assert address != null;
        assert module != null;
        assert name != null;
        assert type_params != null;
        this.address = address;
        this.module = module;
        this.name = name;
        this.type_params = type_params;
    }

    public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        address.serialize(serializer);
        module.serialize(serializer);
        name.serialize(serializer);
        TraitHelpers.serialize_vector_TypeTag(type_params, serializer);
    }

    public static StructTag deserialize(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        Builder builder = new Builder();
        builder.address = AccountAddress.deserialize(deserializer);
        builder.module = Identifier.deserialize(deserializer);
        builder.name = Identifier.deserialize(deserializer);
        builder.type_params = TraitHelpers.deserialize_vector_TypeTag(deserializer);
        return builder.build();
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        StructTag other = (StructTag) obj;
        if (!java.util.Objects.equals(this.address, other.address)) { return false; }
        if (!java.util.Objects.equals(this.module, other.module)) { return false; }
        if (!java.util.Objects.equals(this.name, other.name)) { return false; }
        if (!java.util.Objects.equals(this.type_params, other.type_params)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.address != null ? this.address.hashCode() : 0);
        value = 31 * value + (this.module != null ? this.module.hashCode() : 0);
        value = 31 * value + (this.name != null ? this.name.hashCode() : 0);
        value = 31 * value + (this.type_params != null ? this.type_params.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public AccountAddress address;
        public Identifier module;
        public Identifier name;
        public java.util.List<TypeTag> type_params;

        public StructTag build() {
            return new StructTag(
                address,
                module,
                name,
                type_params
            );
        }
    }
}
