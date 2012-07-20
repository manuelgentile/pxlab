package de.pxlab.pxl.gui;

import java.awt.*;

/**
 * An AWT component object which contains nothing but the PXLab logo. This
 * component may be used to add the PXLab logo to a container.
 * 
 * @author H. Irtel
 * @see PXLabStartupFrame
 * @version 0.1.0
 */
public class PXLabLogoComponent extends Canvas {
	/**
	 * Create a PXLab logo component object with the given size.
	 * 
	 * @param size
	 *            is the horizontal and vertical size of the logo.
	 */
	public PXLabLogoComponent(int size) {
		setBackground(new Color(255, 240, 60));
		setSize(size, size);
	}

	/**
	 * Overrides the component's paint method in order to fit the logo to the
	 * given component size.
	 */
	public void paint(Graphics g) {
		Dimension size = getSize();
		int w = size.width;
		int h = size.height;
		int w1 = 80 * w / 100;
		int h1 = 80 * h / 100;
		int w2 = 60 * w1 / 80;
		int h2 = 60 * h1 / 80;
		int w3 = 40 * w2 / 60;
		int h3 = 40 * h2 / 60;
		g.setColor(new Color(254, 223, 0));
		g.fillRect((w - w1) / 2, 3 * (h - h1) / 4, w1, h1);
		g.setColor(new Color(255, 203, 44));
		g.fillRect((w - w2) / 2, 3 * (h - h2) / 4, w2, h2);
		g.setColor(new Color(253, 184, 16));
		g.fillRect((w - w3) / 2, 3 * (h - h3) / 4, w3, h3);
	}
}
