package de.pxlab.pxl.display;

import de.pxlab.util.Randomizer;
import de.pxlab.pxl.*;

/**
 * A random number text string.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class RandomNumber extends TextParagraph {
	/** Lower limit of the random number. */
	public ExPar LowerLimit = new ExPar(0, 1000, new ExParValue(100),
			"Lower limit");
	/** Upper limit of the random number. */
	public ExPar UpperLimit = new ExPar(9, 9999, new ExParValue(999),
			"Upper limit");
	private Randomizer rnd;

	public RandomNumber() {
		setTitleAndTopic("Random Number Text Display", ATTEND_DSP);
		FontSize.set(60.0);
		Alignment.set(new ExParValueConstant(
				"de.pxlab.pxl.AlignmentCodes.CENTER"));
		ReferencePoint.set(new ExParValueConstant(
				"de.pxlab.pxl.PositionReferenceCodes.MIDDLE_CENTER"));
		Text.set("314");
		Width.set(800);
		Wrapping.set(1);
	}

	protected int create() {
		rnd = new Randomizer();
		return (super.create());
	}

	protected void computeGeometry() {
		int n1 = LowerLimit.getInt();
		int n2 = UpperLimit.getInt();
		this.Text.set(String.valueOf(rnd.nextInt(n2 - n1 + 1) + n1));
		super.computeGeometry();
	}
}
