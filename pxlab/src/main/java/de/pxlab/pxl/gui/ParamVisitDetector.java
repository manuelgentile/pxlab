package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.pxl.*;

/**
 * Detects when the mouse enters a component and sends the respective
 * ExParDescriptor to its listener.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2007/01/25
 */
public class ParamVisitDetector extends MouseAdapter implements
		ParamVisitListener {
	private ParamVisitListener paramVisitListener;
	private ExParDescriptor exParDescriptor;

	/**
	 * Create a detector which tells its listeners that a parameter field has
	 * been entered by the mouse.
	 * 
	 * @param exd
	 *            the ExParDescriptor of the parameter whose component we are
	 *            watching.
	 * @param pv
	 *            the listener which wants to be told about the visit.
	 */
	public ParamVisitDetector(ExParDescriptor exd, ParamVisitListener pv) {
		exParDescriptor = exd;
		paramVisitListener = (pv != null) ? pv : this;
	}

	public void mouseEntered(MouseEvent e) {
		paramVisitListener.setParam(exParDescriptor);
	}

	public void mouseExited(MouseEvent e) {
		paramVisitListener.setParam(null);
	}
	// -------------------------------------------------------
	// ParamVisitListener implementation
	// -------------------------------------------------------
	private ExParDescriptor visited;

	public void setParam(ExParDescriptor exd) {
		if (exd != null) {
			visited = exd;
			// System.out.println("ColorButtonPanel.setParam() Mouse entered " +
			// visited.getName());
		} else {
			// System.out.println("ColorButtonPanel.setParam() Mouse left " +
			// visited.getName());
		}
	}
	// -------------------------------------------------------
}
