package de.pxlab.tools.eyeone;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import de.pxlab.gui.*;

public class SpectralGraph extends Chart implements ChartPaintExtension {
	private ArrayList spectralData = new ArrayList(100);

	public SpectralGraph() {
		super(new LinearAxisModel(350.0, 750.0, 50.0), 9, new LinearAxisModel(
				0.0, 1.0, 0.2), 6, 1.0);
		int cw = 8;
		int ch = 16;
		int hsl = 8 * (cw / 2);
		int hsr = 8 * cw / 4;
		int vst = ch;
		int vsb = 5 * ch / 4;
		int hwidth = 160;
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

	public void setShowReflectance() {
		setYAxisModel(new LinearAxisModel(0.0, 1.0, 0.2));
		setNumberOfLargeYTicks(6);
		setYUnitToXUnitRatio(1.0);
		repaint();
	}

	public void setShowEmission() {
		setYAxisModel(new LinearAxisModel(0.0, 3.0, 0.2));
		setNumberOfLargeYTicks(7);
		setYUnitToXUnitRatio(1.0);
		repaint();
	}

	public void setSpectralData(ArrayList sd) {
		spectralData = sd;
	}

	// -------------------------------------------------------------------
	// Implementation of the ChartPaintExtension interface
	// -------------------------------------------------------------------
	/**
	 * This is part of the implementation of the ChartPaintExtension interface.
	 * We use it here to paint the spectral distribution.
	 */
	public void extendedPaint(Graphics g) {
		if ((spectralData == null) || (spectralData.size() == 0))
			return;
		for (Iterator it = spectralData.iterator(); it.hasNext();) {
			showSpectralDistribution(g, ((NamedSpectrum) it.next()).data);
		}
	}

	private void showSpectralDistribution(Graphics g, float[] data) {
		int first = 380;
		int last = 720;
		int step = 10;
		int x0 = this.xTransform((double) first);
		int y0 = this.yTransform(data[0]);
		int x1, y1;
		int j = 0;
		g.setColor(Color.black);
		for (int i = first; i <= last; i += step) {
			x1 = this.xTransform((double) i);
			y1 = this.yTransform((double) data[j]);
			g.drawLine(x0, y0, x1, y1);
			x0 = x1;
			y0 = y1;
			j++;
		}
	}
}
