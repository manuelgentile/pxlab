package de.pxlab.pxl;

/**
 * Emulates a device which can be used for parallel port I/O.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 03/11/03
 */
class PortIOEmulator implements PortIODevice {
	public PortIOEmulator() {
		super();
	}

	/** Open the port. */
	public int open() {
		return 0;
	}

	/** Close the port. */
	public void close() {
	}

	/**
	 * Send the given data to the respective port.
	 * 
	 * @param portOffset
	 *            offset of the respective port with respect to the I/O-device's
	 *            base.
	 * @param x
	 *            data value to send to the port.
	 */
	public void out(int portOffset, int x) {
	}

	/**
	 * Send the given data to the given analog output channel.
	 * 
	 * @param channel
	 *            offset of the respective analog output channel with respect to
	 *            the I/O-device's base address.
	 * @param x
	 *            data value to send to the analog output channel.
	 */
	public void analogOut(int channel, int x) {
	}

	/**
	 * Get data from an I/O port.
	 * 
	 * @param portOffset
	 *            offset of the respective port with respect to the I/O-device's
	 *            base.
	 * @return the data read from the port.
	 */
	public int in(int portOffset) {
		return 0;
	}
}
