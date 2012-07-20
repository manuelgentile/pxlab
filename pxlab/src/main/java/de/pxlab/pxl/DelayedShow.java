package de.pxlab.pxl;

import java.awt.Graphics;

/**
 * A Thread which shows the active timing group of a display object at a certain
 * delayed point in time. The show method does not paint the background and such
 * it behaves like a TRANSPARENT Display object. This thread is set to minimum
 * priority in order not to interfere with the rest of PXLab's timing mechanism.
 * 
 * @version 0.1.0
 * @author H. Irtel
 */
/*
 * 
 * 2008/03/05
 */
public class DelayedShow extends Thread {
	private Display activeDisplay;
	private DisplayDevice displayDevice;
	private WaitLock waitLock;
	private long showTimeNanos;

	/**
	 * Create the thread and store its parameters.
	 * 
	 * @param activeDisplay
	 *            is the Display object whose timing group has to be shown.
	 * @param displayDevice
	 *            is the display device for showing the display object.
	 * @param showTimeNanos
	 *            is the time of day in nanoseconds when the timing group is to
	 *            be shown.
	 */
	public DelayedShow(Display activeDisplay, DisplayDevice displayDevice,
			long showTimeNanos) {
		this.activeDisplay = activeDisplay;
		this.displayDevice = displayDevice;
		this.showTimeNanos = showTimeNanos;
		setPriority(Thread.MIN_PRIORITY);
		waitLock = new WaitLock();
	}

	/**
	 * Start the delay period and then show the active timing group of the
	 * Display object. The object is only shown if its Execute flag is true at
	 * presentation time.
	 */
	public void run() {
		Debug.time("Delayed show start delay interval: ---> ");
		waitLock.waitUntilNanos(showTimeNanos);
		if (activeDisplay.Execute.getFlag()) {
			displayDevice.setActiveScreen(activeDisplay.Screen.getInt());
			Graphics g = displayDevice.getComponent().getGraphics();
			activeDisplay.setGraphicsContext(g);
			activeDisplay.showGroup();
			Debug.time("Delayed object shown: -> ");
			g.dispose();
		}
	}
}
