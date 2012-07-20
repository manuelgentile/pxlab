package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A framed rod embedded into a circular disk. May be used to show the
 * Rod-and-Frame-Illusion.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2007/01/28
 */
public class FramedRod extends Display {
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.GRAY)), "Color of rod and frame");
	public ExPar DiskColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.WHITE)),
			"Color of background disk");
	public ExPar DiskSize = new ExPar(SCREENSIZE, new ExParValue(560),
			"Background disk size");
	public ExPar FrameSize = new ExPar(SCREENSIZE, new ExParValue(300),
			"Frame size");
	public ExPar RodLength = new ExPar(SCREENSIZE, new ExParValue(100),
			"Rod length");
	public ExPar LineWidth = new ExPar(SMALL_SCREENSIZE, new ExParValue(5),
			"Width of lines");
	public ExPar FrameOrientation = new ExPar(INTEGER, -180, 180,
			new ExParValue(10), "Frame orientation");
	public ExPar RodOrientation = new ExPar(INTEGER, -180, 180, new ExParValue(
			-5), "Rod orientation");
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal position");
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical position");

	public FramedRod() {
		setTitleAndTopic("Framed Rod", COMPLEX_GEOMETRY_DSP);
	}
	private int disk, frame, rod;

	protected int create() {
		disk = enterDisplayElement(new Oval(this.DiskColor), group[0]);
		frame = enterDisplayElement(new PolyLineClosed(this.Color), group[0]);
		rod = enterDisplayElement(new PolyLine(this.Color), group[0]);
		defaultTiming(0);
		return frame;
	}

	protected void computeGeometry() {
		int ds = DiskSize.getInt();
		int fs = FrameSize.getInt();
		int rs = RodLength.getInt();
		int w = LineWidth.getInt();
		int x = LocationX.getInt();
		int y = LocationY.getInt();
		((Oval) getDisplayElement(disk))
				.setRect(x - ds / 2, y - ds / 2, ds, ds);
		int[] px = new int[4];
		int[] py = new int[4];
		px[0] = px[3] = x - fs / 2;
		py[0] = py[1] = y - fs / 2;
		px[1] = px[2] = x - fs / 2 + fs;
		py[2] = py[3] = y - fs / 2 + fs;
		PolyLineClosed pc = (PolyLineClosed) getDisplayElement(frame);
		pc.setPolygon(new Polygon(px, py, 4));
		pc.setRotation(FrameOrientation.getInt());
		pc.setLineWidth(w);
		px[0] = px[1] = x;
		py[0] = y - rs / 2;
		py[1] = y - rs / 2 + rs;
		PolyLine pl = (PolyLine) getDisplayElement(rod);
		pl.setPolygon(new Polygon(px, py, 2));
		pl.setRotation(RodOrientation.getInt());
		pl.setLineWidth(w);
	}
}
