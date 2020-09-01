package org.libra.librasdk.libraid;

import org.junit.Before;
import org.junit.Test;
import org.libra.librasdk.LibraSDKException;
import org.libra.librasdk.Utils;
import org.libra.types.AccountAddress;

import static org.junit.Assert.assertEquals;
import static org.libra.librasdk.libraid.Account.NetworkPrefix.MainnetPrefix;
import static org.libra.librasdk.libraid.Intent.decodeToIntent;

public class IntentTest {
    Account account;
    String encodedAccount;

    @Before
    public void setUp() throws Exception {
        AccountAddress accountAddress = Utils.hexToAddress("f72589b71ff4f8d139674a3f7369c69b");
        SubAddress subAddress = new SubAddress("cf64428bdeb62af2");
        account = new Account(MainnetPrefix, accountAddress, subAddress);
        encodedAccount = account.encode();
    }

    @Test
    public void decodeToIntent_withoutParams() throws LibraSDKException {
        Intent intent = new Intent(account);
        String intentEncoded = intent.encode();
        assertEquals(String.format("libra://%s", encodedAccount), intentEncoded);

        Intent intent1 = decodeToIntent(MainnetPrefix, intentEncoded);
        assertEquals(intent, intent1);
    }

//    @Test
//    public void decodeToIntent() throws LibraSDKException {
//
//
//        Bech32.Bech32Data a12UEL5L = Bech32.decode("A12UEL5L");
//        System.out.println(a12UEL5L.hrp);
//
//        String a = Bech32.encode("a", new byte[0]);
//        System.out.println(a);
//
//
//        Bech32.Bech32Data x = Bech32.decode("abcdef1qpzry9x8gf2tvdw0s3jn54khce6mua7lmqqqxw");
//        System.out.println(x.hrp);
//
//        List<Byte> list = new ArrayList<>();
//        for (int i = 0; i < 32; i++) {
//            list.add((byte)i);
//        }
//
//        Byte[] bytes = list.toArray(new Byte[0]);
//        byte[] bytes1 = ArrayUtils.toPrimitive(bytes);
//        String abcdef = Utils.Bech32Encode("abcdef", bytes1);
//
//        System.out.println(abcdef);
//
//
//    }
}