package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * Presents a paragraph of text and a rating scale for the response. The scale
 * is adjusted by moving the mouse pointer along the scale bar while a button is
 * pressed. Selection is indicated by pressing any response button/key.
 * Selection my be restricted to certain buttons/keys by using the ResponseSet
 * parameter.
 * 
 * @version 0.2.0
 */
/*
 * 06/03/01
 * 
 * 11/17/01 added left and right scale labels
 * 
 * 12/12/01 added UnsignedNumbers parameter
 * 
 * 12/13/01 terrible bug: ScaleValue was not set while moving the mouse!
 * 
 * 2007/06/18 made this a replacement for class RatingScaleQuestion
 */
public class RatingScale extends TextParagraph {
	/** Number of ticks on the scale. */
	public ExPar NumberOfTicks = new ExPar(0, 20, new ExParValue(5),
			"Number of Ticks on Scale");
	/** Lowest tick value. */
	public ExPar LowestTick = new ExPar(-20, 20, new ExParValue(-2),
			"Lowest Scale Tick Value");
	/** Tick step size. */
	public ExPar TickStep = new ExPar(1, 10, new ExParValue(1),
			"Scale Tick Step Size");
	/** Rating scale bar color. */
	public ExPar BarColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GRAY)), "Scale Bar Color");
	/** Rating scale tick and label color. */
	public ExPar TickColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Scale Tick and Label Color");
	/** Rating scale moving pointer color. */
	public ExPar PointerColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.YELLOW)), "Scale Pointer Color");
	/** Horizontal scale bar position. */
	public ExPar ScaleLocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal Scale Position");
	/** Vertical scale bar position. */
	public ExPar ScaleLocationY = new ExPar(VERSCREENPOS, new ExParValue(100),
			"Vertical Scale Position");
	/** Scale reference point. Uses the text reference point codes. */
	public ExPar ScaleReferencePoint = new ExPar(GEOMETRY_EDITOR,
			PositionReferenceCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.PositionReferenceCodes.TOP_CENTER"),
			"Scale reference point");
	/** Width of the scale bar. */
	public ExPar ScaleWidth = new ExPar(HORSCREENSIZE, new ExParValue(460),
			"Width of scale");
	/** Height of the scale bar including scale ticks. */
	public ExPar ScaleHeight = new ExPar(VERSCREENSIZE, new ExParValue(40),
			"Height of scale");
	/**
	 * Tick type code.
	 * 
	 * @see de.pxlab.pxl.RatingScaleTickCodes
	 */
	public ExPar TickType = new ExPar(GEOMETRY_EDITOR,
			RatingScaleTickCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.RatingScaleTickCodes.BAR_TICKS"),
			"Scale tick type");
	/** Width of scale bar and tick lines. */
	public ExPar LineWidth = new ExPar(VERSCREENSIZE, new ExParValue(16),
			"Scale bars line width");
	/**
	 * Label type code.
	 * 
	 * @see de.pxlab.pxl.RatingScaleLabelCodes
	 */
	public ExPar LabelType = new ExPar(GEOMETRY_EDITOR,
			RatingScaleLabelCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.RatingScaleLabelCodes.NUMBER_LABELS"),
			"Scale label type");
	/** Distance of scale labels to the scale. */
	public ExPar LabelDistance = new ExPar(VERSCREENSIZE, new ExParValue(12),
			"Distance between scale labels and scale bar");
	/** Scale labels for text type tick labels. */
	private String[] ls = { "more\nnegative", "more\npositive" };
	public ExPar Labels = new ExPar(STRING, new ExParValue(ls), "Scale labels");
	/** Scale label font size. */
	public ExPar ScaleFontSize = new ExPar(
			VERSCREENSIZE,
			// new ExParValue(32),
			new ExParValue(new ExParExpression(ExParExpression.IDIV),
					new ExParValue(new ExParExpression(
							ExParExpression.SCREEN_HEIGHT)), new ExParValue(28)),
			"Scale labels font size");
	/**
	 * Pointer type code.
	 * 
	 * @see de.pxlab.pxl.RatingScalePointerCodes
	 */
	public ExPar PointerType = new ExPar(GEOMETRY_EDITOR,
			RatingScalePointerCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.RatingScalePointerCodes.DOWN_POINTER"),
			"Scale pointer type");
	/** Size of the pointer. */
	public ExPar PointerSize = new ExPar(VERSCREENSIZE, new ExParValue(36),
			"Scale pointer size");
	/** Flag to show left and right anchor labels. */
	public ExPar AnchorLabels = new ExPar(FLAG, new ExParValue(0),
			"Flag to show anchor labels");
	/** Distance of left/right anchor text to the scale. */
	public ExPar AnchorDistance = new ExPar(HORSCREENSIZE, new ExParValue(20),
			"Distance between anchor text and scale bar");
	/** Initial/current scale value. */
	public ExPar ScaleValue = new ExPar(-5.0, 5.0, new ExParValue(0.0),
			"Scale Pointer Value");
	/**
	 * Rounding factor for scale values. This defines the fractional resolution
	 * of the scale values. Thus a rounding factor of 100 creates ScaleValue
	 * values with two fractional digits.
	 */
	public ExPar RoundingFactor = new ExPar(1.0, 1000.0, new ExParValue(10.0),
			"Scale Value Rounding Factor");
	// --------------------------------------------------------------------
	// ScreenButton implementation
	// --------------------------------------------------------------------
	/**
	 * Size of the screen button for stopping mouse tracking response intervals.
	 * If this is 0 then no screen button is shown. If this is a single non-zero
	 * number then it is the screen button's height. If this parameter contains
	 * two numbers then these are used as the screen button's width and height
	 * respectively.
	 */
	public ExPar ScreenButtonSize = new ExPar(HORSCREENSIZE, new ExParValue(0),
			"Screen button size");
	/**
	 * Position of the screen button for stopping mouse tracking response
	 * intervals. If this is a single number then it is a position code as
	 * defined by class PositionReferenceCodes. If this parameter contains two
	 * numbers then they will be used as the (x,y) position of the screen button
	 * center.
	 * 
	 * @see de.pxlab.pxl.PositionReferenceCodes
	 */
	public ExPar ScreenButtonPosition = new ExPar(GEOMETRY_EDITOR,
			PositionReferenceCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.PositionReferenceCodes.BASE_RIGHT"),
			"Screen button position");
	/**
	 * Text on the screen button for stopping mouse tracking response intervals.
	 */
	public ExPar ScreenButtonText = new ExPar(STRING, new ExParValue("OK"),
			"Screen button text");
	/**
	 * Color of the screen button area for stopping mouse tracking response
	 * intervals.
	 */
	public ExPar ScreenButtonColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Screen button color");
	/**
	 * Color of the text label on the screen button for stopping mouse tracking
	 * response intervals.
	 */
	public ExPar ScreenButtonTextColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.DARK_GRAY)),
			"Screen button text color");
	// --------------------------------------------------------------------
	// End of ScreenButton implementation
	// --------------------------------------------------------------------
	/**
	 * If true then stopping this display is only allowed if the scale value is
	 * valid.
	 */
	public ExPar DisableNonSelection = new ExPar(FLAG, new ExParValue(1),
			"Disable stopping with invalid scale values");

	public RatingScale() {
		setTitleAndTopic("Rating Scale and Text Paragraph", QUESTIONNAIRE_DSP);
		ReferencePoint.set(new ExParValueConstant(
				"de.pxlab.pxl.PositionReferenceCodes.BASE_CENTER"));
		LocationY.set(-40);
		Text.set("Do you feel more positive or \nmore negative about this?");
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.MOUSE_TRACKING_KEY_TIMER"));
	}
	protected RatingScaleElement ratingScale;
	protected ScreenButtonElement screenButton;

	protected int create() {
		int i = super.create();
		ratingScale = new RatingScaleElement(BarColor, TickColor, PointerColor);
		enterDisplayElement(ratingScale, group[0]);
		screenButton = new ScreenButtonElement(ScreenButtonColor,
				ScreenButtonTextColor);
		enterDisplayElement(screenButton, group[0]);
		return i;
	}

	protected void computeGeometry() {
		super.computeGeometry();
		ratingScale.setProperties(ScaleLocationX.getInt(),
				ScaleLocationY.getInt(), ScaleWidth.getInt(),
				ScaleHeight.getInt(), ScaleReferencePoint.getInt(),
				NumberOfTicks.getInt(), LowestTick.getInt(), TickStep.getInt(),
				TickType.getInt(), PointerType.getInt(), LabelType.getInt(),
				AnchorLabels.getFlag(), Labels.getStringArray(),
				LineWidth.getInt(), PointerSize.getInt(),
				ScaleFontSize.getInt(), LabelDistance.getInt(),
				AnchorDistance.getInt());
		ratingScale.setPointer(ScaleValue.getDouble());
		setScreenButton(screenButton.setProperties(
				ScreenButtonPosition.getIntArray(),
				ScreenButtonSize.getIntArray(), ScreenButtonText.getString()));
	}

	/** This is a hook into the mouse button pressed event handler. */
	protected boolean pointerActivated() {
		return updatePointer(pointerActivationX, pointerActivationY);
	}

	/** This is a hook into the mouse button released event handler. */
	protected boolean pointerReleased() {
		return updatePointer(pointerReleaseX, pointerReleaseY);
	}

	protected boolean pointerDragged() {
		return updatePointer(pointerCurrentX, pointerCurrentY);
	}

	private boolean updatePointer(int x, int y) {
		ratingScale.setPointer(x, y);
		if (ratingScale.hasValidPointer()) {
			double v = ratingScale.getPointerValue();
			// System.out.println("Pointer position = " + v);
			int rf = RoundingFactor.getInt();
			ScaleValue.set((double) (Math.round(v * rf)) / rf);
			ratingScale.setPointer(ScaleValue.getDouble());
			// System.out.println("Scale value set to " +
			// ScaleValue.getDouble());
		}
		return true;
	}

	/**
	 * Check whether it is allowed to stop the timer. If the flag
	 * DisableNonSelection is set then timer stopping is allowed only if the
	 * scale has a valid pointer.
	 * 
	 * @return true if DisableNonSelection is false or the scale has a valid
	 *         pointer.
	 */
	public boolean getAllowTimerStop(int rc) {
		if (DisableNonSelection.getFlag()) {
			return ratingScale.hasValidPointer();
		} else {
			return true;
		}
	}
}
