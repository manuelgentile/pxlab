package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;
import java.util.ArrayList;
import de.pxlab.util.Randomizer;

/**
 * An animated field of random dots which has a moving subset. The subset moves
 * into a certain direction for a certain distance. All other dots remain fixed.
 * 
 * <p>
 * The implementation shows two successive frames of random dots where the
 * second frame has some of the dots moved to a different position. Motion
 * length and direction are controlled by parameters.
 * 
 * @version 0.2.0
 */
/*
 * 
 * 06/11/02
 */
public class RandomDotMotionField extends RandomDotField {
	/** The proportion of dots which actually are moving. */
	public ExPar ProportionOfMovingDots = new ExPar(PROPORT,
			new ExParValue(0.2), "Proportion of moving dots");
	/** Currently not used. */
	public ExPar MotionSubset = new ExPar(1, 20, new ExParValue(4),
			"Motion subset");
	/**
	 * Length of the motion vector in screen pixel space. This is the length of
	 * the displacement for each moving dot measured in screen pixel space.
	 */
	public ExPar MotionVectorLength = new ExPar(0, 100, new ExParValue(10),
			"Motion vector lenght");
	/** The angle of the motion direction. */
	public ExPar MotionVectorAngle = new ExPar(ANGLE, new ExParValue(90),
			"Motion direction");

	public RandomDotMotionField() {
		setTitleAndTopic("Random Dot Motion Field", RANDOM_DOT_DSP);
		Probability.set(0.0);
		NumberOfDots.set(Width.getInt() * Height.getInt() / 20);
	}

	public boolean isAnimated() {
		return (true);
	}

	protected int create() {
		setFramesPerCycle(2);
		return (super.create());
	}

	protected void computeGeometry() {
		super.computeGeometry();
		double a = MotionVectorAngle.getDouble() / 180.0 * Math.PI;
		double ds = MotionVectorLength.getDouble();
		int dx = (int) Math.round(ds * Math.cos(a));
		int dy = (int) Math.round(ds * Math.sin(a));
		double p = ProportionOfMovingDots.getDouble();
		int dn = base_dotArray.size();
		RandomDot base_dot, transformed_dot;
		Randomizer rnd = new Randomizer();
		int m = 0;
		for (int i = 0; i < dn; i++) {
			base_dot = (RandomDot) base_dotArray.get(i);
			transformed_dot = (RandomDot) transformed_dotArray.get(i);
			if (rnd.nextDouble() < p) {
				transformed_dot.x = base_dot.x + dx;
				transformed_dot.y = base_dot.y + dy;
				transformed_dot.code = RandomDot.SELECTION;
				m++;
			} else {
				transformed_dot.x = base_dot.x;
				transformed_dot.y = base_dot.y;
				transformed_dot.code = RandomDot.BACKGROUND;
			}
		}
		// System.out.println(m + "dots shifted by (" + dx + "," + dy + ")");
	}

	/* Compute the Display object's geometry for */
	public void computeAnimationFrame(int frame) {
		if (frame == 0) {
			transformed_rda.setDotArray(base_dotArray);
		} else {
			transformed_rda.setDotArray(transformed_dotArray);
		}
		needs_resampling = true;
		// System.out.println("Compute frame " + frame);
	}
}
