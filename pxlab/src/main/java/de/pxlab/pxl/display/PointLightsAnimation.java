package de.pxlab.pxl.display;

import de.pxlab.pxl.*;
import de.pxlab.util.Randomizer;

/**
 * Simulation of up to four point light sources behind a matte projection screen
 * with amplitude modulation.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 07/23/02
 */
public class PointLightsAnimation extends PointLightsMixed {
	/** Number of image frames per animation cycle. */
	public ExPar FramesPerCycle = new ExPar(SMALL_INT, new ExParValue(16),
			"Animation Frames per Cycle");
	/** Timer for single animation frames. Should always be a clock timer. */
	public ExPar FrameTimer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValue(TimerCodes.RAW_CLOCK_TIMER), "Frame Timer");
	/** Duration of a single animation frame. */
	public ExPar FrameDuration = new ExPar(DURATION, new ExParValue(20),
			"Single Frame Duration");

	public boolean isAnimated() {
		return (true);
	}

	public PointLightsAnimation() {
		setTitleAndTopic("Amplitude modulated point lights", PATTERN_IMAGE_DSP);
		Width.set(200);
		Height.set(200);
	}
	protected LightDistribution[] distributions;
	protected int framesPerCycle = 0;
	protected int framesPerCycle2;

	protected int create() {
		lightDist = new LightDistribution(TargetColor);
		nLights = 1;
		ld_idx = enterDisplayElement(lightDist);
		int t = enterTiming(FrameTimer, FrameDuration, 0);
		rnd = new Randomizer();
		return (ld_idx);
	}

	protected void computeGeometry() {
		// copied from super.computeGeometry() because we need the variables
		int n = NumberOfLights.getInt();
		if ((n != nLights) && (n >= 1) && (n <= 4)) {
			removeDisplayElements(ld_idx);
			lightDist = createLightDistribution(n);
			enterDisplayElement(lightDist);
			nLights = n;
		}
		setColorPositions();
		double[] hsv = HorizontalShift.getDoubleArray();
		for (int i = 0; i < hsv.length; i++)
			hs[i] = hsv[i];
		double[] vsv = VerticalShift.getDoubleArray();
		for (int i = 0; i < vsv.length; i++)
			vs[i] = vsv[i];
		lightDist.setMasking(OvalMask.getFlag());
		lightDist.setDistributionType(DistributionType.getInt());
		setPointLights(lightDist, nLights, hs, vs, 1.0,
				InnerDiameter.getDouble(), OuterDiameter.getDouble());
		int fpc = FramesPerCycle.getInt();
		if (fpc != framesPerCycle) {
			framesPerCycle = FramesPerCycle.getInt();
			framesPerCycle2 = (framesPerCycle + 1) / 2;
			distributions = new LightDistribution[framesPerCycle];
		}
		setFramesPerCycle(framesPerCycle);
		double amp = 0.0;
		// Note that 1.0 is the maximum for amp
		double ampStep = 2.0 * Math.PI / framesPerCycle;
		for (int i = 0; i < framesPerCycle2; i++) {
			distributions[i] = createLightDistribution(nLights);
			distributions[i].setMasking(OvalMask.getFlag());
			distributions[i].setDistributionType(DistributionType.getInt());
			// System.out.println("PointLightsAnimation.computeGeometry() amplitude = "
			// + ());
			setPointLights(distributions[i], nLights, hs, vs,
					(1.0 - Math.cos(amp)) / 2.0, InnerDiameter.getDouble(),
					OuterDiameter.getDouble());
			amp += ampStep;
		}
		// Fill in the second half period
		if ((framesPerCycle % 2) == 0) {
			distributions[framesPerCycle2] = lightDist;
			for (int i = 1; i < framesPerCycle2; i++) {
				distributions[framesPerCycle2 + i] = distributions[framesPerCycle2
						- i];
			}
		} else {
			for (int i = 1; i < framesPerCycle2; i++) {
				distributions[framesPerCycle2 + i - 1] = distributions[framesPerCycle2
						- i];
			}
		}
	}

	public void computeAnimationFrame(int frame) {
		if (frame < framesPerCycle) {
			((LightDistribution) getDisplayElement(ld_idx))
					.setImage(distributions[frame].getImage());
		}
	}
}
