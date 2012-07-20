package de.pxlab.pxl;

/** A SpaceMouse is a 3D
 navigation device which offers 6 degrees of freedom in space. See
 <a href="http://www.3dconnexion.com">3DConnexion</a> for more
 information on SpaceMouse devices.

 <p>A SpaceMouse has 6 axes and a certain number of buttons depending on type. These are the axes:
 <ol>
 <li> Translation along X: left/right translation by pressing the knob left or right.
 <li> Translation along Y: up/down translation by pulling the knob up or pushing it down.
 <li> Translation along Z: forward/backbard translation by pushing the knob forward or backward.
 <li> Rotation around X: rotation around the X-axis which runs from left to right.
 <li> Rotation around Y: rotation around the Y-axis which runs vertical.
 <li> Rotation around Z: rotation around the Z-axis which runs straight ahead.
 </ol>

 <p>Using the SpaceMouse requires that the SpaceMouse driver coming
 with the device is installed and the driver interface DLL
 (JNIsiapp.dll) being available to PXLab. */
import java.awt.Component;

public class SpaceMouse {
	private SpaceMouseThread spaceMouseThread;
	private Component component;
	private SpaceMouseListener spaceMouseListener;
	protected int delayDuration = 0;

	/**
	 * Creates a new instance of a SpaceMouse.
	 * 
	 * @param com
	 *            the Component which receives the space mouse events.
	 * @param sml
	 *            the listener for SpaceMouseEvents.
	 */
	public SpaceMouse(Component com, SpaceMouseListener sml) {
		component = com;
		spaceMouseListener = sml;
	}

	/**
	 * Open the device.
	 * 
	 * @return true if opening is successfull and false if not.
	 */
	public boolean open() {
		// Attach to 3DxWare driver
		spaceMouseThread = new SpaceMouseThread(component, spaceMouseListener);
		spaceMouseThread.start();
		return true;
	}

	/** Close the device. No further input will be requested. */
	public void close() {
		spaceMouseThread.stopRunning();
	}

	public void reset() {
	}

	public void setDelay(int d) {
		delayDuration = d;
	}

	/** Devices may have a name. */
	public String getName() {
		return "SpaceMouse";
	}

	/** Actual number of available buttons. */
	public int getNumberOfButtons() {
		return 2;
	}

	/** Actual number of available axes. */
	public int getNumberOfAxes() {
		return 6;
	}

	public String toString() {
		StringBuffer b = new StringBuffer(200);
		b.append(getName());
		b.append("\n  Axes: " + getNumberOfAxes());
		/*
		 * for (int i = 0; i < diAxis.length; i++) b.append("\n    " +
		 * diAxis[i].getName() + " = " + diAxis[i].getValue());
		 */
		b.append("\n  Buttons: " + getNumberOfButtons());
		/*
		 * for (int i = 0; i < diButton.length; i++) b.append("\n    " +
		 * diButton[i].getName() + " = " + (diButton[i].getState()? "|": "o"));
		 */
		return b.toString();
	}
}
