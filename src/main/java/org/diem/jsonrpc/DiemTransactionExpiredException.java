// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.diem.jsonrpc;

import com.novi.serde.Unsigned;
import org.diem.DiemException;

public class DiemTransactionExpiredException extends DiemException {
    private @Unsigned long expirationTimeSec;
    private long latestBlockTimestampUsec;

    public DiemTransactionExpiredException(@Unsigned long expirationTimeSec, @Unsigned long latestBlockTimestampUsec) {
        super("transaction expired");

        this.expirationTimeSec = expirationTimeSec;
        this.latestBlockTimestampUsec = latestBlockTimestampUsec;
    }

    public long getExpirationTimeSec() {
        return expirationTimeSec;
    }

    public long getLatestBlockTimestampUsec() {
        return latestBlockTimestampUsec;
    }
}
