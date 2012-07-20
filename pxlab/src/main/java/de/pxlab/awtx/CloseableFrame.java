package de.pxlab.awtx;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.net.URL;

/**
 * This is a frame which has an active window closing button, may have an icon,
 * and knows whether it has an applet or an application as its parent. The
 * frame's layout manager is BorderLayout.
 * 
 * @author H. Irtel
 * @version 0.3.0
 */
/*
 * 08/11/01
 */
public class CloseableFrame extends DecoratedFrame {
	/**
	 * Create a new closeable frame.
	 * 
	 * @param title
	 *            the title of the frame,
	 * @param parent
	 *            if this frame is instantiated by an applet then it should send
	 *            its reference to the frame.
	 * @param icon
	 *            the frame's icon.
	 */
	public CloseableFrame(String title, Applet parent, Image icon) {
		super(title, parent, icon);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				if (isApplication()) {
					evt.getWindow().dispose();
					System.exit(0);
				} else {
					CloseableFrame.this.parent.stop();
				}
			}
		});
	}

	/**
	 * Create a new closeable frame which has an application as its parent.
	 * 
	 * @param title
	 *            the title of the frame.
	 */
	public CloseableFrame(String title) {
		this(title, null, null);
	}

	/**
	 * Create a new closeable frame which has an application as its parent and
	 * uses the given icon file.
	 * 
	 * @param title
	 *            the title of the frame,
	 * @param icon
	 *            the image for this frame's icon.
	 */
	public CloseableFrame(String title, Image icon) {
		this(title, null, icon);
	}
}
