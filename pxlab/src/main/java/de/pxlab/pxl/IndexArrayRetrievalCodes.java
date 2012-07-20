package de.pxlab.pxl;

public interface IndexArrayRetrievalCodes {
	/** Gets the next index in the sequence of the initial item array. */
	public static final int INDEX_RETRIEVAL_FIXED = 1;
	/**
	 * Gets the next index in a randomized sequence of the initial item array.
	 * This works like random drawing without replacement.
	 */
	public static final int INDEX_RETRIEVAL_RANDOM = 2;
}
