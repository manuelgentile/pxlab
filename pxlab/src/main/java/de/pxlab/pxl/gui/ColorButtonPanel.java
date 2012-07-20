package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import de.pxlab.pxl.*;

/**
 * A panel which contains a series of color buttons. Subclasses have to
 * implement the action listener.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2006/11/28
 */
abstract public class ColorButtonPanel extends Panel implements ActionListener {
	private Dimension prefButtonSize;
	private Font selectedFont = null;
	private Font normalFont = null;
	private ArrayList buttonList = new ArrayList();

	public ColorButtonPanel(int nc) {
		super(new GridLayout(0, nc, 5, 5));
		// FontMetrics fm = getFontMetrics(this.getFont());
		// prefButtonSize = new Dimension(1, fm.getHeight() + 8);
		prefButtonSize = new Dimension(1, 20);
	}

	public Dimension getPreferredButtonSize() {
		return prefButtonSize;
	}

	abstract public void actionPerformed(ActionEvent e);

	public int indexOfButton(Object b) {
		return buttonList.indexOf(b);
	}

	public ColorButton addButton(ExParDescriptor xpd, PxlColor c,
			String toolTip, boolean selected) {
		ColorButton b = addButton(c, toolTip, selected);
		b.addMouseListener(new ParamVisitDetector(xpd, null));
		return b;
	}

	public ColorButton addButton(PxlColor c, String toolTip, boolean selected) {
		if (buttonList.isEmpty())
			super.removeAll();
		ColorButton b = new ColorButton(c.dev(), toolTip, selected, this);
		buttonList.add(b);
		add(b);
		forceLayout();
		// p.repaint();
		return b;
		// System.out.println("ColorButtonPanel.addButton() Button " +
		// (buttonList.size()-1));
	}

	public void setButtonColor(int i, PxlColor c, boolean selected) {
		ColorButton b = (ColorButton) buttonList.get(i);
		if (b != null) {
			b.setColor(c.dev(), selected);
			repaint();
		}
	}

	public void removeAll() {
		super.removeAll();
		buttonList.clear();
		forceLayout();
		// repaint();
	}

	public void remove(int i) {
		if (i < buttonList.size()) {
			// System.out.println("ColorButtonPanel.remove(" + i + ")");
			super.remove(i);
			buttonList.remove(i);
			forceLayout();
			// repaint();
		}
	}
	class ColorButton extends Button {
		private int lightThreshold = 3 * 192;
		private double darkThreshold = 3 * 100;
		private boolean selected;

		public ColorButton(Color c, String toolTip, boolean slct,
				ActionListener act) {
			super(toolTip);
			if (act != null)
				addActionListener(act);
			setColor(c, slct);
		}

		public void setColor(Color c, boolean slct) {
			setBackground(c);
			int h = c.getRed() + c.getGreen() + c.getBlue();
			if (h > lightThreshold)
				setForeground(Color.black);
			if (h < darkThreshold)
				setForeground(Color.white);
			if (selectedFont == null) {
				normalFont = Base.getSystemFont();
				if (normalFont != null) {
					selectedFont = normalFont.deriveFont(Font.BOLD);
				}
			}
			if (selectedFont != null) {
				if (slct != selected) {
					setFont(slct ? selectedFont : normalFont);
					selected = slct;
				}
			}
		}

		public Dimension getPreferredSize() {
			return prefButtonSize;
		}

		public Dimension getMinimumSize() {
			return prefButtonSize;
		}
	}

	protected void forceLayout() {
		Container top = null;
		for (Container p = getParent(); p != null; p = p.getParent()) {
			top = p;
		}
		top.validate();
	}
	/*
	 * public void paint(Graphics g) {
	 * System.out.println("ColorButtonPanel.paint() Panel is " + (isValid()?
	 * "valid": "invalid")); super.paint(g); }
	 * 
	 * 
	 * public void invalidate() {
	 * System.out.println("ColorButtonPanel.invalidate()"); super.invalidate();
	 * }
	 */
}
