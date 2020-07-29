// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk.dto;

import java.util.Objects;

public class Metadata {
    public long version;
    public long timestamp;

    @Override
    public String toString() {
        return "Metadata{" +
                "version=" + version +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Metadata metadata = (Metadata) o;
        return version == metadata.version &&
                timestamp == metadata.timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, timestamp);
    }
}
