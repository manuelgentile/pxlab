package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Ambiguous motion of a pair of dots.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 06/27/01
 */
public class MovingDotPair extends ApparentMotion {
	public ExPar Size = new ExPar(SCREENSIZE, new ExParValue(80),
			"Dot Diameter");
	public ExPar HorizontalDistance = new ExPar(SCREENSIZE,
			new ExParValue(300), "Horizontal Motion Distance");
	public ExPar VerticalDistance = new ExPar(SCREENSIZE, new ExParValue(300),
			"Vertical Motion Distance");

	public MovingDotPair() {
		setTitleAndTopic("Moving Pair of Dots", APPARENT_MOTION_DSP);
	}
	private int dot1, dot2;

	protected int create() {
		dot1 = enterDisplayElement(new Oval(Color), group[0]);
		dot2 = enterDisplayElement(new Oval(Color), group[0]);
		int t = enterTiming(OnOffTimer, OnDuration, 0);
		enterDisplayElement(new Clear(), group[1]);
		enterTiming(OnOffTimer, OffDuration, t);
		setFramesPerCycle(2);
		return (dot1);
	}
	private int leftX, rightX, topY, bottomY, dotSize;

	protected void computeGeometry() {
		int dx = HorizontalDistance.getInt();
		int dy = VerticalDistance.getInt();
		dotSize = Size.getInt();
		leftX = -dx / 2;
		rightX = dx / 2;
		topY = -dy / 2;
		bottomY = dy / 2;
		getDisplayElement(dot1).setCenterAndSize(leftX, topY, dotSize, dotSize);
		getDisplayElement(dot2).setCenterAndSize(rightX, bottomY, dotSize,
				dotSize);
	}

	public void computeAnimationFrame(int frame) {
		if (frame == 0) {
			getDisplayElement(dot1).setCenterAndSize(leftX, topY, dotSize,
					dotSize);
			getDisplayElement(dot2).setCenterAndSize(rightX, bottomY, dotSize,
					dotSize);
		} else {
			getDisplayElement(dot1).setCenterAndSize(rightX, topY, dotSize,
					dotSize);
			getDisplayElement(dot2).setCenterAndSize(leftX, bottomY, dotSize,
					dotSize);
		}
	}
}
