package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;
import de.pxlab.pxl.ExPar;

import de.pxlab.pxl.spectra.SpectralLightSource;
import de.pxlab.pxl.spectra.SpectralLightFilter;

/**
 * This display simulates Josef Albers' Homage to the Square series.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public class HomageToTheSquare1 extends SpectralColorDisplay {
	public ExPar Illuminant = new ExPar(STRING, new ExParValue("D6500"),
			"Illuminant spectrum");
	public ExPar IlluminantLuminance = new ExPar(UNKNOWN,
			new ExParValue(100.0), "Luminance of the illuminant");
	public ExPar Background = new ExPar(STRING, new ExParValue("5R 5/10"),
			"Background reflectance");
	public ExPar Filter1 = new ExPar(STRING, new ExParValue("5R 5/12"),
			"Filter1 transmittance");
	public ExPar Filter2 = new ExPar(STRING, new ExParValue("5R 5/12"),
			"Filter2 transmittance");
	public ExPar Filter1Size = new ExPar(PROPORT,
			new ExParValue(0.8, 0.0, 1.0), "Filter 1 square size");
	public ExPar Filter1Position = new ExPar(PROPORT, new ExParValue(0.75, 0.0,
			1.0), "Filter 1 position");
	public ExPar Filter2Size = new ExPar(PROPORT,
			new ExParValue(0.5, 0.0, 1.0), "Filter 2 square size");
	public ExPar Filter2Position = new ExPar(PROPORT, new ExParValue(0.75, 0.0,
			1.0), "Filter 2 position");
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.white)), "Color 1");

	public HomageToTheSquare1() {
		setTitleAndTopic("Homage to the Square I", SPECTRAL_COLOR_DSP | DEMO);
	}
	private ExPar c1, c2, c3;
	private int s1, s2, s3;

	protected int create() {
		SpectralLightSource ls = getLightSource(this.Illuminant,
				this.IlluminantLuminance);
		SpectralLightFilter bg = getLightFilter(this.Background);
		SpectralLightFilter f1 = getLightFilter(this.Filter1);
		SpectralLightFilter f2 = getLightFilter(this.Filter2);
		c1 = enterLight("Background", ls, bg);
		c2 = enterLight("Filter level 1", ls, bg, f1);
		c3 = enterLight("Filter level 2", ls, bg, f1, f2);
		s1 = enterDisplayElement(new Bar(c1));
		s2 = enterDisplayElement(new Bar(c2));
		s3 = enterDisplayElement(new Bar(c3));
		return (s1);
	}

	protected void computeColors() {
		ExPar.ScreenBackgroundColor.set(Color1.getPxlColor());
	}

	protected void computeGeometry() {
		computeColors();
		Rectangle bg = largeSquare(width, height);
		Rectangle f1 = innerRect(bg, Filter1Size.getDouble());
		f1.y = (int) (bg.y + (bg.height - f1.height)
				* Filter1Position.getDouble());
		Rectangle f2 = innerRect(f1, Filter2Size.getDouble());
		f2.y = (int) (f1.y + (f1.height - f2.height)
				* Filter2Position.getDouble());
		getDisplayElement(s1).setRect(bg.x, bg.y, bg.width, bg.height);
		getDisplayElement(s2).setRect(f1.x, f1.y, f1.width, f1.height);
		getDisplayElement(s3).setRect(f2.x, f2.y, f2.width, f2.height);
	}
}
