package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.pxl.PXLabIcon;

/**
 * The PXLabApplicationFrame class describes frames which can run PXLab
 * applications. These frames have a menu bar with menu entries being extendable
 * by the respective application. The application frame may be started as a
 * stand alone application or may be started by an applet's start button.
 */
/*
 * 
 * 02/27/03 changed exitApplication to localExitApplication (pf)
 */
abstract public class PXLabApplicationFrame extends Frame {
	private MenuBar menuBar;
	private Menu fileMenu;
	private Menu[] applicationMenus;
	private Menu optionMenu;
	private Menu helpMenu;

	public PXLabApplicationFrame(String title) {
		super(title);
		setSize(new Dimension(800, 600));
		setLayout(new BorderLayout());
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				if (isApplication()) {
					localExitApplication();
				} else {
					// CloseableFrame.this.parent.stop();
				}
			}
		});
		PXLabIcon.decorate(this);
	}

	protected void configureMenuBar() {
		// Create and install the menu bar
		menuBar = new MenuBar();
		setMenuBar(menuBar);
		// Add the obligatory file menu
		fileMenu = new Menu("File");
		MenuItem[] mi = getFileMenuItems();
		if (mi != null) {
			for (int i = 0; i < mi.length; i++) {
				fileMenu.add(mi[i]);
			}
			fileMenu.addSeparator();
		}
		MenuItem exitFileMenuItem = new MenuItem("Exit");
		exitFileMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				localExitApplication();
			}
		});
		fileMenu.add(exitFileMenuItem);
		menuBar.add(fileMenu);
		// Add special application menus if these exist
		applicationMenus = getApplicationMenus();
		if (applicationMenus != null) {
			for (int i = 0; i < applicationMenus.length; i++) {
				menuBar.add(applicationMenus[i]);
			}
		}
		// Add an option menu
		optionMenu = new Menu("Options");
		mi = getOptionMenuItems();
		if (mi != null) {
			for (int i = 0; i < mi.length; i++) {
				optionMenu.add(mi[i]);
			}
		}
		menuBar.add(optionMenu);
		// Add the help menu
		helpMenu = new Menu("Help");
		mi = getHelpMenuItems();
		if (mi != null) {
			for (int i = 0; i < mi.length; i++) {
				helpMenu.add(mi[i]);
			}
		}
		menuBar.add(optionMenu);
	}

	/**
	 * Check whether this frame runs as a java application.
	 * 
	 * @return true if the frame runs as an application, false if it runs as an
	 *         applet.
	 */
	private boolean isApplication() {
		return (true);
	}

	// ---------------------------------------------------------------
	// Exit the applications (should handle applets properly)
	// ---------------------------------------------------------------
	// changed to localExitApplication (pf)
	private void localExitApplication() {
		if (exitApplication()) {
			dispose();
			System.exit(0);
		}
	}

	// ---------------------------------------------------------------
	// These abstract methods must be implemented by subclasses
	// ---------------------------------------------------------------
	/**
	 * Try to exit the application.
	 * 
	 * @return true if the application may actually be exited. If false then the
	 *         application should not be exited.
	 */
	abstract protected boolean exitApplication();

	abstract protected MenuItem[] getFileMenuItems();

	abstract protected Menu[] getApplicationMenus();

	abstract protected MenuItem[] getOptionMenuItems();

	abstract protected MenuItem[] getHelpMenuItems();
}
