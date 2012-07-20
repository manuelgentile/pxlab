package de.pxlab.pxl.gui;

import java.awt.*;

import de.pxlab.gui.*;
import de.pxlab.pxl.*;

/**
 * Create an adjustment panel with linear XYZ sliders.
 * 
 * @author H. Irtel
 * @version 0.2.1
 */
/*
 * 
 * 03/09/00
 */
public class XYZAdjustmentSliders extends ColorAdjustmentSliders {
	/** Create an adjustment panel with linear XYZ sliders. */
	public XYZAdjustmentSliders(ColorServer ct) {
		super(PxlColor.CS_XYZ, ct);
		double[] m = PxlColor.systemColor(PxlColor.WHITE).getComponents();
		double mx = Math.max(m[0], Math.max(m[1], m[2]));
		int n = (int) (mx + 19.0) / 20;
		mx = (double) (20 * n);
		AxisModel model1 = new LinearAxisModel(0.0, mx, 0.0);
		slider1 = new Slider(Slider.VERTICAL, model1, (n + 1), 0);
		slider1.setTrackColor(Color.red);
		slider1.setPreferredSpacing(cellHeight, 240, cellHeight);
		slider1.setAxisListener(new ColorAdjustmentSliders.Slider1Handler());
		AxisModel model2 = new LinearAxisModel(0.0, mx, 0.0);
		slider2 = new Slider(Slider.VERTICAL, model2, n + 1, 0);
		slider2.setTrackColor(Color.green);
		slider2.setPreferredSpacing(cellHeight, 240, cellHeight);
		slider2.setAxisListener(new ColorAdjustmentSliders.Slider2Handler());
		AxisModel model3 = new LinearAxisModel(0.0, mx, 0.0);
		slider3 = new Slider(Slider.VERTICAL, model3, n + 1, 0);
		slider3.setTrackColor(Color.blue);
		slider3.setPreferredSpacing(cellHeight, 240, cellHeight);
		slider3.setAxisListener(new ColorAdjustmentSliders.Slider3Handler());
		// Add the sliders to this panel.
		createLayout(slider1, slider2, slider3);
	}
}
