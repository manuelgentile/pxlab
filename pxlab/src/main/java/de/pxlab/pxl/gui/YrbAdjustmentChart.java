package de.pxlab.pxl.gui;

import java.awt.*;
import java.util.*;

import de.pxlab.gui.*;
import de.pxlab.pxl.*;

/**
 * Create a Boynton MacLeod chromaticity chart.
 * 
 * @author H. Irtel
 * @version 0.2.1
 */
/*
 * 
 * 03/16/00
 */
public class YrbAdjustmentChart extends ColorAdjustmentChart {
	/**
	 * Create a chromaticity and luminance adjustment chart for the
	 * Boynton-MacLeod color space.
	 */
	public YrbAdjustmentChart(ColorServer ct) {
		super(PxlColor.CS_Yrb, ct);
		// Create a xy-chromaticity diagram
		chromaChart = new Chart(new LinearAxisModel(0.4, 0.9, 0.55), 6,
				new LinearAxisModel(0.0, 1.8, 0.10), 7, 1.0, this);
		chromaChart.setValidRegion(createValidRegion(1.0));
		chromaChart.setPreferredHorizontalSpacing(5 * cellWidth, 240,
				3 * cellWidth);
		chromaChart.setPreferredVerticalSpacing(1 * cellWidth, 288,
				5 * cellHeight / 4);
		// Create a luminance scale with 1/2-power model
		AxisModel lumModel = new PowerAxisModel(1.0 / 2.0, 0.0, 100.0, 0.0);
		lumSlider = new Slider(Slider.HORIZONTAL, lumModel, 9, 0);
		lumSlider.setPreferredSpacing(5 * cellWidth, 240, 3 * cellWidth);
		lumSlider.setAxisListener(this);
		createLayout(lumSlider, chromaChart);
	}
}
