package de.pxlab.pxl.display;

import java.io.File;

import de.pxlab.pxl.*;

import de.pxlab.util.*;
import java.util.*;

/**
 * Shows a picture which may be a bitmap image or a vector graphics image.
 * 
 * @version 0.1.5
 */
/*
 * 
 * 11/26/01 allow null images and ignore them
 * 
 * 2006/02/10 added support for scalable vector graphics images.
 */
public class Picture extends Display {
	/**
	 * Image file name. Images are cached if the file name is preceded by a
	 * '@'-character.
	 */
	public ExPar FileName = new ExPar(STRING, new ExParValue("@fi-pb.jpg"),
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
	public ExPar Directory = new ExPar(STRING,
			new ExParValue("C:/Land/images"), "Image directory path");
	/** Horizontal picture position. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal screen position");
	/** Vertical picture position. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical screen position");
	/**
	 * Width of scalable vector graphics (SVG) images. This parameter is ignored
	 * for bitmap images.
	 */
	public ExPar Width = new ExPar(HORSCREENSIZE, new ExParValue(0),
			"Scalable vector graphic width");
	/**
	 * Height of scalable vector graphics (SVG) images. This parameter is
	 * ignored for bitmap images.
	 */
	public ExPar Height = new ExPar(VERSCREENSIZE, new ExParValue(0),
			"Scalable vector graphic height");
	/**
	 * Reference point for the picture. See <code>PositionReferenceCodes</code>
	 * for a description.
	 */
	public ExPar ReferencePoint = new ExPar(GEOMETRY_EDITOR,
			PositionReferenceCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.PositionReferenceCodes.MIDDLE_CENTER"),
			"Picture Reference Point");
	public ExPar Color = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.GRAY)), "Point Color");
	public ExPar Histogram = new ExPar(FLAG, new ExParValue(0),
			"Color Histogram");
	/** Clear the content of the picture file cash when recomputing the image. */
	public ExPar ClearFileBaseOnRecompute = new ExPar(FLAG, new ExParValue(0),
			"Clear FileBase when recomputing");

	public Picture() {
		setTitleAndTopic("Picture image", PICTURE_DSP);
	}
	protected int pictIdx;

	protected int create() {
		BitMapElement pict = new BitMapElement(this.Color);
		pictIdx = enterDisplayElement(pict, group[0]);
		defaultTiming(0);
		return pictIdx;
	}

	protected void computeGeometry() {
		String fn = FileName.getString();
		if (StringExt.nonEmpty(fn)) {
			BitMapElement p = (BitMapElement) getDisplayElement(pictIdx);
			p.setImage(Directory.getString(), fn, Width.getInt(),
					Height.getInt());
			p.setLocation(LocationX.getInt(), LocationY.getInt());
			p.setReferencePoint(ReferencePoint.getInt());
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
