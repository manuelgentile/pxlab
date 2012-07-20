package de.pxlab.awtx;

import java.awt.*;
import java.awt.geom.*;
import java.util.Stack;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Static methods to paint geometric objects.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2007/05/10
 */
public class Painter {
	private static double scale = 1.0;
	private static boolean dbg = false;
	private static boolean pr = false;

	/**
	 * Paint a path according to the given path description.
	 * 
	 * This method paints objects defined by an array of strings containing the
	 * path description.
	 * 
	 * <p>
	 * The path description may contain the following commands:
	 * 
	 * <dl>
	 * 
	 * <dt>M x y
	 * <dd>move the current point to (x,y) and initialize a new subpath.
	 * 
	 * <dt>Z
	 * <dd>close the current subpath by connecting the current point with the
	 * most recent argument of an M command which started a subpath.
	 * 
	 * <dt>L x y
	 * <dd>draw a straight line from the current point to (x,y) and make this
	 * the current point.
	 * 
	 * <dt>H x
	 * <dd>draw a straight horizontal line from the current point to (x, current
	 * y) and make this the current point.
	 * 
	 * <dt>V y
	 * <dd>draw a straight vertical line from the current point to (current x,
	 * y) and make this the current point.
	 * 
	 * <dt>C ax ay bx by x y
	 * <dd>draw a cubic Bezier curve from the current point to (x,y) with using
	 * (ax,ay) and (bx,by) as control points. (x,y) becomes the current point.
	 * 
	 * <dt>S bx by x y
	 * <dd>draw a smooth cubic Bezier curve from the current point to (x,y) with
	 * using (bx,by) as control point for the destination point. The control
	 * point for the start point is assumed to be the mirror point of the path
	 * which arrived at the current point;
	 * 
	 * <dt>Q ax ay x y
	 * <dd>draw a quadratic Bezier curve from the current point to (x,y) with
	 * using (ax,ay) as control point. (x,y) becomes the new current point.
	 * 
	 * <dt>T x y
	 * <dd>draw a quadratic Bezier curve from the current point to (x,y) with
	 * using the mirror point of the current point's control point as control
	 * point. (x,y) becomes the new current point.
	 * 
	 * <dt>F
	 * <dd>fill the path in its current state by the fill color.
	 * 
	 * <dt>D
	 * <dd>draw the path in its current state by the outline color.
	 * 
	 * <dt>P
	 * <dd>fill the path in its current state and draw its outline using the
	 * respective colors.
	 * 
	 * <dt>R
	 * <dd>clear the path.
	 * 
	 * </dl>
	 * 
	 * <p>
	 * Capital command letters indicate absolute coordinates. Lower case command
	 * letters indicate relative coordinates with reference to the current
	 * point. The M, L, C, S, Q, and T command move the current point to the
	 * destination point.
	 * 
	 * <p>
	 * Note that a path may contain more the a single subpath. Here is an
	 * example string which creates a heart:
	 * 
	 * <pre>
	 * &quot;M 0,-60 C 50,-100,100,-40,0,60 C -100,-40,-50,-100,0,-60 Z&quot;
	 * </pre>
	 * 
	 * <p>
	 * This example contains an initial move, two successive cubic Bezier curves
	 * and a closing command. The coordinate system for this example is assumed
	 * to have its origin at the screen's center. Note that command letters and
	 * coordinates may either be separated by empty space or by a semicolon.
	 * 
	 * @param g
	 *            the Graphics2D context for painting
	 * @param x
	 *            a horizontal shift added to the path
	 * @param y
	 *            a vertical shift added to the path
	 * @param sc
	 *            a scaling factor for all coordinates of the path
	 * @param lineWidth
	 *            the line width for drawing the path's outline, independent of
	 *            the scaling factor
	 * @param lineColor
	 *            the outline color
	 * @param fillColor
	 *            the fill color
	 * @param doFill
	 *            if true then the path is filled
	 * @param pathString
	 *            an array of strings containing the path description.
	 * @return the bounding Rectangle.
	 */
	public static Rectangle paint(Graphics2D g, int x, int y, double sc,
			double lineWidth, Color lineColor, Color fillColor, boolean doFill,
			String[] pathString) {
		boolean doOutline = lineWidth > 0.0;
		scale = sc;
		pushState(g);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		Stroke stroke = new BasicStroke((float) lineWidth,
				BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
		Path2D.Double path = new Path2D.Double();
		double[] p = new double[6];
		Rectangle bounds = new Rectangle(0, 0, -1, -1);
		double cx = 0.0, cy = 0.0;
		double cpx = 0.0, cpy = 0.0;
		for (int k = 0; k < pathString.length; k++) {
			String[] s = pathString[k].split("[\\s,]+");
			int n = s.length;
			int i = 0;
			while (i < n) {
				if (dbg)
					System.out.print("\nPathElement.show() add " + s[i]);
				switch ((int) (s[i].charAt(0))) {
				case 'M':
					if (pick(s, ++i, 2, p) == 2) {
						path_moveTo(path, cpx = cx = p[0] + x, cpy = cy = p[1]
								+ y);
						i += 2;
					}
					break;
				case 'm':
					if (pick(s, ++i, 2, p) == 2) {
						path_moveTo(path, cpx = cx = cx + p[0], cpy = cy = cy
								+ p[1]);
						i += 2;
					}
					break;
				case 'L':
					if (pick(s, ++i, 2, p) == 2) {
						cpx = cx;
						cpy = cy;
						path_lineTo(path, cx = p[0] + x, cy = p[1] + y);
						i += 2;
					}
					break;
				case 'l':
					if (pick(s, ++i, 2, p) == 2) {
						cpx = cx;
						cpy = cy;
						path_lineTo(path, cx = cx + p[0], cy = cy + p[1]);
						i += 2;
					}
					break;
				case 'H':
					if (pick(s, ++i, 1, p) == 1) {
						cpx = cx;
						cpy = cy;
						path_lineTo(path, cx = p[0] + x, cy);
						i += 1;
					}
					break;
				case 'h':
					if (pick(s, ++i, 1, p) == 1) {
						cpx = cx;
						cpy = cy;
						path_lineTo(path, cx = cx + p[0], cy);
						i += 1;
					}
					break;
				case 'V':
					if (pick(s, ++i, 1, p) == 1) {
						cpx = cx;
						cpy = cy;
						path_lineTo(path, cx, cy = p[0] + y);
						i += 1;
					}
					break;
				case 'v':
					if (pick(s, ++i, 1, p) == 1) {
						cpx = cx;
						cpy = cy;
						path_lineTo(path, cx, cy = cy + p[0]);
						i += 1;
					}
					break;
				case 'C':
					if (pick(s, ++i, 6, p) == 6) {
						path_curveTo(path, p[0] + x, p[1] + y, cpx = p[2] + x,
								cpy = p[3] + y, cx = p[4] + x, cy = p[5] + y);
						i += 6;
					}
					break;
				case 'c':
					if (pick(s, ++i, 6, p) == 6) {
						path_curveTo(path, cx + p[0], cy + p[1], cpx = cx
								+ p[2], cpy = cy + p[3], cx + p[4], cy + p[5]);
						cx = cx + p[4];
						cy = cy + p[5];
						i += 6;
					}
					break;
				case 'S':
					if (pick(s, ++i, 4, p) == 4) {
						path_curveTo(path, cx + cx - cpx, cy + cy - cpy,
								cpx = p[0] + x, cpy = p[1] + y, cx = p[2] + x,
								cy = p[3] + y);
						i += 4;
					}
					break;
				case 's':
					if (pick(s, ++i, 4, p) == 4) {
						path_curveTo(path, cx + cx - cpx, cy + cy - cpy,
								cpx = cx + p[0], cpy = cy + p[1], cx + p[2], cy
										+ p[3]);
						cx = cx + p[2];
						cy = cy + p[3];
						i += 4;
					}
					break;
				case 'Q':
					if (pick(s, ++i, 4, p) == 4) {
						path_quadTo(path, cpx = p[0] + x, cpy = p[1] + y,
								cx = p[2] + x, cy = p[3] + y);
						i += 4;
					}
					break;
				case 'q':
					if (pick(s, ++i, 4, p) == 4) {
						path_quadTo(path, cpx = cx + p[0], cpy = cy + p[1], cx
								+ p[2], cy + p[3]);
						cx = cx + p[2];
						cy = cy + p[3];
						i += 4;
					}
					break;
				case 'T':
					if (pick(s, ++i, 2, p) == 2) {
						path_quadTo(path, cx + cx - cpx, cy + cy - cpy,
								cx = p[0] + x, cy = p[1] + y);
						i += 2;
					}
					break;
				case 't':
					if (pick(s, ++i, 2, p) == 2) {
						path_quadTo(path, cx - cpx, cy - cpy, cx + p[0], cy
								+ p[1]);
						cx = cx + p[0];
						cy = cy + p[1];
						i += 2;
					}
					break;
				case 'z':
				case 'Z':
					path_closePath(path);
					i += 1;
					break;
				case 'r':
				case 'R':
					bounds.add(path.getBounds());
					path_reset(path);
					cx = 0.0;
					cy = 0.0;
					i += 1;
					break;
				case 'f':
				case 'F':
					g.setPaint(fillColor);
					g.fill(path);
					i += 1;
					break;
				case 'd':
				case 'D':
					g.setPaint(lineColor);
					g.setStroke(stroke);
					g.draw(path);
					i += 1;
					break;
				case 'p':
				case 'P':
					g.setPaint(fillColor);
					g.fill(path);
					g.setPaint(lineColor);
					g.setStroke(stroke);
					g.draw(path);
					i += 1;
				}
				// System.out.println("\nCurrentPoint = (" + cx + "," + cy +
				// ") Recent Ctrl Point = (" + cpx + "," + cpy + ")");
			}
		}
		if (doFill) {
			g.setPaint(fillColor);
			g.fill(path);
		}
		if (doOutline) {
			g.setPaint(lineColor);
			g.setStroke(stroke);
			g.draw(path);
		}
		bounds.add(path.getBounds());
		popState(g);
		return bounds;
	}

	private static int pick(String[] s, int i, int n, double[] p) {
		if ((i + n - 1) >= s.length)
			return 0;
		for (int j = 0; j < n; j++) {
			try {
				p[j] = scale * Double.valueOf(s[i + j]).doubleValue();
				if (dbg)
					System.out.print(" " + p[j]);
			} catch (NumberFormatException nfx) {
				p[j] = 0.0;
			}
		}
		return n;
	}
	private static Stack graphicsState = new Stack();

	private static void pushState(Graphics2D g) {
		graphicsState.push(new State(g));
	}

	private static void popState(Graphics2D g) {
		if (!graphicsState.empty()) {
			State s = (State) graphicsState.pop();
			g.setBackground(s.bg);
			g.setPaint(s.paint);
			g.setFont(s.font);
			g.setStroke(s.stroke);
			g.setTransform(s.transform);
			g.setComposite(s.composite);
			g.setClip(s.clip);
		}
	}
	private static class State {
		Color bg;
		Paint paint;
		Font font;
		Stroke stroke;
		AffineTransform transform;
		Composite composite;
		Shape clip;

		public State(Graphics2D g) {
			bg = g.getBackground();
			paint = g.getPaint();
			font = g.getFont();
			stroke = g.getStroke();
			transform = g.getTransform();
			composite = g.getComposite();
			clip = g.getClip();
		}
	}
	private static NumberFormat f = NumberFormat.getInstance(Locale.US);
	static {
		f.setMaximumFractionDigits(0);
		f.setGroupingUsed(false);
	}

	public static void path_moveTo(Path2D.Double path, double x, double y) {
		if (pr)
			System.out.println("\nM " + f.format(x) + " " + f.format(y) + " ");
		path.moveTo(x, y);
	}

	public static void path_lineTo(Path2D.Double path, double x, double y) {
		if (pr)
			System.out.println("L " + f.format(x) + " " + f.format(y) + " ");
		path.lineTo(x, y);
	}

	public static void path_curveTo(Path2D.Double path, double ax, double ay,
			double bx, double by, double x, double y) {
		if (pr)
			System.out.println("C " + f.format(ax) + " " + f.format(ay) + " "
					+ f.format(bx) + " " + f.format(by) + " " + f.format(x)
					+ " " + f.format(y) + " ");
		path.curveTo(ax, ay, bx, by, x, y);
	}

	public static void path_quadTo(Path2D.Double path, double ax, double ay,
			double x, double y) {
		if (pr)
			System.out.println("Q " + f.format(ax) + " " + f.format(ay) + " "
					+ f.format(x) + " " + f.format(y) + " ");
		path.quadTo(ax, ay, x, y);
	}

	public static void path_closePath(Path2D.Double path) {
		if (pr)
			System.out.println("Z ");
		path.closePath();
	}

	public static void path_reset(Path2D.Double path) {
		if (pr)
			System.out.println("R ");
		path.reset();
	}
}
