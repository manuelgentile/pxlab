package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * The Hermann grid illusion with dots at the crossing points: the <a
 * href="http://www.fh-aalen.de/sga/neues/dpg2001/scintillating.html">
 * 'scintillating grid'</a> discovered by Elke Lingelbach and first published by
 * 
 * <p>
 * Schrauf M, Lingelbach B, Lingelbach E, Wist E (1995): "The Hermann grid and
 * the scintillation effect", Perception 24, supplement, page 89.
 * 
 * <p>
 * See <a href="http://www.leinroden.de/304herfold.htm">Bernd Lingelbach's
 * WWW-page</a> for a history of the Hermann and the scintillationg grid
 * illusion.
 * 
 * @version 0.3.0
 */
/*
 * 
 * 03/20/00
 */
public class HermannGridWithDots extends HermannGrid {
	public ExPar DotSize = new ExPar(SCREENSIZE, new ExParValue(16),
			"Dot Diameter");
	public ExPar DotColor = new ExPar(COLOR,
			new ExParValue(new PxlColor(0.95)), "Dot Color");
	/**
	 * Timer for controlling the ON and OFF periods. This should always be set
	 * to TimerCodes.RAW_CLOCK_TIMER.
	 */
	public ExPar OnOffTimer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.RAW_CLOCK_TIMER"),
			"On/Off interval timer");
	/** ON period duration. */
	public ExPar OnDuration = new ExPar(DURATION, new ExParValue(200),
			"On interval duration");
	/** OFF period duration. */
	public ExPar OffDuration = new ExPar(DURATION, new ExParValue(400),
			"Off interval duration");

	/** Cunstructor creating the title of the demo. */
	public HermannGridWithDots() {
		setTitleAndTopic("Hermann Grid with Dots", LATERAL_INHIBITION_DSP
				| DEMO);
	}

	public boolean isAnimated() {
		return (true);
	}
	protected int dots;

	/** Initialize the display list of the demo. */
	protected int create() {
		// We need to clear the screen for animation
		enterDisplayElement(new Clear(), group[0] + group[1]);
		grid = enterDisplayElement(new Grid(Color), group[0] + group[1]);
		dots = enterDisplayElement(new DotGrid(DotColor), group[0]);
		int t = enterTiming(OnOffTimer, OnDuration, 0);
		enterTiming(OnOffTimer, OffDuration, t);
		setFramesPerCycle(2);
		return (dots);
	}

	/** Initialize the display list of the demo. */
	protected void computeGeometry() {
		super.computeGeometry();
		int ds = DotSize.getInt();
		int dls = ds - LineWidth.getInt();
		int s = Size.getInt() + dls;
		int n1 = NumberOfLines.getInt() - 1;
		int dw = (s - ds) / (n1);
		s = (n1) * dw + ds;
		DotGrid b = (DotGrid) getDisplayElement(dots);
		b.setRect(-s / 2, -s / 2, s, s);
		b.setPhase(dw, ds, dw, ds);
	}
	/*
	 * public void computeAnimationFrame(int frame) { if (frame == 0) {
	 * activeTimingGroup = group[0]; showGroup(graphics); } else {
	 * show(graphics); } }
	 */
}
