package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

import de.pxlab.pxl.spectra.SpectralLightSource;
import de.pxlab.pxl.spectra.SpectralLightFilter;

/**
 * This display simulates Josef Albers' Homage to the Square series.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public class HomageToTheSquare2 extends SpectralColorDisplay {
	public ExPar Illuminant = new ExPar(SPECTRUM, new ExParValue("D6500"),
			"Illuminant spectrum");
	public ExPar IlluminantLuminance = new ExPar(0.0, 200.0, new ExParValue(
			100.0), "Luminance of the illuminant");
	public ExPar Background = new ExPar(SPECTRUM, new ExParValue("10R 3/1"),
			"Background reflectance");
	public ExPar Filter1 = new ExPar(SPECTRUM, new ExParValue("2.5YR 7/8"),
			"Filter1 transmittance");
	public ExPar Center = new ExPar(SPECTRUM, new ExParValue("2.5YR 7/8"),
			"Center Reflectance");
	public ExPar Filter2 = new ExPar(SPECTRUM, new ExParValue("2.5YR 7/8"),
			"Filter2 transmittance");
	public ExPar Filter1Size = new ExPar(PROPORT, new ExParValue(0.8),
			"Filter 1 square size");
	public ExPar Filter1Position = new ExPar(PROPORT, new ExParValue(0.75),
			"Filter 1 position");
	public ExPar CenterSize = new ExPar(PROPORT, new ExParValue(6.0 / 8.0),
			"Center square size");
	public ExPar CenterPosition = new ExPar(PROPORT, new ExParValue(0.75),
			"Filter 3 position");
	public ExPar Filter2Size = new ExPar(PROPORT, new ExParValue(4.0 / 6.0),
			"Filter 2 square size");
	public ExPar Filter2Position = new ExPar(PROPORT, new ExParValue(0.75),
			"Filter 2 position");
	public ExPar BackGroundColor = new ExPar(COLOR, new ExParValue(
			new PxlColor(Color.white)), "Background Color");

	public HomageToTheSquare2() {
		setTitleAndTopic("Homage to the Square II", SPECTRAL_COLOR_DSP | DEMO);
	}
	private ExPar c1, c2, c3, c4;
	private int s1, s2, s3, s4;

	protected int create() {
		// First set this display's screen background color to white
		SpectralLightSource ls = getLightSource(this.Illuminant,
				this.IlluminantLuminance);
		SpectralLightFilter bg = getLightFilter(this.Background);
		SpectralLightFilter f1 = getLightFilter(this.Filter1);
		SpectralLightFilter ce = getLightFilter(this.Center);
		SpectralLightFilter f2 = getLightFilter(this.Filter2);
		c1 = enterLight("Background", ls, bg);
		c2 = enterLight("Filter on Background", ls, bg, f1);
		c3 = enterLight("Center", ls, ce);
		c4 = enterLight("Filter on Center", ls, ce, f2);
		s1 = enterDisplayElement(new Bar(c1));
		s2 = enterDisplayElement(new Bar(c2));
		s3 = enterDisplayElement(new Bar(c3));
		s4 = enterDisplayElement(new Bar(c4));
		return (s1);
	}

	protected void computeColors() {
		ExPar.ScreenBackgroundColor.set(BackGroundColor.getPxlColor());
	}

	protected void computeGeometry() {
		computeColors();
		Rectangle bg = largeSquare(width, height);
		Rectangle f1 = innerRect(bg, Filter1Size.getDouble());
		f1.y = (int) (bg.y + (bg.height - f1.height)
				* Filter1Position.getDouble());
		Rectangle ce = innerRect(f1, CenterSize.getDouble());
		ce.y = (int) (f1.y + (f1.height - ce.height)
				* CenterPosition.getDouble());
		Rectangle f2 = innerRect(ce, Filter2Size.getDouble());
		f2.y = (int) (ce.y + (ce.height - f2.height)
				* Filter2Position.getDouble());
		getDisplayElement(s1).setRect(bg.x, bg.y, bg.width, bg.height);
		getDisplayElement(s2).setRect(f1.x, f1.y, f1.width, f1.height);
		getDisplayElement(s3).setRect(ce.x, ce.y, ce.width, ce.height);
		getDisplayElement(s4).setRect(f2.x, f2.y, f2.width, f2.height);
	}
}
