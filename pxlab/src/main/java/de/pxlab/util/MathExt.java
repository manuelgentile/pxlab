package de.pxlab.util;

/** Some useful math functions. */
public class MathExt {
	public static int ggT(int x, int y) {
		if (x > y) {
			return ggT(x - y, y);
		} else if (y > x) {
			return ggT(y - x, x);
		} else
			return x;
	}

	public static int kgV(int x, int y) {
		return x / ggT(x, y) * y;
	}

	/**
	 * Compute binomial coefficients. This algorithm is copied from Program
	 * 14.11 of "Data Structures and Algorithms with Object-Oriented Design
	 * Patterns in Java" by Bruno R. Preiss. Copyright (c) 1998 by Bruno R.
	 * Preiss, P.Eng. All rights reserved. <a href=
	 * "http://www.pads.uwaterloo.ca/Bruno.Preiss/books/opus5/programs/pgm14_11.txt"
	 * > http://www.pads.uwaterloo.ca/Bruno.Preiss/books/opus5/programs/pgm14_11
	 * .txt</a>.
	 */
	public static int binom(int n, int m) {
		int[] b = new int[n + 1];
		b[0] = 1;
		for (int i = 1; i <= n; i++) {
			b[i] = 1;
			for (int j = i - 1; j > 0; j--)
				b[j] += b[j - 1];
		}
		return b[m];
	}
}
