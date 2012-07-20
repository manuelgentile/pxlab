package de.pxlab.pxl;

/**
 * A lock object which may be used to put the current thread to sleep for a
 * given time and wake it up again. This class tries to get the millisecond
 * resolution timing combined with a low CPU load factor.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
public class HiresWaitLock extends WaitLock {
	private boolean waiting;
	private static final long sleepBound = 20 * 1000000L;
	private static final long yieldBound = 2 * 1000000L;

	/**
	 * Causes the current thread to wait until it is told to wake up or the
	 * specified waiting time elapses.
	 * 
	 * @param nanoDuration
	 *            the maximum time to wait, given in nanoseconds.
	 */
	public void waitForNanos(long nanoDuration) {
		if (nanoDuration > 0) {
			long t = HiresClock.getTimeNanos() + nanoDuration;
			long t0 = t - sleepBound;
			long t1 = t - yieldBound;
			waiting = true;
			while (waiting && (HiresClock.getTimeNanos() < t0)) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException iex) {
				}
			}
			while (waiting && (HiresClock.getTimeNanos() < t1)) {
				Thread.yield();
			}
			while (waiting && (HiresClock.getTimeNanos() < t)) {
			}
		}
	}

	/** Causes the current thread to wait until it is told to wake up. */
	public void waitFor() {
		waiting = true;
		while (waiting) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException iex) {
			}
		}
	}

	/** Wake up the thread waiting for this lock. */
	public void wakeUp() {
		// Debug.show(Debug.EVENTS, "HiresWaitLock.wakeUp()");
		waiting = false;
	}
}
