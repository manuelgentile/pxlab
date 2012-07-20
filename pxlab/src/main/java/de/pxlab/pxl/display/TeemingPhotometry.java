package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.awtx.RotatedRectangle;
import de.pxlab.util.Randomizer;
import de.pxlab.pxl.*;

/**
 * A Teeming Brightness Photometer pattern as suggested by G. K. Kranewitter
 * (2001). Helligkeitsmessung durch Bewegungssehen. Dissertation, Universit�t
 * Innsbruck.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 11/01/01
 */
public class TeemingPhotometry extends Photometry {
	/** Line width. */
	public ExPar LineWidth = new ExPar(SMALL_SCREENSIZE, new ExParValue(5),
			"Target Line Width");

	public TeemingPhotometry() {
		setTitleAndTopic("Teeming photometry", PHOTOMETRY_DSP);
	}
	protected int nLines;
	protected int firstDisplayElement;
	protected Randomizer rand;

	protected int create() {
		setBackgroundColorPar(TestColor);
		enterDisplayElement(new Clear(TestColor), group[0]);
		firstDisplayElement = nextDisplayElementIndex();
		enterTiming(FrameTimer, FrameDuration, 0);
		setFramesPerCycle(1);
		nLines = 0;
		rand = new Randomizer();
		return (firstDisplayElement);
	}

	protected void computeGeometry() {
		computeColorsFromLuminance();
		int n = NumberOfElements.getInt();
		if (nLines != n) {
			removeDisplayElements(firstDisplayElement);
			for (int i = 0; i < n; i++) {
				enterDisplayElement(new FilledPolygon(MatchingColor), group[0]);
			}
			nLines = n;
		}
		teemingPattern();
	}

	private void teemingPattern() {
		int lw = LineWidth.getInt();
		int w = FieldWidth.getInt() - lw / 2;
		int h = FieldHeight.getInt() - lw / 2;
		int x1, x2, y1, y2, dx, dy, dd, a;
		for (int i = 0; i < nLines; i++) {
			FilledPolygon pa = (FilledPolygon) getDisplayElement(firstDisplayElement
					+ i);
			x1 = -w / 2 + rand.nextInt(w);
			y1 = -h / 2 + rand.nextInt(h);
			x2 = -w / 2 + rand.nextInt(w);
			y2 = -h / 2 + rand.nextInt(h);
			dx = x2 - x1;
			dy = y2 - y1;
			dd = (int) Math.sqrt(dx * dx + dy * dy);
			a = (int) (Math.atan2(dx, dy) * 180.0 / Math.PI);
			pa.setPolygon(new RotatedRectangle((x1 + x2) / 2, (y1 + y2) / 2,
					lw, dd, a));
		}
	}

	public void computeAnimationFrame(int frame) {
		teemingPattern();
	}
}
