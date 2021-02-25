package com.diem.types;

final class TraitHelpers {
    static void serialize_array16_u8_array(java.util.@com.novi.serde.ArrayLen(length=16) List<@com.novi.serde.Unsigned Byte> value, com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        if (value.size() != 16) {
            throw new java.lang.IllegalArgumentException("Invalid length for fixed-size array: " + value.size() + " instead of "+ 16);
        }
        for (@com.novi.serde.Unsigned Byte item : value) {
            serializer.serialize_u8(item);
        }
    }

    static java.util.@com.novi.serde.ArrayLen(length=16) List<@com.novi.serde.Unsigned Byte> deserialize_array16_u8_array(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        java.util.List<@com.novi.serde.Unsigned Byte> obj = new java.util.ArrayList<@com.novi.serde.Unsigned Byte>(16);
        for (long i = 0; i < 16; i++) {
            obj.add(deserializer.deserialize_u8());
        }
        return obj;
    }

    static void serialize_option_bytes(java.util.Optional<com.novi.serde.Bytes> value, com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        if (value.isPresent()) {
            serializer.serialize_option_tag(true);
            serializer.serialize_bytes(value.get());
        } else {
            serializer.serialize_option_tag(false);
        }
    }

    static java.util.Optional<com.novi.serde.Bytes> deserialize_option_bytes(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        boolean tag = deserializer.deserialize_option_tag();
        if (!tag) {
            return java.util.Optional.empty();
        } else {
            return java.util.Optional.of(deserializer.deserialize_bytes());
        }
    }

    static void serialize_option_str(java.util.Optional<String> value, com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        if (value.isPresent()) {
            serializer.serialize_option_tag(true);
            serializer.serialize_str(value.get());
        } else {
            serializer.serialize_option_tag(false);
        }
    }

    static java.util.Optional<String> deserialize_option_str(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        boolean tag = deserializer.deserialize_option_tag();
        if (!tag) {
            return java.util.Optional.empty();
        } else {
            return java.util.Optional.of(deserializer.deserialize_str());
        }
    }

    static void serialize_option_u64(java.util.Optional<@com.novi.serde.Unsigned Long> value, com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        if (value.isPresent()) {
            serializer.serialize_option_tag(true);
            serializer.serialize_u64(value.get());
        } else {
            serializer.serialize_option_tag(false);
        }
    }

    static java.util.Optional<@com.novi.serde.Unsigned Long> deserialize_option_u64(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        boolean tag = deserializer.deserialize_option_tag();
        if (!tag) {
            return java.util.Optional.empty();
        } else {
            return java.util.Optional.of(deserializer.deserialize_u64());
        }
    }

    static void serialize_tuple2_AccessPath_WriteOp(com.novi.serde.Tuple2<AccessPath, WriteOp> value, com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        value.field0.serialize(serializer);
        value.field1.serialize(serializer);
    }

    static com.novi.serde.Tuple2<AccessPath, WriteOp> deserialize_tuple2_AccessPath_WriteOp(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        return new com.novi.serde.Tuple2<AccessPath, WriteOp>(
            AccessPath.deserialize(deserializer),
            WriteOp.deserialize(deserializer)
        );
    }

    static void serialize_vector_AccountAddress(java.util.List<AccountAddress> value, com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.serialize_len(value.size());
        for (AccountAddress item : value) {
            item.serialize(serializer);
        }
    }

    static java.util.List<AccountAddress> deserialize_vector_AccountAddress(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        long length = deserializer.deserialize_len();
        java.util.List<AccountAddress> obj = new java.util.ArrayList<AccountAddress>((int) length);
        for (long i = 0; i < length; i++) {
            obj.add(AccountAddress.deserialize(deserializer));
        }
        return obj;
    }

    static void serialize_vector_ContractEvent(java.util.List<ContractEvent> value, com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.serialize_len(value.size());
        for (ContractEvent item : value) {
            item.serialize(serializer);
        }
    }

    static java.util.List<ContractEvent> deserialize_vector_ContractEvent(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        long length = deserializer.deserialize_len();
        java.util.List<ContractEvent> obj = new java.util.ArrayList<ContractEvent>((int) length);
        for (long i = 0; i < length; i++) {
            obj.add(ContractEvent.deserialize(deserializer));
        }
        return obj;
    }

    static void serialize_vector_TransactionArgument(java.util.List<TransactionArgument> value, com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.serialize_len(value.size());
        for (TransactionArgument item : value) {
            item.serialize(serializer);
        }
    }

    static java.util.List<TransactionArgument> deserialize_vector_TransactionArgument(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        long length = deserializer.deserialize_len();
        java.util.List<TransactionArgument> obj = new java.util.ArrayList<TransactionArgument>((int) length);
        for (long i = 0; i < length; i++) {
            obj.add(TransactionArgument.deserialize(deserializer));
        }
        return obj;
    }

    static void serialize_vector_TypeTag(java.util.List<TypeTag> value, com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.serialize_len(value.size());
        for (TypeTag item : value) {
            item.serialize(serializer);
        }
    }

    static java.util.List<TypeTag> deserialize_vector_TypeTag(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        long length = deserializer.deserialize_len();
        java.util.List<TypeTag> obj = new java.util.ArrayList<TypeTag>((int) length);
        for (long i = 0; i < length; i++) {
            obj.add(TypeTag.deserialize(deserializer));
        }
        return obj;
    }

    static void serialize_vector_str(java.util.List<String> value, com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.serialize_len(value.size());
        for (String item : value) {
            serializer.serialize_str(item);
        }
    }

    static java.util.List<String> deserialize_vector_str(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        long length = deserializer.deserialize_len();
        java.util.List<String> obj = new java.util.ArrayList<String>((int) length);
        for (long i = 0; i < length; i++) {
            obj.add(deserializer.deserialize_str());
        }
        return obj;
    }

    static void serialize_vector_tuple2_AccessPath_WriteOp(java.util.List<com.novi.serde.Tuple2<AccessPath, WriteOp>> value, com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.serialize_len(value.size());
        for (com.novi.serde.Tuple2<AccessPath, WriteOp> item : value) {
            TraitHelpers.serialize_tuple2_AccessPath_WriteOp(item, serializer);
        }
    }

    static java.util.List<com.novi.serde.Tuple2<AccessPath, WriteOp>> deserialize_vector_tuple2_AccessPath_WriteOp(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        long length = deserializer.deserialize_len();
        java.util.List<com.novi.serde.Tuple2<AccessPath, WriteOp>> obj = new java.util.ArrayList<com.novi.serde.Tuple2<AccessPath, WriteOp>>((int) length);
        for (long i = 0; i < length; i++) {
            obj.add(TraitHelpers.deserialize_tuple2_AccessPath_WriteOp(deserializer));
        }
        return obj;
    }

}

