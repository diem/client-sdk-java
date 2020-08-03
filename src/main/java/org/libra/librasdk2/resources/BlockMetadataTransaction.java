
package org.libra.librasdk2.resources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigInteger;

public class BlockMetadataTransaction implements BaseTransaction {

    @SerializedName("timestamp_usecs")
    @Expose
    private BigInteger timestampUsecs;
    @SerializedName("type")
    @Expose
    private String type;

    public BigInteger getTimestampUsecs() {
        return timestampUsecs;
    }

    public void setTimestampUsecs(BigInteger timestampUsecs) {
        this.timestampUsecs = timestampUsecs;
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
        return new HashCodeBuilder().append(timestampUsecs).append(type).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof BlockMetadataTransaction) == false) {
            return false;
        }
        BlockMetadataTransaction rhs = ((BlockMetadataTransaction) other);
        return new EqualsBuilder().append(timestampUsecs, rhs.timestampUsecs).append(type, rhs.type).isEquals();
    }

}
