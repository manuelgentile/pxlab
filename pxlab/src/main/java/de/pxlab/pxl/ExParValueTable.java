package de.pxlab.pxl;

import java.util.*;
import de.pxlab.stat.*;

/**
 * This is an ArrayList of fixed length ExParValue arrays.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/12/12
 */
public class ExParValueTable extends DataTable {
	/**
	 * Create a table of ExParValue arrays.
	 * 
	 * @param n
	 *            the initial size of the table.
	 */
	public ExParValueTable(int n) {
		super(n);
	}

	/**
	 * Create a table of ExParValue arrays.
	 * 
	 * @param cn
	 *            an array of Strings which are the names of the experimental
	 *            parameters whose values are contained in the table columns.
	 * @param n
	 *            the initial size of the table.
	 */
	public ExParValueTable(String[] cn, int n) {
		super(n);
		setColumnNames(cn);
	}

	/**
	 * Create an ExParValueTable from a data file which contains a fixed number
	 * of rows and columns of data as it is the case for PXLab data files with
	 * extension 'dat'.
	 */
	public ExParValueTable(String fn) {
		super(2000);
		StringDataTable sdt = new StringDataTable(fn);
		int n = sdt.getRows();
		for (int i = 0; i < n; i++) {
			ExParValue[] a = new ExParValue[sdt.getColumns()];
			String[] s = (String[]) (sdt.row(i));
			for (int j = 0; j < s.length; j++) {
				a[j] = new ExParValue(s[j]);
			}
			addRow(a);
		}
	}

	/**
	 * Create an ExParValueTable from a given DataTable. This works like a type
	 * cast. It is assumed that the content of the given DataTable is of type
	 * ExParValue!
	 * 
	 * @param t
	 *            a DataTable which contains arrays of ExParValue objects.
	 */
	public ExParValueTable(DataTable t) {
		super(t.getRows());
		setColumnNames(t.getColumnNames());
		addAll(t);
	}

	/**
	 * Extract a single named column of this data table as a double array.
	 * 
	 * @param n
	 *            the name of the experimental parameter contained in this
	 *            column.
	 * @return a double array containing all column entries.
	 */
	public double[] doubleColumn(String n) {
		return doubleColumn(columnIndex(n));
	}

	/**
	 * Extract a single column of this data table as a double array.
	 * 
	 * @param c
	 *            the index of the column to be extracted.
	 * @return a double array containing all column entries.
	 */
	public double[] doubleColumn(int c) {
		Object[] oc = column(c);
		double[] col = new double[oc.length];
		for (int i = 0; i < oc.length; i++) {
			col[i] = ((ExParValue) (oc[i])).getDouble();
		}
		return col;
	}

	/**
	 * Extract a single named column of this data table as an integer array.
	 * 
	 * @param n
	 *            the name of the experimental parameter contained in this
	 *            column.
	 * @return an integer array containing all column entries.
	 */
	public int[] intColumn(String n) {
		return intColumn(columnIndex(n));
	}

	/**
	 * Extract a single column of this data table as an integer array.
	 * 
	 * @param c
	 *            the index of the column to be extracted.
	 * @return an integer array containing all column entries.
	 */
	public int[] intColumn(int c) {
		Object[] oc = column(c);
		int[] col = new int[oc.length];
		for (int i = 0; i < oc.length; i++) {
			col[i] = ((ExParValue) (oc[i])).getInt();
		}
		return col;
	}

	/**
	 * Get an indexed row of this table as a double array.
	 * 
	 * @param k
	 *            the index of the row.
	 * @return the row array of double values selected.
	 */
	public double[] doubleRow(int k) {
		Object[] r = (Object[]) row(k);
		double[] d = new double[r.length];
		for (int i = 0; i < r.length; i++) {
			d[i] = ((ExParValue) r[i]).getDouble();
		}
		return d;
	}

	public ExParValue entry(int r, String cn) {
		return (ExParValue) (((Object[]) row(r))[columnIndex(cn)]);
	}

	/**
	 * Create a table where all ExParValue entries in this table have been
	 * converted to their String values.
	 * 
	 * @return a StringDataTable object where every entry may be casted to a
	 *         String.
	 */
	public StringDataTable stringTable() {
		int n = getRows();
		int m = getColumns();
		StringDataTable t = new StringDataTable(n);
		for (int i = 0; i < n; i++) {
			Object[] r = (Object[]) row(i);
			String[] s = new String[m];
			for (int j = 0; j < m; j++) {
				s[j] = ((ExParValue) r[j]).getString();
			}
			t.addRow(s);
		}
		t.setColumnNames(getColumnNames());
		return t;
	}

	public String toString() {
		StringBuffer b = new StringBuffer(10000);
		String nl = System.getProperty("line.separator");
		for (int i = 0; i < size(); i++) {
			Object[] r = (Object[]) row(i);
			for (int j = 0; j < getColumns(); j++) {
				// b.append("\t" + ((ExParValue)r[j]).toString());
				b.append("\t"
						+ ((ExParValue) r[j])
								.toFormattedString(StringSubstitutionFormat.SIMPLE_ARRAY_FMT));
			}
			b.append(nl);
		}
		return b.toString();
	}
}
