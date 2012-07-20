package de.pxlab.pxl.spectra;

import java.awt.*;
import de.pxlab.awtx.*;
import java.util.ArrayList;

import de.pxlab.pxl.*;

/**
 * This is a panel of spectral distributions. The left column contains the
 * independent and modifyable spectral distributions while the right panel
 * contains the dependent filter stack output distributions.
 */
public class SpectralDistributionPanel extends Frame {
	/** This holds the list of charts contained in this panel. */
	private ArrayList chart;
	/** The left part holds modifyable spectra. */
	private Panel leftPanel;
	/** The right part holds dependent spectra. */
	private Panel rightPanel;
	private ScrollPane scrollPane;
	private Panel columnPanel;
	private static int bd = 5;
	private ColorServer colorServer;

	/** Create a new spectral distribution panel with two columns. */
	public SpectralDistributionPanel(ColorServer cs) {
		super("SpectralDistributions");
		colorServer = cs;
		columnPanel = new SpectralPanel();
		leftPanel = new Panel(new ColumnLayout(Orientation.CENTER,
				Orientation.TOP, bd));
		columnPanel.add(leftPanel);
		rightPanel = new Panel(new ColumnLayout(Orientation.CENTER,
				Orientation.TOP, bd));
		columnPanel.add(rightPanel);
		scrollPane = new ScrollPane();
		scrollPane.add(columnPanel);
		setBackground(PXLabGUI.background);
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(ss.width - 500, 50, 460, 700);
		add(scrollPane);
		chart = new ArrayList();
	}

	/** Make this panel visible. */
	public void setVisible(boolean v) {
		super.setVisible(v);
	}

	/** Recompute the layout of this panel. */
	public void pack() {
		// super.pack();
	}

	/**
	 * Add a single spectral distribution chart to this panel. The chart is
	 * added to the left column if it is modifyable and to the right column if
	 * not.
	 */
	public void addSpectralDistributionChart(SpectralDistributionChart s) {
		if (s.isModifyable())
			addChart(leftPanel, s);
		else
			addChart(rightPanel, s);
	}

	/**
	 * Add a single spectral distribution chart to this panel. The chart is
	 * added to the left column if it is modifyable and to the right column if
	 * not.
	 */
	/*
	 * public void addSpectralFilterStackChart(SpectralFilterStackChart s) {
	 * addChart(rightPanel, s); s.register(chart);
	 * s.setColorServer(colorServer); }
	 */
	private void addChart(Panel p, SpectralDistributionChart s) {
		chart.add(s);
		p.add(new FramedPanel(s, s.getTitle()));
		p.validate();
		pack();
		repaint();
	}

	/**
	 * Clear this panel, dispose of all charts associated with it and make the
	 * panel invisible.
	 */
	public void clear() {
		for (int i = 0; i < chart.size(); i++) {
			((SpectralDistributionChart) chart.get(i)).dispose();
		}
		chart.clear();
		leftPanel.removeAll();
		rightPanel.removeAll();
		setVisible(false);
	}

	public boolean isEmpty() {
		return (chart.isEmpty());
	}
	class SpectralPanel extends Panel {
		SpectralPanel() {
			super(new GridLayout(1, 2, bd, 0));
		}

		public Insets getInsets() {
			return (new Insets(bd, bd, bd, bd));
		}
	}
}
