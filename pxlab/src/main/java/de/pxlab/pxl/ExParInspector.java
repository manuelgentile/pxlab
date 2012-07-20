package de.pxlab.pxl;

import java.lang.reflect.*;

import java.util.ArrayList;

/**
 * A Utility to search classes for ExPar fields.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class ExParInspector {
	/**
	 * Create an array of ExParDescriptor objects for every public ExPar field
	 * of the given object.
	 * 
	 * @param obj
	 *            the object whose ExPar fields should be retrieved.
	 * @return an array of ExParDescriptor objects which describe the ExPar
	 *         fields of the given object.
	 */
	public static ExParDescriptor[] getExParsOf(Object obj) {
		Class cls = obj.getClass();
		Field[] flds = cls.getFields();
		int nflds = flds.length;
		ExParDescriptor[] exParFields = null;
		if (nflds > 0) {
			ArrayList fldsa = new ArrayList();
			for (int i = 0; i < nflds; i++) {
				// System.out.println("Field " + flds[i].getName());
				try {
					//
					Object a = flds[i].get(obj);
					// System.out.println(" is class " +
					// a.getClass().getName());
					if (a instanceof ExPar) {
						int type = ((ExPar) a).getType();
						String fn = flds[i].getName();
						fldsa.add(new ExParDescriptor(fn, (ExPar) a,
								ExParTypeCodes.editor[type]));
						// System.out.println("Display.createExParFields(): ExPar Field "
						// + fn);
					}
				} catch (IllegalAccessException iae) {
					// System.out.println(iae.getMessage());
				}
			}
			int n = fldsa.size();
			if (n > 0) {
				exParFields = new ExParDescriptor[n];
				for (int i = 0; i < n; i++) {
					ExParDescriptor xpd = (ExParDescriptor) fldsa.get(i);
					exParFields[i] = xpd;
				}
			}
		}
		return exParFields;
	}
}
