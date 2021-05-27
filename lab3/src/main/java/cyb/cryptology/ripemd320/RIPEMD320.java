package cyb.cryptology.ripemd320;

public abstract class RIPEMD320 {
    // indices for choosing 32-bit words in a message
    private static final int[] R1 = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
            7, 4, 13, 1, 10, 6, 15, 3, 12, 0, 9, 5, 2, 14, 11, 8,
            3, 10, 14, 4, 9, 15, 8, 1, 2, 7, 0, 6, 13, 11, 5, 12,
            1, 9, 11, 10, 0, 8, 12, 4, 13, 3, 7, 15, 14, 5, 6, 2,
            4, 0, 5, 9, 7, 12, 2, 10, 14, 1, 3, 8, 11, 6, 15, 13
    };

    private static final int[] R2 = {
            5, 14, 7, 0, 9, 2, 11, 4, 13, 6, 15, 8, 1, 10, 3, 12,
            6, 11, 3, 7, 0, 13, 5, 10, 14, 15, 8, 12, 4, 9, 1, 2,
            15, 5, 1, 3, 7, 14, 6, 9, 11, 8, 12, 2, 10, 0, 4, 13,
            8, 6, 4, 1, 3, 11, 15, 0, 5, 12, 2, 13, 9, 7, 10, 14,
            12, 15, 10, 4, 1, 5, 8, 7, 6, 2, 13, 14, 0, 3, 9, 11
    };

    // sums for left cyclic shift
    private static final int[] S1 = {
            11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8,
            7, 6, 8, 13, 11, 9, 7, 15, 7, 12, 15, 9, 11, 7, 13, 12,
            11, 13, 6, 7, 14, 9, 13, 15, 14, 8, 13, 6, 5, 12, 7, 5,
            11, 12, 14, 15, 14, 15, 9, 8, 9, 14, 5, 6, 8, 6, 5, 12,
            9, 15, 5, 11, 6, 8, 13, 12, 5, 12, 13, 14, 11, 8, 5, 6
    };

    private static final int[] S2 = {
            8, 9, 9, 11, 13, 15, 15, 5, 7, 7, 8, 11, 14, 14, 12, 6,
            9, 13, 15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11,
            9, 7, 15, 11, 8, 6, 6, 14, 12, 13, 5, 14, 13, 13, 7, 5,
            15, 5, 8, 11, 14, 14, 6, 14, 6, 9, 12, 9, 12, 5, 15, 8,
            8, 5, 12, 9, 12, 5, 14, 6, 8, 13, 6, 5, 15, 13, 11, 11
    };

    // initial values of digest words
    private static final int[] H = {
            0x67452301,
            0xEFCDAB89,
            0x98BADCFE,
            0x10325476,
            0xC3D2E1F0,
            0x76543210,
            0xFEDCBA98,
            0x89ABCDEF,
            0x01234567,
            0x3C2D1E0F
    };

    // constants for addition
    private static int k1(int j) {
        if (j <= 15) {
            return 0x00000000;
        } else if (j <= 31) {
            return 0x5A827999;
        } else if (j <= 47) {
            return 0x6ED9EBA1;
        } else if (j <= 63) {
            return 0x8F1BBCDC;
        } else {
            return 0xA953FD4E;
        }
    }

    private static int k2(int j) {
        if (j <= 15) {
            return 0x50A28BE6;
        } else if (j <= 31) {
            return 0x5C4DD124;
        } else if (j <= 47) {
            return 0x6D703EF3;
        } else if (j <= 63) {
            return 0x7A6D76E9;
        } else {
            return 0x00000000;
        }
    }

    // nonlinear bitwise functions
    private static int f(int j, int x, int y, int z) {
        if (j <= 15) {
            return x ^ y ^ z;
        } else if (j <= 31) {
            return (x & y) | (~x & z);
        } else if (j <= 47) {
            return (x | ~y) ^ z;
        } else if (j <= 63) {
            return (x & z) | (y & ~z);
        } else {
            return x ^ (y | ~z);
        }
    }

    private static byte[] expand(byte[] bytes) {
        int length = bytes.length;
        // using bit length
        int remainder = (length << 3) % 512;

        int numToAdd;
        // make length = 448 (mod 512)
        if (remainder < 448) {
            numToAdd = 448 - remainder;
        } else {
            numToAdd = 512 - remainder + 448;
        }
        // byte length
        numToAdd >>= 3;

        // 8 additional bytes for length
        int newLength = length + numToAdd + 8;
        byte[] newBytes = new byte[newLength];
        System.arraycopy(bytes, 0, newBytes, 0, length);

        newBytes[length] = (byte) 0x80;
        for (int i = length + 1; i < newLength - 8; i++) {
            newBytes[i] = 0x00;
        }

        // bit length
        length = length << 3;
        for (int i = newLength - 8; i < newLength; i++) {
            newBytes[i] = (byte) (length & 0xFF);
            length >>>= 8;
        }

        return newBytes;
    }

    private static int cyclicLeftShift(int operand, int shift) {
        return operand << shift | operand >>> (32 - shift);
    }

    public static String hash(String value) {
        byte[] bytes = expand(value.getBytes());

        int[] h = H.clone();
        int a1, b1, c1, d1, e1, a2, b2, c2, d2, e2, t;
        int[] x = new int[16];

        // process 16 words of 32 bits each (16 integers)
        for (int i = 0; i < bytes.length / 64; i++) {
            a1 = h[0];
            b1 = h[1];
            c1 = h[2];
            d1 = h[3];
            e1 = h[4];
            a2 = h[5];
            b2 = h[6];
            c2 = h[7];
            d2 = h[8];
            e2 = h[9];

            for (int j = 0; j < 16; j++) {
                // converse bytes into unsigned integers and then into one integer
                x[j] = (bytes[i * 64 + j * 4] & 0xFF) |
                        (bytes[i * 64 + j * 4 + 1] & 0xFF) << 8 |
                        (bytes[i * 64 + j * 4 + 2] & 0xFF) << 16 |
                        bytes[i * 64 + j * 4 + 3] << 24;
            }

            // rounds 0...15
            for (int j = 0; j < 80; j++) {
                t = a1 + f(j, b1, c1, d1) + x[R1[j]] + k1(j);
                t = cyclicLeftShift(t, S1[j]) + e1;
                a1 = e1;
                e1 = d1;
                d1 = cyclicLeftShift(c1, 10);
                c1 = b1;
                b1 = t;

                t = a2 + f(79 - j, b2, c2, d2) + x[R2[j]] + k2(j);
                t = cyclicLeftShift(t, S2[j]) + e2;
                a2 = e2;
                e2 = d2;
                d2 = cyclicLeftShift(c2, 10);
                c2 = b2;
                b2 = t;

                if (j == 15) {
                    t = b1;
                    b1 = b2;
                    b2 = t;
                } else if (j == 31) {
                    t = d1;
                    d1 = d2;
                    d2 = t;
                } else if (j == 47) {
                    t = a1;
                    a1 = a2;
                    a2 = t;
                } else if (j == 63) {
                    t = c1;
                    c1 = c2;
                    c2 = t;
                } else if (j == 79) {
                    t = e1;
                    e1 = e2;
                    e2 = t;
                }
            }

            h[0] += a1;
            h[1] += b1;
            h[2] += c1;
            h[3] += d1;
            h[4] += e1;
            h[5] += a2;
            h[6] += b2;
            h[7] += c2;
            h[8] += d2;
            h[9] += e2;
        }

        byte[] resultBytes = new byte[40];
        for (int i = 0; i < resultBytes.length / 4; i++) {
            resultBytes[i * 4] = (byte) h[i];
            resultBytes[i * 4 + 1] = (byte) (h[i] >>> 8);
            resultBytes[i * 4 + 2] = (byte) (h[i] >>> 16);
            resultBytes[i * 4 + 3] = (byte) (h[i] >>> 24);
        }

        StringBuilder builder = new StringBuilder();
        for (byte b : resultBytes) {
            if (b == 0) {
                builder.append("00");
            } else if (b > 0 && b <= 15) {
                builder.append("0").append(Integer.toHexString(b & 0xFF));
            } else {
                builder.append(Integer.toHexString(b & 0xFF));
            }
        }

        return builder.toString();
    }
}
