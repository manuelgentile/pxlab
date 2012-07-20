package de.pxlab.stat;

import java.util.*;
import de.pxlab.util.IntegerPair;

/**
 * Some simple ans static descriptive statistical methods.
 * 
 * @version 0.1.1
 */
/*
 * 
 * 09/12/03 moved to newly created package de.pxlab.stat
 */
public class Stats {
	private static Random rnd = new Random();

	/**
	 * Compute the mean of the given data array.
	 * 
	 * @param a
	 *            an array of data values whose mean should be computed.
	 * @return the arithmetic mean of the data array.
	 */
	public static double mean(float[] a) {
		double sa = 0.0;
		int n = a.length;
		for (int i = 0; i < n; i++) {
			sa += (double) a[i];
		}
		return sa / n;
	}

	/**
	 * Compute the mean of the given data array.
	 * 
	 * @param a
	 *            an array of data values whose mean should be computed.
	 * @return the arithmetic mean of the data array.
	 */
	public static double mean(double[] a) {
		double sa = 0.0;
		int n = a.length;
		for (int i = 0; i < n; i++) {
			sa += (double) a[i];
		}
		return sa / n;
	}

	/**
	 * Transform the given data array to its deviates from the mean.
	 * 
	 * @param a
	 *            an array of data values which is to be transformed to
	 *            deviates. Every data value a[i] is replaced by (a[i]-m) where
	 *            m is the arithmetic mean of the data array.
	 */
	public static void deviates(double[] a) {
		double m = mean(a);
		int n = a.length;
		for (int i = 0; i < n; i++) {
			a[i] -= m;
		}
	}

	/**
	 * Transform the given data array to its deviates from the mean.
	 * 
	 * @param a
	 *            an array of data values which is to be transformed to
	 *            deviates. Every data value a[i] is replaced by (a[i]-m) where
	 *            m is the arithmetic mean of the data array.
	 */
	public static void deviates(float[] a) {
		double m = mean(a);
		int n = a.length;
		for (int i = 0; i < n; i++) {
			a[i] = (float) (a[i] - m);
		}
	}

	/**
	 * Compute the variance of the given data array.
	 * 
	 * @param a
	 *            an array of data values whose variance should be computed.
	 * @return the variance of the data array.
	 */
	public static double variance(float[] a) {
		double aa, sa = 0.0, ssa = 0.0;
		int n = a.length;
		for (int i = 0; i < n; i++) {
			aa = a[i];
			sa += aa;
			ssa += aa * aa;
		}
		return (ssa - sa * sa / n) / (n - 1);
	}

	/**
	 * Compute the variance of the given data array.
	 * 
	 * @param a
	 *            an array of data values whose variance should be computed.
	 * @return the variance of the data array.
	 */
	public static double variance(double[] a) {
		double aa, sa = 0.0, ssa = 0.0;
		int n = a.length;
		for (int i = 0; i < n; i++) {
			aa = a[i];
			sa += aa;
			ssa += aa * aa;
		}
		return (ssa - sa * sa / n) / (n - 1);
	}

	/**
	 * Compute the standard deviation of the given data array.
	 * 
	 * @param a
	 *            an array of data values whose standard deviation should be
	 *            computed.
	 * @return the standard deviation of the data array.
	 */
	public static double standardDev(float[] a) {
		return Math.sqrt(variance(a));
	}

	/**
	 * Compute the standard deviation of the given data array.
	 * 
	 * @param a
	 *            an array of data values whose standard deviation should be
	 *            computed.
	 * @return the standard deviation of the data array.
	 */
	public static double standardDev(double[] a) {
		return Math.sqrt(variance(a));
	}

	/**
	 * Compute the standard deviation of the given data array.
	 * 
	 * @param a
	 *            an array of data values whose standard deviation should be
	 *            computed.
	 * @return the standard deviation of the data array.
	 */
	public static double standardErr(double[] a) {
		return Math.sqrt(variance(a) / a.length);
	}

	/**
	 * Standardize the given data array.
	 * 
	 * @param a
	 *            an array of data values which is to be standardized. Every
	 *            data value a[i] is replaced by (a[i]-m)/s where m is the
	 *            arithmetic mean and s is the standard deviation of the data
	 *            array.
	 */
	public static void standardize(double[] a) {
		double m = mean(a);
		double sd = standardDev(a);
		int n = a.length;
		for (int i = 0; i < n; i++) {
			a[i] = (a[i] - m) / sd;
		}
	}

	public static void standardize(float[] a) {
		float m = (float) mean(a);
		float sd = (float) standardDev(a);
		int n = a.length;
		for (int i = 0; i < n; i++) {
			a[i] = (a[i] - m) / sd;
		}
	}

	/**
	 * Compute the correlation between the given data arrays.
	 * 
	 * @param a
	 *            the first data array.
	 * @param b
	 *            the second data array.
	 * @return the Pearson product moment correlation of the two arrays.
	 */
	public static double correlation(float[] a, float[] b) {
		double aa, bb, sa = 0.0, sb = 0.0, ssa = 0.0, ssb = 0.0, sab = 0.0;
		int n = a.length;
		for (int i = 0; i < n; i++) {
			aa = a[i];
			sa += aa;
			ssa += aa * aa;
			bb = b[i];
			sb += bb;
			ssb += bb * bb;
			sab += aa * bb;
		}
		double va = (ssa - sa * sa / n) / (n - 1);
		double vb = (ssb - sb * sb / n) / (n - 1);
		double cab = (sab - sa * sb / n) / (n - 1);
		return cab / Math.sqrt(va * vb);
	}

	/**
	 * Compute the correlation between the given data arrays.
	 * 
	 * @param a
	 *            the first data array.
	 * @param b
	 *            the second data array.
	 * @return the Pearson product moment correlation of the two arrays.
	 */
	public static double correlation(double[] a, double[] b) {
		double aa, bb, sa = 0.0, sb = 0.0, ssa = 0.0, ssb = 0.0, sab = 0.0;
		int n = a.length;
		for (int i = 0; i < n; i++) {
			aa = a[i];
			sa += aa;
			ssa += aa * aa;
			bb = b[i];
			sb += bb;
			ssb += bb * bb;
			sab += aa * bb;
		}
		double va = (ssa - sa * sa / n) / (n - 1);
		double vb = (ssb - sb * sb / n) / (n - 1);
		double cab = (sab - sa * sb / n) / (n - 1);
		return cab / Math.sqrt(va * vb);
	}

	/**
	 * Count the frequencies if integers in the argument array.
	 * 
	 * @param a
	 *            an array of integer numbers whose frequencies have to be
	 *            counted.
	 * @return an array of IntegerPair objects where every entry contains the
	 *         integer number and its respective frequency in the data array.
	 */
	public static IntegerPair[] frequencyTable(int[] a) {
		HashMap m = new HashMap(30);
		for (int i = 0; i < a.length; i++) {
			Integer key = new Integer(a[i]);
			if (m.containsKey(key)) {
				int v = ((Integer) (m.get(key))).intValue();
				m.put(key, new Integer(v + 1));
			} else {
				m.put(key, new Integer(1));
			}
		}
		ArrayList keys = new ArrayList(m.keySet());
		Collections.sort(keys);
		IntegerPair[] ft = new IntegerPair[keys.size()];
		int k = 0;
		for (Iterator it = keys.iterator(); it.hasNext();) {
			Object key = it.next();
			ft[k++] = new IntegerPair((Integer) key, (Integer) (m.get(key)));
		}
		return ft;
	}

	/**
	 * Give me a sample from a normal distribution.
	 * 
	 * @param N
	 *            sample size
	 * @param mean
	 *            normal distribution mean
	 * @param stddev
	 *            standard deviation of the normal distribution
	 * @return an array containing the N sample points.
	 */
	public static float[] normalDistributionSample(int N, double mean,
			double stddev) {
		float[] a = new float[N];
		for (int i = 0; i < N; i++)
			a[i] = (float) (stddev * rnd.nextGaussian() + mean);
		return a;
	}
}
