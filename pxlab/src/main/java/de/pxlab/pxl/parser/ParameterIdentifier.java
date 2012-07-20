package de.pxlab.pxl.parser;

/**
 * An object which fully identifies an experimental parameter instance in the
 * runtime table of experimental parameters.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class ParameterIdentifier {
	public int beginLine = 0;
	public int beginColumn = 0;
	protected String displayListClass = null;
	protected String displayListName = null;
	protected String displayClass = null;
	protected String displayName = null;
	protected String param;

	public ParameterIdentifier(DisplayListIdentifier proc,
			DisplayIdentifier dlist, String p, int line, int col) {
		this(proc, dlist, p);
		beginLine = line;
		beginColumn = col;
	}

	public ParameterIdentifier(DisplayListIdentifier proc,
			DisplayIdentifier dlist, String p) {
		// System.out.print("ParameterIdentifier() input: ");
		if (proc != null) {
			// System.out.print(proc.toString());
			displayListClass = proc.getClassName();
			displayListName = proc.getInstanceModifier();
		}
		if (dlist != null) {
			// System.out.print("." + dlist.toString());
			displayClass = dlist.getClassName();
			displayName = dlist.getInstanceModifier();
		}
		// System.out.println("." + p);
		param = p;
		// System.out.println("ParameterIdentifier() created: " + toString());
	}

	public String toString() {
		StringBuffer b = new StringBuffer(100);
		if (displayListClass != null) {
			b.append(displayListClass);
			if (displayListName != null) {
				b.append(":");
				b.append(displayListName);
			}
			b.append(".");
		}
		if (displayClass != null) {
			b.append(displayClass);
			if (displayName != null) {
				b.append(":");
				b.append(displayName);
			}
			b.append(".");
		}
		b.append(param);
		return b.toString();
	}
}
