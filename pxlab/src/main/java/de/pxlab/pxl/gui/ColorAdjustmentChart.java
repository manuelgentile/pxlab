package de.pxlab.pxl.gui;

import java.awt.*;

import de.pxlab.gui.*;
import java.util.ArrayList;
import de.pxlab.pxl.*;

/**
 * This class defines color adjustment panels which consist of something like a
 * chromaticity chart and a luminance slider.
 * 
 * @author H. Irtel
 * @version 0.2.3
 * @see ColorAdjustmentSliders
 */
/*
 * 01/19/01 Set lowest luminance limit to PxlColor.minLum 02/06/01 added
 * setEnabled()
 */
public class ColorAdjustmentChart extends ColorAdjustmentPanel implements
		AxisListener, ChartListener, ColorClipBoardListener {
	/** This is the chromaticity chart we are working with. */
	protected Chart chromaChart;
	/** This is the luminance slider for our color system. */
	protected Slider lumSlider;

	public ColorAdjustmentChart(int t, ColorServer ct) {
		super(t, ct);
	}

	public void setEnabled(boolean e) {
		lumSlider.setEnabled(e);
		chromaChart.setEnabled(e);
	}

	/**
	 * Set the current chart to represent the given color and recompute the
	 * valid region with respect to this color's luminance. This method should
	 * only be called in order to completely reset the chart's display. This is
	 * the case when the current coordinates may be completely invalid or a new
	 * current color has been selected.
	 */
	public void setValue(PxlColor c) {
		currentColor = c;
		localColor = c.transform(csType);
		lumSlider.setValue(localColor[0]);
		// System.out.println("(ColorAdjustmentChart " +
		// PxlColor.getShortName(csType) + ") Slider value is " +
		// lumSlider.getValue());
		chromaChart.setValidRegion(createValidRegion(currentColor.getY()));
		// This implies a repaint() message for the chromaticity chart
		chromaChart.setValue(localColor[1], localColor[2]);
	}

	/**
	 * Compute the valid region polygon points for the current luminance with
	 * respect to the chart model's coordinates.
	 */
	public double[] createValidRegion(double lum) {
		// This gives us the valid region vertices in XYZ coordinates
		ArrayList vr = PxlColor.getDeviceTransform()
				.getValidRegionVertices(lum);
		// if (true) return null; //x
		// if (vr == null) System.out.println("No valid region defined!");
		int n = vr.size();
		// System.out.println("ColorAdjustmentChart.createValidRegion(): Valid region size: "
		// + n);
		// Don't know what this loop is for but it makes the Microsoft VM work
		for (int i = 0; i < n; i++) {
			// System.out.println(" Starting: " + i);
			// PxlColor c = ((PxlColor)vr.get(i));
			// System.out.println("ColorAdjustmentChart.createValidRegion(): " +
			// c);
		}
		if (n == 0)
			return (null);
		// System.out.println("Start loop 1");
		double vrp[] = new double[n + n];
		// System.out.println("Start loop 2");
		double v[];
		/*
		 * System.out.println("Start loop 3");
		 * System.out.println(" -- Color Space Type:  " + csType);
		 * System.out.println(" -- Luminance:         " + lum);
		 * System.out.println(" -- Valid region size: " + n);
		 */
		for (int i = 0; i < n; i++) {
			// System.out.println(" Starting: " + i);
			PxlColor c = ((PxlColor) vr.get(i));
			String s = c.toString();
			// System.out.println("Node: " + c);
			v = ((PxlColor) vr.get(i)).transform(csType);
			// if (v == null) System.out.println("No transform for node " + i);
			vrp[i + i] = v[1];
			vrp[i + i + 1] = v[2];
		}
		return (vrp);
	}
	protected double[] sample;

	/** This is the ColorClipBoardListener implemenetation. */
	public void colorClipBoardSet(boolean show, ArrayList b) {
		if (show) {
			if (sample == null) {
				sample = new double[32];
			}
			int n = Math.min(16, b.size());
			double[] c;
			for (int i = 0; i < n; i++) {
				c = ((PxlColor) b.get(i)).transform(csType);
				sample[i + i] = c[1];
				sample[i + i + 1] = c[2];
			}
			chromaChart.setSample(sample, n);
			chromaChart.setShowSample(true);
		} else {
			chromaChart.setShowSample(false);
		}
		repaint();
	}

	/**
	 * The ColorChangeListener is called whenever the currently active color in
	 * the color table has been changed. This implementation checks whether this
	 * chart was the source of the change event. If this is true then nothing
	 * has to be done. If someone else is the source then it simply sets the
	 * current color coordinates.
	 */
	public void colorChanged(ColorChangeEvent e) {
		Object src = e.getSource();
		if (src == (Object) lumSlider) {
			// This comes from the luminance slider, update has already been
			// done
		} else if (src == (Object) chromaChart) {
			// This comes from the chromaticity chart, update has already been
			// done
		} else {
			int id = e.getID();
			if (id == ColorChangeEvent.COLOR_SET_PASSIVE) {
				lumSlider.setEnabled(false);
				chromaChart.setEnabled(false);
			} else if (id == ColorChangeEvent.COLOR_SET) {
				lumSlider.setEnabled(true);
				chromaChart.setEnabled(true);
			}
			// This comes from someone else
			setValue(e.getColor());
		}
		// System.out.println("Color adjusted: " + currentColor);
	}

	/**
	 * Handles adjustments in the chromaticity chart. The input coordinates are
	 * the chart models' values for the two axes.
	 */
	public boolean chartValueChanged(double x, double y) {
		// The return value which tells the chart whether the new
		// value is acceptable or not.
		boolean valid = false;
		// The local coordinates of the new color.
		double[] nc = { localColor[0], x, y };
		// Set the current color to the new chromaticity coordinates
		currentColor = PxlColor.instance(csType, nc);
		// System.out.println("Space " + PxlColor.getShortName(csType) +
		// ": Checking color " + currentColor);
		// Check whether the resulting chromaticities are within the
		// range of possible colors.
		if (currentColor.isDisplayable()) {
			// This color is OK.
			// System.out.println("Value Change OK");
			valid = true;
			// update localColor[] to reflect the correct state
			localColor[1] = x;
			localColor[2] = y;
		} else if (follow && currentColor.hasValidChromaticity()) {
			// This color needs a reduction of its luminance in
			// order to be displayable.
			// System.out.println("valid chroma: " + currentColor);
			// System.out.println("maximum L: " + currentColor.maxLum());
			currentColor.setY(currentColor.maxLum());
			// This requires an update of the valid region of the chart.
			// System.out.println("Changing the valid region for " +
			// currentColor);
			setValue(currentColor);
			// chromaChart.setValidRegion(createValidRegion(currentColor.getY()));
			// The resulting color value is OK.
			valid = true;
		} else {
			// The requested color cannot be displayed, so refuse to
			// accept it and restore the previous one.
			// System.out.println("Value change not possible");
			currentColor = PxlColor.instance(csType, localColor);
			valid = false;
		}
		if (valid) {
			// Finally tell the color server that the current color has
			// changed.
			colorServer.colorAdjusted(chromaChart, (PxlColor) currentColor);
			// System.out.println(x + " " + y);
		}
		return (valid);
	}

	/**
	 * Handles adjustments on the luminance scale. The input is the first
	 * coordinate of this panel's color system.
	 */
	public boolean axisValueChanged(double L) {
		// System.out.println("Axis value changing to " + L);
		if (L < PxlColor.minLum)
			L = PxlColor.minLum;
		// The return value which tells the chart whether the new
		// value is acceptable or not.
		boolean valid = false;
		// The local coordinates of the new color.
		double[] nc = { L, localColor[1], localColor[2] };
		// Set the current color to the new chromaticity coordinates
		currentColor = PxlColor.instance(csType, nc);
		// System.out.println("Space " + PxlColor.getShortName(csType) +
		// ": Checking color " + currentColor);
		// Check whether the resulting chromaticities are within the
		// range of possible colors.
		if (currentColor.isDisplayable()) {
			// This color is OK.
			// System.out.println("Value Change OK");
			// It might be possible that the valid region has to
			// be changed, so update localColor and the valid region.
			localColor[0] = L;
			chromaChart.setValidRegion(createValidRegion(currentColor.getY()));
			chromaChart.repaint();
			valid = true;
		} else {
			double[] wp = PxlColor.getDeviceTransform().getWhitePoint();
			if (follow && (currentColor.getY() <= wp[1])) {
				// This color needs a change of its chromaticity in
				// order to be displayable.
				// System.out.println("current color: " + currentColor);
				// System.out.println("is clipped to: " +
				// currentColor.clipped());
				currentColor = currentColor.clipped();
				// This requires an update of the valid region of the chart.
				// System.out.println("Changing the valid region for " +
				// currentColor);
				setValue(currentColor);
				// chromaChart.setValidRegion(createValidRegion(currentColor.getY()));
				// The resulting color value is OK.
				valid = true;
			} else {
				// The requested color cannot be displayed, so refuse to
				// accept it and restore the previous one.
				// System.out.println("Value change not possible");
				currentColor = PxlColor.instance(csType, localColor);
				valid = false;
			}
		}
		if (valid) {
			// Finally tell the color manager that the current color has
			// changed.
			colorServer.colorAdjusted(lumSlider, (PxlColor) currentColor);
			// System.out.println(x + " " + y);
		}
		// System.out.println(valid? "Change OK: ": "Change refused: " + L);
		return (valid);
	}
}
