package de.pxlab.pxl.display;

import java.io.File;

import de.pxlab.pxl.*;

import java.util.*;

/**
 * Shows an image and its projection into a color subspace. Can be used to
 * simulate Edwin H. Land's experiment on color vision.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 11/26/01 allow null images and ignore them
 */
public class ColorTransformedPicture extends Picture {
	public ExPar LongColor = new ExPar(COLOR, new ExParValue(new YxyColor(20.0,
			0.616, 0.344)), "Long End Color");
	public ExPar ShortColor = new ExPar(COLOR, new ExParValue(new YxyColor(
			20.0, 0.26, 0.41)), "Short End Color");
	public ExPar ShowSubspace = new ExPar(FLAG, new ExParValue(0),
			"Show Subspace Flag");

	public ColorTransformedPicture() {
		setTitleAndTopic("Color transformed image", PICTURE_DSP);
	}
	private int pictIdx;

	protected int create() {
		ColorTransformedBitMapElement pict = new ColorTransformedBitMapElement(
				Color, ShortColor, LongColor);
		pictIdx = enterDisplayElement(pict, group[0]);
		// We need a dummy color parameter for the display editor
		defaultTiming(0);
		return (pictIdx);
	}

	protected void computeColors() {
		computeGeometry();
	}

	protected void computeGeometry() {
		ColorTransformedBitMapElement p = (ColorTransformedBitMapElement) getDisplayElement(pictIdx);
		p.setShowSubspace(ShowSubspace.getFlag());
		p.setLocation(LocationX.getInt(), LocationY.getInt());
		p.setReferencePoint(ReferencePoint.getInt());
		String fn = FileName.getString();
		if (de.pxlab.util.StringExt.nonEmpty(fn)) {
			p.setImage(Directory.getString(), fn);
			if (Histogram.getFlag()) {
				HashMap h = p.getColorHistogram();
				if (h != null) {
					p.printColorHistogram(h);
					Histogram.set(0);
				}
			}
		}
	}
}
