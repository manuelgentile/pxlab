package de.pxlab.pxl;

/**
 * Codes for statistical data analysis objects.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see DataProcessor
 * @see DataDisplay
 */
/*
 * 
 * 2005/12/08
 */
public interface StatCodes {
	/** Do not compute any statistics. */
	public static final int NO_STATS = 0;
	/** Total number of elements in input data set. */
	public static final int TOTAL_N = 1;
	/** Number entries included by the Include condition. */
	public static final int INCLUDED_N = 1 << 1;
	/**
	 * Number of entries excluded from INCLUDE_N by the Exclude condition.
	 */
	public static final int EXCLUDED_N = 1 << 2;
	/**
	 * Number of values which had to be excluded for syntactical reasons or
	 * which are missing for some other reasons.
	 */
	public static final int MISSING = 1 << 3;
	/** Number of entries used for computations. */
	public static final int N = 1 << 4;
	/** Minimum data value. */
	public static final int MINIMUM = 1 << 5;
	/** Maximum data value. */
	public static final int MAXIMUM = 1 << 6;
	/** Sum of all data values. */
	public static final int SUM = 1 << 7;
	/** Sum of squares of all data values. */
	public static final int SUM_OF_SQUARES = 1 << 8;
	/** Compute the arithmetic mean. */
	public static final int MEAN = 1 << 9;
	/** Compute the geometric mean. */
	public static final int GEOMETRIC_MEAN = 1 << 10;
	/** Compute the harmonic mean. */
	public static final int HARMONIC_MEAN = 1 << 11;
	/** Compute the mode. */
	public static final int MODE = 1 << 12;
	/** Compute the 25 % quantile. */
	public static final int QUANTILE_25 = 1 << 13;
	/** Compute the median. */
	public static final int MEDIAN = 1 << 14;
	/** Compute the 50 % quantile. */
	public static final int QUANTILE_50 = MEDIAN;
	/** Compute the 75 % quantile. */
	public static final int QUANTILE_75 = 1 << 15;
	/** Compute the range of data values. */
	public static final int RANGE = 1 << 16;
	/** Compute the average deviation of the arithmetic mean. */
	public static final int AV_DEV = 1 << 17;
	/** Compute the coefficient of variation. */
	public static final int VAR_COEFF = 1 << 18;
	/** Compute the variance. */
	public static final int VARIANCE = 1 << 19;
	/** Compute the standard deviation. */
	public static final int STDDEV = 1 << 20;
	/** Compute the standard error of the mean. */
	public static final int STDERR = 1 << 21;
	/** Compute the skew. */
	public static final int SKEW = 1 << 22;
	/** Compute the kurtosis. */
	public static final int KURTOSIS = 1 << 23;
	/** Compute the cumulative distribution. */
	public static final int CUMULATIVE = 1 << 24;
	/** Compute all available descriptive statistics. */
	public static final int ALL = (1 << 31) - 1;
	/** Compute some default statistics. */
	public static final int DEFAULT_STATS = N | MINIMUM | MAXIMUM | MEAN
			| MEDIAN | VARIANCE | STDDEV | STDERR;
	/** Compute factor level statistics N, MEAN, SD, and SE. */
	// public static final int FACTOR_LEVEL_STATS = 1 << 24;
	/** Compute an ANOVA. */
	// public static final int ANOVA = 1 << 25;
	/** Compute a linear regression. */
	// public static final int LINREG = 1 << 26;
}
