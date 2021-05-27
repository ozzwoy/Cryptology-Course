package cyb.cryptology.basic_algorithms;

import java.math.BigInteger;

public abstract class MillerRabinPrimalityTest {

    private static boolean witness(BigInteger a, BigInteger modulo) {
        // even number is not prime
        if (!modulo.testBit(0)) {
            return true;
        }

        BigInteger power = modulo.subtract(BigInteger.ONE);
        int powerOfTwo = 0;

        for (; powerOfTwo < power.bitLength(); powerOfTwo++) {
            if (power.testBit(powerOfTwo)) {
                break;
            }
        }
        // n - 1 = 2 ^ t * u, where u is odd
        BigInteger u = power.shiftRight(powerOfTwo);
        BigInteger prev = RepeatedSquaringModularExponentiation.pow(a, u, modulo);
        BigInteger current = BigInteger.ONE;

        for (int i = 0; i < powerOfTwo; i++) {
            current = prev.pow(2).mod(modulo);
            // if non-trivial square root of 1 is found then n is not prime
            if (current.equals(BigInteger.ONE) && !prev.equals(BigInteger.ONE) && !prev.equals(power)) {
                return true;
            }
        }

        return !current.equals(BigInteger.ONE);
    }

    public static boolean isPrime(BigInteger number, int numOfIterations) {
        if (number.equals(BigInteger.ONE)) {
            return false;
        }
        if (number.equals(BigInteger.TWO)) {
            return true;
        }

        BigInteger testNumber;

        for (int i = 0; i < numOfIterations; i++) {
            testNumber = RandomBigIntegerGenerator.generate(number.bitLength(), BigInteger.ZERO, number);
            if (witness(testNumber, number)) {
                return false;
            }
        }

        return true;
    }
}
