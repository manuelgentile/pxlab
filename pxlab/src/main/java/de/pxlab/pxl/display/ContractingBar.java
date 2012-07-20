package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A vertical bar which moves to the right and simultanously contracts in
 * length.
 * 
 * <P>
 * Conditions used by Xausa, Beghi, and Zanforlin:
 * 
 * <ul>
 * <li>Initial bar length: 6.5 cm
 * 
 * <li>Final lengths: 2, 3.2, 4.3 cm
 * 
 * <li>Motion distances: 2, 3.6, 5.3 cm
 * 
 * <li>speed: 1.6 cm/s
 * 
 * <li>bar width: 1mm
 * 
 * <li>observation distance: 1 m
 * </ul>
 * 
 * <p>
 * Initial parameters here are set for a 45 dots per cm screen using the middle
 * parameters settings.
 * 
 * @author H. Irtel
 * @version 0.3.0
 */
/*
 * 06/18/01
 */
public class ContractingBar extends FrameAnimation {
	/** Color of moving objects. */
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.LIGHT_GRAY)), "Color of Bar");
	public ExPar Width = new ExPar(HORSCREENSIZE, new ExParValue(5),
			"Width of the Bar");
	public ExPar Height = new ExPar(RTDATA, new ExParValue(292),
			"Current Height of the Bar");
	public ExPar InitialHeight = new ExPar(VERSCREENSIZE, new ExParValue(292),
			"Initial Height of the Bar");
	public ExPar FinalHeight = new ExPar(VERSCREENSIZE, new ExParValue(144),
			"Final Height of the Bar");
	public ExPar HorizontalDistance = new ExPar(HORSCREENSIZE, new ExParValue(
			162), "Horizontal Motion Distance");
	/** Horizontal center position of the bar. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal Center Position of Bar");
	/** Vertical position of the base line center. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical Center Position of Bar");

	public ContractingBar() {
		setTitleAndTopic("Contracting and Moving Bar", APPARENT_MOTION_DSP);
		FramesPerCycle.set(45);
	}
	protected Bar bar, last_bar;

	protected int create() {
		last_bar = new Bar(ExPar.ScreenBackgroundColor);
		enterDisplayElement(last_bar, group[0]);
		bar = new Bar(Color);
		int b = enterDisplayElement(bar, group[0]);
		int t = enterTiming(FrameTimer, FrameDuration, 0);
		return (b);
	}
	private int bw, fpc;
	private int[] x = null;
	private int[] h = null;

	protected void computeGeometry() {
		int fpc1 = FramesPerCycle.getInt();
		int fpc2 = ((fpc1 + 1) / 2);
		fpc = 2 * fpc2;
		if (fpc != fpc1)
			FramesPerCycle.set(fpc);
		setFramesPerCycle(fpc);
		if ((x == null) || (x.length < fpc)) {
			x = new int[fpc];
			h = new int[fpc];
		}
		bw = Width.getInt();
		int w = HorizontalDistance.getInt();
		int dx = w / fpc2;
		int x_left = -(fpc2 * dx) / 2 - bw / 2;
		int max_h = InitialHeight.getInt();
		int dh = ((int) Math.abs(InitialHeight.getInt() - FinalHeight.getInt()) / fpc2);
		for (int i = 0; i < fpc; i++) {
			int ff = (i <= fpc2) ? i : (fpc - i);
			h[i] = max_h - ff * dh;
			x[i] = x_left + ff * dx;
		}
		last_bar.setRect(bar.getRect());
		bar.setRect(x[0], -h[0] / 2, bw, h[0]);
	}

	public void computeAnimationFrame(int frame) {
		last_bar.setRect(bar.getRect());
		bar.setRect(x[frame], -h[frame] / 2, bw, h[frame]);
	}

	protected void destroy() {
		x = null;
		h = null;
		super.destroy();
	}
}
