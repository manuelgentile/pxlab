package de.pxlab.pxl;

/**
 * A high resolution clock for measuring response times or other short time
 * intervals. This class provides a high resolution clock which can be used to
 * measure time intervals with high precision. It uses the best available timer.
 * 
 * @version 0.3.1
 * @see java.lang.Thread
 */
/*
 * 
 * 05/23/03 added granularity() method
 * 
 * 2004/10/01 added support for Java 1.5 System.nanoTime() method
 * 
 * 2007/02/12 removed all non-Java timers.
 * 
 * 2007/03/18 removed reference to de.pxlab.stat.Stats.mean()
 */
public class HiresClock {
	private static HighResolutionTimer timer;
	private static String timerName;
	private static int timerGranularity;
	private static int sleepTimerGranularity;
	private static int checksPerTimerGranularityStep;
	static {
		timer = new HighResolutionTimer();
		timerName = "java.lang.System.nanoTime()";
		int[] g = computeTimerGranularity();
		timerGranularity = g[0];
		checksPerTimerGranularityStep = g[1];
		g = computeSleepTimerGranularity();
		sleepTimerGranularity = g[0];
	}

	public static long getTime() {
		return timer.getTime();
	}

	public static double getTimeDouble() {
		return timer.getTimeDouble();
	}

	public static long getTimeNanos() {
		return timer.getTimeNanos();
	}

	/**
	 * Delay the current thread by the given number of milliseconds. The method
	 * uses a combination of non-blocking and blocking delay to get optimal
	 * results. First the current thread is put to sleep using the method
	 * sleep() of class Thread such that other threads are allowed to use system
	 * resources during this interval. The final section of the time interval
	 * given has to use a blocking delay since the sleep() method of class
	 * Thread offers poor granularity for timing.
	 */
	public static void delay(int ms) {
		long t1, t2;
		// t1 = getTimeNanos();
		long t = timer.getTimeNanos() + (long) ms * 1000000L;
		int dms = ms - sleepTimerGranularity;
		// first sleep as long as possible
		if (dms > 0) {
			try {
				Thread.sleep(dms);
			} catch (InterruptedException iex) {
				iex.printStackTrace();
			}
		}
		// finally block until the target time is reached
		while (getTimeNanos() < t)
			;
		// t2 = getTimeNanos();
		// System.out.println("HiresClock.delay() actual delay = " + (t2 - t1) +
		// " ns");
	}

	/**
	 * Delay the current thread by the given number of nanoseconds. The method
	 * uses a combination of non-blocking and blocking delay to get optimal
	 * results. First the current thread is put to sleep using the method
	 * sleep() of class Thread such that other threads are allowed to use system
	 * resources during this interval. The final section of the time interval
	 * given has to use a blocking delay since the sleep() method of class
	 * Thread offers pure granularity for timing.
	 */
	public static void delayNanos(long ns) {
		long t1, t2;
		t1 = getTimeNanos();
		long t = timer.getTimeNanos() + ns;
		System.out.println("ciao");
		int dms = (int) (ns / 1000000L) - sleepTimerGranularity;
		// first sleep as long as possible
		if (dms > 0) {
			try {
				Thread.sleep(dms);
			} catch (InterruptedException iex) {
				iex.printStackTrace();
			}
		}
		// finally block until the target time is reached
		while (getTimeNanos() < t)
			;
		// t2 = getTimeNanos();
		// System.out.println("HiresClock.delay() actual delay = " + (t2 - t1) +
		// " ns");
	}

	/**
	 * Block execution of the currently running program for a given number of
	 * nanoseconds. This method should be used very carefully since it actually
	 * blocks execution of the whole system.
	 * 
	 * @param ns
	 *            the number of nanoseconds to block.
	 */
	public static void blockingDelay(long ns) {
		long t = getTimeNanos() + ns;
		while (getTimeNanos() < t)
			;
	}

	/**
	 * Get a Delay object which can be used later. The strategy is to get a
	 * Delay object, then do something which will take less time than the delay
	 * itself and then tell the HiresClock to wait until the delay is finished.
	 * 
	 * @param d
	 *            delay duration in milliseconds.
	 * @return a Delay object which may be sent back later to the HiresClock in
	 *         order to finish the delay period.
	 */
	public static Delay getDelay(int d) {
		return new Delay(getTime(), d);
	}

	/**
	 * Delay thread execution until the given Delay object's end time has
	 * passed.
	 * 
	 * @param d
	 *            a Delay object which has been created by the HiresClock
	 *            earlier. It contains information about when the delay period
	 *            expires.
	 */
	public static void delay(Delay d) {
		delay((int) (d.start + d.interval - getTime()));
	}

	/**
	 * Get the granularity of the clock. This is the smallest numeric difference
	 * between successive ticks.
	 * 
	 * @return the smallest numeric difference between successive clock steps.
	 */
	private static int[] computeTimerGranularity() {
		int N = 20;
		long[] t = new long[N];
		int[] s = new int[N];
		for (int i = 0; i < N; i++)
			s[i] = 0;
		long t1, t2;
		int k = 0;
		t1 = timer.getTime();
		while (k < N) {
			t2 = timer.getTime();
			s[k]++;
			if (t2 != t1) {
				t[k++] = t2;
				t1 = t2;
			}
		}
		double[] d = new double[N - 1];
		for (int i = 0; i < (N - 1); i++) {
			d[i] = (int) (t[i + 1] - t[i]);
		}
		int[] g = new int[2];
		g[0] = (int) Math.round(mean(d));
		double[] p = new double[N];
		for (int i = 0; i < N; i++) {
			p[i] = (double) (s[i]);
		}
		g[1] = (int) Math.round(mean(p));
		return g;
	}

	private static int[] computeSleepTimerGranularity() {
		int N = 20;
		long[] t = new long[N];
		int[] s = new int[N];
		for (int i = 0; i < N; i++)
			s[i] = 0;
		long t1, t2;
		int k = 0;
		t1 = System.currentTimeMillis();
		while (k < N) {
			t2 = System.currentTimeMillis();
			s[k]++;
			if (t2 != t1) {
				t[k++] = t2;
				t1 = t2;
			}
		}
		double[] d = new double[N - 1];
		for (int i = 0; i < (N - 1); i++) {
			d[i] = (int) (t[i + 1] - t[i]);
		}
		int[] g = new int[2];
		g[0] = (int) Math.round(mean(d));
		double[] p = new double[N];
		for (int i = 0; i < N; i++) {
			p[i] = (double) (s[i]);
		}
		g[1] = (int) Math.round(mean(p));
		return g;
	}

	public static String getTimerName() {
		return timerName;
	}

	public static int getTimerGranularity() {
		return timerGranularity;
	}

	public static int getSleepTimerGranularity() {
		return sleepTimerGranularity;
	}

	public static int getChecksPerTimerGranularityStep() {
		return checksPerTimerGranularityStep;
	}

	/**
	 * Convert a nanosecond time into milliseconds.
	 * 
	 * @param nanos
	 *            the time to be converted in units of a nanosecond counter.
	 * @return the given time in milliseconds.
	 */
	public static double ms(long nanos) {
		return (double) nanos / 1000000.0;
	}

	/**
	 * Convert a nanosecond time interval into milliseconds.
	 * 
	 * @param nanos1
	 *            the nanosecond time interval start time.
	 * @param nanos2
	 *            the nanosecond time interval end time.
	 * @return the interval duration in milliseconds.
	 */
	public static double ms(long nanos1, long nanos2) {
		return (double) (nanos2 - nanos1) / 1000000.0;
	}

	/**
	 * Compute the mean of the given data array.
	 * 
	 * @param a
	 *            an array of data values whose mean should be computed.
	 * @return the arithmetic mean of the data array.
	 */
	private static double mean(double[] a) {
		double sa = 0.0;
		int n = a.length;
		for (int i = 0; i < n; i++) {
			sa += (double) a[i];
		}
		return sa / n;
	}
	public static class Delay {
		protected long start;
		protected int interval;

		public Delay(long s, int i) {
			start = s;
			interval = i;
		}
	}
}
