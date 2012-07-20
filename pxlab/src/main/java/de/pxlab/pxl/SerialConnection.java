package de.pxlab.pxl;

import javax.comm.*;
import java.io.*;
import java.awt.TextArea;
import java.awt.event.*;
import java.util.TooManyListenersException;

/**
 * A class that handles the details of a serial connection. Copied from the Java
 * communication API docs.
 */
public class SerialConnection implements SerialPortEventListener {
	private SerialPortReceiver receiver;
	private SerialParameters parameters;
	private OutputStream os;
	private InputStream is;
	private CommPortIdentifier portId;
	private SerialPort sPort;
	private boolean open;

	/**
	 * Creates a SerialConnection object and initilizes variables passed in as
	 * params.
	 * 
	 * @param parameters
	 *            A SerialParameters object.
	 * @param receiver
	 *            the destination of strings received by this port.
	 */
	public SerialConnection(SerialParameters parameters,
			SerialPortReceiver receiver) {
		this.parameters = parameters;
		this.receiver = receiver;
		open = false;
	}

	/**
	 * Attempts to open a serial connection and streams using the parameters in
	 * the SerialParameters object. If it is unsuccesfull at any step it returns
	 * the port to a closed state, throws a
	 * <code>SerialConnectionException</code>, and returns.
	 * 
	 * Gives a timeout of 30 seconds on the portOpen to allow other applications
	 * to reliquish the port if have it open and no longer need it.
	 */
	public void openConnection() throws SerialConnectionException {
		// Obtain a CommPortIdentifier object for the port you want to open.
		try {
			portId = CommPortIdentifier.getPortIdentifier(parameters
					.getPortName());
		} catch (NoSuchPortException e) {
			throw new SerialConnectionException(e.getMessage());
		}
		// System.out.println("Port Identifier " + portId.getName());
		// Open the port represented by the CommPortIdentifier object. Give
		// the open call a relatively long timeout of 30 seconds to allow
		// a different application to reliquish the port if the user
		// wants to.
		try {
			sPort = (SerialPort) portId.open("SerialConnection", 30000);
		} catch (PortInUseException e) {
			throw new SerialConnectionException(e.getMessage());
		}
		// System.out.println("SerialConnection.openConnection() Port " +
		// portId.getName() + " open.");
		// Set the parameters of the connection. If they won't set, close the
		// port before throwing an exception.
		try {
			setConnectionParameters();
		} catch (SerialConnectionException e) {
			sPort.close();
			throw e;
		}
		// System.out.println("Connection parameters set.");
		// Open the input and output streams for the connection. If they won't
		// open, close the port before throwing an exception.
		try {
			os = sPort.getOutputStream();
			is = sPort.getInputStream();
		} catch (IOException e) {
			sPort.close();
			throw new SerialConnectionException("Error opening i/o streams");
		}
		// Add this object as an event listener for the serial port.
		try {
			sPort.addEventListener(this);
		} catch (TooManyListenersException e) {
			sPort.close();
			throw new SerialConnectionException("too many listeners added");
		}
		// Set notifyOnDataAvailable to true to allow event driven input.
		sPort.notifyOnDataAvailable(true);
		// Set notifyOnBreakInterrup to allow event driven break handling.
		sPort.notifyOnBreakInterrupt(true);
		// Set receive timeout to allow breaking out of polling loop during
		// input handling.
		try {
			sPort.enableReceiveTimeout(30);
		} catch (UnsupportedCommOperationException e) {
		}
		open = true;
	}

	/**
	 * Sets the connection parameters to the setting in the parameters object.
	 * If set fails return the parameters object to origional settings and throw
	 * exception.
	 */
	public void setConnectionParameters() throws SerialConnectionException {
		// Save state of parameters before trying a set.
		int oldBaudRate = sPort.getBaudRate();
		int oldDatabits = sPort.getDataBits();
		int oldStopbits = sPort.getStopBits();
		int oldParity = sPort.getParity();
		int oldFlowControl = sPort.getFlowControlMode();
		// Set connection parameters, if set fails return parameters object
		// to original state.
		try {
			sPort.setSerialPortParams(parameters.getBaudRate(),
					parameters.getDatabits(), parameters.getStopbits(),
					parameters.getParity());
		} catch (UnsupportedCommOperationException e) {
			parameters.setBaudRate(oldBaudRate);
			parameters.setDatabits(oldDatabits);
			parameters.setStopbits(oldStopbits);
			parameters.setParity(oldParity);
			throw new SerialConnectionException("Unsupported parameter");
		}
		// Set flow control.
		try {
			sPort.setFlowControlMode(parameters.getFlowControlIn()
					| parameters.getFlowControlOut());
		} catch (UnsupportedCommOperationException e) {
			throw new SerialConnectionException("Unsupported flow control");
		}
	}

	/**
	 * Close the port and clean up associated elements.
	 */
	public void closeConnection() {
		// System.out.println("SerialConnection.closeConnection().");
		// If port is alread closed just return.
		if (!open) {
			return;
		}
		// Check to make sure sPort has reference to avoid a NPE.
		if (sPort != null) {
			try {
				// close the i/o streams.
				os.close();
				is.close();
			} catch (IOException e) {
				System.err.println(e);
			}
			// Close the port.
			sPort.close();
		}
		open = false;
	}

	/**
	 * Send a one second break signal.
	 */
	public void sendBreak() {
		sPort.sendBreak(1000);
	}

	/**
	 * Reports the open status of the port.
	 * 
	 * @return true if port is open, false if port is closed.
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * Handles SerialPortEvents. The two types of SerialPortEvents that this
	 * program is registered to listen for are DATA_AVAILABLE and BI. During
	 * DATA_AVAILABLE the port buffer is read until it is drained, when no more
	 * data is availble and 30ms has passed the method returns. When a BI event
	 * occurs the words BREAK RECEIVED are written to the messageAreaIn.
	 */
	public void serialEvent(SerialPortEvent e) {
		// Create a StringBuffer and int to receive input data.
		StringBuffer inputBuffer = new StringBuffer(100);
		int newData = 0;
		// Determine type of event.
		switch (e.getEventType()) {
		// Read data until -1 is returned. If \r is received substitute
		// \n for correct newline handling.
		case SerialPortEvent.DATA_AVAILABLE:
			while (newData != -1) {
				try {
					newData = is.read();
					if (newData == -1) {
						break;
					}
					inputBuffer.append((char) newData);
				} catch (IOException ex) {
					System.err.println(ex);
					return;
				}
			}
			// Send received data to receiver.
			receiver.receive(new String(inputBuffer));
			// System.out.println("SerialConnection.serialEvent() Received: " +
			// inputBuffer);
			break;
		// If break event append BREAK RECEIVED message.
		case SerialPortEvent.BI:
			receiver.receive("\n--- BREAK RECEIVED ---\n");
		}
	}

	public void send(String s) {
		// System.out.println("SerialConnection.send(): " + s);
		char[] buf = s.toCharArray();
		for (int i = 0; i < buf.length; i++) {
			send(buf[i]);
		}
	}

	public void send(char s) {
		try {
			os.write((int) s);
		} catch (IOException e) {
			System.err.println("OutputStream write error: " + e);
		}
	}
}
