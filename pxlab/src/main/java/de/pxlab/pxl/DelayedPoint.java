package de.pxlab.pxl;

import java.awt.Point;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * A Point which implements the Delayed interface.
 * 
 * @version 0.1.1
 */
public class DelayedPoint extends Point implements Delayed {
	/**
	 * This is the time in nanoseconds when this Point's delay will have passed.
	 */
	protected long t;

	/**
	 * Create a new TimedPoint.
	 * 
	 * @param x
	 *            the x coordinate of the point.
	 * @param y
	 *            the y coordinate of the point.
	 * @param d
	 *            the intended delay in nanoseconds.
	 */
	public DelayedPoint(int x, int y, long d) {
		super(x, y);
		t = HiresClock.getTimeNanos() + d;
	}

	// ---------------------------------------------------
	// Implementation of the Delayed interface
	// ---------------------------------------------------
	public long getDelay(TimeUnit unit) {
		return unit
				.convert(t - HiresClock.getTimeNanos(), TimeUnit.NANOSECONDS);
	}

	public int compareTo(Delayed x) {
		long dt = getDelay(TimeUnit.NANOSECONDS);
		long dx = x.getDelay(TimeUnit.NANOSECONDS);
		int r = 0;
		if (dt > dx)
			r = 1;
		else if (dt < dx)
			r = -1;
		return r;
	}
	// ---------------------------------------------------
	// End of implementation of the Delayed interface
	// ---------------------------------------------------
}
