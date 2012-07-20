package de.pxlab.pxl.gui;

import java.awt.*;

import de.pxlab.gui.*;
import de.pxlab.pxl.*;

/**
 * Create a CIE L*u*v* chromaticity chart.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 
 * 02/13/00
 */
public class LuvAdjustmentChart extends ColorAdjustmentChart {
	/**
	 * Create a chromaticity and luminance adjustment chart for the
	 * LuvStar-color space.
	 */
	public LuvAdjustmentChart(ColorServer ct) {
		super(PxlColor.CS_LuvStar, ct);
		// Create a uv-chromaticity diagram
		chromaChart = new Chart(new LinearAxisModel(-100.0, 180.0, 0.0), 8,
				new LinearAxisModel(-140.0, 100.0, 0.0), 7, 1.0, this);
		chromaChart.setValidRegion(createValidRegion(1.0));
		chromaChart.setPreferredHorizontalSpacing(5 * cellWidth, 245,
				3 * cellWidth);
		chromaChart.setPreferredVerticalSpacing(1 * cellWidth, 210,
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
