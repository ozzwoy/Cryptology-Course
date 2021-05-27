package cyb.cryptology.basic_algorithms;

import java.math.BigInteger;

public class MontgomeryArithmetics {
    private final BigInteger modulo;
    private final BigInteger denominator;
    private final BigInteger moduloCoefficient;

    public MontgomeryArithmetics(BigInteger modulo) {
        this.modulo = modulo;
        this.denominator = BigInteger.ONE.shiftLeft(modulo.bitLength());
        EuclideanResult result = ExtendedEuclideanAlgorithm.gcd(modulo, denominator);
        this.moduloCoefficient = result.getX().negate();
    }

    public BigInteger montgomeryProduct(BigInteger a, BigInteger b) {
        BigInteger t = a.multiply(b);
        BigInteger current = t.multiply(moduloCoefficient);

        // (t * n') % r, where r = 2 ^ k
        if (current.bitLength() >= denominator.bitLength()) {
            // from 100...0 to 011...1
            BigInteger mask = denominator.subtract(BigInteger.ONE);
            current = current.and(mask);
        }

        // current * n + t
        current = current.multiply(modulo).add(t);
        // current / r, where r = 2 ^ k
        current = current.shiftRight(denominator.bitLength() - 1);

        // r > n => current < 2 * n
        if (current.compareTo(modulo) > 0) {
            current = current.subtract(modulo);
        }

        return current;
    }

    private BigInteger getNResidue(BigInteger number) {
        // (number * r) mod n, where r = 2 ^ k
        return number.shiftLeft(denominator.bitLength() - 1).mod(modulo);
    }

    public BigInteger pow(BigInteger number, BigInteger power) {
        BigInteger a = getNResidue(number);
        BigInteger current = getNResidue(BigInteger.ONE);

        for (int i = power.bitLength() - 1; i >= 0; i--) {
            current = montgomeryProduct(current, current);
            if (power.testBit(i)) {
                current = montgomeryProduct(current, a);
            }
        }

        return montgomeryProduct(current, BigInteger.ONE);
    }
}
