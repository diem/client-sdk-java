package org.libra.librasdk;

import org.bitcoinj.core.AddressFormatException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.libra.librasdk.Utils.intToUInt8;
import static org.libra.librasdk.Utils.mergeArrays;

public class BechBenny32 {
    private final String CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

    int[] generator = new int[]{0x3b6a57b2, 0x26508e6d, 0x1ea119fa, 0x3d4233dd, 0x2a1462b3};

    private int polymod(int[] values) {
        int check = 1;
        for (int value : values) {
            int top = check >> 25;
            check = (check & 0x1ffffff) << 5 ^ value;
            for (int j = 0; j < 5; j++) {
                if (((top >> Utils.intToUInt8(j)) & 1) == 1) {
                    check ^= generator[j];
                }
            }

        }

        return check;
    }

    private int[] hrpExpand(String hrp) {
        List<Integer> result = new ArrayList<>();

        char[] chars = hrp.toCharArray();
        for (char aChar : chars) {
            result.add((int) aChar >> 5);
//            result.add(aChar>>5);
        }

        result.add(0);

        for (char aChar : chars) {
            result.add((int) aChar & 31);
        }
        return result.stream().mapToInt(i -> i).toArray();
    }

    boolean verifyChecksum(String hrp, int[] data) {
        int[] mergeArrays = mergeArrays(hrpExpand(hrp), data);
        return polymod(mergeArrays) == 1;
    }

    int[] createChecksum(String hrp, int[] data) {
        int[] values = mergeArrays(hrpExpand(hrp), data, new int[]{0, 0, 0, 0, 0, 0});
        int mod = polymod(values) ^ 1;
        int[] result = new int[6];
        for (int i = 0; i < result.length; i++) {
            result[i] = (mod >> intToUInt8(5 * (5 - i))) & 31;
        }

        return result;
    }

    // Encode encodes hrp(human-readable part) and data(32bit data array), returns Bech32 / or error
    // if hrp is uppercase, return uppercase Bech32
    public String encode(String hrp, int[] data) throws LibraSDKException {
        if ((hrp.length() + data.length + 7) > 90) {
            throw new LibraSDKException(String.format("too long : hrp length=%d, data length=%d",
                    hrp.length(), data.length));
        }

        if (hrp.length() < 1) {
            throw new LibraSDKException(String.format("invalid hrp : hrp=%s", hrp));
        }

        char[] chars = hrp.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            if (aChar < 33 || aChar > 126) {
                throw new LibraSDKException(String.format("invalid character human-readable part "
                        + ": hrp[%d]=%s", i, aChar));
            }
        }

        String hrpLowerCase = hrp.toLowerCase();
        boolean isLowerCase = hrpLowerCase.equals(hrp);
        boolean isUpperCase = hrp.toUpperCase().equals(hrp);
        if (!isLowerCase && !isUpperCase) {
            throw new LibraSDKException(String.format("mix case : hrp=%s", hrp));
        }

        int[] mergeArrays = mergeArrays(data, createChecksum(hrpLowerCase, data));
        StringBuilder builder = new StringBuilder();
        builder.append(hrp);
        builder.append("1");

        for (int i = 0; i < mergeArrays.length; i++) {
            int p = mergeArrays[i];
            if (p < 0 || p >= CHARSET.length()) {
                throw new LibraSDKException(String.format("invalid data : data[%d]=%d", i, p));

            }
            builder.append(CHARSET.toCharArray()[p]);
        }

        return isUpperCase ? builder.toString().toUpperCase() : builder.toString();
    }

    // Decode decodes bechString(Bech32) returns hrp(human-readable part) and data(32bit data
    // array) / or error
    Bech32Decoded decode(String bechString) throws LibraSDKException {
        if (bechString.length() > 90) {
            throw new LibraSDKException(String.format("too long : len=%d", bechString.length()));
        }

        String bechLowerCase = bechString.toLowerCase();
        boolean isLowerCase = bechLowerCase.equals(bechString);
        boolean isUpperCase = bechString.toUpperCase().equals(bechString);
        if (!isLowerCase && !isUpperCase) {
            throw new LibraSDKException("mixed case");
        }

        int pos = bechLowerCase.lastIndexOf("1");
        if (pos < 1 || pos + 7 > bechString.length()) {
            throw new LibraSDKException(String.format("separator '1' at invalid position : " +
                    "pos=%d" + " , len=%d", pos, bechString.length()));
        }

        String hrp = bechString.substring(0, pos);
        char[] chars = hrp.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] < 33 || chars[i] > 126) {
                throw new LibraSDKException(String.format("invalid character human-readable part "
                        + ": bechString[%d]=%s", i, chars[i]));
            }
        }
        List<Integer> data = new ArrayList<>();

        for (int p = pos + 1; p < bechString.length(); p++) {
            int d = CHARSET.indexOf(bechString.toCharArray()[p]);
            if (d == -1) {
                throw new LibraSDKException(String.format("invalid character data part : " +
                        "bechString[%d]=%s", p, bechString.toCharArray()[p]));
            }
            data.add(d);
        }

        int[] dataInt = data.stream().mapToInt(i -> i).toArray();
        if (!verifyChecksum(hrp, dataInt)) {
            throw new LibraSDKException("invalid checksum");
        }

        int[] range = Arrays.copyOfRange(dataInt, 0, dataInt.length - 6);
        return new Bech32Decoded(hrp, range);
    }

    public static class Bech32Decoded {
        String hrp;
        int[] data;

        public Bech32Decoded(String hrp, int[] data) {
            this.hrp = hrp;
            this.data = data;
        }
    }

    public static byte[] convertBits(final Integer[] in, final int inStart, final int inLen,
                                     final int fromBits, final int toBits, final boolean pad) throws AddressFormatException {
        int acc = 0;
        int bits = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream(64);
        final int maxv = (1 << toBits) - 1;
        final int max_acc = (1 << (fromBits + toBits - 1)) - 1;
        for (int i = 0; i < inLen; i++) {
            int value = in[i + inStart] & 0xff;
            if ((value >>> fromBits) != 0) {
                throw new AddressFormatException(String.format("Input value '%X' exceeds '%d' bit" +
                        " size", value, fromBits));
            }
            acc = ((acc << fromBits) | value) & max_acc;
            bits += fromBits;
            while (bits >= toBits) {
                bits -= toBits;
                out.write((acc >>> bits) & maxv);
            }
        }
        if (pad) {
            if (bits > 0) out.write((acc << (toBits - bits)) & maxv);
        } else if (bits >= fromBits || ((acc << (toBits - bits)) & maxv) != 0) {
            throw new AddressFormatException("Could not convert bits, invalid padding");
        }
        return out.toByteArray();
    }
}
