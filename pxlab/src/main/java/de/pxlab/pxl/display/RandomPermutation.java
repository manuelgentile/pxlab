package de.pxlab.pxl.display;

import de.pxlab.util.Randomizer;
import de.pxlab.pxl.*;

/**
 * Create a random permutation of an array valued parameter.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2008/04/14
 */
public class RandomPermutation extends Display {
	/** Input array. */
	public ExPar InputArray = new ExPar(INTEGER, new ExParValue(0),
			"Input array");
	/** Random permutation output. */
	public ExPar Permutation = new ExPar(RTDATA, new ExParValue(0),
			"Permuted array");

	public RandomPermutation() {
		setTitleAndTopic("Random permutation generator", PARAM_DSP | EXP);
		setVisible(false);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	protected int create() {
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
		String[] a = InputArray.getStringArray();
		String[] b = new String[a.length];
		if (a.length > 1) {
			int[] i = new int[a.length];
			for (int j = 0; j < a.length; j++)
				i[j] = j;
			Randomizer rand = new Randomizer();
			rand.randomize(i);
			for (int j = 0; j < a.length; j++)
				b[j] = a[i[j]];
		} else {
			b[0] = a[0];
		}
		Permutation.set(b);
	}
}
