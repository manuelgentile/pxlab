package de.pxlab.pxl;

/**
 * A horizontal and vertical series of dots.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class DotGrid extends Grid {
	private boolean oval = true;

	public DotGrid(ExPar i) {
		super(i);
	}

	/**
	 * Define whether oval dots or rectangular dots are to be shown.
	 * 
	 * @param v
	 *            if true then oval dots are drawn if false then rectangular
	 *            dots are drawn.
	 */
	public void setOval(boolean v) {
		oval = v;
	}

	/**
	 * Show this Grid. The display context has been set in the static area of
	 * class DisplayElement.
	 */
	public void show() {
		graphics.setColor(colorPar.getDevColor());
		int hn = (size.width - horOnPhase) / horFullPhase + 1;
		int vn = (size.height - verOnPhase) / verFullPhase + 1;
		int x = location.x;
		for (int i = 0; i < hn; i++) {
			int y = location.y;
			for (int j = 0; j < vn; j++) {
				if (oval) {
					graphics.fillOval(x, y, horOnPhase, verOnPhase);
				} else {
					graphics.fillRect(x, y, horOnPhase, verOnPhase);
				}
				y += verFullPhase;
			}
			x += horFullPhase;
		}
		setBounds(location.x, location.y, (hn - 1) * horFullPhase + horOnPhase,
				(vn - 1) * verFullPhase + verOnPhase);
	}
}
