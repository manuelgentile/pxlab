package de.pxlab.pxl;

/**
 * A modal dialog which is called upon Display naming errors. These are run
 * errors which result from non-existent Display and DisplayElement names being
 * used in a design file.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class DisplayError extends NonFatalError {
	public DisplayError(String m) {
		super(m);
	}
}
