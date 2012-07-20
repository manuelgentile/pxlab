package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A cross context matching pattern with two center-surround fields.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 02/11/00
 */
public class RandomContextColorMatching extends Display {
	public ExPar LeftCenterColor = new ExPar(COLOR, new ExParValue(
			new PxlColor(0.391)), "Left center color");
	public ExPar LeftSurroundColor = new ExPar(COLOR, new ExParValue(
			new PxlColor(0.25)), "Left surround color");
	public ExPar RightCenterColor = new ExPar(COLOR, new ExParValue(
			new PxlColor(0.391)), "Right center color");
	public ExPar RightSurroundColor = new ExPar(COLOR, new ExParValue(
			new PxlColor(0.563)), "Right surround color");
	/** Range of random luminance variation. */
	public ExPar LuminanceRange = new ExPar(0.0, 100.0, new ExParValue(20.0),
			"Range of Random Luminance Variation");
	public ExPar Width = new ExPar(HORSCREENSIZE, new ExParValue(300),
			"Pattern Width");
	public ExPar Height = new ExPar(VERSCREENSIZE, new ExParValue(300),
			"Pattern Height");
	public ExPar Gap = new ExPar(HORSCREENSIZE, new ExParValue(60),
			"Gapp Between Patterns");
	public ExPar NumberOfColumns = new ExPar(SMALL_INT, new ExParValue(18),
			"Number of Elements in a Row/Column");
	public ExPar CenterSize = new ExPar(SMALL_INT, new ExParValue(6),
			"Number of Center Elements");
	public ExPar FixationMarkColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Color of the fixation mark");
	public ExPar FixationMarkLocationX = new ExPar(HORSCREENPOS,
			new ExParValue(0), "Horizontal location of the fixation mark");
	public ExPar FixationMarkLocationY = new ExPar(VERSCREENPOS,
			new ExParValue(0), "Vertical location of the fixation mark");
	public ExPar FixationMarkSize = new ExPar(SCREENSIZE, new ExParValue(20),
			"Size of the fixation mark");
	public ExPar FixationMarkLineWidth = new ExPar(SMALL_SCREENSIZE,
			new ExParValue(3), "Thickness of the fixation mark lines");

	public RandomContextColorMatching() {
		setTitleAndTopic("Random Context Color Matching",
				COMPLEX_COLOR_MATCHING_DSP);
	}
	protected int s1, s2, fixMarkElement;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new RegularTiles(LeftCenterColor,
				LeftSurroundColor), group[0]);
		s2 = enterDisplayElement(new RegularTiles(RightCenterColor,
				RightSurroundColor), group[0]);
		fixMarkElement = enterDisplayElement(new Cross(FixationMarkColor),
				group[0]);
		defaultTiming(0);
		return (s1);
	}

	protected void computeGeometry() {
		int w = Width.getInt();
		int h = Height.getInt();
		int d = Gap.getInt();
		int n = NumberOfColumns.getInt();
		int rs = w / n;
		w = n * rs;
		h = n * rs;
		RegularTiles t = (RegularTiles) getDisplayElement(s1);
		t.setProperties(-w - d / 2, -h / 2, w, h, rs, 1, 0);
		t.setCenterSize(CenterSize.getInt());
		t.setLuminanceRange(LuminanceRange.getDouble());
		t = (RegularTiles) getDisplayElement(s2);
		t.setProperties(d / 2, -h / 2, w, h, rs, 1, 0);
		t.setCenterSize(CenterSize.getInt());
		t.setLuminanceRange(LuminanceRange.getDouble());
		Cross fixMark = (Cross) getDisplayElement(fixMarkElement);
		fixMark.setLocation(this.FixationMarkLocationX.getInt(),
				this.FixationMarkLocationY.getInt());
		int fixMarkSize = this.FixationMarkSize.getInt();
		fixMark.setSize(fixMarkSize, fixMarkSize);
		fixMark.setLineWidth(this.FixationMarkLineWidth.getInt());
	}
}
