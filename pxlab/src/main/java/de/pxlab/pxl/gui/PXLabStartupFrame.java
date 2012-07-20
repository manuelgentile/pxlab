package de.pxlab.pxl.gui;

import java.awt.*;

/**
 * A frame which shows the PXLab logo and author information. This frame can be
 * shown before any other screen element is created in order to find out some
 * system properties which may only be found if an object is visible on the
 * screen.
 * 
 * @author H. Irtel
 * @version 0.1.2
 */
/*
 * 
 * 08/12/03 added version check
 */
public class PXLabStartupFrame extends Frame {
	public PXLabStartupFrame() {
		super(" PXLab");
		setLayout(new BorderLayout());
		setBackground(de.pxlab.pxl.PXLabGUI.background);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Panel p = new Panel(new BorderLayout(20, 20));
		p.add(new PXLabLogoComponent(screen.width / 8), BorderLayout.WEST);
		Panel pp = new Panel(new BorderLayout());
		Label pxlab = new Label("PXLab");
		pxlab.setForeground(Color.gray);
		Label hi = new Label("H. Irtel (2002)");
		hi.setForeground(Color.gray);
		pxlab.setFont(new Font("SansSerif", Font.BOLD, 72));
		hi.setFont(new Font("SansSerif", Font.PLAIN, 36));
		pp.add(hi, BorderLayout.NORTH);
		pp.add(pxlab, BorderLayout.CENTER);
		p.add(pp, BorderLayout.CENTER);
		add(p, BorderLayout.CENTER);
		pack();
		setLocation(400, 300);
		setVisible(true);
		try {
			while (!isVisible()) {
				Thread.sleep(10);
			}
			// Thread.sleep(2000);
		} catch (InterruptedException iex) {
		}
		checkVersion();
	}

	public void checkVersion() {
		String thisJava = System.getProperty("java.version");
		int this_major = getMajor(thisJava);
		int comp_major = getMajor(de.pxlab.pxl.Version.javaVersion);
		int this_minor = getMinor(thisJava);
		int comp_minor = getMinor(de.pxlab.pxl.Version.javaVersion);
		String msg = null;
		if (this_major > comp_major) {
			// Probably OK
		} else if (this_major == comp_major) {
			if (this_minor > comp_minor) {
				if ((this_minor >= 4) && (comp_minor == 1)) {
					msg = "Possible problem: incompatible keyboard focus management in full screen mode";
				} else {
					// Probably OK
				}
			} else if (this_minor < comp_minor) {
				msg = "Please upgrade to the latest Java Runtime Environment";
			} else {
				// Fits exactly
			}
		} else if (this_major < comp_major) {
			msg = "Please upgrade to the latest Java Runtime Environment";
		}
		if (msg != null) {
			String m = "Possible version incompatibility!\nPXLab compiled by Java version "
					+ de.pxlab.pxl.Version.javaVersion
					+ "\nCurrently running Java version is " + thisJava + ".\n";
			new de.pxlab.pxl.NonFatalError(m + msg);
		}
	}

	private int getMajor(String v) {
		int i = 0;
		String m = v.substring(0, v.indexOf('.'));
		try {
			i = Integer.parseInt(m);
		} catch (NumberFormatException nfx) {
		}
		return i;
	}

	private int getMinor(String v) {
		int i = 0;
		String n = v.substring(v.indexOf('.') + 1);
		String m = n.substring(0, n.indexOf('.'));
		try {
			i = Integer.parseInt(m);
		} catch (NumberFormatException nfx) {
		}
		return i;
	}

	public Insets getInsets() {
		Insets i = super.getInsets();
		return new Insets(i.top + 20, i.left + 60, i.bottom + 20, i.right + 60);
	}

	/** Find out the currently active system font. */
	public Font getSystemFont() {
		if (isVisible()) {
			return getFont();
		}
		return new Font("SansSerif", Font.PLAIN, 14);
	}

	/** Find out the currently active system font. */
	public Dimension getSystemFontSize() {
		if (isVisible()) {
			Graphics g = getGraphics();
			FontMetrics fm = g.getFontMetrics();
			Dimension size = new Dimension(fm.charWidth('H'), fm.getHeight());
			g.dispose();
			return size;
		}
		return new Dimension(8, 16);
	}

	public void dispose() {
		setVisible(false);
		super.dispose();
	}
}
