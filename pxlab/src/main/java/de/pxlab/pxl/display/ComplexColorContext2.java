package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A target center field with a surround of up to 8 different subfields. The
 * context contains a small square with up to four subsquares and a larger
 * square with up to four wedges. Thus the context contains a smaller inner
 * section and a larger surrounding section.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 02/11/03
 */
public class ComplexColorContext2 extends ComplexColorContext {
	public ExPar SmallFieldSize = new ExPar(SCREENSIZE, new ExParValue(250),
			"Inner Context Field Size");

	public ComplexColorContext2() {
		setTitleAndTopic("Complex Color Context 2", COMPLEX_COLOR_MATCHING_DSP);
	}
	protected int smallFieldSize = 0;

	protected void computeGeometry() {
		boolean modified = checkContextFields();
		double c = Contrast.getDouble();
		if (c != contrast) {
			contrast = c;
			computeColors();
		}
		int x = LocationX.getInt();
		int y = LocationY.getInt();
		int s1 = FieldSize.getInt() / 2;
		int s2 = SmallFieldSize.getInt() / 2;
		int t = TargetSize.getInt();
		if (modified || (x != locX) || (y != locY) || (s1 != fieldSize)
				|| (s2 != smallFieldSize) || (t != targetSize)) {
			switch (nFields) {
			case 1:
				getDisplayElement(firstField).setRect(x - s1 + 1, y - s1 + 1,
						s1 + s1, s1 + s1);
				break;
			case 2:
				getDisplayElement(firstField).setRect(x - s1 + 1, y - s1 + 1,
						s1 + s1, s1 + s1);
				getDisplayElement(firstField + 1).setRect(x - s2 + 1,
						y - s2 + 1, s2 + s2, s2 + s2);
				break;
			case 3:
				getDisplayElement(firstField).setRect(x - s1 + 1, y - s1 + 1,
						s1 + s1, s1 + s1);
				getDisplayElement(firstField + 1).setRect(x - s2 + 1,
						y - s2 + 1, s2, s2 + s2);
				getDisplayElement(firstField + 2).setRect(x + 1, y - s2 + 1,
						s2, s2 + s2);
				break;
			case 4:
				getDisplayElement(firstField).setRect(x - s1 + 1, y - s1 + 1,
						s1 + s1, s1 + s1);
				getDisplayElement(firstField + 1).setRect(x - s2 + 1,
						y - s2 + 1, s2, s2);
				getDisplayElement(firstField + 2).setRect(x + 1, y - s2 + 1,
						s2, s2);
				getDisplayElement(firstField + 3).setRect(x + 1, y + 1, s2, s2);
				getDisplayElement(firstField + 4).setRect(x - s2 + 1, y + 1,
						s2, s2);
				break;
			case 5:
				getDisplayElement(firstField).setRect(x - s1 + 1, y - s1 + 1,
						s1 + s1, s1 + s1);
				getDisplayElement(firstField + 1).setRect(x - s1 + 1,
						y - s1 + 1, s1 + s1, s1 + s1);
				getDisplayElement(firstField + 2).setRect(x - s2 + 1,
						y - s2 + 1, s2 + s2, s2 + s2);
				break;
			case 6:
				getDisplayElement(firstField).setRect(x - s1 + 1, y - s1 + 1,
						s1 + s1, s1 + s1);
				getDisplayElement(firstField + 1).setRect(x - s1 + 1,
						y - s1 + 1, s1 + s1, s1 + s1);
				getDisplayElement(firstField + 2).setRect(x - s2 + 1,
						y - s2 + 1, s2, s2 + s2);
				getDisplayElement(firstField + 3).setRect(x + 1, y - s2 + 1,
						s2, s2 + s2);
				break;
			case 7:
				getDisplayElement(firstField).setRect(x - s1 + 1, y - s1 + 1,
						s1 + s1, s1 + s1);
				getDisplayElement(firstField + 1).setRect(x - s1 + 1,
						y - s1 + 1, s1 + s1, s1 + s1);
				getDisplayElement(firstField + 2).setRect(x - s2 + 1,
						y - s2 + 1, s2, s2);
				getDisplayElement(firstField + 3).setRect(x + 1, y - s2 + 1,
						s2, s2);
				getDisplayElement(firstField + 4).setRect(x + 1, y + 1, s2, s2);
				getDisplayElement(firstField + 5).setRect(x - s2 + 1, y + 1,
						s2, s2);
				break;
			case 8:
				getDisplayElement(firstField).setRect(x - s1 + 1, y - s1 + 1,
						s1 + s1, s1 + s1);
				getDisplayElement(firstField + 1).setRect(x - s1 + 1,
						y - s1 + 1, s1 + s1, s1 + s1);
				getDisplayElement(firstField + 2).setRect(x - s1 + 1,
						y - s1 + 1, s1 + s1, s1 + s1);
				getDisplayElement(firstField + 3).setRect(x - s1 + 1,
						y - s1 + 1, s1 + s1, s1 + s1);
				getDisplayElement(firstField + 4).setRect(x - s2 + 1,
						y - s2 + 1, s2 + s2, s2 + s2);
				break;
			case 9:
				getDisplayElement(firstField).setRect(x - s1 + 1, y - s1 + 1,
						s1 + s1, s1 + s1);
				getDisplayElement(firstField + 1).setRect(x - s1 + 1,
						y - s1 + 1, s1 + s1, s1 + s1);
				getDisplayElement(firstField + 2).setRect(x - s1 + 1,
						y - s1 + 1, s1 + s1, s1 + s1);
				getDisplayElement(firstField + 3).setRect(x - s1 + 1,
						y - s1 + 1, s1 + s1, s1 + s1);
				getDisplayElement(firstField + 4).setRect(x - s2 + 1,
						y - s2 + 1, s2, s2 + s2);
				getDisplayElement(firstField + 5).setRect(x + 1, y - s2 + 1,
						s2, s2 + s2);
				break;
			case 10:
			default:
				getDisplayElement(firstField).setRect(x - s1 + 1, y - s1 + 1,
						s1 + s1, s1 + s1);
				getDisplayElement(firstField + 1).setRect(x - s1 + 1,
						y - s1 + 1, s1 + s1, s1 + s1);
				getDisplayElement(firstField + 2).setRect(x - s1 + 1,
						y - s1 + 1, s1 + s1, s1 + s1);
				getDisplayElement(firstField + 3).setRect(x - s1 + 1,
						y - s1 + 1, s1 + s1, s1 + s1);
				getDisplayElement(firstField + 4).setRect(x - s2 + 1,
						y - s2 + 1, s2, s2);
				getDisplayElement(firstField + 5).setRect(x + 1, y - s2 + 1,
						s2, s2);
				getDisplayElement(firstField + 6).setRect(x + 1, y + 1, s2, s2);
				getDisplayElement(firstField + 7).setRect(x - s2 + 1, y + 1,
						s2, s2);
				break;
			}
			getDisplayElement(target).setCenterAndSize(x, y, t, t);
			locX = x;
			locY = y;
			fieldSize = s1;
			smallFieldSize = s2;
			targetSize = t;
		}
	}

	private boolean checkContextFields() {
		int n = NumberOfContextFields.getInt();
		boolean r = false;
		// System.out.println("nFields = " + nFields);
		// System.out.println("      n = " + n);
		if (n != nFields) {
			// number of fields has changed
			removeDisplayElements(firstField);
			r = true;
			if (n == 1) {
				enterDisplayElement(new Bar(colorTable[0]), group[0]);
				nFields = 1;
			} else if (n == 2) {
				enterDisplayElement(new Bar(colorTable[0]), group[0]);
				enterDisplayElement(new Bar(colorTable[1]), group[0]);
				nFields = 2;
			} else if (n == 3) {
				enterDisplayElement(new Bar(colorTable[0]), group[0]);
				enterDisplayElement(new Bar(colorTable[1]), group[0]);
				enterDisplayElement(new Bar(colorTable[2]), group[0]);
				nFields = 3;
			} else if (n == 4) {
				enterDisplayElement(new Bar(colorTable[0]), group[0]);
				enterDisplayElement(new Bar(colorTable[1]), group[0]);
				enterDisplayElement(new Bar(colorTable[2]), group[0]);
				enterDisplayElement(new Bar(colorTable[3]), group[0]);
				enterDisplayElement(new Bar(colorTable[4]), group[0]);
				nFields = 4;
			} else if (n == 5) {
				enterDisplayElement(new Wedge(colorTable[0], Wedge.TOPLEFT),
						group[0]);
				enterDisplayElement(
						new Wedge(colorTable[1], Wedge.BOTTOMRIGHT), group[0]);
				enterDisplayElement(new Bar(colorTable[2]), group[0]);
				nFields = 5;
			} else if (n == 6) {
				enterDisplayElement(new Wedge(colorTable[0], Wedge.TOPLEFT),
						group[0]);
				enterDisplayElement(
						new Wedge(colorTable[1], Wedge.BOTTOMRIGHT), group[0]);
				enterDisplayElement(new Bar(colorTable[2]), group[0]);
				enterDisplayElement(new Bar(colorTable[3]), group[0]);
				nFields = 6;
			} else if (n == 7) {
				enterDisplayElement(new Wedge(colorTable[0], Wedge.TOPLEFT),
						group[0]);
				enterDisplayElement(
						new Wedge(colorTable[1], Wedge.BOTTOMRIGHT), group[0]);
				enterDisplayElement(new Bar(colorTable[2]), group[0]);
				enterDisplayElement(new Bar(colorTable[3]), group[0]);
				enterDisplayElement(new Bar(colorTable[4]), group[0]);
				enterDisplayElement(new Bar(colorTable[5]), group[0]);
				nFields = 7;
			} else if (n == 8) {
				enterDisplayElement(new Wedge(colorTable[0], Wedge.TOP),
						group[0]);
				enterDisplayElement(new Wedge(colorTable[1], Wedge.RIGHT),
						group[0]);
				enterDisplayElement(new Wedge(colorTable[2], Wedge.BOTTOM),
						group[0]);
				enterDisplayElement(new Wedge(colorTable[3], Wedge.LEFT),
						group[0]);
				enterDisplayElement(new Bar(colorTable[4]), group[0]);
				nFields = 8;
			} else if (n == 9) {
				enterDisplayElement(new Wedge(colorTable[0], Wedge.TOP),
						group[0]);
				enterDisplayElement(new Wedge(colorTable[1], Wedge.RIGHT),
						group[0]);
				enterDisplayElement(new Wedge(colorTable[2], Wedge.BOTTOM),
						group[0]);
				enterDisplayElement(new Wedge(colorTable[3], Wedge.LEFT),
						group[0]);
				enterDisplayElement(new Bar(colorTable[4]), group[0]);
				enterDisplayElement(new Bar(colorTable[5]), group[0]);
				nFields = 9;
			} else /* if (n == 10) */{
				enterDisplayElement(new Wedge(colorTable[0], Wedge.TOP),
						group[0]);
				enterDisplayElement(new Wedge(colorTable[1], Wedge.RIGHT),
						group[0]);
				enterDisplayElement(new Wedge(colorTable[2], Wedge.BOTTOM),
						group[0]);
				enterDisplayElement(new Wedge(colorTable[3], Wedge.LEFT),
						group[0]);
				enterDisplayElement(new Bar(colorTable[4]), group[0]);
				enterDisplayElement(new Bar(colorTable[5]), group[0]);
				enterDisplayElement(new Bar(colorTable[6]), group[0]);
				enterDisplayElement(new Bar(colorTable[7]), group[0]);
				nFields = 10;
			}
			target = enterDisplayElement(new Oval(TargetColor), group[0]);
		}
		return r;
	}
}
