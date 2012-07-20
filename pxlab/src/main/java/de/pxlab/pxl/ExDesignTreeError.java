package de.pxlab.pxl;

/**
 * A modal dialog which is called upon an error in the design file/tree
 * structure.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class ExDesignTreeError extends NonFatalError {
	public ExDesignTreeError(String m) {
		super(m);
	}
}
