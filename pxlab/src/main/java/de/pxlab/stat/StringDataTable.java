package de.pxlab.stat;

import java.io.*;
import java.util.*;

import de.pxlab.util.StringExt;
import de.pxlab.pxl.Debug;

/**
 * A DataTable where every element allows a type cast to a String.
 * 
 * @version 0.2.0
 */
public class StringDataTable extends DataTable {
	public StringDataTable(int n) {
		super(n);
	}

	public StringDataTable(String fn) {
		this(fn, false);
	}

	/**
	 * Read the given file into a DataTable. The table then contains an
	 * ArrayList of String arrays. Every String array contains the elements of a
	 * single line of input. The ArrayList contains as many entries as there are
	 * nonempty lines of input in the data file. Empty lines and lines starting
	 * with a '#' character are ignored.
	 * 
	 * @param fn
	 *            the name of the data file.
	 * @param numberLines
	 *            if true then every input line gets an additional line number
	 *            as its first element.
	 */
	public StringDataTable(String fn, boolean numberLines) {
		super(1000);
		try {
			Debug.show(Debug.FILES, "StringDataTable(): Open data file " + fn);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(fn)));
			String line;
			int n = 0;
			int nLine = 1;
			int nc = -1;
			ArrayList srow;
			StringTokenizer st;
			while ((line = reader.readLine()) != null) {
				// System.out.println("StringDataTable() read " + line);
				n++;
				if (line.length() > 0) {
					line = line.trim();
					if (!line.startsWith("#")) {
						st = new StringTokenizer(line);
						srow = new ArrayList(10);
						if (numberLines) {
							srow.add(String.valueOf(nLine++));
						}
						while (st.hasMoreTokens()) {
							srow.add(st.nextToken());
						}
						if (nc >= 0) {
							if (nc != srow.size()) {
								System.out
										.println("Wrong number of entries in line "
												+ n + " of file " + fn);
							}
						} else {
							nc = srow.size();
						}
						String[] a = StringExt.stringArrayOfList(srow);
						// for (int i = 0; i < a.length; i++)
						// System.out.print("|" + a[i]); System.out.println("");
						addRow(a);
					}
				}
			}
			reader.close();
			Debug.show(Debug.FILES, "StringDataTable(): Closed file " + fn);
			// System.out.println("Read " + size() + " data lines from file " +
			// fn);
		} catch (IOException iox) {
			System.out.println("Read error on file " + fn);
		}
		// System.out.println("StringDataTable()");
		// System.out.println(toString());
	}

	/**
	 * Convert this table to a table whose entries are of type Double.
	 */
	public DataTable doubleTable() {
		int n = getRows();
		int m = getColumns();
		DataTable t = new DataTable(n);
		for (int i = 0; i < n; i++) {
			String[] r = (String[]) (row(i));
			Double[] d = new Double[m];
			for (int j = 0; j < m; j++) {
				// System.out.print(" " + r[j]);
				try {
					d[j] = new Double(r[j]);
				} catch (NumberFormatException nfx) {
					d[j] = new Double(Double.NaN);
				}
				// System.out.print("->" + d[j].toString());
			}
			t.addRow(d);
			// System.out.println("");
		}
		return t;
	}

	/**
	 * Create a subtable of this table containing those columns whose names are
	 * contained in the selection array.
	 * 
	 * @param cn
	 *            an array of names which specify the columns which should be
	 *            contained in the subtable.
	 */
	public StringDataTable subTable(String[] cn) {
		int[] slct = new int[cn.length];
		for (int i = 0; i < cn.length; i++)
			slct[i] = columnIndex(cn[i]);
		return subTable(0, 0, slct);
	}

	/**
	 * Create a subtable of this table containing those columns whose indices
	 * are contained in the selection array.
	 * 
	 * @param slct
	 *            an array of indices which specify the columns which should be
	 *            contained in the subtable.
	 */
	public StringDataTable subTable(int[] slct) {
		return subTable(0, 0, slct);
	}

	/**
	 * Create a subtable of this table containing the given subsection of rows
	 * and those columns whose indices are contained in the selection array.
	 * 
	 * @param firstRow
	 *            the first row of this table which should be contained in the
	 *            subtable.
	 * @param lastRow
	 *            the last row of this table which should be contained in the
	 *            subtable. If lastRow is equal to firstRow then all rows are
	 *            entered into the subtable.
	 * @param slct
	 *            an array of indices which specify the columns which should be
	 *            contained in the subtable.
	 */
	public StringDataTable subTable(int firstRow, int lastRow, int[] slct) {
		checkColumnSelection(slct);
		if (lastRow <= firstRow) {
			firstRow = 0;
			lastRow = size() - 1;
		}
		StringDataTable sub = new StringDataTable(lastRow + 1 - firstRow);
		for (int i = firstRow; i <= lastRow; i++) {
			sub.addRow(subRow(i, slct));
		}
		String[] n = getColumnNames();
		String[] cn = new String[slct.length];
		for (int i = 0; i < slct.length; i++)
			cn[i] = n[slct[i]];
		sub.setColumnNames(cn);
		return sub;
	}

	public String[] subRow(int k, int[] slct) {
		checkColumnSelection(slct);
		String[] newRow = new String[slct.length];
		String[] rw = (String[]) row(k);
		for (int i = 0; i < slct.length; i++)
			newRow[i] = rw[slct[i]];
		return newRow;
	}

	/**
	 * Get a single entry of this table.
	 * 
	 * @param r
	 *            row
	 * @param c
	 *            column
	 * @return the string at the given position.
	 */
	public String stringEntry(int r, int c) {
		return ((String[]) row(r))[c];
	}

	public String toString() {
		StringBuffer b = new StringBuffer(10000);
		String nl = System.getProperty("line.separator");
		int n = getRows();
		for (int i = 0; i < n; i++) {
			Object rw = this.row(i);
			String[] r = (String[]) rw;
			for (int j = 0; j < r.length; j++) {
				b.append(" " + r[j]);
			}
			b.append(nl);
		}
		return b.toString();
	}
}
