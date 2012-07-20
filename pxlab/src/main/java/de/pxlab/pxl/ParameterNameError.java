package de.pxlab.pxl;

/**
 * A modal dialog which is called upon a parameter name errors. These are run
 * errors which result from illegal parameter values being used in a design
 * file.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class ParameterNameError extends NonFatalError {
	public ParameterNameError(String m) {
		super(m);
	}
}
