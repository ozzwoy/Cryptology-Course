package cyb.cryptology.loki97;

import java.math.BigInteger;

public abstract class Utils {

    public static long bytesToUnsignedLong(byte[] bytes, int startIndex) {
        long result = 0L;

        for (int i = 0; i < 8; i++) {
            result |= (bytes[startIndex + i] & 0xFFL) << 8 * (7 - i);
        }

        return result;
    }

    public static byte[] unsignedLongToBytes(long number) {
        byte[] result = new byte[8];

        for (int i = 0; i < 8; i++) {
            result[i] = (byte) ((number >>> (7 - i) * 8) & 0xFF);
        }

        return result;
    }

    public static int hexCharToInt(char ch) {
        if (ch >= '0' && ch <= '9') {
            return (ch - '0') & 0xF;
        } else if (ch >= 'A' && ch <= 'F') {
            return (ch - 'A' + 10) & 0xF;
        } else if (ch >= 'a' && ch <= 'f') {
            return (ch - 'a' + 10) & 0xF;
        } else {
            return 0;
        }
    }

    public static char intToHexChar(int n) {
        if (n >= 0 && n <= 9) {
            return (char) ('0' + n);
        } else if (n < 16) {
            return (char) ('A' + (n - 10));
        } else {
            return ' ';
        }
    }

    public static long hexStringToUnsignedLong(String hex) {
        long result = 0L;

        for (int i = 0; i < 16; i++) {
            result |= (((long) hexCharToInt(hex.charAt(i))) << 4 * (15 - i));
        }

        return result;
    }

    public static byte[] hexStringToBytes(String str) {
        byte[] result = new byte[str.length() / 2];

        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) ((hexCharToInt(str.charAt(2 * i)) << 4) |
                                (hexCharToInt(str.charAt(2 * i + 1))));
        }

        return result;
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();

        for (byte b : bytes) {
            builder.append(intToHexChar(b >>> 4 & 0xF));
            builder.append(intToHexChar(b & 0xF));
        }

        return builder.toString();
    }

    /* Returns the product of two binary numbers a and b, using the
     * generator g as the modulus: p = (a * b) mod g. g generates a
     * suitable Galois Field in GF(2^n). */
    public static int multiplyInField(int a, int b, int g, int n) {
        int p = 0;

        while (b != 0) {
            if ((b & 0x01) != 0) {
                p ^= a;
            }
            a <<= 1;
            if (a >= n) {
                a ^= g;
            }
            b >>>= 1;
        }

        return p;
    }

    /* b ^ 3 mod g in GF(2^n) */
    public static int pow3InField(int b, int g, int n) {
        int result = b;
        if (b == 0)
            return 0;

        b = multiplyInField(result, b, g, n);
        result = multiplyInField(result, b, g, n);

        return result;
    }

    public static long add64(long left, long right) {
        BigInteger bigLeft = BigInteger.valueOf(left);
        BigInteger bigRight = BigInteger.valueOf(right);
        BigInteger modulo = BigInteger.TWO.pow(64);

        if (left < 0) {
            bigLeft = bigLeft.negate().setBit(bigLeft.bitLength());
        }
        if (right < 0) {
            bigRight = bigRight.negate().setBit(bigRight.bitLength());
        }

        return bigLeft.add(bigRight).mod(modulo).longValue();
    }

    public static long subtract64(long left, long right) {
        BigInteger bigLeft = BigInteger.valueOf(left);
        BigInteger bigRight = BigInteger.valueOf(right);
        BigInteger modulo = BigInteger.TWO.pow(64);

        if (left < 0) {
            bigLeft = bigLeft.negate().setBit(bigLeft.bitLength());
        }
        if (right < 0) {
            bigRight = bigRight.negate().setBit(bigRight.bitLength());
        }

        return bigLeft.subtract(bigRight).mod(modulo).longValue();
    }

    public static long multiply64(long left, long right) {
        BigInteger bigLeft = BigInteger.valueOf(left);
        BigInteger bigRight = BigInteger.valueOf(right);
        BigInteger modulo = BigInteger.TWO.pow(64);

        if (left < 0) {
            bigLeft = bigLeft.negate().setBit(bigLeft.bitLength());
        }
        if (right < 0) {
            bigRight = bigRight.negate().setBit(bigRight.bitLength());
        }

        return bigLeft.multiply(bigRight).mod(modulo).longValue();
    }
}
