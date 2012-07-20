package de.pxlab.pxl;

import javax.comm.*;
import java.util.*;

/**
 * Handles events initiated by input lines of a serial communication port. These
 * are the lines DCD, CTS, DSR, and RI of the RS 232 interface.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see ExternalControlBox
 */
public class SerialPortEventHandler implements SerialPortEventListener {
	private ArrayList externalButtonListeners = new ArrayList();

	/** Register a listener for external button events. */
	public void addExternalButtonListener(ExternalButtonListener xb) {
		externalButtonListeners.add(xb);
	}

	/**
	 * Implements the serial port event listener interface. This method decodes
	 * and propagates the serial port event to the registered external button
	 * listener.
	 */
	public void serialEvent(SerialPortEvent spe) {
		// System.out.println("Serial port event detected.");
		long when = System.currentTimeMillis();
		int code = 0;
		switch (spe.getEventType()) {
		case SerialPortEvent.BI:
			break;
		case SerialPortEvent.CD:
			code = ResponseCodes.SERIAL_PORT_BUTTON1;
			break;
		case SerialPortEvent.CTS:
			code = ResponseCodes.SERIAL_PORT_BUTTON3;
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			break;
		case SerialPortEvent.DSR:
			code = ResponseCodes.SERIAL_PORT_BUTTON2;
			break;
		case SerialPortEvent.FE:
			break;
		case SerialPortEvent.OE:
			break;
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.PE:
			break;
		case SerialPortEvent.RI:
			code = ResponseCodes.SERIAL_PORT_BUTTON4;
			break;
		}
		if (!spe.getOldValue() && spe.getNewValue()) {
			for (int i = 0; i < externalButtonListeners.size(); i++) {
				((ExternalButtonListener) externalButtonListeners.get(i))
						.externalButtonPressed(new ExternalButtonEvent(spe
								.getSource(), when, code));
			}
		} else {
			for (int i = 0; i < externalButtonListeners.size(); i++) {
				((ExternalButtonListener) externalButtonListeners.get(i))
						.externalButtonReleased(new ExternalButtonEvent(spe
								.getSource(), when, code));
			}
		}
	}
}
