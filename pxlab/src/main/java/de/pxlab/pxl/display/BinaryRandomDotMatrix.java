package de.pxlab.pxl.display;

import java.awt.*;
import java.util.ArrayList;
import de.pxlab.pxl.*;

/**
 * This is a matrix of binary random dots which may be correlated.
 * 
 * @author H. Irtel
 * @version 0.2.1
 */
/*
 * 
 * 06/13/01
 * 
 * 2005/10/16 use AltColor and Bar() for background element
 */
public class BinaryRandomDotMatrix extends Display {
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.LIGHT_GRAY)), "Dot Color");
	public ExPar AltColor = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)), "Non-Dot Color");
	public ExPar Probability = new ExPar(PROPORT, new ExParValue(0.5),
			"Probability of a Dot Being Set");
	public ExPar Correlation = new ExPar(PROPORT, new ExParValue(0.0),
			"Horizontal Correlation");
	public ExPar DotSize = new ExPar(1, 20, new ExParValue(2), "Dot Size");
	public ExPar Width = new ExPar(HORSCREENSIZE, new ExParValue(200),
			"Dot Field Raster Width");
	public ExPar Height = new ExPar(VERSCREENSIZE, new ExParValue(200),
			"Dot Field Raster Height");
	/** Horizontal center location. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal Center Position");
	/** Vertical center location. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical Center Position");
	/**
	 * Timer for controlling the motion frames. This should always be set to
	 * TimerCodes.RAW_CLOCK_TIMER.
	 */
	public ExPar DotTimer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.RAW_CLOCK_TIMER"),
			"Motion frame timer");
	/** Motion frame duration. */
	public ExPar DotDuration = new ExPar(DURATION, 10, 3000,
			new ExParValue(300), "Motion Frame Duration");

	public BinaryRandomDotMatrix() {
		setTitleAndTopic("Binary Random Dot Matrix", RANDOM_DOT_DSP | DEMO);
	}
	protected BinaryRandomDotArray base_rda;
	protected BinaryRandomDotArray transformed_rda;
	protected int rda_idx;
	protected int bg_idx;
	// protected int tm_idx;
	protected int currentWidth = 0;
	protected int currentHeight = 0;
	protected double currentProbability = 0.0;
	protected double currentCorrelation = 0.0;
	protected boolean needs_resampling = true;

	protected int create() {
		bg_idx = enterDisplayElement(new Bar(AltColor), group[0]);
		base_rda = new BinaryRandomDotArray(Color);
		rda_idx = enterDisplayElement(base_rda, group[0]);
		// rda_idx = enterDisplayElement(base_rda);
		/* tm_idx = */enterTiming(DotTimer, DotDuration, 0);
		return (rda_idx);
	}

	protected void computeColors() {
		AltColor.set(ExPar.ScreenBackgroundColor.getPxlColor());
	}

	protected void computeGeometry() {
		int sw = Width.getInt();
		int sh = Height.getInt();
		double p = Probability.getDouble();
		double c = Correlation.getDouble();
		base_rda.setDotSize(DotSize.getInt());
		int m = 0;
		if (needs_resampling || (sw != currentWidth) || (sh != currentHeight)
				|| (p != currentProbability) || (c != currentCorrelation)) {
			if (c > 0.0)
				m = base_rda.sampleDots(p, c, sw, sh);
			else
				m = base_rda.sampleDots(p, sw, sh);
			transformed_rda = (BinaryRandomDotArray) base_rda.clone();
			removeDisplayElements(rda_idx);
			enterDisplayElement(transformed_rda, group[0]);
			currentWidth = sw;
			currentHeight = sh;
			currentProbability = p;
			currentCorrelation = c;
			needs_resampling = false;
		}
		// in case of dot- or raster size has been changed
		transformed_rda.setDotSize(DotSize.getInt());
		int x = LocationX.getInt();
		int y = LocationY.getInt();
		transformed_rda.setLocation(x, y);
		Dimension s = transformed_rda.getSize();
		getDisplayElement(bg_idx).setRect(x - s.width / 2, y - s.height / 2,
				s.width, s.height);
	}
}
