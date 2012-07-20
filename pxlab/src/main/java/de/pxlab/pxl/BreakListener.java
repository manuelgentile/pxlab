package de.pxlab.pxl;

import java.awt.*;
import java.awt.event.*;

/**
 * Handles keyboard break events which signal that the currently running
 * experiment should be stopped. These signals originate from keyboard response
 * dispatchers which watch a subject's keyboard responses. The signals are sent
 * to owners of display devices.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see DisplayDevice
 * @see KeyboardResponseDispatcher
 */
/*
 * 
 * 2004/10/01
 */
public interface BreakListener {
	/**
	 * The user/subject has pressed a key combination which should stop the
	 * currently running experiment.
	 */
	public void breakPerformed(KeyEvent e);
}
