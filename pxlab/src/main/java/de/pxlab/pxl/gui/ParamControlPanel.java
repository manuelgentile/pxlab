package de.pxlab.pxl.gui;

import java.awt.*;

import java.util.ArrayList;
import de.pxlab.util.StringExt;
import de.pxlab.awtx.*;
import de.pxlab.pxl.*;

/**
 * Creates a panel containing control tools for setting experimental parameters
 * of a Display object.
 * 
 * @author H. Irtel
 * @version 0.4.0
 */
/*
 * 07/28/01 prepared for different types of parameters
 * 
 * 07/17/00
 * 
 * 02/15/02 introduced closeOK() method
 * 
 * 09/08/02 handle integer and double ranges separately
 * 
 * 2006/11/24 don't show some display parameters in non-experiments mode.
 */
public class ParamControlPanel extends InsetPanel implements ExParTypeCodes {
	private ExParControlPanel[] exParControlPanel;
	private Container container;
	private Component displayPanel;
	private boolean expMode;
	private int n = 0;

	/**
	 * Create an empty ParamControlPanel which is contained in the given
	 * Container object and sends its update messages to the given Component
	 * object. The panel must be configured for a Display object before it can
	 * be used.
	 * 
	 * @param container
	 *            this is the Container object which contains this
	 *            ParamControlPanel object. It is needed here since a
	 *            reconfiguration of the ParamControlPanel requires a
	 *            revalidation of the parent's layout.
	 * @param displayPanel
	 *            this is the Component which ist the target of the geometric
	 *            adjustments. It is transferred to all ExParControlPanel
	 *            objects of this ParamControlPanel object since these send a
	 *            repaint message to this Component whenever any of the
	 *            parameters has changed.
	 * @param expMode
	 *            if true then we are in experimental display editing mode. If
	 *            false we are in demonstration mode. Display parameters which
	 *            are not meanigful in any of the two modes may be suppressed.
	 */
	public ParamControlPanel(Container container, Component displayPanel,
			boolean expMode) {
		setInsets(new Insets(PXLabGUI.internalBorder, PXLabGUI.internalBorder,
				PXLabGUI.internalBorder, PXLabGUI.internalBorder));
		this.container = container;
		this.displayPanel = displayPanel;
		this.expMode = expMode;
		if (Debug.layout())
			setBackground(Color.green);
		// System.out.println("ParamControlPanel displayPanel = " +
		// displayPanel);
	}

	/** Remove all components from this panel. */
	private void clearPanel() {
		// System.out.println("ParamControlPanel.clearPanel()");
		for (int i = 0; i < n; i++) {
			if (exParControlPanel[i] != null)
				exParControlPanel[i].clearPanel();
		}
		removeAll();
		exParControlPanel = null;
		n = 0;
	}

	/**
	 * Tell this panel that it will be closed immediately with an OK button.
	 */
	public void closeOK() {
		// System.out.println("ParamControlPanel.closeOK(). n = " + n);
		for (int i = 0; i < n; i++) {
			exParControlPanel[i].closeOK();
		}
	}

	/**
	 * Tell this panel that it will be closed immediately with a Cancel button.
	 */
	public void closeCancel() {
		for (int i = 0; i < n; i++) {
			exParControlPanel[i].closeCancel();
		}
	}

	/**
	 * Configure this ParamControlPanel for the given Display object by creating
	 * a ExParControlPanel object for each geometry ExPar object of the Display.
	 * 
	 * @param display
	 *            the Display object whose ExPar fields are to be used.
	 * @param p
	 *            the array of experimental parameters of this Display.
	 */
	public void configureFor(Display display, ExParDescriptor[] p) {
		// System.out.println("ParamControlPanel.configureFor() " +
		// display.getTitle());
		clearPanel();
		GridBagLayout gbl = new GridBagLayout();
		setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		Component rc = null;
		if (p != null) {
			int m = p.length;
			exParControlPanel = new ExParControlPanel[m];
			SingleParamControlPanel cp = null;
			for (int i = 0; i < m; i++) {
				// System.out.println("ParamControlPanel.configureFor() " +
				// display.getTitle() + "." + p[i].getName());
				// System.out.println("ParamControlPanel.configureFor() " +
				// p[i].getName());
				ExPar xp = p[i].getValue();
				if (!expMode && hideForDemos(p[i].getName()))
					continue;
				Class typeClass = xp.getTypeClass();
				if (typeClass == null) {
					int type = xp.getType();
					switch (type) {
					case COLOR:
						cp = new ColorParamControlPanel(displayPanel, display,
								p[i]);
						break;
					case DEPCOLOR:
					case RTDATA:
						break;
					case FLAG:
						cp = new FlagParamControlPanel(displayPanel, display,
								p[i]);
						break;
					// EXPFACTOR type parameters may not be edited here !
					// case EXPFACTOR:
					case FONTNAME:
					case EXPARNAME:
					case SPECTRUM:
						cp = new StringParamControlPanel(displayPanel, display,
								p[i], false);
						break;
					case STRING:
						cp = new StringParamControlPanel(displayPanel, display,
								p[i], false);
						break;
					case SCREENPOS:
					case VERSCREENPOS:
						cp = new IntegerRangeParamControlPanel(displayPanel,
								display, p[i], MagicNumber.screenTopY,
								MagicNumber.screenBottomY);
						break;
					case HORSCREENPOS:
						cp = new IntegerRangeParamControlPanel(displayPanel,
								display, p[i], MagicNumber.screenLeftX,
								MagicNumber.screenRightX);
						break;
					case PROPORTION:
						cp = new NumericalRangeParamControlPanel(displayPanel,
								display, p[i], 0.0, 1.0);
						break;
					case SIGNED_PROPORTION:
						cp = new NumericalRangeParamControlPanel(displayPanel,
								display, p[i], -1.0, 1.0);
						break;
					case SCREENSIZE:
					case VERSCREENSIZE:
						cp = new IntegerRangeParamControlPanel(displayPanel,
								display, p[i], 0, MagicNumber.screenHeight);
						break;
					case HORSCREENSIZE:
						cp = new IntegerRangeParamControlPanel(displayPanel,
								display, p[i], 0, MagicNumber.screenWidth);
						break;
					case DURATION:
						cp = new IntegerRangeParamControlPanel(displayPanel,
								display, p[i]);
						break;
					case SMALL_INT:
						cp = new IntegerRangeParamControlPanel(displayPanel,
								display, p[i], 0, 50);
						break;
					case INT_8_BIT:
						cp = new IntegerRangeParamControlPanel(displayPanel,
								display, p[i], 0, 255);
						break;
					case SMALL_SCREENSIZE:
						cp = new IntegerRangeParamControlPanel(displayPanel,
								display, p[i], 0, 30);
						break;
					case SMALL_DOUBLE:
					case VISUAL_ANGLE:
						cp = new NumericalRangeParamControlPanel(displayPanel,
								display, p[i], 0.0, 50.0);
						break;
					case SMALL_VISUAL_ANGLE:
						cp = new NumericalRangeParamControlPanel(displayPanel,
								display, p[i], 0.0, 4.0);
						break;
					case ANGLE:
						cp = new NumericalRangeParamControlPanel(displayPanel,
								display, p[i], 0.0, 360.0);
						break;
					case KEYCODE:
						cp = new IntegerRangeParamControlPanel(displayPanel,
								display, p[i], 0, 255);
						break;
					case DOUBLE:
						cp = new NumericalRangeParamControlPanel(displayPanel,
								display, p[i]);
						break;
					case INTEGER:
						cp = new IntegerRangeParamControlPanel(displayPanel,
								display, p[i]);
						break;
					default:
						System.out.println("ExPar type can't be handled: "
								+ type + " of parameter " + p[i].getName());
						break;
					}
				} else {
					String typeClassName = typeClass.getName();
					// System.out.println("ParamControlPanel.configureFor() " +
					// typeClassName);
					if (typeClassName.endsWith("Codes")) {
						cp = new CodeParamControlPanel(displayPanel, display,
								p[i], typeClass,
								!typeClassName.endsWith("BitCodes"));
					}
				}
				if (cp != null) {
					exParControlPanel[i] = new ExParControlPanel(displayPanel,
							display, p[i], cp);
					add(rc = exParControlPanel[i], gbc);
					n++;
				}
			}
		}
		// Correct layout for the last component added.
		if (rc != null) {
			gbc = gbl.getConstraints(rc);
			gbc.weighty = 100.0;
			gbl.setConstraints(rc, gbc);
		}
		container.validate();
		container.repaint();
	}
	private static String[] parsToHide = { "Execute", "Overlay", "Screen",
			"JustInTime", "Timer", "Duration" };

	private boolean hideForDemos(String n) {
		// System.out.println("ParamControlPanel.hideForDemos(): " + n);
		return StringExt.indexOf(n, parsToHide) >= 0;
	}
}
