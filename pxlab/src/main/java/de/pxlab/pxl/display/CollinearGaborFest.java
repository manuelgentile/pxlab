package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * A series of collinear Gabor patches.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
public class CollinearGaborFest extends GaborFest {
	/** Numer of Gabor patterns to be shown. */
	public ExPar NumberOfGabors = new ExPar(SMALL_INT, new ExParValue(5),
			"Number of Gabor patterns");

	public CollinearGaborFest() {
		setTitleAndTopic("Sequence of Collinear Gabor Patterns", GRATING_DSP);
	}

	protected void computeGeometry() {
		// System.out.println("CollinearGaborFest.computeGeometry()");
		super.computeGeometry();
		double[] f = Frequency.getDoubleArray();
		double[] p = Phase.getDoubleArray();
		double[] a = Amplitude.getDoubleArray();
		double[] o = Orientation.getDoubleArray();
		double pp = p[0];
		if (p.length > 1)
			pp = p[1];
		grating.setCollinearGabors(NumberOfGabors.getInt(), f[0], p[0], pp,
				a[0], o[0]);
	}
}
