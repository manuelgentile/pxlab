package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A Left and a right square frame with a fixation mark as it is used for
 * spatial attention experiments.
 * 
 * <p>
 * Posner, M.I. Orienting of attention. Quarterly Journal of Experimental
 * Psychology, 1980, 32, 3-25.
 * 
 * <p>
 * Posner, M. I. & Cohen, Y. (1984) Components of visual orienting. In H, Bouma
 * & D. G. Bouwhis (eds.), Attention & Performance X, 531-556, Erlbaum.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 07/23/2003
 */
public class SpatialAttentionFrames extends Display {
	/** Color of the left spatial location marker square frame. */
	public ExPar LeftFrameColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Color of the Left Location Frame");
	/** Color of the right spatial location marker square frame. */
	public ExPar RightFrameColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Color of the RightLocation Frames");
	/** Size of the spatial location marker square frames. */
	public ExPar FrameSize = new ExPar(SCREENSIZE, new ExParValue(160),
			"Size of a single Location Frame");
	/** Horizontal distance of the spatial location marker square frames. */
	public ExPar FrameDistance = new ExPar(SCREENSIZE, new ExParValue(100),
			"Distance between Location Frames");
	/** Line thickness of the spatial location marker square frames. */
	public ExPar FrameLineWidth = new ExPar(SMALL_SCREENSIZE,
			new ExParValue(5), "Thickness of the Frame Lines");
	/** Fixation mark color. */
	public ExPar FixationMarkColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Color of the Fixation Mark");
	/** Fixation mark size. */
	public ExPar FixationMarkSize = new ExPar(SCREENSIZE, new ExParValue(30),
			"Size of the Fixation Mark");
	/** Fixation mark line width. */
	public ExPar FixationMarkLineWidth = new ExPar(SMALL_SCREENSIZE,
			new ExParValue(1), "Thickness of the Fixation Mark Lines");

	/** Cunstructor creating the title of the display. */
	public SpatialAttentionFrames() {
		setTitleAndTopic("Spatial Attention Frames", COMPLEX_GEOMETRY_DSP);
	}
	protected int leftFrame, rightFrame, fixationMark;

	/** Initialize the display list of the demo. */
	protected int create() {
		leftFrame = enterDisplayElement(new Rect(LeftFrameColor), group[0]);
		rightFrame = enterDisplayElement(new Rect(RightFrameColor), group[0]);
		fixationMark = enterDisplayElement(new Cross(FixationMarkColor),
				group[0]);
		defaultTiming(0);
		return (leftFrame);
	}
	protected int frameSize;
	protected int frameDist2;

	protected void computeGeometry() {
		// setDoubleBuffered(true);
		frameSize = FrameSize.getInt();
		frameDist2 = FrameDistance.getInt() / 2;
		DisplayElement r_left = getDisplayElement(leftFrame);
		r_left.setRect(-(frameSize + frameDist2), -frameSize / 2, frameSize,
				frameSize);
		r_left.setLineWidth(FrameLineWidth.getInt());
		DisplayElement r_right = getDisplayElement(rightFrame);
		r_right.setRect(frameDist2, -frameSize / 2, frameSize, frameSize);
		r_right.setLineWidth(FrameLineWidth.getInt());
		DisplayElement fm = getDisplayElement(fixationMark);
		fm.setLocation(0, 0);
		int fs = FixationMarkSize.getInt();
		fm.setSize(fs, fs);
		fm.setLineWidth(FixationMarkLineWidth.getInt());
	}
}
