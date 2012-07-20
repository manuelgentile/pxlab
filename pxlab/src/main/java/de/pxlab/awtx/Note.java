package de.pxlab.awtx;

import java.awt.*;

/**
 * This is a frame which shows only a single message line. It must be removed by
 * the caller such that it should only be used for indicating waiting periods..
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class Note extends Frame {
	public Note(String message) {
		super(" Wait!");
		setLayout(new BorderLayout());
		add(new Label(message), BorderLayout.CENTER);
		setLocation(300, 200);
		pack();
		setResizable(false);
		setVisible(true);
	}

	public Insets getInsets() {
		return new Insets(20, 20, 20, 20);
	}
}
