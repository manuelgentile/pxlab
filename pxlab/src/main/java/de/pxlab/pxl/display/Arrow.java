package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A filled polygon which looks like an arrow with two heads and a shaft and
 * whose orientation may be adjusted.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*


  */
public class Arrow extends Display {
	/** Width of right arrow head. */
	public ExPar RightHeadWidth = new ExPar(SCREENSIZE, new ExParValue(180),
			"Width of right arrow head");
	/** Length of right arrow head. */
	public ExPar RightHeadLength = new ExPar(SCREENPOS, new ExParValue(180),
			"Length of right arrow head");
	/** Width of left arrow head. */
	public ExPar LeftHeadWidth = new ExPar(SCREENSIZE, new ExParValue(0),
			"Width of left arrow head");
	/** Length of left arrow head. */
	public ExPar LeftHeadLength = new ExPar(SCREENPOS, new ExParValue(0),
			"Length of left arrow head");
	/** Width of the arrow shaft. */
	public ExPar ShaftWidth = new ExPar(SCREENSIZE, new ExParValue(120),
			"Width of arrow shaft");
	/** Length of the arrow shaft. */
	public ExPar ShaftLength = new ExPar(SCREENSIZE, new ExParValue(200),
			"Length of arrow shaft");
	/**
	 * The orientation of the corner opposing the triangle base. This
	 * corresponds to the direction where the arrow points to. A degree value of
	 * 0 corresponds to a right pointing arrow.
	 */
	public ExPar Orientation = new ExPar(ANGLE, new ExParValue(0),
			"Orientation of arrow");
	/** Horizontal position of the base line center. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal position of arrow");
	/** Vertical position of the base line center. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical position of arrow");
	/** The color of the triangle. */
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.RED)), "Color of the arrow");

	/** Constructor creating the title of the display. */
	public Arrow() {
		setTitleAndTopic("Arrow", COMPLEX_GEOMETRY_DSP);
	}
	private int s1;

	protected int create() {
		int[] xp = new int[10];
		int[] yp = new int[10];
		s1 = enterDisplayElement(new de.pxlab.pxl.FilledPolygon(this.Color,
				new Polygon(xp, yp, 10)), group[0]);
		defaultTiming(0);
		return (s1);
	}

	protected void computeGeometry() {
		de.pxlab.pxl.FilledPolygon pa = (de.pxlab.pxl.FilledPolygon) getDisplayElement(s1);
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
		pa.setTranslation(LocationX.getInt(), LocationY.getInt());
	}
}
