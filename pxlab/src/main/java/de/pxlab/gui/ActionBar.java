package de.pxlab.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.EventListener;
import java.util.ArrayList;

/**
 * This is a bar of Button or Choice objects which acts like a conventional tool
 * bar. Buttons are added such that they have equal size and are grouped
 * together. Choices may have different sizes and their position depends on
 * whether they are added first or after the first button has been added. In the
 * latter case the choices are positioned right of the buttons. Provisions are
 * met for introducing a special group of buttons which always are positioned at
 * the rightmost edge of the action bar.
 * 
 * @version 0.1.1
 * @author H. Irtel
 */
/*
 * 03/16/01 added showStatus()
 * 
 * 05/27/01 added addPanel()
 */
public class ActionBar extends Panel {
	/** This holds all buttons and makes them equally wide. */
	private Panel buttonPanel = null;
	/** This holds the choices. */
	private Panel choicePanel = null;
	/** This holds both the choice and the button panel. */
	private Panel leftPanel = null;
	/**
	 * This may be used to hold buttons which should always be at the right edge
	 * of the action bar.
	 */
	private Panel rightPanel = null;
	/** The message label. */
	private Label msgLabel;
	private ActionListener actionBarHandler;

	/** Create a new action bar. */
	public ActionBar() {
		this(null);
	}

	/** Create a new action bar. */
	public ActionBar(ActionListener abh) {
		super(new BorderLayout());
		setBackground(SystemColor.control);
		actionBarHandler = abh;
		leftPanel = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		// leftPanel.setBackground(SystemColor.menu);
		add(leftPanel, BorderLayout.WEST);
		rightPanel = new Panel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		// rightPanel.setBackground(SystemColor.menu);
		add(rightPanel, BorderLayout.EAST);
		msgLabel = new Label(" ");
		// msgLabel.setBackground(SystemColor.menu);
		msgLabel.setAlignment(Label.CENTER);
		msgLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
		rightPanel.add(msgLabel);
	}

	/**
	 * Create a button and add it to the action bar's button panel.
	 * 
	 * @param n
	 *            the label of the button.
	 * @return the Button object created.
	 */
	public Button addButton(String n) {
		return (addButton(actionBarHandler, n));
	}

	/**
	 * Create a button and add it to the action bar's button panel.
	 * 
	 * @param a
	 *            the ActionListener object for the button.
	 * @param n
	 *            the label of the button.
	 * @return the Button object created.
	 */
	public Button addButton(ActionListener a, String n) {
		if (buttonPanel == null) {
			buttonPanel = new Panel(new GridLayout(1, 0, 0, 0));
			// buttonPanel.setBackground(SystemColor.menu);
			leftPanel.add(buttonPanel);
		}
		Button b = new Button(n);
		buttonPanel.add(b);
		b.addActionListener(a);
		return (b);
	}

	/**
	 * Add agiven Button to the action bar's button panel.
	 * 
	 * @param b
	 *            the Button to be added.
	 */
	public void addButton(Button b) {
		if (buttonPanel == null) {
			buttonPanel = new Panel(new GridLayout(1, 0, 0, 0));
			// buttonPanel.setBackground(SystemColor.menu);
			leftPanel.add(buttonPanel);
		}
		buttonPanel.add(b);
	}

	/**
	 * Create a Choice object and add it to the action bar.
	 * 
	 * @param a
	 *            the ItemListener object for the button.
	 * @param n
	 *            the array of choice items.
	 * @return the Choice object created.
	 */
	public Choice addChoice(ItemListener a, String[] n) {
		if (choicePanel == null) {
			choicePanel = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			// choicePanel.setBackground(SystemColor.menu);
			leftPanel.add(choicePanel);
		}
		Choice ce = new Choice();
		for (int i = 0; i < n.length; i++) {
			ce.add(n[i]);
		}
		ce.addItemListener(a);
		choicePanel.add(ce);
		return (ce);
	}

	/**
	 * Add an already instantiated Choice object to the action bar.
	 * 
	 * @param ce
	 *            the choice item to be added.
	 */
	public void addChoice(Choice ce) {
		if (choicePanel == null) {
			choicePanel = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			// choicePanel.setBackground(SystemColor.menu);
			leftPanel.add(choicePanel);
		}
		choicePanel.add(ce);
	}

	/**
	 * Add an already instantiated Panel object to the action bar. This object
	 * gets added to the right of the standard buttons.
	 * 
	 * @param panel
	 *            the Panel object to be added.
	 */
	public void addPanel(Panel panel) {
		leftPanel.add(panel);
	}

	/**
	 * Show the given message string as a status message at the right edge of
	 * the action bar.
	 * 
	 * @param msg
	 *            the message string.
	 */
	public void showStatus(String msg) {
		msgLabel.setText(msg);
		validate();
	}
	/**
	 * Clear the action bar by removing all listeners and all buttons and
	 * choices.
	 */
	/*
	 * Not JDK1.1 compatible public void clear() { if (buttonPanel != null) {
	 * Component[] p = buttonPanel.getComponents(); for(int i = 0; i < p.length;
	 * i++) { EventListener[] e = p[i].getListeners(ActionListener.class); for
	 * (int j = 0; j < e.length; j++) {
	 * ((Button)p[i]).removeActionListener((ActionListener)e[j]); } }
	 * buttonPanel.removeAll(); buttonPanel = null; } if (choicePanel != null) {
	 * Component[] p = choicePanel.getComponents(); for(int i = 0; i < p.length;
	 * i++) { EventListener[] e = p[i].getListeners(ItemListener.class); for
	 * (int j = 0; j < e.length; j++) {
	 * ((Choice)p[i]).removeItemListener((ItemListener)e[j]); } }
	 * choicePanel.removeAll(); choicePanel = null; } }
	 */
}
