package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.awtx.RotatedRectangle;
import de.pxlab.pxl.*;

/**
 * This subclass of SearchPattern uses geometric objects as targets in visual
 * search.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 06/13/01
 */
public class GeometrySearch extends SearchPattern {
	/** Search object type (0=vertical bars). */
	public ExPar Type = new ExPar(0, 1, new ExParValue(0), "Search object type");
	/** Orientation of the search target. */
	public ExPar Orientation = new ExPar(ANGLE, new ExParValue(16),
			"Target orientation");
	/** Ratio if width to height of the search items. */
	public ExPar WidthToHeightRatio = new ExPar(PROPORT, new ExParValue(0.3),
			"Width to height ratio of search objects");

	public GeometrySearch() {
		setTitleAndTopic("Search for Geometric Properties", SEARCH_DSP);
		TargetColor.set(new ExParValueFunction(ExParExpression.GRAY));
		DistractorColor.set(new ExParValueFunction(ExParExpression.GRAY));
	}
	protected int orientation;
	protected int type;
	protected Polygon target;
	protected Polygon distractor;
	protected int itemWidth;

	protected void computeGeometry() {
		super.computeGeometry();
		type = Type.getInt();
		orientation = Orientation.getInt();
		itemWidth = (int) (itemSize * WidthToHeightRatio.getDouble());
		target = new RotatedRectangle(0, 0, itemWidth, itemSize, orientation);
		distractor = new RotatedRectangle(0, 0, itemWidth, itemSize, 0);
	}

	protected void showDistractorAt(Point p) {
		Polygon pp = new Polygon(distractor.xpoints, distractor.ypoints,
				distractor.npoints);
		pp.translate(p.x, p.y);
		graphics.fillPolygon(pp);
	}

	protected void showTargetAt(Point p) {
		Polygon pp = new Polygon(target.xpoints, target.ypoints, target.npoints);
		pp.translate(p.x, p.y);
		graphics.fillPolygon(pp);
	}
}
