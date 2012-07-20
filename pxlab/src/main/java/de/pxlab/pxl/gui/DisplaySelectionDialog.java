package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.pxl.*;

/**
 * A dialog for selecting a single display object.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class DisplaySelectionDialog extends Dialog implements ActionListener {
	private int borderWidth = 10;
	private Button cancelButton;
	private Button okButton;
	private List displayList;
	private DisplayCollection bigList;
	private DisplayPanel displayPanel;
	private Display selectedDisplay;
	private Display visibleDisplay = null;

	public DisplaySelectionDialog(Frame frame) {
		this(frame, false);
	}

	public DisplaySelectionDialog(Frame frame, boolean allTopics) {
		super(frame, " Display Selection", true);
		setResizable(true);
		bigList = allTopics ? (DisplayCollection) (new BigList())
				: (DisplayCollection) (new BigList(Topics.EXP));
		selectedDisplay = null;
		setLayout(new BorderLayout(borderWidth, borderWidth));
		// The display panel is at the CENTER position
		displayPanel = new DisplayPanel();
		displayPanel.setPreferredSize(new Dimension(640, 480));
		add(displayPanel, BorderLayout.CENTER);
		Panel leftPanel = new Panel(new BorderLayout(borderWidth, borderWidth));
		add(leftPanel, BorderLayout.WEST);
		// leftPanel.add(topicField, BorderLayout.NORTH);
		displayList = new List();
		ListHandler dLH = new ListHandler();
		displayList.addItemListener(dLH);
		displayList.addActionListener(dLH);
		for (int i = 0; i < bigList.size(); i++) {
			Display d = (Display) (bigList.get(i));
			String n = d.getClass().getName();
			displayList.add(n.substring(n.lastIndexOf('.') + 1));
		}
		leftPanel.add(displayList, BorderLayout.CENTER);
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
	public Display getSelectedDisplay() {
		return (selectedDisplay);
	}

	/** Button actions. */
	public void actionPerformed(ActionEvent e) {
		Button src = (Button) e.getSource();
		if (src == cancelButton) {
			close();
		} else if (src == okButton) {
			setSelectedDisplay();
			close();
		}
	}

	/** Set the currently selected display for later requests. */
	private void setSelectedDisplay() {
		int idx = displayList.getSelectedIndex();
		if (idx >= 0) {
			selectedDisplay = (Display) bigList.get(idx);
		}
	}

	/** Close this dialog. */
	private void close() {
		setVisible(false);
		if (visibleDisplay != null) {
			visibleDisplay.destroyInstance();
			visibleDisplay = null;
		}
	}

	public Insets getInsets() {
		Insets s = super.getInsets();
		return (new Insets(s.top + borderWidth, s.left + borderWidth, s.bottom
				+ borderWidth, s.right + borderWidth));
	}

	/**
	 * Show the currently selected display object in the display panel.
	 */
	private void showSelectedDisplay() {
		int idx = displayList.getSelectedIndex();
		if (idx >= 0) {
			if (visibleDisplay != null) {
				visibleDisplay.destroyInstance();
			}
			visibleDisplay = (Display) bigList.get(idx);
			visibleDisplay.createInstance();
			visibleDisplay.recompute(displayPanel);
			displayPanel.setDisplay(visibleDisplay);
			displayPanel.repaint();
		}
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
			showSelectedDisplay();
		}

		/** This is a double click on any of the list items. */
		public void actionPerformed(ActionEvent e) {
			setSelectedDisplay();
			close();
		}
	}
}
