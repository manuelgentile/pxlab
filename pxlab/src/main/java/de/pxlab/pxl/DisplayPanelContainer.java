package de.pxlab.pxl;

import java.awt.*;

// The following sequence MUST be kept:
import java.awt.image.*;
import de.pxlab.awtx.*;

/**
 * Interface to add and remove a DisplayPanel from a Container. This interface
 * is implemented by Container objects which can hold DisplayPanel objectss.
 * Usually this are top level containers like Frame or Window which already
 * implement most of this interface's methods.
 * 
 * @author H. Irtel
 * @version 0.3.2
 * @see DisplayPanel
 */
public interface DisplayPanelContainer {
	/** Add the given DisplayPanel to this Container. */
	public void setDisplayPanel(DisplayPanel dp);

	/** Remove this Container's DisplayPanel. */
	public void removeDisplayPanel();

	/** Get this Container's DisplayPanel. */
	public DisplayPanel getDisplayPanel();

	public BufferStrategy getBufferStrategy();

	/**
	 * Ensure that the display panel container is visible on screen.
	 */
	public void open();

	/**
	 * Close the display panel container. This may also remove the container
	 * from the screen.
	 */
	public void close();

	/** Dispose of all resources of this Container. */
	public void dispose();

	/**
	 * Return a graphics context for drawing into this container.
	 * 
	 * @return a Graphics object which can be used to directly draw into this
	 *         conainer.
	 */
	public Graphics getGraphicsContext();

	/** Make sure that this Container's DisplayPanel has the focus. */
	public void setFocus();
}
