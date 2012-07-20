package de.pxlab.awtx;

import java.awt.*;

/** This is a Panel with a predefined inset. */
public class InsetPanel extends Panel {
	public InsetPanel() {
		super();
	}

	public InsetPanel(LayoutManager lm) {
		super(lm);
	}

	public InsetPanel(LayoutManager lm, Insets n) {
		super(lm);
		setInsets(n);
	}
	private static final int controlInset = 6;
	private Insets insets = new java.awt.Insets(controlInset, controlInset,
			controlInset, controlInset);

	public Insets getInsets() {
		return (insets);
	}

	public void setInsets(Insets insets) {
		this.insets = insets;
	}
}
