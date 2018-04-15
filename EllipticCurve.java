import java.math.BigInteger;
import java.math.MathContext;
import java.util.AbstractMap.SimpleImmutableEntry;

public class EllipticCurve {

    private static final String TAB = "    ";
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = new BigInteger("2");
    private static final BigInteger THREE = new BigInteger("3");

    public static BigInteger[] extendedEuclideanAlgorithm(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) {
            return new BigInteger[]{a, BigInteger.ONE, BigInteger.ZERO};
        }

        BigInteger u = BigInteger.ONE;
        BigInteger g = a;
        BigInteger x = BigInteger.ZERO;
        BigInteger y = b;

        while (!y.equals(BigInteger.ZERO)) {
            BigInteger t = g.mod(y);
            BigInteger q = g.subtract(t).divide(y);
            BigInteger s = u.subtract(q.multiply(x));

            u = x;
            g = y;
            x = s;
            y = t;
        }

        BigInteger v = (g.subtract(a.multiply(u))).divide(b);
        while (u.compareTo(BigInteger.ZERO) == -1) {
            u = u.add(b.divide(g));
            v = v.subtract(a.divide(g));
        }

        return new BigInteger[]{g, u, v};
    }

    private static SimpleImmutableEntry<BigInteger, BigInteger> calculateP2(BigInteger N, BigInteger x, BigInteger y, BigInteger a) {
        BigInteger top = THREE.multiply(x.pow(2)).add(a).mod(N);
        BigInteger bottom = TWO.multiply(y).modInverse(N);
        BigInteger lambda = top.multiply(bottom).mod(N);

        BigInteger nextX = lambda.multiply(lambda).subtract(TWO.multiply(x)).mod(N);
        BigInteger nextY = lambda.multiply(x.subtract(nextX)).subtract(y).mod(N);
        return new SimpleImmutableEntry<BigInteger, BigInteger>(nextX, nextY);
    }

    private static SimpleImmutableEntry<BigInteger, BigInteger> calculateNextPoint(
        BigInteger N,
        BigInteger firstX,
        BigInteger x,
        BigInteger firstY,
        BigInteger y
    ) {
        BigInteger top = y.subtract(firstY).mod(N);

        BigInteger bottom = x.subtract(firstX);
        BigInteger[] result = extendedEuclideanAlgorithm(bottom, N);
        if (!result[0].equals(ONE)) {
            return new SimpleImmutableEntry<BigInteger, BigInteger>(null, result[0]);
        }
        bottom = result[1];

        BigInteger lambda = top.multiply(bottom).mod(N);

        BigInteger nextX = lambda.multiply(lambda).subtract(x).subtract(firstX).mod(N);
        BigInteger nextY = lambda.multiply(firstX.subtract(nextX)).subtract(firstY).mod(N);
        return new SimpleImmutableEntry<BigInteger, BigInteger>(nextX, nextY);
    }

    private static BigInteger solve(BigInteger N, BigInteger a, BigInteger firstX, BigInteger firstY) {
        SimpleImmutableEntry<BigInteger, BigInteger> p2 = calculateP2(N, firstX, firstY, a);
        BigInteger curX = p2.getKey();
        BigInteger curY = p2.getValue();

        BigInteger d = null;
        while (d == null) {
            SimpleImmutableEntry<BigInteger, BigInteger> nextP = calculateNextPoint(N, firstX, curX, firstY, curY);
            if (nextP.getKey() == null) {
                d = nextP.getValue();
            }

            curX = nextP.getKey();
            curY = nextP.getValue();
        }
        return d;
    }

    public static void main(String[] args) {
        BigInteger N = new BigInteger("6887");
        BigInteger a = new BigInteger("14");
        BigInteger x = new BigInteger("1512");
        BigInteger y = new BigInteger("3166");
        BigInteger r = solve(N, a, x, y);
        System.out.println("Example From Book:");
        System.out.println(TAB + N + " = " + r + " * " + N.divide(r));
        System.out.println();

        System.out.println("5.18:");

        N = new BigInteger("589");
        a = new BigInteger("4");
        x = new BigInteger("2");
        y = new BigInteger("5");
        r = solve(N, a, x, y);
        System.out.println(TAB + "a:");
        System.out.println(TAB + TAB + N + " = " + r + " * " + N.divide(r));

        N = new BigInteger("26167");
        a = new BigInteger("4");
        x = new BigInteger("2");
        y = new BigInteger("12");
        r = solve(N, a, x, y);
        System.out.println(TAB + "b:");
        System.out.println(TAB + TAB + N + " = " + r + " * " + N.divide(r));

        N = new BigInteger("1386493");
        a = new BigInteger("3");
        x = new BigInteger("1");
        y = new BigInteger("1");
        r = solve(N, a, x, y);
        System.out.println(TAB + "c:");
        System.out.println(TAB + TAB + N + " = " + r + " * " + N.divide(r));

        N = new BigInteger("28102844557");
        a = new BigInteger("18");
        x = new BigInteger("7");
        y = new BigInteger("4");
        r = solve(N, a, x, y);
        System.out.println(TAB + "d:");
        System.out.println(TAB + TAB + N + " = " + r + " * " + N.divide(r));
    }
}

