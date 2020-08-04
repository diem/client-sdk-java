
package org.libra.librasdk.resources;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.libra.librasdk.dto.Transaction;

public class PeerToPeerTransaction extends BaseTransaction {
    public int getChainId() {
        return rawTransaction.transaction.chain_id;
    }

    public long getExpirationTimestampSecs() {
        return rawTransaction.transaction.expiration_timestamp_secs;
    }

    public String getGasCurrency() {
        return rawTransaction.transaction.gas_currency;
    }

    public long getGasUnitPrice() {
        return rawTransaction.transaction.gas_unit_price;
    }

    public long getMaxGasAmount() {
        return rawTransaction.transaction.max_gas_amount;
    }

    public String getPublicKey() {
        return rawTransaction.transaction.public_key;
    }

    public Transaction.Script getScript() {
        return rawTransaction.transaction.script;
    }

    public String getScriptHash() {
        return rawTransaction.transaction.script_hash;
    }

    public String getSender() {
        return rawTransaction.transaction.sender;
    }

    public long getSequenceNumber() {
        return rawTransaction.transaction.sequence_number;
    }

    public String getSignature() {
        return rawTransaction.transaction.signature;
    }

    public String getSignatureScheme() {
        return rawTransaction.transaction.signature_scheme;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public long getAmount() {
        return rawTransaction.transaction.script.amount;
    }

    public String getCurrency() {
        return rawTransaction.transaction.script.currency;
    }

    public String getMetadata() {
        return rawTransaction.transaction.script.metadata;
    }

    public String getMetadataSignature() {
        return rawTransaction.transaction.script.metadata_signature;
    }

    public String getReceiver() {
        return rawTransaction.transaction.script.receiver;
    }

    public String getScriptType() {
        return rawTransaction.transaction.script.type;
    }
}
