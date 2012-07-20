package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import java.util.ArrayList;
import de.pxlab.pxl.*;
import de.pxlab.util.FileList;

/**
 * A dialog for selecting a design file.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class DesignSelectionDialog extends Dialog implements ActionListener {
	private int borderWidth = 10;
	private Button cancelButton;
	private Button okButton;
	private List designList;
	private ArrayList bigList;
	private TextArea textPanel;
	private String pxdPath = "d:\\jpxl";
	private String selectedDesign;

	public DesignSelectionDialog(Frame frame) {
		super(frame, " Design Selection", true);
		setResizable(true);
		try {
			bigList = new FileList(pxdPath, "pxd");
		} catch (FileNotFoundException fnf) {
			new FileError("Path " + pxdPath + " not found.");
			bigList = new ArrayList();
		}
		selectedDesign = null;
		setLayout(new BorderLayout(borderWidth, borderWidth));
		// The display panel is at the CENTER position
		textPanel = new TextArea(40, 100);
		textPanel.setFont(new Font("Monospaced", Font.PLAIN, 14));
		textPanel.setEditable(false);
		add(textPanel, BorderLayout.CENTER);
		Panel leftPanel = new Panel(new BorderLayout(borderWidth, borderWidth));
		add(leftPanel, BorderLayout.WEST);
		// leftPanel.add(topicField, BorderLayout.NORTH);
		designList = new List();
		ListHandler dLH = new ListHandler();
		designList.addItemListener(dLH);
		designList.addActionListener(dLH);
		for (int i = 0; i < bigList.size(); i++) {
			String n = (String) (bigList.get(i));
			// designList.add(n.substring(n.lastIndexOf('/')+1));
			designList.add(n);
		}
		leftPanel.add(designList, BorderLayout.CENTER);
		cancelButton = new Button(" Cancel ");
		cancelButton.addActionListener(this);
		okButton = new Button(" OK ");
		okButton.addActionListener(this);
		Panel buttonGridPanel = new Panel(new GridLayout(1, 0, borderWidth, 0));
		buttonGridPanel.add(cancelButton);
		buttonGridPanel.add(okButton);
		Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		buttonPanel.add(buttonGridPanel);
		leftPanel.add(buttonPanel, BorderLayout.SOUTH);
		pack();
	}

	/** Return the display object which has been selected. */
	public String getSelectedDesign() {
		return (selectedDesign);
	}

	/** Button actions. */
	public void actionPerformed(ActionEvent e) {
		Button src = (Button) e.getSource();
		if (src == cancelButton) {
			close();
		} else if (src == okButton) {
			setSelectedDesign();
			close();
		}
	}

	/** Set the currently selected display for later requests. */
	private void setSelectedDesign() {
		int idx = designList.getSelectedIndex();
		if (idx >= 0) {
			selectedDesign = (String) bigList.get(idx);
		}
	}

	/** Close this dialog. */
	private void close() {
		setVisible(false);
	}

	public Insets getInsets() {
		Insets s = super.getInsets();
		return (new Insets(s.top + borderWidth, s.left + borderWidth, s.bottom
				+ borderWidth, s.right + borderWidth));
	}

	/**
	 * Show the currently selected display object in the display panel.
	 */
	private void showSelectedDesign() {
		int idx = designList.getSelectedIndex();
		if (idx >= 0) {
			textPanel.setText(designFileContent((String) bigList.get(idx)));
			// textPanel.repaint();
		}
	}

	private String designFileContent(String p) {
		File f = new File(p);
		String r = null;
		if (f.canRead()) {
			int n = (int) f.length();
			try {
				BufferedInputStream bf = new BufferedInputStream(
						new FileInputStream(f), n);
				StringBuffer sb = new StringBuffer(n);
				int c;
				while ((c = bf.read()) != -1)
					sb.append((char) c);
				bf.close();
				r = sb.toString();
			} catch (IOException iox) {
			}
		}
		return (r);
	}
	// -----------------------------------------------------------
	// List item selection
	// -----------------------------------------------------------
	/** Handles selections from a list of display object names. */
	private class ListHandler implements ItemListener, ActionListener {
		/**
		 * This is a single click on one of the list items and means selection
		 * or deselection.
		 */
		public void itemStateChanged(ItemEvent e) {
			showSelectedDesign();
		}

		/** This is a double click on any of the list items. */
		public void actionPerformed(ActionEvent e) {
			setSelectedDesign();
			close();
		}
	}
}
