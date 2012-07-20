package de.pxlab.pxl;

/**
 * A timer with the highest resolution available on this system.
 * 
 * @version 0.2.0
 * @see java.lang.System
 * @see HiresClock
 */
public class HighResolutionTimer {
	/**
	 * Get the current time in milliseconds. Actual resolution will be system
	 * dependent.
	 * 
	 * @return the current time in milliseconds. The actual value is rounded
	 *         from the current time in the highest resolution timer available.
	 */
	public long getTime() {
		return (System.nanoTime() + 500000L) / 1000000L;
	}

	/**
	 * Get the current time in nanoseconds. Actual resolution will be system
	 * dependent.
	 */
	public long getTimeNanos() {
		return System.nanoTime();
	}

	/**
	 * Get the current time in milliseconds.
	 * 
	 * @return the current time in milliseconds as a double value. Precision is
	 *         defined by the highest resolution timer available on the system.
	 */
	public double getTimeDouble() {
		return ((double) System.nanoTime()) / 1000000.0;
	}
}
