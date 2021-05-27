package cyb.cryptology.basic_algorithms;

import java.math.BigInteger;

public abstract class FermatPrimalityTest {

    public static boolean isPrime(BigInteger number, int numOfIterations) {
        if (number.equals(BigInteger.ONE)) {
            return false;
        }
        if (number.equals(BigInteger.TWO)) {
            return true;
        }

        BigInteger testNumber;
        BigInteger power = number.subtract(BigInteger.ONE);

        for (int i = 0; i < numOfIterations; i++) {
            // a > 1 and a < (n - 1) implies that a is not divisible by n and trivial square roots of 1 are excluded
            testNumber = RandomBigIntegerGenerator.generate(number.bitLength(), BigInteger.ONE, power);
            // a ^ (n - 1) == 1 (mod n) ?
            boolean prime = RepeatedSquaringModularExponentiation.pow(testNumber, power, number)
                                                                 .mod(number)
                                                                 .equals(BigInteger.ONE);
            if (!prime) {
                return false;
            }
        }

        return true;
    }
}
