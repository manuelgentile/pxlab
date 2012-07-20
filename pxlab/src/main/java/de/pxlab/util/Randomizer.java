package de.pxlab.util;

import java.util.Random;

import java.util.List;

/**
 * Some extensions of the JDK 1.1 java.util.Random class. Most of them are
 * copied from the JDK 1.2 java.util.Random class.
 * 
 * @author H. Irtel
 * @version 0.2.1
 */
public class Randomizer extends Random {
	public Randomizer() {
		super();
	}

	public Randomizer(long seed) {
		super(seed);
	}

	/**
	 * Returns a pseudorandom, uniformly distributed <tt>int</tt> value between
	 * 0 (inclusive) and the specified value (exclusive), drawn from this random
	 * number generator's sequence. The general contract of <tt>nextInt</tt> is
	 * that one <tt>int</tt> value in the specified range is pseudorandomly
	 * generated and returned. All <tt>n</tt> possible <tt>int</tt> values are
	 * produced with (approximately) equal probability. The method
	 * <tt>nextInt(int n)</tt> is implemented by class <tt>Random</tt> as
	 * follows: <blockquote>
	 * 
	 * <pre>
	 * public int nextInt(int n) {
	 * 	if (n &lt;= 0)
	 * 		throw new IllegalArgumentException(&quot;n must be positive&quot;);
	 * 	if ((n &amp; -n) == n) // i.e., n is a power of 2
	 * 		return (int) ((n * (long) next(31)) &gt;&gt; 31);
	 * 	int bits, val;
	 * 	do {
	 * 		bits = next(31);
	 * 		val = bits % n;
	 * 	} while (bits - val + (n - 1) &lt; 0);
	 * 	return val;
	 * }
	 * </pre>
	 * 
	 * </blockquote>
	 * <p>
	 * The hedge "approximately" is used in the foregoing description only
	 * because the next method is only approximately an unbiased source of
	 * independently chosen bits. If it were a perfect source of randomly chosen
	 * bits, then the algorithm shown would choose <tt>int</tt> values from the
	 * stated range with perfect uniformity.
	 * <p>
	 * The algorithm is slightly tricky. It rejects values that would result in
	 * an uneven distribution (due to the fact that 2^31 is not divisible by n).
	 * The probability of a value being rejected depends on n. The worst case is
	 * n=2^30+1, for which the probability of a reject is 1/2, and the expected
	 * number of iterations before the loop terminates is 2.
	 * <p>
	 * The algorithm treats the case where n is a power of two specially: it
	 * returns the correct number of high-order bits from the underlying
	 * pseudo-random number generator. In the absence of special treatment, the
	 * correct number of <i>low-order</i> bits would be returned. Linear
	 * congruential pseudo-random number generators such as the one implemented
	 * by this class are known to have short periods in the sequence of values
	 * of their low-order bits. Thus, this special case greatly increases the
	 * length of the sequence of values returned by successive calls to this
	 * method if n is a small power of two.
	 * 
	 * @param n
	 *            the bound on the random number to be returned. Must be
	 *            positive.
	 * @return a pseudorandom, uniformly distributed <tt>int</tt> value between
	 *         0 (inclusive) and n (exclusive).
	 * @exception IllegalArgumentException
	 *                n is not positive.
	 */
	public int nextInt(int n) {
		if (n <= 0)
			throw new IllegalArgumentException("n must be positive");
		if ((n & -n) == n) // i.e., n is a power of 2
			return (int) ((n * (long) next(31)) >> 31);
		int bits, val;
		do {
			bits = next(31);
			val = bits % n;
		} while (bits - val + (n - 1) < 0);
		return val;
	}

	/**
	 * Returns the next pseudorandom, uniformly distributed <code>boolean</code>
	 * value from this random number generator's sequence. The general contract
	 * of <tt>nextBoolean</tt> is that one <tt>boolean</tt> value is
	 * pseudorandomly generated and returned. The values <code>true</code> and
	 * <code>false</code> are produced with (approximately) equal probability.
	 * The method <tt>nextBoolean</tt> is implemented by class <tt>Random</tt>
	 * as follows: <blockquote>
	 * 
	 * <pre>
	 * public boolean nextBoolean() {
	 * 	return next(1) != 0;
	 * }
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @return the next pseudorandom, uniformly distributed <code>boolean</code>
	 *         value from this random number generator's sequence.
	 */
	public boolean nextBoolean() {
		return next(1) != 0;
	}

	/**
	 * Randomize the entries of the integer array given as an argument.
	 */
	public void randomize(int[] m) {
		int a, k;
		int n = m.length;
		int n1 = n - 1;
		for (int i = 0; i < n1; i++) {
			k = nextInt(n);
			a = m[i];
			m[i] = m[i + k];
			m[i + k] = a;
			--n;
		}
	}

	/**
	 * Randomize the entries of the integer array given as an argument.
	 */
	public void randomize(List m) {
		Object a;
		int k;
		int n = m.size();
		int n1 = n - 1;
		for (int i = 0; i < n1; i++) {
			k = nextInt(n);
			a = m.get(i);
			m.set(i, m.get(i + k));
			m.set(i + k, a);
			--n;
		}
	}

	/**
	 * Get a random element from an integer array. This uses random sampling
	 * with replacement.
	 */
	public int randomElement(int[] a) {
		return (a[nextInt(a.length)]);
	}

	/**
	 * Get a random element from a List. This uses random sampling with
	 * replacement.
	 */
	public Object randomElement(List a) {
		return (a.get(nextInt(a.size())));
	}
	private int[] iList;
	private List aList;
	private int nextPosition = 0;

	/** Initialize sequential random selection without replacement. */
	public void initRandomSequence(int[] a) {
		int n = a.length;
		iList = new int[n];
		for (int i = 0; i < n; i++)
			iList[i] = a[i];
		randomize(iList);
		nextPosition = 0;
	}

	/**
	 * Initialize sequential random selection without replacement.
	 */
	public void initRandomSequence(List a) {
		aList = a;
		int n = a.size();
		iList = new int[n];
		for (int i = 0; i < n; i++)
			iList[i] = i;
		randomize(iList);
		nextPosition = 0;
	}

	/**
	 * Return the next random integer from the previously initialized random
	 * sequence. This uses random sampling without replacement.
	 */
	public int nextRandomInt() {
		if (iList == null)
			throw new RuntimeException(
					"Requesting random integer from non-initialized integer array");
		if (nextPosition >= iList.length)
			throw new RuntimeException(
					"Requesting too many random values from integer array");
		return (iList[nextPosition++]);
	}

	/**
	 * Return the next random object from the previously initialized random
	 * sequence. This uses random sampling without replacement.
	 */
	public Object nextRandomObject() {
		if (aList == null)
			throw new RuntimeException(
					"Requesting random object from non-initialized object list");
		if (nextPosition >= iList.length)
			throw new RuntimeException(
					"Requesting too many random objects from list");
		return (aList.get(iList[nextPosition++]));
	}
}
