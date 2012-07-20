package de.pxlab.pxl;

import java.util.*;

/**
 * An ArrayList which stores timing values for debugging purposes.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see PresentationManager
 */
public class TimingLog extends ArrayList {
	public TimingLog() {
		super(200);
	}

	/** Add a time value to the timing log array. */
	public boolean add(long t) {
		return super.add(String.valueOf(t));
	}

	/**
	 * Add a display instance name and a time value to the timing log array.
	 */
	public boolean add(String n, long t) {
		return super.add(n + String.valueOf(t));
	}

	/** Print the current entries of the timing log array. */
	public void print() {
		int n = size();
		for (int i = 0; i < n; i++) {
			System.out.println(((String) get(i)));
		}
		System.out.println("");
	}
}
