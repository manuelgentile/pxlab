package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * This is a computer monitor adaptation of E G Heinemann's classical experiment
 * on brightness induction using square fields.
 * 
 * <p>
 * Heinemann, E. G. (1955). Simultanous brightness induction as a function of
 * inducing- and test-field luminance. Journal of Experimental Psychology, 50,
 * 89-96.
 * 
 * @author H. Irtel
 * @version 0.4.0
 */
public class BrightnessInductionSquares extends BrightnessInduction {
	public BrightnessInductionSquares() {
		setTitleAndTopic("Brightness Induction Squares",
				COMPLEX_COLOR_MATCHING_DSP);
	}

	protected int create() {
		s1 = enterDisplayElement(new Bar(StandardSurroundColor), group[0]);
		s2 = enterDisplayElement(new Bar(StandardCenterColor), group[0]);
		s3 = enterDisplayElement(new Bar(VariableSurroundColor), group[0]);
		s4 = enterDisplayElement(new Bar(VariableCenterColor), group[0]);
		fixMarkElement = enterDisplayElement(new Cross(FixationMarkColor),
				group[0]);
		defaultTiming(0);
		return (s1);
	}
}
