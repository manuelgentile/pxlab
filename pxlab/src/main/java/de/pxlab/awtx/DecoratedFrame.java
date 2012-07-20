package de.pxlab.awtx;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.net.URL;

/**
 * A frame which may have an icon, and knows whether it has an applet or an
 * application as its parent. The frame's layout manager is BorderLayout.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 08/11/01
 */
public class DecoratedFrame extends Frame {
	protected Applet parent;

	/**
	 * Create a new decorated frame.
	 * 
	 * @param title
	 *            the title of the frame,
	 * @param parent
	 *            if this frame is instantiated by an applet then it should send
	 *            its reference to the frame.
	 * @param icon
	 *            the frame's icon.
	 */
	public DecoratedFrame(String title, Applet parent, Image icon) {
		super(title);
		this.parent = parent;
		setLayout(new BorderLayout());
		if (icon != null)
			setIconImage(icon);
	}

	/**
	 * Create a new decorated frame which has an application as its parent.
	 * 
	 * @param title
	 *            the title of the frame.
	 */
	public DecoratedFrame(String title) {
		this(title, null, null);
	}

	/**
	 * Create a new decorated frame which has an application as its parent and
	 * uses the given icon file.
	 * 
	 * @param title
	 *            the title of the frame,
	 * @param icon
	 *            the image for this frame's icon.
	 */
	public DecoratedFrame(String title, Image icon) {
		this(title, null, icon);
	}

	/** Return true if this frame is running as an application. */
	protected boolean isApplication() {
		return (parent == null);
	}

	public java.awt.Container getParent() {
		return parent;
	}

	/** Use the given file as this frame's icon. */
	private void setIcon(String fn) {
		Image icon = null;
		URL pn = getClass().getResource(fn);
		if (pn == null)
			return;
		try {
			icon = createImage((ImageProducer) pn.getContent());
		} catch (IOException ee) {
			icon = null;
		}
		if (icon == null)
			return;
		MediaTracker itr = new MediaTracker(this);
		itr.addImage(icon, 0);
		try {
			itr.waitForID(0);
			this.setIconImage(icon);
		} catch (InterruptedException e) {
		}
	}
}
