package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;
import de.pxlab.pxl.spectra.SpectralLightSource;
import de.pxlab.pxl.spectra.SpectralLightFilter;

/**
 * This is a surface simulation of a center and surround display illuminated by
 * two different light sources.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public class TwoIlluminants extends SpectralColorDisplay {
	public ExPar Illuminant1 = new ExPar(SPECTRUM, new ExParValue("D6500"),
			"First illuminant spectrum");
	public ExPar Illuminant2 = new ExPar(SPECTRUM, new ExParValue("D2400"),
			"Second illuminant spectrum");
	public ExPar Illuminant1Luminance = new ExPar(0.0, 200.0, new ExParValue(
			180.0), "Luminance of the 1st illuminant");
	public ExPar Illuminant2Luminance = new ExPar(0.0, 200.0, new ExParValue(
			120.0), "Luminance of the 2nd illuminant");
	public ExPar Background = new ExPar(SPECTRUM, new ExParValue("2.5R 3/2"),
			"Background reflectance");
	public ExPar Center = new ExPar(SPECTRUM, new ExParValue("5R 6/4"),
			"Center reflectance");
	public ExPar CenterSize = new ExPar(PROPORT, new ExParValue(0.33),
			"Center square size");

	/** Cunstructor creating the title of the display. */
	public TwoIlluminants() {
		setTitleAndTopic("Two Illuminants for a Center and Surround Field",
				SPECTRAL_COLOR_DSP | DEMO);
	}
	private ExPar c1, c2, c3, c4;
	private int s1, s2, s3, s4;

	protected int create() {
		SpectralLightSource ls1 = getLightSource(this.Illuminant1,
				this.Illuminant1Luminance);
		SpectralLightSource ls2 = getLightSource(this.Illuminant2,
				this.Illuminant2Luminance);
		SpectralLightFilter bg = getLightFilter(this.Background);
		SpectralLightFilter ct = getLightFilter(this.Center);
		c1 = enterLight("Left background", ls1, bg);
		c2 = enterLight("Right background", ls2, bg);
		c3 = enterLight("Left center", ls1, ct);
		c4 = enterLight("Right center", ls2, ct);
		s1 = enterDisplayElement(new Bar(c1));
		s2 = enterDisplayElement(new Bar(c2));
		s3 = enterDisplayElement(new Bar(c3));
		s4 = enterDisplayElement(new Bar(c4));
		return (s4);
	}

	protected void computeGeometry() {
		double p = CenterSize.getDouble();
		Rectangle l = new Rectangle(-width / 2, -height / 2, width / 2, height);
		Rectangle r = new Rectangle(0, -height / 2, width - width / 2, height);
		getDisplayElement(s1).setRect(l);
		getDisplayElement(s2).setRect(r);
		getDisplayElement(s3).setRect(innerRect(l, p));
		getDisplayElement(s4).setRect(innerRect(r, p));
	}
}
