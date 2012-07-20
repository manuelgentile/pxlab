package de.pxlab.pxl.gui;

import java.awt.*;

import de.pxlab.gui.*;
import de.pxlab.pxl.*;

/**
 * Create an adjustment panel with linear RGB sliders.
 * 
 * @author H. Irtel
 * @version 0.2.1
 */
/*
 * 
 * 03/09/00
 */
public class RGBAdjustmentSliders extends ColorAdjustmentSliders {
	/** Create an adjustment panel with linear RGB sliders. */
	public RGBAdjustmentSliders(ColorServer ct) {
		super(PxlColor.CS_RGB, ct);
		AxisModel model1 = new LinearAxisModel(0.0, 1.0, 0.0);
		slider1 = new Slider(Slider.VERTICAL, model1, 9, 0);
		slider1.setTrackColor(Color.red);
		slider1.setPreferredSpacing(cellHeight, 240, cellHeight);
		slider1.setLabelPrecision(1);
		slider1.setAxisListener(new ColorAdjustmentSliders.Slider1Handler());
		AxisModel model2 = new LinearAxisModel(0.0, 1.0, 0.0);
		slider2 = new Slider(Slider.VERTICAL, model2, 9, 0);
		slider2.setTrackColor(Color.green);
		slider2.setPreferredSpacing(cellHeight, 240, cellHeight);
		slider2.setLabelPrecision(1);
		slider2.setAxisListener(new ColorAdjustmentSliders.Slider2Handler());
		AxisModel model3 = new LinearAxisModel(0.0, 1.0, 0.0);
		slider3 = new Slider(Slider.VERTICAL, model3, 9, 0);
		slider3.setTrackColor(Color.blue);
		slider3.setPreferredSpacing(cellHeight, 240, cellHeight);
		slider3.setLabelPrecision(1);
		slider3.setAxisListener(new ColorAdjustmentSliders.Slider3Handler());
		// Add the sliders to this panel.
		createLayout(slider1, slider2, slider3);
	}
}
