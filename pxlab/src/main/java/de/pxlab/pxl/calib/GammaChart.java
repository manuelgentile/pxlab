package de.pxlab.pxl.calib;

import java.awt.*;
import java.util.*;
import java.awt.event.*;

import de.pxlab.gui.*;

public class GammaChart extends JChart implements ChartPaintExtension {
	private ArrayList gammaData = new ArrayList(3);
	private ArrayList gammaPars = new ArrayList(3);
	private static final int yTicks = 8;
	private double maxY;

	public GammaChart() {
		super(new LinearAxisModel(0.0, 256.0, 32.0), 9, new LinearAxisModel(
				0.0, (yTicks - 1) * 20.0, 20.0), yTicks, 1.0);
		maxY = (yTicks - 1) * 15.0;
		int cw = 8;
		int ch = 16;
		int hsl = 14 * (cw / 2);
		int hsr = 8 * cw / 4;
		int vst = ch;
		int vsb = 6 * ch / 4;
		int hwidth = 8 * 60;
		int vstep = 6;
		setPreferredHorizontalSpacing(hsl, hwidth, hsr);
		setPreferredVerticalSpacing(vst, (vstep - 1) * (hwidth / 8), vsb);
		setXLabelPrecision(0);
		setYLabelPrecision(1);
		setFirstXLabelAtTick(1);
		setXLabelAtEveryTick(2);
		setChartPaintExtension(this);
		setForeground(Color.black);
		setBackground(Color.white);
	}

	/**
	 * Set this graph's internal data array. Called by the object which created
	 * this graph.
	 */
	public void setGammaData(ArrayList data) {
		gammaData = data;
		double maxData = 0.0;
		int n = gammaData.size();
		for (int i = 0; i < n; i++) {
			Object[] d = (Object[]) (gammaData.get(i));
			double x = ((double[]) (d[0]))[1];
			if (x > maxData)
				maxData = x;
		}
		if (maxData > maxY) {
			double dx = 5.0 * Math.ceil((maxData / (yTicks - 1)) / 5.0);
			setYAxisModel(new LinearAxisModel(0.0, (yTicks - 1) * dx, dx));
		}
	}

	/**
	 * Set this graph's internal gamma parameter value array. Called by the
	 * object which created this graph.
	 */
	public void setGammaPars(ArrayList data) {
		gammaPars = data;
	}

	// -------------------------------------------------------------------
	// Implementation of the ChartPaintExtension interface
	// -------------------------------------------------------------------
	/**
	 * This is part of the implementation of the ChartPaintExtension interface.
	 * We use it here to paint the spectral distribution.
	 */
	public void extendedPaint(Graphics g) {
		if ((gammaData == null) || (gammaData.size() == 0))
			return;
		double[] pars = null;
		int n = gammaData.size();
		boolean showPars = (gammaPars != null) && (gammaPars.size() == n);
		for (int i = 0; i < n; i++) {
			Object[] d = (Object[]) (gammaData.get(i));
			double[][] data = new double[d.length][];
			for (int j = 0; j < d.length; j++) {
				data[j] = (double[]) (d[j]);
			}
			if (showPars) {
				pars = (double[]) (gammaPars.get(i));
			}
			showGammaFunction(g, data, pars);
		}
	}

	private void showGammaFunction(Graphics g, double[][] data, double[] pars) {
		int n = data.length;
		double scale = (double) data[0][1];
		int x0 = this.xTransform((double) data[0][0]);
		int y0 = this.yTransform((double) data[0][1]);
		int py0 = 0;
		if (pars != null) {
			py0 = this.yTransform(gammaValue((double) data[0][0], scale, pars));
		}
		int x1, y1, py1 = 0;
		for (int i = 1; i < n; i++) {
			x1 = this.xTransform((double) data[i][0]);
			y1 = this.yTransform((double) data[i][1]);
			if (pars != null) {
				g.setColor(Color.gray);
				py1 = this.yTransform(gammaValue((double) data[i][0], scale,
						pars));
				g.drawLine(x0, py0, x1, py1);
			}
			g.setColor(Color.black);
			g.drawLine(x0, y0, x1, y1);
			x0 = x1;
			y0 = y1;
			py0 = py1;
		}
	}

	private double gammaValue(double d, double a, double[] p) {
		return a * Math.pow((p[1] * d / 255.0 - (p[1] - 1.0)), p[0]);
	}
}
