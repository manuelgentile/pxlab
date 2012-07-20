package de.pxlab.pxl;

/*
 This is a ExParValue which actually gets its value from a java class
 constant.

 @author H. Irtel
 @version 0.2.0

 */
/*

 2005/06/13 use classConstant

 */
public class ExParValueConstant extends ExParValue {
	/**
	 * Create an ExParValue object which gets its value from the given
	 * experimental parameter at run time. This constructor must not be called
	 * for symbolic class constants.
	 */
	public ExParValueConstant(String n) {
		ExParValue x = getClassConstant(n);
		if (x != null) {
			set(x);
		}
		classConstant = new String[1];
		classConstant[0] = n;
		typeConjecture = TYPE_CLASS_CONSTANT;
	}
}
