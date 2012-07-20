package de.pxlab.util;

import java.util.Iterator;

/**
 * Create an iterator which returns all possible index arrays for a given set
 * array. A set array is something like [1, 3, 1, 4]. This set array tells us
 * that dimensions 0 and 2 are sets with 1 element only, dimension 1 is a set
 * with 3 elements, and dimension 4 is a set with 4 elements. The iterator will
 * return all possible index sets. It delivers this sequence:
 * 
 * <pre>
 *     0 0 0 0
 *     0 0 0 1
 *     0 0 0 2
 *     0 0 0 3
 *     0 1 0 0
 *     0 1 0 1
 *     0 1 0 2
 *     0 1 0 3
 *     0 2 0 0
 *     0 2 0 1
 *     0 2 0 2
 *     0 2 0 3
 * </pre>
 * 
 * @author Hans Irtel
 * @version 0.2.0
 */
/*
 * 
 * 08/17/01 made the highest index run fastest
 */
public class ExpansionIterator implements Iterator {
	// This is the set of maximal index values
	private int[] set;
	private boolean hasNxt;
	// This is the array we work with and it also contains the next
	// return value
	private int[] nxt;
	// This is used to store the return value for the current call to
	// next()
	private int[] nxtReturn;
	// Length of the arrays
	private int n;

	public ExpansionIterator(int[] s) {
		n = s.length;
		set = new int[n];
		nxtReturn = new int[n];
		nxt = new int[n];
		for (int i = 0; i < n; i++)
			set[i] = s[i] - 1;
		hasNxt = true;
	}

	public boolean hasNext() {
		return (hasNxt);
	}

	//
	public Object next() {
		// copy set[] to nxt[] for returning it later
		System.arraycopy(nxt, 0, nxtReturn, 0, n);
		// now find the next one
		int i = n - 1;
		while (i >= 0) {
			// If nxt[i] is equal to the index limit
			if (nxt[i] == set[i]) {
				// go to the next position
				i--;
			} else {
				// increment nxt[i] to the next index
				nxt[i]++;
				// and reset all previous positions back to 0
				for (int j = i + 1; j < n; j++) {
					nxt[j] = 0;
				}
				// nxt[] is ready for the next call
				hasNxt = true;
				return (nxtReturn);
			}
		}
		hasNxt = false;
		return (nxtReturn);
	}

	/** Not supportet by this Iterator. */
	public void remove() {
		throw new RuntimeException("ExpansionIterator: remove() not supported.");
	}
}
