package de.pxlab.pxl;

/**
 * Type codes for minimization functions.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2006/08/15
 */
public interface MinimizationFunctionCodes {
	/** Use chi square minimization. */
	public static final int CHI_SQUARE = 0;
	/** Use negative log likelihood minimization. */
	public static final int LOG_LIKELIHOOD = 1;
	/** Use minimization of squared deviations. */
	public static final int SQUARED_DEV = 2;
	/** Use minimization of squared deviations after logit transformation. */
	public static final int SQUARED_DEV_LOGIT = 3;
}
