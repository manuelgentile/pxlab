package de.pxlab.pxl.display;

import java.io.File;
import java.awt.*;
import java.awt.image.*;

import de.pxlab.pxl.*;

import java.util.*;

/**
 * Shows an image and its projection into a color subspace. Can be used to
 * simulate Edwin H. Land's experiment on color vision:
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 11/26/01 allow null images and ignore them
 */
public class TwoColorImageProjection extends Picture {
	/** Long wavelength region image file name. */
	public ExPar LongWaveFileName = new ExPar(STRING, new ExParValue(
			"@auszug_rot_019_hc.jpg"), "Long Wavelength Image File");
	/** Short wavelength region image file name. */
	public ExPar ShortWaveFileName = new ExPar(STRING, new ExParValue(
			"@auszug_gruen_122_hc.jpg"), "Short Wavelength Image File");
	public ExPar LongColor = new ExPar(COLOR, new ExParValue(new YxyColor(20.0,
			0.621, 0.34)), "Long End Color");
	public ExPar ShortColor = new ExPar(COLOR, new ExParValue(new YxyColor(
			32.213, 0.24, 0.411)), "Short End Color");
	public ExPar Channel = new ExPar(GEOMETRY_EDITOR,
			TwoColorProjectionCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.TwoColorProjectionCodes.MIXTURE"),
			"Color Channel");

	public TwoColorImageProjection() {
		setTitleAndTopic("Two color image projection", PICTURE_DSP);
	}
	private BitMapElement pict;
	private BitMapElement pLong;
	private BitMapElement pShort;
	private boolean dirty;

	protected int create() {
		pict = new BitMapElement(Color);
		int pictIdx = enterDisplayElement(pict, group[0]);
		// We need a dummy color parameter for the display editor
		defaultTiming(0);
		pLong = new BitMapElement();
		pShort = new BitMapElement();
		return (pictIdx);
	}

	protected void computeColors() {
		computeGeometry();
	}
	private PxlColor longColor = null;
	private PxlColor shortColor = null;
	private int channel = -1;

	protected void computeGeometry() {
		// System.out.println("Entry: dirty = " + (dirty? "true": "false"));
		pict.setLocation(LocationX.getInt(), LocationY.getInt());
		pict.setReferencePoint(ReferencePoint.getInt());
		// p.setShowSubspace(ShowSubspace.getFlag());
		// Load image files if necessary
		BufferedImage iLong = FileBase.loadImage(Directory.getString(),
				LongWaveFileName.getString());
		if (!iLong.equals(pLong.getImage())) {
			pLong.setImage(iLong);
			dirty = true;
			// System.out.println("TwoColorImageProjection.computeGeometry() loaded image "
			// + LongWaveFileName.getString());
		}
		// System.out.println("Long File = " + fp);
		BufferedImage iShort = FileBase.loadImage(Directory.getString(),
				ShortWaveFileName.getString());
		if (!iShort.equals(pShort.getImage())) {
			pShort.setImage(iShort);
			dirty = true;
			// System.out.println("TwoColorImageProjection.computeGeometry() loaded image "
			// + ShortWaveFileName.getString());
		}
		// System.out.println("TwoColorImageProjection.computeGeometry() dirty = "
		// + (dirty? "true": "false"));
		PxlColor lC = LongColor.getPxlColor();
		dirty = dirty || !lC.equals(longColor);
		// System.out.println("long Color: dirty = " + (dirty? "true": "false")
		// + " " + longColor);
		PxlColor sC = ShortColor.getPxlColor();
		dirty = dirty || !sC.equals(shortColor);
		// System.out.println("short Color: dirty = " + (dirty? "true": "false")
		// + " " + shortColor);
		int chn = Channel.getInt();
		dirty = dirty || (chn != channel);
		/*
		 * System.out.println("Short Color = " + sC);
		 * System.out.println("Long Color = " + lC);
		 * System.out.println("Channel Channel = " + chn);
		 * 
		 * System.out.println("Colors: dirty = " + (dirty? "true": "false"));
		 */
		if (dirty) {
			BufferedImage longImage = pLong.getImage();
			BufferedImage shortImage = pShort.getImage();
			int w = longImage.getWidth();
			int h = longImage.getHeight();
			int type = longImage.getType();
			double a, b;
			PxlColor c;
			if ((w == shortImage.getWidth()) && (h == shortImage.getHeight())
					&& (type == shortImage.getType())) {
				dirty = false;
				longColor = LongColor.getPxlColor();
				shortColor = ShortColor.getPxlColor();
				channel = Channel.getInt();
				BufferedImage proImage = new BufferedImage(w, h,
						BufferedImage.TYPE_INT_RGB);
				double mx = PxlColor.getDeviceTransform().getWhitePoint()[1];
				// System.out.print("Working ...");
				if (channel == TwoColorProjectionCodes.MIXTURE) {
					for (int y = 0; y < h; y++) {
						for (int x = 0; x < w; x++) {
							a = (new PxlColor(
									(new Color(longImage.getRGB(x, y)))))
									.getY()
									/ mx;
							b = (new PxlColor((new Color(
									shortImage.getRGB(x, y))))).getY()
									/ mx;
							c = longColor.scaled(a).add(shortColor.scaled(b));
							proImage.setRGB(x, y, c.dev().getRGB());
						}
					}
				} else if (channel == TwoColorProjectionCodes.SHORT_ONLY) {
					for (int y = 0; y < h; y++) {
						for (int x = 0; x < w; x++) {
							b = (new PxlColor((new Color(
									shortImage.getRGB(x, y))))).getY()
									/ mx;
							c = shortColor.scaled(b);
							proImage.setRGB(x, y, c.dev().getRGB());
						}
					}
				} else if (channel == TwoColorProjectionCodes.LONG_ONLY) {
					for (int y = 0; y < h; y++) {
						for (int x = 0; x < w; x++) {
							a = (new PxlColor(
									(new Color(longImage.getRGB(x, y)))))
									.getY()
									/ mx;
							c = longColor.scaled(a);
							proImage.setRGB(x, y, c.dev().getRGB());
						}
					}
				}
				// System.out.println("done.");
				pict.setImage(proImage);
			}
		}
		// System.out.println("Exit: dirty = " + (dirty? "true": "false"));
		/*
		 * if (Histogram.getFlag()) { HashMap h = p.getColorHistogram();
		 * p.printColorHistogram(h); Histogram.set(0); }
		 */
	}
}
