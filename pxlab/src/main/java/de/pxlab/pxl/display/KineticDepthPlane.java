package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;
import java.util.*;
import de.pxlab.util.Randomizer;

/**
 * This is a field of random dots which induces visual depth by virtual rotation
 * in 3D space.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 06/11/02
 */
public class KineticDepthPlane extends RandomDotField {
	public ExPar Orientation = new ExPar(ANGLE, new ExParValue(15, 0, 180),
			"Angle in Space");

	public KineticDepthPlane() {
		setTitleAndTopic("Plane with Kinetic Depth", RANDOM_DOT_DSP | DEMO);
		DotDuration.set(100);
		Probability.set(0.0);
		NumberOfDots.set(Width.getInt() * Height.getInt() / 50);
		DotSize.set(1);
		RasterSize.set(1);
		Width.set(400);
		Height.set(400);
	}

	public boolean isAnimated() {
		return (true);
	}
	protected int fpc = 30;

	protected int create() {
		setFramesPerCycle(fpc);
		return (super.create());
	}

	protected void computeGeometry() {
		super.computeGeometry();
		transform(Orientation.getDouble());
	}

	private void transform(double a) {
		double cos = Math.cos(a / 180.0 * Math.PI);
		int rw = base_rda.getWidth() / 2;
		int dn = base_dotArray.size();
		for (int i = 0; i < dn; i++) {
			((RandomDot) transformed_dotArray.get(i)).x = (int) (cos * (((RandomDot) base_dotArray
					.get(i)).x - rw)) + rw;
		}
	}

	public void computeAnimationFrame(int frame) {
		transform(360.0 * frame / (fpc - 1));
	}
}
