// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.jsonrpc;

import com.novi.serde.Unsigned;
import org.libra.LibraException;

public class LibraTransactionExpiredException extends LibraException {
    private @Unsigned long expirationTimeSec;
    private long latestBlockTimestampUsec;

    public LibraTransactionExpiredException(@Unsigned long expirationTimeSec, @Unsigned long latestBlockTimestampUsec) {
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
