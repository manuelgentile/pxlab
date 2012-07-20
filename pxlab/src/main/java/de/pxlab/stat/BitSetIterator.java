package de.pxlab.stat;

import java.util.Iterator;

/**
 * An Iterator which generates all subsets of a BitSet object.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class BitSetIterator implements Iterator {
	/** Stores the target set. */
	private int set;
	/** The internal counter which creates the subsets. */
	private int count;
	/** last value for count. */
	private int limit;

	public BitSetIterator(BitSet s) {
		set = s.getPattern();
		int size = s.size();
		if (size > 0) {
			count = 1;
			limit = (1 << size) - 1;
		} else {
			count = 0;
			limit = -1;
		}
	}

	/**
	 * Walk through this iterator's set and for every element contained in the
	 * set take the next element of x and copy it to the respective element
	 * position in the output set.
	 */
	private BitSet distribute(int x) {
		int b = 0;
		int m = 1;
		int k = 1;
		for (int p = 0; p < BitSet.SETSIZELIMIT; p++) {
			if ((set & k) != 0) {
				if ((x & m) != 0) {
					b = (b | k);
				} else {
					// do nothing here
				}
				m = (m << 1);
			}
			k = (k << 1);
		}
		return new BitSet(b);
	}

	public boolean hasNext() {
		return ((count > 0) && (count <= limit));
	}

	public Object next() {
		return distribute(count++);
	}

	/** Not supportet by this Iterator. */
	public void remove() {
		throw new RuntimeException("BitSetIterator: remove() not supported.");
	}
}
