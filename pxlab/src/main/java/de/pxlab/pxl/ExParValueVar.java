package de.pxlab.pxl;

/*
 This is a ExParValue which actually gets its value from a named
 experimental parameter. 

 @author H. Irtel
 @version 0.4.1

 */
public class ExParValueVar extends ExParValue {
	/**
	 * Create an ExParValue object which gets its value from the given
	 * experimental parameter at run time. This constructor must not be called
	 * for symbolic class constants.
	 */
	public ExParValueVar(String n) {
		needsEvaluation = true;
		valueParam = n;
		// System.out.println("ExParValueVar() instantiated for " + n + " = " +
		// getValue());
	}
}
