package de.pxlab.pxl;

import java.awt.Component;

import de.pxlab.pxl.device.JNIsiapp;

/**
 * A thread waiting for SpaceMouseEvents in order to send them to the registered
 * SpaceMouseListener.
 * 
 * <p>
 * This code is mainly copied from Jim Wick's (3Dconnexion, March/April, 2003)
 * sample code for a Java interface to the 3DConnexion space mouse devices.
 * 
 * @version 0.1.0
 */
public class SpaceMouseThread extends Thread {
	private JNIsiapp siapp;
	private SpaceMouseListener listener = null;
	private Component component;
	private boolean running;

	/**
	 * Create a new thread which connects to the space mouse and receives the
	 * original event messages from the space mouse driver via the Windows
	 * message loop.
	 * 
	 * @param com
	 *            the component which is used as an internal receiver of the
	 *            space mouse events. This component must be displayable. This
	 *            can be achived by calling the component's addNotify() method
	 *            before connecting to the space mouse.
	 * @param lst
	 *            the listener which wants to receive the space mouse events.
	 */
	SpaceMouseThread(Component com, SpaceMouseListener lst) {
		siapp = new JNIsiapp();
		component = com;
		listener = lst;
		setPriority(getPriority() - 1);
	}

	protected void finalize() throws Throwable {
		System.out.println("SpaceMouseThread: finalize");
		// siapp.SiClose();
		// siapp.SiTerminate();
	}

	public void stopRunning() {
		running = false;
	}

	public boolean getRunning() {
		return running;
	}

	public void run() {
		int r;
		Debug.show(Debug.EVENTS, "SpaceMouseThread.run()");
		r = siapp.SiInitialize();
		if (r == JNIsiapp.SpwRetVal.SPW_NO_ERROR) {
			if (!component.isDisplayable())
				component.addNotify();
			r = siapp.SiOpenWinInit(component, "eventCallback");
			if (r == JNIsiapp.SpwRetVal.SPW_NO_ERROR) {
				// Debug.show(Debug.EVENTS,
				// "SpaceMouseThread.run(): SpaceMouse Windows data initialized.");
				r = siapp.SiOpen();
				if (true) {
					running = true;
					// Debug.show(Debug.EVENTS,
					// "SpaceMouseThread.run(): SpaceMouse thread is running.");
					try {
						for (; running;) {
							siapp.SiWaitForEvent();
							if (siapp.SiIsNewDataAvail()) {
								// Debug.show(Debug.EVENTS,
								// "SpaceMouseThread.run(): SpaceMouse data available");
								siapp.SiGetEvent();
								SpaceMouseEvent e = new SpaceMouseEvent(this,
										siapp.event);
								if (siapp.event.type == JNIsiapp.SiEventType.SI_MOTION_EVENT) {
									listener.spaceMouseAxis(e);
								} else if (siapp.event.type == JNIsiapp.SiEventType.SI_BUTTON_EVENT) {
									listener.spaceMouseButton(e);
								} else if (siapp.event.type == JNIsiapp.SiEventType.SI_ZERO_EVENT) {
									listener.spaceMouseZero(e);
								}
							}
						}
					} catch (Exception e) {
						System.out.println("Exception in JNIsiappThread: " + e);
					} finally {
						System.out
								.println("JNIsiappThread:run: In finally clause");
						siapp.SiClose();
						siapp.SiTerminate();
					}
				} else {
					System.out
							.println("SpaceMouseThread.run() failed to open connection to the Space Mouse driver!");
				}
			} else if (r == JNIsiapp.SpwRetVal.SI_BAD_VALUE) {
				System.out
						.println("SpaceMouseThread.run() failed to initialize Windows specific data.\nBad window caption or bad callback name!");
			} else {
				System.out
						.println("SpaceMouseThread.run() failed to initialize Windows specific data.\nCan't find window caption in the current process!");
			}
		} else {
			System.out
					.println("SpaceMouseThread.run() failed to initialize library!");
		}
	}
}
