package de.pxlab.pxl.gui;

import java.awt.*;

import de.pxlab.gui.*;
import de.pxlab.pxl.*;

/**
 * Create a CIE L*a*b* chromaticity chart.
 * 
 * @author H. Irtel
 * @version 0.2.1
 */
/*
 * 
 * 03/09/00
 */
public class LabAdjustmentChart extends ColorAdjustmentChart {
	/**
	 * Create a chromaticity and luminance adjustment chart for the
	 * LabStar-color space.
	 */
	public LabAdjustmentChart(ColorServer ct) {
		super(PxlColor.CS_LabStar, ct);
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
		createLayout(lumSlider, chromaChart);
	}
}
