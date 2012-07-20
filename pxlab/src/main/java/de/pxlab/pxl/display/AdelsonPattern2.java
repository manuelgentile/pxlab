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
public class AdelsonPattern2 extends Display {
	public ExPar Width = new ExPar(SCREENSIZE, new ExParValue(52),
			"Width of wall");
	public ExPar Height = new ExPar(SCREENSIZE, new ExParValue(60),
			"Height of wall");
	public ExPar Depth = new ExPar(SCREENSIZE, new ExParValue(22),
			"Depth of ground");
	public ExPar DarkWallColor = new ExPar(COLOR, new ExParValue(new PxlColor(
			0.30)), "Dark wall color");
	public ExPar LightWallColor = new ExPar(COLOR, new ExParValue(new PxlColor(
			0.50)), "Light wall color");
	public ExPar GroundColor = new ExPar(COLOR, new ExParValue(new PxlColor(
			0.70)), "Ground color");
	public ExPar ShadedDarkWallColor = new ExPar(DEPCOLOR, new ExParValue(
			new PxlColor(0.30)), "Dark wall in shadow color");
	public ExPar ShadedLightWallColor = new ExPar(DEPCOLOR, new ExParValue(
			new PxlColor(0.50)), "Light wall in shadow color");
	public ExPar ShadedGroundColor = new ExPar(DEPCOLOR, new ExParValue(
			new PxlColor(0.70)), "Ground in shadow color");
	public ExPar ShadingFactor = new ExPar(PROPORT, new ExParValue(0.8),
			"Shading factor");

	/** Constructor creating the title of the display. */
	public AdelsonPattern2() {
		setTitleAndTopic("Adelson Pattern II", COMPLEX_GEOMETRY_DSP | DEMO);
	}
	private int leftTopWall, leftBottomWall, rightTopWall, rightBottomWall,
			ground;

	protected int create() {
		int[] xp = new int[4];
		int[] yp = new int[4];
		leftTopWall = enterDisplayElement(new FilledPolygon(DarkWallColor,
				new Polygon(xp, yp, 4)), group[0]);
		rightTopWall = enterDisplayElement(new FilledPolygon(LightWallColor,
				new Polygon(xp, yp, 4)), group[0]);
		leftBottomWall = enterDisplayElement(new FilledPolygon(LightWallColor,
				new Polygon(xp, yp, 4)), group[0]);
		rightBottomWall = enterDisplayElement(new FilledPolygon(DarkWallColor,
				new Polygon(xp, yp, 4)), group[0]);
		ground = enterDisplayElement(new FilledPolygon(GroundColor,
				new Polygon(xp, yp, 4)), group[0]);
		defaultTiming(0);
		return (ground);
	}
	private int w, h, d, dy;

	protected void computeGeometry() {
		double s = ShadingFactor.getDouble();
		ShadedDarkWallColor.set(DarkWallColor.getPxlColor().scaled(s));
		ShadedLightWallColor.set(LightWallColor.getPxlColor().scaled(s));
		ShadedGroundColor.set(GroundColor.getPxlColor().scaled(s));
		w = Width.getInt();
		h = Height.getInt();
		d = Depth.getInt();
		int y = (h + d) / 2;
		dy = y + y;
		FilledPolygon pa = (FilledPolygon) getDisplayElement(ground);
		Polygon p = pa.getPolygon();
		p.xpoints[0] = 0;
		p.ypoints[0] = -d;
		p.xpoints[1] = -w;
		p.ypoints[1] = 0;
		p.xpoints[2] = 0;
		p.ypoints[2] = d;
		p.xpoints[3] = w;
		p.ypoints[3] = 0;
		pa = (FilledPolygon) getDisplayElement(leftTopWall);
		p = pa.getPolygon();
		p.xpoints[0] = -w;
		p.ypoints[0] = -y;
		p.xpoints[1] = 0;
		p.ypoints[1] = -y;
		p.xpoints[2] = 0;
		p.ypoints[2] = -d;
		p.xpoints[3] = -w;
		p.ypoints[3] = 0;
		pa = (FilledPolygon) getDisplayElement(rightTopWall);
		p = pa.getPolygon();
		p.xpoints[0] = 0;
		p.ypoints[0] = -y;
		p.xpoints[1] = w;
		p.ypoints[1] = -y;
		p.xpoints[2] = w;
		p.ypoints[2] = 0;
		p.xpoints[3] = 0;
		p.ypoints[3] = -d;
		pa = (FilledPolygon) getDisplayElement(leftBottomWall);
		p = pa.getPolygon();
		p.xpoints[0] = -w;
		p.ypoints[0] = 0;
		p.xpoints[1] = 0;
		p.ypoints[1] = d;
		p.xpoints[2] = 0;
		p.ypoints[2] = y;
		p.xpoints[3] = -w;
		p.ypoints[3] = y;
		pa = (FilledPolygon) getDisplayElement(rightBottomWall);
		p = pa.getPolygon();
		p.xpoints[0] = w;
		p.ypoints[0] = 0;
		p.xpoints[1] = w;
		p.ypoints[1] = y;
		p.xpoints[2] = 0;
		p.ypoints[2] = y;
		p.xpoints[3] = 0;
		p.ypoints[3] = d;
	}

	/**
	 * Overrides the show() method of class Display in order to show multiples
	 * of a single pattern cell.
	 */
	public void show() {
		graphics.setClip(-4 * w, -5 * dy / 2, 8 * w, 5 * dy);
		showCellAt(-w - w - w - w, -dy - dy);
		showCellAt(-w - w, -dy - dy);
		showCellAt(0, -dy - dy);
		showCellAt(w + w, -dy - dy);
		showCellAt(w + w + w + w, -dy - dy);
		showShadedCellAt(-w - w - w, -dy);
		showShadedCellAt(-w, -dy);
		showShadedCellAt(w, -dy);
		showShadedCellAt(w + w + w, -dy);
		showCellAt(-w - w - w - w, 0);
		showCellAt(-w - w, 0);
		showCellAt(0, 0);
		showCellAt(w + w, 0);
		showCellAt(w + w + w + w, 0);
		showShadedCellAt(-w - w - w, dy);
		showShadedCellAt(-w, dy);
		showShadedCellAt(w, dy);
		showShadedCellAt(w + w + w, dy);
		showCellAt(-w - w - w - w, dy + dy);
		showCellAt(-w - w, dy + dy);
		showCellAt(0, dy + dy);
		showCellAt(w + w, dy + dy);
		showCellAt(w + w + w + w, dy + dy);
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

	/** Show a single cell at the given position. */
	protected void showShadedCellAt(int x, int y) {
		FilledPolygon de;
		de = (FilledPolygon) getDisplayElement(ground);
		de.setTranslation(x, y);
		de.show(ShadedGroundColor);
		de = (FilledPolygon) getDisplayElement(leftTopWall);
		de.setTranslation(x, y);
		de.show(ShadedDarkWallColor);
		de = (FilledPolygon) getDisplayElement(rightTopWall);
		de.setTranslation(x, y);
		de.show(ShadedLightWallColor);
		de = (FilledPolygon) getDisplayElement(leftBottomWall);
		de.setTranslation(x, y);
		de.show(ShadedLightWallColor);
		de = (FilledPolygon) getDisplayElement(rightBottomWall);
		de.setTranslation(x, y);
		de.show(ShadedDarkWallColor);
	}
}
