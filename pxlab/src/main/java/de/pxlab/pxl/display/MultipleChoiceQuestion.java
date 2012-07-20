package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * This is a single question followed by a list of (multiple) choice
 * alternatives.
 * 
 * @version 0.1.2
 */
/*
 * 
 * 2006/01/16 added DisableNonSelection to disable the timer as long as no
 * alternative is selected.
 * 
 * 2007/04/23 removed ResponseSet initialization. This is forbidden!
 */
public class MultipleChoiceQuestion extends TextParagraph {
	private String[] p = { "Can't answer such a complicated question", "Sex",
			"Science", "Something else" };
	/** A string array of choice alternatives. */
	public ExPar Choices = new ExPar(STRING, new ExParValue(p),
			"Response alternatives");
	/** Selected item highlight color. */
	public ExPar SelectionColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GREEN)), "Selected item color");
	/** List of selected items. */
	public ExPar Selection = new ExPar(RTDATA, new ExParValue(-1),
			"Selected item(s)");
	/** Vertical gap between paragraphs. */
	public ExPar ParSkip = new ExPar(VERSCREENSIZE, new ExParValue(60),
			"Vertical space between paragraphs");
	/** Choice alternatives indentation */
	public ExPar Indent = new ExPar(HORSCREENSIZE, new ExParValue(60),
			"Choice alternatives indentation");
	/** Choice alternatives bullets size. */
	public ExPar BulletSize = new ExPar(SCREENSIZE, new ExParValue(28),
			"Choice bullet size");
	/** Flag to indicate that only a single alternative may be selected */
	public ExPar Unique = new ExPar(FLAG, new ExParValue(1),
			"Flag to allow only single item selection");
	/**
	 * If true then stopping this display is only allowed if at least one
	 * alternative is selected.
	 */
	public ExPar DisableNonSelection = new ExPar(FLAG, new ExParValue(1),
			"Disable stopping without selection");

	public MultipleChoiceQuestion() {
		setTitleAndTopic("Question with Multiple Choice Response",
				QUESTIONNAIRE_DSP);
		Text.set("What is the meaning of life?");
		FontSize.set(36);
		Width.set(500);
		LocationX.set(-250);
		LocationY.set(-200);
		Alignment.set(AlignmentCodes.LEFT);
		ReferencePoint.set(PositionReferenceCodes.BASE_LEFT);
		Wrapping.set(1);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.MOUSE_TRACKING_TIMER"));
		// ResponseSet.set(32);
	}
	private int question;
	private int firstChoiceIdx;
	private int nc;
	private int[] choiceIdx = null;
	private boolean[] selection = null;

	/** Initialize the display list of the demo. */
	protected int create() {
		question = enterDisplayElement(new TextParagraphElement(this.Color),
				group[0]);
		firstChoiceIdx = nextDisplayElementIndex();
		defaultTiming(0);
		choiceIdx = null;
		selection = null;
		return (question);
	}
	int parSkip;
	boolean uniq;

	protected void computeGeometry() {
		uniq = Unique.getFlag();
		int indent = Indent.getInt();
		parSkip = ParSkip.getInt();
		int bulletSize = BulletSize.getInt();
		double lineSkip = LineSkipFactor.getDouble();
		int x = LocationX.getInt();
		int y = LocationY.getInt();
		int w = Width.getInt();
		int xb = x + indent - 3 * bulletSize / 2;
		TextParagraphElement qt = (TextParagraphElement) getDisplayElement(question);
		qt.setText(Text.getString());
		qt.setFont(FontFamily.getString(), FontStyle.getInt(),
				FontSize.getInt());
		qt.setLocation(x, y);
		qt.setSize(w, 1);
		qt.setAlignment(AlignmentCodes.LEFT);
		qt.setReferencePoint(PositionReferenceCodes.TOP_LEFT);
		qt.setWrapLines(true);
		qt.setLineSkipFactor(lineSkip);
		x += indent;
		w -= indent;
		y += parSkip;
		String[] choices = Choices.getStringArray();
		nc = choices.length;
		if (choiceIdx != null) {
			removeDisplayElements(firstChoiceIdx, nextDisplayElementIndex() - 1);
		}
		choiceIdx = new int[nc];
		selection = new boolean[nc];
		for (int i = 0; i < nc; i++)
			selection[i] = false;
		TextParagraphElement t;
		for (int i = 0; i < nc; i++) {
			t = new TextParagraphElement(this.Color);
			String[] chc = new String[1];
			chc[0] = choices[i];
			t.setProperties(chc, FontFamily.getString(), FontStyle.getInt(),
					FontSize.getInt(), x, y, w,
					PositionReferenceCodes.TOP_LEFT, AlignmentCodes.LEFT,
					false, true, lineSkip);
			t.setStrictParagraphMode(true);
			choiceIdx[i] = enterDisplayElement(t, group[0]);
			// System.out.println("MultipleChoiceQuestion.computeGeometry() entered element "
			// + choiceIdx[i]);
			enterDisplayElement(new Bar(selection[i] ? SelectionColor
					: this.Color, xb, y - bulletSize, bulletSize, bulletSize),
					group[0]);
			y += parSkip;
		}
	}

	/** Ignore timing steps since we have only a single group. */
	public void showGroup(Graphics g) {
		show(g);
	}

	/**
	 * We need to override the superclass's show() method in order to shift all
	 * the paragraphs down according to the previous paragraph's height.
	 */
	public void show(Graphics g) {
		// System.out.println("MultipleChoiceQuestion.show(Graphics())");
		setGraphicsContext(g);
		for (int i = 0; i < firstChoiceIdx; i++) {
			getDisplayElement(i).show();
		}
		Rectangle b = getDisplayElement(question).getBounds();
		int y = b.y + b.height + parSkip;
		for (int i = 0; i < nc; i++) {
			// System.out.println("y=" + y);
			DisplayElement de1 = getDisplayElement(choiceIdx[i]);
			DisplayElement de2 = getDisplayElement(choiceIdx[i] + 1);
			Point p = de1.getLocation();
			int dy = y - p.y;
			de1.setLocation(p.x, p.y + dy);
			de1.show();
			p = de2.getLocation();
			de2.setLocation(p.x, p.y + dy);
			de2.show();
			b = de1.getBounds();
			y = b.y + b.height + parSkip;
		}
	}

	/**
	 * Signal that the pointer has been released. This method is called by the
	 * ResponseTimer if the USE_MOUSE_ACTIVE_TRACKING_TIMER bit is activated.
	 */
	protected boolean pointerReleased() {
		int slct = getSelection(pointerReleaseX, pointerReleaseY);
		if (slct < 0)
			return false;
		if (slct == getSelection(pointerActivationX, pointerActivationY)) {
			setSelection(slct);
		}
		return true;
	}

	/**
	 * Find the item which contains the given coordinate in its bounding box if
	 * any.
	 * 
	 * @param x
	 *            x-coordinate of pointer.
	 * @param y
	 *            y-coordinate of pointer.
	 * @return the index of the selected item or (-1) of the given point is not
	 *         within the bounding box of an item.
	 */
	private int getSelection(int x, int y) {
		Point p = new Point(x, y);
		for (int i = 0; i < nc; i++) {
			if ((getDisplayElement(choiceIdx[i]).contains(p))
					|| (getDisplayElement(choiceIdx[i] + 1).contains(p))) {
				return (i);
			}
		}
		return (-1);
	}

	/**
	 * Set the state of the experimental parameter 'Selection' and update the
	 * color of the selected items.
	 */
	private void setSelection(int k) {
		int count = 0;
		for (int i = 0; i < nc; i++) {
			if (i == k) {
				selection[i] = !selection[i];
			} else if (uniq) {
				selection[i] = false;
			}
			DisplayElement de = getDisplayElement(choiceIdx[i] + 1);
			de.setColorPar(selection[i] ? SelectionColor : this.Color);
			if (selection[i])
				count++;
		}
		int[] selections = new int[count];
		int j = 0;
		for (int i = 0; i < nc; i++) {
			if (selection[i])
				selections[j++] = i + 1;
		}
		Selection.set(selections);
	}

	/**
	 * Allow timer stops only if an elemet is currently selected.
	 * 
	 * @return true if currently at least one picture is being selected.
	 */
	public boolean getAllowTimerStop(int rc) {
		if (DisableNonSelection.getFlag()) {
			for (int i = 0; i < nc; i++) {
				if (selection[i]) {
					return true;
				}
			}
			return false;
		} else {
			return true;
		}
	}
}
