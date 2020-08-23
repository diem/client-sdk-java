package org.libra.types;

import java.math.BigInteger;

final class TraitHelpers {
    static void serialize_array16_u8_array(@com.facebook.serde.Unsigned Byte @com.facebook.serde.ArrayLen(length=16) [] value, com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        assert value.length == 16;
        for (@com.facebook.serde.Unsigned Byte item : value) {
            serializer.serialize_u8(item);
        }
    }

    static @com.facebook.serde.Unsigned Byte @com.facebook.serde.ArrayLen(length=16) [] deserialize_array16_u8_array(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        @com.facebook.serde.Unsigned Byte[] obj = new @com.facebook.serde.Unsigned Byte[16];
        for (int i = 0; i < 16; i++) {
            obj[i] = deserializer.deserialize_u8();
        }
        return obj;
    }

    static void serialize_option_bytes(java.util.Optional<com.facebook.serde.Bytes> value, com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        if (value.isPresent()) {
            serializer.serialize_option_tag(true);
            serializer.serialize_bytes(value.get());
        } else {
            serializer.serialize_option_tag(false);
        }
    }

    static java.util.Optional<com.facebook.serde.Bytes> deserialize_option_bytes(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        boolean tag = deserializer.deserialize_option_tag();
        if (!tag) {
            return java.util.Optional.empty();
        } else {
            return java.util.Optional.of(deserializer.deserialize_bytes());
        }
    }

    static void serialize_option_str(java.util.Optional<String> value, com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        if (value.isPresent()) {
            serializer.serialize_option_tag(true);
            serializer.serialize_str(value.get());
        } else {
            serializer.serialize_option_tag(false);
        }
    }

    static java.util.Optional<String> deserialize_option_str(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        boolean tag = deserializer.deserialize_option_tag();
        if (!tag) {
            return java.util.Optional.empty();
        } else {
            return java.util.Optional.of(deserializer.deserialize_str());
        }
    }

    static void serialize_option_u64(java.util.Optional<@com.facebook.serde.Unsigned Long> value, com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        if (value.isPresent()) {
            serializer.serialize_option_tag(true);
            serializer.serialize_u64(value.get());
        } else {
            serializer.serialize_option_tag(false);
        }
    }

    static java.util.Optional<@com.facebook.serde.Unsigned Long> deserialize_option_u64(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        boolean tag = deserializer.deserialize_option_tag();
        if (!tag) {
            return java.util.Optional.empty();
        } else {
            return java.util.Optional.of(deserializer.deserialize_u64());
        }
    }

    static void serialize_tuple2_AccessPath_WriteOp(com.facebook.serde.Tuple2<AccessPath, WriteOp> value, com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        value.field0.serialize(serializer);
        value.field1.serialize(serializer);
    }

    static com.facebook.serde.Tuple2<AccessPath, WriteOp> deserialize_tuple2_AccessPath_WriteOp(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        return new com.facebook.serde.Tuple2<AccessPath, WriteOp>(
            AccessPath.deserialize(deserializer),
            WriteOp.deserialize(deserializer)
        );
    }

    static void serialize_vector_AccountAddress(java.util.List<AccountAddress> value, com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        serializer.serialize_len(value.size());
        for (AccountAddress item : value) {
            item.serialize(serializer);
        }
    }

    static java.util.List<AccountAddress> deserialize_vector_AccountAddress(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        long length = deserializer.deserialize_len();
        java.util.List<AccountAddress> obj = new java.util.ArrayList<AccountAddress>((int) length);
        for (long i = 0; i < length; i++) {
            obj.add(AccountAddress.deserialize(deserializer));
        }
        return obj;
    }

    static void serialize_vector_ContractEvent(java.util.List<ContractEvent> value, com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        serializer.serialize_len(value.size());
        for (ContractEvent item : value) {
            item.serialize(serializer);
        }
    }

    static java.util.List<ContractEvent> deserialize_vector_ContractEvent(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        long length = deserializer.deserialize_len();
        java.util.List<ContractEvent> obj = new java.util.ArrayList<ContractEvent>((int) length);
        for (long i = 0; i < length; i++) {
            obj.add(ContractEvent.deserialize(deserializer));
        }
        return obj;
    }

    static void serialize_vector_TransactionArgument(java.util.List<TransactionArgument> value, com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        serializer.serialize_len(value.size());
        for (TransactionArgument item : value) {
            item.serialize(serializer);
        }
    }

    static java.util.List<TransactionArgument> deserialize_vector_TransactionArgument(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        long length = deserializer.deserialize_len();
        java.util.List<TransactionArgument> obj = new java.util.ArrayList<TransactionArgument>((int) length);
        for (long i = 0; i < length; i++) {
            obj.add(TransactionArgument.deserialize(deserializer));
        }
        return obj;
    }

    static void serialize_vector_TypeTag(java.util.List<TypeTag> value, com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        serializer.serialize_len(value.size());
        for (TypeTag item : value) {
            item.serialize(serializer);
        }
    }

    static java.util.List<TypeTag> deserialize_vector_TypeTag(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        long length = deserializer.deserialize_len();
        java.util.List<TypeTag> obj = new java.util.ArrayList<TypeTag>((int) length);
        for (long i = 0; i < length; i++) {
            obj.add(TypeTag.deserialize(deserializer));
        }
        return obj;
    }

    static void serialize_vector_tuple2_AccessPath_WriteOp(java.util.List<com.facebook.serde.Tuple2<AccessPath, WriteOp>> value, com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        serializer.serialize_len(value.size());
        for (com.facebook.serde.Tuple2<AccessPath, WriteOp> item : value) {
            TraitHelpers.serialize_tuple2_AccessPath_WriteOp(item, serializer);
        }
    }

    static java.util.List<com.facebook.serde.Tuple2<AccessPath, WriteOp>> deserialize_vector_tuple2_AccessPath_WriteOp(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        long length = deserializer.deserialize_len();
        java.util.List<com.facebook.serde.Tuple2<AccessPath, WriteOp>> obj = new java.util.ArrayList<com.facebook.serde.Tuple2<AccessPath, WriteOp>>((int) length);
        for (long i = 0; i < length; i++) {
            obj.add(TraitHelpers.deserialize_tuple2_AccessPath_WriteOp(deserializer));
        }
        return obj;
    }

}

