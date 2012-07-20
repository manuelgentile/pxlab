package de.pxlab.pxl;

import java.util.*;
import de.pxlab.stat.*;
import de.pxlab.util.*;

import de.pxlab.util.ExpansionIterator;

/**
 * A table of factor level combinations and dependent variables. The factorial
 * data table is a HashMap of double values. The data values are the last
 * entries of each input line with the respective factor level combination being
 * the key for the HashMap.
 * 
 * <p>
 * If a single factor level combination appears more than once in the input data
 * then its data value becomes the average value over all replications.
 * 
 * <p>
 * The constructor of this class tries to fix missing values if they are
 * detected. Missing values in an ExParValueTable are entries where the String
 * and double representation of an entry does not denote the same object. This
 * class replaces missing values at a certain factor level combination by the
 * arithmetic mean of the same combination of non-random factor levels over all
 * available levels of the random factor. Note that the first column of the
 * table always is considered to be the random factor and the last column is
 * considered to be the dependent variable.
 * 
 * @version 0.2.1
 */
/*
 * 
 * 2006/01/13
 * 
 * 2006/01/17 manage missing values
 */
public class FactorialExParValueTable extends FactorialDataTable {
	/**
	 * Create a FactorialExParValueTable from the content of the given data
	 * table which must be of type ExParValueTable.
	 * 
	 * @param data
	 *            the ExParValueTable containing the raw data.
	 */
	public FactorialExParValueTable(ExParValueTable data) {
		super(2 * data.size());
		setFactorNames(data.getColumnNames());
		findFactorLevels(data, false);
		createFactorialDataTable(data);
	}

	/**
	 * Figure out the number of factors and the number of levels for each
	 * factor. This assumes that the last element of each row in the table is a
	 * dependent variable while all other preceding elements are factor levels.
	 * This method sets the global variables nFactors, factorLevelNames[],
	 * nFactorLevels[].
	 * 
	 * @param data
	 *            the data table.
	 * @param allFactors
	 *            if true then all columns of the data table are assumed to be
	 *            factor levels. If false then the last column is assumed to be
	 *            a data value.
	 */
	protected void findFactorLevels(ExParValueTable data, boolean allFactors) {
		// Figure out the number of factors and sources
		Object[] d = (Object[]) (data.get(0));
		nFactors = allFactors ? d.length : d.length - 1;
		// System.out.println("We have " + nFactors + " factors.");
		// Now figure out the factor level names
		ArrayList[] factorLevelNamesAL = new ArrayList[nFactors];
		for (int i = 0; i < nFactors; i++) {
			factorLevelNamesAL[i] = new ArrayList(30);
		}
		for (Iterator it = data.iterator(); it.hasNext();) {
			d = (Object[]) (it.next());
			for (int i = 0; i < nFactors; i++) {
				String s = ((ExParValue) d[i]).getString();
				if (!isContained(s, factorLevelNamesAL[i])) {
					factorLevelNamesAL[i].add(s);
				}
			}
		}
		// Sort the factor level names alphabetically, convert them
		// to String arrays and count the number of factor levels.
		nFactorLevels = new int[nFactors];
		factorLevelNames = new String[nFactors][];
		for (int i = 0; i < nFactors; i++) {
			sort(factorLevelNamesAL[i]);
			factorLevelNames[i] = StringExt
					.stringArrayOfList(factorLevelNamesAL[i]);
			nFactorLevels[i] = factorLevelNamesAL[i].size();
		}
		if (Debug.isActive(Debug.DATA)) {
			System.out
					.print("FactorialExParValueTable.findFactorLevels() Number of factor levels: ");
			for (int i = 0; i < nFactors; i++)
				System.out.print(" " + nFactorLevels[i]);
			System.out.println("");
			printFactorLevelNames(System.out);
		}
	}

	/**
	 * Create the numerical data table. The data table is a HashMap of double
	 * values. The data values are the last entries of each input line with the
	 * respective factor level combination being the key for the HashMap. If a
	 * single factor level combination appears more than once in the data file
	 * then its data value becomes the average value over all replications.
	 */
	private void createFactorialDataTable(ArrayList data) {
		int n = data.size();
		HashMap nReplications = new HashMap(2 * n);
		HashMap ssq = new HashMap(2 * n);
		Object[] d;
		int k = nFactors;
		for (Iterator it = data.iterator(); it.hasNext();) {
			d = (Object[]) (it.next());
			Integer key = keyOf(factorLevelOf(d));
			ExParValue v = (ExParValue) d[k];
			if (v.isNumeric()) {
				double x = v.getDouble();
				if (nReplications.containsKey(key)) {
					double y = x + ((Double) (this.get(key))).doubleValue();
					this.put(key, new Double(y));
					int m = ((Integer) (nReplications.get(key))).intValue() + 1;
					nReplications.put(key, new Integer(m));
					y = x * x + ((Double) (ssq.get(key))).doubleValue();
					ssq.put(key, new Double(y));
				} else {
					this.put(key, new Double(x));
					nReplications.put(key, new Integer(1));
					ssq.put(key, new Double(x * x));
				}
			} else {
				// seems to be a missing value, so add its key to the
				// list of missing values.
				if (missingValue == null) {
					missingValue = new ArrayList(100);
				}
				missingValue.add(key);
			}
		}
		double mv = 0.0;
		int m = 0;
		for (ExpansionIterator ex = new ExpansionIterator(nFactorLevels); ex
				.hasNext();) {
			int[] idx = (int[]) ex.next();
			Integer key = keyOf(idx);
			if (this.containsKey(key)) {
				int p = ((Integer) (nReplications.get(key))).intValue();
				if (p > 1) {
					double s = ((Double) (this.get(key))).doubleValue();
					double x = s / p;
					this.put(key, new Double(x));
					double ss = ((Double) (ssq.get(key))).doubleValue();
					double v = (ss - s * s / p) / p;
					Debug.show(Debug.DATA, factorLevelStringOf(idx) + ": " + x
							+ " [" + v + ", " + p + "]");
					if (v > 0) {
						v = Math.sqrt(v);
					}
					mv += v;
					m++;
				}
				if (hasMissingValues() && missingValue.contains(key)) {
					// The missing value is a missing replication, so
					// ignore it and remove it from the list
					int i;
					while ((i = missingValue.indexOf(key)) >= 0) {
						missingValue.remove(i);
					}
				}
			}
		}
		if (Debug.isActive(Debug.DATA) && (m > 0)) {
			Debug.show(Debug.DATA,
					"Mean standard deviation within replications: " + mv / m);
		}
		if (hasMissingValues()) {
			System.out
					.println("FactorialExParValueTable.createFactorialDataTable(): Missing values: "
							+ missingValue.size());
			fixMissingValues(missingValue);
		}
	}

	/**
	 * Compute an index array for the given data element as it is defined by its
	 * factor levels.
	 */
	protected int[] factorLevelOf(Object[] d) {
		int[] idx = new int[nFactors];
		for (int i = 0; i < nFactors; i++) {
			String s = ((ExParValue) d[i]).getString();
			Object[] fln = (Object[]) (factorLevelNames[i]);
			for (int j = 0; j < nFactorLevels[i]; j++) {
				if (s.equals((String) (fln[j]))) {
					idx[i] = j;
					break;
				}
			}
		}
		/*
		 * System.out.print("Factor level index array of" ); for (int i = 0; i <
		 * nFactors; i++) System.out.print(" " + (String)(d[i]));
		 * System.out.print(":"); for (int i = 0; i < nFactors; i++)
		 * System.out.print(" " + idx[i]); System.out.println("");
		 */
		return idx;
	}
}
