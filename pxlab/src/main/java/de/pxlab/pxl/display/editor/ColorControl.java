package de.pxlab.pxl.display.editor;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.awtx.*;
import de.pxlab.pxl.gui.*;
import de.pxlab.pxl.*;
import de.pxlab.pxl.display.VisualGammaFields;

/**
 * Contains multiple color charts, coordinate fields, a color clipboard and
 * color samples.
 * 
 * @version 0.2.0
 */
/*
 * 
 * 2006/11/29 use GridBagLayout
 */
public class ColorControl extends DisplayPropertyControl {
	private CoordinatesPanel coordPanel;
	private ColorClipBoard colorClipBoard;
	private ChartPanel chartPanel;
	private ColorParPanel colorParPanel;
	private ColorParServer colorParServer;
	/** We need these for the gamma parameter diaplog. */
	private VisualGammaFields visualGammaRed = null;
	private VisualGammaFields visualGammaGreen = null;
	private VisualGammaFields visualGammaBlue = null;
	private MenuItem computeGammaPars;
	private MenuItem showGammaPars;
	private MenuItem clearColorClipBoard;
	private boolean whitePointD65 = false;

	public ColorControl(ColorParServer cs, boolean expMode) {
		super("Color");
		// setInsets(new Insets(PXLabGUI.internalBorder,
		// PXLabGUI.internalBorder,
		// PXLabGUI.internalBorder, PXLabGUI.internalBorder));
		Panel p = new Panel(new GridBagLayout());
		if (Debug.layout())
			p.setBackground(Color.magenta);
		addContent(p);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 0, PXLabGUI.internalElementsGroupGap, 0);
		gbc.gridx = 0;
		gbc.weightx = 1.0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		colorParServer = cs;
		colorClipBoard = new ColorClipBoard(colorParServer);
		int[] colorSystems = { PxlColor.CS_Yxy, PxlColor.CS_LabStar,
				PxlColor.CS_JCh, PxlColor.CS_LuvStar, PxlColor.CS_Yrb,
				PxlColor.CS_XYZ, PxlColor.CS_RGB, PxlColor.CS_Dev };
		chartPanel = new ChartPanel(colorSystems, colorParServer,
				colorClipBoard);
		p.add(chartPanel, gbc);
		int[] colorCoordSystems = { PxlColor.CS_Yxy, PxlColor.CS_LabStar,
				PxlColor.CS_LabLChStar, PxlColor.CS_JCh, PxlColor.CS_LuvStar,
				PxlColor.CS_LuvLChStar, PxlColor.CS_Yrb, PxlColor.CS_XYZ,
				PxlColor.CS_RGB, PxlColor.CS_Dev };
		coordPanel = new CoordinatesPanel(colorCoordSystems, colorParServer,
				this);
		p.add(coordPanel, gbc);
		p.add(new FramedPanel(colorClipBoard, "Color Clipboard"), gbc);
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weighty = 100.0;
		colorParPanel = new ColorParPanel(colorParServer);
		p.add(new FramedPanel(colorParPanel, "Parameters"), gbc);
		createOptionsMenu();
	}

	/** Configure this panel for the given display. */
	public void configureFor(Display dsp) {
		DisplayElement de = dsp.getInitialDisplayElement();
		colorParServer.setColorPar(this, de.getColorPar());
		colorParPanel.configureFor(dsp);
		getParent().validate();
	}

	public void setVisualGammaDisplays(VisualGammaFields r,
			VisualGammaFields g, VisualGammaFields b) {
		visualGammaRed = r;
		visualGammaGreen = g;
		visualGammaBlue = b;
		computeGammaPars.setEnabled(true);
	}

	public void setWhitePointD65(boolean t) {
		if (t) {
			PxlColor wp = new YxyColor(1.0, 0.3128, 0.3292);
			double L = wp.maxLum();
			wp.setY(L);
			PxlColor.getDeviceTransform().setWhitePoint(wp.getComponents());
		} else {
			double[] R = PxlColor.getDeviceTransform().getRedBasis();
			double[] G = PxlColor.getDeviceTransform().getGreenBasis();
			double[] B = PxlColor.getDeviceTransform().getBlueBasis();
			double[] W = new double[3];
			W[0] = R[0] + G[0] + B[0];
			W[1] = R[1] + G[1] + B[1];
			W[2] = R[2] + G[2] + B[2];
			PxlColor.getDeviceTransform().setWhitePoint(W);
		}
		whitePointD65 = t;
	}

	public boolean getWhitePointD65() {
		return whitePointD65;
	}

	// Return the items which this panel wants to contribute to the
	// single display control panel's popup menu. */
	public MenuItem[] getPopupMenuItems() {
		return (colorClipBoard.getPopupMenuItems());
	}

	// Return the items which this panel wants to contribute to the
	// single display control panel's popup menu. */
	public MenuItem[] getFileMenuItems() {
		return (colorClipBoard.getFileMenuItems());
	}

	public Menu getCoordinatesPanelMenu() {
		return (coordPanel.getOptionsMenu());
	}

	public Menu getOptionsMenu() {
		return (optionsMenu);
	}

	/**
	 * Get the color board of this color control panel. This is called by a
	 * FileMenu object in order to write the color board content to a file.
	 */
	public ColorClipBoard getColorClipBoard() {
		return (colorClipBoard);
	}
	/** The menu for the display options. */
	private Menu optionsMenu;

	/** Create the display options menu. */
	public void createOptionsMenu() {
		CheckboxMenuItem followInvalidColors;
		CheckboxMenuItem showColorClipBoardInCharts;
		CheckboxMenuItem makeWhitePointD65;
		optionsMenu = new Menu(title);
		optionsMenu.add(coordPanel.getOptionsMenu());
		optionsMenu.addSeparator();
		followInvalidColors = new CheckboxMenuItem("Follow invalid colors",
				ColorAdjustmentPanel.getFollow());
		followInvalidColors.addItemListener(new FollowInvalidColorsHandler());
		optionsMenu.add(followInvalidColors);
		makeWhitePointD65 = new CheckboxMenuItem("Set D65 white point",
				getWhitePointD65());
		makeWhitePointD65.addItemListener(new MakeWhitePointD65Handler());
		optionsMenu.add(makeWhitePointD65);
		showColorClipBoardInCharts = new CheckboxMenuItem(
				"Show color board in charts", colorClipBoard.getShowSample());
		showColorClipBoardInCharts
				.addItemListener(new ShowColorClipBoardInChartsHandler());
		optionsMenu.add(showColorClipBoardInCharts);
		clearColorClipBoard = new MenuItem("Clear color board");
		clearColorClipBoard.addActionListener(new ClearColorClipBoardHandler());
		optionsMenu.add(clearColorClipBoard);
		showGammaPars = new MenuItem("Show Current Gamma Parameters");
		showGammaPars.addActionListener(new ShowGammaParsHandler());
		optionsMenu.add(showGammaPars);
		computeGammaPars = new MenuItem("Compute Gamma Parameters");
		computeGammaPars.addActionListener(new ComputeGammaParsHandler());
		optionsMenu.add(computeGammaPars);
		computeGammaPars.setEnabled(false);
	}
	/** The listener for the following invalid colors item. */
	class FollowInvalidColorsHandler implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			ColorAdjustmentPanel.setFollow(((CheckboxMenuItem) e.getSource())
					.getState());
		}
	}
	/** The listener for the following invalid colors item. */
	class MakeWhitePointD65Handler implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			setWhitePointD65(((CheckboxMenuItem) e.getSource()).getState());
		}
	}
	/** The listener for the following invalid colors item. */
	class ShowColorClipBoardInChartsHandler implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			colorClipBoard.setShowSample(((CheckboxMenuItem) e.getSource())
					.getState());
		}
	}
	/** The listener for the following invalid colors item. */
	class ComputeGammaParsHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// System.out.println("ColorControl.ComputeGammaParsHandler.actionPerformed()");
			Dialog dlg = new VisualGammaParsDialog(new Frame(), visualGammaRed,
					visualGammaGreen, visualGammaBlue);
			dlg.show();
		}
	}
	/** The listener for the following invalid colors item. */
	class ShowGammaParsHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// System.out.println("ColorControl.ComputeGammaParsHandler.actionPerformed()");
			Dialog dlg = new ShowGammaParsDialog(new Frame());
			dlg.show();
		}
	}
	/** The listener for clearing the color board. */
	class ClearColorClipBoardHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			colorClipBoard.clear();
			getParent().validate();
		}
	}
	/*
	 * public void paint(Graphics g) {
	 * System.out.println("ColorControl.paint() Panel is " + (isValid()?
	 * "valid": "invalid")); super.paint(g); }
	 * 
	 * 
	 * public void invalidate() {
	 * System.out.println("ColorControl.invalidate()"); super.invalidate(); }
	 */
}
