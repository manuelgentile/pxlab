package de.pxlab.pxl;

/**
 * A modal dialog which is called upon an error related to the non-existence of
 * a stimulus file.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class FileError extends ParameterValueError {
	public FileError(String m) {
		super(m);
	}
}
