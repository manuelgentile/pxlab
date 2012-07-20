package de.pxlab.util;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MemoryMonitor extends Frame {
	private Label totalMem;
	private Label freeMem;
	private Label freeAfterGCMem;
	private Timer timer;

	public MemoryMonitor() {
		super("Memory Monitor");
		setLocation(800, 600);
		Font fnt = new Font("SansSerif", Font.BOLD, 14);
		totalMem = new Label("Available Memory:                     ");
		totalMem.setFont(fnt);
		freeMem = new Label("Free Memory: ");
		freeMem.setFont(fnt);
		freeAfterGCMem = new Label("Free Memory after GC: xxxxxxxxxxxx Byte");
		freeAfterGCMem.setFont(fnt);
		setLayout(new GridLayout(0, 1));
		add(totalMem);
		add(freeMem);
		add(freeAfterGCMem);
		pack();
		timer = new Timer(true);
		timer.schedule(new Report(), 0L, 1000L);
		setVisible(true);
	}

	public void finalize() throws Throwable {
		timer.cancel();
		super.finalize();
	}
	/**
	 * This is a wrapper class for running the Runnable task argument of the
	 * start() method of this timer.
	 */
	private class Report extends TimerTask {
		private Runtime runtime;
		private long total;
		private long free;
		private long prevFree = 0;
		private NumberFormat nf;

		public Report() {
			runtime = Runtime.getRuntime();
			nf = NumberFormat.getInstance();
			nf.setGroupingUsed(true);
		}

		public void run() {
			total = runtime.totalMemory();
			totalMem.setText("Available Memory: " + nf.format(total) + " Byte");
			free = runtime.freeMemory();
			freeMem.setText("Free Memory: " + nf.format(free) + " Byte");
			if (free > prevFree) {
				// The GC was active
				freeAfterGCMem.setText("Free Memory after GC: "
						+ nf.format(free) + " Byte");
			}
			prevFree = free;
		}
	}
}
