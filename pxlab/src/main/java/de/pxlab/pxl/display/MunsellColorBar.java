package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;
import de.pxlab.pxl.spectra.*;

/**
 * A simple bar whose color is defined by a Munsell Color Notation.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2004/12/01
 */
public class MunsellColorBar extends SpectralColorDisplay {
	/**
	 * This illuminant color temperature. The illuminant is created by
	 * simulating a standard CIE D illuminant and the parameter gives its color
	 * temperature.
	 */
	public ExPar IlluminantColorTemperature = new ExPar(1800.0, 26000.0,
			new ExParValue(6500.0), "Ambient Illuminant Color Temperature");
	/**
	 * 'Luminance' of the Illuminant. The illuminant's intensity is computed
	 * such that a perfect (100 %) reflector illuminated by the illuminant has
	 * this luminance.
	 */
	public ExPar IlluminantLuminance = new ExPar(0.0, 1000.0, new ExParValue(
			100.0), "Luminance of the Ambient Illuminant");
	/**
	 * The Munsell notation of the target's color. The color is simulated by
	 * using the given illuminant and the spectral remittance function of the
	 * munsell color chip whose name is given here.
	 */
	public ExPar MunsellColor = new ExPar(STRING, new ExParValue("5R 6/4"),
			"Munsell Color Name");
	public ExPar MunsellColorHue = new ExPar(SMALL_INT, new ExParValue(-1),
			"Munsell Color Hue Index");
	public ExPar MunsellColorValue = new ExPar(SMALL_INT, new ExParValue(3),
			"Munsell Color Value");
	public ExPar MunsellColorChroma = new ExPar(SMALL_INT, new ExParValue(2),
			"Munsell Color Chroma");
	/**
	 * The Yxy color coordinates of the target patch as they result from the
	 * given Munsel chip remittance and the given illumination.
	 */
	public ExPar Color = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.GRAY)), "Bar Color");
	public ExPar Width = new ExPar(HORSCREENSIZE, new ExParValue(100),
			"Bar Width");
	public ExPar Height = new ExPar(VERSCREENSIZE, new ExParValue(100),
			"Bar Height");
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal Center Position");
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical Center Position");

	public MunsellColorBar() {
		setTitleAndTopic("Munsell Color Bar", COLOR_SPACES_DSP);
	}
	private int bar_idx;

	protected int create() {
		bar_idx = enterDisplayElement(new Bar(this.Color), group[0]);
		defaultTiming(0);
		return (bar_idx);
	}

	protected void computeGeometry() {
		DisplayElement bar = getDisplayElement(bar_idx);
		int w = Width.getInt();
		int h = Height.getInt();
		bar.setRect(LocationX.getInt() - w / 2, LocationY.getInt() - h / 2, w,
				h);
		computeColors();
	}

	protected void computeColors() {
		try {
			SpectralDistribution ambLight = SpectralDistributionFactory
					.instance(getIlluminantName(bar_idx));
			ambLight.setLuminance(IlluminantLuminance.getDouble());
			de.pxlab.pxl.MunsellColor m = null;
			if (MunsellColorHue.getInt() >= 0) {
				m = new de.pxlab.pxl.MunsellColor(MunsellColorHue.getInt(),
						MunsellColorValue.getDouble(),
						MunsellColorChroma.getDouble());
				this.MunsellColor.set(m.toString());
			} else {
				m = new de.pxlab.pxl.MunsellColor(MunsellColor.getString());
				MunsellColorHue.set(m.getHueIndex());
				MunsellColorValue.set(m.getValue());
				MunsellColorChroma.set(m.getChroma());
			}
			SpectralDistribution r = SpectralDistributionFactory.instance(m
					.toString());
			if (r == null)
				r = SpectralDistributionFactory.instance("N 3/");
			// Filter the light by the reflectance
			r.filter(ambLight);
			PxlColor c = r.toXYZ();
			Color.set(c);
			// System.out.println("MunsellColorBar.computeColors() Color = " +
			// c.getY() + " " + c.getx() + " " + c.gety());
		} catch (SpectrumNotFoundException snf) {
			new ParameterValueError(snf.getMessage());
			Color.set(PxlColor.systemColor(PxlColor.BLACK));
		}
	}

	/**
	 * Get the name of the illuminant spectral distribution function for the
	 * display element at the given index.
	 * 
	 * @return the name of the illuminant spectrum or null if unknown.
	 */
	public String getIlluminantName(int idx) {
		return (idx == bar_idx) ? ("D" + String
				.valueOf(IlluminantColorTemperature.getInt())) : null;
	}

	/**
	 * Get the name of the reflectance function of the display element at the
	 * given index.
	 * 
	 * @return the name of the reflectance spectrum or null if unknown.
	 */
	public String getReflectanceName(int idx) {
		return (idx == bar_idx) ? MunsellColor.getString() : null;
	}
}
