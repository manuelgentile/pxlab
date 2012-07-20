package de.pxlab.pxl;

/**
 * This class represents arc like display objects.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 03/22/00
 */
abstract public class DisplayElementArc extends DisplayElement {
	/** Starting angle of the arc. */
	protected int startAngle;
	/** Size of the angle covered by the arc. */
	protected int arcAngle;

	public void setAngles(int sa, int aa) {
		startAngle = sa;
		arcAngle = aa;
	}
}
