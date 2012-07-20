package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * McCollough-Effect
 * 
 * <P>
 * The McCollough-Effect shows that even complex mechanisms of the visual system
 * do adapt. It is a case of color adaptation which is bound to a special
 * stimulus pattern.
 * 
 * <P>
 * Adaptation is done in special mode. It shows horizontal and vertical stripes
 * in different colors. View these for about 5 to 10 minutes. Exact fixation is
 * not necessary. Pressing a mouse button shows both horizontal and vertical
 * stripes. These appear in the corresponding adaptation colors. The effect may
 * hold on for hours.
 * 
 * <P>
 * McCollough, C. (1965). Color adaptation of edge-detectors in the human visual
 * system. Science, 149, 1115-1116
 * 
 * @author E. Reitmayr
 * @version 0.2.0
 */
/*
 * 
 * 06/02/00
 */
public class McCollough extends FrameAnimation {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.red)), "Color 1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.green)), "Color 2");
	public ExPar Color3 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Color 3");
	public ExPar Color4 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.white)), "Color 4");

	public McCollough() {
		setTitleAndTopic("McCollough-Effekt", ADAPTATION_DSP | DEMO);
		FrameDuration.set(1000);
		FramesPerCycle.setType(RTDATA);
	}
	private int c1, c2, c3, c4;
	private int s1;
	private int nBars = 12;

	protected int create() {
		s1 = nextDisplayElementIndex();
		for (int i = 0; i < (2 * nBars); i += 2) {
			enterDisplayElement(new Bar(Color1), group[0]);
			enterDisplayElement(new Bar(Color3), group[0]);
		}
		for (int i = (2 * nBars); i < (4 * nBars); i += 2) {
			enterDisplayElement(new Bar(Color2), group[0]);
			enterDisplayElement(new Bar(Color3), group[0]);
		}
		for (int i = (4 * nBars); i < (8 * nBars); i += 2) {
			enterDisplayElement(new Bar(Color4), group[0]);
			enterDisplayElement(new Bar(Color3), group[0]);
		}
		int t = enterTiming(FrameTimer, FrameDuration, 0);
		setFramesPerCycle(2);
		return (s1);
	}

	protected void computeGeometry() {
		Bar b;
		Rectangle r1 = mediumSquare(width, height);
		int s = mediumSquareSize(width, height);
		int d = (int) (s / (2 * nBars));
		s = 2 * nBars * d;
		int x = (-s) / 2;
		int y = (-s) / 2;
		for (int i = 0; i < (nBars + nBars); i++) {
			b = (Bar) getDisplayElement(s1 + i);
			b.setRect(x, y, d, s);
			x += d;
		}
		x = (-s) / 2;
		for (int i = (nBars + nBars); i < (4 * nBars); i++) {
			b = (Bar) getDisplayElement(s1 + i);
			b.setRect(x, y, s, d);
			y += d;
		}
		y = (-s) / 2;
		for (int i = (4 * nBars); i < (5 * nBars); i++) {
			b = (Bar) getDisplayElement(s1 + i);
			b.setRect(x, y, (int) (s / 2), d);
			y += d;
		}
		y = (-s) / 2;
		for (int i = (5 * nBars); i < (6 * nBars); i++) {
			b = (Bar) getDisplayElement(s1 + i);
			b.setRect((int) (x + (s / 2)), y, d, (int) (s / 2));
			x += d;
		}
		x = (-s) / 2;
		for (int i = (6 * nBars); i < (7 * nBars); i++) {
			b = (Bar) getDisplayElement(s1 + i);
			b.setRect(x, (int) (y + (s / 2)), d, (int) (s / 2));
			x += d;
		}
		x = (-s) / 2;
		for (int i = (7 * nBars); i < (8 * nBars); i++) {
			b = (Bar) getDisplayElement(s1 + i);
			b.setRect((int) (x + (s / 2)), (int) (y + (s / 2)), (int) (s / 2),
					d);
			y += d;
		}
		y = (-s) / 2;
	}

	public void computeAnimationFrame(int frame) {
		if (frame == 0) {
			for (int i = 0; i < (nBars + nBars); i++) {
				((Bar) getDisplayElement(s1 + i)).show();
			}
		} else {
			for (int i = (nBars + nBars); i < (4 * nBars); i++) {
				((Bar) getDisplayElement(s1 + i)).show();
			}
		}
	}
}
