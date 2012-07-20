package de.pxlab.pxl;

/**
 * Dots for building random dot patterns. Every dot has a horizontal and
 * vertical screen position and also carries a code which may be used to
 * identify further dot properties like color or form.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 05/28/02
 */
public class RandomDot {
	public static final int BACKGROUND = 1;
	public static final int SELECTION = 2;
	public static final int CENTER = 3;
	/** Horizontal dot position. */
	public int x;
	/** Vertical dot position. */
	public int y;
	/** Attribute code for this dot. */
	public int code;

	/**
	 * Create a single dot at the given position using the given attribute code.
	 */
	public RandomDot(int x, int y, int code) {
		this.x = x;
		this.y = y;
		this.code = code;
	}
}
