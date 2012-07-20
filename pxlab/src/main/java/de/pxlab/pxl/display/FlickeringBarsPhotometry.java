package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * A series of horizontal bars for matching the luminance of two different
 * colors. During flicker the colors should fuse and there should be no more
 * flicker if the two colors have the same luminance.
 * 
 * <P>
 * The demonstration also shows that direct comparison and flicker-photometry
 * lead to different results.
 * 
 * <P>
 * An interesting effect may be seen if the eyes are moved or closed shortly
 * while viewing the flicker pattern. The static stimulus pattern is visible
 * then for a short moment because of visible persistence during periods where
 * there is no external stimulus available.
 * 
 * 
 * <P>
 * Pokorny, J. & Smith, V. C. (1986). Colorimetry and color discrimination. In
 * K. R. Boff, L. Kaufman & J. P. Thomas (Eds.) Handbook of perception and human
 * performance. Vol. I. Sensory processes and perception, Kapitel 8. New York:
 * Wiley.
 * 
 * <P>
 * Wyszecki, G. & Stiles, W. S. (1982). Color science (2nd ed.). Ney York:
 * Wiley.
 * 
 * @author H. Irtel
 * @version 0.3.0
 */
/*
 * 
 * 11/01/01 created abstract superclass Photometry and made this a subclass
 * 
 * 03/24/00
 */
public class FlickeringBarsPhotometry extends Photometry {
	public FlickeringBarsPhotometry() {
		setTitleAndTopic("Flicker photometry with color bars", PHOTOMETRY_DSP);
	}
	private int firstDisplayElement;
	private int nBars;

	protected int create() {
		nBars = 0;
		firstDisplayElement = nextDisplayElementIndex();
		enterTiming(FrameTimer, FrameDuration, 0);
		setFramesPerCycle(2);
		return (firstDisplayElement);
	}

	protected void computeGeometry() {
		computeColorsFromLuminance();
		int n = NumberOfElements.getInt();
		if (nBars != n) {
			removeDisplayElements(firstDisplayElement);
			for (int i = 0; i < n; i += 2) {
				enterDisplayElement(new Bar(MatchingColor), group[0]);
				enterDisplayElement(new Bar(TestColor), group[0]);
			}
			nBars = n;
		}
		Bar b;
		int w = FieldWidth.getInt();
		int h = FieldHeight.getInt();
		int d = (int) (h / nBars);
		h = nBars * d;
		int x = (-w) / 2;
		int y = (-h) / 2;
		for (int i = 0; i < nBars; i++) {
			b = (Bar) getDisplayElement(firstDisplayElement + i);
			b.setRect(x, y, w, d);
			y += d;
		}
	}

	/**
	 * The dynamic minimum flicker display is simple: we only have to draw the
	 * two shape arrays with colors being exchanged after every cycle. Note that
	 * we skip drawing of the background shape at index 0 to speed things up a
	 * little bit.
	 */
	public void computeAnimationFrame(int frame) {
		for (int i = firstDisplayElement; i < displayElementCount(); i += 2) {
			((Bar) getDisplayElement(i))
					.setColorPar((frame == 0) ? MatchingColor : TestColor);
			((Bar) getDisplayElement(i + 1))
					.setColorPar((frame == 0) ? TestColor : MatchingColor);
		}
	}
}
