package de.pxlab.util;

import java.util.Iterator;

/**
 * An iterator which returns all possible subsets of a set possibly restricted
 * to sets of a given size.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class SubsetIterator implements Iterator {
	/** The maximum number of elements allowed in the set. */
	public static final int SETSIZE_MAX = 31;
	private int setSize;
	private int pattern;
	private int patternLimit;
	private int subsetSize;
	private boolean hasNxt;
	private boolean allSubsets = true;

	/**
	 * Create an iterator for a set of the given size. The size must not exceed
	 * the SETSIZE_MAX value.
	 */
	public SubsetIterator(int n) {
		this(n, -1);
	}

	/**
	 * Create an iterator over all subsets of size m for a set of size n
	 * elements. The set size must not exceed the SETSIZE_MAX value and the
	 * subset size must not exceed the set size.
	 */
	public SubsetIterator(int n, int m) {
		setSize = n;
		if (n > SETSIZE_MAX) {
			throw new RuntimeException(
					"SubsetIterator(): Set size exceeds allowed maximum of "
							+ String.valueOf(SETSIZE_MAX));
		}
		allSubsets = (m < 0);
		subsetSize = (allSubsets) ? 0 : ((m > setSize) ? setSize : m);
		patternLimit = (1 << setSize);
		pattern = (allSubsets || (subsetSize == 0)) ? 0
				: ((1 << subsetSize) - 1);
		hasNxt = true;
	}

	/** Return true if another element is available. */
	public boolean hasNext() {
		return (hasNxt);
	}

	/** Get the next element of the iterator. */
	public Object next() {
		boolean[] nxt = new boolean[setSize];
		int m = 1;
		for (int i = 0; i < setSize; i++) {
			nxt[i] = (pattern & m) != 0;
			m = m << 1;
		}
		/*
		 * int m = patternLimit; for (int i = 0; i < setSize; i++) { m = m >> 1;
		 * nxt[i] = (pattern & m) != 0; }
		 */
		if (allSubsets) {
			pattern++;
			hasNxt = (pattern < patternLimit);
		} else {
			do {
				pattern++;
				if (pattern >= patternLimit) {
					hasNxt = false;
					return nxt;
				}
				if (qSum() == subsetSize) {
					hasNxt = true;
					return nxt;
				}
			} while (true);
		}
		return nxt;
	}

	private int qSum() {
		long m = 1L;
		int s = 0;
		for (int i = 0; i < setSize; i++) {
			if ((pattern & m) != 0) {
				s++;
			}
			m = m << 1;
		}
		return s;
	}

	/** Not supportet by this Iterator. */
	public void remove() {
		throw new RuntimeException("SubsetIterator: remove() not supported.");
	}
}
