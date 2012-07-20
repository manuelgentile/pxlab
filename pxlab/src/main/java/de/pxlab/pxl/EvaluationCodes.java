package de.pxlab.pxl;

/**
 * Evaluation codes for the Feedback display object.
 * 
 * @author H. Irtel
 * @version 0.1.4
 */
public interface EvaluationCodes {
	public static final int NO_EVALUATION = 0;
	/**
	 * Compares the value of the parameter whose name is given in
	 * 'ResponseParameter' to the value of parameter 'CorrectCode'. If these are
	 * identical then 'Response' is set to correct (1), otherwise to false (2).
	 */
	public static final int COMPARE_CODE = 1;
	/**
	 * Checks whether the value of the parameter whose name is given in
	 * 'ResponseParameter' is contained in the set of values of 'SelectionSet'.
	 * If yes then the parameter 'Response' is set to the corresponding index of
	 * the response code in the selection set.
	 */
	public static final int SELECT_ONE = 2;
	public static final int LETTER = 3;
	public static final int TEXTLINE = 4;
	/**
	 * Evaluate the distance of a target color value int CIELAB coordinates
	 * defined by the parameter Feedback.TargetParameter with a response color
	 * parameter defined by Feedback.ResponseParameter. An error/false response
	 * is defined to have a resulting value which is larger than
	 * Feedback.CorrectCode.
	 */
	public static final int CIELAB_COLOR_DISTANCE = 5;
	/**
	 * Evaluate the chromatic distance as measured by CIELAB ab-chromaticity of
	 * a target color value defined by the parameter Feedback.TargetParameter
	 * with a response color parameter defined by Feedback.ResponseParameter. An
	 * error/false response is defined to have a resulting value which is larger
	 * than Feedback.CorrectCode.
	 */
	public static final int CIELAB_AB_DISTANCE = 6;
	/**
	 * Assume that the ResponseParameter is the name of an experimental
	 * parameter which holds the string description of a PxlColor and convert it
	 * according to this description. The resulting color object is stored in
	 * Response.
	 */
	public static final int STRING_TO_COLOR = 7;
	/**
	 * Compares the value of the parameter whose name is given in
	 * 'ResponseParameter' to the value of parameter 'CorrectCode'. If these are
	 * identical then 'Response' is set to correct and 'Visible' is set to 0,
	 * otherwise 'Response' is set to false and 'Visible' to 1.
	 */
	public static final int CHECK_NOGO = 8;
	/**
	 * Evaluate the distance of a response position to a target position. The
	 * target position is defined by a parameter whose name is given in
	 * Feedback.TargetParameter and the response position is given by a
	 * parameter whose name is defined by Feedback.ResponseParameter. An
	 * error/false response is defined to have a distance value which is larger
	 * than Feedback.CorrectCode. The actual distance is stored in parameter
	 * Response and the Text parameter is set according to the hit/miss state of
	 * the response.
	 */
	public static final int POSITION = 9;
	/**
	 * Same as POSITION but the parameter Response stores the value
	 * ResponseCodes.CORRECT for hits and ResponseCodes.ERROR for misses.
	 */
	public static final int POSITION_HIT = 10;
}
