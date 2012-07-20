package de.pxlab.pxl.display;

import java.awt.image.BufferedImage;
import de.pxlab.pxl.*;

/**
 * A single Self Assessment Manikin image.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2007/05/04
 */
public class SelfAssessmentManikin extends Display implements SAMScaleCodes {
	/** Drawing color. */
	public ExPar LineColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)), "Drawing line color");
	/** Frame line color. */
	public ExPar FrameColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Frame line color");
	/** Color used to indicate that an image is selected. */
	public ExPar SelectedColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Selected background color");
	/** Background color. */
	public ExPar BackgroundColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.WHITE)), "Background color");
	/** Horizontal picture position. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal picture position");
	/** Vertical picture position. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical picture position");
	/** Width of the picture. */
	public ExPar Width = new ExPar(HORSCREENSIZE, new ExParValue(620),
			"Picture width");
	/** Line width. */
	public ExPar LineWidth = new ExPar(SMALL_DOUBLE, new ExParValue(1.5),
			"Width of lines");
	/** Frame line width. */
	public ExPar FrameLineWidth = new ExPar(SMALL_DOUBLE, new ExParValue(1.5),
			"Width of frame lines");
	/**
	 * Reference point for the picture. See <code>PositionReferenceCodes</code>
	 * for a description.
	 */
	public ExPar ReferencePoint = new ExPar(GEOMETRY_EDITOR,
			PositionReferenceCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.PositionReferenceCodes.MIDDLE_CENTER"),
			"Picture Reference Point");
	/**
	 * Self-Assessment-Manikin scale dimension: valence, arousal, or dominance.
	 * See <code>SAMScaleCodes</code> for the respective codes.
	 */
	public ExPar Dimension = new ExPar(GEOMETRY_EDITOR, SAMScaleCodes.class,
			new ExParValueConstant("de.pxlab.pxl.SAMScaleCodes.VALENCE"),
			"Dimension");
	/** Scale value. Must satisfy 1 <= x <= 9. */
	public ExPar Level = new ExPar(DOUBLE, 1.0, 9.0, new ExParValue(5.0),
			"Scale value");
	/** Selected flag. If true then the image is shown int its selected state. */
	public ExPar Selected = new ExPar(FLAG, new ExParValue(0), "Selection flag");
	/**
	 * Portrait flag. If true then the valence scale uses a portrait image
	 * instead of a full body image.
	 */
	public ExPar Portrait = new ExPar(FLAG, new ExParValue(0), "Portrait flag");
	/**
	 * Narrow border flag. If true then a narrow border is used. The narrow
	 * border is not apropriate for the dominance scale. Narrow border should be
	 * used if only the valence and the arousal scales are used.
	 */
	public ExPar NarrowBorder = new ExPar(FLAG, new ExParValue(0),
			"Narrow border flag");
	/**
	 * Activate special selection points below SAM images. If true then the
	 * image itself is not selected but there is a special selection point below
	 * every image for selection. In this case the even numbered levels of the
	 * scale do not show images but are blank and are smaller than the odd
	 * numbered levels.
	 */
	public ExPar SelectionPoints = new ExPar(FLAG, new ExParValue(0),
			"Use special selection points");
	/** Show grid flag. */
	public ExPar ShowGrid = new ExPar(FLAG, new ExParValue(0), "Show grid flag");

	public SelfAssessmentManikin() {
		setTitleAndTopic("Self-Assessment Manikin", PICTURE_DSP | EXP);
	}
	protected int pictIdx;
	protected SAMImageFactory samImageFactory;

	protected int create() {
		samImageFactory = new SAMImageFactory();
		BitMapElement pict = new BitMapElement(this.LineColor);
		pictIdx = enterDisplayElement(pict, group[0]);
		defaultTiming(0);
		return pictIdx;
	}

	protected void computeColors() {
		samImageFactory.setColors(LineColor.getDevColor(),
				FrameColor.getDevColor(), SelectedColor.getDevColor(),
				BackgroundColor.getDevColor());
		computeGeometry();
	}

	protected void computeGeometry() {
		samImageFactory.setValencePortrait(Portrait.getFlag());
		samImageFactory.setNarrowBorder(NarrowBorder.getFlag());
		samImageFactory.setFrameLineWidth(FrameLineWidth.getDouble());
		samImageFactory.setLineWidth(LineWidth.getDouble());
		samImageFactory.setSelectionPoints(SelectionPoints.getFlag());
		samImageFactory.setShowGrid(ShowGrid.getFlag());
		BufferedImage img = samImageFactory.instance(Dimension.getInt(),
				Level.getDouble(), Selected.getFlag(), Width.getInt());
		BitMapElement p = (BitMapElement) getDisplayElement(pictIdx);
		p.setImage(img);
		p.setLocation(LocationX.getInt(), LocationY.getInt());
		p.setReferencePoint(ReferencePoint.getInt());
	}
}
