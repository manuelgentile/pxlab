package de.pxlab.pxl.gui;

import java.awt.*;
import java.util.*;

import de.pxlab.gui.*;
import de.pxlab.pxl.*;

/**
 * Create a CIE Yxy chromaticity chart.
 * 
 * @author H. Irtel
 * @version 0.2.1
 */
/*
 * 
 * 03/09/00
 */
public class YxyAdjustmentChart extends ColorAdjustmentChart {
	/**
	 * Create a chromaticity and luminance adjustment chart for the Yxy-color
	 * space.
	 */
	public YxyAdjustmentChart(ColorServer ct) {
		super(PxlColor.CS_Yxy, ct);
		// Create a xy-chromaticity diagram
		chromaChart = new Chart(new LinearAxisModel(0.1, 0.7, 0.30), 7,
				new LinearAxisModel(0.0, 0.7, 0.30), 8, 1.0, this);
		chromaChart.setValidRegion(createValidRegion(1.0));
		chromaChart.setPreferredHorizontalSpacing(5 * cellWidth, 240,
				3 * cellWidth);
		chromaChart.setPreferredVerticalSpacing(1 * cellWidth, 280,
				5 * cellHeight / 4);
		// Create a luminance scale with 1/2-power model
		AxisModel lumModel = new PowerAxisModel(1.0 / 2.0, 0.0, 100.0, 0.0);
		lumSlider = new Slider(Slider.HORIZONTAL, lumModel, 9, 0);
		lumSlider.setPreferredSpacing(5 * cellWidth, 240, 3 * cellWidth);
		lumSlider.setAxisListener(this);
		createLayout(lumSlider, chromaChart);
	}
}
