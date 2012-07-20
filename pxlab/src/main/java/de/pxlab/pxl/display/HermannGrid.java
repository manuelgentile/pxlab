package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * The Hermann grid illusion, named after Ludimar Hermann, a German physiologist
 * who first described the pattern in 1870.
 * 
 * Dark dots appear at the cross points of a Hermann grid. These have been used
 * to measure the size of "perceptive fields".
 * 
 * <p>
 * Hermann, L. (1870) Eine Erscheinung simultanen Contrastes. Pfl�gers Archiv
 * f�r die gesamte Physiologie 3, 13-15.
 * 
 * <P>
 * Spillmann, L. (1971). Foveal perceptive fields in the human visual system
 * measured with simultanous contrast in grids and bars. Pfl�gers Archiv f�r die
 * gesamte Physiologie, 326, 281-299.
 * 
 * <p>
 * See <a href="http://www.leinroden.de/304herfold.htm">Bernd Lingelbach's
 * WWW-page</a> for a history of the Hermann grid.
 * 
 * @version 0.3.0
 */
/*
 * 
 * 03/20/00
 */
public class HermannGrid extends Display {
	public ExPar Color = new ExPar(COLOR, new ExParValue(new PxlColor(0.7)),
			"Grid Color");
	public ExPar Size = new ExPar(SCREENSIZE, new ExParValue(400), "Grid Size");
	public ExPar NumberOfLines = new ExPar(SMALL_INT, new ExParValue(7),
			"Number of Grid Lines");
	public ExPar LineWidth = new ExPar(SCREENSIZE, new ExParValue(12),
			"Width of Lines");

	/** Cunstructor creating the title of the demo. */
	public HermannGrid() {
		setTitleAndTopic("Hermann Grid", LATERAL_INHIBITION_DSP | DEMO);
	}
	protected int grid;

	/** Initialize the display list of the demo. */
	protected int create() {
		grid = enterDisplayElement(new Grid(Color), group[0]);
		defaultTiming(0);
		return (grid);
	}

	protected void computeGeometry() {
		int s = Size.getInt();
		int lw = LineWidth.getInt();
		int n1 = NumberOfLines.getInt() - 1;
		int dw = (s - lw) / (n1);
		s = (n1) * dw + lw;
		Grid b = (Grid) getDisplayElement(grid);
		b.setRect(-s / 2, -s / 2, s, s);
		b.setPhase(dw, lw, dw, lw);
	}
}
