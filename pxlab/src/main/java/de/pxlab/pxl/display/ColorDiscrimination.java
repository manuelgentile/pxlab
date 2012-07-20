package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Two adjacent rectangles whose colors are to be discriminated. The left patch
 * is filled with the color defined by parameter BColor. The right patch is
 * filled with a convex mixture of the colors defined by parameters AColor and
 * BColor. The mixture color is stored in parameter TargetColor. The mixture
 * equation is
 * 
 * <p>
 * TargetColor = AColor * WeightOfA + (1-WeightOfA) * BColor
 * 
 * <p>
 * Thus parameter WeightOfA is a proportion parameter. This display uses
 * dithering to increase color resolution.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see de.pxlab.pxl.Dither
 */
public class ColorDiscrimination extends Display {
	/** Private screen background color. */
	public ExPar BackgroundColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GRAY)), "Background Color");
	/** This is the computed convex mixture of AColor and BColor. */
	public ExPar TargetColor = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.RED)), "Target patch color");
	/**
	 * First component of the convext mixture for computing the TargetColor.
	 */
	public ExPar AColor = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.GREEN)), "Target Mixture component color A");
	/**
	 * Second component of the convext mixture for computing the TargetColor.
	 */
	public ExPar BColor = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.BLUE)), "Target mixture component color B");
	/**
	 * Mixture weight of color AColor in the convext mixture which is used to
	 * compute the TargetColor.
	 */
	public ExPar WeightOfA = new ExPar(PROPORT, new ExParValue(0.5),
			"Mixture weight for component A");
	/**
	 * Size of the combined target and test patch or of a single test patch if.
	 */
	public ExPar PatchSize = new ExPar(SCREENSIZE, new ExParValue(100),
			"Stimulus patch size");
	/**
	 * The size of the dithering matrix used to increase color resolution.
	 */
	public ExPar DitheringType = new ExPar(GEOMETRY_EDITOR,
			DitheringCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.DitheringCodes.NO_DITHERING"),
			"Type of dithering");

	public ColorDiscrimination() {
		setTitleAndTopic("Direct Color Discrimination",
				COLOR_DISCRIMINATION_DSP);
	}
	private Bar bar1, bar2;

	protected int create() {
		setBackgroundColorPar(BackgroundColor);
		bar1 = new Bar(BColor);
		int s = enterDisplayElement(bar1, group[0]);
		bar2 = new Bar(TargetColor);
		enterDisplayElement(bar2, group[0]);
		defaultTiming();
		return (s);
	}

	protected void computeColors() {
		TargetColor.set(AColor.getPxlColor().mix(WeightOfA.getDouble(),
				BColor.getPxlColor()));
	}

	protected void computeGeometry() {
		computeColors();
		int h = PatchSize.getInt();
		int w = h / 2;
		bar1.setRect(-w, -h / 2, w, h);
		bar1.setDither(DitheringType.getInt());
		bar2.setRect(0, -h / 2, w, h);
		bar2.setDither(DitheringType.getInt());
	}
}
