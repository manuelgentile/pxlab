package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

import de.pxlab.pxl.*;
import de.pxlab.util.StringExt;

/**
 * The color board is a clipboard for colors which works like a stack. One can
 * copy the current color to the color board and paste it back to any selected
 * object. One can also copy a single color from the board to multiple objects
 * in sequence by using the left mouse button within the ColorClipBoard. The
 * ColorClipBoard itself does not activate the PopupMenu.
 * 
 * @version 0.4.0
 * @see de.pxlab.pxl.ColorServer
 * @see de.pxlab.pxl.ColorChangeEvent
 */
/*
 * 02/07/01 added save menu item and handler
 * 
 * 12/10/03 addColor()
 * 
 * 2006/11/28 changed to Swing
 * 
 * 2007/10/09 allow import of color board and export of CIELab values
 */
public class ColorClipBoard extends ColorButtonPanel {
	private ColorServer colorServer;
	private ArrayList colorClipBoard = new ArrayList();
	private boolean showSample = false;
	private ArrayList colorClipBoardListeners = new ArrayList();
	private MenuItem copyMenuItem;
	private MenuItem pasteMenuItem;
	private Menu loadMenuItem;
	private MenuItem loadYxyMenuItem;
	private MenuItem loadCIELabMenuItem;
	private MenuItem loadCIELabLChMenuItem;
	private Menu saveMenuItem;
	private MenuItem saveYxyMenuItem;
	private MenuItem saveCIELabMenuItem;
	private MenuItem saveCIELabLChMenuItem;

	/**
	 * Create the color clipboard. The ColorClipBoard adds two entries to the
	 * PopupMenu given as an argument. One is for copying the ColorServer's
	 * active color to the clipboard and one is for pasting the most recently
	 * copied color from the clipboard back to the ColorServer's active color.
	 */
	public ColorClipBoard(ColorServer cs) {
		super(2);
		colorServer = cs;
		createMenuItems();
	}

	public Dimension getPreferredSize() {
		return colorClipBoard.isEmpty() ? getPreferredButtonSize() : super
				.getPreferredSize();
	}

	private void createMenuItems() {
		MenuHandler pmh = new MenuHandler();
		copyMenuItem = new MenuItem("Copy to Color Board");
		copyMenuItem.addActionListener(pmh);
		pasteMenuItem = new MenuItem("Paste from Color Board");
		pasteMenuItem.addActionListener(pmh);
		loadMenuItem = new Menu("Import Color Board");
		loadYxyMenuItem = new MenuItem("Yxy");
		loadYxyMenuItem.addActionListener(pmh);
		loadMenuItem.add(loadYxyMenuItem);
		loadCIELabMenuItem = new MenuItem("CIELab");
		loadCIELabMenuItem.addActionListener(pmh);
		loadMenuItem.add(loadCIELabMenuItem);
		loadCIELabLChMenuItem = new MenuItem("CIELab LCh");
		loadCIELabLChMenuItem.addActionListener(pmh);
		loadMenuItem.add(loadCIELabLChMenuItem);
		saveMenuItem = new Menu("Export Color Board");
		saveYxyMenuItem = new MenuItem("Yxy");
		saveYxyMenuItem.addActionListener(pmh);
		saveMenuItem.add(saveYxyMenuItem);
		saveCIELabMenuItem = new MenuItem("CIELab");
		saveCIELabMenuItem.addActionListener(pmh);
		saveMenuItem.add(saveCIELabMenuItem);
		saveCIELabLChMenuItem = new MenuItem("CIELab LCh");
		saveCIELabLChMenuItem.addActionListener(pmh);
		saveMenuItem.add(saveCIELabLChMenuItem);
	}

	public MenuItem[] getPopupMenuItems() {
		MenuItem[] mi = { copyMenuItem, pasteMenuItem };
		return (mi);
	}

	public MenuItem[] getFileMenuItems() {
		MenuItem[] mi = { loadMenuItem, saveMenuItem };
		return (mi);
	}

	/**
	 * Tell the color board whether its content should be sent to the
	 * chromaticity charts for being displayed in the chart.
	 */
	public void setShowSample(boolean a) {
		showSample = a;
		fireColorClipBoardEvent();
		// System.out.println("Showing color board: " + showSample);
	}

	public boolean getShowSample() {
		return (showSample);
	}

	/** Write the current color board content to a file. */
	public void save(int cs, String dir, String fn) {
		if (!colorClipBoard.isEmpty()) {
			try {
				BufferedWriter fs = new BufferedWriter(new FileWriter(new File(
						dir, fn)));
				// System.out.println("Writing color board to file " + fn);
				for (int i = 0; i < colorClipBoard.size(); i++) {
					if (cs == PxlColor.CS_Yxy)
						fs.write(((PxlColor) colorClipBoard.get(i)).toString());
					else if (cs == PxlColor.CS_LabStar)
						fs.write(StringExt.valueOf(((PxlColor) colorClipBoard
								.get(i)).toLabStar()));
					else if (cs == PxlColor.CS_LabLChStar)
						fs.write(StringExt.valueOf(((PxlColor) colorClipBoard
								.get(i)).toLabLChStar()));
					fs.newLine();
				}
				fs.close();
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}
	}

	/** Load the content of a color file into the color board. */
	public void load(int cs, String dir, String fn) {
		String[] c = FileBase.loadStrings(dir, fn);
		if (c != null) {
			int n = c.length;
			for (int i = 0; i < n; i++) {
				double[] p = StringExt.doubleArray(c[i], 0.0);
				addColor(PxlColor.instance(cs, p));
			}
		}
	}

	/**
	 * Send a ColorClipBoardSet message to all registered listeners. This tells
	 * the listeners that the content of the board has changed and they should
	 * update their display. Usually only color charts which are able to show
	 * multiple colors listen to ColorClipBoardSetEvents.
	 */
	private void fireColorClipBoardEvent() {
		if (colorClipBoardListeners.isEmpty())
			return;
		for (int i = 0; i < colorClipBoardListeners.size(); i++) {
			((ColorClipBoardListener) (colorClipBoardListeners.get(i)))
					.colorClipBoardSet(showSample, colorClipBoard);
		}
	}

	public void addColorClipBoardListener(ColorClipBoardListener a) {
		colorClipBoardListeners.add(a);
	}

	public void removeColorClipBoardListener(ColorClipBoardListener a) {
		colorClipBoardListeners.remove(colorClipBoardListeners.indexOf(a));
	}

	public void removeAllColorClipBoardListeners() {
		colorClipBoardListeners.clear();
	}

	private void addColor(PxlColor c) {
		colorClipBoard.add(c);
		addButton(c, c.toString(), false);
		if (showSample)
			fireColorClipBoardEvent();
		getParent().getParent().validate();
		getParent().getParent().repaint();
	}

	private void removeColor() {
		int i = colorClipBoard.size() - 1;
		colorClipBoard.remove(i);
		remove(i);
		if (showSample)
			fireColorClipBoardEvent();
	}

	public void clear() {
		colorClipBoard.clear();
		removeAll();
		if (showSample)
			fireColorClipBoardEvent();
	}

	public void actionPerformed(ActionEvent e) {
		int i = indexOfButton(e.getSource());
		if (i >= 0) {
			colorServer.colorAdjusted(this, (PxlColor) colorClipBoard.get(i));
		}
	}
	/** This subclass handles copy/paste-commands from the popup menu. */
	class MenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			MenuItem s = (MenuItem) e.getSource();
			if (s == copyMenuItem) {
				// System.out.println("Copy: colorClipBoard.size() = " +
				// colorClipBoard.size());
				addColor(colorServer.getActiveColor());
				/* getParent(). */getParent().validate();
			} else if (s == pasteMenuItem) {
				if (!colorClipBoard.isEmpty()) {
					// System.out.println("ColorClipBoard.actionPerformed() Copy");
					int i = colorClipBoard.size() - 1;
					colorServer.colorAdjusted(this,
							(PxlColor) colorClipBoard.get(i));
					removeColor();
					/* getParent(). */getParent().validate();
				} else {
					// System.out.println("ColorClipBoard.actionPerformed() Copy: colorClipBoard is empty");
				}
			} else if ((s == loadYxyMenuItem) || (s == loadCIELabMenuItem)
					|| (s == loadCIELabLChMenuItem)) {
				FileDialog fd = new FileDialog(new Frame(),
						loadMenuItem.getLabel(), FileDialog.LOAD);
				fd.show();
				String fn = fd.getFile();
				if (fn != null) {
					int cs = PxlColor.CS_Yxy;
					if (s == loadCIELabMenuItem)
						cs = PxlColor.CS_LabStar;
					else if (s == loadCIELabLChMenuItem)
						cs = PxlColor.CS_LabLChStar;
					load(cs, fd.getDirectory(), fn);
				}
				fd.dispose();
			} else if ((s == saveYxyMenuItem) || (s == saveCIELabMenuItem)
					|| (s == saveCIELabLChMenuItem)) {
				FileDialog fd = new FileDialog(new Frame(), s.getLabel(),
						FileDialog.SAVE);
				fd.show();
				String fn = fd.getFile();
				if (fn != null) {
					int cs = PxlColor.CS_Yxy;
					if (s == saveCIELabMenuItem)
						cs = PxlColor.CS_LabStar;
					else if (s == saveCIELabLChMenuItem)
						cs = PxlColor.CS_LabLChStar;
					save(cs, fd.getDirectory(), fn);
				}
				fd.dispose();
			}
			// System.out.println("  colorClipBoard.size() = " +
			// colorClipBoard.size());
		}
	}
	/*
	 * public void paint(Graphics g) {
	 * System.out.println("ColorClipBoard.paint() Panel is " + (isValid()?
	 * "valid": "invalid")); super.paint(g); }
	 */
}
