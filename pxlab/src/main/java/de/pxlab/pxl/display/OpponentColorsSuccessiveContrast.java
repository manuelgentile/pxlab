package de.pxlab.pxl.display;

import de.pxlab.pxl.Display;
import de.pxlab.pxl.*;
import java.awt.*;

/**
 * After Effects and Opponent Colors.
 * 
 * <p>
 * Looking at a colored area for some time leads to adaptation: The visual
 * system adjusts its reference point. The color becomes desaturated (see
 * demonstration "Desaturation"), and after removing the adapting field, stimuli
 * that looked white earlier will get the color which is complementary to the
 * adaptation color.
 * 
 * <p>
 * Look at the fixation mark for some time. The switch to display group 1 and
 * the colored patches will be switched off, and one can see the after effect on
 * the white background.
 * 
 * <p>
 * The colors used in this demonstration correspond to the unique hues blue,
 * yellow, green, and red of opponent colors theory. One can see in the
 * chromaticity diagram that unique red is not complementary to unique green.
 * Careful observation also shows that the after effect of unique green is not
 * unique red. These are hints to the fact that the red/green channel of the
 * opponent colors system is not a linear code of cone absorptions.
 * 
 * <p>
 * Brown, J. L. (1965). Afterimages. In C. H. Graham (Ed.) Vision and visual
 * perception. New York: Wiley.
 * 
 * @author H. Irtel
 * @author E. Reitmayr
 * @version 0.2.1
 */
/*
 * 
 * 2004/08/17 removed timing entries and created multiple groups
 * 
 * 05/19/00
 */
public class OpponentColorsSuccessiveContrast extends Display {
	public ExPar SquareSize = new ExPar(PROPORT, new ExParValue(0.5),
			"Size of the little squares");
	public ExPar Square1Color = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Color of the first square");
	public ExPar Square2Color = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.YELLOW)),
			"Color of the second square");
	public ExPar Square3Color = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLUE)),
			"Color of the third square");
	public ExPar Square4Color = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GREEN)),
			"Color of the fourth square");
	public ExPar Square5Color = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.RED)),
			"Color of the fifth square");
	public ExPar CrossColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Color of the fixation cross");

	/** Cunstructor creating the title of the display. */
	public OpponentColorsSuccessiveContrast() {
		setTitleAndTopic("Opponent Colors Successive Contrast", ADAPTATION_DSP
				| DEMO);
	}
	private int n;
	private int s1, s2, s3, s4, s5, s6;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new Bar(Square1Color), group[0] + group[1]);
		s2 = enterDisplayElement(new Bar(Square2Color), group[1]);
		s3 = enterDisplayElement(new Bar(Square3Color), group[1]);
		s4 = enterDisplayElement(new Bar(Square4Color), group[1]);
		s5 = enterDisplayElement(new Bar(Square5Color), group[1]);
		s6 = enterDisplayElement(new Cross(CrossColor), group[0] + group[1]);
		return (s2);
	}

	protected void computeGeometry() {
		Rectangle r1 = largeSquare(width, height);
		getDisplayElement(s1).setRect(r1);
		int n = 2;
		double ssize = SquareSize.getDouble();
		int ss = (int) ((ssize * r1.width / n));
		Rectangle[] r = rectPattern(r1, n, n, (r1.width - ss * 2) / 3,
				(r1.width - ss * 2) / 3, (r1.width - ss * 2) / 3,
				(r1.width - ss * 2) / 3);
		for (int i = 0; i < (n * n); i++)
			getDisplayElement(s1 + 1 + i).setRect(r[i]);
		Cross c1 = (Cross) getDisplayElement(s6);
		c1.setRect(new Point(0, 0), new Dimension((int) (r1.width / 16),
				(int) (r1.height / 16)));
	}
}
