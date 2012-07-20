package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * Three timing groups.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2006/07/04
 */
public class TimingGroupsTest extends Display {
	/** The color of the bar. */
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.GRAY)), "Bar color");
	/**
	 * Timer for controlling the ON and OFF periods. This should always be set
	 * to de.pxlab.pxl.TimerCodes.VS_CLOCK_TIMER.
	 */
	public ExPar GroupTimer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.VS_CLOCK_TIMER"),
			"Internal cycle interval timer");
	/** Test interval duration. */
	public ExPar Group1Duration = new ExPar(DURATION, 0, 3000, new ExParValue(
			600), "Test interval duration");
	public ExPar Group2Duration = new ExPar(DURATION, 0, 3000, new ExParValue(
			600), "Test interval duration");
	public ExPar Group3Duration = new ExPar(DURATION, 0, 3000, new ExParValue(
			600), "Test interval duration");

	public TimingGroupsTest() {
		setTitleAndTopic("Timing groups test", SIMPLE_GEOMETRY_DSP);
	}

	public boolean isAnimated() {
		return (true);
	}
	private Bar b1, b2, b3, b4, b5;

	protected int create() {
		b1 = new Bar(this.Color);
		b2 = new Bar(this.Color);
		b3 = new Bar(this.Color);
		b4 = new Bar(this.Color);
		b5 = new Bar(this.Color);
		int b = enterDisplayElement(b1, group[0]);
		int t = enterTiming(GroupTimer, Group1Duration, 0);
		enterDisplayElement(b2, group[1]);
		enterDisplayElement(b3, group[1]);
		t = enterTiming(GroupTimer, Group2Duration, t);
		enterDisplayElement(b4, group[2]);
		enterDisplayElement(b5, group[2]);
		t = enterTiming(GroupTimer, Group3Duration, t);
		setFramesPerCycle(1);
		return b;
	}

	protected void computeGeometry() {
		int s = hugeSquareSize(width, height);
		int a = s / 15;
		b1.setRect(-a, -s / 2, a + a, s);
		b2.setRect(-4 * a, -s / 2, a + a, s);
		b3.setRect(2 * a, -s / 2, a + a, s);
		b4.setRect(-7 * a, -s / 2, a + a, s);
		b5.setRect(5 * a, -s / 2, a + a, s);
	}
}
