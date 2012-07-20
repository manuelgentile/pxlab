package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;
import de.pxlab.util.Randomizer;

/**
 * A series of colored stripes.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2007/10/16
 */
public class ColorStripes extends Display implements StripePatternCodes {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new YxyColor(11.20,
			0.313, 0.329)), "Color of context field 1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new YxyColor(17.78,
			0.313, 0.329)), "Color of context field 2");
	public ExPar Color3 = new ExPar(COLOR, new ExParValue(new YxyColor(19.95,
			0.313, 0.329)), "Color of context field 3");
	public ExPar Color4 = new ExPar(COLOR, new ExParValue(new YxyColor(28.18,
			0.313, 0.329)), "Color of context field 4");
	public ExPar Color5 = new ExPar(COLOR, new ExParValue(new YxyColor(35.48,
			0.313, 0.329)), "Color of context field 5");
	public ExPar Color6 = new ExPar(COLOR, new ExParValue(new YxyColor(50.12,
			0.313, 0.329)), "Color of context field 6");
	public ExPar Color7 = new ExPar(COLOR, new ExParValue(new YxyColor(56.23,
			0.313, 0.329)), "Color of context field 7");
	public ExPar Color8 = new ExPar(COLOR, new ExParValue(new YxyColor(89.12,
			0.313, 0.329)), "Color of context field 8");
	public ExPar Color9 = new ExPar(COLOR, new ExParValue(new YxyColor(89.12,
			0.313, 0.329)), "Color of context field 9");
	public ExPar Color10 = new ExPar(COLOR, new ExParValue(new YxyColor(89.12,
			0.313, 0.329)), "Color of context field 10");
	public ExPar Color11 = new ExPar(COLOR, new ExParValue(new YxyColor(89.12,
			0.313, 0.329)), "Color of context field 11");
	public ExPar Color12 = new ExPar(COLOR, new ExParValue(new YxyColor(89.12,
			0.313, 0.329)), "Color of context field 12");
	public ExPar Color13 = new ExPar(COLOR, new ExParValue(new YxyColor(89.12,
			0.313, 0.329)), "Color of context field 13");
	public ExPar Color14 = new ExPar(COLOR, new ExParValue(new YxyColor(89.12,
			0.313, 0.329)), "Color of context field 14");
	public ExPar Color15 = new ExPar(COLOR, new ExParValue(new YxyColor(89.12,
			0.313, 0.329)), "Color of context field 15");
	public ExPar Color16 = new ExPar(COLOR, new ExParValue(new YxyColor(89.12,
			0.313, 0.329)), "Color of context field 16");
	public ExPar NumberOfColors = new ExPar(1, 16, new ExParValue(4),
			"Number of colors to use");
	public ExPar Pattern = new ExPar(GEOMETRY_EDITOR, StripePatternCodes.class,
			new ExParValueConstant(
					"de.pxlab.pxl.StripePatternCodes.REPETITION_PATTERN"),
			"Stripe pattern type");
	public ExPar StripeWidth = new ExPar(SCREENSIZE, new ExParValue(100, 60),
			"Width of a single stripe");
	public ExPar Width = new ExPar(SCREENSIZE, new ExParValue(500),
			"Maximum width of full pattern");
	public ExPar Height = new ExPar(SCREENSIZE, new ExParValue(500),
			"Height of full pattern");
	public ExPar Gap = new ExPar(SCREENSIZE, new ExParValue(0),
			"Gap between stripes");
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal position");
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical position");
	/**
	 * Reference point for the picture. See <code>PositionReferenceCodes</code>
	 * for a description.
	 */
	public ExPar ReferencePoint = new ExPar(GEOMETRY_EDITOR,
			PositionReferenceCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.PositionReferenceCodes.MIDDLE_CENTER"),
			"Pattern Reference Point");
	/**
	 * Exactly the number of entries in StripeWidth. This corresponds to the
	 * number of stripe elements with different sizes.
	 */
	private int nStripeElements = 0;
	/**
	 * Number of pattern elements. Will depend on the stripe element widths and
	 * the total pattern width.
	 */
	private int nBars = 0;
	private int firstElement;

	public ColorStripes() {
		setTitleAndTopic("Color Stripes", SIMPLE_GEOMETRY_DSP);
	}

	protected int create() {
		firstElement = nextDisplayElementIndex();
		nStripeElements = 0;
		nBars = 0;
		defaultTiming(0);
		return firstElement;
	}

	/*
	 * protected void computeColors() { }
	 */
	protected void computeGeometry() {
		int w;
		int patternWidth = Width.getInt();
		int nextStripeElement;
		int nextStripeWidth;
		int nextColor;
		int[] stripeWidth = StripeWidth.getIntArray();
		int n = stripeWidth.length;
		int[] bw = new int[1000];
		removeDisplayElements(firstElement);
		nStripeElements = n;
		firstElement = nextDisplayElementIndex();
		w = 0;
		initStripeWidthIndex();
		nextStripeElement = nextStripeWidthIndex();
		nextStripeWidth = stripeWidth[nextStripeElement];
		nextColor = 1;
		int k = 0;
		while ((w + nextStripeWidth) <= patternWidth) {
			enterDisplayElement(new Bar(colorPar(nextColor)), group[0]);
			nextColor++;
			if (nextColor > NumberOfColors.getInt())
				nextColor = 1;
			bw[k++] = nextStripeWidth;
			w += nextStripeWidth;
			nextStripeElement = nextStripeWidthIndex();
			nextStripeWidth = stripeWidth[nextStripeElement];
		}
		nBars = k;
		int p = ReferencePoint.getInt();
		int x = DisplayElement.locX(p, LocationX.getInt(), w);
		int h = Height.getInt();
		int y = DisplayElement.rectLocY(p, LocationY.getInt(), h);
		Bar b;
		for (int i = 0; i < nBars; i++) {
			b = (Bar) getDisplayElement(firstElement + i);
			b.setLocation(x, y);
			b.setSize(bw[i], h);
			x += bw[i];
		}
	}
	private int[] stripeWidthIndex;
	private int stripeWidthLength;
	private int lastStripeWidthIndexPointer;

	private void initStripeWidthIndex() {
		stripeWidthLength = StripeWidth.getValue().length;
		stripeWidthIndex = new int[stripeWidthLength];
		for (int i = 0; i < stripeWidthLength; i++)
			stripeWidthIndex[i] = i;
		lastStripeWidthIndexPointer = -1;
	}

	private void shiftStripeWidthIndex() {
		int a = stripeWidthIndex[0];
		for (int i = 1; i < stripeWidthLength; i++)
			stripeWidthIndex[i - 1] = stripeWidthIndex[i];
		stripeWidthIndex[stripeWidthLength - 1] = a;
		// System.out.println("Shifting " + a);
	}
	private static Randomizer rand = new Randomizer();

	private int nextStripeWidthIndex() {
		int x = 0;
		int pattern = Pattern.getInt();
		if (pattern == RANDOM_PATTERN) {
			x = rand.nextInt(stripeWidthLength);
		} else if (pattern == REPETITION_PATTERN) {
			lastStripeWidthIndexPointer++;
			if (lastStripeWidthIndexPointer >= stripeWidthLength) {
				lastStripeWidthIndexPointer = 0;
			}
			x = lastStripeWidthIndexPointer;
		} else if (pattern == SHIFT_PATTERN) {
			lastStripeWidthIndexPointer++;
			if (lastStripeWidthIndexPointer >= stripeWidthLength) {
				lastStripeWidthIndexPointer = 0;
				shiftStripeWidthIndex();
			}
			x = stripeWidthIndex[lastStripeWidthIndexPointer];
		}
		return x;
	}

	private ExPar colorPar(int i) {
		if (i < 0)
			i = -i;
		i = (i - 1) % 16 + 1;
		switch (i) {
		case 1:
			return Color1;
		case 2:
			return Color2;
		case 3:
			return Color3;
		case 4:
			return Color4;
		case 5:
			return Color5;
		case 6:
			return Color6;
		case 7:
			return Color7;
		case 8:
			return Color8;
		case 9:
			return Color9;
		case 10:
			return Color10;
		case 11:
			return Color11;
		case 12:
			return Color12;
		case 13:
			return Color13;
		case 14:
			return Color14;
		case 15:
			return Color15;
		case 16:
			return Color16;
		default:
			return null;
		}
	}
}
