package de.pxlab.pxl;

import java.awt.*;

/**
 * A single line text element with a filled rectangle background. This is an
 * example of how two DisplayElement subclasses may be combined to form a new
 * DisplayElement subclass.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 07/21/02
 */
public class LabelElement extends DisplayElement implements
		PositionReferenceCodes {
	private Bar3D bar;
	private TextElement label;

	public LabelElement(ExPar t, ExPar b) {
		type = DisplayElement.TEXT;
		colorPar = t;
		label = new TextElement(t);
		label.setReferencePoint(BASE_CENTER);
		bar = new Bar3D(b);
	}

	/** Set the label text string. */
	public void setText(String t) {
		label.setText(t);
	}

	/** Get the text string. */
	public String getText() {
		return (label.getText());
	}

	public void show() {
		// System.out.println("LabelElement.show() rect = " + getLocation() +
		// "," + getSize());
		bar.setRect(getLocation(), getSize());
		bar.show();
		label.setFont("SansSerif", Font.PLAIN, size.height / 2);
		label.setLocation(location.x + size.width / 2, location.y + 11
				* size.height / 16);
		label.show();
		setBounds(bar.getBounds());
	}
}
