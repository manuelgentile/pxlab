package de.pxlab.pxl;

import java.util.ArrayList;

/**
 * A ColorServer owns a color object which may be set and retrieved by clients.
 * The ColorServer also allows clients to register themselves as
 * ColorChangeListerns in order to get a message whenever the color object has
 * been changed by some client. This is an abstract class.
 * 
 * <p>
 * A color coordinates client communicates with a color server. The client tells
 * the server that it wants to change the server's active color and sends the
 * new coordinates to the server by sending the message
 * colorCoordinatesChanged() to the server. The server then distributes this
 * message to all registered ColorChangeListeners. These adjust their state to
 * the new coordinates. Clients who are able to send coordinate change messages
 * must be careful not to resend the change message whenever they get a signal
 * from the color table and respond with a state change. The client identifies
 * itself when sending the coordinates changed message to the server in order to
 * prevent the server from sending the same message back to that client which
 * initially sent the change message.
 * 
 * @author H. Irtel
 * @version 0.3.0
 * @see ColorChangeListener
 * @see ColorChangeEvent
 */
/*
 * 02/04/01
 */
abstract public class ColorServer {
	// private PxlColor defaultBackground = PxlColor.black;
	/**
	 * This method is called by clients which manage color coordinates and wish
	 * to tell the server that they have changed the coordinates of the
	 * currently active color. This method fires ColorChangeEvents of type
	 * COLOR_ADJUSTED.
	 * 
	 * @param source
	 *            the object which generated the change.
	 * @param x
	 *            the new color coordinates.
	 */
	abstract public void colorAdjusted(Object source, PxlColor x);

	/**
	 * Tell the color server that the given source may have changed the color
	 * parameter and requests an update of all listeners. This method is used in
	 * cases where the currently active color dependents on other objects like
	 * spectra which may have changed the color store but do not know how to
	 * connect to the active color.
	 * 
	 * @param source
	 *            the object which generated the change.
	 */
	public void colorAdjusted(Object source) {
		colorAdjusted(source, getActiveColor());
	}

	/**
	 * Set the current color of this server to the given color. This method
	 * creates and fires ColorChangeEvents of type COLOR_SET.
	 * 
	 * @param source
	 *            the source object of the change.
	 * @param adjustible
	 *            if true than the active color is adjustible if false then it
	 *            is passive and for display only.
	 * @param color
	 *            the color coordinates of the new color.
	 */
	abstract public void setActiveColor(Object source, boolean adjustible,
			PxlColor color);

	/**
	 * Set the current color of this server to the given color. This method
	 * creates and fires ColorChangeEvents of type COLOR_SET.
	 * 
	 * @param source
	 *            the source object of the change.
	 * @param color
	 *            the color coordinates of the new color.
	 */
	public void setActiveColor(Object source, PxlColor color) {
		setActiveColor(source, true, color);
	}

	/**
	 * Return the currently active color's coordinates.
	 * 
	 * @return a PxlColor object describing the coordinates of the currently
	 *         active color of this server.
	 */
	abstract public PxlColor getActiveColor();
	/** The list of ChangeListener objects registered. */
	private ArrayList colorChangeListeners = new ArrayList();

	/**
	 * Add a ColorChangeListener object to this ColorServer.
	 * 
	 * @param x
	 *            an Object which implements the ColorChangeListener interface.
	 */
	public void addColorChangeListener(ColorChangeListener x) {
		colorChangeListeners.add(x);
	}

	/**
	 * Remove the given Object from the list of ColorChangeListener objects.
	 * 
	 * @param x
	 *            an Object which implements the ColorChangeListener interface.
	 */
	public void removeColorChangeListener(ColorChangeListener x) {
		colorChangeListeners.remove(colorChangeListeners.indexOf(x));
	}

	/**
	 * Send a ColorChangeEvent message to all registered listeners except the
	 * source of the event.
	 * 
	 * @param e
	 *            the ColorChangeEvent which should be distributed to all
	 *            registered listeners.
	 */
	protected void fireColorChangeEvent(ColorChangeEvent e) {
		if (colorChangeListeners.isEmpty())
			return;
		int n = colorChangeListeners.size();
		for (int i = 0; i < n; i++) {
			Object ccl = colorChangeListeners.get(i);
			if (!ccl.equals(e.getSource()))
				((ColorChangeListener) (ccl)).colorChanged(e);
		}
	}
	/**
	 * Set the default background color of this ColorServer.
	 * 
	 * @param c
	 *            the new background color.
	 */
	/*
	 * public void setDefaultBackground(PxlColor c) { defaultBackground = c; }
	 */
	/**
	 * Get the default background color of this ColorServer.
	 * 
	 * @return the background color of this server.
	 */
	/*
	 * public PxlColor getDefaultBackground() { return(defaultBackground); }
	 */
}
