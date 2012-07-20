package de.pxlab.pxl;

import java.awt.Dimension;

/**
 * A series of horizontal and vertical bars.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class Grid extends DisplayElement {
	protected int horFullPhase;
	protected int horOnPhase;
	protected int verFullPhase;
	protected int verOnPhase;

	public Grid(ExPar i) {
		type = DisplayElement.BAR_PATTERN;
		colorPar = i;
	}

	/**
	 * Set the phase parameters of this striped bar.
	 * 
	 * @param hor_p
	 *            number of total horizontal lines for a single period. Note
	 *            that (p+a) must be larger than this striped bar's height
	 *            parameter.
	 * @param hor_a
	 *            number of active horizontal lines in a single period. This
	 *            number must be less or equal to p.
	 * @param ver_p
	 *            number of total vertical lines for a single period. Note that
	 *            (p+a) must be larger than this striped bar's height parameter.
	 * @param ver_a
	 *            number of active vertical lines in a single period. This
	 *            number must be less or equal to p.
	 */
	public void setPhase(int hor_p, int hor_a, int ver_p, int ver_a) {
		horFullPhase = hor_p;
		horOnPhase = (hor_a <= hor_p) ? hor_a : hor_p;
		verFullPhase = ver_p;
		verOnPhase = (ver_a <= ver_p) ? ver_a : ver_p;
	}

	/**
	 * Return the actual drawing height of this striped bar. The actual height
	 * is such that only full ON phases of the bar are drawn and that the bar's
	 * bottom line is in an ON phase.
	 */
	public Dimension getDrawingSize() {
		return new Dimension(((size.width - horOnPhase) / horFullPhase)
				* horFullPhase + horOnPhase,
				((size.height - verOnPhase) / verFullPhase) * verFullPhase
						+ verOnPhase);
	}

	/**
	 * Show this Grid. The display context has been set in the static area of
	 * class DisplayElement.
	 */
	public void show() {
		int n = (size.width - horOnPhase) / horFullPhase + 1;
		graphics.setColor(colorPar.getDevColor());
		int x = location.x;
		for (int i = 0; i < n; i++) {
			graphics.fillRect(x, location.y, horOnPhase, size.height);
			x += horFullPhase;
		}
		n = (size.height - verOnPhase) / verFullPhase + 1;
		int y = location.y;
		for (int i = 0; i < n; i++) {
			graphics.fillRect(location.x, y, size.width, verOnPhase);
			y += verFullPhase;
		}
		setBounds(location.x, location.y, (n - 1) * horFullPhase + horOnPhase,
				(n - 1) * verFullPhase + verOnPhase);
	}
}
