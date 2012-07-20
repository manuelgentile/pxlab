package de.pxlab.pxl;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Converts an Iterator into an Eumeration as it is neede for the implementation
 * of TreeNode.
 */
public class EnumerationAdapter implements Enumeration {
	private Iterator iterator;

	public EnumerationAdapter(Iterator it) {
		iterator = it;
	}

	public boolean hasMoreElements() {
		return (iterator.hasNext());
	}

	public Object nextElement() {
		return (iterator.next());
	}
}
