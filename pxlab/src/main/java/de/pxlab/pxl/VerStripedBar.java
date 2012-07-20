package de.pxlab.pxl;

/**
 * A series of vertical bars.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public class VerStripedBar extends StripedBar {
	public VerStripedBar(ExPar i) {
		super(i);
	}

	/**
	 * Return the actual drawing height of this striped bar. The actual height
	 * is such that only full ON phases of the bar are drawn and that the bar's
	 * bottom line is in an ON phase.
	 */
	public int getDrawingSize() {
		return (((size.width - onPhase) / fullPhase) * fullPhase + onPhase);
	}

	/**
	 * Show this VerStripedBar. The display context has been set in the static
	 * area of class DisplayElement.
	 */
	public void show() {
		int n = (size.width - onPhase) / fullPhase + 1;
		graphics.setColor(colorPar.getDevColor());
		int x = location.x;
		for (int i = 0; i < n; i++) {
			graphics.fillRect(x, location.y, onPhase, size.height);
			x += fullPhase;
		}
		setBounds(location.x, location.y, (n - 1) * fullPhase + onPhase,
				size.height);
	}
}
