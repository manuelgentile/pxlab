package de.pxlab.gui;

import java.awt.*;
import java.awt.event.*;

/**
 * This class paints a 2D chart with independent axis models for each axis.
 * 
 * @author H. Irtel
 * @version 0.2.3
 */
/*
 * 
 * 01/01/01 Fixed valid region display bug for cases when Y > (R+G+B)
 * 
 * 02/06/01 added setEnabled()
 * 
 * 09/18/02 Sometimes the paint() message seems to come pefore things are set up
 * correctly, I don't know why! but worked around that
 * 
 * 03/19/03 allow for empty chartListener and alos allow changing the axis model
 */
public class Chart extends Panel implements MouseListener, MouseMotionListener,
		ComponentListener {
	private String title = null;
	// These are the axis models
	private AxisModel xModel;
	private AxisModel yModel;
	// Every chart must have a chart listener because this listener
	// object is asked whether adjustments are to be acceptable or
	// not.
	private ChartListener chartListener;
	private boolean enabled = true;
	// Localized constructor arguments
	// private XYAdjustmentPanel parentAdjustmentPanel;
	private double yUnitToXUnitRatio;
	private int numberOfLargeXTicks, numberOfLargeYTicks;
	// The valid region is a subregion of the chart which may be used
	// by the chart creator to indicate that cursor movement sould be
	// restricted.
	private double[] validRegionVertex;
	private Polygon validRegion;
	private boolean showValidRegion = false;
	private boolean validRegionDirty = true;
	// Preferred spacing parameters
	private int leftBorder;
	private int preferredGridWidth;
	private int rightBorder;
	private int topBorder;
	private int preferredGridHeight;
	private int bottomBorder;
	// Permanent variables
	// private int width, height;
	private Rectangle grid = new Rectangle();
	private int gridXRange, gridYRange;
	private int gridRightX, gridBottomY;
	private int markHalfWidth, markHalfHeight;
	private int titleXPos, titleYPos;
	private int xLabelBaseY, yLabelBaseXOffset, yLabelBaseYOffset;
	private double[] largeXTick, largeYTick;
	private int[] largeXTickPos, largeYTickPos;
	private String[] xLabel, yLabel;
	private int firstXLabelAtTick = 0, firstYLabelAtTick = 0;
	private int xLabelAtEveryTick = 1, yLabelAtEveryTick = 1;
	private int[] xLabelBaseXOffset, yLabelBaseX;
	private int xLabelPrecision = 4;
	private int yLabelPrecision = 4;
	private boolean showsCrosshair;
	private double currentXMapValue, currentYMapValue;
	private boolean markCurrentPosition = false;
	private boolean showPosition = true;
	private ChartPaintExtension chartPaintExtension = null;
	private ChartMouseExtension chartMouseExtension = null;

	/**
	 * Create a 2D-chart with the given parameters.
	 * 
	 * @param xm
	 *            AxisModel for the x-axis
	 * @param nxt
	 *            number of large x-axis ticks
	 * @param ym
	 *            AxisModel for the y-axis
	 * @param nyt
	 *            number of large y-axis ticks
	 * @param y2xr
	 *            ratio of y-axis steps to x-axis steps on the screen
	 */
	public Chart(AxisModel xm, int nxt, AxisModel ym, int nyt, double y2xr) {
		this(xm, nxt, ym, nyt, y2xr, null);
	}

	/**
	 * Create a 2D-chart with the given parameters.
	 * 
	 * @param xm
	 *            AxisModel for the x-axis
	 * @param nxt
	 *            number of large x-axis ticks
	 * @param ym
	 *            AxisModel for the y-axis
	 * @param nyt
	 *            number of large y-axis ticks
	 * @param y2xr
	 *            ratio of y-axis steps to x-axis steps on the screen
	 * @param cl
	 *            ChartListener for this chart
	 */
	public Chart(AxisModel xm, int nxt, AxisModel ym, int nyt, double y2xr,
			ChartListener cl) {
		xModel = xm;
		yModel = ym;
		numberOfLargeXTicks = nxt;
		numberOfLargeYTicks = nyt;
		yUnitToXUnitRatio = y2xr;
		chartListener = cl;
		// Compute those properties of the chart which only depend on
		// the axis models.
		computeMappingProperties();
		// Suggest initial sizes
		Font defaultFont = new Font("SansSerif", Font.PLAIN, 14);
		int cw = (50 * defaultFont.getSize()) / 100;
		setPreferredHorizontalSpacing(7 * cw, 260, 2 * cw);
		setPreferredVerticalSpacing(2 * cw, 260, 3 * cw);
		setMarkSize(2 * cw);
		setBackground(SystemColor.control);
		// Add our listeners
		addMouseListener(this);
		addMouseMotionListener(this);
		addComponentListener(this);
	}

	public void setEnabled(boolean e) {
		enabled = e;
	}

	public boolean isEnabled() {
		return (enabled);
	}

	public void setXAxisModel(AxisModel m) {
		xModel = m;
		computeMappingProperties();
	}

	public void setYAxisModel(AxisModel m) {
		yModel = m;
		computeMappingProperties();
	}

	public void setNumberOfLargeXTicks(int nxt) {
		numberOfLargeXTicks = nxt;
		computeMappingProperties();
		computeView();
	}

	public void setNumberOfLargeYTicks(int nyt) {
		numberOfLargeYTicks = nyt;
		computeMappingProperties();
		computeView();
	}

	public void setYUnitToXUnitRatio(double y2xr) {
		yUnitToXUnitRatio = y2xr;
		computeMappingProperties();
		computeView();
	}

	/** Return the current chart position's x coordinate. */
	public double getXValue() {
		return (xModel.getValue());
	}

	/** Return the current chart position's y coordinate. */
	public double getYValue() {
		return (yModel.getValue());
	}

	/**
	 * Set the chart position to (x,y). Note that this method does not result in
	 * a message to the ChartListener.
	 */
	public void setValue(double x, double y) {
		xModel.setValue(x);
		yModel.setValue(y);
		currentXMapValue = xModel.getMappedValue(x);
		currentYMapValue = yModel.getMappedValue(y);
		markCurrentPosition = true;
		repaint(grid.x, grid.y, grid.width, grid.height);
	}

	/** Set the preferred horizontal spacing of the chart. */
	public void setPreferredHorizontalSpacing(int leftBorder, int gridWidth,
			int rightBorder) {
		this.leftBorder = leftBorder;
		this.preferredGridWidth = gridWidth;
		this.rightBorder = rightBorder;
	}

	/** Set the preferred vertical spacing of the chart. */
	public void setPreferredVerticalSpacing(int topBorder, int gridHeight,
			int bottomBorder) {
		this.topBorder = topBorder;
		this.preferredGridHeight = gridHeight;
		this.bottomBorder = bottomBorder;
	}

	/** Return the preferred size of the chart. */
	public Dimension getPreferredSize() {
		return (new Dimension(leftBorder + preferredGridWidth + rightBorder,
				topBorder + preferredGridHeight + bottomBorder));
	}

	/** Return the minimum size of the chart. */
	public Dimension getMinimumSize() {
		return (getPreferredSize());
	}

	/**
	 * Set the number of decimal digits to be printed in the labels.
	 */
	public void setLabelPrecision(int x) {
		xLabelPrecision = x;
		yLabelPrecision = x;
		computeMappingProperties();
	}

	/** Set the title line of this chart. */
	public void setTitle(String t) {
		title = t;
		computeMappingProperties();
	}

	/**
	 * Set the number of decimal digits to be printed in the labels.
	 */
	public void setXLabelPrecision(int x) {
		xLabelPrecision = x;
		computeMappingProperties();
	}

	/**
	 * Set the number of decimal digits to be printed in the labels.
	 */
	public void setYLabelPrecision(int x) {
		yLabelPrecision = x;
		computeMappingProperties();
	}

	/** Set the index of the x ticks where the first label appears. */
	public void setFirstXLabelAtTick(int n) {
		firstXLabelAtTick = n;
	}

	/** Set the index of the y ticks where the first label appears. */
	public void setFirstYLabelAtTick(int n) {
		firstYLabelAtTick = n;
	}

	/** Set the number of index steps between successive labels. */
	public void setXLabelAtEveryTick(int n) {
		xLabelAtEveryTick = n;
	}

	/** Set the number of index steps between successive labels. */
	public void setYLabelAtEveryTick(int n) {
		yLabelAtEveryTick = n;
	}

	/**
	 * Set the size of the marker which marks the current position.
	 */
	public void setMarkSize(int s) {
		markHalfWidth = s / 2;
		markHalfHeight = s / 2;
	}

	/**
	 * Compute those properties of the chart which do not depend on the view but
	 * only depend on the chart's models.
	 */
	private void computeMappingProperties() {
		double x;
		double largeXStep = xModel.getStepSizeValue(numberOfLargeXTicks - 1);
		largeXTick = new double[numberOfLargeXTicks];
		xLabel = new String[numberOfLargeXTicks];
		for (int i = 0; i < numberOfLargeXTicks; i++) {
			x = xModel.getValueForSteps(i, largeXStep);
			largeXTick[i] = xModel.getMappedValue(x);
			xLabel[i] = numericLabel(x, xLabelPrecision);
		}
		double y;
		double largeYStep = yModel.getStepSizeValue(numberOfLargeYTicks - 1);
		largeYTick = new double[numberOfLargeYTicks];
		yLabel = new String[numberOfLargeYTicks];
		for (int i = 0; i < numberOfLargeYTicks; i++) {
			y = yModel.getValueForSteps(i, largeYStep);
			largeYTick[i] = yModel.getMappedValue(y);
			yLabel[i] = numericLabel(y, yLabelPrecision);
		}
	}

	/**
	 * Compute the geometric view of the chart. Whenever the size of the chart
	 * has changed we have to recompute the chart's geometry.
	 */
	private void computeView() {
		int width = getSize().width;
		int height = getSize().height;
		FontMetrics fm = getFontMetrics(getFont());
		int cw = fm.stringWidth("0");
		// int ch = fm.getHeight();
		if (title != null) {
			titleXPos = leftBorder;
			titleYPos = topBorder - fm.getDescent();
		}
		// Compute the diagram's grid, take care to fit into available
		// space and to coincide corners with computed diagram values.
		int gridWidth = width - leftBorder - rightBorder;
		int gridHeight = height - topBorder - bottomBorder;
		int dx = gridWidth / (numberOfLargeXTicks - 1);
		int dy = gridHeight / (numberOfLargeYTicks - 1);
		if ((double) dy / (double) dx > yUnitToXUnitRatio) {
			dy = (int) Math.round((double) dx * yUnitToXUnitRatio);
		} else {
			dx = (int) Math.round((double) dy / yUnitToXUnitRatio);
		}
		grid.x = leftBorder;
		grid.y = topBorder;
		grid.width = dx * (numberOfLargeXTicks - 1);
		grid.height = dy * (numberOfLargeYTicks - 1);
		gridXRange = grid.width - 1;
		gridYRange = grid.height - 1;
		gridRightX = grid.x + gridXRange;
		gridBottomY = grid.y + gridYRange;
		xLabelBaseY = gridBottomY + bottomBorder - fm.getDescent();
		yLabelBaseXOffset = grid.x - cw;
		yLabelBaseYOffset = fm.getAscent() / 2;
		largeXTickPos = new int[numberOfLargeXTicks];
		xLabelBaseXOffset = new int[numberOfLargeXTicks];
		for (int i = 0; i < numberOfLargeXTicks; i++) {
			largeXTickPos[i] = xPosition(largeXTick[i]);
			xLabelBaseXOffset[i] = fm.stringWidth(xLabel[i]) / 2;
		}
		largeYTickPos = new int[numberOfLargeYTicks];
		yLabelBaseX = new int[numberOfLargeYTicks];
		for (int i = 0; i < numberOfLargeYTicks; i++) {
			largeYTickPos[i] = yPosition(largeYTick[i]);
			yLabelBaseX[i] = yLabelBaseXOffset - fm.stringWidth(yLabel[i]);
		}
		if (showValidRegion) {
			computeValidRegionView();
		}
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

	/**
	 * Paint the chart's geometry including the curent color marker and the
	 * valid color region.
	 */
	public void paint(Graphics g) {
		if (showValidRegion) {
			drawValidRegion(g);
		}
		// Sometimes the paint() message seems to come pefore things
		// are set up correctly, I don't know why!
		if (largeXTickPos == null)
			return;
		// Draw internal grid lines
		g.setColor(SystemColor.controlShadow);
		for (int i = 1; i < (numberOfLargeXTicks - 1); i++) {
			/*
			 * System.out.println("--- i = " + i); System.out.println("A = " +
			 * largeXTickPos[i]); System.out.println("B = " + grid.y);
			 * System.out.println("C = " + gridBottomY);
			 */
			g.drawLine(largeXTickPos[i], grid.y, largeXTickPos[i], gridBottomY);
		}
		for (int i = 1; i < (numberOfLargeYTicks - 1); i++) {
			g.drawLine(grid.x, largeYTickPos[i], gridRightX, largeYTickPos[i]);
		}
		// Draw grid frame
		g.setColor(SystemColor.controlText);
		g.drawRect(grid.x, grid.y, gridXRange, gridYRange);
		// Draw labels
		for (int i = firstXLabelAtTick; i < numberOfLargeXTicks; i += xLabelAtEveryTick) {
			g.drawString(xLabel[i], largeXTickPos[i] - xLabelBaseXOffset[i],
					xLabelBaseY);
		}
		for (int i = firstYLabelAtTick; i < numberOfLargeYTicks; i += yLabelAtEveryTick) {
			g.drawString(yLabel[i], yLabelBaseX[i], largeYTickPos[i]
					+ yLabelBaseYOffset);
		}
		if (title != null) {
			g.drawString(title, titleXPos, titleYPos);
		}
		int currentXPosition = xPosition(currentXMapValue);
		int currentYPosition = yPosition(currentYMapValue);
		if (showsCrosshair) {
			g.drawLine(grid.x, currentYPosition, gridRightX, currentYPosition);
			g.drawLine(currentXPosition, grid.y, currentXPosition, gridBottomY);
		}
		if (markCurrentPosition && showPosition) {
			drawMarkAt(g, currentXPosition, currentYPosition);
		}
		if (showSample) {
			displaySample(g);
		}
		if (chartPaintExtension != null) {
			chartPaintExtension.extendedPaint(g);
		}
	}

	/**
	 * If this method is called with a true argument then the most recent or
	 * 'current' position is marked in the chart.
	 */
	public void setShowPosition(boolean a) {
		showPosition = a;
	}

	/**
	 * Set this chart's paint extension hook to the given extension object. The
	 * extension object's method extendedPaint() is called whenever the chart is
	 * painted. Paint extension is switched off by sending a null pointer to
	 * this method.
	 */
	public void setChartPaintExtension(ChartPaintExtension p) {
		chartPaintExtension = p;
	}

	/**
	 * Set this chart's mouse extension hook to the given extension object.
	 * Mouse extension is switched off by sending a null pointer to this method.
	 */
	public void setChartMouseExtension(ChartMouseExtension p) {
		chartMouseExtension = p;
	}
	private boolean showSample = false;
	private double[] sample;
	private int sampleSize;
	public static final int SAMPLE_POINTS = 0;
	public static final int SAMPLE_LINE = 1;
	private int sampleType = SAMPLE_POINTS;

	public void setShowSample(boolean a) {
		showSample = a;
		repaint(grid.x, grid.y, grid.width, grid.height);
	}

	public void setShowSample(boolean a, int t) {
		sampleType = t;
		showSample = a;
		repaint(grid.x, grid.y, grid.width, grid.height);
	}

	/**
	 * Set the sample coordinates. Note that coordinates are given in the axis
	 * models' coordinates.
	 */
	public void setSample(double[] a, int s) {
		sample = a;
		sampleSize = s;
	}

	public void clearSample() {
		sample = null;
		sampleSize = 0;
	}

	private void displaySample(Graphics g) {
		if (sampleType == SAMPLE_POINTS) {
			for (int i = 0; i < sampleSize; i++) {
				drawDiagonalMarkAt(g,
						xPosition(xModel.getMappedValue(sample[i + i])),
						yPosition(yModel.getMappedValue(sample[i + i + 1])));
			}
		} else if (sampleType == SAMPLE_LINE) {
			int x0 = xPosition(xModel.getMappedValue(sample[0 + 0]));
			int y0 = yPosition(yModel.getMappedValue(sample[0 + 0 + 1]));
			int x1, y1;
			for (int i = 1; i < sampleSize; i++) {
				x1 = xPosition(xModel.getMappedValue(sample[i + i]));
				y1 = yPosition(yModel.getMappedValue(sample[i + i + 1]));
				g.drawLine(x0, y0, x1, y1);
				x0 = x1;
				y0 = y1;
			}
		}
	}

	/**
	 * Set the chart's valid region. The valid region is a region which may
	 * restrict the cursor movement within the chart. The cursor crosshair is
	 * switched off when it leaves the valid region. The input array is an array
	 * of pairs of doubles which hold the x- and y-coordinate of the valid
	 * region vertex points. Coordinates are given in the axis models' space.
	 */
	public void setValidRegion(double[] a) {
		if (a == null) {
			validRegionVertex = null;
		} else {
			validRegionVertex = new double[a.length];
			for (int i = 0; i < (a.length - 1); i += 2) {
				validRegionVertex[i] = xModel.getMappedValue(a[i]);
				validRegionVertex[i + 1] = yModel.getMappedValue(a[i + 1]);
			}
		}
		showValidRegion = true;
		validRegionDirty = true;
	}

	/** Compute the geometric view of the chart's valid region. */
	private void computeValidRegionView() {
		if (validRegionVertex == null) {
			validRegion = null;
		} else {
			validRegion = new Polygon();
			for (int i = 0; i < (validRegionVertex.length - 1); i += 2) {
				validRegion.addPoint(xPosition(validRegionVertex[i]),
						yPosition(validRegionVertex[i + 1]));
			}
			validRegionDirty = false;
		}
	}

	/** Draw the chart's valid region. */
	private void drawValidRegion(Graphics g) {
		if (validRegionDirty)
			computeValidRegionView();
		if (validRegion != null) {
			g.setColor(SystemColor.controlLtHighlight);
			// g.setColor(Color.white);
			g.fillPolygon(validRegion);
			g.setColor(SystemColor.controlShadow);
			g.drawPolygon(validRegion);
		}
	}

	/** Draw a mark at the given position. **/
	private void drawMarkAt(Graphics g, int mx, int my) {
		g.drawLine(mx, my - markHalfHeight, mx, my + markHalfHeight);
		g.drawLine(mx - markHalfWidth, my, mx + markHalfWidth, my);
	}

	/** Draw a mark at the given position. **/
	private void drawDiagonalMarkAt(Graphics g, int mx, int my) {
		g.drawLine(mx - markHalfWidth, my - markHalfHeight, mx + markHalfWidth,
				my + markHalfHeight);
		g.drawLine(mx - markHalfWidth, my + markHalfHeight, mx + markHalfWidth,
				my - markHalfHeight);
	}

	/**
	 * Transform the model's x coordinate to the chart's horizontal pixel
	 * position.
	 */
	public int xTransform(double x) {
		return (xPosition(xModel.getMappedValue(x)));
	}

	/**
	 * Transform the model's y coordinate to the chart's vertical pixel
	 * position.
	 */
	public int yTransform(double y) {
		return (yPosition(yModel.getMappedValue(y)));
	}

	// Return the chart view's x position for the model's mapping
	// values.
	private int xPosition(double x) {
		return ((int) Math.round((double) gridXRange * x + grid.x));
	}

	// Return the chart view's y position for the model's mapping
	// values.
	private int yPosition(double y) {
		return (gridBottomY - (int) Math.round((double) gridYRange * y));
	}

	// Return the model's mapping values for the chart view's x
	// position.
	private double xValue(int x) {
		return ((double) (x - grid.x) / (double) gridXRange);
	}

	// Return the model's mapping values for the chart view's y
	// position.
	private double yValue(int y) {
		return ((double) (gridBottomY - y) / (double) gridYRange);
	}
	private boolean validStart;

	// Mouse Motion Listener -----------------------------------
	// The mouse has been dragged within the chart.
	public void mouseDragged(MouseEvent e) {
		if (!enabled)
			return;
		if (validStart) {
			trackMouse(e.getX(), e.getY());
			repaint(grid.x, grid.y, grid.width, grid.height);
		}
	}

	public void mouseMoved(MouseEvent e) {
	}

	// Mouse Listener ------------------------------------------
	// A mouse button has been pressed within the chart.
	public void mousePressed(MouseEvent e) {
		if (!enabled)
			return;
		// System.out.println("Mouse pressed!");
		int x = e.getX();
		int y = e.getY();
		if (showValidRegion) {
			validStart = (validRegion == null) ? false : validRegion.contains(
					x, y);
		} else {
			validStart = grid.contains(x, y);
		}
		if (validStart) {
			if (chartMouseExtension != null) {
				chartMouseExtension.extendedMousePressed(e,
						xModel.getValueFromMap(xValue(x)),
						yModel.getValueFromMap(yValue(y)));
			}
			markCurrentPosition = false;
			trackMouse(x, y);
			repaint(grid.x, grid.y, grid.width, grid.height);
		}
	}

	// The mouse button has been released. Switch off the cursor
	// crosshair and mark the last position.
	public void mouseReleased(MouseEvent e) {
		if (!enabled)
			return;
		// System.out.println("Mouse released!");
		if (validStart) {
			int x = e.getX();
			int y = e.getY();
			if (chartMouseExtension != null) {
				chartMouseExtension.extendedMouseReleased(e,
						xModel.getValueFromMap(xValue(x)),
						yModel.getValueFromMap(yValue(y)));
			}
			trackMouse(x, y);
			if (showsCrosshair) {
				showsCrosshair = false;
			}
			markCurrentPosition = true;
			validStart = false;
			repaint(grid.x, grid.y, grid.width, grid.height);
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Track the mouse when the button is down. The tracking method computes the
	 * model coordinates for the target position and then asks the chart
	 * listener whether he is willing to accept the new coordinates. If this is
	 * true then the model parameters are set accordingly. If the chart listener
	 * refuses to accept the new values then the crosshair is switched off and
	 * the model parameters are not updated.
	 */
	private void trackMouse(int x, int y) {
		if (grid.contains(x, y)) {
			double cx = xValue(x);
			double cy = yValue(y);
			double mx = xModel.getValueFromMap(cx);
			double my = yModel.getValueFromMap(cy);
			// Check whether the chart listener accepts the new values
			if (chartListener != null) {
				if (chartListener.chartValueChanged(mx, my)) {
					// New value has been accepted
					currentXMapValue = cx;
					xModel.setValue(mx);
					currentYMapValue = cy;
					yModel.setValue(my);
					// System.out.println(x + " " + y + " -> " +cx + " " + cy +
					// " -> " + mx + " " + my);
					if (!showsCrosshair) {
						showsCrosshair = true;
					}
				} else {
					// New value has been rejected
					if (showsCrosshair) {
						showsCrosshair = false;
					}
				}
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
	}

	/**
	 * Create a string representation of x with at most n decimal digits.
	 */
	private String numericLabel(double x, int n) {
		String s = null;
		if (n == 0) {
			s = String.valueOf(Math.round(x));
			// System.out.println(x + " with pf= " + n +
			// " (precision="+n+") is " + s );
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
}
