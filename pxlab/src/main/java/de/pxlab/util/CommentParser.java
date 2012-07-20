package de.pxlab.util;

import java.io.*;

/**
 * Extract special comments from a text file. Special comments are introduced by
 * the <code>startSpecial</code> string and are ebded by the
 * <code>endSpecial</code> string.
 * 
 * @author hans Irtel
 * @version 0.1.0
 */
public class CommentParser {
	private static final String startSpecialDefault = "/*@";
	private static final String endSpecialDefault = "*/";
	private String startSpecial;
	private String endSpecial;
	private String fileBuffer;

	/**
	 * Create a comment parser for the given reader using the default starting
	 * and ending string. The default starting string is a C-style comment start
	 * sequence (a slash followed by a star) followed by an apmersand character.
	 * The default ending string is the standard C-style comment ending string.
	 * 
	 * @param source
	 *            the Reader from where to get the characters to read.
	 */
	public CommentParser(Reader source) {
		this(source, startSpecialDefault, endSpecialDefault);
	}

	/**
	 * Create a comment parser for the given reader using the given starting
	 * string and the default ending string. The default ending string is the
	 * standard C-style comment ending string.
	 * 
	 * @param source
	 *            the Reader from where to get the characters to read.
	 * @param start
	 *            a string which introduces special comments.
	 */
	public CommentParser(Reader source, String start) {
		this(source, start, endSpecialDefault);
	}

	/**
	 * Create a comment parser for the given reader using the given starting and
	 * ending strings.
	 * 
	 * @param source
	 *            the Reader from where to get the characters to read.
	 * @param start
	 *            a string which introduces special comments.
	 * @param end
	 *            a string which ends special comments.
	 */
	public CommentParser(Reader source, String start, String end) {
		startSpecial = start;
		endSpecial = end;
		StringBuffer b = new StringBuffer(1000);
		int c;
		try {
			while ((c = source.read()) != -1) {
				b.append((char) c);
			}
			source.close();
			fileBuffer = b.toString();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}

	/**
	 * Get the collected special comments of this parser.
	 * 
	 * @return a String which contains all concatenated comments of this
	 *         parser's source file.
	 */
	public String getSpecialComments() {
		String a = fileBuffer;
		// StringBuffer b = new StringBuffer("<!-- CommentParser -->\n");;
		StringBuffer b = new StringBuffer(1000);
		;
		int i1, i2;
		while ((i1 = a.indexOf(startSpecial)) >= 0) {
			i1 += startSpecial.length();
			i2 = a.indexOf(endSpecial, i1);
			if (i2 < 0) {
				System.out.println("CommentParser.parseString(): "
						+ "Comment not closed.");
				return (null);
			} else {
				b.append(a.substring(i1, i2));
				if (i2 < a.length()) {
					a = a.substring(i2);
				} else {
					break;
				}
			}
		}
		return (b.toString());
	}

	/**
	 * Get the description comment in the source file of the given class. White
	 * space at the beginning and end of lines is removed.
	 */
	public static String getDescriptionFromSource(Class cls) {
		String fn = cls.getName();
		fn = "/jpxl/src/share/classes/" + fn.replace('.', '/') + ".java";
		String cmt = "";
		StringBuffer b = new StringBuffer(10000);
		try {
			BufferedReader bfr = new BufferedReader(new FileReader(fn));
			String ln;
			while ((ln = bfr.readLine()) != null) {
				String lnz = ln.trim();
				b.append(lnz + "\n");
			}
			bfr.close();
			String bs = b.toString();
			int i1 = bs.indexOf("/**");
			int i2 = bs.indexOf("*/", i1);
			if ((i1 > 0) && (i2 > 0)) {
				cmt = bs.substring(i1 + 4, i2);
			}
		} catch (IOException ioex) {
			cmt = "No comment found for " + fn;
		}
		return cmt;
	}

	/**
	 * Get the description comment in the source file of the given class. White
	 * space at the beginning and end of lines is removed.
	 */
	public static String getDescriptionFromHTML(Class cls) {
		String fn = cls.getName();
		fn = "/jpxl/html/" + fn.replace('.', '/') + ".html";
		String cmt = "";
		StringBuffer b = new StringBuffer(10000);
		int state = 0;
		try {
			BufferedReader bfr = new BufferedReader(new FileReader(fn));
			String ln;
			while ((ln = bfr.readLine()) != null) {
				if (state == 1) {
					if (ln.startsWith("<DL>")) {
						break;
					}
					String lnz = StringExt.remove(ln, "<p>");
					lnz = StringExt.remove(lnz, "<P>");
					lnz = StringExt.remove(lnz, "</p>");
					lnz = StringExt.remove(lnz, "</P>");
					b.append(lnz + "\n");
				}
				if (ln.startsWith("<DT>public class <B>")) {
					state = 1;
				}
			}
			bfr.close();
			cmt = b.toString();
		} catch (IOException ioex) {
			cmt = "No comment found for " + fn;
		}
		return cmt;
	}

	public static void main(String[] args) {
		// System.out.println(getDescriptionFromHTML(de.pxlab.pxl.display.GaborPattern.class));
	}
}
