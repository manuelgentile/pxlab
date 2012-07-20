package de.pxlab.util;

import java.io.*;
import java.util.*;

/**
 * A TextCache is a text file which contains key-string pairs. The keys are
 * strings which have the keyLeader character as its first character. The string
 * associated with a key is separated from the key by the keySeparator
 * character. The default value for keyLeader is an ampersand '@' and the
 * default for the keySeparator is the equals sign '='. Everything following the
 * keySeparator up to the next keyLeader or the end of the text file is the text
 * string. An exception is that blank characters at the end of a string element
 * are removed and any new line character in the text file is replaced by a
 * blank. The key-string pairs are cached in memory after instantiation and the
 * strings my be retrieved by using the respective keys.
 * 
 * @author Hans Irtel
 * @version 0.1.1
 */
/*
 * 
 * 07/05/02 an empty file name does nothing.
 */
public class TextCache extends Properties {
	private String fileName;

	/**
	 * Create a TextCache from the given file using '@' as a keyLeader, '=' as
	 * the keySeparator, and '_' as blank characters.
	 * 
	 * @param file
	 *            the name of the text file containing the key-string pairs.
	 */
	public TextCache(String file) {
		this(file, '@', '=', '_');
	}

	/**
	 * Create a TextCache from the given file using the given keyLeader and
	 * keySeparator characters.
	 * 
	 * @param file
	 *            the name of the text file containing the key-string pairs.
	 * @param keyLeader
	 *            the character that introduces key strings.
	 * @param keySeparator
	 *            the character that separates keys and their associated
	 *            strings.
	 */
	public TextCache(String file, char keyLeader, char keySeparator,
			char blankChar) {
		if (StringExt.nonEmpty(file)) {
			fileName = file;
			try {
				StringBuffer strBuff = new StringBuffer(5000);
				BufferedReader buff = new BufferedReader(new FileReader(file));
				String str;
				while ((str = buff.readLine()) != null)
					strBuff.append(str + " ");
				buff.close();
				char[] delimiter = { keyLeader, keySeparator };
				boolean isKey = false;
				String key = null;
				for (StringTokenizer st = new StringTokenizer(
						strBuff.toString(), new String(delimiter), true); st
						.hasMoreElements();) {
					String token = st.nextToken();
					if (token.indexOf(keyLeader) >= 0) {
						isKey = true;
					} else if (token.indexOf(keySeparator) >= 0) {
						isKey = false;
					} else if (isKey) {
						key = token;
					} else {
						if (put(key, trailingBlanksRemoved(token)) != null) {
							System.out
									.println("Duplicate keys in TextCache file: "
											+ key);
						}
					}
				}
			} catch (FileNotFoundException e) {
				System.err.println("TextCache(): File " + fileName + ": " + e);
			} catch (IOException e) {
				System.err.println("TextCache(): File " + fileName + ": " + e);
			}
		}
	}

	public String trailingBlanksRemoved(String s) {
		int n = s.length() - 1;
		int i = n;
		while ((i >= 0) && (s.charAt(i) <= ' '))
			i--;
		return ((i == n) ? s : (s.substring(0, i + 1)));
	}

	/**
	 * Get the name of the file which generated this TextCache object.
	 */
	public String getFileName() {
		return (fileName);
	}

	/** Get the text segement associated with the given key. */
	public String getTextSegment(String key) {
		return (getProperty(key));
	}
}
