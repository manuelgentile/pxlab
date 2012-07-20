package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;
import de.pxlab.pxl.spectra.SpectralLightSource;
import de.pxlab.pxl.spectra.SpectralLightFilter;

/**
 * This is a simulation of two surfaces which are metameric under Lightsource C
 * and may be covered by a filter.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class FilteredMetamericSurfaces extends SpectralColorDisplay {
	public ExPar CenterSize = new ExPar(PROPORT, new ExParValue(0.4),
			"Center square size");
	public ExPar Illuminant = new ExPar(SPECTRUM, new ExParValue("C"),
			"Illuminant spectrum");
	public ExPar IlluminantLuminance = new ExPar(0.0, 200.0, new ExParValue(
			100.0), "Luminance of the illuminant");
	public ExPar Background = new ExPar(SPECTRUM, new ExParValue("N 5/"),
			"Background reflectance");
	public ExPar Center1 = new ExPar(SPECTRUM, new ExParValue("MG1"),
			"Center 1 reflectance");
	public ExPar Center2 = new ExPar(SPECTRUM, new ExParValue("MG2"),
			"Center 2 reflectance");
	public ExPar Filter = new ExPar(SPECTRUM, new ExParValue("5R 7/4"),
			"Filter Transmittance");
	public ExPar FilterSize = new ExPar(PROPORT, new ExParValue(0.25),
			"Filter square size");
	public ExPar FilterYPosition = new ExPar(VERSCREENPOS,
			new ExParValue(60.0), "Vertical filter position");

	/** Cunstructor creating the title of the display. */
	public FilteredMetamericSurfaces() {
		setTitleAndTopic("Two Metameric Surfaces Coverd by a Filter",
				SPECTRAL_COLOR_DSP | DEMO);
	}
	private ExPar c1, c2, c3, c4, c5, c6;
	private int s1, s2, s3, s4, s5, s6;

	protected int create() {
		SpectralLightSource ls = getLightSource(this.Illuminant,
				this.IlluminantLuminance);
		SpectralLightFilter bg = getLightFilter(this.Background);
		SpectralLightFilter ct1 = getLightFilter(this.Center1);
		SpectralLightFilter ct2 = getLightFilter(this.Center2);
		SpectralLightFilter fl = getLightFilter(this.Filter);
		c1 = enterLight("Surround", ls, bg);
		c2 = enterLight("Left center", ls, ct1);
		c3 = enterLight("Right center", ls, ct2);
		c4 = enterLight("Filtered background", ls, bg, fl);
		c5 = enterLight("Filtered left center", ls, ct1, fl);
		c6 = enterLight("Filtered right center", ls, ct2, fl);
		s1 = enterDisplayElement(new Bar(c1));
		s2 = enterDisplayElement(new Bar(c2));
		s3 = enterDisplayElement(new Bar(c3));
		s4 = enterDisplayElement(new Bar(c4));
		s5 = enterDisplayElement(new Bar(c5));
		s6 = enterDisplayElement(new Bar(c6));
		return (s1);
	}

	protected void computeGeometry() {
		double p = CenterSize.getDouble();
		Rectangle r1 = hugeSquare(width, height);
		getDisplayElement(s1).setRect(r1);
		Rectangle r = innerRect(r1, p);
		Rectangle rf = innerRect(r1, FilterSize.getDouble());
		Rectangle rc1 = new Rectangle(r.x - r.width / 2, r.y, r.width, r.height);
		Rectangle rc2 = new Rectangle(r.x - r.width / 2 + r.width, r.y,
				r.width, r.height);
		getDisplayElement(s2).setRect(rc1);
		getDisplayElement(s3).setRect(rc2);
		rf.y += FilterYPosition.getInt();
		Rectangle rbf = rf.intersection(r1);
		Rectangle rc1f = rf.intersection(rc1);
		Rectangle rc2f = rf.intersection(rc2);
		getDisplayElement(s4).setRect(rbf);
		getDisplayElement(s5).setRect(rc1f);
		getDisplayElement(s6).setRect(rc2f);
	}
}
