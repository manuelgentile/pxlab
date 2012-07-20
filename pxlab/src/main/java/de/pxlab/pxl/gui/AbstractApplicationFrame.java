package de.pxlab.pxl.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import de.pxlab.pxl.DebugFocusListener;
import de.pxlab.pxl.RuntimeOptionHandler;

/**
 * This is a general Swing based application frame which has a menu, a toolbar
 * and an application panel. The implementation must provide the menu entries,
 * the toolbar and the application panel upon request.
 * 
 * @version 0.2.0
 */
/*
 * 
 * 2006/10/18 added info items
 */
abstract public class AbstractApplicationFrame extends JFrame {
	public AbstractApplicationFrame(String t, RuntimeOptionHandler optionHandler) {
		super(t);
		if (optionHandler != null) {
			optionHandler.evaluate();
		}
		createActions();
		setJMenuBar(getApplicationMenuBar());
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(getApplicationToolBar(), BorderLayout.PAGE_START);
		contentPane.add(getApplicationPanel(), BorderLayout.CENTER);
		setContentPane(contentPane);
		addWindowListener(new WindowClosingHandler());
		addFocusListener(new DebugFocusListener("Abstract Application Frame"));
	}

	/* ------------------------------------------------------- */
	/* Menu and ToolBar creation */
	/* ------------------------------------------------------- */
	/**
	 * Create an application's menu bar. Every application menu bar contains a
	 * file menu as its first entry, followed by some application specific
	 * menus, followed by an options menu, a system properties info menu, and a
	 * help menu.
	 * 
	 * @return the ready-to-add menu bar for the application frame.
	 */
	protected JMenuBar getApplicationMenuBar() {
		JMenuItem menuItem = null;
		JMenuBar menuBar;
		menuBar = new JMenuBar();
		menuBar.add(getFileMenu());
		JMenu[] appMenu = getApplicationMenus();
		for (int i = 0; i < appMenu.length; i++) {
			menuBar.add(appMenu[i]);
		}
		menuBar.add(getOptionsMenu());
		JMenu infoMenu = new SystemInfoMenu(this);
		JMenuItem[] infoItem = getApplicationInfoItems(this);
		if (infoItem != null) {
			for (int i = 0; i < infoItem.length; i++)
				infoMenu.add(infoItem[i]);
		}
		menuBar.add(infoMenu);
		menuBar.add(getHelpMenu());
		return menuBar;
	}

	/**
	 * Create an application's tool bar and menu action entries. This method is
	 * separated because it might be necessary to create the action entries
	 * before the menu and tool bar can be populated.
	 */
	abstract protected void createActions();

	/**
	 * Get an application's file menu.
	 * 
	 * @return the file menu populated by the application.
	 */
	abstract protected JMenu getFileMenu();

	/**
	 * Get an application's special menus.
	 * 
	 * @return an array of menus which constitute the application specific
	 *         menus.
	 */
	abstract protected JMenu[] getApplicationMenus();

	/**
	 * Get an application's special info menu items.
	 * 
	 * @return an array of menu items which constitute the application specific
	 *         info menu items.
	 */
	protected JMenuItem[] getApplicationInfoItems(Frame parent) {
		return null;
	}

	/**
	 * Get an application's options menus.
	 * 
	 * @return a menu containing the application's options submenus.
	 */
	abstract protected JMenu getOptionsMenu();

	/**
	 * Get an application's help menus.
	 * 
	 * @return a menu containing the application's help submenus.
	 */
	abstract protected JMenu getHelpMenu();

	/**
	 * Create an application's tool bar.
	 * 
	 * @return a ready-to-add tool bar for the application frame.
	 */
	protected JToolBar getApplicationToolBar() {
		JButton button = null;
		JToolBar toolBar = new JToolBar();
		Color darkerBG = new Color(191, 187, 180);
		Action[] actions = getToolBarActions();
		for (int i = 0; i < actions.length; i++) {
			if (actions[i] == null) {
				toolBar.addSeparator();
			} else {
				button = new JButton(actions[i]);
				if (button.getIcon() != null) {
					button.setText(""); // an icon-only button
				}
				// button.setBackground(darkerBG);
				button.addFocusListener(new DebugFocusListener(
						"Abstract Application Frame Toolbar Button"));
				toolBar.add(button);
			}
		}
		return toolBar;
	}

	/**
	 * Get an application's tool bar actions.
	 * 
	 * @return an array of actions which are intended to be listed in the
	 *         application's tool bar.
	 */
	abstract protected Action[] getToolBarActions();

	/**
	 * Get an application's main content panel.
	 * 
	 * @return a panel containing the application's main content.
	 */
	abstract protected JPanel getApplicationPanel();
	/**
	 * A subclass which handles window closing events. The main window is closed
	 * only if the application's mayExit() method returns true.
	 */
	public class WindowClosingHandler extends WindowAdapter {
		public void windowClosing(WindowEvent evt) {
			if (mayExit()) {
				evt.getWindow().dispose();
				System.exit(0);
			}
		}
	}

	/**
	 * Ask the application whether it is allowed to close the application frame
	 * and shut down the program.
	 * 
	 * @return true if it is possible to exit the program without loosing
	 *         important information.
	 */
	abstract protected boolean mayExit();

	protected void showFocus() {
		KeyboardFocusManager k = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		Window wa = k.getActiveWindow();
		Window wf = k.getFocusedWindow();
		Component c = k.getFocusOwner();
		String s = "Active Window = " + wa.getClass().getName() + "\n"
				+ "Focused Window = " + wf.getClass().getName() + "\n"
				+ "Focus Owner = " + c.getClass().getName();
		System.out.println(s);
	}
}
