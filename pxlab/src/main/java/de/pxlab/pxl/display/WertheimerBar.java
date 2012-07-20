package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * This is the classical Wertheimer bar which demonstrates apparent motion.
 * 
 * @author H. Irtel
 * @version 0.3.0
 */
/*
 * 06/18/01
 */
public class WertheimerBar extends ApparentMotion {
	public ExPar BarWidth = new ExPar(PROPORT, new ExParValue(0.3),
			"Relative width of the bar");
	public ExPar BarLength = new ExPar(PROPORT, new ExParValue(0.6),
			"Relative length of the bar");

	public WertheimerBar() {
		setTitleAndTopic("Wertheimer's Apparent Motion Bar",
				APPARENT_MOTION_DSP);
	}
	private int bar;

	protected int create() {
		bar = enterDisplayElement(new Bar(Color), group[0]);
		int t = enterTiming(OnOffTimer, OnDuration, 0);
		enterDisplayElement(new Clear(), group[1]);
		enterTiming(OnOffTimer, OffDuration, t);
		setFramesPerCycle(2);
		return bar;
	}
	private Rectangle b1, b2;

	protected void computeGeometry() {
		Rectangle r1 = largeSquare(width, height);
		int w = (int) ((r1.width / 2) * BarWidth.getDouble());
		int h = (int) ((r1.width - w) * BarLength.getDouble());
		int tx = r1.x + w + (r1.width - w - h) / 2;
		int ty = r1.y + r1.height - w - (r1.height - w - h) / 2;
		b1 = new Rectangle(tx - w, ty - h, w, h);
		getDisplayElement(bar).setRect(b1);
		b2 = new Rectangle(tx, ty, h, w);
	}

	public void computeAnimationFrame(int frame) {
		// System.out.println("WertheimerApparentMotion.computeAnimationFrame() frame = "
		// + frame);
		getDisplayElement(bar).setRect((frame == 0) ? b1 : b2);
	}
}
