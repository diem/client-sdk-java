
package org.libra.librasdk2.resources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigInteger;

public class Script {

    @SerializedName("amount")
    @Expose
    private BigInteger amount;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("metadata")
    @Expose
    private String metadata;
    @SerializedName("metadata_signature")
    @Expose
    private String metadataSignature;
    @SerializedName("receiver")
    @Expose
    private String receiver;
    @SerializedName("type")
    @Expose
    private String type;

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getMetadataSignature() {
        return metadataSignature;
    }

    public void setMetadataSignature(String metadataSignature) {
        this.metadataSignature = metadataSignature;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
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
        return new HashCodeBuilder().append(amount).append(currency).append(metadata).append(metadataSignature).append(receiver).append(type).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Script) == false) {
            return false;
        }
        Script rhs = ((Script) other);
        return new EqualsBuilder().append(amount, rhs.amount).append(currency, rhs.currency).append(metadata, rhs.metadata).append(metadataSignature, rhs.metadataSignature).append(receiver, rhs.receiver).append(type, rhs.type).isEquals();
    }

}
