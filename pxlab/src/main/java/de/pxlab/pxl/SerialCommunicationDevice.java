package de.pxlab.pxl;

import javax.comm.*;

/**
 * A device for communicating across a serial line. This is a simple copy of the
 * software that comes with the Java cummunication API docs.
 * 
 * @version 0.1.1
 * @author H. Irtel
 */
/*
 * 
 * 03/04/03
 * 
 * 2005/06/21 use nanosecond event time
 */
public class SerialCommunicationDevice implements SerialPortReceiver {
	private SerialConnection connection;
	private SerialCommunicationListener listener;

	public SerialCommunicationDevice(String dev,
			SerialCommunicationListener dest) {
		connection = new SerialConnection(new SerialParameters(dev, 2400,
				SerialPort.FLOWCONTROL_NONE, SerialPort.FLOWCONTROL_NONE,
				SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
				SerialPort.PARITY_NONE), this);
		try {
			connection.openConnection();
		} catch (SerialConnectionException sx) {
		}
		listener = dest;
		// System.out.println("SerialCommunicationDevice() ready.");
	}

	public boolean isOpen() {
		return connection.isOpen();
	}

	public void close() {
		// System.out.println("SerialCommunicationDevice.close().");
		connection.closeConnection();
	}

	public void send(String s) {
		connection.send(s);
	}

	public void send(char s) {
		connection.send(s);
	}
	StringBuffer inBuf = new StringBuffer(100);

	/* Format = 'x = 0.418E0 y = 0.375E0 LUM = 18.32E0 cd/m^2' */
	/** Implements the SerialPortReceiver. */
	public void receive(String s) {
		// System.out.println("SerialCommunicationDevice.receive(): " + s);
		inBuf.append(s);
		if (s.charAt(s.length() - 1) == (char) 10) {
			// System.out.print("Input line found: ");
			// System.out.print("SerialCommunicationDevice.receive(): Sending to listener: "
			// + inBuf );
			listener.serialLineInput(new SerialLineInputEvent(this, HiresClock
					.getTimeNanos(), inBuf.toString()));
			inBuf = new StringBuffer(100);
			// tekLuma.send("!NEW\n");
		}
	}
}
