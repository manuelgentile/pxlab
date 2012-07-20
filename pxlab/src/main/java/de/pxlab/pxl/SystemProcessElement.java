package de.pxlab.pxl;

import java.util.ArrayList;
import java.io.*;

import de.pxlab.util.*;

/**
 * Runs an external process.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/09/22
 */
public class SystemProcessElement extends DisplayElement implements Runnable {
	private ProcessBuilder processBuilder;
	private boolean waitForProcess;
	private int exitValue;
	protected MediaEventListener mediaEventListener;

	public SystemProcessElement() {
	}

	public void setProperties(String[] cmd, String[] arg, String wd,
			boolean wfp, MediaEventListener listener) {
		processBuilder = null;
		ArrayList a = new ArrayList();
		if (StringExt.nonEmpty(cmd[0])) {
			for (int i = 0; i < cmd.length; i++)
				if (StringExt.nonEmpty(cmd[i]))
					a.add(cmd[i]);
			for (int i = 0; i < arg.length; i++)
				if (StringExt.nonEmpty(arg[i]))
					a.add(arg[i]);
			processBuilder = new ProcessBuilder(a);
			if (StringExt.nonEmpty(wd)) {
				File dir = new File(wd);
				if (dir.isDirectory()) {
					processBuilder.directory(dir);
				}
			}
		}
		waitForProcess = wfp;
		mediaEventListener = listener;
	}

	public void show() {
		// System.out.println("SystemProcessElement.show()");
		if (processBuilder != null) {
			Thread t = new Thread(this);
			t.start();
		}
	}

	public void run() {
		// System.out.println("SystemProcessElement.run()");
		long startTime = de.pxlab.pxl.HiresClock.getTimeNanos();
		try {
			Process process = processBuilder.start();
			if (waitForProcess) {
				try {
					// System.out.print("SystemProcessElement.run() waiting ...");
					exitValue = process.waitFor();
					// System.out.println("Finished!");
				} catch (InterruptedException iex) {
				}
			}
		} catch (IOException iox) {
		}
		if (waitForProcess) {
			long finishTime = de.pxlab.pxl.HiresClock.getTimeNanos();
			mediaEventListener.mediaActionPerformed(new MediaEvent(this,
					ResponseCodes.CLOSE_MEDIA, finishTime - startTime,
					finishTime));
		}
	}

	public int getExitValue() {
		return exitValue;
	}
}
