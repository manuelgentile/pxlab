package de.pxlab.pxl;

/**
 * A series of horizontal or vertical bars.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 05/25/00
 */
abstract public class StripedBar extends DisplayElement {
	protected int fullPhase;
	protected int onPhase;

	public StripedBar(ExPar i) {
		type = DisplayElement.BAR_PATTERN;
		colorPar = i;
	}

	/**
	 * Set the phase parameters of this striped bar.
	 * 
	 * @param p
	 *            number of total lines for a single period. Note that (p+a)
	 *            must be larger than this striped bar's height parameter.
	 * @param a
	 *            number of active lines in a single period. This number must be
	 *            less or equal to p.
	 */
	public void setPhase(int p, int a) {
		fullPhase = p;
		onPhase = (a <= p) ? a : p;
	}

	/**
	 * Return the actual drawing width/height of this bar pattern. The actual
	 * height is such that only full ON phases of the bar are drawn and that the
	 * bar's border lines are in an ON phase.
	 */
	abstract public int getDrawingSize();
}
