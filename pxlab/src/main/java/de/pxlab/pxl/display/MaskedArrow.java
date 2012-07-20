package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * An arrow with a mask inside as used by Vorberg et al (2002) for experiments
 * on subliminal perception. The arrow has two heads, also, screenposition and
 * orientation may be adjusted.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
  */
public class MaskedArrow extends Display {
	/** Width of right arrow head. */
	public ExPar RightHeadWidth = new ExPar(SCREENSIZE, new ExParValue(180),
			"Width of right arrow head");
	/** Length of right arrow head. */
	public ExPar RightHeadLength = new ExPar(SCREENPOS, new ExParValue(180),
			"Length of right arrow head");
	/** Width of left arrow head. */
	public ExPar LeftHeadWidth = new ExPar(SCREENSIZE, new ExParValue(180),
			"Width of left arrow head");
	/** Length of left arrow head. */
	public ExPar LeftHeadLength = new ExPar(SCREENPOS, new ExParValue(180),
			"Length of left arrow head");
	/** Width of the arrow shaft. */
	public ExPar ShaftWidth = new ExPar(SCREENSIZE, new ExParValue(120),
			"Width of arrow shaft");
	/** Length of the arrow shaft. */
	public ExPar ShaftLength = new ExPar(SCREENSIZE, new ExParValue(200),
			"Length of arrow shaft");
	/** Width of mask. */
	public ExPar MaskWidth = new ExPar(SCREENSIZE, new ExParValue(80),
			"Width of mask");
	/** Length of the mask. */
	public ExPar MaskLength = new ExPar(SCREENSIZE, new ExParValue(120),
			"Length of mask");
	/**
	 * The orientation of the corner opposing the triangle base. This
	 * corresponds to the direction where the arrow points to. A degree value of
	 * 0 corresponds to a right pointing arrow.
	 */
	public ExPar Orientation = new ExPar(ANGLE, new ExParValue(0),
			"Orientation of arrow");
	/** Horizontal position of the arrow. */
	public ExPar ArrowLocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal position of arrow");
	/** Vertical position of the arrow. */
	public ExPar ArrowLocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical position of arrow");
	/** Horizontal position of the mask. */
	public ExPar MaskLocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal position of mask");
	/** Vertical position of the mask. */
	public ExPar MaskLocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical position of mask");
	/** The color of arrow. */
	public ExPar ArrowColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.RED)), "Color of the arrow");
	/** The color of the mask. */
	public ExPar MaskColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)), "Color of the mask");

	/** Constructor creating the title of the display. */
	public MaskedArrow() {
		setTitleAndTopic("Masked arrow", COMPLEX_GEOMETRY_DSP);
	}
	private int s1, s2;

	protected int create() {
		int[] xp1 = new int[10];
		int[] yp1 = new int[10];
		int[] xp2 = new int[10];
		int[] yp2 = new int[10];
		s1 = enterDisplayElement(new FilledPolygon(this.ArrowColor,
				new Polygon(xp1, yp1, 10)), group[0]);
		s2 = enterDisplayElement(new FilledPolygon(this.MaskColor, new Polygon(
				xp1, yp1, 10)), group[0]);
		defaultTiming(0);
		return (s1);
	}

	protected void computeGeometry() {
		FilledPolygon pa = (FilledPolygon) getDisplayElement(s1);
		Polygon p = pa.getPolygon();
		int rHW = RightHeadWidth.getInt();
		int rHL = RightHeadLength.getInt();
		int lHW = LeftHeadWidth.getInt();
		int lHL = LeftHeadLength.getInt();
		int sW = ShaftWidth.getInt();
		int sL = ShaftLength.getInt();
		if (rHL >= 0 && lHL >= 0) {
			int l = sL + rHL + lHL;
			p.xpoints[0] = (l / 2) - rHL;
			p.ypoints[0] = -rHW / 2;
			p.xpoints[1] = (l / 2);
			p.ypoints[1] = 0;
			p.xpoints[2] = (l / 2) - rHL;
			p.ypoints[2] = rHW / 2;
			p.xpoints[3] = (l / 2) - rHL;
			p.ypoints[3] = sW / 2;
			p.xpoints[4] = -(l / 2) + lHL;
			p.ypoints[4] = sW / 2;
			p.xpoints[5] = -(l / 2) + lHL;
			p.ypoints[5] = lHW / 2;
			p.xpoints[6] = -(l / 2);
			p.ypoints[6] = 0;
			p.xpoints[7] = -(l / 2) + lHL;
			p.ypoints[7] = -lHW / 2;
			p.xpoints[8] = -(l / 2) + lHL;
			p.ypoints[8] = -sW / 2;
			p.xpoints[9] = (l / 2) - rHL;
			p.ypoints[9] = -sW / 2;
		} else if (rHL < 0 && lHL < 0) {
			int l = sL;
			p.xpoints[0] = (l / 2);
			p.ypoints[0] = -rHW / 2;
			p.xpoints[1] = (l / 2) + rHL;
			p.ypoints[1] = 0;
			p.xpoints[2] = (l / 2);
			p.ypoints[2] = rHW / 2;
			p.xpoints[3] = (l / 2);
			p.ypoints[3] = sW / 2;
			p.xpoints[4] = -(l / 2);
			p.ypoints[4] = sW / 2;
			p.xpoints[5] = -(l / 2);
			p.ypoints[5] = lHW / 2;
			p.xpoints[6] = -(l / 2) - lHL;
			p.ypoints[6] = 0;
			p.xpoints[7] = -(l / 2);
			p.ypoints[7] = -lHW / 2;
			p.xpoints[8] = -(l / 2);
			p.ypoints[8] = -sW / 2;
			p.xpoints[9] = (l / 2);
			p.ypoints[9] = -sW / 2;
		} else if (rHL < 0 && lHL >= 0) {
			int l = sL + lHL;
			p.xpoints[0] = (l / 2);
			p.ypoints[0] = -rHW / 2;
			p.xpoints[1] = (l / 2) + rHL;
			p.ypoints[1] = 0;
			p.xpoints[2] = (l / 2);
			p.ypoints[2] = rHW / 2;
			p.xpoints[3] = (l / 2);
			p.ypoints[3] = sW / 2;
			p.xpoints[4] = -(l / 2) + lHL;
			p.ypoints[4] = sW / 2;
			p.xpoints[5] = -(l / 2) + lHL;
			p.ypoints[5] = lHW / 2;
			p.xpoints[6] = -(l / 2);
			p.ypoints[6] = 0;
			p.xpoints[7] = -(l / 2) + lHL;
			p.ypoints[7] = -lHW / 2;
			p.xpoints[8] = -(l / 2) + lHL;
			p.ypoints[8] = -sW / 2;
			p.xpoints[9] = (l / 2);
			p.ypoints[9] = -sW / 2;
		} else if (rHL >= 0 && lHL < 0) {
			int l = sL + rHL;
			p.xpoints[0] = (l / 2) - rHL;
			p.ypoints[0] = -rHW / 2;
			p.xpoints[1] = (l / 2);
			p.ypoints[1] = 0;
			p.xpoints[2] = (l / 2) - rHL;
			p.ypoints[2] = rHW / 2;
			p.xpoints[3] = (l / 2) - rHL;
			p.ypoints[3] = sW / 2;
			p.xpoints[4] = -(l / 2);
			p.ypoints[4] = sW / 2;
			p.xpoints[5] = -(l / 2);
			p.ypoints[5] = lHW / 2;
			p.xpoints[6] = -(l / 2) - lHL;
			p.ypoints[6] = 0;
			p.xpoints[7] = -(l / 2);
			p.ypoints[7] = -lHW / 2;
			p.xpoints[8] = -(l / 2);
			p.ypoints[8] = -sW / 2;
			p.xpoints[9] = (l / 2) - rHL;
			p.ypoints[9] = -sW / 2;
		}
		pa.setRotation(Orientation.getInt());
		pa.setTranslation(ArrowLocationX.getInt(), ArrowLocationY.getInt());
		FilledPolygon pa2 = (FilledPolygon) getDisplayElement(s2);
		Polygon p2 = pa2.getPolygon();
		p2.xpoints[0] = MaskLength.getInt() / 2;
		p2.ypoints[0] = -MaskWidth.getInt() / 2;
		p2.xpoints[1] = 2 * MaskLength.getInt() / 5;
		p2.ypoints[1] = -MaskWidth.getInt() / 4;
		p2.xpoints[2] = MaskLength.getInt() / 2;
		p2.ypoints[2] = 0;
		p2.xpoints[3] = 2 * MaskLength.getInt() / 5;
		p2.ypoints[3] = MaskWidth.getInt() / 4;
		p2.xpoints[4] = MaskLength.getInt() / 2;
		p2.ypoints[4] = MaskWidth.getInt() / 2;
		p2.xpoints[5] = -MaskLength.getInt() / 2;
		p2.ypoints[5] = MaskWidth.getInt() / 2;
		p2.xpoints[6] = -2 * MaskLength.getInt() / 5;
		p2.ypoints[6] = MaskWidth.getInt() / 4;
		p2.xpoints[7] = -MaskLength.getInt() / 2;
		p2.ypoints[7] = 0;
		p2.xpoints[8] = -2 * MaskLength.getInt() / 5;
		p2.ypoints[8] = -MaskWidth.getInt() / 4;
		p2.xpoints[9] = -MaskLength.getInt() / 2;
		p2.ypoints[9] = -MaskWidth.getInt() / 2;
		pa2.setRotation(Orientation.getInt());
		pa2.setTranslation(MaskLocationX.getInt(), MaskLocationY.getInt());
	}
}
