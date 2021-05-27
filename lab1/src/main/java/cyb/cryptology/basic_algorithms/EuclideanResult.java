package cyb.cryptology.basic_algorithms;

import java.math.BigInteger;

public class EuclideanResult {
    private BigInteger gcd;
    private BigInteger x;
    private BigInteger y;

    public EuclideanResult() {}

    public EuclideanResult(BigInteger gcd, BigInteger x, BigInteger y) {
        this.gcd = gcd;
        this.x = x;
        this.y = y;
    }

    public BigInteger getGcd() {
        return gcd;
    }

    public BigInteger getX() {
        return x;
    }

    public BigInteger getY() {
        return y;
    }

    public void setGcd(BigInteger gcd) {
        this.gcd = gcd;
    }

    public void setX(BigInteger x) {
        this.x = x;
    }

    public void setY(BigInteger y) {
        this.y = y;
    }
}
