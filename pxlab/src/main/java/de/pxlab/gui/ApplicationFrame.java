package de.pxlab.gui;

import java.awt.*;
import java.awt.event.*;

/**
 * The ApplicationFrame class describes frames which can run applications. These
 * frames have a menu bar with menu entries being extendable by the respective
 * application.
 */
abstract public class ApplicationFrame extends Frame {
	private MenuBar menuBar;
	private Menu fileMenu;
	private Menu[] applicationMenus;
	private Menu optionMenu;
	private Menu helpMenu;

	public ApplicationFrame(String title) {
		super(title);
		setSize(new Dimension(800, 600));
		setLayout(new BorderLayout());
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				exitApplication();
			}
		});
		// PXLabIcon.decorate(this);
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
				exitApplication();
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

	// ---------------------------------------------------------------
	// Exit the applications (should handle applets properly)
	// ---------------------------------------------------------------
	private void exitApplication() {
		dispose();
		System.exit(0);
	}

	// ---------------------------------------------------------------
	// These abstract methods must be implemented by subclasses
	// ---------------------------------------------------------------
	abstract protected MenuItem[] getFileMenuItems();

	abstract protected Menu[] getApplicationMenus();

	abstract protected MenuItem[] getOptionMenuItems();

	abstract protected MenuItem[] getHelpMenuItems();
}
