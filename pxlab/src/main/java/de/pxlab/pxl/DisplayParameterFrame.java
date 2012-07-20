package de.pxlab.pxl;

import java.awt.Frame;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Provides a frame for showing display parameter values. The frame must be
 * initialized to a Display befor it can be shown.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 02/04/01 allow null Display objects in describe();
 */
public class DisplayParameterFrame extends Frame {
	private TextArea textArea;

	/**
	 * Create the description frame. This frame is created only once and made
	 * visible when needed.
	 */
	public DisplayParameterFrame() {
		super();
		textArea = new TextArea(30, 60);
		textArea.setEditable(false);
		add(textArea);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
		pack();
		setVisible(false);
	}

	/** Show the description of the given display. */
	public void describe(Display dsp) {
		if (dsp == null)
			return;
		ExParDescriptor[] exd = dsp.getExParFields();
		StringBuffer buf = new StringBuffer();
		String nl = System.getProperty("line.separator");
		if (exd != null) {
			for (int i = 0; i < exd.length; i++) {
				buf.append(exd[i].toString() + nl + nl);
			}
		} else {
			buf.append("This Display object does not have parameters!" + nl);
		}
		textArea.setText(buf.toString());
		setTitle("  " + dsp.getTitle());
		setVisible(true);
	}
}
