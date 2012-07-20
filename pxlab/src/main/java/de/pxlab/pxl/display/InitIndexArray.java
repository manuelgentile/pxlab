package de.pxlab.pxl.display;

import de.pxlab.util.Randomizer;
import de.pxlab.pxl.*;

/**
 * Create and initialize a index array. This provides an array of integer
 * values. These values may be used as indices into any other array. Sequential
 * retrieval is done with SetNextIndex. Retrieval may be done strictly
 * sequential in the fixed initial order or it may be done randomly in order to
 * simulate retrieval without putting the element back.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2008/04/15
 */
public class InitIndexArray extends Display {
	protected static final String NEXT_INDEX = "_next_index";
	/** The name of the index array to be used. */
	public ExPar Name = new ExPar(STRING, new ExParValue("IndexArray1"),
			"Name of the index array");
	/**
	 * The initial index array. This is only needed if you want to use an
	 * arbitrary array of index values.
	 */
	public ExPar InitialArray = new ExPar(INTEGER, new ExParValue(-1),
			"Initial index array");
	/** First index to use. Only used if InitialArray is not set. */
	public ExPar FirstIndex = new ExPar(INTEGER, new ExParValue(0),
			"First index to use");
	/**
	 * Number of entries in the index array. Only used if InitialArray is not
	 * set.
	 */
	public ExPar ArrayLength = new ExPar(INTEGER, new ExParValue(0),
			"Use this number of elements");
	/** This is how the items are retrieved from the array. */
	public ExPar RetrievalType = new ExPar(
			GEOMETRY_EDITOR,
			IndexArrayRetrievalCodes.class,
			new ExParValueConstant(
					"de.pxlab.pxl.IndexArrayRetrievalCodes.INDEX_RETRIEVAL_RANDOM"),
			"Index retrieval type");

	public InitIndexArray() {
		setTitleAndTopic("Initialize an item array", PARAM_DSP | EXP);
		setVisible(false);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	protected int create() {
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
		int[] a = InitialArray.getIntArray();
		int n = a.length;
		if (n > 1) {
			// a is the actual index array
		} else {
			n = ArrayLength.getInt();
			int e = FirstIndex.getInt();
			a = new int[n];
			for (int i = 0; i < n; i++)
				a[i] = i + e;
		}
		if (RetrievalType.getInt() == de.pxlab.pxl.IndexArrayRetrievalCodes.INDEX_RETRIEVAL_RANDOM) {
			Randomizer r = new Randomizer();
			r.randomize(a);
		}
		RuntimeRegistry.put(Name.getString(), a);
		RuntimeRegistry.put(Name.getString() + NEXT_INDEX, new Integer(0));
	}
}
