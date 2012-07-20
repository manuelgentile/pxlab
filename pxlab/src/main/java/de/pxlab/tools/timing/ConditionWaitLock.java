package de.pxlab.tools.timing;

import java.util.concurrent.locks.*;

import de.pxlab.pxl.WaitLock;

public class ConditionWaitLock extends WaitLock {
	private final ReentrantLock lock = new ReentrantLock();
	private Condition condition;

	public ConditionWaitLock() {
		condition = lock.newCondition();
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
			lock.lock();
			try {
				try {
					condition.awaitNanos(nanoDuration);
				} catch (InterruptedException iex) {
				}
			} finally {
				lock.unlock();
			}
		}
	}

	/** Wake up the thread waiting for this lock. */
	public void wakeUp() {
		lock.lock();
		try {
			condition.signal();
		} finally {
			lock.unlock();
		}
	}
}
