// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.librasdk;

public class StaleResponseException extends LibraSDKException {

    public StaleResponseException(String msg) {
        super(msg);
    }
}
