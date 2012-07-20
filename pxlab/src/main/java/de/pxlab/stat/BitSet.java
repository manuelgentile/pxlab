package de.pxlab.stat;

import java.util.Iterator;

/**
 * Provides small sets and set operations. Set elements essentially are the
 * integer numbers from 1 to SETSIZELIMIT which currently is 31.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class BitSet {
	public static final int SETSIZELIMIT = 31;
	private int pattern;

	/** Create an empty set. */
	public BitSet() {
		this(0);
	}

	/**
	 * Create a new set from the given integer mask.
	 * 
	 * @param p
	 *            an integer whose bit pattern defines the elements contained in
	 *            the set.
	 */
	public BitSet(int p) {
		pattern = p;
		// System.out.println("BitSet(" + p + "): = " + toString());
	}

	protected int getPattern() {
		return pattern;
	}

	/**
	 * Create a singleton set which contains the given element.
	 * 
	 * @param element
	 *            an integer number which denotes the element of the singleton
	 *            set.
	 */
	public static BitSet singleton(int element) {
		if (element > SETSIZELIMIT) {
			throw new IllegalArgumentException(
					"Argument too large. Set size limited to " + SETSIZELIMIT
							+ " elements.");
		}
		return new BitSet(1 << element);
	}

	/**
	 * Create the union set.
	 * 
	 * @param m
	 *            a set which will be joined to this set.
	 */
	public BitSet union(BitSet m) {
		int p = (this.pattern) | (m.pattern);
		// System.out.println("BitSet.union(" + m.pattern + "): = " + p);
		return new BitSet(p);
	}

	/**
	 * Create the intersection set.
	 * 
	 * @param m
	 *            a set which will be intersected with this set.
	 */
	public BitSet intersection(BitSet m) {
		return new BitSet(pattern & m.pattern);
	}

	/**
	 * Return true if the given pattern is a subset of this pattern.
	 */
	public boolean subsetOf(BitSet m) {
		return ((this.pattern & m.pattern) == this.pattern);
	}

	/**
	 * Enter the given element to this set.
	 * 
	 * @param element
	 *            an integer number identifying the element to be added.
	 */
	public void enter(int element) {
		if (element > SETSIZELIMIT) {
			throw new IllegalArgumentException(
					"Argument too large. Set size limited to " + SETSIZELIMIT
							+ " elements.");
		}
		pattern = pattern | (1 << element);
		// System.out.println("BitSet.enter(" + element + "): = " + p);
	}

	/**
	 * Return true if the given factor is a member of this factor pattern.
	 */
	public boolean containsElement(int element) {
		if (element > SETSIZELIMIT) {
			throw new IllegalArgumentException(
					"Argument too large. Set size limited to " + SETSIZELIMIT
							+ " elements.");
		}
		return (this.pattern & (1 << element)) != 0;
	}

	/** Return the number of elements in this factor pattern. */
	public int size() {
		int s = 0;
		int m = 1;
		for (int i = 0; i < 31; i++) {
			if ((pattern & m) != 0)
				s++;
			m = (m << 1);
		}
		return s;
	}

	public Iterator iterator() {
		return new BitSetIterator(this);
	}

	public String toString() {
		return toString(1);
	}

	public boolean isEmpty() {
		return pattern == 0;
	}

	public String toString(int n) {
		char[] c = new char[SETSIZELIMIT];
		for (int i = 0; i < SETSIZELIMIT; i++)
			c[i] = '0';
		StringBuffer b = new StringBuffer(new String(c));
		int fp = SETSIZELIMIT - 1;
		boolean found = false;
		int k = (1 << (SETSIZELIMIT - 1));
		for (int i = 0; i < SETSIZELIMIT; i++) {
			if ((pattern & k) != 0) {
				b.setCharAt(i, '1');
				if (!found) {
					fp = i;
					found = true;
				}
			}
			k = (k >> 1);
		}
		if (fp > (SETSIZELIMIT - n))
			fp = SETSIZELIMIT - n;
		return b.toString().substring(fp);
	}
}
