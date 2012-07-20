package de.pxlab.pxl.gui;

import java.awt.*;

import de.pxlab.gui.*;
import de.pxlab.pxl.*;

/**
 * This class defines color adjustment panels which consist of 3 sliders, one
 * for each color channel.
 * 
 * @version 0.2.1
 * @see ColorAdjustmentChart
 */
/*
 * 02/06/01 added setEnabled()
 * 
 * 2007/01/23 Fixed bug with 3 sliders: Changing the active slider did not
 * preserve the current color coordinates.
 */
public class ColorAdjustmentSliders extends ColorAdjustmentPanel {
	protected Slider slider1, slider2, slider3;

	public ColorAdjustmentSliders(int t, ColorServer ct) {
		super(t, ct);
		setLayout(new GridLayout(1, 0));
	}

	public void setEnabled(boolean e) {
		slider1.setEnabled(e);
		slider2.setEnabled(e);
		slider3.setEnabled(e);
	}

	/**
	 * Set the current slider positions to represent the given color. This
	 * method should only be called in order to completely reset the chart's
	 * display. This is the case when the current coordinates may be completely
	 * invalid or a new current color has been selected.
	 */
	public void setValue(PxlColor c) {
		currentColor = c;
		localColor = c.transform(csType);
		slider1.setValue(localColor[0]);
		slider2.setValue(localColor[1]);
		slider3.setValue(localColor[2]);
	}

	/**
	 * Handles adjustments in the chromaticity chart. The input coordinates are
	 * the chart models' values for the two axes.
	 */
	protected boolean valueChanged(double x, double y, double z) {
		// The return value which tells the slider whether the new
		// value is acceptable or not.
		boolean valid = false;
		// The local coordinates of the new color.
		double[] nc = { x, y, z };
		// System.out.println("Trying to change value to local coordinates = " +
		// Utils.stringOfArray(nc));
		// Set the current color to the new chromaticity coordinates
		currentColor = PxlColor.instance(csType, nc);
		// System.out.println("Checking XYZ color " + currentColor);
		// Check whether the resulting chromaticities are within the
		// range of possible colors.
		if (currentColor.isDisplayable()) {
			// This color is OK.
			// System.out.println("Value Change OK");
			// Main.controlPanel.showStatus("Adjustment OK");
			valid = true;
		} else {
			// The requested color cannot be displayed, so refuse to
			// accept it and restore the previous one.
			// System.out.println("Value change not possible");
			// Main.controlPanel.showStatus("Adjustment impossible");
			currentColor = PxlColor.instance(csType, localColor);
			valid = false;
		}
		if (valid) {
			// Finally tell the color manager that the current color has
			// changed.
			colorServer.colorAdjusted(this, (PxlColor) currentColor);
			localColor = currentColor.transform(csType);
			// System.out.println(x + " " + y);
		}
		return (valid);
	}

	/**
	 * The ColorChangeListener implementation simply sets the current color
	 * coordinates.
	 */
	public void colorChanged(ColorChangeEvent e) {
		if (e.getSource() == (Object) this) {
			// This comes from one of our own sliders, update has already been
			// done
		} else {
			int id = e.getID();
			if (id == ColorChangeEvent.COLOR_SET_PASSIVE) {
				slider1.setEnabled(false);
				slider2.setEnabled(false);
				slider3.setEnabled(false);
			} else if (id == ColorChangeEvent.COLOR_SET) {
				slider1.setEnabled(true);
				slider2.setEnabled(true);
				slider3.setEnabled(true);
			}
			// This comes from someone else
			setValue(e.getColor());
		}
		// System.out.println("Color adjusted: " + currentColor);
	}
	public class Slider1Handler implements AxisListener {
		/**
		 * Handles adjustments on the luminance scale. The input is the first
		 * coordinate of this panel's color system.
		 */
		public boolean axisValueChanged(double x) {
			return (valueChanged(x, localColor[1], localColor[2]));
		}
	}
	public class Slider2Handler implements AxisListener {
		/**
		 * Handles adjustments on the luminance scale. The input is the first
		 * coordinate of this panel's color system.
		 */
		public boolean axisValueChanged(double x) {
			return (valueChanged(localColor[0], x, localColor[2]));
		}
	}
	public class Slider3Handler implements AxisListener {
		/**
		 * Handles adjustments on the luminance scale. The input is the first
		 * coordinate of this panel's color system.
		 */
		public boolean axisValueChanged(double x) {
			return (valueChanged(localColor[0], localColor[1], x));
		}
	}
}
