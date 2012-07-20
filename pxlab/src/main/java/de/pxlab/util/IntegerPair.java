package de.pxlab.util;

import java.util.Comparator;

/**
 * Stores a pair of integer values.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class IntegerPair implements Comparator {
	public int a;
	public int b;

	public IntegerPair(int a, int b) {
		this.a = a;
		this.b = b;
	}

	public IntegerPair(Integer a, Integer b) {
		this.a = a.intValue();
		this.b = b.intValue();
	}

	public String toString() {
		return String.valueOf(a) + " " + String.valueOf(b);
	}

	/**
	 * Compares its two arguments for order.
	 * 
	 * @return a negative integer, zero, or a positive integer as the first
	 *         argument is less than, equal to, or greater than the second.
	 */
	public int compare(Object o1, Object o2) {
		int r = 0;
		if (((IntegerPair) o1).a < ((IntegerPair) o2).a) {
			r = -1;
		} else if (((IntegerPair) o1).a > ((IntegerPair) o2).a) {
			r = 1;
		} else if (((IntegerPair) o1).b < ((IntegerPair) o2).b) {
			r = -1;
		} else if (((IntegerPair) o1).b > ((IntegerPair) o2).b) {
			r = 1;
		}
		return r;
	}

	/** Indicates whether some other object is "equal to" this Comparator. */
	public boolean equals(Object obj) {
		return (a == ((IntegerPair) obj).a) && (b == ((IntegerPair) obj).b);
	}
}
