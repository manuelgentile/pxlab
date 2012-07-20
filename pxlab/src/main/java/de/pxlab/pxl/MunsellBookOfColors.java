package de.pxlab.pxl;

import java.util.StringTokenizer;
import java.util.Iterator;

/**
 * This class describes colors according to the Munsell color system.
 */
public class MunsellBookOfColors {
	/**
	 * The first entry of each MaxChroma[] array is the minimum Chroma for that
	 * Hue. The remaining 8 entries give the maximum Chroma for the respective
	 * Value. Value levels are: 2.5, 3, 4, 5, 6, 7, 8, 8.5, 9
	 */
	private static final int maxChroma[/* 40 */][/* 10 */] = {
	/* 0 2.5R */{ 2, 2, 6, 12, 12, 12, 10, 4, 0, 2 },
	/* 1 5R */{ 1, 2, 6, 14, 14, 14, 10, 4, 0, 2 },
	/* 2 7.5R */{ 2, 2, 6, 12, 12, 12, 10, 4, 0, 2 },
	/* 3 10R */{ 1, 2, 4, 8, 12, 12, 12, 8, 0, 2 },
	/* 4 2.5YR */{ 2, 2, 2, 8, 10, 14, 12, 8, 0, 2 },
	/* 5 5YR */{ 1, 1, 2, 6, 8, 12, 12, 8, 0, 4 },
	/* 6 7.5YR */{ 2, 0, 2, 6, 8, 10, 12, 10, 0, 4 },
	/* 7 10YR */{ 1, 1, 2, 4, 8, 10, 12, 10, 0, 4 },
	/* 8 2.5Y */{ 2, 0, 2, 4, 6, 8, 12, 12, 10, 6 },
	/* 9 5Y */{ 1, 1, 2, 4, 6, 8, 12, 12, 12, 8 },
	/* 10 7.5Y */{ 2, 0, 2, 4, 6, 8, 10, 12, 12, 10 },
	/* 11 10Y */{ 1, 1, 2, 4, 6, 8, 10, 12, 10, 10 },
	/* 12 2.5GY */{ 2, 0, 2, 4, 6, 8, 10, 10, 10, 8 },
	/* 13 5GY */{ 1, 1, 2, 4, 8, 8, 8, 10, 10, 6 },
	/* 14 7.5GY */{ 2, 0, 2, 4, 8, 10, 10, 8, 6, 4 },
	/* 15 10GY */{ 1, 2, 2, 4, 8, 10, 10, 6, 6, 4 },
	/* 16 2.5G */{ 2, 2, 4, 6, 10, 12, 10, 6, 0, 2 },
	/* 17 5G */{ 1, 2, 4, 8, 8, 10, 10, 6, 0, 2 },
	/* 18 7.5G */{ 2, 2, 4, 8, 8, 10, 10, 6, 0, 2 },
	/* 19 10G */{ 1, 2, 4, 8, 8, 10, 8, 4, 0, 2 },
	/* 20 2.5BG */{ 2, 2, 6, 8, 8, 10, 8, 4, 0, 2 },
	/* 21 5BG */{ 1, 2, 4, 8, 8, 8, 8, 4, 0, 2 },
	/* 22 7.5BG */{ 2, 2, 4, 8, 8, 8, 8, 4, 0, 2 },
	/* 23 10BG */{ 1, 2, 4, 8, 8, 8, 8, 4, 0, 2 },
	/* 24 2.5B */{ 2, 2, 4, 8, 8, 8, 8, 4, 0, 2 },
	/* 25 5B */{ 1, 2, 6, 8, 8, 8, 8, 4, 0, 2 },
	/* 26 7.5B */{ 2, 2, 6, 8, 10, 10, 8, 4, 0, 2 },
	/* 27 10B */{ 1, 4, 6, 8, 10, 10, 8, 4, 0, 2 },
	/* 28 2.5PB */{ 2, 4, 6, 10, 10, 10, 8, 6, 0, 2 },
	/* 29 5PB */{ 1, 4, 8, 12, 12, 10, 8, 6, 0, 2 },
	/* 30 7.5PB */{ 2, 6, 10, 12, 12, 10, 8, 6, 0, 2 },
	/* 31 10PB */{ 1, 6, 8, 10, 10, 10, 8, 4, 0, 2 },
	/* 32 2.5P */{ 2, 4, 8, 10, 10, 10, 8, 4, 0, 2 },
	/* 33 5P */{ 1, 6, 8, 10, 10, 8, 8, 4, 0, 2 },
	/* 34 7.5P */{ 2, 4, 8, 10, 10, 10, 8, 6, 0, 2 },
	/* 35 10P */{ 1, 4, 8, 10, 12, 10, 8, 6, 0, 2 },
	/* 36 2.5RP */{ 2, 4, 6, 10, 12, 12, 10, 6, 0, 2 },
	/* 37 5RP */{ 1, 4, 6, 12, 12, 10, 8, 6, 0, 2 },
	/* 38 7.5RP */{ 2, 4, 6, 12, 12, 10, 8, 6, 0, 2 },
	/* 39 10RP */{ 1, 4, 6, 12, 12, 10, 8, 6, 0, 2 } };
	private static final String hueName[/* 40 */] = {
	/* 0 */"2.5R",
	/* 1 */"5R",
	/* 2 */"7.5R",
	/* 3 */"10R",
	/* 4 */"2.5YR",
	/* 5 */"5YR",
	/* 6 */"7.5YR",
	/* 7 */"10YR",
	/* 8 */"2.5Y",
	/* 9 */"5Y",
	/* 10 */"7.5Y",
	/* 11 */"10Y",
	/* 12 */"2.5GY",
	/* 13 */"5GY",
	/* 14 */"7.5GY",
	/* 15 */"10GY",
	/* 16 */"2.5G",
	/* 17 */"5G",
	/* 18 */"7.5G",
	/* 19 */"10G",
	/* 20 */"2.5BG",
	/* 21 */"5BG",
	/* 22 */"7.5BG",
	/* 23 */"10BG",
	/* 24 */"2.5B",
	/* 25 */"5B",
	/* 26 */"7.5B",
	/* 27 */"10B",
	/* 28 */"2.5PB",
	/* 29 */"5PB",
	/* 30 */"7.5PB",
	/* 31 */"10PB",
	/* 32 */"2.5P",
	/* 33 */"5P",
	/* 34 */"7.5P",
	/* 35 */"10P",
	/* 36 */"2.5RP",
	/* 37 */"5RP",
	/* 38 */"7.5RP",
	/* 39 */"10RP" };
	private static final String basicHueName[/* 40 */] = {
	/* 0 */"R",
	/* 1 */"YR",
	/* 2 */"Y",
	/* 3 */"GY",
	/* 4 */"G",
	/* 5 */"BG",
	/* 6 */"B",
	/* 7 */"PB",
	/* 8 */"P",
	/* 9 */"RP" };
	private static final String[] valueName = { "", "2.5", "3", "4", "5", "6",
			"7", "8", "8.5", "9" };
	private static final double[] valueLevel = { 0.0, 2.5, 3.0, 4.0, 5.0, 6.0,
			7.0, 8.0, 8.5, 9.0 };

	/**
	 * Find the Hue index in the Munsell Book of Colors for the given Hue
	 * notation.
	 * 
	 * @return a Hue index ranging from 0 to 39 or -1 if the argument string is
	 *         not a valid Hue notation within the 40-Hue Munsell Book of
	 *         Colors.
	 */
	public static int findHueIndex(String h) {
		int hi = -1;
		for (int i = 0; i < 40; i++) {
			if (h.equals(hueName[i])) {
				hi = i;
				break;
			}
		}
		return (hi);
	}

	/**
	 * Find the basic Hue index in the Munsell Book of Colors for the given
	 * basic Hue notation.
	 * 
	 * @return a basic Hue index ranging from 0 to 9 or -1 if the argument
	 *         string is not a valid Hue notation within the 40-Hue Munsell Book
	 *         of Colors.
	 */
	public static int findBasicHueIndex(String h) {
		int hi = -1;
		for (int i = 0; i < 10; i++) {
			if (h.equals(basicHueName[i])) {
				hi = i;
				break;
			}
		}
		return (hi);
	}

	/**
	 * Find the Value index in the Munsell Book of Colors for the given Value.
	 * Value indices are available in the Munsell Book of Colors only for these
	 * Values: 2.5, 3, 4, 5, 6, 7, 8, 8.5, 9.
	 * 
	 * @return a Value index ranging from 1 to 9 or -1 if the given value is not
	 *         contained in the Munsell Book of Colors.
	 */
	public static int findValueIndex(double v) {
		int iv = 5 * (int) Math.round(2.0 * v);
		int i = 0;
		// Available 'iv' values are: 25, 30, 40, 50, 60, 70, 80, 85, 90
		if (iv <= 25) {
			i = 1;
		} else if (iv >= 90) {
			i = 9;
		} else if (iv >= 85) {
			i = 8;
		} else {
			i = iv / 10 - 1;
		}
		return (i);
	}

	/** Return the Munsell Value level at the given index. */
	public static double getValueLevel(int v) {
		return (valueLevel[v]);
	}

	/** Return the Munsell Hue name at the given index. */
	public static String getHueName(int i) {
		return (hueName[i]);
	}

	/** Return the Munsell basic Hue name at the given index. */
	public static String getBasicHueName(int i) {
		return (basicHueName[i]);
	}

	/** Return the Munsell Value name at the given index. */
	public static String getValueName(int i) {
		return (valueName[i]);
	}

	/**
	 * Find the Chroma index for the given Chroma level c at the given Hue and
	 * Value index.
	 * 
	 * @return a valid Chroma index or -1 if the given Chroma level is not
	 *         contained in the Munsell Book of Colors.
	 */
	public static int findChromaIndex(int hi, int vi, double c) {
		int ci = -1;
		int cc = (int) Math.round(c);
		// get minimum Chroma of this Hue
		int minChroma = maxChroma[hi][0];
		if ((minChroma == 1) && (cc == 1)) {
			ci = 1;
		} else if ((minChroma <= cc) && (cc <= maxChroma[hi][vi])
				&& ((cc % 2) == 0)) {
			ci = cc;
		}
		return (ci);
	}

	public static int getChromaLevel(int c) {
		return ((c == 0) ? 1 : (c + c));
	}

	/** Create an iterator for the whole Munsell Book of Colors. */
	public static Iterator iterator() {
		return (new Itr(-1, -1.0));
	}

	/**
	 * Create an iterator for a constant Value page of the Munsell Book of
	 * Colors.
	 * 
	 * @param v
	 *            the Value level. Valid levels range from 2.5 to 9.0.
	 */
	public static Iterator constantValueIterator(double v) {
		return (new Itr(-1, v));
	}

	/**
	 * Create an iterator for a constant Hue page of the Munsell Book of Colors.
	 * 
	 * @param hi
	 *            the Hue number to be used. The range is 1, ..., 40.
	 */
	public static Iterator constantHueIterator(int hi) {
		return (new Itr(hi - 1, -1.0));
	}

	/**
	 * Create an iterator for a cosntant Hue page of the Munsell Book of Colors.
	 * 
	 * @param h
	 *            is the Munsell notation name of the Hue to be shown.
	 */
	public static Iterator constantHueIterator(String h) {
		return (new Itr(findHueIndex(h), -1.0));
	}

	/**
	 * Create an iterator for a constant Chroma page of the Munsell Book of
	 * Colors.
	 * 
	 * @param c
	 *            the Chroma value of the page to be shown.
	 */
	public static Iterator constantChromaIterator(double c) {
		return (new Itr(c));
	}

	/**
	 * Create an iterator for the Munsell Book of Colors with only showing the
	 * maximum Chroma colors for each Hue and Value.
	 */
	public static Iterator maximumChromaIterator() {
		return (new Itr());
	}
	private static class Itr implements Iterator {
		// Most recently used Hue index
		private int hueIdx;
		// Most recently used Value index
		private int valueIdx;
		// Most recently used Chroma index
		private int chromaIdx;
		// Next Hue index
		private int nextHueIdx;
		// Next Value index
		private int nextValueIdx;
		// Next Chroma index, -1 if none is available
		private int nextChromaIdx;
		/**
		 * If this flag is true then the iterator tries to keep Hue constant and
		 * first walks along the Value levels. If it is false then the Value
		 * level is kept constant and Hue is changed first.
		 */
		private boolean valueFirst = true;
		/** True if this iterator is allowed to change Hue. */
		private boolean changeHue = true;
		/** True if this iterator is allowed to change Value. */
		private boolean changeValue = true;
		/** True for a constant or maximum Chroma iterator. */
		private boolean constantChroma = false;
		/** True for a maximum Chroma iterator. */
		private boolean maximumChroma = false;

		/**
		 * Create a new iterator over the Munsell Book of Colors.
		 * 
		 * @param hi
		 *            For a constant Hue iterator this is the Munsell Hue index
		 *            (ranging from 0 to 39). If the iterator should iterate
		 *            over the whole Book of Colors then this parameter should
		 *            be set to (-1).
		 * @param v
		 *            For a constant Value iterator this is the Munsell Value
		 *            level (ranging from 2.5 to 9.0). If the iterator should
		 *            iterate over the whole Book of Colors then this parameter
		 *            should be set to (-1.0).
		 */
		public Itr(int hi, double v) {
			if (hi > 39)
				hi = 39;
			findFirstChroma1(hi, (v >= 0.0) ? findValueIndex(v) : -1);
			nextHueIdx = hueIdx;
			nextValueIdx = valueIdx;
			nextChromaIdx = maxChroma[hueIdx][0];
		}

		/**
		 * Create a new iterator over the Munsell Book of Colors which keeps the
		 * Chroma level constant.
		 * 
		 * @param c
		 *            Chroma level for this iterator.
		 */
		public Itr(double c) {
			hueIdx = 0;
			valueIdx = 0;
			chromaIdx = (int) Math.round(c);
			constantChroma = true;
			findNextChroma2();
		}

		/**
		 * Create a new iterator over the Munsell Book of Colors which reports
		 * only the maximum Chroma levels for each Hue and Value.
		 */
		public Itr() {
			hueIdx = 0;
			valueIdx = 1;
			chromaIdx = maxChroma[hueIdx][valueIdx];
			while (chromaIdx == 0) {
				chromaIdx = maxChroma[hueIdx][++valueIdx];
			}
			constantChroma = true;
			maximumChroma = true;
			nextHueIdx = hueIdx;
			nextValueIdx = valueIdx;
			nextChromaIdx = chromaIdx;
		}

		/** Returns true if this iterator has a next element. */
		public boolean hasNext() {
			return (nextChromaIdx > 0);
		}

		/** Returns the next object of the iterator. */
		public Object next() {
			hueIdx = nextHueIdx;
			valueIdx = nextValueIdx;
			chromaIdx = nextChromaIdx;
			if (constantChroma) {
				if (maximumChroma) {
					findNextChroma3();
				} else {
					findNextChroma2();
				}
			} else {
				findNextChroma();
			}
			// System.out.println("MunsellBookOfColors.next(): " + hueIdx + ":"
			// + valueIdx + ":" + chromaIdx);
			return (new MunsellColor(hueIdx, valueIdx, chromaIdx));
		}

		/**
		 * Figure out what the first Chroma level is for this iterator.
		 */
		private void findFirstChroma1(int hi, int v) {
			if (hi >= 0) {
				// constant Hue wanted
				hueIdx = hi;
				changeHue = false;
			} else {
				hueIdx = 0;
			}
			if (v >= 0) {
				// constant Value wanted
				valueIdx = v;
				valueFirst = false;
				changeValue = false;
			} else {
				valueIdx = 1;
			}
			while (maxChroma[hueIdx][valueIdx] == 0) {
				if (valueFirst) {
					valueIdx++;
				} else {
					hueIdx++;
				}
			}
		}

		/** Figure out the next color for this iterator. */
		private void findNextChroma() {
			nextHueIdx = hueIdx;
			nextValueIdx = valueIdx;
			nextChromaIdx = chromaIdx;
			// System.out.print("Next to " + hueIdx + ":" + valueIdx + ":" +
			// chromaIdx + " is ");
			if (nextChromaIdx == maxChroma[nextHueIdx][nextValueIdx]) {
				if (valueFirst) {
					// changing value is always allowed here
					// change Value first
					if (nextValueIdx == 9) {
						nextValueIdx = 1;
						// move to the next Hue
						if (nextHueIdx == 39) {
							nextChromaIdx = -1;
						} else {
							if (changeHue) {
								// Move to the next Hue and return the minimum
								// Chroma level
								nextHueIdx++;
								while (maxChroma[nextHueIdx][nextValueIdx] < maxChroma[nextHueIdx][0]) {
									nextValueIdx++;
								}
								nextChromaIdx = maxChroma[nextHueIdx][0];
							} else {
								nextChromaIdx = -1;
							}
						}
					} else {
						int i;
						do {
							nextValueIdx++;
							i = maxChroma[nextHueIdx][nextValueIdx];
						} while (i == 0);
						nextChromaIdx = maxChroma[nextHueIdx][0];
					}
				} else {
					// changing Hue is always allowed here
					// change Hue first
					if (nextHueIdx == 39) {
						// Move to the first Hue
						nextHueIdx = 0;
						if (nextValueIdx == 9) {
							nextChromaIdx = -1;
						} else {
							if (changeValue) {
								// And move to the next Value level which
								// contains Chroma steps
								nextValueIdx++;
								while (maxChroma[nextHueIdx][nextValueIdx] < maxChroma[nextHueIdx][0]) {
									nextHueIdx++;
								}
								nextChromaIdx = maxChroma[nextHueIdx][0];
							} else {
								nextChromaIdx = -1;
							}
						}
					} else {
						// Move to the next Hue and return the minimum
						// Chroma level
						nextHueIdx++;
						while (maxChroma[nextHueIdx][nextValueIdx] < maxChroma[nextHueIdx][0]) {
							nextHueIdx++;
							if (nextHueIdx == 40) {
								// Move to the next Value level
								nextHueIdx = 0;
								nextValueIdx++;
							}
						}
						if (changeValue || (nextValueIdx == valueIdx)) {
							nextChromaIdx = maxChroma[nextHueIdx][0];
						} else {
							nextChromaIdx = -1;
						}
					}
				}
			} else {
				if (nextChromaIdx == 1) {
					nextChromaIdx = 2;
				} else {
					nextChromaIdx += 2;
				}
			}
			// System.out.println(nextHueIdx + ":" + nextValueIdx + ":" +
			// nextChromaIdx);
		}

		/**
		 * Figure out the next color for this iterator which stays on a single
		 * Chroma level.
		 */
		private void findNextChroma2() {
			nextHueIdx = hueIdx;
			nextValueIdx = valueIdx;
			nextChromaIdx = chromaIdx;
			// System.out.print("Next to " + hueIdx + ":" + valueIdx + ":" +
			// chromaIdx + " is ");
			do {
				nextValueIdx++;
				if (nextValueIdx == 10) {
					nextValueIdx = 1;
					nextHueIdx++;
					if (nextHueIdx == 40) {
						nextChromaIdx = -1;
						break;
					}
				}
			} while (!valid());
			// System.out.println(nextHueIdx + ":" + nextValueIdx + ":" +
			// nextChromaIdx);
		}

		private boolean valid() {
			int minChroma = maxChroma[nextHueIdx][0];
			return ((((minChroma == 1) && (nextChromaIdx == 1)) || ((minChroma <= nextChromaIdx) && ((nextChromaIdx % 2) == 0))) && (nextChromaIdx <= maxChroma[nextHueIdx][nextValueIdx]));
		}

		/**
		 * Figure out the next color for this iterator which stays on a single
		 * Chroma level.
		 */
		private void findNextChroma3() {
			nextHueIdx = hueIdx;
			nextValueIdx = valueIdx;
			// System.out.print("MunsellBookOfColors.findNextChroma3(): Next to "
			// + hueIdx + ":" + valueIdx + " is ");
			boolean done = false;
			do {
				nextValueIdx++;
				if (nextValueIdx == 10) {
					nextValueIdx = 1;
					nextHueIdx++;
					if (nextHueIdx == 40) {
						nextHueIdx = 0;
					}
				}
				done = ((nextHueIdx == 0) && (nextValueIdx == 1));
			} while (maxChroma[nextHueIdx][nextValueIdx] == 0);
			nextChromaIdx = done ? (-1) : maxChroma[nextHueIdx][nextValueIdx];
			// System.out.println(nextHueIdx + ":" + nextValueIdx + ":" +
			// nextChromaIdx);
		}

		/** Not supportet by this Iterator. */
		public void remove() {
			throw new RuntimeException(
					"MunsellBookOfColors.Itr: remove() not supported.");
		}
	}
}
