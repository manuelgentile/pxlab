package de.pxlab.pxl;

/**
 * Classes which implement this interface are able to handle the transition form
 * full screen back to windowed display mode. They have to accept the request to
 * move back to windowed mode and they also have to accept some keyboard control
 * input which may be used to control propereties which are controlled by
 * buttons in window mode.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 2004/08/19
 */
public interface ReturnFromFullScreen {
	/** Move the display back to window mode. */
	public void returnFromFullScreen();

	/** Check whether the display is in full screen mode. */
	public boolean isFullScreen();

	/**
	 * Activate the given timing group subset of display elements. This is a
	 * full screen mode replacement the timing group buttons 'x', 1, 2, ...
	 */
	public void activateTimingGroup(int b);
}
