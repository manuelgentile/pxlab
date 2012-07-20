package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.pxl.*;
import de.pxlab.pxl.spectra.*;

/**
 * Create a frame which shows a single spectral distribution.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 06/13/00
 */
public class SpectralDistributionFrame extends Frame implements
		ComponentListener {
	private static Point location = null;
	private static Dimension size = null;

	public SpectralDistributionFrame(String m1, String m2) {
		super(m1 + " x " + m2);
		SpectralDistribution sd, rf;
		try {
			sd = SpectralDistributionFactory.instance(m1);
			rf = SpectralDistributionFactory.instance(m2);
			sd.filter(rf);
			sd.normalize();
			init(sd);
		} catch (SpectrumNotFoundException snfe) {
			dispose();
			new ParameterValueError(snfe.getMessage());
		}
	}

	public SpectralDistributionFrame(String m, boolean norm) {
		super(m);
		SpectralDistribution sd;
		try {
			sd = SpectralDistributionFactory.instance(m);
			if (norm)
				sd.normalize();
			init(sd);
		} catch (SpectrumNotFoundException snfe) {
			dispose();
			new ParameterValueError(snfe.getMessage());
		}
	}

	private void init(SpectralDistribution sd) {
		// System.out.println("Showing spectrum " + m);
		SpectralDistributionChart sdChart = new SpectralDistributionChart(sd,
				false, false, null, null);
		setLayout(new BorderLayout());
		add(sdChart, BorderLayout.CENTER);
		// setLayout(new FlowLayout());
		// add(sdChart);
		setBackground(PXLabGUI.background);
		if (location != null) {
			int x = location.x;
			int y = location.y;
			setLocation(new Point(x + 20, y + 15));
			setSize(size);
		} else {
			pack();
			setLocation(400, 200);
		}
		addComponentListener(this);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				setVisible(false);
				dispose();
			}
		});
		setVisible(true);
	}

	/** Add a small margin to the top of the chart. */
	public Insets getInsets() {
		Insets s = super.getInsets();
		return (new Insets(s.top + 10, s.left, s.bottom, s.right));
	}

	public void componentResized(ComponentEvent e) {
		location = getLocation();
		size = getSize();
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
		location = getLocation();
		size = getSize();
	}

	public void componentShown(ComponentEvent e) {
	}
}
