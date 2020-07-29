package org.libra.types;

import java.math.BigInteger;

public final class SignedTransaction {
    public final org.libra.types.RawTransaction raw_txn;
    public final org.libra.types.TransactionAuthenticator authenticator;

    public SignedTransaction(org.libra.types.RawTransaction raw_txn, org.libra.types.TransactionAuthenticator authenticator) {
       assert raw_txn != null;
       assert authenticator != null;
       this.raw_txn = raw_txn;
       this.authenticator = authenticator;
    }

    public void serialize(com.facebook.serde.Serializer serializer) throws java.lang.Exception {
        raw_txn.serialize(serializer);
        authenticator.serialize(serializer);
    }

    public static SignedTransaction deserialize(com.facebook.serde.Deserializer deserializer) throws java.lang.Exception {
        Builder builder = new Builder();
        builder.raw_txn = org.libra.types.RawTransaction.deserialize(deserializer);
        builder.authenticator = org.libra.types.TransactionAuthenticator.deserialize(deserializer);
        return builder.build();
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SignedTransaction other = (SignedTransaction) obj;
        if (!java.util.Objects.equals(this.raw_txn, other.raw_txn)) { return false; }
        if (!java.util.Objects.equals(this.authenticator, other.authenticator)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.raw_txn != null ? this.raw_txn.hashCode() : 0);
        value = 31 * value + (this.authenticator != null ? this.authenticator.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public org.libra.types.RawTransaction raw_txn;
        public org.libra.types.TransactionAuthenticator authenticator;

        public SignedTransaction build() {
            return new SignedTransaction(
                raw_txn,
                authenticator
            );
        }
    }
}
