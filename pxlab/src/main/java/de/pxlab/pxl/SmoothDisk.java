package de.pxlab.pxl;

import java.awt.Rectangle;

/**
 * A filled Disk with a smooth sinusoidal contour. The smooth disk's size
 * parameter refers to its constant color area. The transition region is added
 * to the disks size. This a disk with width 200 and a transition area of size
 * 20 has an effective width of 240 pixels.
 * 
 * @author H. Irtel
 * @version 0.3.1
 */
/*
 * 01/26/01 use an ExPar for color
 * 
 * 02/07/02 added dithering.
 */
public class SmoothDisk extends DisplayElement {
	/*
	 * The parameter which describes the background color. This is used to
	 * compute colors in the transition region. Note that the background color
	 * itself is never drawn.
	 */
	private ExPar bgColorPar;
	/*
	 * Number of lines in the transition region. 0 means that no transition
	 * color is drawn.
	 */
	private int transitionRegion = 0;

	public SmoothDisk(ExPar i, ExPar b) {
		type = DisplayElement.OVAL;
		colorPar = i;
		bgColorPar = b;
	}

	public void setTransitionRegion(int t) {
		transitionRegion = t;
	}

	/*
	 * public SmoothDisk(int x, int y, int w, int h) { type =
	 * DisplayElement.OVAL; setLocation(x, y); setSize(w, h); }
	 * 
	 * public SmoothDisk(ExPar i, int x, int y, int w, int h) { type =
	 * DisplayElement.OVAL; colorPar = i; setLocation(x, y); setSize(w, h); }
	 */
	public void show() {
		if (transitionRegion == 0) {
			if (dither != null) {
				dither.setColor(colorPar.getPxlColor());
				drawDitheredOval(location.x + size.width / 2, location.y
						+ size.height / 2, size.width / 2, size.height / 2);
			} else {
				graphics2D.setColor(colorPar.getDevColor());
				graphics2D.fillOval(location.x, location.y, size.width,
						size.height);
			}
		} else {
			PxlColor[] ramp = bgColorPar.getPxlColor().sinusoidalRampTo(
					colorPar.getPxlColor(), transitionRegion + 2);
			int size2 = size.width + /* 2 * */transitionRegion;
			int xx1 = location.x - transitionRegion / 2, yy1 = location.y
					- transitionRegion / 2;
			for (int i = 0; i <= transitionRegion; i++) {
				if (dither != null) {
					dither.setColor(ramp[i + 1]);
					drawDitheredOval(xx1 + size2 / 2, yy1 + size2 / 2,
							size2 / 2, size2 / 2);
				} else {
					graphics2D.setColor(ramp[i + 1].dev());
					graphics2D.fillOval(xx1, yy1, size2, size2);
				}
				xx1++;
				yy1++;
				size2 -= 2;
			}
		}
		if (validBounds) {
			setBounds(new Rectangle(location, size));
		}
	}
}
