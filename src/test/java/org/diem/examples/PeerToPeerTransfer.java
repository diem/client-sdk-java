// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.diem.examples;

import com.novi.serde.Bytes;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.diem.*;
import org.junit.Assert;
import org.junit.Test;
import org.diem.stdlib.Helpers;

import java.security.SecureRandom;

import static org.diem.examples.Utils.genChildVASPAccount;
import static org.diem.examples.Utils.getAccountBalance;

public class PeerToPeerTransfer {

    @Test
    public void nonCustodialToNonCustodial() throws DiemException {
        DiemClient client = Testnet.createClient();
        LocalAccount nonCustodialAccount1 = Utils.genAccount(client, 1_000_000);
        LocalAccount nonCustodialAccount2 = Utils.genAccount(client, 1_000_000);

        // Non custodial to non custodial has no requirement on metadata
        Utils.submitAndWait(client, nonCustodialAccount1, Helpers.encode_peer_to_peer_with_metadata_script(
                Testnet.COIN1_TYPE, nonCustodialAccount2.address, 500_000L,
                new Bytes(new byte[0]), new Bytes(new byte[0])
        ));

        Assert.assertEquals(500_000, getAccountBalance(client, nonCustodialAccount1));
        Assert.assertEquals(1_500_000, getAccountBalance(client, nonCustodialAccount2));
    }

    @Test
    public void nonCustodialAccountToCustodialAccount() throws DiemException {
        DiemClient client = Testnet.createClient();
        LocalAccount nonCustodialAccount = Utils.genAccount(client, 1_000_000);

        LocalAccount custodialAccount = Utils.genAccount(client, 1_000_000);
        LocalAccount custodialChildAccount = genChildVASPAccount(client, custodialAccount, 0l);

        SubAddress custodialUserSubAddress = SubAddress.generate();


        // Non custodial account to custodial account requires target custodial account sub-address,
        // hence we need construct a general metadata includes to-subaddress
        TransactionMetadata metadata = TransactionMetadata.createGeneralMetadataToSubAddress(custodialUserSubAddress);
        Utils.submitAndWait(client, nonCustodialAccount, Helpers.encode_peer_to_peer_with_metadata_script(
                Testnet.COIN1_TYPE, custodialChildAccount.address, 500_000L,
                metadata.getMetadata(),
                new Bytes(new byte[0]) // no metadata signature for GeneralMetadata
        ));

        Assert.assertEquals(1_000_000, getAccountBalance(client, custodialAccount));
        Assert.assertEquals(500_000, getAccountBalance(client, custodialChildAccount));
        Assert.assertEquals(500_000, getAccountBalance(client, nonCustodialAccount));
    }

    @Test
    public void custodialAccountToNonCustodialAccount() throws DiemException {
        DiemClient client = Testnet.createClient();
        LocalAccount nonCustodialAccount = Utils.genAccount(client, 1_000_000);

        LocalAccount custodialAccount = Utils.genAccount(client, 1_000_000);
        LocalAccount custodialChildAccount = genChildVASPAccount(client, custodialAccount, 500_000l);

        SubAddress custodialUserSubAddress = SubAddress.generate();


        // Non custodial account to custodial account requires target custodial account sub-address,
        // hence we need construct a general metadata includes to-subaddress
        TransactionMetadata metadata = TransactionMetadata.createGeneralMetadataToSubAddress(custodialUserSubAddress);
        Utils.submitAndWait(client, custodialChildAccount, Helpers.encode_peer_to_peer_with_metadata_script(
                Testnet.COIN1_TYPE, nonCustodialAccount.address, 500_000L,
                metadata.getMetadata(),
                new Bytes(new byte[0]) // no metadata signature for GeneralMetadata
        ));

        Assert.assertEquals(500_000, getAccountBalance(client, custodialAccount));
        Assert.assertEquals(0, getAccountBalance(client, custodialChildAccount));
        Assert.assertEquals(1_500_000, getAccountBalance(client, nonCustodialAccount));
    }

    @Test
    public void custodialAccountToCustodialAccountUnderThreshold() throws DiemException {
        DiemClient client = Testnet.createClient();
        LocalAccount senderParentVASPAccount = Utils.genAccount(client, 1_000_000);
        LocalAccount senderChildVASPAccount = genChildVASPAccount(client, senderParentVASPAccount, 500_000l);

        SubAddress senderCustodialUserSubAddress = SubAddress.generate();

        LocalAccount receiverParentVASPAccount = Utils.genAccount(client, 1_000_000);
        LocalAccount receiverChildVASPAccount = genChildVASPAccount(client, receiverParentVASPAccount, 500_000l);

        SubAddress receiverCustodialUserSubAddress = SubAddress.generate();

        TransactionMetadata metadata = TransactionMetadata.createGeneralMetadataWithFromToSubAddresses(
                senderCustodialUserSubAddress, receiverCustodialUserSubAddress);
        Utils.submitAndWait(client, senderChildVASPAccount, Helpers.encode_peer_to_peer_with_metadata_script(
                Testnet.COIN1_TYPE, receiverChildVASPAccount.address, 500_000L,
                metadata.getMetadata(),
                new Bytes(new byte[0]) // no metadata signature for GeneralMetadata
        ));

        Assert.assertEquals(500_000, getAccountBalance(client, senderParentVASPAccount));
        Assert.assertEquals(0, getAccountBalance(client, senderChildVASPAccount));
        Assert.assertEquals(500_000, getAccountBalance(client, receiverParentVASPAccount));
        Assert.assertEquals(1_000_000, getAccountBalance(client, receiverChildVASPAccount));
    }

    @Test
    public void custodialAccountToCustodialAccountAboveThreshold() throws DiemException {
        DiemClient client = Testnet.createClient();
        LocalAccount senderParentVASPAccount = Utils.genAccount(client, 1_000_000_000);
        LocalAccount senderChildVASPAccount = genChildVASPAccount(client, senderParentVASPAccount, 500_000_000l);

        LocalAccount receiverParentVASPAccount = Utils.genAccount(client, 1_000_000_000);
        LocalAccount receiverChildVASPAccount = genChildVASPAccount(client, receiverParentVASPAccount, 500_000_000l);

        // setup parent vasp compliance key, testnet defaults it to fake key.
        PrivateKey receiverComplianceKey = new Ed25519PrivateKey(new Ed25519PrivateKeyParameters(new SecureRandom()));
        Utils.submitAndWait(client, receiverParentVASPAccount, Helpers.encode_rotate_dual_attestation_info_script(
                new Bytes("http://hello.com".getBytes()),
                new Bytes(receiverComplianceKey.publicKey())
        ));

        // sender & receiver communicate by off chain APIs
        String offChainReferenceId = "32323abc";

        long amount = 500_000_000;
        TransactionMetadata metadata = TransactionMetadata.createTravelRuleMetadata(
                offChainReferenceId, senderChildVASPAccount.address, amount);

        // metadata signature should be generated from receiver's compliance key, so that
        // we can use parent vasp's compliance public key to verify the signature
        byte[] metadataSignature = receiverComplianceKey.sign(metadata.getSignatureMessage());

        Utils.submitAndWait(client, senderChildVASPAccount, Helpers.encode_peer_to_peer_with_metadata_script(
                Testnet.COIN1_TYPE, receiverChildVASPAccount.address, amount,
                metadata.getMetadata(),
                new Bytes(metadataSignature)
        ));

        Assert.assertEquals(500_000_000, getAccountBalance(client, senderParentVASPAccount));
        Assert.assertEquals(0, getAccountBalance(client, senderChildVASPAccount));
        Assert.assertEquals(500_000_000, getAccountBalance(client, receiverParentVASPAccount));
        Assert.assertEquals(1_000_000_000, getAccountBalance(client, receiverChildVASPAccount));
    }
}
