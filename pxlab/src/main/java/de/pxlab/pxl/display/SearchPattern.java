package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;
import de.pxlab.util.Randomizer;

/**
 * This is a pattern for visual search experiments. The pattern contains
 * distractors and targets. The type 0 pattern is a field of cells which will be
 * selected for positions at random. The type 1 pattern is a circular
 * arrangement.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 
 * 06/13/01
 * 
 * 08/06/03 fixed position bug int circular pattern
 */
public class SearchPattern extends Display {
	/** Distractor item color. */
	public ExPar DistractorColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Distractor Color");
	/** Target item color. */
	public ExPar TargetColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.RED)), "Target Color");
	/** Number of items including target and distractors. */
	public ExPar NumberOfItems = new ExPar(SMALL_INT, new ExParValue(9),
			"Number of Items");
	/** Single item cell square size in pixels. */
	public ExPar CellSize = new ExPar(SCREENSIZE, new ExParValue(50),
			"Item Cell Size");
	/** Item object size. */
	public ExPar Size = new ExPar(SCREENSIZE, new ExParValue(30),
			"Target and Distractor Item Size");
	/**
	 * Total display field width. Note that this parameter determines the number
	 * of columns which is the number of cells which fit into the width.
	 */
	public ExPar Width = new ExPar(HORSCREENSIZE, new ExParValue(400),
			"Field width");
	/** Total display field height. */
	public ExPar Height = new ExPar(VERSCREENSIZE, new ExParValue(400),
			"Field height");
	/**
	 * Flag to indicate that the target item should be shown. If this flag is
	 * false then a distractor item is shown instead of the target item.
	 */
	public ExPar ShowTarget = new ExPar(FLAG, new ExParValue(1),
			"Show target flag");
	/**
	 * Pattern arrangement code. SearchPatternCodes.RECTANGULAR corresponds to
	 * randomly selected positions in a rectangular matrix and
	 * SearchPatternCodes.CIRCULAR corresponds to a circular arrangement.
	 */
	public ExPar Pattern = new ExPar(GEOMETRY_EDITOR, SearchPatternCodes.class,
			new ExParValueConstant("de.pxlab.pxl.SearchPatternCodes.CIRCULAR"),
			"Pattern arrangement");
	/**
	 * Flag to indicate that the item positions should be randomized within
	 * their cells.
	 */
	public ExPar LocalRandomization = new ExPar(FLAG, new ExParValue(1),
			"Randomize positions within a cell");

	public SearchPattern() {
		setTitleAndTopic("Visual Search Pattern", SEARCH_DSP);
	}
	protected Randomizer rnd;
	protected int cellSize;
	protected int fieldWidth;
	protected int fieldHeight;
	protected int horCells;
	protected int verCells;
	protected int nCells;
	protected int nItems;
	protected int[] cellIndex;
	protected int itemSize;
	protected int patternType;
	protected Point[] positions;
	protected boolean localRandom;

	protected int create() {
		rnd = new Randomizer();
		// This is only a dummy to survive the timing element check
		enterDisplayElement(new Bar(TargetColor), group[0]);
		defaultTiming(0);
		return (backgroundFieldIndex);
	}

	protected void computeGeometry() {
		computeCells();
	}

	protected void computeCells() {
		cellSize = CellSize.getInt();
		if (cellSize < 1)
			cellSize = 1;
		fieldWidth = Width.getInt();
		fieldHeight = Height.getInt();
		horCells = fieldWidth / cellSize;
		verCells = fieldHeight / cellSize;
		nCells = horCells * verCells;
		nItems = NumberOfItems.getInt();
		patternType = Pattern.getInt();
		localRandom = LocalRandomization.getFlag();
		if (patternType == 1) {
			computeCircularPattern();
		} else {
			computeRandomPositionPattern();
		}
	}

	protected void computeCircularPattern() {
		if ((positions == null) || (positions.length != nItems)) {
			positions = new Point[nItems];
		}
		nCells = nItems;
		computeRandomPositionPattern();
		double angle = 0.0;
		double angleStep = 2.0 * Math.PI / nItems;
		int d = (cellSize - itemSize) / 2;
		for (int i = 0; i < nItems; i++) {
			positions[i] = new Point((int) (fieldWidth / 2 * Math.cos(angle))
					- d, (int) (fieldHeight / 2 * Math.sin(angle)) - d);
			angle += angleStep;
		}
	}

	protected void computeRandomPositionPattern() {
		if (nItems > nCells) {
			new ParameterValueError("SearchPattern: " + nItems + " for "
					+ nCells + " available cells.");
			nItems = nCells;
		}
		itemSize = Size.getInt();
		if ((cellIndex == null) || (nCells != cellIndex.length)) {
			cellIndex = new int[nCells];
		}
		for (int i = 0; i < nCells; i++)
			cellIndex[i] = i;
		rnd.randomize(cellIndex);
	}

	/**
	 * Return the top left coordinates of the cell with the given index.
	 */
	protected Point positionOfItem(int k) {
		Point p = null;
		int i = cellIndex[k];
		if (patternType == 1) {
			return (positions[i]);
		} else {
			int d = (cellSize - itemSize);
			int rx = d / 2;
			int ry = d / 2;
			if (localRandom && (d > 0)) {
				rx = rnd.nextInt(d);
				ry = rnd.nextInt(d);
			}
			p = new Point((i % horCells) * cellSize - fieldWidth / 2 + rx,
					(i / horCells) * cellSize - fieldHeight / 2 + ry);
		}
		return (p);
	}

	protected void destroy() {
		cellIndex = null;
		rnd = null;
	}

	public void show() {
		boolean showTarget = ShowTarget.getFlag();
		graphics.setColor(DistractorColor.getDevColor());
		for (int i = 0; i < (nItems - 1); i++)
			showDistractorAt(positionOfItem(i));
		if (showTarget) {
			graphics.setColor(TargetColor.getDevColor());
			showTargetAt(positionOfItem(nItems - 1));
		} else {
			showDistractorAt(positionOfItem(nItems - 1));
		}
	}

	/**
	 * We have to override the showStep() method since otherwise the realtime
	 * display does not show anything.
	 */
	public void showGroup() {
		show();
	}

	protected void showDistractorAt(Point p) {
		graphics.fillOval(p.x, p.y, itemSize, itemSize);
	}

	protected void showTargetAt(Point p) {
		graphics.fillOval(p.x, p.y, itemSize, itemSize);
	}
}
