package de.pxlab.tools.timing;

import de.pxlab.pxl.*;
import de.pxlab.stat.Stats;

/**
 * Compute the granularity of the PXLab high resolution timer. This class reads
 * timer values and counts how many values may be read while the timer does not
 * change its value. It also checks what the average difference between two
 * succesive different timer values is. If the number of read operations at a
 * single timer value is 1 then the timer changes value at each read operation.
 * This means that the actual granularity could not be determined since reading
 * the timer and checking whether the value read is different from the prviously
 * read value takes longer as the granularity of the timer.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class TimerGranularity {
	public TimerGranularity() {
		int[] g = computeTimerGranularity(1000);
		if (g[1] == 1) {
			g = computeTimerGranularity(1000000);
			System.out.println("HiresClock granularity could not be measured!");
			System.out.println("It must be less than " + g[0] + " ns.");
		} else {
			System.out.println("HiresClock granularity is " + g[0] + " ns.");
			System.out
					.println("Number of calls executed at a single granularity step: "
							+ g[1]);
		}
	}

	private static int[] computeTimerGranularity(int N) {
		// HighResolutionTimer timer = new HighResolutionTimerEmulation();
		// HighResolutionTimer timer = new Java5TimerWrapper();
		HighResolutionTimer timer = null;
		// s[k] counts the number of calls which result in timing value t[k]
		long[] t = new long[N];
		int[] s = new int[N];
		for (int i = 0; i < N; i++)
			s[i] = 0;
		long t1, t2;
		if (timer != null) {
			t1 = timer.getTimeNanos();
			int k = 0;
			t1 = timer.getTimeNanos();
			while (k < N) {
				t2 = timer.getTimeNanos();
				s[k]++;
				if (t2 != t1) {
					t[k++] = t2;
					t1 = t2;
				}
			}
		} else {
			t1 = HiresClock.getTimeNanos();
			int k = 0;
			t1 = HiresClock.getTimeNanos();
			while (k < N) {
				t2 = HiresClock.getTimeNanos();
				s[k]++;
				if (t2 != t1) {
					t[k++] = t2;
					t1 = t2;
				}
			}
		}
		// d[i] timing value difference between t[i+1] and t[i]
		double[] d = new double[N - 1];
		for (int i = 0; i < (N - 1); i++) {
			d[i] = (int) (t[i + 1] - t[i]);
		}
		int[] g = new int[2];
		// Mean difference of succesive timing values
		g[0] = (int) Math.round(Stats.mean(d));
		double[] p = new double[N];
		for (int i = 0; i < N; i++) {
			p[i] = (double) (s[i]);
		}
		// Mean number of calls to HiresClock.getTimeNanos() at each value
		g[1] = (int) Math.round(Stats.mean(p));
		return g;
	}

	public static void main(String[] args) {
		new TimerGranularity();
	}
}
