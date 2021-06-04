package cyb.cryptology.loki97;

public abstract class LOKI97 {
    /* Size of S-box S1 (2^13) */
    private static final int S1_SIZE = 0x2000;
    /* Generator polynomial for S-box S1 in GF(2^13) */
    private static final int S1_GEN = 0x2911;
    private static final int S1_MASK = S1_SIZE - 1;

    /* Size of S-box S2 (2^11) */
    private static final int S2_SIZE = 0x800;
    /* Generator polynomial for S-box S2 in GF(2^11) */
    private static final int S2_GEN = 0xAA7;
    private static final int S2_MASK = S2_SIZE - 1;

    /* Table of permutation P */
    private static final int[] PERMUTATION_TABLE = {
            56, 48,	40,	32,	24,	16,	8,  0,
            57, 49, 41, 33, 25, 17, 9,  1,
            58,	50,	42,	34,	26,	18,	10,	2,
            59, 51, 43, 35, 27, 19, 11, 3,
            60,	52,	44,	36,	28,	20,	12,	4,
            61, 53, 45, 37, 29, 21, 13, 5,
            62,	54,	46,	38,	30,	22,	14,	6,
            63, 55, 47, 39, 31, 23, 15, 7
    };

    private static final long DELTA = 0x9E3779B97F4A7C15L;

    /* Function for shuffling bits. By exchanging bits with an intermediate key
    and part of the input data, the KP function shuffles the bits to complicate
    the process of matching the input and output data coming from and to the
    S-boxes. */
    private static long kp(long a, long b) {
        long aLeft = a >>> 32;
        long aRight = a & 0xFFFFFFFFL;
        long bRight = b & 0xFFFFFFFFL;

        long resultLeft = (aLeft & ~bRight) | (aRight & bRight);
        long resultRight = (aRight & ~bRight) | (aLeft & bRight);

        return (resultLeft << 32) | resultRight;
    }

    /* S1(A) does cubing A by modulo S1_GEN, S1_GEN is irreducible polynomial in GF(2^13), output is 8-bit word */
    private static long s1(int a) {
        return Utils.pow3InField(a ^ S1_MASK, S1_GEN, S1_SIZE) & 0xFFL;
    }

    /* S2(A) does cubing A by modulo S2_GEN, S2_GEN is irreducible polynomial in GF(2^11), output is 8-bit word */
    private static long s2(int a) {
        return Utils.pow3InField(a ^ S2_MASK, S2_GEN, S2_SIZE) & 0xFFL;
    }

    /* E(A) splits 64-bit word A into chunks for S1 or S2 input, 96 bits overall */
    private static int e(long a, int chunk_num) {
        if (chunk_num == 0) {
            // [4-0 | 63-56]
            return ((int) (a & 0x1F)) << 8 | ((int) ((a >>> 56) & 0xFF));
        } else if (chunk_num == 1) {
            // [58-48]
            return (int) ((a >>> 48) & S2_MASK);
        } else if (chunk_num == 2) {
            // [52-40]
            return (int) ((a >>> 40) & S1_MASK);
        } else if (chunk_num == 3) {
            // [42-32]
            return (int) ((a >>> 32) & S2_MASK);
        } else if (chunk_num == 4) {
            // [34-24]
            return (int) ((a >>> 24) & S2_MASK);
        } else if (chunk_num == 5) {
            // [28-16]
            return (int) ((a >>> 16) & S1_MASK);
        } else if (chunk_num == 6) {
            // [18-8]
            return (int) ((a >>> 8) & S2_MASK);
        } else if (chunk_num == 7) {
            // [12-0]
            return (int) (a & S1_MASK);
        } else {
            return 0;
        }
    }

    /* Sa(A) computes S-box column [S1, S2, S1, S2, S2, S1, S2, S1], A is split on chunks by E(),
    output is 64-bit word */
    private static long sa(long a) {
        long result = 0;

        result |= s1(e(a, 0)) << 56;
        result |= s2(e(a, 1)) << 48;
        result |= s1(e(a, 2)) << 40;
        result |= s2(e(a, 3)) << 32;
        result |= s2(e(a, 4)) << 24;
        result |= s1(e(a, 5)) << 16;
        result |= s2(e(a, 6)) << 8;
        result |= s1(e(a, 7));

        return result;
    }

    /* Sb(A, B) computes S-box column [S2, S2, S1, S1, S2, S2, S1, S1], A is 64-bit word,
    B is 32-bit word, output is 64-bit word */
    private static long sb(long a, long b) {
        long result = 0;

        result |= s2((int) ((b >>> 21 &  0x700) | (a >>> 56 & 0xFF))) << 56;
        result |= s2((int) ((b >>> 18 &  0x700) | (a >>> 48 & 0xFF))) << 48;
        result |= s1((int) ((b >>> 13 & 0x1F00) | (a >>> 40 & 0xFF))) << 40;
        result |= s1((int) ((b >>>  8 & 0x1F00) | (a >>> 32 & 0xFF))) << 32;
        result |= s2((int) ((b >>>  5 &  0x700) | (a >>> 24 & 0xFF))) << 24;
        result |= s2((int) ((b >>>  2 &  0x700) | (a >>> 16 & 0xFF))) << 16;
        result |= s1((int) ((b <<   3 & 0x1F00) | (a >>>  8 & 0xFF))) <<  8;
        result |= s1((int) ((b <<   8 & 0x1F00) | (a        & 0xFF)));

        return result;
    }

    /* P(A) is a permutation of the output of the function Sa(A) */
    private static long p(long a) {
        long result = 0L;
        long bit;

        for (int i = 0; i < 64; i++) {
            bit = (a & (0x01L << i)) >>> i;
            result |= bit << PERMUTATION_TABLE[63 - i];
        }

        return result;
    }

    /* f(A,B) = Sb(P(Sa(E(KP(A,B)))),r(B)) - complex non-linear round function */
    private static long f(long a, long b) {
        return sb(p(sa(kp(a, b))), b >>> 32);
    }

    /* Computes initial keys S_10, S_20, S_30, S_40 */
    private static long[] getInitialKeys(String key) {
        int length = key.length();
        long[] keys = new long[4];

        if (length == 32) {
            // length = 128 bits
            keys[3] = Utils.hexStringToUnsignedLong(key.substring(0, 16));
            keys[2] = Utils.hexStringToUnsignedLong(key.substring(16, 32));
            keys[1] = f(keys[2], keys[3]);
            keys[0] = f(keys[3], keys[2]);
        } else if (length == 48) {
            // length = 192 bits
            keys[3] = Utils.hexStringToUnsignedLong(key.substring(0, 16));
            keys[2] = Utils.hexStringToUnsignedLong(key.substring(16, 32));
            keys[1] = Utils.hexStringToUnsignedLong(key.substring(32, 48));
            keys[0] = f(keys[3], keys[2]);
        } else if (length == 64) {
            // length = 256 bits
            keys[3] = Utils.hexStringToUnsignedLong(key.substring(0, 16));
            keys[2] = Utils.hexStringToUnsignedLong(key.substring(16, 32));
            keys[1] = Utils.hexStringToUnsignedLong(key.substring(32, 48));
            keys[0] = Utils.hexStringToUnsignedLong(key.substring(48, 64));
        }

        return keys;
    }

    /* g(i, A, B, C) is used for generating intermediate session keys */
    private static long g(int i, long a, long b, long c) {
        return f(a + b + DELTA * i, c);
    }

    /* Computes SK_i session keys, i = 1...48 */
    private static long[] getSessionKeys(String key) {
        long[] result = new long[48];
        long[] currentKeys = getInitialKeys(key);

        for (int i = 0; i < 48; i++) {
            result[i] = currentKeys[3] ^ g(i + 1, currentKeys[0], currentKeys[2], currentKeys[1]);
            currentKeys[3] = currentKeys[2];
            currentKeys[2] = currentKeys[1];
            currentKeys[1] = currentKeys[0];
            currentKeys[0] = result[i];
        }

        return result;
    }

    private static boolean isKeyWrong(String key) {
        return !(key.length() % 16 == 0 || key.length() % 24 == 0 || key.length() % 32 == 0);
    }

    private static boolean isMessageWrong(String message) {
        return message.length() % 16 != 0;
    }

    public static String encrypt(String message, String key) {
        if (isMessageWrong(message) || isKeyWrong(key)) {
            return "";
        }

        byte[] bytes = Utils.hexStringToBytes(message);
        int numOfBlocks = bytes.length / 16;
        byte[] result = new byte[bytes.length];

        long[] sessionKeys = getSessionKeys(key);
        byte[] leftBytes;
        byte[] rightBytes;
        long left, right;
        long curLeft, curRight;
        long k1, k2, k3;

        for (int i = 0; i < numOfBlocks; i++) {
            left = Utils.bytesToUnsignedLong(bytes, i * 16);
            right = Utils.bytesToUnsignedLong(bytes, i * 16 + 8);

            for (int j = 0; j < 16; j++) {
                k1 = sessionKeys[3 * j];
                k2 = sessionKeys[3 * j + 1];
                k3 = sessionKeys[3 * j + 2];

                curRight = left ^ f(right + k1, k2);
                curLeft = right + k1 + k3;

                right = curRight;
                left = curLeft;
            }

            leftBytes = Utils.unsignedLongToBytes(left);
            rightBytes = Utils.unsignedLongToBytes(right);
            for (int k = 0; k < 8; k++) {
                result[i * 16 + k] = rightBytes[k];
                result[i * 16 + 8 + k] = leftBytes[k];
            }
        }

        return Utils.bytesToHexString(result);
    }

    public static String decrypt(String cipher, String key) {
        if (isMessageWrong(cipher) || isKeyWrong(key)) {
            return "";
        }

        byte[] bytes = Utils.hexStringToBytes(cipher);
        int numOfBlocks = bytes.length / 16;
        byte[] result = new byte[bytes.length];

        long[] sessionKeys = getSessionKeys(key);
        byte[] leftBytes;
        byte[] rightBytes;
        long left, right;
        long curLeft, curRight;
        long k1, k2, k3;

        for (int i = 0; i < numOfBlocks; i++) {
            right = Utils.bytesToUnsignedLong(bytes, i * 16);
            left = Utils.bytesToUnsignedLong(bytes, i * 16 + 8);

            for (int j = 0; j < 16; j++) {
                k1 = sessionKeys[3 * (15 - j) + 2];
                k2 = sessionKeys[3 * (15 - j) + 1];
                k3 = sessionKeys[3 * (15 - j)];

                curLeft = right ^ f(left - k1, k2);
                curRight = left - k1 - k3;

                right = curRight;
                left = curLeft;
            }

            leftBytes = Utils.unsignedLongToBytes(left);
            rightBytes = Utils.unsignedLongToBytes(right);
            for (int k = 0; k < 8; k++) {
                result[i * 16 + k] = leftBytes[k];
                result[i * 16 + 8 + k] = rightBytes[k];
            }
        }

        return Utils.bytesToHexString(result);
    }
}
