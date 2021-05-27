package cyb.cryptology.basic_algorithms;

import java.math.BigInteger;

public abstract class RepeatedSquaringModularExponentiation {

    public static BigInteger pow(BigInteger number, BigInteger power, BigInteger modulo) {
        BigInteger result = BigInteger.ONE;
        BigInteger current = number;
        int length = power.bitLength();

        for (int i = 0; i < length; i++) {
            if (power.testBit(i)) {
                result = result.multiply(current).mod(modulo);
            }
            current = current.pow(2).mod(modulo);
        }

        return result;
    }
}
