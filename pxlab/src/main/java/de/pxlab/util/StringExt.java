package de.pxlab.util;

import java.text.*;
import java.util.Locale;
import java.text.NumberFormat;
import java.util.StringTokenizer;

import java.util.ArrayList;

/**
 * Some string extension methods.
 * 
 * @version 0.1.11
 */
/*
 * 
 * 06/25/03 getTextParagraph(): take care that two successive \n characters are
 * treated as an empty line.
 * 
 * 07/29/03 pathList()
 * 
 * 2006/11/12
 */
public class StringExt {
	/**
	 * Parse the given string containing newline characters into an array of
	 * strings.
	 * 
	 * @param s
	 *            a string which may contain newline characters.
	 * @return an array of strings with every single string corresponding to a
	 *         single line of the input string argument.
	 */
	public static ArrayList getTextParagraph(String s) {
		// System.out.println(s);
		ArrayList a = new ArrayList();
		StringTokenizer st = new StringTokenizer(s, "\n", true);
		String lastToken = "";
		String token;
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if (token.equals("\n")) {
				if (token.equals(lastToken)) {
					a.add(new String(""));
				} else {
					// ignore single newline chars
				}
			} else {
				// System.out.println(ss);
				a.add(token);
			}
			lastToken = token;
		}
		// System.out.println(a.size());
		return (a);
	}

	public static int[] getPageIndex(String[] text, String pageMark) {
		ArrayList a = new ArrayList(50);
		int n = text.length;
		int i = 0;
		while (i < n) {
			while (i < n) {
				if (nonEmpty(text[i])) {
					break;
				} else {
					i++;
				}
			}
			a.add(new Integer(i));
			i++;
			while (i < n) {
				if (text[i].startsWith(pageMark)) {
					break;
				} else {
					i++;
				}
			}
			a.add(new Integer(i - 1));
			i++;
		}
		int[] p = new int[a.size()];
		for (int j = 0; j < p.length; j++) {
			p[j] = ((Integer) (a.get(j))).intValue();
		}
		return p;
	}

	/*
	 * public static ArrayList getTextParagraph(String s) { //
	 * System.out.println(s); ArrayList a = new ArrayList(); int i0 = 0; int i1;
	 * 
	 * i1 = s.indexOf('\n');
	 * 
	 * StringTokenizer st = new StringTokenizer(s, "\n", true); while
	 * (st.hasMoreTokens()) { String ss = st.nextToken().replace('\n', ' '); //
	 * System.out.println(ss); a.add(ss); } // System.out.println(a.size());
	 * return(a); }
	 */
	/**
	 * Create a text paragraph by breaking the input text into lines according
	 * to the given FontMetrics and the requested width.
	 * 
	 * @param text
	 *            the text which should be broken into lines. The string should
	 *            not contain newline characters since these are ignored.
	 * @param fm
	 *            the FontMetrics which should be used for line measuring.
	 * @param parWidth
	 *            the intended maximum width for every single line.
	 * @return an array of strings which when drawn with the given FontMetrics
	 *         fills the intended width.
	 */
	public static ArrayList getTextParagraph(String text,
			java.awt.FontMetrics fm, int parWidth) {
		// System.out.println(text);
		StringBuffer line = new StringBuffer("");
		ArrayList paragraph = new ArrayList();
		int lineWidth = 0;
		int n = 0;
		StringTokenizer st = new StringTokenizer(text);
		int space = fm.stringWidth(" ");
		while (st.hasMoreTokens()) {
			String word = st.nextToken();
			int w = fm.stringWidth(word);
			if (n == 0) {
				line = new StringBuffer(word);
				n = 1;
				lineWidth = w;
			} else {
				if ((lineWidth + space + w) < parWidth) {
					line.append(" " + word);
					lineWidth += (space + w);
					n++;
				} else {
					// emit a line
					paragraph.add(line.toString());
					// System.out.println("adding: " + line.toString());
					n = 1;
					line = new StringBuffer(word);
					lineWidth = w;
				}
			}
		}
		paragraph.add(line.toString());
		// System.out.println("adding: " + line.toString());
		return (paragraph);
	}

	/**
	 * Convert a string into an array of strings such that every element
	 * corresponds to a word of the string. This may be used to convert a
	 * command line into an args[] array.
	 * 
	 * @param cmdLine
	 *            a string containing a sequence of 'words' separated by blank
	 *            characters.
	 * @return an array of strings which correspond to the single 'words' of the
	 *         input string or null if the input string is null.
	 */
	public static String[] stringArrayOfString(String cmdLine) {
		String[] args = null;
		if (cmdLine != null) {
			ArrayList argList = new ArrayList();
			StringTokenizer st = new StringTokenizer(cmdLine);
			while (st.hasMoreTokens()) {
				argList.add(st.nextToken());
			}
			args = stringArrayOfList(argList);
		}
		return args;
	}

	/**
	 * Convert the given ArrayList to an array of strings.
	 * 
	 * @param a
	 *            an ArrayList which contains a list of strings.
	 * @return an array of the strings contained in the argument.
	 */
	public static String[] stringArrayOfList(ArrayList a) {
		String[] s = new String[a.size()];
		for (int i = 0; i < a.size(); i++) {
			s[i] = (String) a.get(i);
			// System.out.println(i + ": " + s[i]);
		}
		return (s);
	}

	/**
	 * Return the index of the first position of string n in the array a.
	 * 
	 * @param n
	 *            the string whose index should be found.
	 * @param a
	 *            the array of strings where to look for.
	 * @return the index of n in a or (-1) if n is not contained in a.
	 */
	public static int indexOf(String n, String[] a) {
		if (a != null) {
			int m = a.length;
			for (int i = 0; i < m; i++) {
				if (n.equals(a[i])) {
					return (i);
				}
			}
		}
		return (-1);
	}

	public static int indexOf(int n, int[] a) {
		if (a != null) {
			int m = a.length;
			for (int i = 0; i < m; i++) {
				if (n == a[i]) {
					return (i);
				}
			}
		}
		return (-1);
	}

	/**
	 * Remove the first occurence of substring t from the string s.
	 */
	public static String remove(String s, String t) {
		int i = s.indexOf(t);
		if (i < 0) {
			return s;
		}
		int tl = t.length();
		if (i == 0) {
			return s.substring(tl);
		}
		if (i == (s.length() - tl)) {
			return s.substring(0, i);
		}
		return s.substring(0, i) + s.substring(i + tl);
	}

	/**
	 * Replace the first substring t of the string s by the replacement string
	 * r.
	 */
	public static String replace(String s, String t, String r) {
		int i = s.indexOf(t);
		if (i < 0) {
			return s;
		}
		int tl = t.length();
		if (i == 0) {
			return (r + s.substring(tl));
		}
		if (i == (s.length() - tl)) {
			return (s.substring(0, i) + r);
		}
		return (s.substring(0, i) + r + s.substring(i + tl));
	}

	public static boolean nonEmpty(String s) {
		return ((s != null) && (s.length() > 0));
	}

	/**
	 * Sort the given String array lexicographically.
	 * 
	 * @param arr
	 *            array of strings which is required to be sorted.
	 * @return the same array but sorted lexicographically.
	 */
	public static String[] sort(String[] arr) {
		String temp;
		int pos;
		for (int j = 0; j < arr.length; j++) {
			pos = j;
			for (int i = j + 1; i < arr.length; i++)
				if (arr[i].compareTo(arr[pos]) < 0)
					pos = i;
			temp = arr[j];
			arr[j] = arr[pos];
			arr[pos] = temp;
		}
		return arr;
	}

	/**
	 * Unquote the given string and replace escape sequences by the original
	 * characters.
	 * 
	 * @param s
	 *            the string to be unquoted.
	 * @return a string with quotes removed and escape sequences replaced by the
	 *         respective character codes.
	 */
	public static String unquote(String s) {
		char[] in = s.toCharArray();
		char[] out = new char[in.length];
		boolean inEscape = false;
		int k = 0;
		int n = in.length - 1;
		// Starting with i=1 and going up to n-1 removes the quotes
		for (int i = 1; i < n; i++) {
			if (inEscape) {
				switch (in[i]) {
				case 'n':
					out[k++] = '\n';
					break;
				case 't':
					out[k++] = '\t';
					break;
				case 'b':
					out[k++] = '\b';
					break;
				case 'r':
					out[k++] = '\r';
					break;
				case 'f':
					out[k++] = '\f';
					break;
				case '\\':
					out[k++] = '\\';
					break;
				case '\'':
					out[k++] = '\'';
					break;
				case '\"':
					out[k++] = '\"';
					break;
				default:
					out[k++] = '\\';
					out[k++] = in[i];
					break;
				}
				inEscape = false;
			} else {
				if (in[i] == '\\') {
					inEscape = true;
				} else {
					out[k++] = in[i];
				}
			}
		}
		// System.out.println("Input: " + s);
		// System.out.println("Output: " + new String(out, 0, k));
		return (new String(out, 0, k));
	}

	/**
	 * Unquote the given string and replace Java escape sequences and HTML
	 * escape character codes by the original characters.
	 * 
	 * @param s
	 *            the string to be unquoted.
	 * @return a string with quotes removed and Java and HTML escape sequences
	 *         replaced by the respective character codes.
	 */
	public static String unquoteHTML(String s) {
		String t = unquote(s);
		try {
			byte[] c = new byte[1];
			c[0] = (byte) 228;
			t = replace(t, "&auml;", new String(c, "ISO-8859-1"));
			c[0] = (byte) 246;
			t = replace(t, "&ouml;", new String(c, "ISO-8859-1"));
			c[0] = (byte) 252;
			t = replace(t, "&uuml;", new String(c, "ISO-8859-1"));
			c[0] = (byte) 196;
			t = replace(t, "&Auml;", new String(c, "ISO-8859-1"));
			c[0] = (byte) 214;
			t = replace(t, "&Ouml;", new String(c, "ISO-8859-1"));
			c[0] = (byte) 220;
			t = replace(t, "&Uuml;", new String(c, "ISO-8859-1"));
			c[0] = (byte) 223;
			t = replace(t, "&szlig;", new String(c, "ISO-8859-1"));
		} catch (java.io.UnsupportedEncodingException uex) {
		}
		return t;
	}

	/**
	 * Quote the given string by enclosing it between quote characters and
	 * replace unprintable characters by escape sequences.
	 * 
	 * @param s
	 *            the string to be quoted.
	 * @return a string which is a quoted representation of the input string.
	 */
	public static String quote(String s) {
		char[] in = s.toCharArray();
		int n = in.length;
		StringBuffer out = new StringBuffer(n);
		out.append("\"");
		for (int i = 0; i < n; i++) {
			switch (in[i]) {
			case '\n':
				out.append("\\n");
				break;
			case '\t':
				out.append("\\t");
				break;
			case '\b':
				out.append("\\b");
				break;
			case '\r':
				out.append("\\r");
				break;
			case '\f':
				out.append("\\f");
				break;
			case '\\':
				out.append("\\\\");
				break;
			case '\'':
				out.append("\\\'");
				break;
			case '\"':
				out.append("\\\"");
				break;
			default:
				out.append(new String(in, i, 1));
				break;
			}
		}
		out.append("\"");
		// System.out.println("Input: " + s);
		// System.out.println("Output: " + out.toString());
		return (out.toString());
	}
	private static NumberFormat hexFormat = NumberFormat.getInstance(Locale.US);
	static {
		hexFormat.setMinimumIntegerDigits(2);
	}

	/**
	 * Convert a given string into HTTP encoded format.
	 * 
	 * @param st
	 *            the string to be encoded.
	 * @return a string which is encoded for HTTP transfer.
	 */
	public static String HTTPString(String st) {
		char[] s = st.toCharArray();
		StringBuffer sb = new StringBuffer(1000);
		int n = s.length;
		for (int i = 0; i < n; i++) {
			char c = s[i];
			if ((c > 31) && (c < 127)) {
				switch (c) {
				case ';':
				case '/':
				case '#':
				case '?':
				case ':':
				case '+':
				case '&':
				case '>':
				case '<':
				case '=':
				case '%':
					sb.append(escapedChar(c));
					break;
				case ' ':
					sb.append("+");
					break;
				default:
					sb.append(new String(s, i, 1));
					break;
				}
			} else if (c == '\n') {
				sb.append(escapedChar('\r'));
				sb.append(escapedChar('\n'));
			} else {
				sb.append(escapedChar(c));
			}
		}
		// System.out.println("StringExt.HTTPString(): \n" + sb);
		return (sb.toString());
	}

	private static String escapedChar(char c) {
		char[] s = new char[3];
		s[0] = '%';
		int x1 = c / 16;
		int x0 = c % 16;
		s[1] = (char) ((x1 > 9) ? ('A' + (x1 - 10)) : ('0' + x1));
		s[2] = (char) ((x0 > 9) ? ('A' + (x0 - 10)) : ('0' + x0));
		return (new String(s));
	}

	/**
	 * Return an ArrayList of all paths contained int the path list given as an
	 * argument.
	 * 
	 * @param p
	 *            a list of path names deparated byte the path separator
	 *            character.
	 * @return an ArrayList which contains a list of path strings.
	 */
	public static ArrayList pathList(String p) {
		ArrayList a = new ArrayList();
		if (nonEmpty(p)) {
			StringTokenizer st = new StringTokenizer(p,
					System.getProperty("path.separator"));
			while (st.hasMoreTokens()) {
				a.add(st.nextToken());
			}
		}
		return (a);
	}

	public static String valueOf(String[] a) {
		StringBuffer s = new StringBuffer(30);
		s.append("[");
		if (a != null) {
			if (a.length > 0)
				s.append(" " + a[0]);
			for (int i = 1; i < a.length; i++)
				s.append(", " + a[i]);
		}
		s.append("]");
		return s.toString();
	}

	public static String valueOf(boolean[] a) {
		StringBuffer s = new StringBuffer(30);
		s.append("[");
		if (a != null) {
			for (int i = 0; i < a.length; i++)
				s.append(a[i] ? "|" : "o");
		}
		s.append("]");
		return s.toString();
	}

	public static String valueOf(int[] a) {
		StringBuffer s = new StringBuffer(30);
		s.append("[");
		if (a != null) {
			if (a.length > 0)
				s.append(" " + String.valueOf(a[0]));
			for (int i = 1; i < a.length; i++)
				s.append(", " + String.valueOf(a[i]));
		}
		s.append("]");
		return s.toString();
	}

	public static String valueOf(double[] a) {
		return valueOf(a, 6);
	}

	public static String valueOf(double[] a, int maxFraction) {
		StringBuffer s = new StringBuffer(30);
		NumberFormat n = NumberFormat.getInstance(Locale.US);
		n.setMaximumFractionDigits(maxFraction);
		n.setGroupingUsed(false);
		s.append("[");
		if (a != null) {
			if (a.length > 0)
				s.append(" " + n.format(a[0]));
			for (int i = 1; i < a.length; i++)
				s.append(", " + n.format(a[i]));
		}
		s.append("]");
		return s.toString();
	}

	public static String valueOf(double a, int maxFraction) {
		NumberFormat n = NumberFormat.getInstance(Locale.US);
		n.setMaximumFractionDigits(maxFraction);
		n.setGroupingUsed(false);
		return n.format(a);
	}

	public static double doubleValue(String s, double def) {
		double x = def;
		try {
			x = Double.valueOf(s).doubleValue();
		} catch (NumberFormatException nfx) {
		}
		return x;
	}

	/**
	 * Convert a string into an array of double values. The string may contain a
	 * series of numbers. Semicolons are allowed to separate the numbers and
	 * square brackets are allowed to enclose a series of numbers.
	 * 
	 * @param s
	 *            the input string
	 * @param def
	 *            a default double value which is put into the array if a format
	 *            conversion exception is thrown.
	 * @return an array of double values.
	 */
	public static double[] doubleArray(String s, double def) {
		ArrayList n = new ArrayList(100);
		StringTokenizer st = new StringTokenizer(s, " \t,[]");
		while (st.hasMoreTokens()) {
			n.add(st.nextToken());
		}
		int m = n.size();
		double[] p = new double[m];
		for (int j = 0; j < m; j++) {
			try {
				p[j] = Double.valueOf((String) n.get(j)).doubleValue();
			} catch (NumberFormatException nfx) {
				p[j] = def;
			}
		}
		return p;
	}

	public static int intValue(String s, int def) {
		int x = def;
		try {
			x = Integer.valueOf(s).intValue();
		} catch (NumberFormatException nfx) {
		}
		return x;
	}

	/**
	 * Return a fixed length string.
	 * 
	 * @param n
	 *            length of the string.
	 * @param s
	 *            left aligned content of the string.
	 * @return a string which has a fixed length and contains the argument
	 *         string left aligned. The rest is filled with blanks.
	 */
	public static String fixedWidth(int n, String s) {
		StringBuilder sb = new StringBuilder(s);
		while (sb.length() < n)
			sb.append(' ');
		return sb.toString();
	}

	/**
	 * Create an error message based on the parser's error message.
	 * 
	 * @param pex
	 *            the parse exception
	 * @param fn
	 *            the name of the file which had been parsed.
	 * @return an error message
	 */
	public static String fixedParserErrorMessage(Throwable pex, String fn) {
		String m = pex.getMessage();
		if (m == null) {
			return "Unknown Syntax Error in File " + fn;
		} else {
			return "Syntax Error in File " + fn + "\n"
					+ m.replace((char) 13, ' ').replace((char) 10, ' ');
		}
	}
}
