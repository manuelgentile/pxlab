package de.pxlab.pxl;

/**
 * Describes ExPar object values, their accessible instance names, and their
 * preferred editor.
 * 
 * @author H. Irtel
 * @version 0.2.2
 * 
 *          06/02/02/ added setName(..) and seValue(..) (er)
 * 
 *          09/16/03 added getFieldName() to get the class's parameter name
 *          without instance prefix.
 */
public class ExParDescriptor {
	/** Describes a parameter which cannot be edited. */
	public static final int NO_EDIT = 0;
	/** Describes a parameter which is edited by the color editor. */
	public static final int COLOR_EDIT = 1;
	/**
	 * Describes a parameter which is edited by the geometry editor.
	 */
	public static final int GEOMETRY_EDIT = 2;
	/** Describes a parameter which is edited by the timing editor. */
	public static final int TIMING_EDIT = 3;
	/**
	 * Describes a parameter which is edited by the (yet unknown) system editor.
	 */
	public static final int SYSTEM_EDIT = 4;
	private String name;
	private ExPar value;
	private int editorType = NO_EDIT;

	/**
	 * Instantiate an ExParDescriptor object for the given named parameter.
	 * 
	 * @param n
	 *            the full instance name of the given ExPar
	 * @param p
	 *            the ExPar to be described.
	 */
	public ExParDescriptor(String n, ExPar p) {
		name = n;
		value = p;
	}

	/**
	 * Instantiate an ExParDescriptor object for the given named parameter.
	 * 
	 * @param n
	 *            the full instance name of the given ExPar
	 * @param p
	 *            the ExPar to be described.
	 * @param t
	 *            the type of editor to be used for this experimental parameter.
	 */
	public ExParDescriptor(String n, ExPar p, int t) {
		name = n;
		value = p;
		editorType = t;
	}

	/**
	 * Return the full instance name of this descriptor. This includes the
	 * DisplayList and Display prefix parts if they exist.
	 */
	public String getName() {
		return (name);
	}

	/**
	 * Return the experimental parameter name without any DisplayList or Display
	 * prefix.
	 */
	public String getFieldName() {
		int k = name.lastIndexOf(".");
		if (k < 0) {
			return name;
		} else {
			return name.substring(k + 1);
		}
	}

	/** Return the ExPar object described by this descriptor. */
	public ExPar getValue() {
		return (value);
	}

	public void setName(String n) {
		name = n;
	}

	public void setValue(ExPar xp) {
		value = xp;
	}

	public int getEditorType() {
		return (editorType);
	}

	public void setEditorType(int t) {
		editorType = t;
	}

	public String toString() {
		return (name + "=" + value + " [t=" + editorType + "]");
	}
}
