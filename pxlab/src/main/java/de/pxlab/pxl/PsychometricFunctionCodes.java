package de.pxlab.pxl;

/**
 * Type codes for psychometric functions.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 
 * 2006/01/24
 * 
 * 2008/01/30
 */
public interface PsychometricFunctionCodes {
	/**
	 * Assume a logistic psychometric function and estimate the pse and the jnd
	 * using all available data points.
	 */
	public static final int LOGISTIC = 10;
	/**
	 * Assume a Weibull psychometric function and estimate the pse and the jnd
	 * using all available data points.
	 */
	public static final int WEIBULL = 11;
	/**
	 * Assume a Gumbell psychometric function and estimate the pse and the jnd
	 * using all available data points.
	 */
	public static final int GUMBEL = 12;
	/**
	 * Assume an isotonic psychometric function and estimate the function values
	 * for all available data points.
	 */
	public static final int ISOTONIC = 19;
}
