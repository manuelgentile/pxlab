package de.pxlab.pxl.spectra;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import de.pxlab.awtx.*;
import de.pxlab.gui.*;
import de.pxlab.pxl.*;

/**
 * This class describes spectral distribution charts which may possibly be
 * modified by drawing in the chart and which are not directly connected to
 * display colors.
 */
public class SpectralDistributionChart extends Panel implements ChartListener,
		AxisListener, ChartPaintExtension, ChartMouseExtension, ActionListener,
		SpectralLightListener {
	private Chart chart;
	private SpectralDistribution spectralDistribution;
	private boolean modifyable;
	private Slider lumSlider;
	private boolean lightSource;
	private TextField specField;
	private int tfw = 5;
	private Button loadButton;
	private Button saveButton;
	private ExPar luminancePar;
	private ExPar spectrumPar;
	private ColorParServer colorParServer = null;
	private ExPar colorPar = null;
	private String title;

	/**
	 * Create a new spectral chart which shows the given spectral distribution
	 * and is modifyable.
	 */
	public SpectralDistributionChart(SpectralLightFilter s) {
		this(s, false, true, s.getSpectrumPar(), null);
		// System.out.println("SpectralDistributionChart.init(): " +
		// s.getSpectrumPar().getString());
		s.addSpectralLightListener(this);
		title = spectrumPar.getString();
	}

	/**
	 * Create a new spectral chart which shows the given spectral distribution,
	 * a luminance slider, and is modifyable.
	 */
	public SpectralDistributionChart(SpectralLightSource s) {
		this(s, true, true, s.getSpectrumPar(), s.getLuminancePar());
		// System.out.println("SpectralDistributionChart.init(): " +
		// s.getSpectrumPar().getString());
		s.addSpectralLightListener(this);
		title = spectrumPar.getString();
	}

	/**
	 * Create a new spectral chart which shows the given spectral distribution,
	 * a luminance slider, and is modifyable.
	 */
	public SpectralDistributionChart(FilteredSpectralLight s) {
		this(s.getDistribution(1), false, false, null, null);
		// System.out.println("SpectralDistributionChart.init(): " +
		// s.getName());
		s.addSpectralLightListener(this);
		title = s.getName();
	}

	/**
	 * Create a new spectral chart which shows the given spectral distribution,
	 * a luminance slider, and is modifyable.
	 */
	public SpectralDistributionChart(FilteredSpectralLight s, ColorParServer cps) {
		this(s.getDistribution(1), false, false, null, null);
		// System.out.println("SpectralDistributionChart.init(): " +
		// s.getName());
		s.addSpectralLightListener(this);
		title = s.getName();
		colorParServer = cps;
		colorPar = s.getColorPar();
	}

	/**
	 * Create a new spectral chart which shows the given spectral distribution,
	 * a luminance slider, and is modifyable.
	 */
	public SpectralDistributionChart(SpectralDistribution s, String title) {
		this(s, false, false, null, null);
		this.title = title;
	}

	/**
	 * Create a new spectral chart which shows the given spectral distribution.
	 * If this is an emitted light spectrum then the chart also gets a luminance
	 * slider. If the spectrum has dependent spectra then these are added later.
	 * 
	 * @param s
	 *            spectral distribution to show.
	 * @param mdf
	 *            set this to true if this spectral distribution may be
	 *            modified. Usually reflectance charts and input light
	 *            distributions will be modifyable, while output lights will
	 *            not.
	 */
	public SpectralDistributionChart(SpectralDistribution s, boolean lsrc,
			boolean mdf, ExPar spctPar, ExPar lumPar) {
		spectralDistribution = s;
		modifyable = mdf;
		lightSource = lsrc;
		spectrumPar = spctPar;
		luminancePar = lumPar;
		setLayout(new BorderLayout());
		int cw = 8;
		int ch = 16;
		int hsl = 7 * (cw / 2);
		int hsr = 8 * cw / 4;
		int vst = ch / 4;
		int vsb = 5 * ch / 4;
		int hwidth = 160;
		int vstep = lightSource ? 7 : 6;
		// Create a xy-chromaticity diagram
		chart = new Chart(new LinearAxisModel(350.0, 750.0, 50.0), 9,
				new LinearAxisModel(0.0, (vstep - 1) * 0.2, 0.2), vstep, 1.0,
				this);
		chart.setPreferredHorizontalSpacing(hsl, hwidth, hsr);
		chart.setPreferredVerticalSpacing(vst, (vstep - 1) * (hwidth / 8), vsb);
		chart.setXLabelPrecision(0);
		chart.setYLabelPrecision(1);
		chart.setFirstXLabelAtTick(1);
		chart.setXLabelAtEveryTick(2);
		chart.setChartPaintExtension(this);
		chart.setChartMouseExtension(this);
		chart.setShowPosition(false);
		add(chart, BorderLayout.CENTER);
		if (lightSource) {
			double lum = luminancePar.getDouble();
			// Create a luminance scale with 1/2-power model
			int lumRange = 100 * (((int) lum + 99) / 100);
			AxisModel lumModel = new PowerAxisModel(1.0 / 2.0, 0.0,
					(double) lumRange, lum);
			lumSlider = new Slider(Slider.HORIZONTAL, lumModel, 5, 0);
			lumSlider.setPreferredSpacing(hsl, hwidth, hsr);
			lumSlider.setAxisListener(this);
			add(lumSlider, BorderLayout.SOUTH);
		}
		if (modifyable) {
			Panel tp = new Panel(new BorderLayout());
			tp.add(new Fill(hsl, 1), BorderLayout.WEST);
			tp.add(new Fill(hsr, 1), BorderLayout.EAST);
			Panel tpp = new Panel(new RowLayout());
			specField = new TextField(spectrumPar.getString(), tfw);
			specField.addActionListener(this);
			tpp.add(specField);
			loadButton = new Button("Load");
			loadButton.addActionListener(this);
			tpp.add(loadButton);
			saveButton = new Button("Save");
			saveButton.addActionListener(this);
			tpp.add(saveButton);
			tp.add(tpp, BorderLayout.CENTER);
			// tp.setBackground(Color.yellow);
			add(tp, BorderLayout.NORTH);
		}
	}

	/** Return this chart's modifyable state. */
	public boolean isModifyable() {
		return (modifyable);
	}

	/** Set this chart's modifyable state. */
	public void setModifyable(boolean m) {
		modifyable = m;
	}

	/** Return this chart's lightSource state. */
	public boolean isLightSource() {
		return (lightSource);
	}

	/** Set this chart's lightSource state. */
	public void setLightSource(boolean m) {
		lightSource = m;
	}

	public String getTitle() {
		return (title);
	}

	/** Return the spectral distribution shown by this chart. */
	public SpectralDistribution getSpectralDistribution() {
		return (spectralDistribution);
	}

	public void repaintChart() {
		chart.repaint();
	}

	// ---------------------------------------------------------------
	// This is the SpectralLightListener implementation
	// ---------------------------------------------------------------
	/**
	 * This tells the listener that the whole spectrum has been changed.
	 */
	public void spectrumChanged(Object o) {
		chart.repaint();
		if (colorParServer != null)
			colorParServer.setColorPar(this, colorPar);
	}

	/**
	 * This tells the listener that a single value of the spectrum has been
	 * changed.
	 */
	public void spectrumChanged(Object o, int w) {
		chart.repaint();
		if (colorParServer != null)
			colorParServer.setColorPar(this, colorPar);
	}

	/**
	 * This tells the listener that the luminance of a light source has been
	 * changed.
	 */
	public void luminanceChanged(Object o) {
		chart.repaint();
	}
	// -------------------------------------------------------------------
	// CharListener implementation
	// -------------------------------------------------------------------
	private int prev_w;
	private double prev_value;
	private boolean buttonDown = false;

	/**
	 * This implements the ChartListener interface. This method is called
	 * whenever the chart has set a new value. We only listen to chart value
	 * changes, however, when a chart is modifyable.
	 */
	public boolean chartValueChanged(double x, double y) {
		if (modifyable) {
			int w = (int) (x + 0.5);
			if (buttonDown) {
				specField.setText("");
				// specField.repaint();
				if (w == prev_w) {
					((SpectralLightFilter) spectralDistribution).setValueAt(w,
							y, this);
				} else if (w < prev_w) {
					for (int j = w; j < prev_w; j++) {
						((SpectralLightFilter) spectralDistribution)
								.setValueAt(j, y, this);
					}
				} else {
					for (int j = prev_w + 1; j <= w; j++) {
						((SpectralLightFilter) spectralDistribution)
								.setValueAt(j, y, this);
					}
				}
				prev_w = w;
			}
		}
		return (true);
	}

	// -------------------------------------------------------------------
	// AxisListener implementation
	// -------------------------------------------------------------------
	/** This implements the AxisListener interface. */
	public boolean axisValueChanged(double x) {
		if (modifyable && lightSource) {
			((SpectralLightSource) spectralDistribution).setLuminance(x);
			luminancePar.set(x);
		}
		return (true);
	}

	// -------------------------------------------------------------------
	// Load/Save button handler
	// -------------------------------------------------------------------
	/**
	 * When any of the color coordinate fields is changed then the current color
	 * is updated.
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == specField) {
			if (modifyable) {
				try {
					setSpectralData(SpectralDistributionFactory
							.instance(specField.getText()));
				} catch (SpectrumNotFoundException snfe) {
					new ParameterValueError(snfe.getMessage());
				}
			}
		} else if (source == loadButton) {
			if (modifyable) {
				SpectralDistribution sd = SpectralDistributionFactory.load();
				if (sd != null) {
					setSpectralData(sd);
					specField.setText("");
				}
			}
		} else if (source == saveButton) {
			spectralDistribution.save();
		}
	}

	private void setSpectralData(SpectralDistribution sd) {
		if ((sd.getFirst() != 380) || (sd.getLast() != 720)
				|| (sd.getStep() != 5)) {
			// System.out.println("Modifying wavelength range!");
			sd.modifyWavelengthRange(380, 720, 5);
		}
		if (lightSource)
			sd.normalize();
		((SpectralLightFilter) spectralDistribution)
				.setData(sd.getData(), this);
		// repaintChart();
	}

	// -------------------------------------------------------------------
	// Implementation of the ChartPaintExtension interface
	// -------------------------------------------------------------------
	/**
	 * This is part of the implementation of the ChartPaintExtension interface.
	 * We use it here to paint the spectral distribution.
	 */
	public void extendedPaint(Graphics g) {
		int first = spectralDistribution.getFirst();
		int last = spectralDistribution.getLast();
		int step = spectralDistribution.getStep();
		float[] data = spectralDistribution.getData();
		int x0 = chart.xTransform((double) first);
		int y0 = chart.yTransform(data[0]);
		int x1, y1;
		int j = 0;
		g.setColor(Color.black);
		for (int i = first; i <= last; i += step) {
			x1 = chart.xTransform((double) i);
			y1 = chart.yTransform((double) data[j]);
			g.drawLine(x0, y0, x1, y1);
			x0 = x1;
			y0 = y1;
			j++;
		}
	}

	// -------------------------------------------------------------------
	// Implementation of the ChartMouseExtension interface
	// -------------------------------------------------------------------
	/**
	 * This method is called whenever the mouse button has been pressed within
	 * the chart. It gets the models' x,y-coordinates as arguments.
	 */
	public void extendedMousePressed(MouseEvent e, double x, double y) {
		prev_w = (int) (x + 0.5);
		prev_value = y;
		buttonDown = true;
		if (colorParServer != null)
			colorParServer.setColorPar(this, colorPar);
	}

	/**
	 * This method is called whenever the mouse button has been released within
	 * the chart. It gets the models' x,y-coordinates as arguments.
	 */
	public void extendedMouseReleased(MouseEvent e, double x, double y) {
		buttonDown = false;
	}

	// -------------------------------------------------------------------
	//
	// -------------------------------------------------------------------
	public void dispose() {
		chart = null;
		spectralDistribution = null;
	}
}
