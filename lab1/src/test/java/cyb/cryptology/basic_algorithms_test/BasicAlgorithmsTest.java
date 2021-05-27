package cyb.cryptology.basic_algorithms_test;

import cyb.cryptology.basic_algorithms.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

public class BasicAlgorithmsTest {

    @Test
    public void testExtendedEuclideanAlgorithm() {
        BigInteger a = BigInteger.valueOf(99);
        BigInteger b = BigInteger.valueOf(78);
        EuclideanResult result = ExtendedEuclideanAlgorithm.gcd(a, b);

        // -11 * 99 + 14 * 78 = 3
        Assertions.assertEquals(result.getGcd(), BigInteger.valueOf(3));
        Assertions.assertEquals(result.getX(), BigInteger.valueOf(-11));
        Assertions.assertEquals(result.getY(), BigInteger.valueOf(14));
    }

    @Test
    public void testRepeatedSquaringModularExponentiation() {
        BigInteger number = BigInteger.valueOf(7);
        BigInteger power = BigInteger.valueOf(560);
        BigInteger modulo = BigInteger.valueOf(561);

        // 7 ^ 560 (mod 561) = 1
        Assertions.assertEquals(RepeatedSquaringModularExponentiation.pow(number, power, modulo), BigInteger.ONE);
    }

    @Test
    public void testMontgomeryModularExponentiation() {
        BigInteger number = BigInteger.valueOf(7);
        BigInteger power = BigInteger.valueOf(560);
        BigInteger modulo = BigInteger.valueOf(561);
        MontgomeryArithmetics montgomery = new MontgomeryArithmetics(modulo);

        // 7 ^ 560 (mod 561) = 1
        Assertions.assertEquals(montgomery.pow(number, power), BigInteger.ONE);
    }

    @Test
    public void testKaratsubaMultiplication() {
        BigInteger a = BigInteger.valueOf(254165587159L);
        BigInteger b = BigInteger.valueOf(652188247931L);

        Assertions.assertEquals(KaratsubaAlgorithm.multiply(a, b), a.multiply(b));
    }

    @Test
    public void testFermatPrimalityTest() {
        BigInteger prime = BigInteger.valueOf(524287);
        BigInteger composite = BigInteger.valueOf(6);
        int iterations = 100;

        Assertions.assertFalse(FermatPrimalityTest.isPrime(BigInteger.ONE, iterations));
        Assertions.assertTrue(FermatPrimalityTest.isPrime(BigInteger.TWO, iterations));
        Assertions.assertTrue(FermatPrimalityTest.isPrime(prime, iterations));
        Assertions.assertFalse(FermatPrimalityTest.isPrime(composite, iterations));
    }

    @Test
    public void testMillerRabinPrimalityTest() {
        BigInteger prime = BigInteger.valueOf(524287);
        BigInteger composite = BigInteger.valueOf(6);
        int iterations = 100;

        Assertions.assertFalse(MillerRabinPrimalityTest.isPrime(BigInteger.ONE, iterations));
        Assertions.assertTrue(MillerRabinPrimalityTest.isPrime(BigInteger.TWO, iterations));
        Assertions.assertTrue(MillerRabinPrimalityTest.isPrime(prime, iterations));
        Assertions.assertFalse(MillerRabinPrimalityTest.isPrime(composite, iterations));
    }

}
