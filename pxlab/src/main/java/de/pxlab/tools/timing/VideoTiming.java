package de.pxlab.tools.timing;

import java.awt.*;

import de.pxlab.pxl.*;
import de.pxlab.pxl.run.ExRunOptionHandler;

/**
 * Measure the duration of a video frame and of a time interval of about the
 * size of a response time interval. This can be used to test the internal time
 * measurement methods since the video frame duration should be hardware
 * controlled and be a stable reference duration. The target interval is about
 * 200 ms rounded up to an integer multiple of video frame durations. The output
 * looks like this:
 * 
 * <pre>
 *    Nominal refresh rate          = 100 Hz
 *    Nominal frame duration        = 10000000 ns
 *    Number of frames observed     = 1000
 *    Actual frame duration minimum = 9938671 ns
 *    Actual frame duration maximum = 10051074 ns
 *    Number of single frame misses = 0
 *    Actual frame duration mean    = 9994930 ns
 *    Actual frame duration sd      = 5268 ns [0.0527 %]
 *    Target duration               = 200000000 ns
 *    Actual duration minimum       = 199869383 ns
 *    Actual duration maximum       = 199910959 ns
 *    Actual duration mean          = 199898600 ns
 *    Actual duration sd            = 6221 ns [0.0031 %]
 * </pre>
 * 
 * The command line generating this output was:
 * 
 * <pre>
 *    java de.pxlab.pxl.tools.timing.VideoTiming -S 4 -w 800 -h 600 -R 100
 * </pre>
 * 
 * This defines a 100 Hz refresh video mode. Thus the nominal frame duration is
 * 100 ms = 100000000 ns. The actual frame duration mean is slightly less than
 * this, probably because of video timing requirements which have to be observed
 * by the graphics controller. The result shows that 1000 frames have been
 * observed and no single video frame was missed. The standard deviation for the
 * measured video frame duration was about 0.05 % of the mean. Also note that
 * the standard deviation of the 'response time' interval is about the same
 * size. This indicates that timing precision is independent of the duration to
 * be measured.
 * 
 * <p>
 * Looking at the single measurement data of the 1000 video frames reveals a
 * high negative correlation (-0.52) of two successive frame duration values but
 * an almost 0 correlation for all other lags. This means that a measurement
 * delay for any single frame is corrected at the next frame.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class VideoTiming extends Frame {
	private int n = 200;
	private boolean printFrames = false;

	public VideoTiming(String[] args) {
		setVisible(true);
		if ((args != null) && (args.length > 0)) {
			ExRunOptionHandler optionHandler = new ExRunOptionHandler(
					"VideoTiming", args);
			optionHandler.evaluate();
			// commandLineAssignments =
			// optionHandler.getCommandLineAssignments();
		}
		ExperimentalDisplayDevice dd = new ExperimentalDisplayDevice(this,
				null, null, null);
		dd.open();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException iex) {
		}
		int refreshRate = dd.getDisplayMode().getRefreshRate();
		long frameDuration = (1000000000L / refreshRate);
		long frameDurationThreshold = 3L * frameDuration / 2L;
		System.out.println("\nStarting to measure video frames");
		System.out.println("Estimated duration:             "
				+ (frameDuration * n) / 1000000000 + " seconds ...\n");
		System.gc();
		int n1 = n + 1;
		long[] begin_vb = new long[n1];
		long[] vb = new long[n];
		// An initial call in order to make sure the DLL is loaded.
		VideoSystem.waitForBeginOfVerticalBlank();
		HiresClock.getTime();
		for (int i = 0; i < n1; i++) {
			VideoSystem.waitForBeginOfVerticalBlank();
			begin_vb[i] = HiresClock.getTimeNanos();
		}
		dd.close();
		System.out.println("Nominal refresh rate          = " + refreshRate
				+ " Hz");
		System.out.println("Nominal frame duration        = " + frameDuration
				+ " ns");
		System.out.println("Number of frames observed     = " + n);
		long s = 0L;
		long ss = 0L;
		int m = 0;
		long vmin = Long.MAX_VALUE, vmax = 0L;
		long t;
		int mmx = 0;
		for (int k = 1; k < n1; k++) {
			t = begin_vb[k] - begin_vb[k - 1];
			vb[k - 1] = t;
			s += t;
			ss += (t * t);
			m++;
			if (t < vmin)
				vmin = t;
			if (t > vmax)
				vmax = t;
			if (t > frameDurationThreshold)
				mmx++;
		}
		double mean = (double) s / m;
		double sd = Math.sqrt(((double) ss - m * mean * mean) / (m - 1));
		double sdp = Math.round(10000.0 * 100.0 * sd / mean) / 10000.0;
		System.out.println("Actual frame duration minimum = " + vmin + " ns");
		System.out.println("Actual frame duration maximum = " + vmax + " ns");
		System.out.println("Number of single frame misses = " + mmx);
		System.out.println("Actual frame duration mean    = "
				+ Math.round(mean) + " ns");
		System.out.println("Actual frame duration sd      = " + Math.round(sd)
				+ " ns" + " [" + sdp + " %]");
		long intendedDuration = 200000000L;
		int nFrames = (int) Math.round((double) intendedDuration
				/ (double) frameDuration);
		long measuredDuration = nFrames * frameDuration;
		s = 0L;
		ss = 0L;
		m = 0;
		vmin = Long.MAX_VALUE;
		vmax = 0L;
		for (int k = nFrames; k < n1; k += nFrames) {
			t = begin_vb[k] - begin_vb[k - nFrames];
			s += t;
			ss += (t * t);
			m++;
			if (t < vmin)
				vmin = t;
			if (t > vmax)
				vmax = t;
		}
		mean = (double) s / m;
		sd = Math.sqrt(((double) ss - m * mean * mean) / (m - 1));
		sdp = Math.round(10000.0 * 100.0 * sd / mean) / 10000.0;
		System.out.println("Target duration               = "
				+ measuredDuration + " ns");
		System.out.println("Actual duration minimum       = " + vmin + " ns");
		System.out.println("Actual duration maximum       = " + vmax + " ns");
		System.out.println("Actual duration mean          = "
				+ Math.round(mean) + " ns");
		System.out.println("Actual duration sd            = " + Math.round(sd)
				+ " ns" + " [" + sdp + " %]");
		if (printFrames) {
			for (int k = 0; k < n; k++) {
				System.out.println(k + " " + vb[k]);
			}
		}
		System.exit(0);
	}

	public static void main(String[] args) {
		new VideoTiming(args);
	}
}
