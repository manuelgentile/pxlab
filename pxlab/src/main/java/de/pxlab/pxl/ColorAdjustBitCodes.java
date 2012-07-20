package de.pxlab.pxl;

/**
 * Control codes for adjustable colors.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see de.pxlab.pxl.display.ColorAdjustableHSB
 */
public interface ColorAdjustBitCodes {
	/** Allow adjustment of the lightness coordinate. */
	public static final int LIGHTNESS = 1;
	/** Allow adjustment of the hue coordinate. */
	public static final int HUE = 2;
	/** Allow adjustment of the chroma coordinate. */
	public static final int CHROMA = 4;
}
