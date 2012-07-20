package de.pxlab.pxl;

/**
 * Creates an experimental parameter value which does not have a valid value.
 * The method isNotSet() returns true for this value.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/02/15
 */
public class ExParValueNotSet extends ExParValue {
	public ExParValueNotSet() {
		super(0);
		setNotSet();
	}
}
