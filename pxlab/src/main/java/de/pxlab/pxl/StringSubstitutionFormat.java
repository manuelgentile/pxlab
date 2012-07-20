package de.pxlab.pxl;

/**
 * Format indicator characters for parameter value substitution in string
 * values. String valued parameters may contain names of experimental parameters
 * enclosed between two percent '%' signs like
 * 
 * <pre>
 * Text = &quot;%ResponseTime% ms&quot;;
 * </pre>
 * 
 * The string '%ResponseTime%' is then replaced by the current value of
 * parameter ResponseTime when the string value of parameter Text is computed.
 * This may result in a string like '324.3176' which may be undesireable. Format
 * characters solve this problem.
 * 
 * <pre>
 * Text = &quot;%ResponseTime@i% ms&quot;;
 * </pre>
 * 
 * will result in '324' as replacement string.
 * 
 * @version 0.1.0
 */
public interface StringSubstitutionFormat {
	/**
	 * Use the default format for an output value in substitution strings. This
	 * is the format which is identical to the parameter value syntax of design
	 * files.
	 */
	public static final char DEFAULT_FMT = '0';
	/**
	 * Format arrays as simple list of elements separated by a blank character
	 * when formatting substitution parameters in output strings.
	 */
	public static final char SIMPLE_ARRAY_FMT = 'b';
	/** Format scalar values as integers. */
	public static final char INTEGER_FMT = 'i';
	/** Format scalar values as strings. */
	public static final char STRING_FMT = 's';
	/** Format according to our internal type conjecture. */
	public static final char IGNORE_FMT = 'o';
}
