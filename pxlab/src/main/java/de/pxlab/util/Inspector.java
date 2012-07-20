package de.pxlab.util;

import java.lang.reflect.*;

import java.util.ArrayList;

/**
 * Utilities to inspect internals of classes and to get the values of declared
 * fields.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class Inspector {
	/**
	 * Get the static integer constants of the given class.
	 * 
	 * @return a string array containing the names of all static integer
	 *         constants of the class to be inspected or null if this class does
	 *         not have static integer fields defined.
	 */
	public static String[] getStaticIntegerConstantsOf(Class cls) {
		Field[] flds = cls.getFields();
		int nflds = flds.length;
		String[] constants = null;
		if (nflds > 0) {
			ArrayList fldsa = new ArrayList();
			for (int i = 0; i < nflds; i++) {
				// System.out.println("Inspector.getStaticIntegerConstantsOf(): Field "
				// + flds[i].getName());
				try {
					Object a = flds[i].get(null);
					// System.out.println(" is class " +
					// a.getClass().getName());
					if (a instanceof Integer) {
						String cn = flds[i].getName();
						fldsa.add(cn);
						// System.out.println("Inspector.getStaticIntegerConstantsOf(): Integer "
						// + cn);
					}
				} catch (IllegalAccessException iae) {
					System.out.println(iae.getMessage());
				}
			}
			int n = fldsa.size();
			if (n > 0) {
				constants = new String[n];
				for (int i = 0; i < n; i++) {
					constants[i] = (String) fldsa.get(i);
					// System.out.println("Inspector.getStaticIntegerConstantsOf(): "
					// + constants[i]);
				}
			}
		}
		return constants;
	}

	/**
	 * Find the value of a class constant of type integer whose name is given as
	 * an argument. The constant must be defined with the public and static
	 * attributes.
	 * 
	 * @param cls
	 *            then class which contains the integer constant.
	 * @param fn
	 *            name of the field.
	 * @return an integer having the constant's value.
	 */
	public static int valueOf(Class cls, String fn) {
		Object obj = null;
		String err = null;
		int x = 0;
		try {
			java.lang.reflect.Field fld = cls.getField(fn);
			try {
				obj = fld.get(null);
				if (obj instanceof Integer) {
					x = ((Integer) obj).intValue();
				} else {
					RuntimeException ex = new RuntimeException("Field " + fn
							+ " is not an integer.");
					ex.printStackTrace();
				}
			} catch (IllegalAccessException iae) {
				RuntimeException ex = new RuntimeException("Illegal access to "
						+ fn);
				ex.printStackTrace();
			}
		} catch (NoSuchFieldException nsfe) {
			RuntimeException ex = new RuntimeException("Class " + cls.getName()
					+ " does not have a field named " + fn);
			ex.printStackTrace();
		}
		return x;
	}
	/* This is an example of how this method may be used. */
	/*
	 * public static void main(String[] args) { String[] n =
	 * getStaticIntegerConstantsOf(de.pxlab.pxl.ResponseCodes.class); for (int i
	 * = 0; i < n.length; i++) { System.out.println(n[i] + " = " +
	 * valueOf(de.pxlab.pxl.ResponseCodes.class, n[i])); } }
	 */
}
