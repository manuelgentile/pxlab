package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A list of text items which may be ranked by the subject. The text items are
 * contained in a paragraph shown on the left side of the screen. The subject's
 * ranking is shown on the right side of the screen. Clicking on an item moves
 * it to the opposite side. A special OK field is provided for finishing.
 * 
 * <p>
 * Every ranking decision is considered to be a response and every ranking
 * response sets the global experimental parameter TrialState to COPY. This
 * creates another Trial with the same parameters such that the list of trials
 * is automatically extended. The ranking state is preserved across trials only
 * as long as the item list is identical.
 * 
 * <p>
 * OK field responses do not set the global parameter TrialState but leave its
 * original value EXECUTE. Thus no more Trial is created after an OK field
 * response. In this case the next Trial defined in the Block is executed or the
 * Block is finished if its Trial list is exhausted.
 * 
 * <p>
 * The Timer parameter must contain the MOUSE_TRACKING_TIMER bits. If it also
 * contains the RELEASE_RESPONSE_TIMER bits then an ordinary keyboard key may be
 * used to finish ranking since these also leave the TrialState at the EXECUTE
 * value.
 * 
 * @version 0.2.0
 */
/*
 * 
 * 09/14/01
 * 
 * 2007/06/05 Fixed some bugs and extended the appearance parameters
 */
public class ItemRanking extends FontDisplay {
	private String[] itlst = { "Pferd", "Maus", "Hund" };
	/** List of items to be ranked. */
	public ExPar Items = new ExPar(STRING, new ExParValue(itlst), "Item list");
	/** The ranks of the items in Item created by the subject's ordering. */
	public ExPar Ranks = new ExPar(RTDATA, new ExParValue(0), "Item Ranks");
	/** A place holder string for empty positions. */
	public ExPar PlaceHolder = new ExPar(STRING, new ExParValue("-"),
			"Empty position place holder");
	/** Horizontal center position. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal center location");
	/** Vertical center position. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical center");
	/** Width of the horizontal gap between the two list paragraphs. */
	public ExPar HorizontalGap = new ExPar(HORSCREENSIZE, new ExParValue(50),
			"Width of horizontal gap between lists");
	/** Base line skip factor. */
	public ExPar LineSkipFactor = new ExPar(1.0, 5.0, new ExParValue(1.0),
			"Base line skip factor");
	/**
	 * Text alignment within a list. Should be an array in order to control the
	 * left and right list independently.
	 */
	public ExPar Alignment = new ExPar(GEOMETRY_EDITOR, AlignmentCodes.class,
			new ExParValueConstant("de.pxlab.pxl.AlignmentCodes.LEFT"),
			"Text alignment within a list");
	// --------------------------------------------------------------------
	// ScreenButton implementation
	// --------------------------------------------------------------------
	/**
	 * Size of the screen button for stopping mouse tracking response intervals.
	 * If this is 0 then no screen button is shown. If this is a single non-zero
	 * number then it is the screen button's height. If this parameter contains
	 * two numbers then these are used as the screen button's width and height
	 * respectively.
	 */
	public ExPar ScreenButtonSize = new ExPar(HORSCREENSIZE, new ExParValue(0),
			"Screen button size");
	/**
	 * Position of the screen button for stopping mouse tracking response
	 * intervals. If this is a single number then it is a position code as
	 * defined by class PositionReferenceCodes. If this parameter contains two
	 * numbers then they will be used as the (x,y) position of the screen button
	 * center.
	 * 
	 * @see de.pxlab.pxl.PositionReferenceCodes
	 */
	public ExPar ScreenButtonPosition = new ExPar(GEOMETRY_EDITOR,
			PositionReferenceCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.PositionReferenceCodes.BASE_RIGHT"),
			"Screen button position");
	/**
	 * Text on the screen button for stopping mouse tracking response intervals.
	 */
	public ExPar ScreenButtonText = new ExPar(STRING, new ExParValue("OK"),
			"Screen button text");
	/**
	 * Color of the screen button area for stopping mouse tracking response
	 * intervals.
	 */
	public ExPar ScreenButtonColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Screen button color");
	/**
	 * Color of the text label on the screen button for stopping mouse tracking
	 * response intervals.
	 */
	public ExPar ScreenButtonTextColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.DARK_GRAY)),
			"Screen button text color");

	// --------------------------------------------------------------------
	// End of ScreenButton implementation
	// --------------------------------------------------------------------
	public ItemRanking() {
		setTitleAndTopic("Item Ranking", QUESTIONNAIRE_DSP | EXP);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.MOUSE_TRACKING_TIMER"));
	}
	protected int leftList, rightList;
	protected String id = "";
	protected ScreenButtonElement screenButton;

	/** Initialize the display list of the demo. */
	protected int create() {
		DisplayElement.setValidBounds(true);
		leftList = enterDisplayElement(new TextParagraphElement(this.Color),
				group[0]);
		rightList = enterDisplayElement(new TextParagraphElement(this.Color),
				group[0]);
		screenButton = new ScreenButtonElement(ScreenButtonColor,
				ScreenButtonTextColor);
		enterDisplayElement(screenButton, group[0]);
		defaultTiming(0);
		Ranks.set(0);
		return (leftList);
	}
	private int n;
	private int[] ranks;

	protected void computeGeometry() {
		// System.out.println("ItemRanking.computeGeometry()");
		// System.out.println("  Items = " + Items);
		// System.out.println("  Ranks = " + Ranks);
		String[] items = Items.getStringArray();
		// reset ranks if we get a new item list
		String ids = idString(items);
		if (!ids.equals(id)) {
			ranks = null;
			id = ids;
		}
		n = items.length;
		// ranks = Ranks.getIntArray();
		if ((ranks == null) || (ranks.length != n)) {
			// System.out.println("  new ranks of  length " + n);
			ranks = new int[n];
			for (int i = 0; i < n; i++) {
				ranks[i] = 0;
			}
			// Ranks.set(ranks);
		}
		String[] displayItems = new String[n];
		String[] rankedItems = new String[n];
		String ph = PlaceHolder.getString();
		for (int i = 0; i < n; i++)
			rankedItems[i] = ph;
		// for (int i = 0; i < n; i++) System.out.println("-ranks[" + i + "] = "
		// + ranks[i]);
		for (int i = 0; i < n; i++) {
			// System.out.println("ranks[" + i + "] = " + ranks[i]);
			if (ranks[i] == 0) {
				// Item i not yet ranked
				displayItems[i] = items[i];
			} else {
				displayItems[i] = ph;
				rankedItems[ranks[i] - 1] = items[i];
			}
		}
		int gap2 = HorizontalGap.getInt() / 2;
		int[] a = Alignment.getIntArray();
		int a1 = a[0];
		int a2 = (a.length > 1) ? a[1] : a[0];
		((TextParagraphElement) getDisplayElement(leftList)).setProperties(
				displayItems, FontFamily.getString(), FontStyle.getInt(),
				FontSize.getInt(), -gap2 + LocationX.getInt(),
				LocationY.getInt(), width / 2,
				PositionReferenceCodes.MIDDLE_RIGHT, a1, false, false,
				LineSkipFactor.getDouble());
		((TextParagraphElement) getDisplayElement(rightList)).setProperties(
				rankedItems, FontFamily.getString(), FontStyle.getInt(),
				FontSize.getInt(), gap2 + LocationX.getInt(),
				LocationY.getInt(), width / 2,
				PositionReferenceCodes.MIDDLE_LEFT, a2, false, false,
				LineSkipFactor.getDouble());
		setScreenButton(screenButton.setProperties(
				ScreenButtonPosition.getIntArray(),
				ScreenButtonSize.getIntArray(), ScreenButtonText.getString()));
	}

	/**
	 * Signal that the pointer has been released. This method is called by the
	 * ResponseTimer if the USE_MOUSE_ACTIVE_TRACKING_TIMER bit is activated.
	 */
	protected boolean pointerReleased() {
		// System.out.println("ItemRanking.pointerReleased() at " +
		// pointerReleaseX + " " + pointerReleaseY);
		Rectangle lr = getDisplayElement(leftList).getBounds();
		Rectangle rr = getDisplayElement(rightList).getBounds();
		int h = lr.height / n;
		int index = (pointerReleaseY - lr.y) / h;
		// for (int i = 0; i < n; i++) System.out.println("ranks[" + i + "] = "
		// + ranks[i]);
		if ((index >= 0) && (index < n)) {
			if (lr.contains(pointerReleaseX, pointerReleaseY)) {
				// left column: put the selected item into the ranking
				// list at the next free position
				// System.out.println("ItemRanking.pointerReleased(): Item " +
				// index + " left Column");
				if (ranks[index] == 0) {
					// This item has not yet been ranked
					// System.out.println("ItemRanking.pointerReleased(): not yet ranked");
					ranks[index] = firstFreeRank();
					// System.out.println("ItemRanking.pointerReleased(): new Rank: "
					// + ranks[index]);
				}
			} else if (rr.contains(pointerReleaseX, pointerReleaseY)) {
				// right column: remove that item from the list and shift
				// the remaining ones up
				index++;
				// System.out.println("ItemRanking.pointerReleased(): Rank " +
				// index + " right Column");
				int item = itemAtRank(index);
				ranks[item] = 0;
				for (int i = 0; i < n; i++) {
					if (ranks[i] > index) {
						ranks[i]--;
					}
				}
			}
			// for (int i = 0; i < n; i++) System.out.println("->ranks[" + i +
			// "] = " + ranks[i]);
			Ranks.set(ranks);
			// System.out.println("  Ranks (after response) = " + Ranks);
		}
		ExPar.BlockState.set(StateCodes.EXECUTE);
		ExPar.TrialState.set(StateCodes.COPY);
		return true;
	}

	protected void screenButtonReleased() {
		ExPar.TrialState.set(StateCodes.EXECUTE);
		Ranks.set(-1);
		ranks = null;
	}

	/**
	 * Search for the highest entry in rank[] and return this value incremented
	 * by 1.
	 */
	private int firstFreeRank() {
		int r = 0;
		for (int i = 0; i < n; i++) {
			if (ranks[i] > r) {
				r = ranks[i];
			}
		}
		return (r + 1);
	}

	/** Search for the item which has the given rank. */
	private int itemAtRank(int r) {
		for (int i = 0; i < n; i++) {
			if (ranks[i] == r) {
				return i;
			}
		}
		return (0);
	}

	private String idString(String[] a) {
		StringBuilder sb = new StringBuilder(200);
		for (int i = 0; i < a.length; i++)
			sb.append(a[i]);
		return sb.toString();
	}
}
