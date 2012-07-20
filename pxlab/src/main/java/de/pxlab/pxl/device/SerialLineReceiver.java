package de.pxlab.pxl.device;

/** Receives input strings from a serial communication port. */
public interface SerialLineReceiver {
	public void receive(String s);
}
