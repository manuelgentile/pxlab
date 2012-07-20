/*
 * element.java
 *
 * Created on January 12, 2003, 6:28 PM
 */
package com.elementengine.xml;

import java.util.StringTokenizer;
import java.util.Hashtable;

/**
 * 
 * @author Carmen Delessio carmen@elementengine.com-
 */
/*
 * 
 * H. I. 2006/02/21 replace both 0x0D and 0x0A instead of '\n'
 */
public class Element {
	String tagName = null;
	String elementString = null;
	String value = null;
	int nestLevel = 0;
	Hashtable requestedAttributes = new Hashtable();

	public Element() {
	}

	public Element(String name, String elementValue, int nestLevelValue) {
		tagName = name;
		elementString = elementValue;
		nestLevel = nestLevelValue;
	}

	public String getTagName() {
		return tagName;
	}

	public int getNestLevel() {
		return nestLevel;
	}

	public String getValue() {
		if (value != null) {
			return value;
		} else {
			int offset = elementString.indexOf(">");
			if (offset > 0) {
				int offset2 = elementString.indexOf("</");
				if (offset2 > 0) {
					return (elementString.substring(offset + 1, offset2));
				} else {
					value = (elementString.substring(offset + 1));
				}
			} else {
				value = elementString;
			}
		}
		return value;
	}

	public String getAttribute(String name) {
		// System.out.println("------Element.getAttribute() from \n     " +
		// elementString);
		// System.out.println("->Element.getAttribute(" + name + ") = ");
		if (requestedAttributes.containsKey(name)) {
			// System.out.println(" ->> " + (String)
			// requestedAttributes.get(name));
			return (String) requestedAttributes.get(name);
		}
		int i;
		StringTokenizer t = new StringTokenizer(elementString, "\"=", true);
		int numTokens = t.countTokens();
		String[] tokens = new String[numTokens];
		int numFixed = 0;
		String[] fixedTokens = new String[numTokens]; // max will be same as
														// numTokens
		// remove any extra white space tokens and leading spaces
		if (numTokens > 0) {
			for (i = 0; i < numTokens; i++) {
				tokens[i] = t.nextToken();
				// System.out.println(" ->> Looking at " + tokens[i]);
				// H. I. 2006/02/21 replace both 0x0D and 0x0A instead of '\n'
				tokens[i] = tokens[i].replace((char) 0x0D, ' '); // suppress
																	// newline
				tokens[i] = tokens[i].replace((char) 0x0A, ' '); // suppress
																	// newline
				tokens[i] = tokens[i].replace('\t', ' '); // suppress tab
				// System.out.println(" ->- Looking at " + tokens[i]);
				if (tokens[i].equals(" ")) {
					continue; // with for loop
				}
				while ((tokens[i].charAt(0) == ' ')) { // remove leading space
					tokens[i] = tokens[i].substring(1);
					if (tokens[i].equals(" ")) {
						break; // out of while
					}
				} // while spaces
					// System.out.println(" ->>> Looking at " + tokens[i]);
				int len = tokens[i].length(); // remove trailing spaces
				while (tokens[i].charAt(len - 1) == ' ') {
					tokens[i] = tokens[i].substring(0, len - 1);
					len--;
				}
				// System.out.println(" ->>>> Looking at " + tokens[i]);
				if (tokens[i].equals(" ")) {
					// System.out.println("blank token");
				} else {
					// System.out.println(" ->>> token fixed: " + tokens[i]);
					fixedTokens[numFixed] = tokens[i];
					numFixed++;
				}
			} // end for
			for (i = 0; i < numFixed; i++) {
				if (fixedTokens[i].equals(name)) {
					if (fixedTokens[i + 1].equals("=")
							&& fixedTokens[i + 2].equals("\"")
							&& fixedTokens[i + 4].equals("\"")) {
						requestedAttributes.put(name, fixedTokens[i + 3]);
						// System.out.println(fixedTokens[i+3]);
						return (fixedTokens[i + 3]);
					} else {
						// System.out.println("");
						return ("");
					}
				}
			} // for i
		}
		// System.out.println("");
		return ("");
	}// getAttribute

	public boolean hasAttribute(java.lang.String name) {
		String testValue = getAttribute(name);
		if (testValue != "") {
			requestedAttributes.put(name, testValue);
			return true;
		} else {
			return false;
		}
	}

	public String toString() {
		int i;
		String tabs = " ";
		String returnString;
		for (i = 0; i < nestLevel; i++) {
			tabs = tabs + "\t";
		}
		return tabs + tagName + " " + elementString;
	}
}
