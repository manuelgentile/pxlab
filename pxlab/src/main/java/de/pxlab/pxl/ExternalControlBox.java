package de.pxlab.pxl;

import java.util.*;
import javax.comm.*;

/**
 * A control object for external devices which have response buttons and can
 * control simple signals like LEDs or trigger pulses.
 * 
 * <p>
 * An <em>external response button</em> is a button device where each button can
 * be pressed and released like a mouse button.
 * 
 * <p>
 * An <em>external signal</em> is a signal which can be switched on and off like
 * an LED, keeping its state in between.
 * 
 * <p>
 * An <em>external trigger</em> creates a single pulse whenever the trigger is
 * activated. The pulse may be very short.
 * 
 * <p>
 * Currently the only device implemented is a button and signal box connected to
 * the input and output lines of an RS 232 serial communication port. These are
 * the input lines DCD, CTS, DSR, and RI of the RS 232 interface. The line DTR
 * is used by the input signal mechanism for providing the control voltage.
 * Available output signals are those connected to the lines RTS and DTR. Note
 * that DTR is used as a power source for the input buttons.
 * 
 * <p>
 * This class allows only a single control box being opened.
 * 
 * @version 0.2.1
 */
/*
 * 01/24/02
 */
public class ExternalControlBox implements ExternalSignalCodes {
	private static String portName;
	private static SerialPort serialPort = null;
	private static SerialPortEventHandler serialPortEventHandler = null;

	/**
	 * Open the external control box whose name is given as an argument.
	 * 
	 * @param dev
	 *            the name of the external device. Possible names are those
	 *            which are supported by the Java communication API: "COM1" and
	 *            "COM2" on PCs, and "Serial A" and "Serial B" on Sun Ultra
	 *            workstations. If the name is null then the first device found
	 *            is used.
	 * @return true if successful and false if not.
	 */
	public static boolean open(String dev) {
		// System.out.println("ExternalControlBox.open()");
		boolean valid = false;
		if (serialPort != null)
			close();
		if (serialPort == null) {
			String errMsg = "External port " + dev + " not found!";
			Enumeration portList = CommPortIdentifier.getPortIdentifiers();
			while (!valid && portList.hasMoreElements()) {
				CommPortIdentifier portId = (CommPortIdentifier) portList
						.nextElement();
				if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
					if ((dev == null) || portId.getName().equals(dev)) {
						// System.out.println("ExternalControlBox.open() Serial port "
						// + dev + " found.");
						try {
							serialPort = (SerialPort) portId.open(
									"ExternalButtonControl", 2000);
							serialPortEventHandler = new SerialPortEventHandler();
							try {
								serialPort
										.addEventListener(serialPortEventHandler);
								serialPort.notifyOnCarrierDetect(true);
								serialPort.notifyOnCTS(true);
								serialPort.notifyOnRingIndicator(true);
								serialPort.notifyOnDSR(true);
								serialPort.setRTS(false);
								serialPort.setDTR(true);
								portName = dev;
								valid = true;
								// System.out.println("ExternalControlBox.open() Serial port configured.");
							} catch (TooManyListenersException e) {
								errMsg = "To many listeners to external port "
										+ dev;
								// System.out.println("ExternalControlBox.open() "
								// + errMsg);
							}
						} catch (PortInUseException e) {
							errMsg = "Port " + dev + " already in use!";
							// System.out.println("ExternalControlBox.open() " +
							// errMsg);
						}
					}
				}
			}
			if (!valid) {
				new ParameterValueError(errMsg);
			}
		} else {
			System.out
					.println("ExternalControlBox.open(): Trying to open serial port which is already open!");
		}
		return valid;
	}

	/**
	 * Check whether the external control box is open. Editors can call this
	 * method in order to avoid accessing the box before it has been
	 * initialized.
	 */
	public static boolean isOpen() {
		return serialPort != null;
	}

	/**
	 * Close the external control box such that it may savely be reopened later.
	 */
	public static void close() {
		serialPort.close();
		serialPort = null;
		// System.out.println("ExternalControlBox.close() Serial port closed.");
	}

	public String toString() {
		return portName;
	}

	/**
	 * Activate the external control signal whose code is given as an argument.
	 */
	public static void setSignal(int code) {
		// System.out.println("ExternalControlBox.setSignal(): " + code);
		if (code == SIGNAL_1) {
			serialPort.setRTS(true);
		} else if (code == SIGNAL_2) {
			serialPort.setDTR(true);
		}
	}

	/**
	 * Clear the external control signal whose code is given as an argument.
	 */
	public static void clearSignal(int code) {
		// System.out.println("ExternalControlBox.clearSignal(): " + code);
		if (code == SIGNAL_1) {
			serialPort.setRTS(false);
		} else if (code == SIGNAL_2) {
			serialPort.setDTR(false);
		}
	}

	/**
	 * Get the state of the external control signal whose code is given as an
	 * argument.
	 */
	public static boolean getSignal(int code) {
		return (code == SIGNAL_1) ? serialPort.isRTS() : serialPort.isDTR();
	}

	/**
	 * Activate the external control box trigger signal. Trigger signal duration
	 * will be arbitrary small.
	 */
	public static void trigger() {
		serialPort.setRTS(true);
		// try { Thread.sleep(100); } catch (InterruptedException iex) {}
		serialPort.setRTS(false);
	}

	/** Register a listener for external button events. */
	public static void addExternalButtonListener(ExternalButtonListener xb) {
		if (serialPortEventHandler != null) {
			serialPortEventHandler.addExternalButtonListener(xb);
		}
	}
}
