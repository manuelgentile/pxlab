package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * An arbitrary geometric path which may be filled. The sequence of path
 * elements is defined by command tags and coordinates in the experimental
 * parameter Path. The available commands for defining a path are described in
 * class <a href="../../de.pxlab.awtx.Painter.html">de.pxlab.awtx.Painter</a>.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see de.pxlab.awtx.Painter
 */
/*
 * 
 * 2007/05/08
 */
public class GeometricPath extends Display {
	/** Defines the sequence of path elements and commands. */
	public ExPar Path = new ExPar(STRING, new ExParValue(
			"M 0,-60 C 50,-100,100,-40,0,60 C -100,-40,-50,-100,0,-60 Z"),
			"Path elements and commands");
	/** Scaling factor for the coordinates. */
	public ExPar ScalingFactor = new ExPar(DOUBLE, 0.0, 100.0, new ExParValue(
			1.0), "Scaling factor");
	/** Horizontal shift relative to the screen center. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal center position");
	/** Vertical shift relative to the screen center. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical center position");
	/** Width of the outline. */
	public ExPar LineWidth = new ExPar(SMALL_SCREENSIZE, new ExParValue(6),
			"Width of outline");
	/** Outline color. */
	public ExPar LineColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.YELLOW)), "Outline color");
	/** Fill color. */
	public ExPar FillColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.RED)), "Fill color");
	/** If true then the path is filled. */
	public ExPar Fill = new ExPar(FLAG, new ExParValue(1), "Fill flag");
	/** If true then the location is marked. */
	public ExPar ShowLocation = new ExPar(FLAG, new ExParValue(1),
			"Show location flag");

	public GeometricPath() {
		setTitleAndTopic("Geometric Path", SIMPLE_GEOMETRY_DSP);
	}
	protected PathElement path;
	protected DisplayElement fixMarkElement;

	protected int create() {
		enterDisplayElement(
				fixMarkElement = new FixationMarkElement(LineColor), group[0]);
		path = new PathElement(LineColor, FillColor);
		int d = enterDisplayElement(path, group[0]);
		defaultTiming(0);
		return d;
	}

	protected void computeGeometry() {
		fixMarkElement.setLocation(LocationX.getInt(), LocationY.getInt());
		int s = ShowLocation.getFlag() ? 20 : 0;
		fixMarkElement.setSize(s, s);
		path.setProperties(LocationX.getInt(), LocationY.getInt(),
				ScalingFactor.getDouble(), LineWidth.getInt(), Fill.getFlag(),
				Path.getStringArray());
	}
}
