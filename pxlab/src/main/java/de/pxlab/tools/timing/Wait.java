package de.pxlab.tools.timing;

import java.awt.*;
import java.util.Random;

import de.pxlab.pxl.WaitLock;
import de.pxlab.pxl.HiresClock;
import de.pxlab.util.*;

/**
 * Check the precision of PXLab's WaitLock() mechanism for waiting a certain
 * number of milliseconds. This tool will generate a number of random wait
 * intervals and check how long the wait actually takes. The output is a table
 * like this:
 * 
 * <pre>
 * Time	Wait	n
 * 1	-	0
 * 2	2	1
 * 3	3	2
 * 4	4	1
 * 5	5	2
 * 6	6	2
 * 7	-	0
 * 8	8	2
 * 9	-	0
 * 10	10	1
 * 11	-	0
 * 12	-	0
 * 13	13	1
 * 14	14	1
 * 15	15	1
 * 16	16	1
 * 17	17	2
 * 18	18	2
 * 19	19	1
 * 20	-	0
 * </pre>
 * 
 * <p>
 * The first column contains the intended wait duration in milliseconds. The
 * second column contains the average actual wait and the last column contains
 * the number of samples measured for that time interval. The above table was
 * created with PXLab's optimized wait locking mechanism. It shows perfect
 * reliability. The following table is created with option '-k o' which checks
 * the java.lang.Object class's wait() method running under Windows XP:
 * 
 * <pre>
 * Time	Wait	n
 * 1	-	0
 * 2	15	1
 * 3	15	1
 * 4	-	0
 * 5	15	1
 * 6	15	1
 * 7	-	0
 * 8	15	3
 * 9	15	1
 * 10	15	1
 * 11	15	1
 * 12	15	1
 * 13	15	1
 * 14	15	2
 * 15	15	2
 * 16	31	2
 * 17	-	0
 * 18	31	1
 * 19	-	0
 * 20	31	1
 * </pre>
 * 
 * <p>
 * This table shows that the java.lang.Object class wait() method has a
 * granularity of about 15 or 16 milliseconds on Windows XP.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class Wait extends Frame implements CommandLineOptionHandler {
	private String options = "k:n:m:?";
	private String lockType = "hires";
	private int N = 600;
	private int M = 120;
	private WaitLock waitLock;

	public Wait(String[] args) {
		super(" Wait Timing Test");
		pack();
		setVisible(true);
		CommandLineParser clp = new CommandLineParser(args, options, this);
		if (clp.hasMoreArgs()) {
			while (clp.hasMoreArgs()) {
			}
		}
		int[] d = new int[N];
		double[] t = new double[N];
		long t1, t2;
		Random r = new Random();
		lockType = lockType.toLowerCase();
		if (lockType.startsWith("c")) {
			waitLock = new ConditionWaitLock();
		} else if (lockType.startsWith("o")) {
			waitLock = new ObjectWaitLock();
		} else if (lockType.startsWith("p")) {
			waitLock = new ParkingWaitLock();
		} else {
			waitLock = new WaitLock();
		}
		System.out.println("WaitLock: " + waitLock.getClass().getName());
		System.out.print("Checking random wait intervals ");
		for (int i = 0; i < N; i++) {
			d[i] = r.nextInt(M) + 1;
			t1 = HiresClock.getTimeNanos();
			waitLock.waitFor(d[i]);
			t2 = HiresClock.getTimeNanos();
			t[i] = ((double) (t2 - t1)) / 1000000L;
			System.out.print(".");
		}
		System.out.println("");
		int[] s = new int[M];
		int[] q = new int[M];
		for (int i = 0; i < M; i++) {
			s[i] = 0;
			q[i] = 0;
		}
		for (int i = 0; i < N; i++) {
			s[d[i] - 1] += t[i];
			q[d[i] - 1]++;
		}
		System.out.println("Time\tWait\tn");
		for (int i = 0; i < M; i++) {
			String m = (q[i] > 0) ? String.valueOf(Math.round((double) s[i]
					/ (double) q[i])) : "-";
			System.out.println((i + 1) + "\t" + m + "\t" + q[i]);
		}
		System.exit(0);
	}

	// --------------------------------------------------------------------
	// CommandLineOptionHandler implementation
	// --------------------------------------------------------------------
	/**
	 * This ist the command line option handler which is called for every
	 * command line option found.
	 */
	public void commandLineOption(char c, String arg) {
		// System.out.println("ExRunOptionHandler.commandLineOption(): -" + c +
		// " " + arg);
		try {
			switch (c) {
			case 'k':
				lockType = arg;
				break;
			case 'n':
				N = Integer.parseInt(arg);
				break;
			case 'm':
				M = Integer.parseInt(arg);
				break;
			case '?':
				showOptions();
				System.exit(1);
				break;
			}
		} catch (NumberFormatException nex) {
			System.out.println("Number error.");
			showOptions();
			System.exit(1);
		}
	}

	/**
	 * This method is called whenever an error is found in the command line
	 * options.
	 */
	public void commandLineError(int e, String s) {
		System.out.println("Command line error: " + s);
		showOptions();
	}

	// --------------------------------------------------------------------
	// End of CommandLineOptionHandler implementation
	// --------------------------------------------------------------------
	/** This shows the available command line options. */
	private void showOptions() {
		System.out.println("Usage: java " + this.getClass().getName()
				+ " [options] [assignments]");
		System.out.println("Options are:");
		System.out.println("   -k code  set WaitLock type to \'code\'");
		System.out.println("   -n n     number of checks");
		System.out
				.println("   -m n     set timing interval range to [1 ... n] ");
	}

	public static void main(String[] args) {
		new Wait(args);
	}
}
