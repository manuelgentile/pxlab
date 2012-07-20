package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * An abstract class which provides the experimental parameters which are common
 * to all photometry display classes.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
abstract public class Photometry extends FrameAnimation {
	/** The color which is matched to the test color. */
	public ExPar MatchingColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GRAY)), "Matching Color");
	/**
	 * The luminance of the color which is matched to the test color.
	 */
	public ExPar MatchingLuminance = new ExPar(0.0, 200.0,
			new ExParValue(50.0), "Matching Color Luminance");
	/** The color which is to be measured. */
	public ExPar TestColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.RED)), "Test Color");
	/** The luminance of the color which is to be measured. */
	// public ExPar TestLuminance = new ExPar(0.0, 200.0,
	// new ExParValue(40.0), "Test Color Luminance");
	/** Test field width. */
	public ExPar FieldWidth = new ExPar(HORSCREENSIZE, new ExParValue(400),
			"Target Field Width");
	/** Test field height. */
	public ExPar FieldHeight = new ExPar(VERSCREENSIZE, new ExParValue(400),
			"Target Field Height");
	/** Number of subelements of the display. */
	public ExPar NumberOfElements = new ExPar(1, 100, new ExParValue(12),
			"Number of Subelements");

	protected void computeColors() {
		// PxlColor c = MatchingColor.getPxlColor();
		// MatchingLuminance.set(c.getY());
		// c = TestColor.getPxlColor();
		// TestLuminance.set(c.getY());
	}

	protected void computeColorsFromLuminance() {
		PxlColor c = MatchingColor.getPxlColor();
		// System.out.println("Photometry.computeColorsFromLuminance(): MatchingColor[xy] = [ "
		// + c.getx() + ", " + c.gety() + "]");
		PxlColor a = new YxyColor(MatchingLuminance.getDouble(), c.getx(),
				c.gety());
		// System.out.println("Photometry.computeColorsFromLuminance(): MatchingColor[Yxy] = "
		// + a);
		MatchingColor.set(a.clipped());
		// System.out.println("Photometry.computeColorsFromLuminance(): clipped MatchingColor = "
		// + MatchingColor.getPxlColor());
		// c = TestColor.getPxlColor();
		// a = new YxyColor(TestLuminance.getDouble(), c.getx(), c.gety());
		// TestColor.set(a.clipped());
	}
}
