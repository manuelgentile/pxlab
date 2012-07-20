package de.pxlab.pxl;

/**
 * A series of horizontal bars.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 05/25/00
 */
public class HorStripedBar extends StripedBar {
	public HorStripedBar(ExPar i) {
		super(i);
	}

	/**
	 * Return the actual drawing height of this striped bar. The actual height
	 * is such that only full ON phases of the bar are drawn and that the bar's
	 * bottom line is in an ON phase.
	 */
	public int getDrawingSize() {
		return (((size.height - onPhase) / fullPhase) * fullPhase + onPhase);
	}

	/**
	 * Show this HorStripedBar. The display context has been set in the static
	 * area of class DisplayElement.
	 */
	public void show() {
		int n = (size.height - onPhase) / fullPhase + 1;
		// System.out.println("HorStripedBar.show(): fullPhase = " + fullPhase);
		// System.out.println("HorStripedBar.show(): x = " + location.x +
		// ", y = " + location.y);
		graphics.setColor(colorPar.getDevColor());
		// System.out.println("  showing HorStripedBar() with color " +
		// graphics.getColor());
		int y = location.y;
		for (int i = 0; i < n; i++) {
			graphics.fillRect(location.x, y, size.width, onPhase);
			y += fullPhase;
		}
		setBounds(location.x, location.y, size.width, (n - 1) * fullPhase
				+ onPhase);
	}
}
