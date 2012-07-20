package de.pxlab.pxl.device;

public class SerialLineMonitor {
	protected boolean validData;
	protected long timeOutLimit = 5000L;

	public SerialLineMonitor() {
		validData = false;
	}

	public synchronized void waitForData() {
		validData = false;
		try {
			wait(timeOutLimit);
		} catch (InterruptedException iex) {
		}
	}

	public synchronized void measurementFinished() {
		validData = true;
		notify();
	}

	public boolean hasValidData() {
		return validData;
	}

	public void setTimeOutLimit(int t) {
		timeOutLimit = t;
	}
}
