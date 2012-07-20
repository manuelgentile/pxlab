package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Sinusoidal color mixture gratings with 4 different frequencies showing that
 * spatial hue resolution is much lower than spatial brightness resolution.
 * 
 * <P>
 * The visual system's ability to resolve spatial patterns is much better for
 * brightness than for hue. Thus a sine wave grating of colors will be seen as
 * being modulated by brigthness only if the spatial frequency is high enough.
 * With low spatial frequency the hue modulation is noticeable. This effect is
 * used in television broadcasting, where brightness information is transmitted
 * with much higher bandwidth than hue information.
 * 
 * <P>
 * The relevant variable is the spatial frequency of the retinal image. To see
 * this move away from the screen and watch how the gratings with different
 * frequencies successively loose their hue modulation.
 * 
 * <P>
 * Only the basic components of the sine wave may be adjusted. These are the
 * colors of the amplitude maxima and minima. The reason why the highest
 * frequency area looks brighter than the other areas are nonlinearities in the
 * phosphor's luminance response function and spatial crosstalk across video
 * lines.
 * 
 * <P>
 * Hartridge, H. (1947). The visual perception of fine detail. Phil. Trans. Roy.
 * Soc. London, 232, 519-671.
 * 
 * <P>
 * De Valois, R. L. & De Valois, K. K. (1988). Spatial vision. New York: Oxford
 * University Press.
 * 
 * @author M. Hodapp
 * @version 0.1.0
 */
/*
 * 
 * 05/22/00
 */
public class SpatialColorRes extends Display {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.blue)), "Color1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.yellow)), "Color2");

	public SpatialColorRes() {
		setTitleAndTopic("Spatial Color Resolution", GRATING_DSP | DEMO);
	}
	private int s1, s2, s3, s4;
	private int nLines;
	private int x11, x12, x21, x22, x31, x32, x41, x42, y1, y2, y3, y4;

	protected int create() {
		s1 = enterDisplayElement(new Bar(Color2));
		s2 = enterDisplayElement(new Bar(Color2));
		s3 = enterDisplayElement(new Bar(Color2));
		s4 = enterDisplayElement(new Bar(Color2));
		return (s1);
	}

	/**
	 * The shapes are derived from double squares. The transition area size is
	 * an adjustible parameter.
	 */
	protected void computeGeometry() {
		Rectangle r = centeredRect(width, height, 4 * width / 6, 4 * height / 6);
		int rectsizeX = (int) (r.width / 4);
		int rectsizeY = (int) (r.height);
		int rectsizeY2 = rectsizeY / 16;
		int rectsizeY3 = 16 * rectsizeY2;
		int size = (int) (width / 6);
		int rectY = (int) (r.y);
		Rectangle r1 = new Rectangle(r.x, rectY, rectsizeX, rectsizeY3);
		Rectangle r2 = new Rectangle(r.x + size, rectY, rectsizeX, rectsizeY3);
		Rectangle r3 = new Rectangle(r.x + 2 * size, rectY, rectsizeX,
				rectsizeY3);
		Rectangle r4 = new Rectangle(r.x + 3 * size, rectY, rectsizeX,
				rectsizeY3);
		nLines = 2 * (r1.height / 2) / 2;
		x11 = r1.x;
		x12 = r1.x + r1.width;
		y1 = r1.y;
		x21 = r2.x;
		x22 = r2.x + r2.width;
		y2 = r2.y;
		x31 = r3.x;
		x32 = r3.x + r3.width;
		y3 = r3.y;
		x41 = r4.x;
		x42 = r4.x + r4.width;
		y4 = r4.y;
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect(r2);
		getDisplayElement(s3).setRect(r3);
		getDisplayElement(s4).setRect(r4);
	}

	/**
	 * This demo has its own paint method for drawing the non-selectable
	 * transition area.
	 */
	public void show(Graphics g) {
		super.show(g);
		int nLines2 = 2;
		if (nLines > 0) {
			// PxlColor[] ramp = getColor(c1).linearRampTo(getColor(c2),
			// nLines1);
			PxlColor[] ramp1 = Color1.getPxlColor().linearRampTo(
					Color2.getPxlColor(), nLines2);
			int yy1 = y1, yy2 = y2, yy3 = y3, yy4 = y4, xx11 = x11, xx12 = x12;
			for (int i = 0; i < nLines; i++) {
				g.setColor(Color1.getPxlColor().dev());
				g.drawLine(x11, yy1, x12, yy1);
				yy1 += 2;
			}
			int z1 = 2 * (nLines / (nLines2)) / 2;
			for (int j = 0; j < z1; j++) {
				for (int i = 0; i < nLines2; i++) {
					g.setColor(ramp1[i].dev());
					g.drawLine(x21, yy2, x22, yy2);
					yy2 += 1;
				}
				int k = nLines2 - 1;
				for (int n = 0; n < nLines2; n++) {
					g.setColor(ramp1[k].dev());
					g.drawLine(x21, yy2, x22, yy2);
					yy2 += 1;
					k -= 1;
				}
			}
			int nLines3 = 4;
			int z2 = 2 * (nLines / (nLines3)) / 2;
			PxlColor[] ramp2 = Color1.getPxlColor().linearRampTo(
					Color2.getPxlColor(), nLines3);
			for (int j = 0; j < z2; j++) {
				for (int i = 0; i < nLines3; i++) {
					g.setColor(ramp2[i].dev());
					g.drawLine(x31, yy3, x32, yy3);
					yy3 += 1;
				}
				int k = nLines3 - 1;
				for (int n = 0; n < nLines3; n++) {
					g.setColor(ramp2[k].dev());
					g.drawLine(x31, yy3, x32, yy3);
					yy3 += 1;
					k -= 1;
				}
			}
			int nLines4 = 8;
			int z3 = 2 * (nLines / (nLines4)) / 2;
			PxlColor[] ramp3 = Color1.getPxlColor().linearRampTo(
					Color2.getPxlColor(), nLines4);
			for (int j = 0; j < z3; j++) {
				for (int i = 0; i < nLines4; i++) {
					g.setColor(ramp3[i].dev());
					g.drawLine(x41, yy4, x42, yy4);
					yy4 += 1;
				}
				int k = nLines4 - 1;
				for (int n = 0; n < nLines4; n++) {
					g.setColor(ramp3[k].dev());
					g.drawLine(x41, yy4, x42, yy4);
					yy4 += 1;
					k -= 1;
				}
			}
		}
	}
}
