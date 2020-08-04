
package org.libra.librasdk.resources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Role {

    @SerializedName("parent_vasp")
    @Expose
    private ParentVasp parentVasp;

    public ParentVasp getParentVasp() {
        return parentVasp;
    }

    public void setParentVasp(ParentVasp parentVasp) {
        this.parentVasp = parentVasp;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(parentVasp).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Role) == false) {
            return false;
        }
        Role rhs = ((Role) other);
        return new EqualsBuilder().append(parentVasp, rhs.parentVasp).isEquals();
    }

}
