package de.pxlab.pxl;

import java.awt.AWTEvent;

/**
 * This class is only used to manage Event id numbers. PXL internal events have
 * to extend this class instead of AWTEvent in order to get non-overlapping
 * event id numbers.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 06/21/00
 */
public class PxlEvent extends AWTEvent {
	public static final int COLOR_CHANGE_EVENT_FIRST = RESERVED_ID_MAX + 1;
	public static final int SPACE_MOUSE_EVENT_FIRST = COLOR_CHANGE_EVENT_FIRST + 10;
	public static final int NEXT_EVENT_FIRST = SPACE_MOUSE_EVENT_FIRST + 10;

	protected PxlEvent(Object s, int i) {
		super(s, i);
	}
}
