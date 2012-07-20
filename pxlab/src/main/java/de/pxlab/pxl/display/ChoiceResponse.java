package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * A response mechanism for multiple choice responses. This display defines a
 * set of labels describing response alternatives. The subject's task is to
 * select one of the labels. This may be done by using the mouse pointer or by
 * pressing an appropriate response key (keys are not yet implemented).
 * 
 * @version 0.3.0
 */
/*
 * 
 * 07/19/02
 * 
 * 2005/09/08 added vertical arrangement
 * 
 * 2007/06/18 disable finishing the timing group int an unselected state.
 */
public class ChoiceResponse extends Display {
	public ExPar TextColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)), "Text Color");
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.GRAY)), "Field Color");
	public ExPar Width = new ExPar(HORSCREENSIZE, new ExParValue(360),
			"Width of Response Area");
	public ExPar Height = new ExPar(VERSCREENSIZE, new ExParValue(100),
			"Height of Response Area");
	public ExPar GapSize = new ExPar(PROPORT, new ExParValue(0.1),
			"Relative Gap Between Labels");
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal Center Position");
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical Center Position");
	public ExPar Horizontal = new ExPar(FLAG, new ExParValue(1),
			"Horizontal/vertical orientation flag");
	String[] s = { "Yes", "No" };
	/** A string array of choice alternatives. */
	public ExPar Choices = new ExPar(STRING, new ExParValue(s),
			"Choice Alternatives");
	/** Selected item index. */
	public ExPar Selection = new ExPar(RTDATA, new ExParValue(0),
			"Selected choice alternative");
	/**
	 * If true then stopping this display is only allowed if at least one
	 * alternative is selected.
	 */
	public ExPar DisableNonSelection = new ExPar(FLAG, new ExParValue(1),
			"Disable stopping without selection");

	public ChoiceResponse() {
		setTitleAndTopic("Choice Response", QUESTIONNAIRE_DSP);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.MOUSE_TRACKING_TIMER"));
	}
	protected int label_idx;
	protected int count;

	protected int create() {
		label_idx = enterDisplayElement(new LabelElement(TextColor, Color),
				group[0]);
		enterDisplayElement(new LabelElement(TextColor, Color), group[0]);
		count = 2;
		defaultTiming(0);
		return (label_idx);
	}

	protected void computeGeometry() {
		String[] t = Choices.getStringArray();
		int n = t.length;
		if (n != count) {
			// Update display element list
			removeDisplayElements(label_idx);
			for (int i = 0; i < n; i++) {
				enterDisplayElement(new LabelElement(TextColor, Color),
						group[0]);
			}
			count = n;
		}
		boolean hor = Horizontal.getFlag();
		int w = Width.getInt();
		int h = Height.getInt();
		int gap100 = (int) (100.0 * GapSize.getDouble());
		int d = 0, dg = 0;
		// Single label width
		if (hor) {
			d = (int) ((100L * w) / (100L * n + gap100 * (n - 1)));
			dg = (int) ((d * (100L + gap100)) / 100L);
			w = (int) (d * (100L * n + gap100 * (n - 1)) / 100L);
		} else {
			d = (int) ((100L * h) / (100L * n + gap100 * (n - 1)));
			dg = (int) ((d * (100L + gap100)) / 100L);
			h = (int) (d * (100L * n + gap100 * (n - 1)) / 100L);
		}
		int x = -w / 2 + LocationX.getInt();
		int y = -h / 2 + LocationY.getInt();
		LabelElement a;
		for (int i = 0; i < count; i++) {
			a = (LabelElement) getDisplayElement(label_idx + i);
			a.setText(t[i]);
			if (hor) {
				a.setRect(x, y, d, h);
				x += dg;
			} else {
				a.setRect(x, y, w, d);
				y += dg;
			}
			// System.out.println("ChoiceResponse.computeGeometry() Label at = "
			// + a.getLocation());
		}
		Selection.set(-1);
	}

	/**
	 * A selection is done by releasing the mouse pointer over one of the label
	 * bars.
	 */
	protected boolean pointerReleased() {
		int s = -1;
		for (int i = 0; i < count; i++) {
			if (getDisplayElement(label_idx + i).contains(pointerReleaseX,
					pointerReleaseY)) {
				s = i;
				break;
			}
		}
		Selection.set(s);
		return false;
	}

	/**
	 * Allow timer stops only if an elemet is currently selected.
	 * 
	 * @return true if currently at least one picture is being selected.
	 */
	public boolean getAllowTimerStop(int rc) {
		if (DisableNonSelection.getFlag()) {
			return Selection.getInt() >= 0;
		} else {
			return true;
		}
	}
}
