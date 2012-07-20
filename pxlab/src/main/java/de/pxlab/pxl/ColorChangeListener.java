package de.pxlab.pxl;

/**
 * This interface defines a listener object for color change events. These are
 * created whenever the currently active color of a color server has been
 * changed.
 * 
 * @author H. Irtel
 * @version 0.2.0
 * @see ColorServer
 * @see ColorChangeEvent
 */
/*
 * 03/08/00
 */
public interface ColorChangeListener {
	public void colorChanged(ColorChangeEvent e);
}
