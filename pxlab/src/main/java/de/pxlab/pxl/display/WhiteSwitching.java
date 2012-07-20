package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Single frame white bars on a black background help to evaluate the decay time
 * of display.
 * 
 * @author H. Irtel
 * @version 0.3.0
 */
/*
 * 2005/09/22
 */
public class WhiteSwitching extends ApparentMotion {
	public ExPar BackgroundColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)), "Background color");

	public WhiteSwitching() {
		setTitleAndTopic("Single Frame White Bars", DISPLAY_TEST_DSP | DEMO);
		OnDuration.set(13.66);
		OffDuration.set(500);
		Color.set(new ExParValueFunction(ExParExpression.WHITE));
	}
	private int bar;

	protected int create() {
		enterDisplayElement(new Clear(BackgroundColor), group[0] + group[1]);
		bar = enterDisplayElement(new VerStripedBar(Color), group[0]);
		int t = enterTiming(OnOffTimer, OnDuration, 0);
		enterTiming(OnOffTimer, OffDuration, t);
		setFramesPerCycle(1);
		return bar;
	}

	protected void computeGeometry() {
		VerStripedBar b = (VerStripedBar) (getDisplayElement(bar));
		b.setRect(-width / 2, -height / 2, width, height);
		int p = width / 8;
		b.setPhase(p, p / 2);
	}
}
