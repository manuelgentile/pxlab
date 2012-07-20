package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * This is a simple center/surround display where the center and surround sizes
 * may be adjusted by the user.
 * 
 * @author H. Irtel
 * @version 0.4.2
 */
/*
 * History: 08/10/00 new ExPar definitions 02/04/01 no more ColorTable
 */
public class SimpleCenterSurround extends Display {
	public ExPar CenterColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.RED)), "Center Field color");
	public ExPar SurroundColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.YELLOW)),
			"Surround Field Color");
	public ExPar CenterSize = new ExPar(PROPORT, new ExParValue(0.33),
			"Center Square Size");
	public ExPar SurroundSize = new ExPar(SCREENSIZE, new ExParValue(400),
			"Background Square Size");

	public SimpleCenterSurround() {
		setTitleAndTopic("Simple Center and Surround Field",
				SIMPLE_GEOMETRY_DSP | DEMO);
	}
	private int surround, center;

	protected int create() {
		surround = enterDisplayElement(new Bar(SurroundColor), group[0]);
		center = enterDisplayElement(new Bar(CenterColor), group[0] + group[1]);
		return (center);
	}

	protected void computeGeometry() {
		int surround_size = SurroundSize.getInt();
		int center_size = relSquareSize(surround_size, surround_size,
				CenterSize.getDouble());
		getDisplayElement(surround).setRect(
				centeredSquare(width, height, surround_size));
		getDisplayElement(center).setRect(
				centeredSquare(width, height, center_size));
	}
}
