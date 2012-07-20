package de.pxlab.pxl;

/**
 * Handles devices which can be used for parallel port I/O. Currently no
 * interrupts are supported.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 03/11/03 developed in order to support the wasco PCI-Interface boards by
 * Windows native methods and emulate an I/O port if no wasco board is
 * available..
 */
public class PortIO {
	private PortIODevice pio;
	private String deviceName;
	private String wascoBoardName = "IODA-PCI12K8/extended";

	/** Create a new port control object for the named I/O device. */
	public PortIO(String deviceName) {
		this.deviceName = deviceName;
	}

	/**
	 * Open the device's port. Real ports are only used if we run as an
	 * application. Otherwise we always use emulation mode. If both the wasco
	 * DLL and the board hardware exist we open the wasco PCI board.
	 */
	public int open() {
		pio = (PortIODevice) new PortIOEmulator();
		PortIODevice wpio = null;
		if (Base.isApplication()) {
			if (deviceName != null) {
				if (deviceName.equals(wascoBoardName)) {
					try {
						wpio = new WascoPortIO(deviceName);
					} catch (SecurityException sex) {
						Syslog.out
								.println("PortIO.open(): Can't access wasco.dll or pxlabw.dll - emulate port I/O only.");
					} catch (UnsatisfiedLinkError ule) {
						Syslog.out
								.println("PortIO.open(): Can't find wasco.dll or pxlabw.dll - emulate port I/O only.");
					}
					if ((wpio != null) && (wpio.open() == 0)) {
						pio = wpio;
					} else {
						Syslog.out
								.println("PortIO.open(): Can't open wasco I/O-device "
										+ deviceName + ". Use emulation.");
					}
				} else {
					Syslog.out
							.println("PortIO.open(): Don't know how to handle I/O-device "
									+ deviceName + ". Use emulation.");
				}
			} else {
				Syslog.out
						.println("PortIO.open(): Missing I/O-device name. Use emulation.");
			}
		} else {
			Syslog.out
					.println("PortIO.open(): We are running as an applet: Emulate I/O-Board only.");
		}
		return 0;
	}

	/** Close the port. */
	public void close() {
		pio.close();
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
		pio.out(portOffset, x);
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
		pio.analogOut(channel, x);
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
		return pio.in(portOffset);
	}
}
