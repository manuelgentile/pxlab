package de.pxlab.awtx;

import java.awt.Dimension;
import java.awt.Panel;

/** This is a filler panel which is used for layout purposes only. */
public class Fill extends Panel {
	private int hfill;
	private int vfill;

	public Fill(int h, int v) {
		hfill = h;
		vfill = v;
	}

	public Dimension getPreferredSize() {
		return (new Dimension(hfill, vfill));
	}

	public Dimension getMinimumSize() {
		return (new Dimension(hfill, vfill));
	}

	public Dimension getMaximumSize() {
		return (new Dimension(hfill, vfill));
	}
}
