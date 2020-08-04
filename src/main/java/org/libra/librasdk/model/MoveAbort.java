
package org.libra.librasdk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigInteger;

public class MoveAbort {

    @SerializedName("abort_code")
    @Expose
    private BigInteger abortCode;
    @SerializedName("location")
    @Expose
    private String location;

    public BigInteger getAbortCode() {
        return abortCode;
    }

    public void setAbortCode(BigInteger abortCode) {
        this.abortCode = abortCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(abortCode).append(location).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof MoveAbort) == false) {
            return false;
        }
        MoveAbort rhs = ((MoveAbort) other);
        return new EqualsBuilder().append(abortCode, rhs.abortCode).append(location, rhs.location).isEquals();
    }

}
