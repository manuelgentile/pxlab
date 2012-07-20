package de.pxlab.gui;

import java.awt.Component;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Double buffering support for components. Her is an example how this class can
 * be used withoud making any change to an existing paint() method.
 * 
 * <pre>
 * private BackBuffer backBuffer;
 * 
 * public void update(Graphics g) {
 * 	if (backBuffer == null) {
 * 		backBuffer = new BackBuffer(this);
 * 	}
 * 	Graphics b = backBuffer.getGraphics();
 * 	super.update(b);
 * 	paint(b);
 * 	backBuffer.blit(g);
 * }
 * </pre>
 * 
 * @author H. Irtel
 * @version 0.1.0, 06/12/00
 * @see Chart
 * @see Slider
 */
public class BackBuffer extends ComponentAdapter {
	Image image;
	Component component;
	Dimension size;

	/**
	 * Create a back buffer for double buffered drawing to the given component.
	 */
	public BackBuffer(Component c) {
		setComponent(c);
	}

	/**
	 * Create a Graphics object for drawing into the back buffer. Note that this
	 * Graphics object MUST be disposed after use!
	 */
	public Graphics getGraphics() {
		if (image == null) {
			createMemoryBuffer();
		}
		return (image.getGraphics());
	}

	/** Blit the back buffer to the given Graphics context. */
	public void blit(Graphics g) {
		g.drawImage(image, 0, 0, component);
	}

	public void componentResized(ComponentEvent e) {
		Dimension newSize = component.getSize();
		if ((image == null) || (newSize.width > size.width)
				|| (newSize.height > size.height)) {
			createMemoryBuffer();
		}
	}

	private void createMemoryBuffer() {
		size = component.getSize();
		image = component.createImage(size.width, size.height);
	}

	private void setComponent(Component c) {
		if (component != null) {
			component.removeComponentListener(this);
		}
		component = c;
		component.addComponentListener(this);
	}
}
