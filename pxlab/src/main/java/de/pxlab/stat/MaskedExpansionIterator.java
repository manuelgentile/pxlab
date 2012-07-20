package de.pxlab.stat;

import java.util.Iterator;

import de.pxlab.util.ExpansionIterator;

/**
 * Create an iterator which returns all possible index arrays for a given set
 * array where those index values given in a mask are fixed. A set array is
 * something like [1, 3, 1, 4]. This set array tells us that dimensions 0 and 2
 * are sets with 1 element only, dimension 1 is a set with 3 elements, and
 * dimension 4 is a set with 4 elements. The mask array has the same number of
 * elements as the set array. Iteration of the output is restricted to those
 * elements which are non-negative in the mask array. The iterator will return
 * all possible index sets which have those indices fixed which are non-negative
 * in the mask array . The example with mask array m = [0, 1, -1, -1] delivers
 * this sequence:
 * 
 * <pre>
 *     0 1 0 0 
 *     0 1 0 1 
 *     0 1 0 2 
 *     0 1 0 3
 * </pre>
 * 
 * @author Hans Irtel
 * @version 0.1.0
 */
/*
 * 
 * 09/10/03
 */
public class MaskedExpansionIterator implements Iterator {
	// This is used to store the return value for the current call to
	// next()
	private int[] mask;
	// Length of the arrays
	private int iLength;
	private int sLength;
	private ExpansionIterator iterator;

	public MaskedExpansionIterator(int[] s, int[] m) {
		iLength = 0;
		sLength = s.length;
		for (int i = 0; i < sLength; i++) {
			if (m[i] < 0)
				iLength++;
		}
		int[] ss = new int[iLength];
		int j = 0;
		for (int i = 0; i < sLength; i++) {
			if (m[i] < 0)
				ss[j++] = s[i];
		}
		iterator = new ExpansionIterator(ss);
		mask = m;
	}

	public boolean hasNext() {
		return (iterator.hasNext());
	}

	//
	public Object next() {
		int[] r = (int[]) (iterator.next());
		int[] rr = new int[sLength];
		int j = 0;
		for (int i = 0; i < sLength; i++) {
			if (mask[i] < 0) {
				rr[i] = r[j++];
			} else {
				rr[i] = mask[i];
			}
		}
		return rr;
	}

	/** Not supportet by this Iterator. */
	public void remove() {
		throw new RuntimeException(
				"MaskedExpansionIterator.remove() not supported.");
	}
}
