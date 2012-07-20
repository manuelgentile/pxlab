package de.pxlab.pxl.calib;

import java.awt.*;
import java.util.*;
import java.awt.event.*;

import de.pxlab.gui.*;

public class YxyChart extends JChart implements ChartPaintExtension {
	private ArrayList colorData = new ArrayList(3);

	public YxyChart() {
		super(new LinearAxisModel(0.1, 0.7, 0.30), 7, new LinearAxisModel(0.0,
				0.7, 0.30), 8, 1.0);
		int cw = 8;
		int ch = 16;
		int hsl = 10 * (cw / 2);
		int hsr = 8 * cw / 4;
		int vst = ch;
		int vsb = 6 * ch / 4;
		int hwidth = 6 * 60;
		int vheight = 7 * 60;
		setPreferredHorizontalSpacing(hsl, hwidth, hsr);
		setPreferredVerticalSpacing(vst, vheight, vsb);
		setXLabelPrecision(2);
		setYLabelPrecision(2);
		setChartPaintExtension(this);
	}

	/**
	 * Set this graph's internal data array. Called by the object which created
	 * this graph.
	 */
	public void setColorData(ArrayList data) {
		colorData = data;
	}

	// -------------------------------------------------------------------
	// Implementation of the ChartPaintExtension interface
	// -------------------------------------------------------------------
	/**
	 * This is part of the implementation of the ChartPaintExtension interface.
	 * We use it here to paint the spectral distribution.
	 */
	public void extendedPaint(Graphics g) {
		if ((colorData != null) && (colorData.size() != 0)) {
			int n = colorData.size();
			for (int i = 0; i < n; i++) {
				Object[] d = (Object[]) (colorData.get(i));
				double[] data = (double[]) (d[0]);
				showColorPoint(g, data);
			}
		}
	}

	private void showColorPoint(Graphics g, double[] data) {
		if (data.length == 4) {
			int x = this.xTransform((double) data[2]);
			int y = this.yTransform((double) data[3]);
			g.setColor(Color.black);
			g.drawLine(x - 5, y, x + 5, y);
			g.drawLine(x, y - 5, x, y + 5);
		}
	}
}
