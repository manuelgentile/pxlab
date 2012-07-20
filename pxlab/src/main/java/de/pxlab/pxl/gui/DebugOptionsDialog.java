package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;
import de.pxlab.awtx.CloseButtonDialog;
import de.pxlab.pxl.Debug;

/**
 * A Dialog for setting debug options.
 * 
 * @author H. Irtel
 * @version 0.1.3
 */
public class DebugOptionsDialog extends CloseButtonDialog implements
		ItemListener {
	private ArrayList box = new ArrayList();

	public DebugOptionsDialog(Frame p) {
		super(p, "Debug Options");
		Panel boxPanel = new Panel(new GridLayout(0, 2, 5, 5));
		boxPanel.add(createCheckbox("File access", (Debug.FILES)));
		boxPanel.add(createCheckbox("Design tree parser", (Debug.PARSER)));
		boxPanel.add(createCheckbox("Design tree node creation",
				(Debug.CREATE_NODE)));
		boxPanel.add(createCheckbox("Assignment Execution", (Debug.ASSIGNMENT)));
		boxPanel.add(createCheckbox("Expression Evaluation", (Debug.EXPR)));
		boxPanel.add(createCheckbox("Display list management",
				(Debug.DISPLAY_LIST)));
		boxPanel.add(createCheckbox("Runtime tree node push/pop",
				(Debug.PUSH_POP)));
		boxPanel.add(createCheckbox("Display list argument values",
				(Debug.ARGVALUES)));
		boxPanel.add(createCheckbox("Procedure/Session/Block/Trial states",
				(Debug.STATE_CTRL)));
		boxPanel.add(createCheckbox("Display object properties",
				(Debug.DSP_PROPS)));
		boxPanel.add(createCheckbox("Adaptive procedure parameters",
				(Debug.ADAPTIVE)));
		boxPanel.add(createCheckbox("Focus events", (Debug.FOCUS)));
		boxPanel.add(createCheckbox("Stack trace on errors",
				(Debug.ERR_STACK_TRACE)));
		boxPanel.add(createCheckbox("Timing in ms", (Debug.TIMING)));
		boxPanel.add(createCheckbox("Timing in ns", (Debug.HR_TIMING)));
		boxPanel.add(createCheckbox("Media files and timing",
				(Debug.MEDIA_TIMING)));
		boxPanel.add(createCheckbox("Voice key levels", (Debug.VOICE_KEY)));
		boxPanel.add(createCheckbox("Color gamut violations",
				(Debug.COLOR_GAMUT)));
		add(boxPanel, BorderLayout.CENTER);
		pack();
	}

	public void closeDialog() {
		setVisible(false);
		dispose();
	}

	private Checkbox createCheckbox(String n, long pattern) {
		Checkbox b = new Checkbox(n, Debug.isActive(pattern));
		b.addItemListener(this);
		box.add(new Entry(b, pattern));
		return (b);
	}

	public void itemStateChanged(ItemEvent e) {
		Checkbox b = (Checkbox) e.getSource();
		for (int i = 0; i < box.size(); i++) {
			if (b == (Checkbox) ((Entry) box.get(i)).checkbox) {
				if (b.getState()) {
					Debug.add(((Entry) box.get(i)).pattern);
				} else {
					Debug.remove(((Entry) box.get(i)).pattern);
				}
			}
		}
	}
	private class Entry {
		Checkbox checkbox;
		long pattern;

		public Entry(Checkbox b, long p) {
			checkbox = b;
			pattern = p;
		}
	}
}
