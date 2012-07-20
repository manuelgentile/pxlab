package de.pxlab.pxl.data;

import java.io.*;

import de.pxlab.stat.*;
import de.pxlab.util.*;
import de.pxlab.pxl.*;

/**
 * Transform colors and export them to a file.
 * 
 * <p>
 * If the input table contains a single column then it must be an array valued
 * ExParValue.
 * 
 * <p>
 * Otherwise the input table must contain exactly three columns which are
 * interpreted as color coordinates.
 * 
 * <p>
 * The output table usually is a 3-dimensional table of color coordinates.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 
 * 2006/03/09
 */
public class TransformColors extends DataDisplay {
	/**
	 * The input color space code. Codes are defined in class
	 * de.pxlab.pxl.ColorSpaceCodes.
	 * 
	 * @see de.pxlab.pxl.ColorSpaceCodes
	 */
	public ExPar InputSpace = new ExPar(GEOMETRY_EDITOR, ColorSpaceCodes.class,
			new ExParValueConstant("de.pxlab.pxl.ColorSpaceCodes.CS_Yxy"),
			"Input color space");
	/**
	 * The output color space code. Codes are defined in class
	 * de.pxlab.pxl.ColorSpaceCodes.
	 * 
	 * @see de.pxlab.pxl.ColorSpaceCodes
	 */
	public ExPar OutputSpace = new ExPar(GEOMETRY_EDITOR,
			ColorSpaceCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.ColorSpaceCodes.CS_Yxy"),
			"Output color space");
	/** Flag to write HTML output. */
	public ExPar HTMLFormat = new ExPar(FLAG, new ExParValue(0),
			"Flag to write HTML formatted output");

	public TransformColors() {
		setTitleAndTopic("Transform colors", Topics.DATA);
	}

	protected int processDataTable(ExDesign exDesign, String[] p,
			ExParValueTable grandTable) {
		ExParValueTable table = null;
		if (p != null && p.length != 0) {
			table = new ExParValueTable(grandTable.subTable(p));
		} else {
			table = grandTable;
		}
		int n = table.size();
		PxlColor[] pc = new PxlColor[n];
		int inputSpace = InputSpace.getInt();
		if (table.getColumns() == 1) {
			for (int i = 0; i < n; i++) {
				double[] d = ((ExParValue) (((Object[]) (table.row(i)))[0]))
						.getDoubleArray();
				pc[i] = PxlColor.instance(inputSpace, d);
				System.out.println(i + ": " + pc[i]);
			}
		} else if (table.getColumns() == 3) {
			for (int i = 0; i < n; i++) {
				double[] d = table.doubleRow(i);
				pc[i] = new PxlColor(d);
				System.out.println(i + ": " + pc[i]);
			}
		} else {
			return 0;
		}
		int outputSpace = OutputSpace.getInt();
		DataTable tb = new DataTable(n);
		for (int i = 0; i < n; i++) {
			double[] d = pc[i].transform(outputSpace);
			Object[] a = new Object[3];
			for (int j = 0; j < 3; j++) {
				a[j] = new Double(d[j]);
			}
			// System.out.println(i + ": " + d[0] + " " + d[1] + " " + d[2]);
			tb.addRow(a);
		}
		String data = tb.toString(false, HTMLFormat.getFlag(), false);
		if (HTMLFormat.getFlag()) {
			String title = "<" + StatEngine.header2 + ">" + Header.getString()
					+ "</" + StatEngine.header2 + ">\n";
			showResults(title + data);
		} else {
			showResults(data);
		}
		return 0;
	}
}
