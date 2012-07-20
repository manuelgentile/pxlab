package de.pxlab.tools.timing;

import de.pxlab.pxl.WaitLock;

public class ObjectWaitLock extends WaitLock {
	private Object condition;

	public ObjectWaitLock() {
		condition = new Object();
	}

	/**
	 * Causes the current thread to wait until it is told to wake up or the
	 * specified waiting time elapses.
	 * 
	 * @param nanoDuration
	 *            the maximum time to wait, given in nanoseconds.
	 */
	public void waitForNanos(long nanoDuration) {
		if (nanoDuration > 0) {
			synchronized (condition) {
				try {
					condition.wait(nanoDuration / 1000000L,
							(int) (nanoDuration % 1000000L));
				} catch (InterruptedException iex) {
				}
			}
		}
	}

	/** Wake up the thread waiting for this lock. */
	public void wakeUp() {
		synchronized (condition) {
			condition.notify();
		}
	}
}
