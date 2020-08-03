
package org.libra.librasdk2.resources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class LibraAccount {

    @SerializedName("authentication_key")
    @Expose
    private String authenticationKey;
    @SerializedName("balances")
    @Expose
    private List<Balance> balances = new ArrayList<Balance>();
    @SerializedName("delegated_key_rotation_capability")
    @Expose
    private Boolean delegatedKeyRotationCapability;
    @SerializedName("delegated_withdrawal_capability")
    @Expose
    private Boolean delegatedWithdrawalCapability;
    @SerializedName("is_frozen")
    @Expose
    private Boolean isFrozen;
    @SerializedName("received_events_key")
    @Expose
    private String receivedEventsKey;
    @SerializedName("role")
    @Expose
    private Role role;
    @SerializedName("sent_events_key")
    @Expose
    private String sentEventsKey;
    @SerializedName("sequence_number")
    @Expose
    private BigInteger sequenceNumber;

    public String getAuthenticationKey() {
        return authenticationKey;
    }

    public void setAuthenticationKey(String authenticationKey) {
        this.authenticationKey = authenticationKey;
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }

    public Boolean getDelegatedKeyRotationCapability() {
        return delegatedKeyRotationCapability;
    }

    public void setDelegatedKeyRotationCapability(Boolean delegatedKeyRotationCapability) {
        this.delegatedKeyRotationCapability = delegatedKeyRotationCapability;
    }

    public Boolean getDelegatedWithdrawalCapability() {
        return delegatedWithdrawalCapability;
    }

    public void setDelegatedWithdrawalCapability(Boolean delegatedWithdrawalCapability) {
        this.delegatedWithdrawalCapability = delegatedWithdrawalCapability;
    }

    public Boolean getIsFrozen() {
        return isFrozen;
    }

    public void setIsFrozen(Boolean isFrozen) {
        this.isFrozen = isFrozen;
    }

    public String getReceivedEventsKey() {
        return receivedEventsKey;
    }

    public void setReceivedEventsKey(String receivedEventsKey) {
        this.receivedEventsKey = receivedEventsKey;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getSentEventsKey() {
        return sentEventsKey;
    }

    public void setSentEventsKey(String sentEventsKey) {
        this.sentEventsKey = sentEventsKey;
    }

    public BigInteger getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(BigInteger sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(authenticationKey).append(balances).append(delegatedKeyRotationCapability).append(delegatedWithdrawalCapability).append(isFrozen).append(receivedEventsKey).append(role).append(sentEventsKey).append(sequenceNumber).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof LibraAccount) == false) {
            return false;
        }
        LibraAccount rhs = ((LibraAccount) other);
        return new EqualsBuilder().append(authenticationKey, rhs.authenticationKey).append(balances, rhs.balances).append(delegatedKeyRotationCapability, rhs.delegatedKeyRotationCapability).append(delegatedWithdrawalCapability, rhs.delegatedWithdrawalCapability).append(isFrozen, rhs.isFrozen).append(receivedEventsKey, rhs.receivedEventsKey).append(role, rhs.role).append(sentEventsKey, rhs.sentEventsKey).append(sequenceNumber, rhs.sequenceNumber).isEquals();
    }

}
