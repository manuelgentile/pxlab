package de.pxlab.pxl;

import java.awt.event.*;

/**
 * A FocusListener for every Component which may be used for debugging purposes.
 * It echoes focus change events on the command line if the respective debugging
 * option is set.
 * 
 * @author H. Irtel
 * @version 0.1.3
 * @see Debug
 */
/*
 * 
 * 2004/11/09
 */
public class DebugFocusListener implements FocusListener {
	private String name;

	public DebugFocusListener(String n) {
		name = n;
	}

	public void focusGained(FocusEvent e) {
		if (Debug.isActive(Debug.FOCUS))
			System.out.println(name + " gained focus.");
	}

	public void focusLost(FocusEvent e) {
		if (Debug.isActive(Debug.FOCUS))
			System.out.println(name + " lost focus.");
	}
}
