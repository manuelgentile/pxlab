package de.pxlab.pxl;

import java.awt.*;
import java.awt.image.*;

import de.pxlab.util.StringExt;

/**
 * A horizontal scale with a pointer which can be used for rating scale
 * responses.
 * 
 * @version 0.3.1
 */
/*
 * 06/03/01
 * 
 * 11/17/01 added left and right text labels
 * 
 * 11/26/01 added adjustable label gap
 * 
 * 12/12/01 added unsignedNumbers parameter
 * 
 * 12/17/02 fixed bug when rounding size to integer multiples of tick steps
 * 
 * 2006/06/28 allow pointer to cover ticks.
 * 
 * 2007/06/01 use buffered drawing to make things work properly with tracking
 * timers in TRANSPARENT mode
 * 
 * 2007/06/15 derived from class ResponseScaleElement
 * 
 * 2007/06/20 21:07
 * 
 * 2007/06/27 made labels a TextParagraphElement
 */
public class RatingScaleElement extends DisplayElement implements
		RatingScaleTickCodes, RatingScalePointerCodes, RatingScaleLabelCodes,
		PositionReferenceCodes {
	/** Number of ticks on the scale. */
	protected int nTicks = 5;
	/** The lowest tick's value on the scale. */
	protected int lowestTick = -2;
	/** The step size between ticks on the scale. */
	protected int tickStep = 1;
	/**
	 * Tick mark type as described by RatingScaleTickCodes.
	 * 
	 * @see RatingScaleTickCodes
	 */
	protected int tickType = BAR_TICKS;
	/**
	 * Pointer type as described by RatingScalePointerCodes.
	 * 
	 * @see RatingScalePointerCodes
	 */
	protected int pointerType = DOWN_POINTER;
	protected int labelType = NUMBER_LABELS;
	/** Labels at the ticks or at the left/right end of the scale. */
	protected String[] labels = null;
	protected String[] tickLabels = null;
	/** Distance between labels and scale bar. */
	protected int labelDistance = 20;
	/** Distance between anchors and scale bar. */
	protected int anchorDistance = 20;
	/** Tick color parameter. */
	protected ExPar tickColorPar;
	/** Pointer color parameter. */
	protected ExPar pointerColorPar;
	/**
	 * Reference point for positioning.
	 * 
	 * @see PositionReferenceCodes
	 */
	protected int referencePoint = BASE_CENTER;
	/** The font used for numbers. */
	protected Font font = null;
	protected FontMetrics fontMetrics = null;
	protected int fontSize = 20;
	/** Half of the lineWidth but at least 1 */
	protected int lineWidth2 = 1;
	protected int pointerSize = 20;
	protected int pointerWidth = 0;
	protected int pointerHeight = 0;
	protected int scaleBarLeftX;
	protected int scaleBarRightX;
	protected int scaleBarY;
	protected int scaleBarWidth;
	protected int boxUp;
	protected int boxDown;
	protected int boxLeftRight;
	protected boolean showScaleBar = true;
	protected boolean showPointer = true;
	protected boolean showLabels = true;
	protected boolean anchorLabels = true;
	private TextParagraphElement leftAnchor, rightAnchor;
	private TextParagraphElement labelText;
	private String[] ta = new String[1];
	private double pointerValue = 0.0;
	private boolean validPointer = false;
	private BufferedImage memBuf = null;

	/** Create a new scale with the given bar, tick, and pointer color. */
	public RatingScaleElement(ExPar bc, ExPar tc, ExPar pc) {
		type = DisplayElement.SCALE;
		colorPar = bc;
		tickColorPar = tc;
		pointerColorPar = pc;
	}

	/** Set most of this scale's parameters. */
	public void setProperties(int x, int y, int width, int height, int refPnt,
			int nts, int loTick, int tStep, int tType, int pType, int lType,
			boolean anchLb, String[] lb, int lw, int ps, int fs, int labDist,
			int anchDist) {
		setLocation(x, y);
		setSize(width, height);
		referencePoint = refPnt;
		nTicks = (nts >= 2) ? nts : 2;
		lowestTick = loTick;
		tickStep = tStep;
		tickType = tType;
		pointerType = pType;
		labelType = lType;
		anchorLabels = anchLb;
		labels = lb;
		setLineWidth(lw);
		pointerSize = ps;
		fontSize = fs;
		labelDistance = labDist;
		anchorDistance = anchDist;
	}

	/** Show this Scale on screen. */
	public void show() {
		// Have to switch off antialiasing here since otherwise the
		// text labels change their appearance when they are redrawn
		// multiple times.
		Object textRenderingHint = graphics2D
				.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
		graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		// scale bar width int pixels
		scaleBarWidth = size.width;
		// height of scale bar + ticks
		int scaleHeight = size.height;
		if ((scaleBarWidth <= 1) || (scaleHeight == 0) || (lineWidth <= 0))
			return;
		// Correct width to integer multiples of tick steps + 1
		int tickStep = scaleBarWidth / (nTicks - 1);
		scaleBarWidth = (nTicks - 1) * tickStep + 1;
		lineWidth2 = (lineWidth + 1) / 2;
		// This is the top left coordinate of the enclosing rectangle
		int x = locX(referencePoint, location.x, scaleBarWidth);
		int y = rectLocY(referencePoint, location.y, scaleHeight);
		// System.out.println("top left = " + x + ", " + y);
		// Actual scale bar position
		scaleBarLeftX = x;
		scaleBarRightX = x + scaleBarWidth - 1;
		scaleBarY = y + scaleHeight / 2;
		// pointer delta x
		int pointerDX = 0;
		// pointer delta y upwards
		int pointerUpY = 0;
		// pointer delta y downwards
		int pointerDownY = 0;
		switch (pointerType) {
		case NO_POINTER:
			break;
		case DOWN_POINTER:
			pointerWidth = 2 * (pointerSize / 3);
			pointerHeight = 3 * (pointerSize / 3);
			pointerDX = pointerWidth / 2;
			pointerUpY = pointerHeight;
			break;
		case UP_POINTER:
			pointerWidth = 2 * (pointerSize / 3);
			pointerHeight = 3 * (pointerSize / 3);
			pointerDX = pointerWidth / 2;
			pointerDownY = pointerHeight;
			break;
		case CROSS_POINTER:
			pointerWidth = 8 * (pointerSize / 8);
			pointerHeight = pointerWidth;
			pointerDX = 5 * pointerWidth / 8;
			pointerUpY = pointerDX;
			pointerDownY = pointerDX;
			break;
		case DOUBLE_POINTER:
			pointerWidth = 2 * (pointerSize / 3);
			pointerHeight = 4 * (pointerSize / 4);
			pointerDX = pointerWidth / 2;
			pointerUpY = pointerHeight / 2;
			pointerDownY = pointerUpY;
			break;
		case LINE_POINTER:
			pointerWidth = (pointerSize / 3);
			pointerHeight = 3 * (pointerSize / 3);
			pointerDX = pointerWidth / 2;
			pointerUpY = pointerHeight / 2;
			pointerDownY = pointerUpY;
			break;
		}
		// actual horizontal scale overlap
		boxLeftRight = (lineWidth2 > pointerDX) ? lineWidth2 : pointerDX;
		if (tickWidth() > (2 * boxLeftRight + 1))
			boxLeftRight = tickWidth() / 2;
		// actual vertical upwards scale overlap
		boxUp = (scaleHeight / 2 > pointerUpY) ? scaleHeight / 2 : pointerUpY;
		// actual vertical downwards scale overlap
		boxDown = (scaleHeight / 2 > pointerDownY) ? scaleHeight / 2
				: pointerDownY;
		int boxx = scaleBarLeftX - boxLeftRight;
		int boxy = scaleBarY - boxUp;
		int boxWidth = scaleBarWidth + boxLeftRight + boxLeftRight;
		int boxHeight = boxUp + boxDown;
		// create memory buffer is necessary
		if ((memBuf == null) || (memBuf.getWidth() != boxWidth)
				|| (memBuf.getHeight() != boxHeight)) {
			memBuf = new BufferedImage(boxWidth, boxHeight,
					BufferedImage.TYPE_INT_RGB);
		}
		Graphics2D gr2D = memBuf.createGraphics();
		// shift its origin
		gr2D.translate(-boxx, -boxy);
		// clear it to the background color
		gr2D.setColor(ExPar.ScreenBackgroundColor.getDevColor());
		gr2D.fillRect(boxx, boxy, boxWidth, boxHeight);
		// gr2D.setColor(Color.GREEN);
		// gr2D.drawRect(boxx, boxy, boxWidth-1, boxHeight-1);
		// First draw the scale bar at the given reference point
		if (showScaleBar) {
			gr2D.setColor(colorPar.getDevColor());
			gr2D.fillRect(scaleBarLeftX, scaleBarY - lineWidth2, scaleBarWidth,
					lineWidth);
		}
		createLabels();
		if (labelType != NO_LABELS) {
			if (labelText == null) {
				labelText = new TextParagraphElement(tickColorPar);
				labelText.setStrictParagraphMode(true);
			}
		}
		// Now draw the ticks if any.
		if (tickType != NO_TICKS) {
			gr2D.setColor(tickColorPar.getDevColor());
			graphics2D.setColor(tickColorPar.getDevColor());
			int tx = scaleBarLeftX;
			for (int i = 0; i < nTicks; i++) {
				drawTickAt(gr2D, tx, scaleBarY);
				if (labelType != NO_LABELS) {
					ta[0] = tickLabels[i];
					labelText.setProperties(ta, "SansSerif", Font.PLAIN,
							fontSize, tx, scaleBarY + boxDown + labelDistance,
							tickStep, TOP_CENTER, AlignmentCodes.CENTER, false,
							true, 1.0);
					labelText.setPixelReferencePoint(TOP_CENTER);
					labelText.show();
				}
				tx += tickStep;
			}
		}
		if (showPointer && validPointer) {
			drawPointer(gr2D, getPointerValue());
		}
		graphics2D.drawImage(memBuf, boxx, boxy, null);
		if (anchorLabels) {
			if (leftAnchor == null)
				leftAnchor = new TextParagraphElement(tickColorPar);
			if (rightAnchor == null)
				rightAnchor = new TextParagraphElement(tickColorPar);
			ta[0] = labels[0];
			leftAnchor.setProperties(ta, "SansSerif", Font.PLAIN, fontSize,
					scaleBarLeftX - boxLeftRight - labelDistance, scaleBarY
							- lineWidth2, displayWidth / 2, MIDDLE_RIGHT,
					AlignmentCodes.RIGHT, false, false, 1.0);
			leftAnchor.setPixelReferencePoint(MIDDLE_RIGHT);
			leftAnchor.show();
			ta[0] = labels[1];
			rightAnchor.setProperties(ta, "SansSerif", Font.PLAIN, fontSize,
					scaleBarRightX + boxLeftRight + labelDistance, scaleBarY
							- lineWidth2, displayWidth / 2, MIDDLE_LEFT,
					AlignmentCodes.LEFT, false, false, 1.0);
			rightAnchor.setPixelReferencePoint(MIDDLE_LEFT);
			rightAnchor.show();
		}
		graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				textRenderingHint);
		// We make the bounds larger than the scale in order to
		// capture the subject's cursor
		setBounds(boxx, boxy, boxWidth, boxHeight);
	}

	private void createLabels() {
		if (labelType == NO_LABELS) {
		} else if (labelType == TEXT_LABELS) {
			if (labels.length >= nTicks) {
				tickLabels = labels;
			} else {
				new ParameterValueError("Missing labels");
				tickLabels = new String[nTicks];
				for (int i = 0; i < nTicks; i++)
					tickLabels[i] = "?";
			}
		} else {
			// number labels
			if (tickLabels == null || tickLabels.length < nTicks)
				tickLabels = new String[nTicks];
			int val = lowestTick;
			for (int i = 0; i < nTicks; i++) {
				if (labelType == UNSIGNED_NUMBER_LABELS) {
					tickLabels[i] = String.valueOf(Math.abs(val));
				} else {
					tickLabels[i] = String.valueOf(val);
					if ((lowestTick < 0) && (val > 0))
						tickLabels[i] = "+" + tickLabels[i];
				}
				val += tickStep;
			}
		}
		if (anchorLabels) {
			if (labels.length >= 2) {
			} else {
				new ParameterValueError("Missing anchor labels");
				labels = new String[2];
				labels[0] = "?";
				labels[1] = "?";
			}
		}
	}

	/**
	 * Set the font of this text element if the new font properties are
	 * different from the current font.
	 */
	public void setFont(String fn, int ft, int fs) {
		if ((font == null) || (!(font.getName().equals(fn)))
				|| (font.getStyle() != ft) || (font.getSize() != fs)) {
			// System.out.println("Setting font family " + fn + " using style "
			// + ft + " at size " + fs + "pt");
			font = new Font(fn, ft, fs);
			fontMetrics = graphics2D.getFontMetrics();
			graphics2D.setFont(font);
		}
	}

	/** Draw a tick mark at the given position. */
	private void drawTickAt(Graphics2D g, int x, int y) {
		int w = tickWidth();
		int h = tickHeight();
		if (tickType == BAR_TICKS) {
			g.fillRect(x - w / 2, y - h / 2, w, h);
		} else if (tickType == BULLET_TICKS) {
			g.fillOval(x - w / 2, y - h / 2, w, h);
		}
	}

	private int tickWidth() {
		int x = 0;
		switch (tickType) {
		case BAR_TICKS:
			x = 2 * lineWidth2;
			break;
		case BULLET_TICKS:
		case CIRCLE_TICKS:
		case CROSS_TICKS:
			x = size.height;
			break;
		}
		return x;
	}

	private int tickHeight() {
		return (tickType != NO_TICKS) ? size.height : 0;
	}
	private int[] px = new int[5];
	private int[] py = new int[5];

	/** Draw the pointer at the given screen position. */
	private void drawPointerAt(Graphics2D g, int x, int y) {
		if (pointerType == NO_POINTER)
			return;
		g.setColor(pointerColorPar.getDevColor());
		int w2, h1, h2, sign;
		switch (pointerType) {
		case CROSS_POINTER:
			Stroke str = g.getStroke();
			g.setStroke(new BasicStroke((float) pointerWidth / 4,
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
			g.drawLine(x - pointerWidth / 2, y - pointerHeight / 2, x
					+ pointerWidth / 2, y + pointerHeight / 2);
			g.drawLine(x - pointerWidth / 2, y + pointerHeight / 2, x
					+ pointerWidth / 2, y - pointerHeight / 2);
			g.setStroke(str);
			break;
		case LINE_POINTER:
			g.fillRect(x - lineWidth2, y - pointerHeight / 2, lineWidth,
					pointerHeight);
			break;
		case DOWN_POINTER:
		case UP_POINTER:
			w2 = pointerWidth / 2;
			h1 = pointerHeight / 3;
			h2 = 2 * (pointerHeight / 3);
			sign = (pointerType == DOWN_POINTER) ? (-1) : 1;
			setPointerPolygon(x, y, w2, h1, h2, sign, px, py);
			g.fillPolygon(px, py, 5);
			break;
		case DOUBLE_POINTER:
			w2 = pointerWidth / 2;
			h1 = pointerHeight / 3;
			h2 = pointerHeight / 3;
			setPointerPolygon(x, y, w2, h1, h2, 1, px, py);
			g.fillPolygon(px, py, 5);
			setPointerPolygon(x, y, w2, h1, h2, -1, px, py);
			g.fillPolygon(px, py, 5);
			break;
		}
	}

	private void setPointerPolygon(int x, int y, int w2, int h1, int h2,
			int sign, int[] px, int[] py) {
		px[0] = x;
		py[0] = y;
		px[1] = px[2] = x - w2;
		px[3] = px[4] = x + w2;
		py[1] = py[4] = y + sign * h1;
		py[2] = py[3] = y + sign * (h1 + h2);
	}

	/**
	 * Set the current pointer position from the given screen coordinates. This
	 * method delivers valid results only after the scale has first been drawn,
	 * since before being drawn the scale's bounding box is undefined.
	 */
	public void setPointer(int x, int y) {
		// System.out.println("bounds = " + b);
		// System.out.println("x = " + x + ", y = " + y);
		if ((x < scaleBarLeftX) && (x > scaleBarLeftX - lineWidth2))
			x = scaleBarLeftX;
		if ((x > scaleBarRightX) && (x < scaleBarRightX + lineWidth2))
			x = scaleBarRightX;
		if ((x >= scaleBarLeftX) && (x <= scaleBarRightX)
				&& (y > scaleBarY - boxUp) && (y < scaleBarY + boxDown)) {
			setPointer(((double) (x - scaleBarLeftX))
					/ (double) (scaleBarWidth - 1) * (nTicks - 1) * tickStep
					+ lowestTick);
		} else {
			// Clicking outside of the scale area does not change the pointer
			// state
		}
	}

	/** Set the current pointer position. */
	public void setPointer(double p) {
		if ((p >= lowestTick) && (p <= (lowestTick + (nTicks - 1) * tickStep))) {
			pointerValue = p;
			validPointer = true;
		} else {
			validPointer = false;
		}
	}

	/**
	 * Check whether this scale has a valid pointer position.
	 * 
	 * @return true if the scale currently has a valid pointer position.
	 */
	public boolean hasValidPointer() {
		return validPointer;
	}

	/**
	 * Get the current pointer position an the scale in scale coordinates.
	 */
	public double getPointerValue() {
		return pointerValue;
	}

	/**
	 * Draw the pointer at the given user coordinate position on the scale.
	 */
	private void drawPointer(Graphics2D g, double value) {
		drawPointerAt(g, (int) (scaleBarLeftX + (value - lowestTick)
				/ ((nTicks - 1) * tickStep) * scaleBarWidth), scaleBarY);
	}
}
