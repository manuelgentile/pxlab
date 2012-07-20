package de.pxlab.pxl.display.editor;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.awtx.*;
import de.pxlab.pxl.*;

/**
 * This is the super class of single display property panels which have some
 * features in common.
 * 
 * @version 0.2.0
 */
abstract public class DisplayPropertyControl extends InsetPanel {
	protected String title;
	private ScrollPane scrollPane;

	public DisplayPropertyControl(String title) {
		super(new BorderLayout());
		if (Debug.layout())
			setBackground(Color.blue);
		else
			setBackground(PXLabGUI.background);
		this.title = title;
		scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
		scrollPane.setSize(390, 100);
		if (Debug.layout())
			scrollPane.setBackground(Color.red);
		add(scrollPane, BorderLayout.CENTER);
	}

	protected void addContent(Panel p) {
		scrollPane.add(p);
		// p.setBackground(Color.cyan);
	}

	abstract public void configureFor(Display dsp);

	public String getTitle() {
		return (title);
	}
}
