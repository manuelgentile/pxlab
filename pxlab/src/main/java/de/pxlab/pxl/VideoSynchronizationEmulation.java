package de.pxlab.pxl;

/**
 * Emulates video sychronization capabilities. This class should not be used
 * directly but only via VideoSystem objects. It emulates a 75 Hz video refresh
 * rate.
 * 
 * @author H. Irtel
 * @version 0.2.0
 * @see VideoSystem
 */
public class VideoSynchronizationEmulation implements VideoSynchronization {
	private WaitLock waitLock;
	//private static final long cyclePeriod = 13333333L;
	
	private static final long cyclePeriod = 5000000L;

	protected VideoSynchronizationEmulation() {
		waitLock = new WaitLock();
	}

	/**
	 * Emulate a wait for the start of the next vertical retrace interval.
	 */
	public void waitForBeginOfVerticalBlank() {
		waitLock.waitUntilNanos(cyclePeriod
				* ((HiresClock.getTimeNanos() + cyclePeriod - 1L) / cyclePeriod));
	}

	/** Returns immediately. */
	public void waitForEndOfVerticalBlank() {
	}

	/** Is always false. */
	public boolean inVerticalBlank() {
		return false;
	}

	public boolean init() {
		return true;
	}

	public void close() {
	}

	/** Compute the duration of a single video frame. */
	public double getFrameDuration() {
		return (double) cyclePeriod / 1000000.0;
	}
}
