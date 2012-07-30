package de.pxlab.util;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Maps verbal descriptions of options to their respective option codes using
 * regular expression notation.
 * 
 * Should do: - create list of numeric codes, verbal codes, description - check
 * whether a given verbal option input matches an option in the list - convert
 * verbal option input to numeric codes - allow numeric codes as options and
 * convert them to proper codes - show possible option list
 * 
 * @version 0.3.0
 */
/*
 * 
 * 2004/12/11
 * 
 * 2005/02/24 handle numbers separately
 * 
 * 2006/11/12 version 0.3.0
 */
public class OptionCodeMap extends HashMap {
	private long lastCode;
	private String title;
	private boolean showCodes;

	public OptionCodeMap(String t, boolean s) {
		title = t;
		showCodes = s;
	}

	/**
	 * Check whether the given option description matches any of the entries in
	 * the given code map. The code map is searched in the same order as the
	 * entries have been generated. The first match is reported. The value of
	 * the matching entry may be retrieved using the getCode() method.
	 * 
	 * @return true if any of the entries in the map matches the given
	 *         description and false otherwise.
	 */
	public boolean hasCodeFor(String shorthand) {
		// System.out.println("OptionCodeMap.hasCodeFor(): Looking for " +
		// shorthand);
		
		System.out.println(shorthand);
		
		if (shorthand.startsWith("?")) {
			System.out.println(toString());
			return false;
		}
		boolean rc = false;
		try {
			lastCode = Long.parseLong(shorthand);
			for (Iterator it = values().iterator(); it.hasNext();) {
				OptionCodeMap.Entry om = (OptionCodeMap.Entry) it.next();
				if (om.getCode() == lastCode) {
					rc = true;
					// System.out.println("OptionCodeMap.hasCodeFor(): Found code "
					// + lastCode);
					break;
				}
			}
		} catch (NumberFormatException nfx) {
		}
		if (!rc) {
			for (Iterator it = keySet().iterator(); it.hasNext();) {
				String name = (String) (it.next());
				if (name.startsWith(shorthand)) {
					lastCode = ((OptionCodeMap.Entry) (get(name))).getCode();
					rc = true;
					// System.out.println("OptionCodeMap.hasCodeFor(): Found " +
					// name + " with code " + lastCode);
					break;
				}
			}
		}
		return rc;
	}

	/**
	 * Get the code of the most recent match checked by the method hasCodeFor().
	 * 
	 * @return the code which corresponds to that entry whose key has most
	 *         recently been checked.
	 */
	public long getCode() {
		return lastCode;
	}

	/**
	 * Enter a description and its corresponding option code into the map. The
	 * map preserves insertion order.
	 * 
	 * @param description
	 *            a regular expression string which describes a pattern
	 *            associated with the given code.
	 * @param code
	 *            the option code assiciated with the given regular expression
	 *            string.
	 */
	public void put(String name, long code, String description) {
		put(name, new OptionCodeMap.Entry(code, description));
	}

	public String toString() {
		StringBuilder s = new StringBuilder(200);
		s.append("\n" + title + "\n");
		for (Iterator it = keySet().iterator(); it.hasNext();) {
			String name = (String) (it.next());
			OptionCodeMap.Entry e = (OptionCodeMap.Entry) (get(name));
			s.append("   " + StringExt.fixedWidth(20, name));
			if (showCodes)
				s.append("\t[" + e.getCode() + "]");
			s.append("\t" + e.getDescription() + "\n");
		}
		return s.toString();
	}
	private class Entry {
		private long code;
		private String description;

		public Entry(long c, String d) {
			code = c;
			description = d;
		}

		public long getCode() {
			return code;
		}

		public String getDescription() {
			return description;
		}
	}
}
