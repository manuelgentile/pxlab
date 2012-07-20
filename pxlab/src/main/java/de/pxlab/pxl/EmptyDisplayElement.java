package de.pxlab.pxl;

import java.awt.Rectangle;

/**
 * This is a display element which does not change the display when painted.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 04/05/00
 */
public class EmptyDisplayElement extends DisplayElement {
	public EmptyDisplayElement() {
		type = DisplayElement.EMPTY;
	}

	public void show() {
		if (validBounds) {
			setBounds(new Rectangle());
		}
	}
}
