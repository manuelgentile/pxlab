package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A filled triangle which looks like an arrow and whose orientation may be
 * adjusted.
 * 
 * @version 0.2.1
 */
/*
 * 01/26/01 use ExPar color for DisplayElement objects
 */
public class AdelsonPattern extends Display {
	public ExPar Width = new ExPar(SCREENSIZE, new ExParValue(28),
			"Width of wall");
	public ExPar Height = new ExPar(SCREENSIZE, new ExParValue(36),
			"Height of wall");
	public ExPar Depth = new ExPar(SCREENSIZE, new ExParValue(12),
			"Depth of ground");
	public ExPar DarkWallColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GRAY)), "Dark wall color");
	public ExPar BrightWallColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Bright wall color");
	public ExPar GroundColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.DARK_GRAY)), "Ground color");

	/** Constructor creating the title of the display. */
	public AdelsonPattern() {
		setTitleAndTopic("Adelson Pattern", COMPLEX_GEOMETRY_DSP | DEMO);
	}
	private int leftWall, rightWall, ground;

	protected int create() {
		int[] xp = new int[4];
		int[] yp = new int[4];
		leftWall = enterDisplayElement(new FilledPolygon(DarkWallColor,
				new Polygon(xp, yp, 4)), group[0]);
		rightWall = enterDisplayElement(new FilledPolygon(BrightWallColor,
				new Polygon(xp, yp, 4)), group[0]);
		ground = enterDisplayElement(new FilledPolygon(GroundColor,
				new Polygon(xp, yp, 4)), group[0]);
		defaultTiming(0);
		return (ground);
	}
	private int w, h, d;

	protected void computeGeometry() {
		w = Width.getInt();
		h = Height.getInt();
		d = Depth.getInt();
		FilledPolygon pa = (FilledPolygon) getDisplayElement(ground);
		Polygon p = pa.getPolygon();
		p.xpoints[0] = 0;
		p.ypoints[0] = 0;
		p.xpoints[1] = -w;
		p.ypoints[1] = d;
		p.xpoints[2] = 0;
		p.ypoints[2] = d + d;
		p.xpoints[3] = w;
		p.ypoints[3] = d;
		pa = (FilledPolygon) getDisplayElement(leftWall);
		p = pa.getPolygon();
		p.xpoints[0] = 0;
		p.ypoints[0] = -h;
		p.xpoints[1] = 0;
		p.ypoints[1] = 0;
		p.xpoints[2] = -w;
		p.ypoints[2] = d;
		p.xpoints[3] = -w;
		p.ypoints[3] = d - h;
		pa = (FilledPolygon) getDisplayElement(rightWall);
		p = pa.getPolygon();
		p.xpoints[0] = 0;
		p.ypoints[0] = -h;
		p.xpoints[1] = 0;
		p.ypoints[1] = 0;
		p.xpoints[2] = w;
		p.ypoints[2] = d;
		p.xpoints[3] = w;
		p.ypoints[3] = d - h;
	}

	/**
	 * Overrides the show() method of class Display in order to show multiples
	 * of a single pattern cell.
	 */
	public void show() {
		showCellAt(-w, -d - h);
		showCellAt(w, -d - h);
		showCellAt(-w - w, 0);
		showCellAt(0, 0);
		showCellAt(w + w, 0);
		showCellAt(-w, d + h);
		showCellAt(w, d + h);
	}

	/** Show a single cell at the given position. */
	protected void showCellAt(int x, int y) {
		FilledPolygon de;
		int n = displayElementList.size();
		for (int i = backgroundFieldIndex + 1; i < n; i++) {
			de = (FilledPolygon) displayElementList.get(i);
			de.setTranslation(x, y);
			de.show();
		}
	}
}
