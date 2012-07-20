package de.pxlab.pxl.parser;

/**
 * An object which identifies a runtime display or display list instance. The
 * identifier contains a class name and an instance modifier.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class DisplayIdentifier {
	public int beginLine = 0;
	public int beginColumn = 0;
	public String className;
	public String instanceModifier;
	protected String comment = null;

	public DisplayIdentifier(String cls, String n, int line, int col) {
		this(cls, n);
		beginLine = line;
		beginColumn = col;
	}

	public DisplayIdentifier(String cls, String n) {
		className = cls;
		instanceModifier = n;
	}

	public String getClassName() {
		return className;
	}

	public String getInstanceModifier() {
		return instanceModifier;
	}

	public String toString() {
		StringBuffer b = new StringBuffer(40);
		b.append(className);
		if (instanceModifier != null) {
			b.append(":");
			b.append(instanceModifier);
		}
		return b.toString();
	}

	public static DisplayIdentifier valueOf(String n) {
		int i = n.indexOf(':');
		if (i > 0) {
			return new DisplayIdentifier(n.substring(0, i), n.substring(i + 1));
		} else {
			return new DisplayIdentifier(n, null);
		}
	}

	public boolean hasComment() {
		return comment != null;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String s) {
		if (comment != null) {
			comment = comment + "\n" + s;
		} else {
			comment = s;
		}
	}
}
