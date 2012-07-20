package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;
import de.pxlab.util.Randomizer;

/**
 * A field of moving random dots. A certain proportion will move coherently into
 * the same direction while the rest of the dots moves at random.
 * 
 * <p>
 * This display models the Stimulus of the following paper:<br>
 * Newsome, W. T., Britten, K. H., & Movshon, J. A. (1989). Neuronal correlates
 * of perceptual decision. Nature, 341, 52-54.
 * 
 * @version 0.2.0
 */
/*
 * 06/11/02
 */
public class RandomDotMotionThreshold extends RandomDotMotionField {
	public RandomDotMotionThreshold() {
		setTitleAndTopic("Motion Threshold Measurement", RANDOM_DOT_DSP);
		NumberOfDots.set(Width.getInt() * Height.getInt() / 50);
	}
	protected int[] vdx;
	protected int[] vdy;
	protected double currentMotionVectorLength = -1.0;

	protected void computeGeometry() {
		super.computeGeometry();
		/* Prepare an array of possible shifts for random selection. */
		if (vdx == null) {
			vdx = new int[360];
			vdy = new int[360];
		}
		double ds = MotionVectorLength.getDouble();
		if (ds != currentMotionVectorLength) {
			for (int i = 0; i < 360; i++) {
				double va = (double) i / 180.0 * Math.PI;
				vdx[i] = (int) Math.round(ds * Math.cos(va));
				vdy[i] = (int) Math.round(ds * Math.sin(va));
			}
			currentMotionVectorLength = ds;
		}
		backgroundMotion();
		// System.out.println(m + "dots shifted by (" + dx + "," + dy + ")");
	}

	protected void backgroundMotion() {
		int dn = base_dotArray.size();
		RandomDot base_dot, transformed_dot;
		Randomizer rnd = new Randomizer();
		int m = 0;
		int vidx;
		for (int i = 0; i < dn; i++) {
			transformed_dot = (RandomDot) transformed_dotArray.get(i);
			if (transformed_dot.code == RandomDot.BACKGROUND) {
				base_dot = (RandomDot) base_dotArray.get(i);
				vidx = rnd.nextInt(360);
				transformed_dot.x = base_dot.x + vdx[vidx];
				transformed_dot.y = base_dot.y + vdy[vidx];
			}
		}
	}

	public void destroy() {
		vdx = null;
		vdy = null;
		super.destroy();
	}

	/* Compute the Display object's geometry for */
	public void computeAnimationFrame(int frame) {
		super.computeAnimationFrame(frame);
		if (frame == 1) {
			backgroundMotion();
		}
	}
}
