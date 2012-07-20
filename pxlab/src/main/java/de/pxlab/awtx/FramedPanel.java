package de.pxlab.awtx;

import java.awt.*;

/**
 * This class has been copied from the book
 * 
 * <p>
 * Geary, D. M. (1999). Graphic Java 1.2, Mastering the JFC, Vol I: AWT (3rd
 * ed.). Sun Microsystems Press.
 */
public class FramedPanel extends Panel {
	String title;

	public FramedPanel(Component c, String title) {
		this.title = title;
		setLayout(new BorderLayout());
		add(c, "Center");
	}

	public Insets getInsets() {
		Graphics g = getGraphics();
		FontMetrics fm = g.getFontMetrics();
		g.dispose();
		return new Insets(fm.getHeight() + 2, 8, 8, 8);
	}

	public void paint(Graphics g) {
		Dimension sz = getSize();
		FontMetrics fm = g.getFontMetrics();
		int h = fm.getHeight();
		g.setColor(SystemColor.controlShadow);
		g.drawRect(0, h / 2, sz.width - 2, sz.height - 2 - h / 2);
		g.setColor(SystemColor.controlLtHighlight);
		g.drawRect(1, h / 2 + 1, sz.width - 2, sz.height - 2 - h / 2);
		g.setColor(getBackground());
		g.clearRect(sz.width / 2 - fm.stringWidth(title) / 2 - 2, 0,
				fm.stringWidth(title) + 4, fm.getHeight());
		g.setColor(getForeground());
		g.drawString(title, sz.width / 2 - fm.stringWidth(title) / 2,
				fm.getAscent());
	}
}
