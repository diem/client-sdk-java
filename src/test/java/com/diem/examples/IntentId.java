// Copyright (c) The Diem Core Contributors
// SPDX-License-Identifier: Apache-2.0

package com.diem.examples;

import com.diem.AccountIdentifier;
import com.diem.IntentIdentifier;
import com.diem.LocalAccount;
import com.diem.Testnet;
import org.junit.Assert;
import org.junit.Test;

public class IntentId {
    @Test
    public void encodeAndDecode() {
        LocalAccount merchant = LocalAccount.generate();
        long amount = 5_000_000;

        AccountIdentifier merchantAccountId = new AccountIdentifier(
                AccountIdentifier.NetworkPrefix.TestnetPrefix,
                merchant.address
        );

        IntentIdentifier intentId = new IntentIdentifier(merchantAccountId, Testnet.XUS, amount);
        String encodeIntent = intentId.encode();
        System.out.println("encoded intent identifier: " + encodeIntent);

        // payer receives encode intent, decode for creating payment transaction
        IntentIdentifier decodedIntent = IntentIdentifier.decode(AccountIdentifier.NetworkPrefix.TestnetPrefix, encodeIntent);

        Assert.assertEquals(merchantAccountId, decodedIntent.getAccountIdentifier());
        Assert.assertEquals(Testnet.XUS, decodedIntent.getCurrency());
        Assert.assertEquals(amount, decodedIntent.getAmount());
    }
}
