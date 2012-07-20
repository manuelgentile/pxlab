package de.pxlab.pxl;

import java.awt.AWTEvent;
import de.pxlab.pxl.device.JNIsiapp;

/**
 * Events genarated by a SpaceMouse.
 * 
 * <p>
 * This code is mainly copied from Jim Wick's (3Dconnexion, March/April, 2003)
 * sample code for a Java interface to the 3DConnexion SpaceMouse driver.
 * 
 * @version 0.1.0
 */
public class SpaceMouseEvent extends PxlEvent {
	public static final int SPACE_MOUSE_BUTTON_EVENT = SPACE_MOUSE_EVENT_FIRST
			+ JNIsiapp.SiEventType.SI_BUTTON_EVENT;
	public static final int SPACE_MOUSE_MOTION_EVENT = SPACE_MOUSE_EVENT_FIRST
			+ JNIsiapp.SiEventType.SI_MOTION_EVENT;
	public static final int SPACE_MOUSE_ZERO_EVENT = SPACE_MOUSE_EVENT_FIRST
			+ JNIsiapp.SiEventType.SI_ZERO_EVENT;
	public static final int TR_X = JNIsiapp.SI_TX;
	public static final int TR_Y = JNIsiapp.SI_TY;
	public static final int TR_Z = JNIsiapp.SI_TZ;
	public static final int ROT_X = JNIsiapp.SI_RX;
	public static final int ROT_Y = JNIsiapp.SI_RY;
	public static final int ROT_Z = JNIsiapp.SI_RZ;
	public static double axisLimit = 81.0;
	private int id;
	private double[] motionData;
	private int lastButton, currentButton, buttonReleased, buttonPressed;
	private long when;
	private int period;
	private static int[] bitMask;
	static {
		bitMask = new int[32];
		for (int i = 0; i < 32; i++)
			bitMask[i] = (1 << i);
	}

	public SpaceMouseEvent(Object s, JNIsiapp.SiSpwEvent event) {
		super(s, SPACE_MOUSE_EVENT_FIRST);
		when = System.nanoTime();
		period = event.period;
		if (event.type == JNIsiapp.SiEventType.SI_MOTION_EVENT) {
			id = SPACE_MOUSE_MOTION_EVENT;
			motionData = new double[6];
			for (int i = 0; i < 6; i++)
				motionData[i] = (double) event.mData[i] / axisLimit;
			// System.arraycopy(event.mData, 0, motionData, 0, 6);
		} else if (event.type == JNIsiapp.SiEventType.SI_BUTTON_EVENT) {
			id = SPACE_MOUSE_BUTTON_EVENT;
			lastButton = event.bData.last;
			currentButton = event.bData.current;
			buttonPressed = event.bData.pressed;
			buttonReleased = event.bData.released;
		} else if (event.type == JNIsiapp.SiEventType.SI_ZERO_EVENT) {
			id = SPACE_MOUSE_ZERO_EVENT;
		}
	}

	public static void setAxisLimit(double d) {
		axisLimit = d;
	}

	public double[] getAxisDeltas() {
		return motionData;
	}

	public int getPeriod() {
		return period;
	}

	public int getID() {
		return id;
	}

	public long getWhen() {
		return when;
	}

	public boolean isButtonPressed() {
		return buttonPressed != 0;
	}

	public boolean isButtonPressed(int b) {
		return (b >= 1) && (b <= 32) && ((buttonPressed & bitMask[b - 1]) != 0);
	}

	public int getButtonPressed() {
		for (int i = 0; i < 32; i++)
			if ((bitMask[i] & buttonPressed) != 0)
				return i + 1;
		return 0;
	}

	public int getButtonReleased() {
		for (int i = 0; i < 32; i++)
			if ((bitMask[i] & buttonReleased) != 0)
				return i + 1;
		return 0;
	}

	public String toString() {
		String r = null;
		if (id == SPACE_MOUSE_MOTION_EVENT) {
			r = "Motion Event: [" + "TX=" + motionData[0] + ", " + "TY="
					+ motionData[1] + ", " + "TZ=" + motionData[2] + ", "
					+ "RX=" + motionData[3] + ", " + "RY=" + motionData[4]
					+ ", " + "RZ=" + motionData[5] + "]";
		} else if (id == SPACE_MOUSE_BUTTON_EVENT) {
			r = "Button Event: \n" + "	   last: " + lastButton + "\n"
					+ "	current: " + currentButton + "\n" + "	pressed: "
					+ buttonPressed + "\n" + "    released: " + buttonReleased;
		} else if (id == SPACE_MOUSE_ZERO_EVENT) {
			r = "Zero Event";
		}
		return r;
	}
}
