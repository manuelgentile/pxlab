package de.pxlab.pxl.display;

import java.awt.Rectangle;

import de.pxlab.pxl.*;
import de.pxlab.pxl.spectra.SpectralLightSource;
import de.pxlab.pxl.spectra.SpectralLightFilter;

/**
 * This is a surface simulation of a center and surround display with a
 * transparent filter covering part of the center and surround.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 08/16/00
 */
public class CenterSurroundWithFilter extends SpectralColorDisplay {
	public ExPar Illuminant = new ExPar(SPECTRUM, new ExParValue("D6500"),
			"Illuminant spectrum");
	public ExPar IlluminantLuminance = new ExPar(SPECTRUM,
			new ExParValue(140.0), "Luminance of the illuminant");
	public ExPar Background = new ExPar(SPECTRUM, new ExParValue("N 3/"),
			"Background reflectance");
	public ExPar Center = new ExPar(SPECTRUM, new ExParValue("5R 6/4"),
			"Center reflectance");
	public ExPar Filter = new ExPar(SPECTRUM, new ExParValue("5G 6/4"),
			"Filter transmittance");
	public ExPar CenterSize = new ExPar(PROPORT, new ExParValue(0.33),
			"Center square size");
	public ExPar FilterSize = new ExPar(PROPORT, new ExParValue(0.4),
			"Filter square size");
	public ExPar FilterXPosition = new ExPar(HORSCREENPOS,
			new ExParValue(-30.0), "Horizontal filter position");
	public ExPar FilterYPosition = new ExPar(VERSCREENPOS,
			new ExParValue(-40.0), "Vertical filter position");

	/** Constructor creating the title of the display. */
	public CenterSurroundWithFilter() {
		setTitleAndTopic("Center and Surround Field with Filter",
				SPECTRAL_COLOR_DSP | DEMO);
	}
	private ExPar c1, c2, c3, c4;
	private int s1, s2, s3, s4;

	protected int create() {
		SpectralLightSource ls = getLightSource(Illuminant, IlluminantLuminance);
		SpectralLightFilter bg = getLightFilter(Background);
		SpectralLightFilter ct = getLightFilter(Center);
		SpectralLightFilter fl = getLightFilter(Filter);
		c1 = enterLight("Background", ls, bg);
		c2 = enterLight("Center", ls, ct);
		c3 = enterLight("Filtered background", ls, bg, fl);
		c4 = enterLight("Filtered center", ls, ct, fl);
		s1 = enterDisplayElement(new Bar(c1));
		s2 = enterDisplayElement(new Bar(c2));
		s3 = enterDisplayElement(new Bar(c3));
		s4 = enterDisplayElement(new Bar(c4));
		defaultTiming(0);
		return (s4);
	}

	protected void computeGeometry() {
		int cs = relSquareSize(width, height, CenterSize.getDouble());
		int fs = relSquareSize(width, height, FilterSize.getDouble());
		Rectangle c = centeredSquare(width, height, cs);
		Rectangle f = centeredSquare(width, height, fs);
		f.x = FilterXPosition.getInt();
		f.y = FilterYPosition.getInt();
		Rectangle cf = f.intersection(c);
		getDisplayElement(s1).setRect(-width / 2, -height / 2, width, height);
		getDisplayElement(s2).setRect(c.x, c.y, c.width, c.height);
		getDisplayElement(s3).setRect(f.x, f.y, f.width, f.height);
		getDisplayElement(s4).setRect(cf.x, cf.y, cf.width, cf.height);
	}
}
