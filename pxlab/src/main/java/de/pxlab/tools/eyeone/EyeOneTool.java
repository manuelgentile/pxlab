package de.pxlab.tools.eyeone;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import de.pxlab.awtx.LoadFileDialog;

import de.pxlab.gui.*;

/**
 * Make spectral measurements using the GretagMacbeth Eye-One Pro device.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 03/19/03
 */
public class EyeOneTool extends ApplicationFrame {
	private EyeOnePanel eyeOnePanel;

	public EyeOneTool(String[] args) {
		super(" Spectral Measurement with GretagMacbeth Eye-One Pro");
		eyeOnePanel = new EyeOnePanel();
		add(eyeOnePanel, BorderLayout.CENTER);
		// Now configure the menu bar. This must be done after the
		// application has been created since the application frame
		// will ask the application for menu bar entries.
		configureMenuBar();
		setVisible(true);
	}

	// ---------------------------------------------------------------
	// Implementations of the abstract superclass methods
	// ---------------------------------------------------------------
	// See below
	// abstract protected MenuItem[] getFileMenuItems();
	protected Menu[] getApplicationMenus() {
		return (null);
	}

	// See below
	// abstract protected MenuItem[] getOptionMenuItems();
	protected MenuItem[] getHelpMenuItems() {
		return (null);
	}
	// ---------------------------------------------------------------
	// File menu and actions
	// ---------------------------------------------------------------
	/** This frame's open design file menu item. */
	private MenuItem openColorFileMenuItem;
	/** This frame's reload design file menu item. */
	private MenuItem saveColorFileMenuItem;

	/** Create and return this frame's file menu entries. */
	protected MenuItem[] getFileMenuItems() {
		MenuItem[] mi = new MenuItem[2];
		FileMenuHandler fmh = new FileMenuHandler();
		openColorFileMenuItem = new MenuItem("Open Color File");
		openColorFileMenuItem.addActionListener(fmh);
		mi[0] = openColorFileMenuItem;
		saveColorFileMenuItem = new MenuItem("Save to Color File");
		saveColorFileMenuItem.addActionListener(fmh);
		mi[1] = saveColorFileMenuItem;
		return (mi);
	}
	/** Handles file menu item selections. */
	private class FileMenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			MenuItem mi = (MenuItem) e.getSource();
			if (mi == openColorFileMenuItem) {
				openColorFile();
			} else if (mi == saveColorFileMenuItem) {
				saveColorFile();
			}
		}
	}

	/**
	 * Open a design file and make the respective design tree the current design
	 * tree. The design file open menu item is handled here since it both
	 * affects the procedure and the runtime control panel.
	 */
	private void openColorFile() {
		System.out.println("SM.openColorFile() Opening File ...");
		/*
		 * LoadFileDialog fd = new
		 * LoadFileDialog(openExDesignFileMenuItem.getLabel(), "pxd"); String fn
		 * = fd.getPath(); if (fd.getFile() != null) { exDesign = new
		 * ExDesign(fn, null); if (exDesign.getExDesignTree() != null) {
		 * exRunPanel.setExDesign(exDesign); exDesignFile = fn;
		 * setTitle("PXLab - " + exDesignFile); } } fd.dispose();
		 */
	}

	/** Reload the current ExDesign file. */
	private void saveColorFile() {
		/*
		 * if (exDesignFile == null || !(new File(exDesignFile).canRead())) {
		 * openExDesignFile(); } else { exDesign = new ExDesign(exDesignFile,
		 * null); if (exDesign.getExDesignTree() != null) {
		 * exRunPanel.setExDesign(exDesign); } }
		 */
	}

	// ---------------------------------------------------------------
	// Option menu handling
	// ---------------------------------------------------------------
	/**
	 * This method is called by the parent frame to get the option menu entries.
	 */
	protected MenuItem[] getOptionMenuItems() {
		return eyeOnePanel.getOptionMenuItems();
	}

	// ---------------------------------------------------------------
	// Main entry point
	// ---------------------------------------------------------------
	public static void main(String[] args) {
		new EyeOneTool(args);
	}
}
