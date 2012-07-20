package de.pxlab.tools.timing;

import java.util.concurrent.locks.LockSupport;

import de.pxlab.pxl.WaitLock;

public class ParkingWaitLock extends WaitLock {
	private Thread waitingThread;

	/**
	 * Causes the current thread to wait until it is told to wake up or the
	 * specified waiting time elapses.
	 * 
	 * @param nanoDuration
	 *            the maximum time to wait, given in nanoseconds.
	 */
	public void waitForNanos(long nanoDuration) {
		if (nanoDuration > 0) {
			waitingThread = Thread.currentThread();
			LockSupport.parkNanos(nanoDuration);
		}
		waitingThread = null;
	}

	/** Wake up the thread waiting for this lock. */
	public void wakeUp() {
		if (waitingThread != null) {
			LockSupport.unpark(waitingThread);
		}
	}
}
