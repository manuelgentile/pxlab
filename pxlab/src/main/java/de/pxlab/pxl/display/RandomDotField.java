package de.pxlab.pxl.display;

import java.awt.*;
import java.util.ArrayList;
import de.pxlab.pxl.*;

/**
 * This is a field of random dots which is used as a superclass for random dot
 * display objects. The dot field is an area of raster cells which may be
 * occupied by a dot or not.
 * 
 * <p>
 * Note that this implementation holds two RandomDotArray objects: The first one
 * serves as an 'original' while the second one may be a transformed version of
 * the first one. Usually the second array is the one which is actually shown,
 * since most subclasses include some transformation of the original set of
 * dots.
 * 
 * @version 0.2.1
 */
/*
 * 
 * 06/11/02
 * 
 * 2005/10/16 use AltColor and Bar() for background element
 */
public class RandomDotField extends Display {
	/** Dot color. */
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.LIGHT_GRAY)), "Dot color");
	/** Always set to the global ScreenBackgroundColor. */
	public ExPar AltColor = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)), "Background color");
	/**
	 * Probability of a single raster element to become a visible dot. If this
	 * value is negative then a fixed number of dots is set. The number is given
	 * by the parameter NumberOfDots.
	 */
	public ExPar Probability = new ExPar(PROPORT, new ExParValue(0.5),
			"Probability of a cell to contain a dot");
	/**
	 * Number of dots in the pattern which are set. The dot position is
	 * randomized. This parameter is used only if the parameter Probability is
	 * negative.
	 */
	public ExPar NumberOfDots = new ExPar(1, 3000, new ExParValue(0),
			"Number of dots");
	/** Size of a single dot in pixels. */
	public ExPar DotSize = new ExPar(1, 20, new ExParValue(2), "Dot size");
	/**
	 * Size of a single raster cell. Should be equal or larger than DotSize.
	 */
	public ExPar RasterSize = new ExPar(1, 20, new ExParValue(2),
			"Dot position raster size");
	/** Number of horizontal raster cells. */
	public ExPar Width = new ExPar(HORSCREENSIZE, new ExParValue(200),
			"Horizontal dot field raster cells");
	/** Number of vertical raster cells. */
	public ExPar Height = new ExPar(VERSCREENSIZE, new ExParValue(200),
			"Vertical dot field raster cells");
	/**
	 * If true then dots are arranged within a circular field. This only works
	 * if the NumberOfDots is used and Probability is negative.
	 */
	public ExPar Circular = new ExPar(FLAG, new ExParValue(1),
			"Dot field circular form flag");
	/** Horizontal center location. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal center position");
	/** Vertical center location. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical center position");
	/**
	 * Timer for controlling the motion frames. This should always be set to
	 * TimerCodes.RAW_CLOCK_TIMER which actually is the default value.
	 */
	public ExPar DotTimer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.RAW_CLOCK_TIMER"),
			"Motion frame timer");
	/** Motion frame duration. */
	public ExPar DotDuration = new ExPar(DURATION, 10, 3000,
			new ExParValue(300), "Motion frame duration");

	public RandomDotField() {
		setTitleAndTopic("Random Dot Field", RANDOM_DOT_DSP);
	}
	protected RandomDotArray base_rda;
	protected RandomDotArray transformed_rda;
	protected ArrayList base_dotArray;
	protected ArrayList transformed_dotArray;
	protected int rda_idx;
	protected int bg_idx;
	// protected int tm_idx;
	protected int currentWidth = 0;
	protected int currentHeight = 0;
	protected boolean currentForm = true;
	protected int currentNumberOfDots = 0;
	protected double currentProbability = 0.0;
	protected boolean needs_resampling = true;

	protected int create() {
		bg_idx = enterDisplayElement(new Bar(AltColor), group[0]);
		base_rda = new RandomDotArray(Color);
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
		int n = NumberOfDots.getInt();
		double p = Probability.getDouble();
		boolean fm = Circular.getFlag();
		base_rda.setDotSize(DotSize.getInt());
		base_rda.setRasterSize(RasterSize.getInt());
		if (needs_resampling || (sw != currentWidth) || (sh != currentHeight)
				|| (n != currentNumberOfDots) || (p != currentProbability)
				|| (fm != currentForm)) {
			base_rda.sampleDots(p, n, sw, sh, fm);
			transformed_rda = (RandomDotArray) base_rda.clone();
			removeDisplayElements(rda_idx);
			enterDisplayElement(transformed_rda, group[0]);
			base_dotArray = base_rda.getDotArray();
			transformed_dotArray = transformed_rda.getDotArray();
			currentWidth = sw;
			currentHeight = sh;
			currentForm = fm;
			currentNumberOfDots = n;
			currentProbability = p;
			needs_resampling = false;
		}
		// in case of dot- or raster size has been changed
		transformed_rda.setDotSize(DotSize.getInt());
		transformed_rda.setRasterSize(RasterSize.getInt());
		int x = LocationX.getInt();
		int y = LocationY.getInt();
		transformed_rda.setLocation(x, y);
		Dimension s = transformed_rda.getSize();
		getDisplayElement(bg_idx).setRect(x - s.width / 2, y - s.height / 2,
				s.width, s.height);
	}

	public void destroy() {
		base_rda = null;
		transformed_rda = null;
		super.destroy();
	}
}
