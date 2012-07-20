package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Shows a M�ller-Lyer figure.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/02/17
 */
public class MuellerLyer extends Display {
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.WHITE)), "Color of lines");
	public ExPar BaseLine = new ExPar(SCREENSIZE, new ExParValue(300),
			"Length of base line");
	public ExPar WingLine = new ExPar(SCREENSIZE, new ExParValue(100),
			"Length of wing lines");
	public ExPar WingAngle = new ExPar(ANGLE, new ExParValue(270.0),
			"Angle between the wings");
	public ExPar LineWidth = new ExPar(SMALL_SCREENSIZE, new ExParValue(6),
			"Width of lines");
	public ExPar Orientation = new ExPar(ANGLE, new ExParValue(0),
			"Orientation of figure");
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal position");
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical position");

	public MuellerLyer() {
		setTitleAndTopic("M�ller-Lyer figure", COMPLEX_GEOMETRY_DSP);
	}
	private PolyLine base, wing1, wing2;
	private int[] base_x, base_y, wing1_x, wing1_y, wing2_x, wing2_y;

	protected int create() {
		base_x = new int[2];
		base_y = new int[2];
		wing1_x = new int[3];
		wing1_y = new int[3];
		wing2_x = new int[3];
		wing2_y = new int[3];
		int b = enterDisplayElement(base = new PolyLine(this.Color), group[0]);
		enterDisplayElement(wing1 = new PolyLine(this.Color), group[0]);
		enterDisplayElement(wing2 = new PolyLine(this.Color), group[0]);
		defaultTiming(0);
		return b;
	}

	protected void computeGeometry() {
		base.setLineWidth(LineWidth.getInt());
		wing1.setLineWidth(LineWidth.getInt());
		wing2.setLineWidth(LineWidth.getInt());
		base.setRotation(Orientation.getDouble());
		wing1.setRotation(Orientation.getDouble());
		wing2.setRotation(Orientation.getDouble());
		base.setTranslation(LocationX.getInt(), LocationY.getInt());
		wing1.setTranslation(LocationX.getInt(), LocationY.getInt());
		wing2.setTranslation(LocationX.getInt(), LocationY.getInt());
		double wa = WingAngle.getDouble();
		double a = wa / (2.0 * 180.0) * Math.PI;
		double cs = Math.cos(a);
		double sn = Math.sin(a);
		double w = WingLine.getDouble();
		int wdx = (int) Math.round(w * cs);
		int wdy = (int) Math.round(w * sn);
		int b = BaseLine.getInt();
		int d = 0;
		if ((w > 0.0) && (wa < 180.0)) {
			d = (LineWidth.getInt());
		}
		int x1 = -b / 2;
		int x2 = x1 + b - 1;
		base_x[0] = x1 + d;
		base_x[1] = x2 - d;
		base_y[0] = 0;
		base_y[1] = 0;
		wing1_x[0] = x1 + wdx;
		wing1_x[1] = x1;
		wing1_x[2] = x1 + wdx;
		wing1_y[0] = base_y[0] - wdy;
		wing1_y[1] = base_y[0];
		wing1_y[2] = base_y[0] + wdy;
		wing2_x[0] = x2 - wdx;
		wing2_x[1] = x2;
		wing2_x[2] = x2 - wdx;
		wing2_y[0] = base_y[1] - wdy;
		wing2_y[1] = base_y[1];
		wing2_y[2] = base_y[1] + wdy;
		base.setPolygon(new Polygon(base_x, base_y, 2));
		wing1.setPolygon(new Polygon(wing1_x, wing1_y, 3));
		wing2.setPolygon(new Polygon(wing2_x, wing2_y, 3));
	}
}
