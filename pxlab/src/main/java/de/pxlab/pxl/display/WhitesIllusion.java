package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * White's Illusion.
 * 
 * <P>
 * Do the mid-gray stripes look equally bright?
 * 
 * <P>
 * White, M. (1979). A new effect on perceived lightness. Perception, 8,
 * 413-416.
 * 
 * @author E. Reitmayr
 * @version 0.2.1
 */
/*
 * 
 * 05/26/00
 */
public class WhitesIllusion extends Display {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(15.0)),
			"Color1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new PxlColor(44.0)),
			"Color2");
	public ExPar Color3 = new ExPar(COLOR, new ExParValue(new PxlColor(22.0)),
			"Color3");
	public ExPar Color4 = new ExPar(COLOR, new ExParValue(new PxlColor(22.0)),
			"Color4");
	public ExPar BarSize = new ExPar(PROPORT, new ExParValue(0.3),
			"Size of the mid gray bars");

	public WhitesIllusion() {
		setTitleAndTopic("White's Illusion", ASSIMILATION_DSP | DEMO);
	}
	private int nBars = 8;
	private int nRects = 6;
	private int s1;

	protected int create() {
		for (int i = 0; i < nBars; i++) {
			enterDisplayElement(new Bar(Color1));
			enterDisplayElement(new Bar(Color2));
		}
		for (int i = 0; i < nRects; i++) {
			enterDisplayElement(new Bar(Color3), group[0]);
		}
		for (int i = 0; i < nRects; i++) {
			enterDisplayElement(new Bar(Color4), group[0]);
		}
		s1 = enterDisplayElement(new Bar(Color3));
		return (s1);
	}

	protected void computeGeometry() {
		Rectangle r = largeSquare(width, height);
		int s = largeSquareSize(width, height);
		// Make sure that every bar has the same number of video lines
		int d = (int) (s / (2 * nBars));
		s = 2 * nBars * d;
		int x = r.x;
		int y = r.y;
		for (int i = 0; i < nBars; i++) {
			getDisplayElement(1 + i + i).setRect(new Rectangle(x, y, s, d));
			y += d;
			getDisplayElement(1 + i + i + 1).setRect(new Rectangle(x, y, s, d));
			y += d;
		}
		double par = BarSize.getDouble();
		y = r.y;
		for (int i = 2 * nBars + 1; i <= 2 * nBars + nRects; i++) {
			getDisplayElement(i).setRect(
					new Rectangle((int) (x + s / 5), (int) (y + 3 * d),
							(int) (s * par * 3 / 5), d));
			y += 2 * d;
		}
		y = r.y;
		for (int i = 2 * nBars + nRects + 1; i <= 2 * nBars + 2 * nRects; i++) {
			getDisplayElement(i).setRect(
					new Rectangle((int) (x + s - s / 5 - s * par * 3 / 5),
							(int) (y + 2 * d), (int) (s * par * 3 / 5), d));
			y += 2 * d;
		}
	}
}
