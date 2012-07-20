package de.pxlab.pxl.display;

// import java.awt.*;
import de.pxlab.pxl.*;

/**
 * This is a paragraph of text. The paragraph's width, position, line skip and
 * alignment may be defined by experimental parameters. A single string of text
 * is broken into lines at carriage return characters. If line wrapping is
 * switched on then the text is broken into lines such that it fits the given
 * width but explicit line breaks are preserved.
 * 
 * @author H. Irtel
 * @version 0.1.3
 */
/*
 * 03/15/01 03/23/01 added Width and Wrapping parameter
 * 
 * 06/10/01 allow Width < 1 for proportional screen size
 * 
 * 09/05/02 Set timer default to RELEASE_RESPONSE_TIMER and reference point to
 * MIDDLE_CENTER
 * 
 * 2005/10/18 made Width parameter default depend on screen width.
 */
public class TextParagraph extends TextDisplay {
	/** Width of the text paragraph. */
	public ExPar Width = new ExPar(HORSCREENSIZE,
	// new ExParValue(800),
			new ExParValue(new ExParExpression(ExParExpression.MUL_OP),
					new ExParValue(new ExParExpression(
							ExParExpression.SCREEN_WIDTH)), new ExParValue(
							0.8333)), "Width of text paragraph");
	/**
	 * Flag to activate automatic line breaks such that the text fits the given
	 * width.
	 */
	public ExPar Wrapping = new ExPar(FLAG, new ExParValue(1), "Wrapping mode");
	/**
	 * Somehow emphasize the first line of text. Currently it will be written in
	 * bold face if this flag is set.
	 */
	public ExPar EmphasizeFirstLine = new ExPar(FLAG, new ExParValue(0),
			"Emphasize first line of text");
	/** Text alignment within a paragraph. */
	public ExPar Alignment = new ExPar(GEOMETRY_EDITOR, AlignmentCodes.class,
			new ExParValueConstant("de.pxlab.pxl.AlignmentCodes.LEFT"),
			"Text alignment within paragraph");
	/** Base line skip factor. */
	public ExPar LineSkipFactor = new ExPar(1.0, 5.0, new ExParValue(1.0),
			"Base line skip factor");

	public TextParagraph() {
		setTitleAndTopic("Text paragraph", TEXT_PAR_DSP);
		String[] p = { "A Paragraph of Text presented by PXLab" };
		Text.set(p);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.RELEASE_RESPONSE_TIMER"));
	}
	protected TextParagraphElement textpar;

	/** Initialize the display list of the demo. */
	protected int create() {
		textpar = new TextParagraphElement(this.Color);
		int t = enterDisplayElement(textpar, group[0]);
		defaultTiming(0);
		return (t);
	}

	protected void computeGeometry() {
		// System.out.println("TextParagraph.computeGeometry() Text = " + Text);
		double w = Width.getDouble();
		int ww = (w < 1.0) ? (int) (width * w) : (int) w;
		textpar.setProperties(Text.getStringArray(), FontFamily.getString(),
				FontStyle.getInt(), FontSize.getInt(), LocationX.getInt(),
				LocationY.getInt(), ww, ReferencePoint.getInt(),
				Alignment.getInt(), EmphasizeFirstLine.getFlag(),
				Wrapping.getFlag(), LineSkipFactor.getDouble());
	}
}
