package de.pxlab.util;

/**
 * This interface defines methods for handling command line options.
 * 
 * @author H. Irtel
 * @version 0.1.2
 */
/*
 * 
 * 04/13/00
 */
public interface CommandLineOptionHandler {
	public static final int unknownOptionChar = 1;
	public static final int missingOptionArg = 2;

	/** This method is called for every command line option found. */
	public void commandLineOption(char c, String arg);

	/**
	 * This method is called whenever an error in the command line options is
	 * found.
	 */
	public void commandLineError(int e, String s);
}
