package de.pxlab.pxl;

/**
 * A native Windows interface to the wasco PCI boards for parallel I/O and
 * Digital-to-Analog conversion. This code will only be used if we are running
 * as an application, the respective DLLs are present and the respective board
 * hardware is present. Otherwise this code will be emulated.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 03/11/03
 */
public class WascoPortIO implements PortIODevice {
	/** These definitions are for 16-bit access. */
	/** Port offset for Digital-to-analog converter 1 */
	public static final int DAOUT1 = 0x00;
	public static final int DAOUT2 = 0x02;
	public static final int DAOUT3 = 0x04;
	public static final int DAOUT4 = 0x06;
	public static final int DAOUT5 = 0x08;
	public static final int DAOUT6 = 0x0a;
	public static final int DAOUT7 = 0x0c;
	public static final int DAOUT8 = 0x0e;
	public static final int TTL_IN = 0x40;
	public static final int TTL_OUT = 0x60;
	public static final int TIMER_0 = 0x80;
	public static final int TIMER_1 = 0x82;
	public static final int TIMER_2 = 0x84;
	public static final int TIMER_CTRL = 0x86;
	public static final int INT_CTRL = 0xa0;
	public static final int TIMER_INT_RESET = 0xbe;
	private String boardName;

	public WascoPortIO(String boardName) {
		this.boardName = boardName;
		System.loadLibrary("wasco");
		System.loadLibrary("pxlabw");
	}

	protected void finalize() {
		closePort();
	}

	/**
	 * Open the port. This is only then successful if the respective device
	 * exists.
	 */
	public int open() {
		return openPort(boardName);
	}

	/** Close the port. */
	public void close() {
		closePort();
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
		outPort(portOffset, x);
	}

	/**
	 * Send the given data to the given analog output channel.
	 * 
	 * @param channel
	 *            number of the analog output channel. Channel numbers start
	 *            with 1, ...
	 * @param x
	 *            data value to send to the analog output channel.
	 */
	public void analogOut(int channel, int x) {
		analogOutPort(channel, x);
	}

	public int in(int portOffset) {
		return inPort(portOffset);
	}

	public native void outPort(int portOffset, int x);

	public native void analogOutPort(int channel, int x);

	public native int inPort(int portOffset);

	private native int openPort(String n);

	private native void closePort();
}
