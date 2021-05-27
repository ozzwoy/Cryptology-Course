package cyb.cryptology.basic_algorithms;

import java.math.BigInteger;
import java.util.Random;

public abstract class RandomBigIntegerGenerator {
    private static final Random random = new Random();

    public static BigInteger generate(int length, BigInteger leftLimitExclusive, BigInteger rightLimitExclusive) {
        BigInteger a;

        while (true) {
            a = new BigInteger(length, random);
            if (a.compareTo(leftLimitExclusive) > 0 && a.compareTo(rightLimitExclusive) < 0) {
                return a;
            }
        }
    }
}
