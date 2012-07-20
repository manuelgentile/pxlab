package de.pxlab.pxl;

/**
 * A modal message dialog which is called upon a parameter value error. These
 * are run errors which result from illegal parameter values being used in a
 * design file.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class ParameterValueError extends NonFatalError {
	public ParameterValueError(String m) {
		super(m);
	}
}
