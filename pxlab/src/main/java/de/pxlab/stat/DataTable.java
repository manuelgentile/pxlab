package de.pxlab.stat;

import java.util.*;
import java.io.PrintStream;

import de.pxlab.util.StringExt;

/**
 * A table which has an extensible number of rows and a fixed number of columns.
 * The columns of the table have names.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class DataTable extends ArrayList {
	/** Number of columns in this table. */
	private int columns = 0;
	/** The names of the table columns. */
	private String[] columnNames = null;

	/**
	 * Create a table for the given number of rows. The table is still empty.
	 */
	public DataTable(int n) {
		super(n);
	}

	/**
	 * Add a row to the table.
	 * 
	 * @param row
	 *            an Object array which contains the table row. If this table is
	 *            nonempty, then the number of array elements must be identical
	 *            to the number of columns in this table. If this table is empty
	 *            then the number of array elements defines the number of
	 *            columns for the table.
	 * @return true.
	 */
	public boolean addRow(Object[] row) {
		boolean r = true;
		if (size() == 0) {
			columns = row.length;
			// System.out.println("DataTable.addRow() number of columns: " +
			// columns);
		} else {
			if (row.length != getColumns()) {
				throw new RuntimeException(
						"Can't add a row with different number of columns.");
			}
		}
		return super.add(row);
	}

	/**
	 * Add a row to the table.
	 * 
	 * @param a
	 *            an Object which is an array containing the table row. If this
	 *            table is nonempty, then the number of array elements must be
	 *            identical to the number of columns in this table. If this
	 *            table is empty then the number of array elements defines the
	 *            number of columns for the table.
	 * @return true.
	 */
	public boolean add(Object a) {
		return addRow((Object[]) a);
	}

	/**
	 * Add the given table to the end of this table. If this table is empty then
	 * it becomes identical to the input table with respect to number of columns
	 * and column names.
	 * 
	 * @param tab
	 *            the table which should be added. The table must have the same
	 *            number of columns as this table.
	 * @return true if this table changed as a result of the call.
	 */
	public boolean addAll(DataTable tab) {
		if (getRows() == 0) {
			columns = tab.getColumns();
			setColumnNames(tab.getColumnNames());
		}
		if (tab.getColumns() != getColumns()) {
			throw new RuntimeException(
					"Can't add a table with different number of columns.");
		}
		return super.addAll(tab);
	}

	/**
	 * Return the number of columns int this data table.
	 * 
	 * @return number of columns.
	 */
	public int getColumns() {
		return columns;
	}

	/**
	 * Return the number of rows int this data table.
	 * 
	 * @return number of rows.
	 */
	public int getRows() {
		return size();
	}

	/**
	 * Set the names of the table columns.
	 * 
	 * @param cn
	 *            an array of Strings which are the names of the table columns.
	 */
	public void setColumnNames(String[] cn) {
		columnNames = cn;
	}

	/**
	 * Get the names of the table columns.
	 * 
	 * @return a String array of names.
	 */
	public String[] getColumnNames() {
		if (columnNames == null) {
			int n = getColumns();
			if (n > 0) {
				char[] a = new char[1];
				columnNames = new String[n];
				for (int i = 0; i < n; i++) {
					a[0] = (char) ('A' + i);
					columnNames[i] = new String(a);
				}
			}
		}
		return columnNames;
	}

	/**
	 * Create a subtable of this table containing those columns whose names are
	 * contained in the selection array.
	 * 
	 * @param cn
	 *            an array of names which specify the columns which should be
	 *            contained in the subtable.
	 */
	public DataTable subTable(String[] cn) {
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
	public DataTable subTable(int[] slct) {
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
	public DataTable subTable(int firstRow, int lastRow, int[] slct) {
		checkColumnSelection(slct);
		if (lastRow <= firstRow) {
			firstRow = 0;
			lastRow = size() - 1;
		}
		DataTable sub = new DataTable(lastRow + 1 - firstRow);
		for (int i = firstRow; i <= lastRow; i++) {
			sub.add(subRow(i, slct));
		}
		String[] n = getColumnNames();
		String[] cn = new String[slct.length];
		for (int i = 0; i < slct.length; i++)
			cn[i] = n[slct[i]];
		sub.setColumnNames(cn);
		return sub;
	}

	/**
	 * Create a single subrow of this table containing those columns whose
	 * indices are contained in the selection array.
	 * 
	 * @param k
	 *            the index of the row.
	 * @param slct
	 *            an array of indices which specify the columns which should be
	 *            contained in the subrow.
	 * @return an Object array containing the requested subrow.
	 */
	public Object[] subRow(int k, int[] slct) {
		checkColumnSelection(slct);
		Object[] newRow = new Object[slct.length];
		Object[] rw = (Object[]) row(k);
		for (int i = 0; i < slct.length; i++)
			newRow[i] = rw[slct[i]];
		return newRow;
	}

	/**
	 * Extract a single column of this data table.
	 * 
	 * @param name
	 *            the name of the column to be extracted.
	 * @return an Object array containing all column entries.
	 */
	public Object[] column(String name) {
		return column(columnIndex(name));
	}

	/**
	 * Extract a single column of this data table.
	 * 
	 * @param k
	 *            the index of the column.
	 * @return an Object array of column elements.
	 */
	public Object[] column(int k) {
		if (k >= getColumns()) {
			throw new RuntimeException("Illegal column requested: " + k
					+ ". Table has only " + getColumns() + " columns.");
		}
		if (size() == 0) {
			throw new RuntimeException("Table is empty.");
		}
		Object[] col = new Object[size()];
		for (int i = 0; i < size(); i++)
			col[i] = ((Object[]) row(i))[k];
		return col;
	}

	/**
	 * Get an indexed row of this table.
	 * 
	 * @param k
	 *            the index of the row.
	 * @return the Object array selected.
	 */
	public Object row(int k) {
		if (size() == 0) {
			throw new RuntimeException("Table is empty.");
		}
		if (k >= size()) {
			throw new RuntimeException("Illegal row requested: " + k
					+ ". Table has only " + size() + " rows.");
		}
		return get(k);
	}

	/**
	 * Get an indexed row of this table as a double array. This method will
	 * throw a runtime exception if the table entries cannot be casted to type
	 * Double.
	 * 
	 * @param k
	 *            the index of the row.
	 * @return the row array of double values selected.
	 */
	public double[] doubleRow(int k) {
		int n = getColumns();
		double[] r = new double[n];
		Object[] w = (Object[]) row(k);
		for (int i = 0; i < n; i++) {
			r[i] = ((Double) w[i]).doubleValue();
		}
		return r;
	}

	/**
	 * Get a named column of this table as a double array. This method will
	 * throw a runtime exception if the table entries cannot be casted to type
	 * Double.
	 * 
	 * @param n
	 *            the name string of the column.
	 * @return the row array of double values selected.
	 */
	public double[] doubleColumn(String n) {
		return doubleColumn(columnIndex(n));
	}

	/**
	 * Get an indexed column of this table as a double array. This method will
	 * throw a runtime exception if the table entries cannot be casted to type
	 * Double.
	 * 
	 * @param k
	 *            the index of the column.
	 * @return the row array of double values selected.
	 */
	public double[] doubleColumn(int k) {
		int n = getRows();
		double[] r = new double[n];
		Object[] w = column(k);
		for (int i = 0; i < n; i++) {
			r[i] = ((Double) w[i]).doubleValue();
		}
		return r;
	}

	/**
	 * Check whether it is possible to select the given columns from this table.
	 * 
	 * @param slct
	 *            an array of column indices to select.
	 */
	protected void checkColumnSelection(int[] slct) {
		for (int i = 0; i < slct.length; i++) {
			if (slct[i] >= getColumns()) {
				throw new RuntimeException("Illegal column requested: "
						+ slct[i] + ". Table has only " + getColumns()
						+ " columns.");
			}
		}
	}

	/**
	 * Find the column index for the given column name.
	 * 
	 * @param name
	 *            the parameter name whose column index is requested.
	 * @return the column index of the given name.
	 */
	public int columnIndex(String name) {
		int ix = StringExt.indexOf(name, columnNames);
		if (ix >= 0) {
			return ix;
		} else {
			System.out.println("ExparValueTable.column() Error: Parameter "
					+ name + " not contained in data table.");
			return -1;
		}
	}

	public void printColumnNames(PrintStream out) {
		out.println("Column names:" + columnNamesString(false));
	}
	private boolean convertDataToInteger = false;

	public String toString(boolean cn, boolean html, boolean intFormat) {
		String s = null;
		convertDataToInteger = intFormat;
		if (html) {
			s = toHTML(cn);
		} else {
			if (cn) {
				s = columnNamesString(false) + toString();
			} else {
				s = toString();
			}
		}
		convertDataToInteger = false;
		return s;
	}

	protected String columnNamesString(boolean html) {
		StringBuffer a = new StringBuffer(200);
		String nl = System.getProperty("line.separator");
		if (html)
			a.append("<tr><td align=\"center\">");
		a.append(columnNames[0]);
		for (int i = 1; i < columns; i++) {
			if (html)
				a.append("</td><td align=\"center\">");
			a.append(" " + columnNames[i]);
		}
		if (html)
			a.append("</td></tr>");
		a.append(nl);
		return a.toString();
	}

	public String toString() {
		StringBuffer b = new StringBuffer(10000);
		String nl = System.getProperty("line.separator");
		String s;
		int n = getRows();
		b.append(nl);
		for (int i = 0; i < n; i++) {
			Object[] r = (Object[]) row(i);
			for (int j = 0; j < r.length; j++) {
				s = (r[j] == null) ? "?" : stringValueFor(r[j]);
				b.append(" " + s);
			}
			b.append(nl);
		}
		return b.toString();
	}

	private String stringValueFor(Object k) {
		String s;
		if (convertDataToInteger) {
			if (k instanceof Double) {
				s = String
						.valueOf((int) Math.round(((Double) k).doubleValue()));
			} else {
				s = k.toString();
			}
		} else {
			s = k.toString();
		}
		return s;
	}

	private String toHTML(boolean cn) {
		StringBuffer b = new StringBuffer(10000);
		String nl = System.getProperty("line.separator");
		String s;
		b.append("<table>");
		if (cn)
			b.append(columnNamesString(true));
		int n = getRows();
		for (int i = 0; i < n; i++) {
			Object[] r = (Object[]) row(i);
			b.append("<tr><td align=\"right\">"
					+ ((r[0] == null) ? "?" : stringValueFor(r[0])));
			for (int j = 1; j < r.length; j++) {
				s = (r[j] == null) ? "?" : stringValueFor(r[j]);
				b.append("</td><td align=\"right\">" + s);
			}
			b.append("</td></tr>" + nl);
		}
		b.append("</table>" + nl);
		return b.toString();
	}
}
