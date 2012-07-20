package de.pxlab.pxl;

/**
 * Codes for defining adaptive procedures.
 * 
 * @author H. Irtel
 * @version 0.2.0
 * @see AdaptiveControl
 */
/*
 * 09/13/01
 */
public interface AdaptiveProcedureCodes {
	public static final int NON_ADAPTIVE = 0;
	public static final int NON_CHANGING = 1;
	public static final int USER_DEFINED = 2;
	public static final int UP_1_DOWN_1 = 3;
	public static final int UP_1_DOWN_2 = 4;
	public static final int UP_1_DOWN_3 = 5;
	public static final int DELAYED_UP_1_DOWN_1 = 6;
	// Modify AdaptiveControl.LastAdaptiveProcedureCode if you add another code!
}
