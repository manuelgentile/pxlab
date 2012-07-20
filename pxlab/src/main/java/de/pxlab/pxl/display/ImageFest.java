package de.pxlab.pxl.display;

import java.awt.*;
import java.awt.image.*;

import de.pxlab.pxl.*;

/**
 * An external image modulated by a spatial and temporal Gaussian envelope. Only
 * achromatic images should be used since only the blue channel data are used as
 * weights. Image data are transformed into luminance by assuming an sRGB image
 * color space. The zero contrast luminance level may be set by a parameter.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/09/19
 */
public class ImageFest extends ModelFest {
	/**
	 * Image file name. Images are cached if the file name is preceded by a
	 * '@'-character.
	 */
	public ExPar FileName = new ExPar(STRING, new ExParValue("@sf_256_bw.jpg"),
			"Image file name");
	/**
	 * Image directory path name. Images are cached if the directory name is
	 * preceded by a '@'-character.
	 * 
	 * <p>
	 * Note that the PXLab design file grammar uses the '\'-character in a
	 * string as an escape sequence. This means that strings like 'c:\Images'
	 * are NOT allowed and MUST be written as 'c:/Images'.
	 */
	public ExPar Directory = new ExPar(STRING, new ExParValue(
			"images/modelfest"), "Image directory path");
	/**
	 * Relative luminance level which should be considered to be the zero
	 * contrast luminance level.
	 */
	public ExPar ZeroContrastLevel = new ExPar(PROPORT, new ExParValue(0.5),
			"Zero contrast luminance level");

	public ImageFest() {
		setTitleAndTopic("ModelFest Image Pattern", GRATING_DSP);
	}

	protected void computeGeometry() {
		// System.out.println("ImagePattern.computeGeometry()");
		BufferedImage ip = FileBase.loadImage(Directory.getString(),
				FileName.getString());
		int w = ip.getWidth();
		int h = ip.getHeight();
		if (w == h) {
			Size.set(w);
			super.computeGeometry();
			double a = Amplitude.getDouble();
			// System.out.println("ImagePattern.computeGeometry(): ImagePattern");
			grating.setImagePattern(a, ip, ZeroContrastLevel.getDouble());
		}
		if (!getFullRecompute()) {
			grating.computeColors();
		}
	}
}
