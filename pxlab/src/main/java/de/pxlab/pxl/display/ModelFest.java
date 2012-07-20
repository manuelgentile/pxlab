package de.pxlab.pxl.display;

import java.awt.*;
import java.awt.image.*;

import de.pxlab.pxl.*;

/**
 * A superclass for ModelFest pattern stimuli. See <a
 * href="http://vision.arc.nasa.gov/modelfest/"
 * >http://vision.arc.nasa.gov/modelfest/</a> or <a
 * href="http://www.neurometrics.com/projects/Modelfest/IndexModelfest.htm">
 * http://www.neurometrics.com/projects/Modelfest/IndexModelfest.htm</a> for a
 * description of this set of stimuli.
 * 
 * <p>
 * This implementation strictly separates 4 aspects of each stimulus: the base
 * pattern, the spatial envelope, the temporal envelope, and the colors. At each
 * point in space and time the pattern value is considered to be a mixture
 * weight for the convex mixture of two colors. It thus is easy to extend the
 * patterns from its original monochrome versions to arbitrary color 'lines' in
 * CIE xy-chromaticity space or any of its linear transformations. See <a
 * href="http://www.liv.ac.uk/Psychology/hmp/projects/colfest.html">
 * http://www.liv.ac.uk/Psychology/hmp/projects/colfest.html</a> for an
 * extension of the original ModelFest ideas to color.
 * 
 * <P>
 * Carney, T., Klein, S. A., Tyler, C. W. Silverstein, A. D., Beutter, B., Levi,
 * D., Watson, A. B., Reeves, A. J., Chen, C. C., Makous, W. & Eckstein, M. P.
 * (1999). The development of an image/threshold database for designing and
 * testing human vision models. SPIE proceedings, 3644, 542-551.
 * 
 * 
 * <p>
 * Wuerger, S.M., Watson, A.B., and Ahumada, A. (2002). Towards a
 * spatio-chromatic standard observer for detection, in Human Vision and
 * Electronic Imaging VII, ed. B. E. Rogowitz and T.N. Pappas, Proceedings of
 * SPIE, San Jose, CA, USA, Vol. 4662, pp. 159-172.
 * 
 * <p>
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/09/19
 */
abstract public class ModelFest extends ConvexMixturePattern {
	/** Number of horizontal and vertical pixels in the pattern. */
	public ExPar Size = new ExPar(SCREENSIZE, new ExParValue(256),
			"Pattern width and height in number of pixels");
	/**
	 * Number of pixels per degree visual angle. Used for all metric
	 * calculations.
	 */
	public ExPar PixelPerDegree = new ExPar(SCREENSIZE, new ExParValue(120),
			"Pixel per degree visual angle");
	/** Amplitude of the pattern. */
	public ExPar Amplitude = new ExPar(PROPORT, new ExParValue(1.0),
			"Amplitute of the pattern");
	/**
	 * Horizontal standard deviation of the spatial Gaussian envelope in degrees
	 * of visual angle.
	 */
	public ExPar HorizontalGaussianStandardDeviation = new ExPar(
			SMALL_VISUAL_ANGLE, new ExParValue(0.5),
			"Horizontal standard deviation of gaussian envelope");
	/**
	 * Vertical standard deviation of the spatial Gaussian envelope in degrees
	 * of visual angle.
	 */
	public ExPar VerticalGaussianStandardDeviation = new ExPar(
			SMALL_VISUAL_ANGLE, new ExParValue(0.5),
			"Vertical standard deviation of gaussian envelope");
	/**
	 * If true then an animated version is shown. The animation is a temporal
	 * Gaussian envelope.
	 */
	public ExPar Animation = new ExPar(FLAG, new ExParValue(0),
			"Animation flag");
	/** Number of image frames per animation cycle. */
	public ExPar FramesPerCycle = new ExPar(SMALL_INT, new ExParValue(16),
			"Animation frames per cycle");
	/** Timer for single animation frames. Should always be a clock timer. */
	public ExPar FrameTimer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.RAW_CLOCK_TIMER"),
			"Frame timer");
	/**
	 * Duration of a single animation frame. This is computed from the parameter
	 * Duration and the parameter FramesPerCycle if Animation is activated.
	 */
	public ExPar FrameDuration = new ExPar(DURATION, new ExParValue(1000),
			"Single frame duration");
	/**
	 * Temporal Gaussian standard deviation in milliseconds. The maximum is at
	 * half the Duration value.
	 */
	public ExPar TemporalGaussianStandardDeviation = new ExPar(DURATION,
			new ExParValue(125), "Temporal Gaussian standard deviation");

	public boolean isAnimated() {
		return Animation.getFlag();
	}

	public ModelFest() {
		Width.setType(RTDATA);
		Height.setType(RTDATA);
	}

	protected int create() {
		// System.out.println("ModelFest.create()");
		grating = new ConvexMixtureElement(LowColor, HighColor, FixationColor);
		int s = enterDisplayElement(grating);
		enterTiming(FrameTimer, FrameDuration, 0);
		return (s);
	}

	protected void computeTiming() {
		if (isAnimated()) {
			int fpc = FramesPerCycle.getInt();
			FrameDuration.set(Duration.getInt() / fpc);
			setFramesPerCycle(fpc);
			setFrameIncrement(1);
			grating.setAnimationProperties(Duration.getInt(),
					TemporalGaussianStandardDeviation.getInt(), fpc);
		} else {
			grating.setMovieLength(0);
		}
	}

	protected void computeGeometry() {
		// System.out.println("ModelFest.computeGeometry()");
		grating.setLocation(LocationX.getInt(), LocationY.getInt());
		grating.setSize(Size.getInt(), Size.getInt());
		grating.setFixation(FixationType.getInt(), FixationSize.getInt());
		grating.setPixelPerDegree(PixelPerDegree.getInt());
		grating.setGaussianStandardDeviation(
				HorizontalGaussianStandardDeviation.getDouble(),
				VerticalGaussianStandardDeviation.getDouble());
	}

	public void computeAnimationFrame(int frame) {
		grating.setActiveMovieFrame(frame);
	}

	protected void destroy() {
		if (grating != null)
			grating.destroy();
		super.destroy();
	}
}
