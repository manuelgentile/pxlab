package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;
import de.pxlab.util.Randomizer;

/**
 * A set of static dots mixed with a set of moving dots which induce apparent
 * motion for the static dots.
 * 
 * @version 0.2.0
 */
/*
 * 
 * 06/11/02
 */
public class RandomDotMotionContrast extends RandomDotMotionThreshold {
	/*
	 * public ExPar CenterSize = new ExPar(PROPORT, new ExParValue(0.33, 0.0,
	 * 1.0), "Center Field Size");
	 */
	public RandomDotMotionContrast() {
		setTitleAndTopic("Motion contrast for Random Dots", RANDOM_DOT_DSP);
		Circular.set(1);
		MotionVectorLength.set(1);
		DotDuration.set(60);
	}

	// private int fpc = 30;
	protected int create() {
		int s = super.create();
		// setFramesPerCycle(fpc);
		return s;
	}

	protected void backgroundMotion() {
		int dn = base_dotArray.size();
		RandomDot transformed_dot;
		int m = 0;
		int vidx = MotionVectorAngle.getInt() % 360;
		int w2 = transformed_rda.getWidth() / 2;
		int h2 = transformed_rda.getHeight() / 2;
		long r2 = (long) w2 * (long) w2 * (long) h2 * (long) h2;
		int rx, ry;
		long rrx, rry;
		int x, y;
		for (int i = 0; i < dn; i++) {
			transformed_dot = (RandomDot) transformed_dotArray.get(i);
			if (transformed_dot.code == RandomDot.BACKGROUND) {
				x = transformed_dot.x + vdx[vidx];
				y = transformed_dot.y + vdy[vidx];
				rx = x - w2;
				ry = y - h2;
				rrx = (long) h2 * (long) rx;
				rry = (long) w2 * (long) ry;
				if ((rrx * rrx + rry * rry) >= r2) {
					transformed_dot.x = (-1) * (transformed_dot.x - w2) + w2;
					transformed_dot.y = (-1) * (transformed_dot.y - h2) + h2;
				} else {
					transformed_dot.x = x;
					transformed_dot.y = y;
				}
			} else {
			}
		}
	}

	/* Compute the Display object's geometry for */
	public void computeAnimationFrame(int frame) {
		super.computeAnimationFrame(1);
		backgroundMotion();
	}
}
