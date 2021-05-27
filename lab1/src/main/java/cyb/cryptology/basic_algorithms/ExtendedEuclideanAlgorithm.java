package cyb.cryptology.basic_algorithms;

import java.math.BigInteger;

public abstract class ExtendedEuclideanAlgorithm {

    public static EuclideanResult gcd(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) {
            return new EuclideanResult(a, BigInteger.ONE, BigInteger.ZERO);
        }
        EuclideanResult current = gcd(b, a.mod(b));

        return new EuclideanResult(current.getGcd(), current.getY(),
                current.getX().subtract(a.divide(b).multiply(current.getY())));
    }
}
