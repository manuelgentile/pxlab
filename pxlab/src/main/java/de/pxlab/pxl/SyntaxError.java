package de.pxlab.pxl;

/**
 * A modal dialog which is called upon a syntax error in a design file.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class SyntaxError extends NonFatalError {
	public SyntaxError(String m) {
		super(m);
	}
}
