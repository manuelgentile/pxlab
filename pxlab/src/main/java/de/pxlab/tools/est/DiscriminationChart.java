package de.pxlab.tools.est;

import java.awt.*;
import java.util.*;
import java.awt.event.*;

import de.pxlab.gui.*;

public class DiscriminationChart extends Chart implements ChartPaintExtension {
	private ArrayList data = new ArrayList(7);
	private ArrayList pars = new ArrayList(7);

	public DiscriminationChart(String s, double x_min, double x_max,
			double x_step, double y_min, double y_max, double y_step) {
		super(new LinearAxisModel(x_min, x_max, x_step), (int) Math
				.round((x_max - x_min) / x_step + 1), new LinearAxisModel(
				y_min, y_max, y_step), (int) Math.round((y_max - y_min)
				/ y_step + 1), 1.0);
		int cw = 8;
		int ch = 16;
		int hsl = 10 * (cw / 2);
		int hsr = 8 * cw / 4;
		int vst = ch;
		int vsb = 6 * ch / 4;
		int size = 160;
		setPreferredHorizontalSpacing(hsl, size, hsr);
		setPreferredVerticalSpacing(vst, size, vsb);
		setXLabelPrecision(2);
		setYLabelPrecision(2);
		setChartPaintExtension(this);
		setForeground(Color.black);
		setBackground(Color.white);
		if (s.equals("ab")) {
			setXAxisModel(new LinearAxisModel(-30.0, 30.0, 10.0));
			setYAxisModel(new LinearAxisModel(-30.0, 30.0, 10.0));
			// setNumberOfLargeXTicks(9);
			// setNumberOfLargeYTicks(9);
		}
	}

	/**
	 * Set this graph's internal data array. Called by the object which created
	 * this graph.
	 */
	public void addData(double[] x, double[] y) {
		data.add(x);
		data.add(y);
	}

	/**
	 * Set this graph's internal gamma parameter value array. Called by the
	 * object which created this graph.
	 */
	public void addPars(double[] p) {
		pars.add(p);
	}

	// -------------------------------------------------------------------
	// Implementation of the ChartPaintExtension interface
	// -------------------------------------------------------------------
	/**
	 * This is part of the implementation of the ChartPaintExtension interface.
	 * We use it here to paint the spectral distribution.
	 */
	public void extendedPaint(Graphics g) {
		int m = pars.size();
		if (m != 0) {
			// System.out.println("Show ellipses: " + m);
			for (int i = 0; i < m; i++) {
				double[] p = (double[]) pars.get(i);
				showEllipse(g, p);
			}
		}
		m = data.size();
		if (m != 0) {
			// System.out.println("Show data sets: " + m/2);
			for (int i = 0; i < m; i += 2) {
				double[] x = (double[]) data.get(i);
				double[] y = (double[]) data.get(i + 1);
				showDataPoints(g, x, y);
			}
		}
	}

	private void showEllipse(Graphics g, double[] p) {
		int n = 32;
		double[] xx = new double[n + 1];
		double[] yy = new double[n + 1];
		double s = (2.0 * Math.PI) / n;
		double t = 0.0;
		// System.out.println("a = " + p[2] + " b = + " + p[3]);
		for (int i = 0; i <= n; i++) {
			xx[i] = p[2] * Math.cos(t);
			yy[i] = p[3] * Math.sin(t);
			t += s;
		}
		double a11 = Math.cos(p[4]);
		double a12 = -Math.sin(p[4]);
		double a21 = -a12;
		double a22 = a11;
		int[] xxx = new int[n + 1];
		int[] yyy = new int[n + 1];
		for (int i = 0; i <= n; i++) {
			xxx[i] = this.xTransform(a11 * xx[i] + a12 * yy[i] + p[0]);
			yyy[i] = this.yTransform(a21 * xx[i] + a22 * yy[i] + p[1]);
		}
		g.setColor(Color.gray);
		for (int i = 0; i < n; i++) {
			g.drawLine(xxx[i], yyy[i], xxx[i + 1], yyy[i + 1]);
		}
		g.drawLine(xxx[0], yyy[0], xxx[n / 2], yyy[n / 2]);
		g.drawLine(xxx[n / 4], yyy[n / 4], xxx[3 * n / 4], yyy[3 * n / 4]);
	}

	private void showDataPoints(Graphics g, double[] x, double[] y) {
		int n = x.length;
		g.setColor(Color.black);
		for (int i = 0; i < n; i++) {
			showPoint(g, x[i], y[i]);
		}
	}

	private void showPoint(Graphics g, double x, double y) {
		int xx = this.xTransform(x);
		int yy = this.yTransform(y);
		int d = 3;
		g.fillOval(xx - d, yy - d, 2 * d, 2 * d);
	}
}
