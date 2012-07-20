package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A filled polygon whose position, rotation, and size may be adjusted.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class PolyArea extends Display {
	private int[] p = { -100, 0, 0, -100, 200, 0 };
	/**
	 * Pairs of (x,y)-coordinates of polygon points. The first and last points
	 * are connected to form a closed polygon.
	 */
	public ExPar Points = new ExPar(SCREENPOS, new ExParValue(p),
			"Pairs of polygon points");
	/** Scaling factor for the coordinates. */
	public ExPar ScalingFactor = new ExPar(DOUBLE, 0.0, 100.0, new ExParValue(
			1.0), "Coordinates scaling factor");
	/**
	 * Angle of rotation around the polygon's location as defined by
	 * (LocationX,LocationY).
	 */
	public ExPar Rotation = new ExPar(ANGLE, new ExParValue(0), "Rotation");
	/** Horizontal shift relative to the screen center. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal center position");
	/** Vertical shift relative to the screen center. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical center position");
	/** The color of the polygon. */
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.RED)), "Fill color");

	public PolyArea() {
		setTitleAndTopic("Filled Polygon", SIMPLE_GEOMETRY_DSP);
	}
	protected FilledPolygon fpg;

	protected int create() {
		fpg = new FilledPolygon(this.Color);
		int d = enterDisplayElement(fpg, group[0]);
		defaultTiming(0);
		return (d);
	}

	protected void computeGeometry() {
		// System.out.println("PolyArea.computeGeometry() Points = " +
		// Points.toString());
		int[] points = Points.getIntArray();
		int np = points.length / 2;
		double s = ScalingFactor.getDouble();
		Polygon pg = fpg.getPolygon();
		if ((pg == null) || (pg.npoints != np)) {
			int[] xp = new int[np];
			int[] yp = new int[np];
			pg = new Polygon(xp, yp, np);
			fpg.setPolygon(pg);
		}
		if (s != 1.0) {
			for (int i = 0; i < np; i++) {
				pg.xpoints[i] = (int) Math.round(s * points[i + i]);
				pg.ypoints[i] = (int) Math.round(s * points[i + i + 1]);
			}
		} else {
			for (int i = 0; i < np; i++) {
				pg.xpoints[i] = points[i + i];
				pg.ypoints[i] = points[i + i + 1];
			}
		}
		fpg.setRotation(Rotation.getInt());
		fpg.setTranslation(LocationX.getInt(), LocationY.getInt());
	}
}
