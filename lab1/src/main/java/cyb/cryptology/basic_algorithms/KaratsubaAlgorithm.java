package cyb.cryptology.basic_algorithms;

import java.math.BigInteger;

public abstract class KaratsubaAlgorithm {
    private static final int LIMIT = 10;

    public static BigInteger multiply(BigInteger left, BigInteger right) {
        int length = Math.max(left.bitLength(), right.bitLength());
        if (length < LIMIT) {
            return left.multiply(right);
        }

        length /= 2;
        // left = a * x + b,   right = c * x + d, where x = 10 ^ length
        BigInteger a = left.shiftRight(length);
        BigInteger b = left.subtract(a.shiftLeft(length));
        BigInteger c = right.shiftRight(length);
        BigInteger d = right.subtract(c.shiftLeft(length));

        // AB = ac * x ^ 2 + ((a + b)(c + d) - ac - bd) * x + bd, where x = 10 ^ length
        BigInteger first = multiply(a, c);
        BigInteger third = multiply(b, d);
        BigInteger second = multiply(a.add(b), c.add(d)).subtract(first).subtract(third);

        return third.add(second.shiftLeft(length)).add(first.shiftLeft(length * 2));
    }
}
