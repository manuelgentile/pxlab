package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * A grid of horizontal and vertical lines which may be adjusted such that its
 * lines match a physical ruler in order to measure the size of the display
 * screen. Note that the center cross is at PXLab coordinate position (0, 0)
 * which corresponds to (width/2, height/2) in screen pixel numbers.
 * 
 * <p>
 * Hold a ruler to the screen such that a mark of the ruler is aligned with the
 * central line of the grid. Then adjust the grid such that the grid lines are
 * aligned with the ruler's unit marks. This gives you the number of pixel dots
 * per unit of the ruler.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 07/02/02
 */
public class DisplaySizeMeasurement extends Display {
	/** Horizontal unit step size. */
	public ExPar HorizontalUnit = new ExPar(1, 256, new ExParValue(45),
			"Horizontal Unit Size");
	/** Vertical unit step size. */
	public ExPar VerticalUnit = new ExPar(1, 256, new ExParValue(45),
			"Vertical Unit Size");
	/** The color of the lines. */
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.GRAY)), "Color of the Ruler Lines");
	/** The color of the center lines. */
	public ExPar CenterColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.WHITE)),
			"Color of the Center Lines");

	/** Constructor creating the title of the display. */
	public DisplaySizeMeasurement() {
		setTitleAndTopic("Display Size Measurement", COMPLEX_GEOMETRY_DSP);
	}
	private int s1, s2;

	protected int create() {
		s1 = enterDisplayElement(new RulerElement(Color, CenterColor, true),
				group[0]);
		s2 = enterDisplayElement(new RulerElement(Color, CenterColor, false),
				group[0]);
		defaultTiming(0);
		return (s1);
	}

	protected void computeGeometry() {
		RulerElement p = (RulerElement) getDisplayElement(s1);
		p.setUnitSize(HorizontalUnit.getInt());
		p = (RulerElement) getDisplayElement(s2);
		p.setUnitSize(VerticalUnit.getInt());
		ExPar.HorizontalScreenResolution.getValue().set(
				HorizontalUnit.getValue());
		ExPar.VerticalScreenResolution.getValue().set(VerticalUnit.getValue());
	}
}
