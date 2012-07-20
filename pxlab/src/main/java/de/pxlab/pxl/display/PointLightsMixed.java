package de.pxlab.pxl.display;

import de.pxlab.pxl.*;
import de.pxlab.util.Randomizer;

/**
 * Simulation of up to four point light sources behind a matte projection screen
 * such that the colors may be mixtures of some mixture components.
 * 
 * @author H. Irtel
 * @version 0.3.0
 */
/*
 * 
 * 07/23/02
 * 
 * 07/27/04 allow for isoluminant convex mixture patterns.
 */
public class PointLightsMixed extends Display {
	/**
	 * First component of the convex mixture for computing the TargetColor.
	 */
	public ExPar AColor = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.GREEN)), "Target Mixture Component Color A");
	/**
	 * Second component of the convex mixture for computing the TargetColor.
	 */
	public ExPar BColor = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.BLUE)), "Target Mixture Component Color B");
	/**
	 * Mixture weight of color AColor in the convext mixture which is used to
	 * compute the TargetColor.
	 */
	public ExPar WeightOfA = new ExPar(PROPORT, new ExParValue(0.5),
			"Mixture weight for component A");
	/**
	 * This is the computed convex mixture of AColor and BColor and constitutes
	 * the detection target.
	 */
	public ExPar TargetColor = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.RED)), "Target Spot Color");
	/** First distractor color. */
	public ExPar CColor = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.YELLOW)), "Distractor Color C");
	/** Second distractor color. */
	public ExPar DColor = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.YELLOW)), "Distractor Color D");
	/**
	 * Color position string. This string contains one of the letters T, A, B,
	 * C, D for every test spot on the screen. The letter defines which of the
	 * colors TargetColor, AColor, BColor, CColor, or DColor is shown at the
	 * respective position. Missing positions are filled by color CColor.
	 */
	public ExPar Positions = new ExPar(STRING, new ExParValue("TCCC"),
			"Color Position String");
	/**
	 * If positive then it specifies the target position: 0, 1, ..., 3, If (-1)
	 * then a single character in the Positions parameter is replaced by 'T' at
	 * random. Otherwise nothing is done.
	 */
	public ExPar TargetPosition = new ExPar(INTEGER, new ExParValue(-2),
			"Target Position Indicator");
	/** Image width. */
	public ExPar Width = new ExPar(HORSCREENSIZE, new ExParValue(300),
			"Pattern width");
	/** Image height. */
	public ExPar Height = new ExPar(VERSCREENSIZE, new ExParValue(300),
			"Pattern height");
	/** Number of point lights to show. */
	public ExPar NumberOfLights = new ExPar(1, 4, new ExParValue(1),
			"Number of Lights to Show");
	/** Diameter of the inner part of the distribution. */
	public ExPar InnerDiameter = new ExPar(PROPORT, new ExParValue(0.03),
			"Inner Diameter of Distribution");
	/** Outer diameter or total size of the light distribution. */
	public ExPar OuterDiameter = new ExPar(PROPORT, new ExParValue(0.06),
			"Outer Diameter or Total Size of Distribution");
	/**
	 * Oval masking flag for getting an oval pattern instead of a rectangular
	 * pattern.
	 */
	public ExPar OvalMask = new ExPar(FLAG, new ExParValue(0),
			"Oval Masking Flag");
	/** Distribution type. */
	public ExPar DistributionType = new ExPar(GEOMETRY_EDITOR,
			LightDistributionCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.LightDistributionCodes.DEFOCUSSED_DISK"),
			"Light Distribution Type");
	/** Mixture type. */
	public ExPar MixtureType = new ExPar(GEOMETRY_EDITOR,
			LightMixtureCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.LightMixtureCodes.ADDITIVE_MIXTURE"),
			"Additive or Convex Mixture");
	protected double[] hs = { 0.4, 0.6, 0.4, 0.6 };
	/** Horizontal shift of 4th point light. */
	public ExPar HorizontalShift = new ExPar(PROPORT, new ExParValue(hs),
			"Horizontal Center Shift of Lights");
	protected double[] vs = { 0.4, 0.4, 0.6, 0.6 };
	/** Vertical shift of 4th point light. */
	public ExPar VerticalShift = new ExPar(PROPORT, new ExParValue(vs),
			"Vertical Center Shift of Lights");

	public PointLightsMixed() {
		setTitleAndTopic("Point lights with mixed colors", PATTERN_IMAGE_DSP);
	}
	protected LightDistribution lightDist;
	protected int nLights;
	protected int ld_idx;
	protected Randomizer rnd;
	protected String positions;

	protected int create() {
		lightDist = new LightDistribution(TargetColor);
		nLights = 1;
		ld_idx = enterDisplayElement(lightDist);
		defaultTiming(0);
		rnd = new Randomizer();
		return (ld_idx);
	}

	protected void computeColors() {
		TargetColor.set(AColor.getPxlColor().mix(WeightOfA.getDouble(),
				BColor.getPxlColor()));
		// System.out.println("PointLights.computeColors()");
		lightDist.computeColors();
	}

	protected void computeGeometry() {
		// System.out.println("PointLightsMixed.computeGeometry()");
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
		lightDist.setMixtureType(MixtureType.getInt());
		setPointLights(lightDist, nLights, hs, vs, 1.0,
				InnerDiameter.getDouble(), OuterDiameter.getDouble());
	}

	protected void setColorPositions() {
		positions = Positions.getString();
		if (positions.indexOf('T') < 0) {
			int target = TargetPosition.getInt();
			if (target > -2) {
				char[] p = positions.toCharArray();
				if (target >= 0) {
					if (target < p.length) {
						p[target] = 'T';
						positions = new String(p);
					}
				} else if (target == -1) {
					target = rnd.nextInt(p.length);
					p[target] = 'T';
					positions = new String(p);
					TargetPosition.set(target);
				}
			}
		}
	}

	protected LightDistribution createLightDistribution(int n) {
		switch (n) {
		case 1:
			return new LightDistribution(DColor);
		case 2:
			return new LightDistribution(DColor, DColor);
		case 3:
			return new LightDistribution(DColor, DColor, DColor);
		case 4:
			return new LightDistribution(DColor, DColor, DColor, DColor);
		}
		return null;
	}

	protected void setPointLights(LightDistribution lightDist, int n,
			double[] hs, double[] vs, double amplitude, double inner,
			double outer) {
		lightDist.setColorPar(colorParAt(0));
		switch (n) {
		case 1:
			lightDist.setPointLight(Width.getInt(), Height.getInt(), hs[0],
					vs[0], amplitude, inner, outer);
			break;
		case 2:
			lightDist.setColorPar2(colorParAt(1));
			lightDist.setPointLights(Width.getInt(), Height.getInt(), hs[0],
					vs[0], hs[1], vs[1], amplitude, inner, outer);
			break;
		case 3:
			lightDist.setColorPar2(colorParAt(1));
			lightDist.setColorPar3(colorParAt(2));
			lightDist.setPointLights(Width.getInt(), Height.getInt(), hs[0],
					vs[0], hs[1], vs[1], hs[2], vs[2], amplitude, inner, outer);
			break;
		case 4:
			lightDist.setColorPar2(colorParAt(1));
			lightDist.setColorPar3(colorParAt(2));
			lightDist.setColorPar4(colorParAt(3));
			lightDist.setPointLights(Width.getInt(), Height.getInt(), hs[0],
					vs[0], hs[1], vs[1], hs[2], vs[2], hs[3], vs[3], amplitude,
					inner, outer);
			break;
		}
	}

	protected ExPar colorParAt(int i) {
		ExPar pc = DColor;
		if (positions.length() > i) {
			char c = positions.charAt(i);
			if (c == 'T') {
				pc = TargetColor;
			} else if (c == 'A') {
				pc = AColor;
			} else if (c == 'B') {
				pc = BColor;
			} else if (c == 'C') {
				pc = CColor;
			} else if (c == 'D') {
				pc = DColor;
			}
		}
		return pc;
	}

	protected void destroy() {
		if (lightDist != null)
			lightDist.destroy();
		super.destroy();
	}
}
