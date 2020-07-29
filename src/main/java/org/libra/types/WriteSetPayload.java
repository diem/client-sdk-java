package org.libra.types;

import java.math.BigInteger;

public abstract class WriteSetPayload {
    abstract public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception;

    public static WriteSetPayload deserialize(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        WriteSetPayload obj;
        int index = deserializer.deserialize_variant_index();
        switch (index) {
            case 0: return Direct.load(deserializer);
            case 1: return Script.load(deserializer);
            default: throw new java.lang.Exception("Unknown variant index for WriteSetPayload: " + index);
        }
    }

    public static final class Direct extends WriteSetPayload {
        public final org.libra.types.ChangeSet value;

        public Direct(org.libra.types.ChangeSet value) {
           assert value != null;
           this.value = value;
        }

        public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(0);
            value.serialize(serializer);
        }

        static Direct load(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.value = org.libra.types.ChangeSet.deserialize(deserializer);
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
            public org.libra.types.ChangeSet value;

            public Direct build() {
                return new Direct(
                    value
                );
            }
        }
    }

    public static final class Script extends WriteSetPayload {
        public final org.libra.types.AccountAddress execute_as;
        public final org.libra.types.Script script;

        public Script(org.libra.types.AccountAddress execute_as, org.libra.types.Script script) {
           assert execute_as != null;
           assert script != null;
           this.execute_as = execute_as;
           this.script = script;
        }

        public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
            serializer.serialize_variant_index(1);
            execute_as.serialize(serializer);
            script.serialize(serializer);
        }

        static Script load(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
            Builder builder = new Builder();
            builder.execute_as = org.libra.types.AccountAddress.deserialize(deserializer);
            builder.script = org.libra.types.Script.deserialize(deserializer);
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
            public org.libra.types.AccountAddress execute_as;
            public org.libra.types.Script script;

            public Script build() {
                return new Script(
                    execute_as,
                    script
                );
            }
        }
    }
}

