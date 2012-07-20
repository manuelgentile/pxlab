package de.pxlab.pxl.gui;

import java.awt.*;

import de.pxlab.gui.*;
import de.pxlab.pxl.*;

/**
 * Create a CIE CAM 97 chromaticity chart.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 
 * 06/25/00
 */
public class JChAdjustmentChart extends ColorAdjustmentChart {
	/**
	 * Create a chromaticity and luminance adjustment chart for the
	 * CIECAM97-color space.
	 */
	public JChAdjustmentChart(ColorServer ct) {
		super(PxlColor.CS_JCh, ct);
		// Create a ab-chromaticity diagram
		chromaChart = new Chart(new LinearAxisModel(-120.0, 120.0, 0.0), 7,
				new LinearAxisModel(-120.0, 120.0, 0.0), 7, 1.0, this);
		chromaChart.setValidRegion(createValidRegion(1.0));
		chromaChart.setPreferredHorizontalSpacing(5 * cellWidth, 240,
				3 * cellWidth);
		chromaChart.setPreferredVerticalSpacing(1 * cellWidth, 240,
				5 * cellHeight / 4);
		chromaChart.setLabelPrecision(0);
		// Create a linear scale for the L* axis
		AxisModel lumModel = new LinearAxisModel(0.0, 100.0, 0.0);
		lumSlider = new Slider(Slider.HORIZONTAL, lumModel, 9, 0);
		lumSlider.setPreferredSpacing(7 * cellWidth, 240, 3 * cellWidth);
		lumSlider.setAxisListener(this);
		CIECAM97.setScene(new CIECAM97.Scene(31.83, 20.0, PxlColor
				.getCIEWhitePointXYZ()));
		CIECAM97.setView(CIECAM97.View.AverageSurround);
		createLayout(lumSlider, chromaChart);
	}
}
