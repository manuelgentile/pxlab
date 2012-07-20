package de.pxlab.pxl;

/**
 * Codes for selecting a display screen in multiple screen display systems.
 * 
 * @author H. Irtel
 * @version 0.2.0
 * @see DisplayDevice
 */
/*
 * 2004/10/27
 */
public interface ScreenSelectionCodes {
	/** Don't mind. */
	public static final int NO_SELECTION = DisplayDevice.PRIMARY_SCREEN;
	/** Show display on left/primary screen. */
	public static final int LEFT_SCREEN = DisplayDevice.PRIMARY_SCREEN;
	/** Show display on right/secondary screen. */
	public static final int RIGHT_SCREEN = DisplayDevice.SECONDARY_SCREEN;
}
