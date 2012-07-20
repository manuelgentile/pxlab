package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Shows multiple paragraphs of text. The paragraphs' text content and positions
 * are independent and my be defined by setting the respective parameters to
 * array valued objects.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class TextParagraphMultiple extends TextParagraph {
	public TextParagraphMultiple() {
		setTitleAndTopic("Multiple Text Paragraph", TEXT_PAR_DSP);
		String[] p = { "Paragraphs of Text presented by PXLab",
				"And multiples of these!" };
		Text.set(p);
		Width.set(150);
		Wrapping.set(1);
		LocationX.getValue().set(new ExParValue(-20, 20));
		ExParValue[] a = new ExParValue[2];
		a[0] = new ExParValueConstant(
				"de.pxlab.pxl.PositionReferenceCodes.TOP_RIGHT");
		a[1] = new ExParValueConstant(
				"de.pxlab.pxl.PositionReferenceCodes.TOP_LEFT");
		ReferencePoint.getValue().set(ExParValue.constantExParValue(a));
		a = new ExParValue[2];
		a[0] = new ExParValueConstant("de.pxlab.pxl.AlignmentCodes.RIGHT");
		a[1] = new ExParValueConstant("de.pxlab.pxl.AlignmentCodes.LEFT");
		Alignment.getValue().set(ExParValue.constantExParValue(a));
	}
	protected int firstDisplayElement;
	protected int n;

	protected int create() {
		firstDisplayElement = enterDisplayElement(new TextParagraphElement(
				this.Color), group[0]);
		n = 1;
		defaultTiming(0);
		return (firstDisplayElement);
	}

	protected void computeGeometry() {
		String[] txt = Text.getStringArray();
		int nn = txt.length;
		// Check whether the number of text elements has changed
		if (nn != n) {
			removeDisplayElements(firstDisplayElement);
			for (int i = 0; i < nn; i++) {
				enterDisplayElement(new TextParagraphElement(this.Color),
						group[0]);
			}
			n = nn;
		}
		double w = Width.getDouble();
		int ww = (w < 1.0) ? (int) (width * w) : (int) w;
		int[] x = LocationX.getIntArray();
		int[] y = LocationY.getIntArray();
		int[] r = ReferencePoint.getIntArray();
		int[] a = Alignment.getIntArray();
		for (int i = 0; i < n; i++) {
			TextParagraphElement te = (TextParagraphElement) getDisplayElement(firstDisplayElement
					+ i);
			String[] txt2 = new String[1];
			txt2[0] = txt[i];
			te.setProperties(txt2, FontFamily.getString(), FontStyle.getInt(),
					FontSize.getInt(),
					(i >= x.length) ? x[x.length - 1] : x[i],
					(i >= y.length) ? y[y.length - 1] : y[i], ww,
					(i >= r.length) ? r[r.length - 1] : r[i],
					(i >= a.length) ? a[a.length - 1] : a[i],
					EmphasizeFirstLine.getFlag(), Wrapping.getFlag(),
					LineSkipFactor.getDouble());
		}
	}
}
