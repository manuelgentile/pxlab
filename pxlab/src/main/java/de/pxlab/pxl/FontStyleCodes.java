package de.pxlab.pxl;

/**
 * Codes for defining text font styles.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public interface FontStyleCodes {
	/*
	 * Constants to be used for styles. Can be combined to mix styles.
	 */
	/**
	 * The plain style constant.
	 */
	public static final int PLAIN = java.awt.Font.PLAIN;
	/**
	 * The bold style constant. This can be combined with the other style
	 * constants (except PLAIN) for mixed styles.
	 */
	public static final int BOLD = java.awt.Font.BOLD;
	/**
	 * The italicized style constant. This can be combined with the other style
	 * constants (except PLAIN) for mixed styles.
	 */
	public static final int ITALIC = java.awt.Font.ITALIC;
}
