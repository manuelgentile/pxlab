package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Simple apparent motion of a dot.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 06/27/01
 */
public class MovingDot extends ApparentMotion {
	public ExPar Size = new ExPar(SCREENSIZE, new ExParValue(80),
			"Dot Diameter");
	public ExPar Distance = new ExPar(SCREENSIZE, new ExParValue(300),
			"Motion Distance");

	public MovingDot() {
		setTitleAndTopic("Moving Dot", APPARENT_MOTION_DSP);
	}
	private int dot;

	protected int create() {
		dot = enterDisplayElement(new Oval(Color), group[0]);
		int t = enterTiming(OnOffTimer, OnDuration, 0);
		enterDisplayElement(new Clear(), group[1]);
		enterTiming(OnOffTimer, OffDuration, t);
		setFramesPerCycle(2);
		return (dot);
	}
	private int leftX, rightX, dotSize;

	protected void computeGeometry() {
		int d = Distance.getInt();
		dotSize = Size.getInt();
		leftX = -d / 2;
		rightX = d / 2;
		getDisplayElement(dot).setCenterAndSize(leftX, 0, dotSize, dotSize);
	}

	public void computeAnimationFrame(int frame) {
		getDisplayElement(dot).setCenterAndSize((frame == 0) ? leftX : rightX,
				0, dotSize, dotSize);
	}
}
