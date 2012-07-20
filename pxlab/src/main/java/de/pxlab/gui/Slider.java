package de.pxlab.gui;

import java.awt.*;
import java.awt.event.*;

/**
 * Creates a horizontal or vertical slider which shows a view onto one of the
 * AxisModel objects.
 * 
 * @author H. Irtel
 * @version 0.3.2
 * @see AxisModel
 * @see AxisListener
 */
/*
 * 02/06/01 added setEnabled()
 * 
 * 01/15/02 fixed paint() with a cludge since sometimes paint() seems to be
 * called before the view has been calculated.
 */
public class Slider extends Panel implements MouseListener,
		MouseMotionListener, ComponentListener, FocusListener {
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	// Mathematical Slider Model
	protected AxisModel model;
	private boolean enabled = true;
	// Preferred spacing
	private int longBorder;
	private int leftSmallBorder;
	private int preferredGridSize;
	private int rightSmallBorder;
	// Pointer
	protected int pointerHalfWidth;
	protected int pointerHeight;
	protected int touchSize;
	protected Rectangle touchRect;
	protected Rectangle paintRect;
	protected Rectangle pointerRect = new Rectangle();
	protected int[] pointerX = new int[5];
	protected int[] pointerY = new int[5];
	protected int pointerPosition;
	protected Color pointerColor = SystemColor.control;
	// Track
	protected Color trackColor = null;
	protected Rectangle trackRect;
	protected int axisMin, axisRange;
	// Ticks
	protected boolean showTicks = true;
	protected boolean showSmallTicks;
	protected int numberOfLargeTicks;
	protected int numberOfSmallTicks;
	protected double[] smallTicksAt;
	protected double[] largeTick;
	protected double[] smallTick;
	protected int tickBase;
	protected int[] largeTickPos;
	protected int[] smallTickPos;
	protected int largeTickSize;
	protected int smallTickSize;
	protected int scaleSize;
	// Labels
	protected boolean showLabels = true;
	protected String[] label;
	protected int labelSize;
	protected int labelBaseX;
	protected int labelBaseY;
	protected int labelBaseOffsetY;
	protected int labelPrecision = 1;
	// Title
	protected String title = null;
	protected int titleSize;
	protected int titleBaseX;
	protected int titleBaseY;
	// Slider Orientation
	protected int orientation;
	protected boolean hor;
	// Window
	protected int preferredSize;
	protected int width, height;

	/**
	 * Create a horizontal slider with linear model and a range from 0 to 100
	 * with 5 visible intervals and numeric labels.
	 */
	public Slider() {
		this(HORIZONTAL, new LinearAxisModel(0.0, 100.0, 0.0), 6, 0);
	}

	/** Create a slider with the given orientation and model. */
	public Slider(int or, AxisModel m, int nl, int ns) {
		this(null, or, m, nl, ns);
	}
	static int tt = 0;

	/** Create a slider with the given title, orientation and model. */
	public Slider(String t, int or, AxisModel m, int nl, int ns) {
		// if (t == null) t = "Nr. " + tt; tt++;
		title = t;
		model = m;
		// System.out.println("Creating Slider " + t + " Orient: " + or + " nl="
		// + nl + " ns=" + ns);
		computeMappingProperties(or, nl, ns);
		// Suggest initial sizes
		// int cw = getFontMetrics(getFont()).charWidth('0');
		int cw = 16;
		setPreferredSpacing(2 * cw, 260, 2 * cw);
		setBackground(SystemColor.control);
		addMouseListener(this);
		addMouseMotionListener(this);
		addComponentListener(this);
		addFocusListener(this);
	}

	public void setEnabled(boolean e) {
		enabled = e;
	}

	public boolean isEnabled() {
		return (enabled);
	}

	public void setLabelPrecision(int n) {
		labelPrecision = n;
		computeMappingProperties(orientation, numberOfLargeTicks,
				numberOfSmallTicks);
	}

	public void setShowLabels(boolean x) {
		showLabels = x;
		computeMappingProperties(orientation, numberOfLargeTicks,
				numberOfSmallTicks);
	}

	public boolean getShowLabels() {
		return (showLabels);
	}

	public void setShowTicks(boolean x) {
		showTicks = x;
		if (!x)
			showSmallTicks = false;
	}

	public boolean getShowTicks() {
		return (showTicks);
	}

	public void setTrackColor(Color c) {
		trackColor = c;
	}

	public Color getTrackColor() {
		return (trackColor);
	}

	public void setOrientation(int n) {
		orientation = n;
	}

	public int getOrientation() {
		return (orientation);
	}

	public AxisModel getModel() {
		return (model);
	}

	public void setTitle(String t) {
		title = t;
	}

	public String getTitle() {
		return (title);
	}

	public void setNumberOfLargeTicks(int n) {
		computeMappingProperties(orientation, n, numberOfSmallTicks);
	}

	public void setNumberOfSmallTicks(int n) {
		smallTicksAt = null;
		computeMappingProperties(orientation, numberOfLargeTicks, n);
	}

	public int getNumberOfLargeTicks() {
		return (numberOfLargeTicks);
	}

	public void setSmallTicksAt(double[] a) {
		smallTicksAt = a;
		computeMappingProperties(orientation, numberOfLargeTicks, 0);
	}

	/** Set the lower bound of the range of values. */
	public void setMinimum(double x) {
		model.setMinimum(x);
		computeMappingProperties(orientation, numberOfLargeTicks,
				numberOfSmallTicks);
	}

	/** Return the lower bound of the slider model. */
	public double getMinimum() {
		return (model.getMinimum());
	}

	/** Set the upper bound of the slider model. */
	public void setMaximum(double x) {
		model.setMaximum(x);
		computeMappingProperties(orientation, numberOfLargeTicks,
				numberOfSmallTicks);
	}

	/** Return the upper bound of the slider model. */
	public double getMaximum() {
		return (model.getMaximum());
	}

	/** Return the current value of the slider model. */
	public double getValue() {
		return (model.getValue());
	}

	/**
	 * Set the current value of the slider model and repaint the slider's track.
	 */
	public void setValue(double v) {
		model.setValue(v);
		// System.out.println("setValue() paintRect: " + paintRect);
		if (paintRect != null) {
			repaint(paintRect.x, paintRect.y, paintRect.width, paintRect.height);
		} else {
			repaint();
		}
	}

	/** Add a listener to this slider's model */
	public void setAxisListener(AxisListener x) {
		model.setAxisListener(x);
	}

	/** Remove a listener from this slider's model */
	public void removeAxisListener() {
		model.removeAxisListener();
	}

	/** Set the preferred horizontal spacing of the chart. */
	public void setPreferredSpacing(int leftSmallBorder, int gridWidth,
			int rightSmallBorder) {
		this.leftSmallBorder = leftSmallBorder;
		this.preferredGridSize = gridWidth;
		this.rightSmallBorder = rightSmallBorder;
	}

	public Dimension getPreferredSize() {
		// System.out.println("Font: " + getFont());
		FontMetrics fm = getFontMetrics(getFont());
		Dimension ret;
		longBorder = fm.getHeight() / 6;
		touchSize = fm.getHeight();
		scaleSize = showTicks ? fm.getHeight() / 2 : 0;
		pointerHalfWidth = ((3 * fm.charWidth('0')) / 2);
		pointerHeight = touchSize;
		if (showTicks) {
			largeTickSize = scaleSize;
			smallTickSize = 3 * scaleSize / 4;
		}
		if (hor) {
			titleSize = (title == null) ? 0 : fm.getHeight();
			labelSize = showLabels ? fm.getHeight() : 0;
			preferredSize = longBorder + titleSize + touchSize + scaleSize
					+ labelSize;
			ret = new Dimension(leftSmallBorder + preferredGridSize
					+ rightSmallBorder, preferredSize);
		} else {
			titleSize = (title == null) ? 0 : ((3 * fm.getHeight()) / 2);
			labelSize = showLabels ? (7 * fm.charWidth('0')) : 0;
			preferredSize = longBorder + touchSize + scaleSize + labelSize;
			ret = new Dimension(preferredSize, leftSmallBorder + titleSize
					+ preferredGridSize + rightSmallBorder);
		}
		return (ret);
	}

	public Dimension getMinimumSize() {
		return (getPreferredSize());
	}

	/**
	 * Initialize the Slider's model relation. Creates an equally spaced
	 * sequence of steps on the scale in the range between min and max,
	 * including the limits. This method computes all those properties of the
	 * slider which are related to the slider's model but are independent of the
	 * slider's view.
	 */
	protected void computeMappingProperties(int or, int nl, int ns) {
		// System.out.println("Computing mapping of " + title + " Orient: " + or
		// + " nl=" + nl + " ns=" + ns);
		if (or == HORIZONTAL) {
			orientation = or;
			hor = true;
		} else if (or == VERTICAL) {
			orientation = or;
			hor = false;
		} else {
			throw new IllegalArgumentException(
					"orientation must be one of: VERTICAL, HORIZONTAL");
		}
		numberOfLargeTicks = nl;
		if (numberOfLargeTicks < 0)
			setShowTicks(false);
		if (numberOfLargeTicks < 2)
			numberOfLargeTicks = 2;
		numberOfSmallTicks = ns;
		showSmallTicks = ((numberOfSmallTicks > 0) || ((smallTicksAt != null) && (smallTicksAt.length > 0)));
		if (showTicks || showLabels) {
			if (showLabels)
				label = new String[numberOfLargeTicks];
			double min = model.getMinimum();
			double x;
			double largeStep = model.getStepSizeValue(numberOfLargeTicks - 1);
			largeTick = new double[numberOfLargeTicks];
			for (int i = 0; i < numberOfLargeTicks; i++) {
				x = model.getValueForSteps(i, largeStep);
				// System.out.print("large Tick Value " + x);
				largeTick[i] = model.getMappedValue(x);
				// System.out.println(" maps to " + largeTick[i]);
				if (showLabels) {
					label[i] = numericLabel(x, labelPrecision);
				}
			}
			if (showTicks && showSmallTicks) {
				if (smallTicksAt != null) {
					numberOfSmallTicks = smallTicksAt.length;
					smallTick = new double[numberOfSmallTicks];
					for (int i = 0; i < numberOfSmallTicks; i++) {
						x = model.getValueForSteps(1, smallTicksAt[i]
								* largeStep);
						// System.out.print("small Tick Value " + x);
						smallTick[i] = model.getMappedValue(x);
						// System.out.println(" maps to " + smallTick[i]);
					}
				} else {
					double smallStep = (model.getValueForSteps(1, largeStep) - min)
							/ (numberOfSmallTicks + 1);
					smallTick = new double[numberOfSmallTicks];
					x = min + smallStep;
					for (int i = 0; i < numberOfSmallTicks; i++) {
						// System.out.print("small Tick Value " + x);
						smallTick[i] = model.getMappedValue(x);
						// System.out.println(" maps to " + smallTick[i]);
						x += smallStep;
					}
				}
			}
		}
	}

	/**
	 * This method computes the slider's view. This covers all properties which
	 * relate to the user interface of the slider. It maps the slider model
	 * interface computed by computeMappingProperties() to the screen window
	 * covered by the slider.
	 */
	protected void computeView() {
		// System.out.println("Computing view for " + title + " hor=" + hor);
		FontMetrics fm = getFontMetrics(getFont());
		getPreferredSize();
		if (hor) {
			computeHorizontalView(getSize().width, getSize().height,
					fm.getLeading() + fm.getAscent(), fm.charWidth('0'));
		} else {
			computeVerticalView(getSize().width, getSize().height,
					fm.getAscent(), fm.charWidth('0'));
		}
		if (trackColor == null)
			trackColor = SystemColor.control;
		// System.out.println("axisMin="+axisMin+", axisRange="+axisRange+", preferredSize="+preferredSize);
	}

	protected void computeHorizontalView(int width, int height, int fmLA,
			int fmCW) {
		// System.out.println("Horizontal track!");
		// Top left position of rectangles
		int leftX = leftSmallBorder;
		int topY = longBorder;
		if (height > preferredSize)
			topY = (height - preferredSize) / 2;
		// The effective width of the slider's track
		int wd = width - leftSmallBorder - rightSmallBorder;
		titleBaseX = leftX;
		titleBaseY = topY + (8 * titleSize) / 10;
		// The rectangle which is sensitive to mouse clicks for slider
		// movement.
		touchRect = new Rectangle(leftX, topY + titleSize, wd, touchSize);
		// The track which holds the slider's knob
		trackRect = new Rectangle(leftX, touchRect.y + touchSize / 3, wd,
				touchSize / 3);
		// The axis minimum and range
		axisMin = leftX;
		axisRange = wd - 1;
		// Label offsets
		if (showLabels) {
			labelBaseY = topY + titleSize + touchSize + scaleSize + fmLA;
		}
		// Tick positions
		if (showTicks || showLabels) {
			tickBase = touchRect.y + touchSize + scaleSize - 1;
			largeTickPos = new int[numberOfLargeTicks];
			for (int i = 0; i < numberOfLargeTicks; i++) {
				largeTickPos[i] = (int) Math.round(axisMin + largeTick[i]
						* axisRange);
			}
			// Positions of small ticks relative to large ticks
			if (showTicks && showSmallTicks) {
				smallTickPos = new int[numberOfSmallTicks];
				// System.out.print("Small Ticks at ");
				for (int i = 0; i < numberOfSmallTicks; i++) {
					smallTickPos[i] = (int) Math
							.round(smallTick[i] * axisRange);
					// System.out.print(" " + smallTickPos[i]);
				}
				// System.out.println("");
			}
		}
		// Compute pointer polygon
		int b = touchRect.y + touchRect.height - 1;
		pointerX[0] = pointerHalfWidth;
		pointerX[1] = pointerHalfWidth;
		pointerX[2] = 0;
		pointerX[3] = -pointerHalfWidth;
		pointerX[4] = -pointerHalfWidth;
		pointerY[0] = b - touchSize + 1;
		pointerY[1] = b - pointerHalfWidth;
		pointerY[2] = b;
		pointerY[3] = b - pointerHalfWidth;
		pointerY[4] = pointerRect.y = b - touchSize + 1;
		// Pointer bounding rectangle
		pointerRect.y = touchRect.y;
		pointerRect.height = touchSize;
		pointerRect.width = 2 * pointerHalfWidth + 1;
		paintRect = new Rectangle(touchRect.x - pointerHalfWidth, touchRect.y,
				touchRect.width + pointerRect.width, touchRect.height);
		// System.out.println("paintRect: " + paintRect);
	}

	protected void computeVerticalView(int width, int height, int fmLA, int fmCW) {
		// System.out.println("Vertical track!");
		// Top left position of rectangles
		int topY = leftSmallBorder;
		int leftX = longBorder;
		if (width > preferredSize)
			leftX = (width - preferredSize) / 2;
		// The effective height of the slider's track
		int ht = height - titleSize - leftSmallBorder - rightSmallBorder;
		// The rectangle which is sensitive to mouse clicks for slider
		// movement.
		touchRect = new Rectangle(leftX + labelSize + scaleSize, topY
				+ titleSize, touchSize, ht);
		// The track which holds the slider's knob
		trackRect = new Rectangle(touchRect.x + touchSize / 3, touchRect.y,
				touchSize / 3, ht);
		// The axis minimum and range
		axisMin = touchRect.y + ht - 1;
		axisRange = -(ht - 1);
		// Label offsets
		if (showLabels) {
			labelBaseX = leftX + labelSize - fmCW;
			labelBaseOffsetY = fmLA / 2;
		}
		titleBaseX = leftX;
		titleBaseY = topY + fmLA;
		if (showTicks || showLabels) {
			tickBase = leftX + labelSize;
			largeTickPos = new int[numberOfLargeTicks];
			for (int i = 0; i < numberOfLargeTicks; i++) {
				largeTickPos[i] = (int) Math.round(axisMin + largeTick[i]
						* axisRange);
			}
			if (showTicks && showSmallTicks) {
				smallTickPos = new int[numberOfSmallTicks];
				// System.out.print("Small Ticks at ");
				for (int i = 0; i < numberOfSmallTicks; i++) {
					smallTickPos[i] = (int) Math
							.round(smallTick[i] * axisRange);
					// System.out.print(" " + smallTickPos[i]);
				}
				// System.out.println("");
			}
		}
		int b = touchRect.x;
		pointerX[0] = b + touchSize - 1;
		pointerX[1] = b + pointerHalfWidth;
		pointerX[2] = b;
		pointerX[3] = b + pointerHalfWidth;
		pointerX[4] = b + touchSize - 1;
		pointerY[0] = -pointerHalfWidth;
		pointerY[1] = -pointerHalfWidth;
		pointerY[2] = 0;
		pointerY[3] = pointerHalfWidth;
		pointerY[4] = pointerHalfWidth;
		pointerRect.x = touchRect.x;
		pointerRect.height = 2 * pointerHalfWidth + 1;
		pointerRect.width = touchSize;
		paintRect = new Rectangle(touchRect.x, touchRect.y - pointerHalfWidth,
				touchRect.width, touchRect.height + pointerRect.height);
		// System.out.println("paintRect: " + paintRect);
	}
	private BackBuffer backBuffer;

	public void update(Graphics g) {
		if (backBuffer == null) {
			backBuffer = new BackBuffer(this);
		}
		Graphics b = backBuffer.getGraphics();
		super.update(b);
		paint(b);
		backBuffer.blit(g);
	}

	/** This paints the slider's layout. */
	public void paint(Graphics g) {
		g.setColor(trackColor);
		// if (trackRect == null) System.out.println("no trackRect!");
		// This is a cludge since sometimes paint() seems to be called
		// before the view has been calculated.
		if (trackRect == null)
			computeView();
		g.fill3DRect(trackRect.x, trackRect.y, trackRect.width,
				trackRect.height, false);
		g.setColor(SystemColor.controlText);
		if (title != null) {
			g.drawString(title, titleBaseX, titleBaseY);
		}
		if (hor)
			paintHorizontalView(g);
		else
			paintVerticalView(g);
	}

	/**
	 * This paints a horizontal view of the slider. The following external
	 * geometric data are used to paint the view: trackRect, largeTickPos[],
	 * smallTickPos[], axisMin/Y, largeTickSize, smallTickSize, labelBase,
	 * pointerX/Y[]
	 */
	protected void paintHorizontalView(Graphics g) {
		FontMetrics fm = g.getFontMetrics();
		if (showTicks || showLabels) {
			for (int i = 0; i < numberOfLargeTicks; i++) {
				if (showTicks)
					g.drawLine(largeTickPos[i], tickBase, largeTickPos[i],
							tickBase - largeTickSize + 1);
				if (showLabels)
					g.drawString(label[i],
							largeTickPos[i] - fm.stringWidth(label[i]) / 2,
							labelBaseY);
				if (showSmallTicks && (i < (numberOfLargeTicks - 1))) {
					for (int j = 0; j < numberOfSmallTicks; j++) {
						g.drawLine(largeTickPos[i] + smallTickPos[j], tickBase,
								largeTickPos[i] + smallTickPos[j], tickBase
										- smallTickSize + 1);
						// System.out.println("Small Tick at " +
						// model.getValueFromMap((double)(largeTickPos[i]+smallTickPos[j]-axisMin)/(double)axisRange));
					}
				}
			}
			if (showTicks)
				g.drawLine(largeTickPos[0], tickBase,
						largeTickPos[numberOfLargeTicks - 1], tickBase);
		}
		pointerPosition = (int) Math.round(axisMin
				+ model.getMappedValue(model.getValue()) * axisRange);
		int[] pX = new int[5];
		for (int i = 0; i < 5; i++) {
			pX[i] = pointerX[i] + pointerPosition;
		}
		pointerRect.x = pX[4];
		g.setColor(SystemColor.control);
		g.fillPolygon(pX, pointerY, 5);
		g.setColor(SystemColor.controlLtHighlight);
		g.drawLine(pX[4] + 1, pointerY[4] + 1, pX[0] - 2, pointerY[0] + 1);
		g.drawLine(pX[3] + 1, pointerY[3], pX[4] + 1, pointerY[4] + 1);
		g.setColor(SystemColor.controlHighlight);
		g.drawLine(pX[3] + 5, pointerY[0] + 4, pX[1] - 5, pointerY[0] + 4);
		g.drawLine(pX[3] + 5, pointerY[0] + 7, pX[1] - 5, pointerY[0] + 7);
		g.setColor(SystemColor.controlShadow);
		g.drawLine(pX[3] + 5, pointerY[0] + 5, pX[1] - 5, pointerY[0] + 5);
		g.drawLine(pX[3] + 5, pointerY[0] + 8, pX[1] - 5, pointerY[0] + 8);
		g.drawLine(pX[0] - 1, pointerY[0] + 1, pX[1] - 1, pointerY[1]);
		g.drawLine(pX[2], pointerY[2] - 1, pX[1] - 1, pointerY[1]);
		g.setColor(SystemColor.controlText);
		g.drawPolygon(pX, pointerY, 5);
		// System.out.println(model.getValue());
	}

	protected void paintVerticalView(Graphics g) {
		FontMetrics fm = g.getFontMetrics();
		if (showTicks || showLabels) {
			for (int i = 0; i < numberOfLargeTicks; i++) {
				if (showTicks)
					g.drawLine(tickBase, largeTickPos[i], tickBase
							+ largeTickSize - 1, largeTickPos[i]);
				if (showLabels)
					g.drawString(label[i],
							labelBaseX - fm.stringWidth(label[i]),
							largeTickPos[i] + labelBaseOffsetY);
				if (showSmallTicks && (i < (numberOfLargeTicks - 1))) {
					for (int j = 0; j < numberOfSmallTicks; j++) {
						g.drawLine(tickBase, largeTickPos[i] + smallTickPos[j],
								tickBase + smallTickSize - 1, largeTickPos[i]
										+ smallTickPos[j]);
						// System.out.println("Small Tick at " +
						// model.getValueFromMap((double)(largeTickPos[i]+smallTickPos[j]-axisMin)/(double)axisRange));
					}
				}
			}
			if (showTicks)
				g.drawLine(tickBase, largeTickPos[0], tickBase,
						largeTickPos[numberOfLargeTicks - 1]);
		}
		pointerPosition = (int) Math.round(axisMin
				+ model.getMappedValue(model.getValue()) * axisRange);
		int[] pY = new int[5];
		for (int i = 0; i < 5; i++) {
			pY[i] = pointerY[i] + pointerPosition;
		}
		pointerRect.y = pY[0];
		g.setColor(pointerColor);
		g.fillPolygon(pointerX, pY, 5);
		g.setColor(SystemColor.controlLtHighlight);
		g.drawLine(pointerX[0] - 1, pY[0] + 1, pointerX[1], pY[1] + 1);
		g.drawLine(pointerX[1], pY[1] + 1, pointerX[2] + 1, pY[2]);
		g.setColor(SystemColor.controlHighlight);
		g.drawLine(pointerX[0] - 5, pY[0] + 5, pointerX[0] - 5, pY[4] - 5);
		g.drawLine(pointerX[0] - 8, pY[0] + 5, pointerX[0] - 8, pY[4] - 5);
		g.setColor(SystemColor.controlShadow);
		g.drawLine(pointerX[0] - 4, pY[0] + 5, pointerX[0] - 4, pY[4] - 5);
		g.drawLine(pointerX[0] - 7, pY[0] + 5, pointerX[0] - 7, pY[4] - 5);
		g.drawLine(pointerX[0] - 1, pY[0] + 2, pointerX[4] - 1, pY[4] - 1);
		g.drawLine(pointerX[3], pY[3] - 1, pointerX[4] - 1, pY[4] - 1);
		g.setColor(SystemColor.controlText);
		g.drawPolygon(pointerX, pY, 5);
		// System.out.println(model.getValue());
	}

	public String toString() {
		String nl = System.getProperty("line.separator");
		StringBuffer s = new StringBuffer("Slider \"" + "\" from "
				+ numericLabel(model.getMinimum(), 5) + " to "
				+ numericLabel(model.getMaximum(), 5) + " with orientation "
				+ orientation + nl + "  " + numberOfLargeTicks
				+ " Labels are: ");
		for (int i = 0; i < largeTick.length; i++) {
			s.append(" " + label[i] + " (" + largeTick[i] + ")");
		}
		s.append(nl);
		return (new String(s));
	}

	/**
	 * Create a string representation of x with at most n decimal digits.
	 */
	private String numericLabel(double x, int n) {
		String s = null;
		if (n == 0) {
			s = String.valueOf(Math.round(x));
		} else {
			int precisionFactor = 1;
			double xp;
			for (int i = 0; i < n; i++)
				precisionFactor *= 10;
			xp = (double) Math.round(precisionFactor * x)
					/ (double) precisionFactor;
			s = String.valueOf(xp);
			// System.out.println(x + " with pf= " + precisionFactor +
			// " (precision="+n+") is " + s + " (xp="+xp+")");
		}
		return (s);
	}
	// Mouse/Motion listener implementation
	protected int pointerDelta;
	private boolean inSlider = false;

	public void mouseDragged(MouseEvent e) {
		if (!enabled)
			return;
		// System.out.println("dragging ...");
		trackMouse(e);
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (!enabled)
			return;
		// System.out.println("---> pressed");
		int x = e.getX();
		int y = e.getY();
		if (pointerRect.contains(x, y)) {
			pointerDelta = pointerPosition - (hor ? x : y);
			inSlider = true;
		} else if (touchRect.contains(x, y)) {
			pointerDelta = 0;
			inSlider = true;
			trackMouse(e);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (!enabled)
			return;
		// System.out.println("---> released");
		pointerDelta = 0;
		inSlider = false;
		// trackMouse(e);
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	private void trackMouse(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (inSlider /* pointerRect.contains(x, y) || touchRect.contains(x, y) */) {
			double p = (double) ((hor ? x : y) + pointerDelta - axisMin)
					/ (double) (axisRange);
			if (p < 0.0)
				p = 0.0;
			if (p > 1.0)
				p = 1.0;
			if (model.setValueFromMap(p)) {
				// System.out.println("Slider set to " + model.getValue());
				repaint(paintRect.x, paintRect.y, paintRect.width,
						paintRect.height);
			}
		}
	}

	/**
	 * The ComponentListener is implemented for detecting size changes which
	 * require a recomputation of the chart's geometry.
	 */
	public void componentResized(ComponentEvent e) {
		// System.out.println("Size has been changed!");
		computeView();
		repaint();
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
		// System.out.println("Slider is shown!");
		// computeView();
	}

	public void focusGained(FocusEvent e) {
		pointerColor = SystemColor.controlHighlight;
		repaint();
	}

	public void focusLost(FocusEvent e) {
		pointerColor = SystemColor.control;
		repaint();
	}
}
