package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Selection from an arbitrary sample of color patches defined by their
 * Yxy-coordinates.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 02/11/00
 */
public class RandomColorSampleSelection extends ColorSampleSelection {
	public RandomColorSampleSelection() {
		setTitleAndTopic("Random color sample and selection", COLOR_SPACES_DSP);
	}
	protected RandomColor randomColor;

	/** Initialize the display list of the demo. */
	protected int create() {
		randomColor = new RandomColor(PxlColor.CS_LabStar);
		return super.create();
	}

	protected void computeColors() {
		int n = NumberOfSelectionColumns.getInt()
				* NumberOfSelectionRows.getInt();
		double[] cs = ColorSample.getDoubleArray();
		if (cs.length != (3 * n)) {
			cs = new double[3 * n];
			ColorSample.set(cs);
		}
		PxlColor a;
		for (int i = 0; i < n; i++) {
			a = randomColor.nextPxlColor();
			cs[i + i + i + 0] = a.getY();
			cs[i + i + i + 1] = a.getx();
			cs[i + i + i + 2] = a.gety();
			// System.out.println(a + "[" + cs[i+i+i+0] + ", " + cs[i+i+i+1] +
			// ", " + cs[i+i+i+2] + "]");
		}
		super.computeColors();
	}

	protected void computeGeometry() {
		super.computeGeometry();
	}
}
