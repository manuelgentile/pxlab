package de.pxlab.pxl;

import java.awt.*;
import java.awt.event.*;

/**
 * This class describes possible response events and response event groups.
 * Response events are created from Java input events by the response timer
 * class. Response event groups are created by setting some of the response
 * event parameters to nonzero values. The response event class has a method for
 * checking whether a single response event is contained in the given response
 * event group.
 * 
 * @version 0.1.6
 */
/*
 * 
 * 06/27/00
 * 
 * 01/23/02 added support for an ExternalButtonEvent responses
 * 
 * 10/01/02 use KeyCodes constants for mouse events
 * 
 * 2005/05/18 added MediaEvent generated ResponseEvent
 * 
 * 2006/06/19 no longer use 'direction' for MediaEvent objects. 'code' contain
 * all necessary information.
 * 
 * 2007/02/11 SpaceMouse and polled device events.
 */
public class ResponseEvent implements TimerBitCodes {
	private int direction;
	private int device;
	private int modifiers;
	private int code;
	private int[] activeCodes;
	private char character;
	private Point location;
	private long time;
	private Rectangle region;

	// private String text;
	public ResponseEvent() {
	}

	/*
	 * public ResponseEvent(int direction, int device, int[] activeCodes) {
	 * this.direction = direction; this.device = device; this.activeCodes =
	 * activeCodes; }
	 */
	public ResponseEvent(MouseEvent e, int dir) {
		direction = dir;
		device = MOUSE_BUTTON_TIMER_BIT;
		time = e.getWhen();
		modifiers = e.getModifiers();
		code = e.getButton();
		// System.out.println("getButton(): " + e.getButton());
		// System.out.println("our Code: " + code);
		location = e.getPoint();
	}

	public ResponseEvent(KeyEvent e, int dir) {
		direction = dir;
		device = KEY_TIMER_BIT;
		time = e.getWhen();
		modifiers = e.getModifiers();
		code = e.getKeyCode();
		character = e.getKeyChar();
	}

	// public ResponseEvent(MediaEvent e, int dir) {
	public ResponseEvent(MediaEvent e) {
		// direction = dir;
		direction = 0;
		time = e.getWhen();
		code = e.getID();
		device = (code == ResponseCodes.SYNC_MEDIA) ? SYNC_TO_MEDIA_TIMER_BIT
				: END_OF_MEDIA_TIMER_BIT;
	}

	public ResponseEvent(PolledDeviceData e) {
		int cbi = e.getChangedButtonIndex();
		int cdi = e.getChangedDirectionalIndex();
		if (cbi >= 0) {
			code = ResponseCodes.DI_BUTTON1 + ResponseCodes.DI_DEVICE_ID_SHIFT
					* ((PolledDevice) e.getSource()).getID() + cbi;
			boolean[] bs = e.getButtonStates();
			direction = bs[cbi] ? DOWN_TIMER_BIT : UP_TIMER_BIT;
		} else if (cdi >= 0) {
			code = ResponseCodes.DI_DIRECTIONAL
					+ ResponseCodes.DI_DEVICE_ID_SHIFT
					* ((PolledDevice) e.getSource()).getID()
					+ ResponseCodes.DI_DEVICE_DIRECTIONAL_SHIFT * cdi
					+ e.getDirections()[cdi];
			boolean[] ds = e.getDirectionalStates();
			direction = ds[cdi] ? UP_TIMER_BIT : DOWN_TIMER_BIT;
		}
		device = XBUTTON_TIMER_BIT;
		time = e.getWhen();
	}

	public ResponseEvent(ExternalButtonEvent e, int dir) {
		direction = dir;
		device = XBUTTON_TIMER_BIT;
		time = e.getWhen();
		code = e.getButtonCode();
	}

	public ResponseEvent(SpaceMouseEvent e, int dir) {
		direction = dir;
		device = XBUTTON_TIMER_BIT;
		time = e.getWhen();
		code = ResponseCodes.SPACE_MOUSE_BUTTON1
				+ ((dir == DOWN_TIMER_BIT) ? e.getButtonPressed() : e
						.getButtonReleased());
	}

	/*
	 * Actually not used by RealTimeDisplayPanel public
	 * ResponseEvent(SerialLineInputEvent e) { device = SERIAL_LINE_TIMER_BIT;
	 * time = e.getWhen(); text = e.getText(); }
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getDirection() {
		return (direction);
	}

	public void setDevice(int device) {
		this.device = device;
	}

	public int getDevice() {
		return (device);
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return (code);
	}

	public void setModifiers(int modifiers) {
		this.modifiers = modifiers;
	}

	public int getModifiers() {
		return (modifiers);
	}

	public void setRegion(Rectangle region) {
		this.region = region;
	}

	public Rectangle getRegion() {
		return (region);
	}

	public void setCharacter(char character) {
		this.character = character;
	}

	public int getCharacter() {
		return (character);
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public Point getLocation() {
		return (location);
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getTime() {
		return (time);
	}

	/*
	 * public String getText() { return text; }
	 */
	/*
	 * private int buttonCode(int m) { int c = 0; if ((m &
	 * InputEvent.BUTTON1_MASK) != 0) c = KeyCodes.LEFT_BUTTON; else if ((m &
	 * InputEvent.BUTTON2_MASK) != 0) c = KeyCodes.RIGHT_BUTTON; else if ((m &
	 * InputEvent.BUTTON3_MASK) != 0) c = KeyCodes.MIDDLE_BUTTON;
	 * System.out.println("modifier " + m + " is buttonCode " + c); return(c); }
	 */
	public String toString() {
		StringBuffer b = new StringBuffer("ResponseEvent[");
		b.append("dir=" + direction + ", ");
		b.append("dev=" + device + ", ");
		b.append("mod=" + modifiers + ", ");
		b.append("cod=" + code + ", ");
		b.append("time=" + time + "]");
		return (b.toString());
	}
}
