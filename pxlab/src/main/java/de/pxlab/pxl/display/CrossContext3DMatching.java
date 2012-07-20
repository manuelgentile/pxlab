package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A cross context matching pattern with two center-surround fields and a 3D
 * shadow around the center fields.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 10/09/00
 */
public class CrossContext3DMatching extends Display {
	public ExPar LeftCenterColor = new ExPar(COLOR, new ExParValue(
			new PxlColor(0.42)), "Left center color");
	public ExPar LeftSurroundColor = new ExPar(COLOR, new ExParValue(
			new PxlColor(0.22)), "Left surround color");
	public ExPar RightCenterColor = new ExPar(COLOR, new ExParValue(
			new PxlColor(0.42)), "Right center color");
	public ExPar RightSurroundColor = new ExPar(COLOR, new ExParValue(
			new PxlColor(0.22)), "Right surround color");
	public ExPar ShadowColor = new ExPar(COLOR, new ExParValue(new PxlColor(
			0.05)), "Shadow color");
	public ExPar LightColor = new ExPar(COLOR, new ExParValue(
			new PxlColor(0.55)), "Light color");
	public ExPar CenterSize = new ExPar(PROPORT, new ExParValue(0.4),
			"Center square size");
	public ExPar CenterFrameSize = new ExPar(PROPORT, new ExParValue(0.1),
			"Center square frame size");

	public CrossContext3DMatching() {
		setTitleAndTopic("3D Cross Context Matching",
				COMPLEX_COLOR_MATCHING_DSP);
	}
	private int s1, s2, s3, s3a, s3b, s3c, s3d, s4, s4a, s4b, s4c, s4d;

	protected int create() {
		s1 = enterDisplayElement(new Bar(LeftSurroundColor));
		s2 = enterDisplayElement(new Bar(RightSurroundColor));
		s3a = enterDisplayElement(new FilledPolygon(ShadowColor));
		s3b = enterDisplayElement(new FilledPolygon(ShadowColor));
		s3c = enterDisplayElement(new FilledPolygon(LightColor));
		s3d = enterDisplayElement(new FilledPolygon(LightColor));
		s3 = enterDisplayElement(new Bar(LeftCenterColor), group[0]);
		s4a = enterDisplayElement(new FilledPolygon(ShadowColor));
		s4b = enterDisplayElement(new FilledPolygon(LightColor));
		s4c = enterDisplayElement(new FilledPolygon(ShadowColor));
		s4d = enterDisplayElement(new FilledPolygon(LightColor));
		s4 = enterDisplayElement(new Bar(RightCenterColor), group[0]);
		defaultTiming(0);
		return (s4);
	}
	private int[] xa = new int[4];
	private int[] ya = new int[4];
	private int[] xb = new int[4];
	private int[] yb = new int[4];
	private int[] xc = new int[4];
	private int[] yc = new int[4];
	private int[] xd = new int[4];
	private int[] yd = new int[4];

	protected void computeGeometry() {
		// get the squares
		Rectangle r1 = firstSquareOfTwo(width, height, true);
		Rectangle r2 = secondSquareOfTwo(width, height, true);
		Rectangle r3 = innerRect(r1, CenterSize.getDouble());
		Rectangle r3i = innerRect(r3, 1.0 - CenterFrameSize.getDouble());
		Rectangle r4 = innerRect(r2, CenterSize.getDouble());
		Rectangle r4i = innerRect(r4, 1.0 - CenterFrameSize.getDouble());
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect(r2);
		getDisplayElement(s3).setRect(r3i.x, r3i.y, r3i.width, r3i.height);
		getDisplayElement(s4).setRect(r4i.x, r4i.y, r4i.width, r4i.height);
		// When computing the polygon borders we have to remember
		// that the AWT does not paint a polygon's right/bottom
		// border lines while the PXLab rectangles do.
		// left pattern
		// left border
		xa[0] = r3.x;
		ya[0] = r3.y;
		xa[1] = r3i.x;
		ya[1] = r3i.y;
		xa[2] = r3i.x;
		ya[2] = r3i.y + r3i.height;
		xa[3] = r3.x;
		ya[3] = r3.y + r3.height;
		((FilledPolygon) getDisplayElement(s3a)).setPolygon(new Polygon(xa, ya,
				4));
		// top border
		xb[0] = xa[0];
		yb[0] = ya[0];
		xb[1] = r3.x + r3.width;
		yb[1] = r3.y;
		xb[2] = r3i.x + r3i.width;
		yb[2] = r3i.y;
		xb[3] = xa[1];
		yb[3] = ya[1];
		((FilledPolygon) getDisplayElement(s3b)).setPolygon(new Polygon(xb, yb,
				4));
		// right border
		xc[0] = xb[2];
		yc[0] = yb[2];
		xc[1] = xb[1];
		yc[1] = yb[1];
		xc[2] = r3.x + r3.width;
		yc[2] = r3.y + r3.height;
		xc[3] = r3i.x + r3i.width;
		yc[3] = r3i.y + r3i.height;
		((FilledPolygon) getDisplayElement(s3c)).setPolygon(new Polygon(xc, yc,
				4));
		// bottom border
		xd[0] = xa[2];
		yd[0] = ya[2];
		xd[1] = xc[3];
		yd[1] = yc[3];
		xd[2] = xc[2];
		yd[2] = yc[2];
		xd[3] = xa[3];
		yd[3] = ya[3];
		((FilledPolygon) getDisplayElement(s3d)).setPolygon(new Polygon(xd, yd,
				4));
		// right pattern
		// left border
		xa[0] = r4.x;
		ya[0] = r4.y;
		xa[1] = r4i.x;
		ya[1] = r4i.y;
		xa[2] = r4i.x;
		ya[2] = r4i.y + r4i.height;
		xa[3] = r4.x;
		ya[3] = r4.y + r4.height;
		((FilledPolygon) getDisplayElement(s4a)).setPolygon(new Polygon(xa, ya,
				4));
		// top border
		xb[0] = xa[0];
		yb[0] = ya[0];
		xb[1] = r4.x + r4.width;
		yb[1] = r4.y;
		xb[2] = r4i.x + r4i.width;
		yb[2] = r4i.y;
		xb[3] = xa[1];
		yb[3] = ya[1];
		((FilledPolygon) getDisplayElement(s4b)).setPolygon(new Polygon(xb, yb,
				4));
		// right border
		xc[0] = xb[2];
		yc[0] = yb[2];
		xc[1] = xb[1];
		yc[1] = yb[1];
		xc[2] = r4.x + r4.width;
		yc[2] = r4.y + r4.height;
		xc[3] = r4i.x + r4i.width;
		yc[3] = r4i.y + r4i.height;
		((FilledPolygon) getDisplayElement(s4c)).setPolygon(new Polygon(xc, yc,
				4));
		// bottom border
		xd[0] = xa[2];
		yd[0] = ya[2];
		xd[1] = xc[3];
		yd[1] = yc[3];
		xd[2] = xc[2];
		yd[2] = yc[2];
		xd[3] = xa[3];
		yd[3] = ya[3];
		((FilledPolygon) getDisplayElement(s4d)).setPolygon(new Polygon(xd, yd,
				4));
	}
}
