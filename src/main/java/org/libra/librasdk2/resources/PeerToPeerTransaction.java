
package org.libra.librasdk2.resources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigInteger;

public class PeerToPeerTransaction implements BaseTransaction{

    @SerializedName("chain_id")
    @Expose
    private BigInteger chainId;
    @SerializedName("expiration_timestamp_secs")
    @Expose
    private BigInteger expirationTimestampSecs;
    @SerializedName("gas_currency")
    @Expose
    private String gasCurrency;
    @SerializedName("gas_unit_price")
    @Expose
    private BigInteger gasUnitPrice;
    @SerializedName("max_gas_amount")
    @Expose
    private BigInteger maxGasAmount;
    @SerializedName("public_key")
    @Expose
    private String publicKey;
    @SerializedName("script")
    @Expose
    private Script script;
    @SerializedName("script_hash")
    @Expose
    private String scriptHash;
    @SerializedName("sender")
    @Expose
    private String sender;
    @SerializedName("sequence_number")
    @Expose
    private BigInteger sequenceNumber;
    @SerializedName("signature")
    @Expose
    private String signature;
    @SerializedName("signature_scheme")
    @Expose
    private String signatureScheme;
    @SerializedName("type")
    @Expose
    private String type;

    public BigInteger getChainId() {
        return chainId;
    }

    public void setChainId(BigInteger chainId) {
        this.chainId = chainId;
    }

    public BigInteger getExpirationTimestampSecs() {
        return expirationTimestampSecs;
    }

    public void setExpirationTimestampSecs(BigInteger expirationTimestampSecs) {
        this.expirationTimestampSecs = expirationTimestampSecs;
    }

    public String getGasCurrency() {
        return gasCurrency;
    }

    public void setGasCurrency(String gasCurrency) {
        this.gasCurrency = gasCurrency;
    }

    public BigInteger getGasUnitPrice() {
        return gasUnitPrice;
    }

    public void setGasUnitPrice(BigInteger gasUnitPrice) {
        this.gasUnitPrice = gasUnitPrice;
    }

    public BigInteger getMaxGasAmount() {
        return maxGasAmount;
    }

    public void setMaxGasAmount(BigInteger maxGasAmount) {
        this.maxGasAmount = maxGasAmount;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }

    public String getScriptHash() {
        return scriptHash;
    }

    public void setScriptHash(String scriptHash) {
        this.scriptHash = scriptHash;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public BigInteger getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(BigInteger sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignatureScheme() {
        return signatureScheme;
    }

    public void setSignatureScheme(String signatureScheme) {
        this.signatureScheme = signatureScheme;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(chainId).append(expirationTimestampSecs).append(gasCurrency).append(gasUnitPrice).append(maxGasAmount).append(publicKey).append(script).append(scriptHash).append(sender).append(sequenceNumber).append(signature).append(signatureScheme).append(type).toHashCode();
    }

    public long getAmount() {
        return this.getAmount();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof PeerToPeerTransaction) == false) {
            return false;
        }
        PeerToPeerTransaction rhs = ((PeerToPeerTransaction) other);
        return new EqualsBuilder().append(chainId, rhs.chainId).append(expirationTimestampSecs, rhs.expirationTimestampSecs).append(gasCurrency, rhs.gasCurrency).append(gasUnitPrice, rhs.gasUnitPrice).append(maxGasAmount, rhs.maxGasAmount).append(publicKey, rhs.publicKey).append(script, rhs.script).append(scriptHash, rhs.scriptHash).append(sender, rhs.sender).append(sequenceNumber, rhs.sequenceNumber).append(signature, rhs.signature).append(signatureScheme, rhs.signatureScheme).append(type, rhs.type).isEquals();
    }

}
