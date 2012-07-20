package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.JEditorPane;

import de.pxlab.pxl.*;

/**
 * Provides a frame for showing display descriptions. The descriptions are HTML
 * files with their name being identical to the respective display class name.
 * 
 * @author H. Irtel
 * @version 0.2.2
 */
/*
 * 
 * Swing version
 * 
 * 
 * 02/04/01 allow null Display objects in describe();
 */
public class DescriptionFrame extends Frame {
	/** Text area for the description text. */
	private JEditorPane textArea;
	private ScrollPane scrollPane;

	public DescriptionFrame() {
		this("PXLab");
	}

	/**
	 * Create the description frame. This frame is created only once and made
	 * visible when needed.
	 */
	public DescriptionFrame(String t) {
		super("  " + t);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// textArea = null;
				setVisible(false);
			}
		});
		textArea = new JEditorPane();
		textArea.setContentType("text/html");
		textArea.setPreferredSize(new Dimension(400, 5000));
		textArea.setEditable(false);
		scrollPane = new ScrollPane();
		scrollPane.setSize(new Dimension(500, 600));
		scrollPane.add(textArea);
		add(scrollPane);
		pack();
		setVisible(false);
	}

	/** Show the description of the given display. */
	public void describe(Display dsp) {
		if (dsp == null)
			return;
		String className = dsp.getClass().getName();
		String simpleClassName = className
				.substring(className.lastIndexOf('.') + 1);
		String htmlFileName = "de/pxlab/pxl/display/html/" + simpleClassName
				+ ".html";
		StringBuffer buf = new StringBuffer();
		/*
		 * s = "file:" + System.getProperty("user.dir") +
		 * System.getProperty("file.separator") + "describe-e" +
		 * System.getProperty("file.separator") + n + ".html"; URL helpURL = new
		 * URL(s);
		 * System.out.println("DescriptionFrame.describe(): Trying to open URL "
		 * + helpURL); try { BufferedReader in = new BufferedReader(new
		 * InputStreamReader(helpURL.openStream()));
		 */
		// System.out.println("DescriptionFrame.describe(): " + htmlFileName);
		InputStream ins = ClassLoader.getSystemResourceAsStream(htmlFileName);
		if (ins == null) {
			buf.append("Description resource not found for " + className);
		} else {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						ins));
				String nl = System.getProperty("line.separator");
				String ln;
				while ((ln = in.readLine()) != null)
					buf.append(ln).append(nl);
				textArea.setText(buf.toString());
				in.close();
			} catch (IOException e) {
				buf.append("No description available for " + className);
			}
		}
		show(buf.toString(), dsp.getTitle());
	}

	public void show(String b, String t) {
		textArea.setText(b);
		if (t != null) {
			setTitle("  " + t);
		}
		setVisible(true);
	}
	private StringBuffer sb = null;

	public void addText(String s) {
		if (sb == null) {
			sb = new StringBuffer(1000);
		}
		sb.append(s);
		textArea.setDocument(textArea.getEditorKit().createDefaultDocument());
		textArea.setText(sb.toString());
		setVisible(true);
	}

	public void clear() {
		sb = new StringBuffer(1000);
		textArea.setText("");
		setVisible(true);
	}
}
