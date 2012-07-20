package de.pxlab.pxl.display.editor;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.awtx.*;

import de.pxlab.pxl.*;
import de.pxlab.pxl.gui.*;

/**
 * A frame which holds an editor for experimental display objects.
 * 
 * @author H. Irtel
 * @version 0.3.3
 */
/*
 * 
 * 11/09/01 added command line option handling
 * 
 * 08/11/01
 * 
 * 08/20/03 no File menue for applets
 */
public class DisplayEditor extends CloseableFrame implements
		DisplayEditorPanelController, DisplaySelectionListener {
	/** Prefix string for the title bar. */
	private String titlePrefix = "";
	/** This frame's menu bar. */
	private MenuBar menuBar;
	private Menu fileMenu;
	/**
	 * This is the main display selection Menu which is contained in the main
	 * control frame's menu bar.
	 */
	private Menu displaySelectionMenu;
	private Menu optionsMenu;
	private Menu helpMenu;
	/** That's where the work gets done. */
	protected DisplayEditorPanel displayEditorPanel;
	private String initialDisplay = null;

	/**
	 * Create a display editor with the given collection of Display objects.
	 * 
	 * @param dspList
	 *            the list of displays which are available
	 * @param args
	 *            command line arguments.
	 * @param xmd
	 *            true for experimental display editing and false for vision
	 *            demos.
	 */
	public DisplayEditor(DisplayCollection dspList, String[] args, boolean xmd) {
		super("");
		// Debug.setDebugLayout(true);
		titlePrefix = (xmd ? " PXLab Display Editor "
				: " Vision Demonstrations ") + Base.getCopyright();
		// Get the command line arguments and set our internal parameter
		if ((args != null) && (args.length > 0)) {
			StringBuffer cmdLine = new StringBuffer(args[0]);
			for (int i = 1; i < args.length; i++)
				cmdLine.append(" " + args[i]);
			ExPar.CommandLine.set(cmdLine.toString());
			DisplayEditorOptionHandler opthd = new DisplayEditorOptionHandler(
					"DisplayEditor", args);
			initialDisplay = opthd.getInitialDisplay();
		}
		setSize(new Dimension(Base.hasScreenWidth() ? Base.getScreenWidth()
				: 1200, Base.hasScreenHeight() ? Base.getScreenHeight() : 800));
		PXLabIcon.decorate(this);
		// Create and install the menu bar
		menuBar = new MenuBar();
		setMenuBar(menuBar);
		if (Base.isApplication()) {
			fileMenu = new FileMenu(this);
			menuBar.add(fileMenu);
		}
		displaySelectionMenu = new DisplaySelectionMenu("Select", dspList, this);
		menuBar.add(displaySelectionMenu);
		displayEditorPanel = new DisplayEditorPanel(this, xmd);
		add(displayEditorPanel);
		displayEditorPanel.setBigList(dspList);
		if (Base.isApplication()) {
			MenuItem[] fmu = displayEditorPanel.getFileMenuItems();
			if (fmu != null) {
				int k = fileMenu.getItemCount() - 2;
				for (int i = 0; i < fmu.length; i++) {
					fileMenu.insert(fmu[i], k + i);
				}
			}
		}
		optionsMenu = displayEditorPanel.getOptionsMenu();
		menuBar.add(optionsMenu);
		helpMenu = new HelpMenu(this);
		menuBar.setHelpMenu(helpMenu);
		setVisible(true);
		// System.out.println("DisplayEditor() after setVisible()");
		Display iD = null;
		if (initialDisplay != null) {
			iD = (Display) DisplaySupport.load(initialDisplay, null,
					ExDesignNode.DisplayNode);
			if (iD == null) {
				iD = (Display) dspList.get(0);
			}
		} else {
			iD = (Display) dspList.get(0);
		}
		selectDisplay(iD);
	}

	// ---------------------------------------------------------------
	// DisplayEditorPanelController implementation
	// ---------------------------------------------------------------
	/**
	 * Return the Frame object which is the editor's parent in the container
	 * hierarchy.
	 */
	public Frame getFrame() {
		return (this);
	}

	/**
	 * Get the controller's menu bar if it has one.
	 * 
	 * @return the controller's menu bar or null if this controller does not
	 *         have a menu bar.
	 */
	public MenuBar getMenuBar() {
		return menuBar;
	}

	/**
	 * Set the enabled state of the controller's menu bar.
	 * 
	 * @param state
	 *            if true then the menu bar items are enabled if false then they
	 *            are disabled
	 */
	public void setMenuBarEnabled(boolean state) {
		if (Base.isApplication()) {
			fileMenu.setEnabled(state);
		}
		displaySelectionMenu.setEnabled(state);
		optionsMenu.setEnabled(state);
		helpMenu.setEnabled(state);
	}

	// ---------------------------------------------------------------
	// DisplaySelectionListener implementation
	// ---------------------------------------------------------------
	/**
	 * This is called by the DisplaySelectionMenu whenever a new Display object
	 * gets selected from the menu.
	 */
	public void selectDisplay(DisplaySupport dsp) {
		displayEditorPanel.setUninitializedDisplay((Display) dsp);
		String n = dsp.getClass().getName();
		if (n.startsWith("de.pxlab.pxl.display."))
			n = n.substring(21);
		setTitle(titlePrefix + " - " + dsp.getTitle() + " [" + n + "]");
	}

	// ---------------------------------------------------------------
	// Main entry point
	// ---------------------------------------------------------------
	public static void main(String[] args) {
		new DisplayEditor(new BigList(Topics.EXP), args, true);
	}
}
