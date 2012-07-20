package de.pxlab.pxl;

/**
 * An external device whose data have to be read by polling. It generates button
 * states, directional statess, and axis change values.
 * 
 * @version 0.1.0
 */
public abstract class PolledDevice {
	protected int id;
	protected int delayDuration = 0;

	/**
	 * Get the name of the device.
	 * 
	 * @return the name of the device.
	 */
	public abstract String getName();

	/**
	 * Multiple devices of the same type will differ by an ID value.
	 */
	public int getID() {
		return id;
	}

	/** Set the delay between successive polling cycles. */
	public void setPollingDelay(int d) {
		delayDuration = d;
	}

	/** Execute the polling delay. */
	protected void delay() {
		if (delayDuration > 0)
			HiresClock.delay(delayDuration);
	}

	/**
	 * Get the current state data of the device.
	 * 
	 * @return a data structure containing the current device state.
	 */
	public abstract PolledDeviceData read();

	/** Get the number of axes contained in the device. */
	public abstract int getNumberOfAxes();

	/** Get the number of buttons contained in the device. */
	public abstract int getNumberOfButtons();

	/** Get the number of available directional features. */
	public abstract int getNumberOfDirectionals();

	/**
	 * Open the device.
	 * 
	 * @return true if opening is successfull and false if not.
	 */
	public abstract boolean open();

	/** Close the device. No further input will be requested. */
	public abstract void close();

	/** Reset the device to an initial state. */
	public abstract void reset();
}
