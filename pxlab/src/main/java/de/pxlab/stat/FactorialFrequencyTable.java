package de.pxlab.stat;

import java.io.*;
import java.util.*;

import de.pxlab.util.ExpansionIterator;

/**
 * A table of factor level combinations and their frequencies. The factorial
 * frequency table is a HashMap. The data values are the numbers of cases for
 * every factor level combination. Usually the n columns of a data file will
 * contain the true factor levels in the first n-1 columns and the categorical
 * choice in the last column. As an example the last column may contain one of
 * three alternatives A, B, C which have been chosen in a trial for some factor
 * level combination. Then the frequency table will contain the number of cases
 * where any alternative A, B, C has been chosen as its data. Note that summing
 * over all 'factor' levels of the last factor will result in the total number
 * of choices.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2006/01/11
 */
public class FactorialFrequencyTable extends FactorialDataTable {
	/**
	 * Create a FactorialDataTable from the content of the given
	 * StringDataTable.
	 * 
	 * @param data
	 *            the StringDataTable containing the raw data.
	 */
	public FactorialFrequencyTable(StringDataTable data) {
		super(2 * data.size());
		String[] n = data.getColumnNames();
		String[] m = new String[n.length + 1];
		for (int i = 0; i < n.length; i++)
			m[i] = n[i];
		m[n.length] = "N";
		setFactorNames(m);
		findFactorLevels(data, true);
		createFactorialFrequencyTable(data);
	}

	/**
	 * Create the numerical data table. The data table is a HashMap of double
	 * values. The data values are the last entries of each input line with the
	 * respective factor level combination being the key for the HashMap. If a
	 * single factor level combination appears more than once in the data file
	 * then its data value becomes the average value over all replications.
	 */
	private void createFactorialFrequencyTable(StringDataTable data) {
		Object[] d;
		for (Iterator it = data.iterator(); it.hasNext();) {
			d = (Object[]) (it.next());
			Integer key = keyOf(factorLevelOf(d));
			if (this.containsKey(key)) {
				int y = 1 + ((Integer) (this.get(key))).intValue();
				this.put(key, new Integer(y));
			} else {
				this.put(key, new Integer(1));
			}
		}
	}

	/**
	 * Get the string value of a table entry.
	 * 
	 * @param key
	 *            the key to access the table entry.
	 * @return a string value.
	 */
	public String stringValueFor(Object key) {
		return ((Integer) get(key)).toString();
	}

	/**
	 * Get the double value of a table entry.
	 * 
	 * @param key
	 *            the key to access the table entry.
	 * @return a double value.
	 */
	public double doubleValueFor(Object key) {
		return (double) (((Integer) get(key)).intValue());
	}

	/**
	 * Get an integer value of a table entry.
	 * 
	 * @param key
	 *            the key to access the table entry.
	 * @return an integer value.
	 */
	public int intValueFor(Object key) {
		return ((Integer) get(key)).intValue();
	}
}
