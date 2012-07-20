package de.pxlab.pxl.gui;

import de.pxlab.pxl.ExParDescriptor;

/**
 * Receives notification whenever the ParamVisitDetector detects that the mouse
 * enters or exits a parameter related component.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2007/01/25
 */
public interface ParamVisitListener {
	public void setParam(ExParDescriptor e);
}
