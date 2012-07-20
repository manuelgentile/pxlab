package de.pxlab.pxl;

/**
 * Algorithms used for computing the point of subjective equality (pse) and the
 * just noticable difference (jnd) of a psychometric function as the results of
 * an adaptive procedure. Psychometric function formulae are described by the
 * following paper:
 * 
 * <p>
 * Treutwein, B. (1995). Adaptive psychophysical procedures. Vision Research,
 * 35, 2503-2522.
 * 
 * @author H. Irtel
 * @version 0.2.0
 * @see AdaptiveControl
 */
/*
 * 09/13/01
 */
public interface AdaptiveResultCodes extends PsychometricFunctionCodes {
	/** Do not compute any results. */
	public static final int NO_RESULTS = 0;
	/**
	 * The pse is estimated as the mean of the last AdaptiveComputingPoints
	 * points and the jnd as the standard deviation of these.
	 */
	public static final int TAIL_MEAN = 1;
	/**
	 * The pse is estimated as the mean of all turn points and the jnd as the
	 * standard deviation of these.
	 */
	public static final int TURNPOINT_MEAN = 2;
	/**
	 * This allows arbitrary quantile estimation by isotonic regression.
	 */
	public static final int ISOTONIC_REGRESSION = 3;
}
