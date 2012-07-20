package de.pxlab.pxl;

/**
 * Codes for defining the Overlay display parameter.
 * 
 * @author H. Irtel
 * @version 0.1.2
 * @see Display
 * @see TimerCodes
 */
public interface OverlayCodes {
	/** No Overlay. */
	public static final int NONE = 0;
	/**
	 * Join the non-background display elements of this display object with the
	 * previous display object in the display list such that the display
	 * elements of both display objects are presented at the same time. This
	 * requires that the Timer parameter of the previous display object is set
	 * to NO_TIMER because display objects which have the JOIN overlay set are
	 * actually joined to a single memory buffer image for preloading. The timer
	 * type used for this joined display object is the timer type of the last
	 * JOIN element in the chain.
	 */
	public static final int JOIN = 1;
	/**
	 * Make this display an overlay for all subsequent display objects in the
	 * display list. The effect is that this display is drawn without its
	 * background and with no regard to its timing properties. It is redrwan
	 * after each subsequent timing group of the display list until a display
	 * object has the overlay parameter set to CLEAR_DISPLAY_LIST.
	 */
	public static final int DISPLAY_LIST = 2;
	/**
	 * Remove the currently installed display list overlay and present this
	 * display as if it has NONE as its overlay code.
	 */
	public static final int CLEAR_DISPLAY_LIST = 3;
	/**
	 * This display object is treated as usual with the only exception that its
	 * background display element is never shown. All other display elements are
	 * shown as usual. The implementation is such that this display object's
	 * non-background elements are painted onto a memory buffer which contains
	 * the most recently used memory buffer image. This image buffer is then
	 * used for buffer preloading as usual.
	 */
	public static final int TRANSPARENT = 4;
}
