package de.pxlab.pxl;

public interface DitheringCodes {
	/** Don't use dithering. */
	public static final int NO_DITHERING = 0;
	/** Use random dithering. */
	public static final int RANDOM_DITHERING = 1;
	/** Use ordered dithering with a dithering matrix of size 2x2 pixels. */
	public static final int ORDERED_DITHERING_2X2 = 2;
	/** Use ordered dithering with a dithering matrix of size 3x3 pixels. */
	public static final int ORDERED_DITHERING_3X3 = 3;
	/** Use ordered dithering with a dithering matrix of size 4x4 pixels. */
	public static final int ORDERED_DITHERING_4X4 = 4;
}
