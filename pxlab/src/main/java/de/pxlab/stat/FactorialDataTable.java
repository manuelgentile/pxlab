package de.pxlab.stat;

import java.io.*;
import java.util.*;

import de.pxlab.util.*;
import de.pxlab.pxl.Debug;
import de.pxlab.pxl.NonFatalError;

/**
 * A table of factor level combinations and a single dependent variable. The
 * factorial data table is a HashMap of double values. The data values are the
 * last entries of each input line with the respective factor level combination
 * being the key for the HashMap. If a single factor level combination appears
 * more than once in the input data then its data value becomes the average
 * value over all replications.
 * 
 * @version 0.2.2
 */
/*
 * 
 * 2006/01/13
 * 
 * 2006/01/17 manage missing values
 * 
 * 2006/03/22 use numeric sort of factor level names if possible
 */
public class FactorialDataTable extends HashMap implements Comparator {
	/**
	 * The number of factors in the data table. This is one less than the number
	 * of columns in the input table.
	 */
	protected int nFactors;
	/**
	 * An array of integers which give the number of factor levels for each
	 * factor.
	 */
	protected int[] nFactorLevels;
	/**
	 * An array of String arrays which for each factor holds the names of the
	 * levels.
	 */
	protected String[][] factorLevelNames;
	/** Name of this table's factors. */
	protected String[] factorNames;
	/** Stores missing value keys if there are any. */
	protected ArrayList missingValue = null;

	/**
	 * Create a FactorialDataTable.
	 * 
	 * @param n
	 *            an estimate for the expected number of entries in the table.
	 */
	protected FactorialDataTable(int n) {
		super(n);
	}

	/**
	 * Create a FactorialDataTable from the content of the given string data
	 * table.
	 * 
	 * @param data
	 *            the data table containing the raw data.
	 */
	public FactorialDataTable(StringDataTable data) {
		super(2 * data.size());
		setFactorNames(data.getColumnNames());
		findFactorLevels(data, false);
		createFactorialDataTable(data);
	}

	public void setFactorNames(String[] fn) {
		factorNames = fn;
	}

	public String[] getFactorNames() {
		return factorNames;
	}

	public String getFactorName(int i) {
		if (i >= factorNames.length) {
			throw new RuntimeException(
					"FactorialDataTable.getFactorName() Illegal index: " + i);
		}
		return factorNames[i];
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
	protected void findFactorLevels(StringDataTable data, boolean allFactors) {
		// Figure out the number of factors
		nFactors = data.getColumns();
		if (!allFactors)
			nFactors--;
		// Now figure out the factor level names
		ArrayList[] factorLevelNamesAL = new ArrayList[nFactors];
		for (int i = 0; i < nFactors; i++) {
			factorLevelNamesAL[i] = new ArrayList(30);
		}
		Object[] d;
		for (Iterator it = data.iterator(); it.hasNext();) {
			d = (Object[]) (it.next());
			for (int i = 0; i < nFactors; i++) {
				if (!isContained((String) (d[i]), factorLevelNamesAL[i])) {
					factorLevelNamesAL[i].add(d[i]);
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

	public String[][] getFactorLevelNames() {
		return factorLevelNames;
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
		Object[] d;
		int k = nFactors;
		for (Iterator it = data.iterator(); it.hasNext();) {
			d = (Object[]) (it.next());
			Integer key = keyOf(factorLevelOf(d));
			try {
				Double x = Double.valueOf((String) (d[k]));
				if (nReplications.containsKey(key)) {
					int m = ((Integer) (nReplications.get(key))).intValue() + 1;
					double y = x.doubleValue() + doubleValueFor(key);
					nReplications.put(key, new Integer(m));
					this.put(key, new Double(y));
				} else {
					nReplications.put(key, new Integer(1));
					this.put(key, x);
				}
			} catch (NumberFormatException nfx) {
				System.out.print("Format error in data value of line: ");
				for (int i = 0; i < nFactors + 1; i++)
					System.out.print(" " + (String) (d[i]));
				System.exit(3);
			}
		}
		for (ExpansionIterator ex = new ExpansionIterator(nFactorLevels); ex
				.hasNext();) {
			int[] idx = (int[]) ex.next();
			Integer key = keyOf(idx);
			if (this.containsKey(key)) {
				int p = ((Integer) (nReplications.get(key))).intValue();
				if (p > 1) {
					double x = doubleValueFor(key) / p;
					this.put(key, new Double(x));
					Debug.show(Debug.DATA, p + " replications of level "
							+ factorLevelStringOf(idx));
				}
			}
		}
	}

	public boolean hasMissingValues() {
		return (missingValue != null) && (missingValue.size() > 0);
	}

	public ArrayList getMissingValues() {
		return missingValue;
	}

	/**
	 * Fix missing values. This method takes a factor level combination which
	 * has been identified as a missing value earlier and enters the mean of all
	 * levels of the random factor for the fixed non-random factor level
	 * combination.
	 * 
	 * @param mv
	 *            an array list of keys to missing value factor level
	 *            combinations.
	 */
	protected void fixMissingValues(ArrayList mv) {
		for (int i = 0; i < mv.size(); i++) {
			Integer key = (Integer) mv.get(i);
			int[] mask = factorLevelOfKey(key);
			mask[0] = -1;
			double x = 0.0;
			int n = 0;
			for (MaskedExpansionIterator ex = new MaskedExpansionIterator(
					nFactorLevels, mask); ex.hasNext();) {
				int[] idx = (int[]) ex.next();
				Integer mkey = keyOf(idx);
				if (this.containsKey(mkey)) {
					n++;
					x += doubleValueFor(mkey);
				}
			}
			if (n > 0) {
				x = x / n;
				this.put(key, new Double(x));
				System.out
						.println("FactorialExParValueTable.fixMissingValues(): Set factor level \""
								+ factorLevelStringOf(factorLevelOfKey(key))
								+ "\" to " + x);
			} else {
				System.out
						.println("FactorialExParValueTable.fixMissingValues(): Unfixed: "
								+ factorLevelStringOf(factorLevelOfKey(key)));
			}
		}
	}

	/**
	 * Check whether the given factor level combination is contained in this
	 * data table.
	 * 
	 * @param idx
	 *            an array of factor levels defining the factor level
	 *            combination.
	 * @return true if this table contains any data for the given factor level
	 *         combination.
	 */
	public boolean containsFactorLevel(int[] idx) {
		return this.containsKey(keyOf(idx));
	}

	/**
	 * Get the data for the given factor level combination.
	 * 
	 * @param idx
	 *            an array of factor levels defining the factor level
	 *            combination.
	 * @return the data for the given factor level combination.
	 * @throws an
	 *             IllegalArgumentException if this table does not contain any
	 *             data for the given factor level.
	 */
	public double getFactorLevel(int[] idx) {
		Double x = (Double) (this.get(keyOf(idx)));
		if (x == null) {
			throw new IllegalArgumentException(
					"No data for factor level combination "
							+ factorLevelStringOf(idx));
		}
		return x.doubleValue();
	}

	/**
	 * Get the number of factor levels.
	 * 
	 * @return an integer array containing the number of levels for each factor.
	 */
	public int[] numberOfFactorLevels() {
		return nFactorLevels;
	}

	/**
	 * Get the number of factors.
	 * 
	 * @return the number of factors int this data table.
	 */
	public int numberOfFactors() {
		return nFactors;
	}

	/**
	 * Create a default array of factor names. This is ["Random", A, B, ...,
	 * "Data"].
	 */
	public String[] defaultFactorNames() {
		char[] a = new char[1];
		String[] fN = new String[nFactors + 1];
		fN[0] = "Random";
		for (int i = 1; i < nFactors; i++) {
			a[0] = (char) ('A' + (i - 1));
			fN[i] = new String(a);
		}
		fN[nFactors] = "Data";
		return fN;
	}

	/**
	 * Return a String representation of the given factor level array.
	 * 
	 * @param idx
	 *            an array of factor levels.
	 * @return a String which contains the factor level names of the given
	 *         factor levels.
	 */
	public String factorLevelStringOf(int[] idx) {
		StringBuffer b = new StringBuffer(100);
		for (int i = 0; i < nFactors; i++) {
			String n = factorLevelNames[i][idx[i]];
			b.append(n + " ");
		}
		return b.toString();
	}

	/**
	 * Compute an index array for the given data element as it is defined by its
	 * factor levels.
	 */
	protected int[] factorLevelOf(Object[] d) {
		int[] idx = new int[nFactors];
		for (int i = 0; i < nFactors; i++) {
			String s = (String) (d[i]);
			String[] fln = factorLevelNames[i];
			for (int j = 0; j < nFactorLevels[i]; j++) {
				if (s.equals(fln[j])) {
					idx[i] = j;
					break;
				}
			}
		}
		/*
		 * System.out.print("Factor level index array of" ); for (int i = 0; i <
		 * nFactors; i++) System.out.print(" " + (String)(d[i]));
		 * System.out.print(":"); for (int i = 0; i < nFactors; i++)
		 * System.out.print(" " + idx[i]); System.out.println("\n---"); /*
		 */
		return idx;
	}

	/**
	 * Compute a hash key for the given factor level array.
	 * 
	 * @param idx
	 *            an array of factor levels for this data table.
	 * @return a key object which codes the given factor levels such that the
	 *         method factorLevelArrayOfKey() is able to restore the factor
	 *         level array from the key.
	 */
	protected Integer keyOf(int[] idx) {
		int k = 1;
		int key = idx[nFactors - 1];
		for (int i = (nFactors - 2); i >= 0; i--) {
			k *= nFactorLevels[i + 1];
			key += (k * idx[i]);
		}
		// System.out.println("  key = " + key);
		return new Integer(key);
	}

	/**
	 * Create a factor level array for the given key of this table.
	 * 
	 * @param ikey
	 *            a valid key for the given data table.
	 * @return an array of factor level values corresponding to the given key.
	 */
	protected int[] factorLevelOfKey(Integer ikey) {
		int[] idx = new int[nFactors];
		int key = ikey.intValue();
		// System.out.print("Index array for key = " + key + ": ");
		int k = 1;
		for (int i = 0; i < nFactors; i++)
			k *= nFactorLevels[i];
		for (int i = 0; i < nFactors; i++) {
			k /= nFactorLevels[i];
			idx[i] = key / k;
			key -= (idx[i] * k);
		}
		/*
		 * for (int i = 0; i < nFactors; i++) System.out.print(" " + idx[i]);
		 * System.out.println("");
		 */
		return idx;
	}

	/**
	 * Check whether the give String is contained in the given ArrayList.
	 * 
	 * @param s
	 *            the String to look for,
	 * @param d
	 *            an ArrayList containing String objects as its elements.
	 * @return true if the String s is contained in the ArrayList of String
	 *         objects.
	 */
	protected static boolean isContained(String s, ArrayList d) {
		for (Iterator it = d.iterator(); it.hasNext();) {
			if (s.equals((String) (it.next())))
				return true;
		}
		return false;
	}

	/**
	 * Sort an array list of factor level names. Sorting is done numerical if
	 * all factor level names are numbers. Otherwise sorting is done
	 * alphabetically.
	 */
	protected void sort(ArrayList n) {
		double d;
		boolean numeric = true;
		for (Iterator it = n.iterator(); it.hasNext();) {
			try {
				d = Double.valueOf((String) (it.next())).doubleValue();
			} catch (NumberFormatException nfx) {
				numeric = false;
				break;
			}
		}
		if (numeric) {
			Collections.sort(n, this);
		} else {
			Collections.sort(n);
		}
	}

	public int compare(Object a, Object b) {
		double da = Double.valueOf((String) a).doubleValue();
		double db = Double.valueOf((String) b).doubleValue();
		return (da < db) ? -1 : ((da > db) ? 1 : 0);
	}

	public boolean equals(Object a) {
		return false;
	}

	public void printFactorLevelNames(PrintStream out) {
		for (int i = 0; i < nFactors; i++) {
			out.print("Factor " + getFactorName(i) + ":");
			for (int j = 0; j < nFactorLevels[i]; j++) {
				out.print(" " + factorLevelNames[i][j]);
			}
			out.println("");
		}
	}

	/**
	 * Get the name of a given factor level.
	 * 
	 * @param factor
	 *            the factor index.
	 * @param level
	 *            the factor level.
	 * @return a String containing the respective factor level name.
	 */
	public String factorLevelName(int factor, int level) {
		String r = null;
		if (factor < nFactors) {
			if (level < nFactorLevels[factor]) {
				r = factorLevelNames[factor][level];
			} else {
				throw new IllegalArgumentException("Factor " + factor
						+ " does not have a level " + level);
			}
		} else {
			throw new IllegalArgumentException("Factor " + factor
					+ " does not exist.");
		}
		return r;
	}

	public String describe() {
		StringBuffer b = new StringBuffer(1000);
		String nl = System.getProperty("line.separator");
		b.append("Number of Factors: " + nFactors + nl);
		b.append("Factor levels: ");
		for (int i = 0; i < nFactors; i++)
			b.append(" " + nFactorLevels[i]);
		b.append(nl);
		return b.toString();
	}

	/**
	 * Get the name of a given factor.
	 * 
	 * @param factor
	 *            the factor index.
	 * @return a String containing the respective factor name.
	 */
	public String factorName(int factor) {
		String r = null;
		if (factor < nFactors) {
			String s = " ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			if (factor == 0)
				r = "RND";
			else if (factor == (nFactors - 1))
				r = "DATA";
			else {
				r = s.substring(factor, factor + 1);
			}
		} else {
			throw new IllegalArgumentException("Factor " + factor
					+ " does not exist.");
		}
		return r;
	}

	/**
	 * Create a repeated measures table from this factorial data table. A
	 * repeated measures table rearranges the data such that the table is
	 * suitable for importing it into commercial statistics packages like SYSTAT
	 * or SPSS. These programs require that all data collected for one factor
	 * level combination of the random factor and the between factors be
	 * contained in a single row of the data table. In most cases the random
	 * factor will be the subject such that all data collected from one subject
	 * for a single factor level combination of the between factors will be
	 * contained in a single row of the table. The data for all levels of every
	 * within or repeated measures factor will be written from left to right
	 * following the factor levels of the random and the between factors.
	 * Suppose we have two independent within factors with 2 levels of the
	 * first, and 3 levels of the second. The sequence will be such that the
	 * last factor runs fastest. We thus will have the following factor level
	 * combinations from left to right: 11, 12, 13, 21, 22, 23. The first column
	 * of the output table will be the factor level of the random factor which
	 * is the first factor in the factorial data table. Then follow the factor
	 * levels of the between factors if these exist. And then we have the
	 * repeated measures of all within factors.
	 * 
	 * @return a data table of the format described above.
	 */
	public DataTable repeatedMeasuresTable() {
		// First let Anova figure out the factor types
		AnovaEngine engine = new AnovaEngine(this);
		if (!engine.computeStatistics())
			return null;
		int[] factorType = engine.getFactorType();
		if (factorType == null)
			return null;
		// Now set b[i] to 1 for WITHIN and to the number of factor
		// levels for every other type, don't use the DATA
		// factor. Also count the number of WITHIN factor level
		// combinations
		int[] b = new int[factorType.length - 1];
		int kw = 1;
		int kb = 0;
		for (int i = 0; i < nFactors; i++) {
			if (factorType[i] == AnovaEngine.WITHIN) {
				b[i] = 1;
				kw *= nFactorLevels[i];
			} else {
				b[i] = nFactorLevels[i];
				kb++;
			}
		}
		DataTable table = new DataTable(1000);
		int[] mask = new int[nFactors];
		String[] cNames = new String[kb + kw];
		boolean firstRow = true;
		for (ExpansionIterator ex = new ExpansionIterator(b); ex.hasNext();) {
			int[] bidx = (int[]) ex.next();
			Object[] a = new Object[kb + kw];
			int j = 0;
			for (int i = 0; i < nFactors; i++) {
				if (factorType[i] == AnovaEngine.WITHIN) {
					mask[i] = -1;
				} else {
					mask[i] = bidx[i];
					a[j] = factorLevelName(i, bidx[i]);
					if (firstRow) {
						cNames[j] = factorNames[i];
					}
					j++;
				}
			}
			boolean hasValue = false;
			ArrayList mv = null;
			for (MaskedExpansionIterator mex = new MaskedExpansionIterator(
					nFactorLevels, mask); mex.hasNext();) {
				int[] idx = (int[]) mex.next();
				if (firstRow) {
					StringBuffer vb = new StringBuffer("Var");
					for (int i = 1; i < nFactors; i++)
						if (factorType[i] == AnovaEngine.WITHIN) {
							vb.append(String.valueOf(idx[i]));
						} else {
							vb.append("_");
						}
					cNames[j] = vb.toString();
				}
				Integer key = keyOf(idx);
				if (containsKey(key)) {
					a[j++] = get(key);
					hasValue = hasValue || true;
				} else {
					a[j++] = null;
					if (mv == null) {
						mv = new ArrayList(kw);
					}
					mv.add(key);
				}
			}
			if (hasValue) {
				table.addRow(a);
				if (mv != null) {
					if (missingValue == null) {
						missingValue = new ArrayList(100);
					}
					missingValue.addAll(mv);
				}
			}
			firstRow = false;
		}
		table.setColumnNames(cNames);
		return table;
	}

	/**
	 * Create a repeated measures table from this factorial data table. A
	 * repeated measures table rearranges the data such that the table is
	 * suitable for importing it into commercial statistics packages like SYSTAT
	 * or SPSS. These programs require that all data collected for one factor
	 * level combination of the random factor and the between factors be
	 * contained in a single row of the data table. In most cases the random
	 * factor will be the subject such that all data collected from one subject
	 * for a single factor level combination of the between factors will be
	 * contained in a single row of the table. The data for all levels of every
	 * within or repeated measures factor will be written from left to right
	 * following the factor levels of the random and the between factors.
	 * Suppose we have two independent within factors with 2 levels of the
	 * first, and 3 levels of the second. The sequence will be such that the
	 * last factor runs fastest. We thus will have the following factor level
	 * combinations from left to right: 11, 12, 13, 21, 22, 23. The first column
	 * of the output table will be the factor level of the random factor which
	 * is the first factor in the factorial data table. Then follow the factor
	 * levels of the between factors if these exist. And then we have the
	 * repeated measures of all within factors.
	 * 
	 * @return a data table of the format described above.
	 */
	public DataTable crossTable() {
		if (nFactors != 3) {
			System.out
					.println("FactorialdataTable.crossTable() needs exactly 2 factors.");
			return null;
		}
		// Now set b[i] to 1 for the second factor and to the number of factor
		// levels for every other type, don't use the DATA
		// factor.
		int[] b = new int[nFactors];
		int kw = 1;
		int kb = 0;
		for (int i = 0; i < nFactors; i++) {
			if (i == 2) {
				b[i] = 1;
				kw *= nFactorLevels[i];
			} else {
				b[i] = nFactorLevels[i];
				kb++;
			}
		}
		DataTable table = new DataTable(1000);
		int[] mask = new int[nFactors];
		String[] cNames = new String[kb + kw];
		boolean firstRow = true;
		for (ExpansionIterator ex = new ExpansionIterator(b); ex.hasNext();) {
			int[] bidx = (int[]) ex.next();
			Object[] a = new Object[kb + kw];
			int j = 0;
			for (int i = 0; i < nFactors; i++) {
				if (i == 2) {
					mask[i] = -1;
				} else {
					mask[i] = bidx[i];
					a[j] = factorLevelName(i, bidx[i]);
					if (firstRow) {
						cNames[j] = factorNames[i];
					}
					j++;
				}
			}
			boolean hasValue = false;
			ArrayList mv = null;
			for (MaskedExpansionIterator mex = new MaskedExpansionIterator(
					nFactorLevels, mask); mex.hasNext();) {
				int[] idx = (int[]) mex.next();
				if (firstRow) {
					StringBuffer vb = new StringBuffer("Var");
					for (int i = 1; i < nFactors; i++)
						if (i == 2) {
							vb.append(String.valueOf(idx[i]));
						} else {
							vb.append("_");
						}
					cNames[j] = vb.toString();
				}
				Integer key = keyOf(idx);
				if (containsKey(key)) {
					a[j++] = get(key);
					hasValue = hasValue || true;
				} else {
					a[j++] = null;
					if (mv == null) {
						mv = new ArrayList(kw);
					}
					mv.add(key);
				}
			}
			if (hasValue) {
				table.addRow(a);
				if (mv != null) {
					if (missingValue == null) {
						missingValue = new ArrayList(100);
					}
					missingValue.addAll(mv);
				}
			}
			firstRow = false;
		}
		table.setColumnNames(cNames);
		return table;
	}

	protected String factorNamesString(boolean html) {
		StringBuffer a = new StringBuffer(200);
		String nl = System.getProperty("line.separator");
		if (html)
			a.append("<tr><td align=\"center\">");
		a.append(factorNames[0]);
		for (int i = 1; i < factorNames.length; i++) {
			if (html)
				a.append("</td><td align=\"center\">");
			a.append(" " + factorNames[i]);
		}
		if (html)
			a.append("</td></tr>");
		a.append(nl);
		return a.toString();
	}

	public String toString() {
		StringBuffer a = new StringBuffer(5000);
		int[] idx;
		Integer key;
		String nl = System.getProperty("line.separator");
		for (ExpansionIterator ex = new ExpansionIterator(nFactorLevels); ex
				.hasNext();) {
			idx = (int[]) ex.next();
			// for (int i = 0; i < idx.length; i++) System.out.print("-" +
			// idx[i]); System.out.println("");
			key = keyOf(idx);
			if (containsKey(key)) {
				a.append(factorLevelStringOf(idx) + "\t" + stringValueFor(key)
						+ nl);
			}
		}
		return a.toString();
	}

	protected String toHTML(boolean cn) {
		StringBuffer b = new StringBuffer(5000);
		int[] idx;
		Integer key;
		b.append("<table>");
		String nl = System.getProperty("line.separator");
		if (cn)
			b.append(factorNamesString(true));
		for (ExpansionIterator ex = new ExpansionIterator(nFactorLevels); ex
				.hasNext();) {
			idx = (int[]) ex.next();
			key = keyOf(idx);
			if (containsKey(key)) {
				b.append("<tr>" + factorLevelHTMLOf(idx)
						+ "<td align=\"right\">" + stringValueFor(key)
						+ "</td></tr>" + nl);
			}
		}
		b.append("</table>");
		return b.toString();
	}

	public String toString(boolean cn, boolean html, boolean intFormat) {
		if (html) {
			return toHTML(cn);
		} else {
			if (cn) {
				return factorNamesString(false) + toString();
			} else {
				return toString();
			}
		}
	}

	protected String factorLevelHTMLOf(int[] idx) {
		StringBuffer b = new StringBuffer(100);
		for (int i = 0; i < nFactors; i++) {
			String n = factorLevelNames[i][idx[i]];
			b.append("<td align=\"right\">" + n + "</td>");
		}
		return b.toString();
	}

	public void print(PrintStream out) {
		out.print(toString());
	}

	/**
	 * Get the string value of a table entry.
	 * 
	 * @param key
	 *            the key to access the table entry.
	 * @return a string value.
	 */
	public String stringValueFor(Object key) {
		return ((Double) get(key)).toString();
	}

	/**
	 * Get the double value of a table entry.
	 * 
	 * @param key
	 *            the key to access the table entry.
	 * @return a double value.
	 */
	public double doubleValueFor(Object key) {
		return ((Double) get(key)).doubleValue();
	}

	/**
	 * Get an integer value of a table entry.
	 * 
	 * @param key
	 *            the key to access the table entry.
	 * @return an integer value.
	 */
	public int intValueFor(Object key) {
		return (int) Math.round(((Double) get(key)).doubleValue());
	}

	/**
	 * Create a new table which contains all rows of this table which have a
	 * fixed factor level combination for certain factors.
	 * 
	 * @param mask
	 *            factor level mask. The size of this array must be identical to
	 *            the number of factors. If an entry in this array is
	 *            nonnegative then it is a fixed factor level. If it is negative
	 *            then the returned table contains all entris of this table
	 *            which iterate over this factor.
	 */
	public FactorialDataTable subSection(int[] mask) {
		StringDataTable t = new StringDataTable(size());
		t.setColumnNames(getFactorNames());
		for (MaskedExpansionIterator ex = new MaskedExpansionIterator(
				nFactorLevels, mask); ex.hasNext();) {
			int[] idx = (int[]) ex.next();
			Integer mkey = keyOf(idx);
			if (containsKey(mkey)) {
				Object[] a = new Object[nFactors + 1];
				for (int i = 0; i < idx.length; i++)
					a[i] = factorLevelName(i, idx[i]);
				a[nFactors] = stringValueFor(mkey);
				// System.out.print("Add "); for (int i = 0; i < idx.length+1;
				// i++) System.out.print("-" + (String)a[i]);
				// System.out.println("");
				t.addRow(a);
			}
		}
		return new FactorialDataTable(t);
	}

	/**
	 * Create a new table which contains the subtable of this table which has a
	 * fixed factor level combination for certain factors. The resulting table
	 * no longer contains the factors whose factor levels are fixed.
	 * 
	 * @param mask
	 *            factor level mask. The size of this array must be identical to
	 *            the number of factors. If an entry in this array is
	 *            nonnegative then it is a fixed factor level. If it is negative
	 *            then the returned table contains all entris of this table
	 *            which iterate over this factor.
	 */
	public FactorialDataTable subTable(int[] mask) {
		StringDataTable t = new StringDataTable(size());
		int n = mask.length;
		if (n != nFactors) {
			new NonFatalError("FactorialDataTable.subTable() mask length (" + n
					+ ") not equal to number of factors (" + nFactors + ")");
			return null;
		}
		int m = 0;
		for (int i = 0; i < n; i++)
			if (mask[i] < 0)
				m++;
		m++;
		for (MaskedExpansionIterator ex = new MaskedExpansionIterator(
				nFactorLevels, mask); ex.hasNext();) {
			int[] idx = (int[]) ex.next();
			Integer mkey = keyOf(idx);
			if (containsKey(mkey)) {
				String[] a = new String[m];
				int k = 0;
				for (int i = 0; i < idx.length; i++) {
					if (mask[i] < 0)
						a[k++] = factorLevelName(i, idx[i]);
				}
				a[m - 1] = stringValueFor(mkey);
				// System.out.print("Add "); for (int i = 0; i < idx.length+1;
				// i++) System.out.print("-" + (String)a[i]);
				// System.out.println("");
				t.addRow(a);
			}
		}
		t.setColumnNames(selectedFactorNames(mask));
		return new FactorialDataTable(t);
	}

	/**
	 * Creates a new table where the new data are a function of all levels of a
	 * single factor of the original data. The respective factor no longer is
	 * contained in the resulting data table.
	 * 
	 * @param f
	 *            the factor whose levels are to be combined.
	 * @param func
	 *            the function for combining the factor levels.
	 * @return a derived factorial data table.
	 */
	public FactorialDataTable derivedTable(int f,
			DoubleArrayArgumentFunction func) {
		if (f >= nFactors) {
			new NonFatalError(
					"FactorialDataTable.derivedTable() factor number (" + f
							+ ") out of range");
			return null;
		}
		StringDataTable t = new StringDataTable(size());
		int[] mask = new int[nFactors];
		for (int i = 0; i < nFactors; i++)
			mask[i] = -1;
		mask[f] = 0;
		int n = nFactorLevels[f];
		double[] d = new double[n];
		for (MaskedExpansionIterator ex = new MaskedExpansionIterator(
				nFactorLevels, mask); ex.hasNext();) {
			int[] idx = (int[]) ex.next();
			boolean valid = true;
			for (int i = 0; i < n; i++) {
				idx[f] = i;
				Integer mkey = keyOf(idx);
				if (containsKey(mkey)) {
					d[i] = doubleValueFor(mkey);
				} else {
					valid = valid && false;
				}
			}
			if (valid) {
				String[] a = new String[nFactors];
				int k = 0;
				for (int i = 0; i < nFactors; i++) {
					if (i != f) {
						a[k++] = factorLevelName(i, idx[i]);
					}
				}
				a[k] = String.valueOf(func.valueOf(d));
				t.addRow(a);
			}
		}
		t.setColumnNames(selectedFactorNames(mask));
		return new FactorialDataTable(t);
	}

	private String[] selectedFactorNames(int[] mask) {
		int m = 0;
		for (int i = 0; i < mask.length; i++)
			if (mask[i] < 0)
				m++;
		String[] fnn = new String[m + 1];
		int k = 0;
		for (int i = 0; i < mask.length; i++) {
			if (mask[i] < 0)
				fnn[k++] = factorNames[i];
		}
		fnn[k] = factorNames[nFactors];
		return fnn;
	}

	public Iterator subSectionIterator(int[] mask) {
		return new SubsectionIterator(mask);
	}
	/**
	 * An iterator which creates a series of subTables. Every subTable has a
	 * fixed factor level combination for certain factors.
	 */
	private class SubsectionIterator implements Iterator {
		private ExpansionIterator factorIt;
		private int[] mask;
		private int n;

		/**
		 * Create a subTable iterator for this table. The parameter mask defines
		 * those factors which iterate over the sequence of tables generated. If
		 * an entry in mask[] is nonnegative, then the respective factor belongs
		 * to the iteration group. If an entry is negative then all levels of
		 * this factor will be contained in the table.
		 */
		public SubsectionIterator(int[] mask) {
			this.mask = mask;
			n = mask.length;
			if (n != nFactors) {
				throw new RuntimeException(
						"FactorialDataTable.SubsectionIterator() mask[] size is not equal to number of factors in table.");
			}
			// count iterating factors: k
			int k = 0;
			for (int i = 0; i < n; i++)
				if (mask[i] >= 0)
					k++;
			int[] m = new int[k];
			// create array of iterating factors levels
			int j = 0;
			for (int i = 0; i < n; i++)
				if (mask[i] >= 0)
					m[j++] = nFactorLevels[i];
			// create factor level iterator
			factorIt = new ExpansionIterator(m);
		}

		public boolean hasNext() {
			return factorIt.hasNext();
		}

		public Object next() {
			int[] m = (int[]) factorIt.next();
			int[] mx = new int[n];
			int j = 0;
			for (int i = 0; i < n; i++)
				if (mask[i] >= 0) {
					mx[i] = m[j++];
				} else {
					mx[i] = -1;
				}
			return subSection(mx);
		}

		public void remove() {
		}
	}
}
