package de.pxlab.pxl;

/**
 * Describes vertical blanking sychronization capabilities.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public interface VideoSynchronization {
	/** Wait until the next vertical blanking interval starts. */
	public void waitForBeginOfVerticalBlank();

	/** Wait until the current/next vertical blanking interval is finished. */
	public void waitForEndOfVerticalBlank();

	/**
	 * Check whether we currently are in a vertical blanking interval. Note that
	 * vertical blanking duration is only approximately 0.02 to 0.03 ms.
	 */
	public boolean inVerticalBlank();

	public boolean init();

	public void close();

	/** Compute the duration of a single video frame. */
	public double getFrameDuration();
}
