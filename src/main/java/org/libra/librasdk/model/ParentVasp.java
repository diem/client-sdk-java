
package org.libra.librasdk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigInteger;

public class ParentVasp {

    @SerializedName("base_url")
    @Expose
    private String baseUrl;
    @SerializedName("compliance_key")
    @Expose
    private String complianceKey;
    @SerializedName("expiration_time")
    @Expose
    private BigInteger expirationTime;
    @SerializedName("human_name")
    @Expose
    private String humanName;
    @SerializedName("num_children")
    @Expose
    private BigInteger numChildren;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getComplianceKey() {
        return complianceKey;
    }

    public void setComplianceKey(String complianceKey) {
        this.complianceKey = complianceKey;
    }

    public BigInteger getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(BigInteger expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getHumanName() {
        return humanName;
    }

    public void setHumanName(String humanName) {
        this.humanName = humanName;
    }

    public BigInteger getNumChildren() {
        return numChildren;
    }

    public void setNumChildren(BigInteger numChildren) {
        this.numChildren = numChildren;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(baseUrl).append(complianceKey).append(expirationTime).append(humanName).append(numChildren).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ParentVasp) == false) {
            return false;
        }
        ParentVasp rhs = ((ParentVasp) other);
        return new EqualsBuilder().append(baseUrl, rhs.baseUrl).append(complianceKey, rhs.complianceKey).append(expirationTime, rhs.expirationTime).append(humanName, rhs.humanName).append(numChildren, rhs.numChildren).isEquals();
    }

}
