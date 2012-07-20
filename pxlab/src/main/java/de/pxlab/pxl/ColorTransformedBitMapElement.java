package de.pxlab.pxl;

import java.awt.*;
import java.awt.image.*;
import java.net.*;
import java.io.*;

// import de.pxlab.awtx.ImageLoader;
/**
 * A bitmap which contains a picture from an external file. Picture dimensions
 * are determined by the file content.
 * 
 * @author H. Irtel
 * @version 0.3.1
 */
/*
 * 11/22/01 allow URLs as image directories
 */
public class ColorTransformedBitMapElement extends BitMapElement {
	protected ExPar shortColorPar;
	protected ExPar longColorPar;
	protected PxlColor previousColorA = null, previousColorB = null;
	protected LinearColorTransform colorTransform;
	protected boolean showSubspace = false;
	protected boolean imageModified = true;
	protected BufferedImage sourceImage;
	protected BufferedImage reducedImage;

	public ColorTransformedBitMapElement(ExPar i, ExPar shortCol, ExPar longCol) {
		colorPar = i;
		shortColorPar = shortCol;
		longColorPar = longCol;
		type = DisplayElement.PICTURE;
	}

	public void setShowSubspace(boolean s) {
		showSubspace = s;
	}

	/** Every modification goes via this method. */
	public void setImage(String dir, String fn) {
		BufferedImage tryImage = FileBase.loadImage(dir, fn);
		if (!tryImage.equals(sourceImage)) {
			if (tryImage != null) {
				sourceImage = tryImage;
				imageModified = true;
			}
		}
		setColorTransform();
		recomputeImage();
		setImage(showSubspace ? reducedImage : sourceImage);
	}

	/**
	 * Recompute the image from its original according to the given parameters.
	 */
	protected void recomputeImage() {
		int w = sourceImage.getWidth();
		int h = sourceImage.getHeight();
		if ((reducedImage == null) || (w != reducedImage.getWidth())
				|| (h != reducedImage.getHeight())
				|| sourceImage.getType() != reducedImage.getType()) {
			reducedImage = new BufferedImage(w, h, sourceImage.getType());
			imageModified = true;
		}
		if (imageModified) {
			// System.out.print("ColorTransformedBitMapElement.recomputeImage() recomputing ...");
			// Koordinate als Skalierungsfaktor in der Mischung
			/*
			 * PxlColor pa = shortColorPar.getPxlColor(); PxlColor pb =
			 * longColorPar.getPxlColor(); PxlColor ma, mb; double amp = 50.0;
			 * double mxa = amp * 1.0/colorTransform.getMaxLumRed(); double mxb
			 * = amp * 1.0/colorTransform.getMaxLumGreen();
			 */
			double[] a = null;
			double[] b = null;
			PxlColor c = null;
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					a = colorTransform.toLinearRGB(new PxlColor(new Color(
							sourceImage.getRGB(x, y))).getComponents());
					// Projektion auf Schnittebene
					a[2] = 0.0;
					// Abschneiden der Farben ausserhalb der Basis
					if (a[0] < 0.0)
						a[0] = 0.0;
					if (a[1] < 0.0)
						a[1] = 0.0;
					c = (new PxlColor(colorTransform.fromLinearRGB(a)));
					// Projektion auf Schnittebene mit clipping (funktioniert
					// nicht)
					// a[2] = 0.0;
					// c = new
					// PxlColor(colorTransform.fromLinearRGB(colorTransform.clipped(a)));
					// Koordinate als Skalierungsfaktor in der Mischung
					/*
					 * ma = pa.scaled(a[0] * mxa); mb = pb.scaled(a[1] * mxb); c
					 * = ma.add(mb);
					 */
					reducedImage.setRGB(x, y, c.dev().getRGB());
				}
				// System.out.println(LinearColorTransform.stringOfArray(a));
				// System.out.println("  Line " + y);
			}
			imageModified = false;
			// System.out.println(" done.");
		} else {
			// System.out.print("ColorTransformedBitMapElement.recomputeImage() does not need recomputing.");
		}
	}

	protected void setColorTransform() {
		PxlColor a = shortColorPar.getPxlColor();
		PxlColor b = longColorPar.getPxlColor();
		imageModified = (imageModified || (!a.equals(previousColorA)) || (!b
				.equals(previousColorB)));
		if (imageModified) {
			// System.out.println("ColorTransformedBitMapElement.setColorTransform() New colors!");
			// Create a third vector which is orthogonal to a and b
			double nY = 100.0;
			double nZ = nY * (a.X * b.Y - b.X * a.Y) / (b.X * a.Z - a.X * b.Z);
			double nX = -(a.Y * nY + a.Z * nZ) / a.X;
			PxlColor c = new PxlColor(nX, nY, nZ);
			/*
			 * System.out.println("ColorTransformedBitMapElement.getTransform(): "
			 * ); System.out.println("   " +
			 * LinearColorTransform.stringOfArray(a.getYxyComponents()));
			 * System.out.println("   " +
			 * LinearColorTransform.stringOfArray(b.getYxyComponents()));
			 * System.out.println("   " +
			 * LinearColorTransform.stringOfArray(c.getYxyComponents()));
			 */
			colorTransform = new LinearColorTransform(a.getYxyComponents(),
					b.getYxyComponents(), c.getYxyComponents());
			previousColorA = (PxlColor) (a.clone());
			previousColorB = (PxlColor) (b.clone());
		}
	}

	public void show() {
		setImage(showSubspace ? reducedImage : sourceImage);
		super.show();
	}
}
