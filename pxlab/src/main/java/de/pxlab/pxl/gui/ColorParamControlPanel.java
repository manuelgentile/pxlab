package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.awtx.*;
import de.pxlab.pxl.*;

/**
 * A control panel for color parameters.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 09/17/02
 */
public class ColorParamControlPanel extends SingleParamControlPanel /*
																	 * implements
																	 * ColorChangeListener
																	 */{
	private ColorParServer colorParServer;
	private ChartPanel chartPanel;
	private CoordinatesPanel coordPanel;

	/**
	 * Create a ColorParamControlPanel which sends its update messages to the
	 * given Component object and can adjust the ExPar object of the given
	 * Display.
	 * 
	 * @param dspp
	 *            this is the Component which shows the Display object and which
	 *            receives the repaint() messages whenver a parameter value
	 *            changes. This ExParControlPanel object sends a repaint message
	 *            to this Component whenever the parameter has changed.
	 * @param dsp
	 *            the Display object whose parameter is controlled here.
	 * @param exd
	 *            the ExParDescriptor object which describes the ExPar parameter
	 *            to be controlled.
	 */
	public ColorParamControlPanel(Component dspp, Display dsp,
			ExParDescriptor exd) {
		super(dspp, dsp, exd);
		setLayout(new BorderLayout(10, 10));
		// Create a ColorParServer and set its experimental parameter to this
		// panel's color parameter.
		colorParServer = new ColorParServer();
		colorParServer.setColorPar(this, exPar);
		// colorParServer.addColorChangeListener(this);
		int[] colorSystems = { PxlColor.CS_Yxy, PxlColor.CS_LabStar,
				PxlColor.CS_Yrb, PxlColor.CS_Dev };
		chartPanel = new ChartPanel(colorSystems, colorParServer, null /*
																		 * no
																		 * colorClipBoard
																		 */);
		add(chartPanel, BorderLayout.CENTER);
		coordPanel = new CoordinatesPanel(colorSystems, colorParServer, this /*
																			 * will
																			 * be
																			 * the
																			 * Container
																			 */);
		add(coordPanel, BorderLayout.SOUTH);
		updateControl(exParValue);
	}

	/**
	 * Implements the ExParControlPanel: Update the visual control to new
	 * parameter values.
	 */
	public void updateControl(ExParValue v) {
		colorParServer.colorAdjusted(this, v.getPxlColor());
	}
}
