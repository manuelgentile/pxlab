package de.pxlab.pxl;

/**
 * This is a modal dialog which shows up on the detection of fatal errors in an
 * experimental run. It terminantes the experimental run but does not terminate
 * run time controllers and design editors.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class FatalRunError extends FatalError {
	public FatalRunError(String s) {
		super(s);
	}
}
