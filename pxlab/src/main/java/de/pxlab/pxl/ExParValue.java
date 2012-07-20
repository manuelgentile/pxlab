package de.pxlab.pxl;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.HashSet;
import java.util.Vector;
import java.util.Enumeration;
import java.io.StringReader;

import javax.swing.tree.TreeNode;
import javax.swing.tree.MutableTreeNode;

import de.pxlab.util.StringExt;
import de.pxlab.pxl.gui.RuntimeParseError;
import de.pxlab.pxl.parser.*;

/**
 * The class ExParValue captures experimental parameter values. Parameter values
 * may simultanously be integer, double, or string typed. All parameters are
 * treated as arrays and each parameter simultanously has its int, double, and
 * String representation. The class provides methods to create new parameter
 * values and to set parameter values. It also provides some simple binary
 * operations for experimental parameter values.
 * 
 * <p>
 * ExParValue objects may also represent expressions of ExParValue objects or
 * values of named ExPar objects. In case of expressions the binary operation
 * reference exParExpression is non-null. Before primitive values of an
 * ExParValue which represents an expression are retrieved the variable
 * needsEvaluation is checked whether this ExParValue has to be evaluated. This
 * means that its expression tree is evaluated and its primitive values reflect
 * the current value of the expression tree.
 * 
 * <p>
 * At any point in time an experimental parameter has only a single ExParValue
 * as its value. However this value may be hidden by another value which uses
 * its <code>next</code> field to preserve a pointer to the hidden value. This
 * mechanism is used to push parameter values whenever a lower hierarchy level
 * of a design file is activated.
 * 
 * @version 0.3.20
 */
/*
 * 01/26/01 added special device color support for PxlColor typed values
 * 
 * 02/02/01 fixed getDevColor()
 * 
 * 02/13/01 added setInt(), setString(), setDouble() methods
 * 
 * 02/23/01 fixed serious bug when creating ExParValue objects from Strings
 * 
 * 05/10/01 expressions are preserved during runtime now. Previously expressions
 * were evaluated and then replaced by the result.
 * 
 * 05/12/01 treat class constants in a similar way as experimental parameters in
 * order to preserve their names during runtime.
 * 
 * 05/14/01 allow for NonFatalErros on undefined parameter values.
 * 
 * 11/23/01 fixed toString() for condition operator values
 * 
 * 07/13/02 fixed bug for cases where an evaluation value was redefined to a
 * primitive value.
 * 
 * 02/04/03 fixed bug in isEmpty() method
 * 
 * 07/14/03 fixed bug in getClassConstant()
 * 
 * 07/29/03 had to remove the mechanism of the colorValueModified flag since it
 * is not compatible with changing the color device parameters while a program
 * is running.
 * 
 * 05/05/04 getPxlColor() now returns black in case of error.
 * 
 * 2005/01/13 added formatting character for parameter substitution.
 * 
 * 2005/02/15 added isNotSet() and setNotSet() methods.
 * 
 * 2005/05/15 fixed bug in not()
 * 
 * 2005/06/13 handle class constants more consistently and preserve their names
 * for arrays.
 * 
 * 2005/06/21 added 'i' formatting character
 * 
 * 2005/07/13 getValueForLanguage()
 * 
 * 2005/07/22 arrayOf()
 * 
 * 2006/01/12 isNumeric()
 * 
 * 2007/02/02 static methods runtimeParameterValue(String s), file(String dir,
 * String fn), assignableParameterValue(String s)
 */
public class ExParValue implements TreeNode, // MutableTreeNode,
		Cloneable, StringSubstitutionFormat {
	// Parameter value arrays
	private int i[];
	private double d[];
	private String s[];
	/**
	 * The length of the internal arrays for this parameter's primitive values.
	 */
	public int length = 0;
	/** Flag to indicate that this is an expansion array. */
	private boolean expansion = false;
	/**
	 * This flag indicates whether this ExParValue needs a call to the
	 * evaluation() method at runtime. This is the case if this ExParValue is an
	 * expression or a reference to another ExPar variable.
	 */
	protected boolean needsEvaluation = false;
	/**
	 * If this ExParValue has its needsEvaluation parameter set to true and this
	 * String is non-null then this ExParValue gets its actual value from
	 * another ExPar object. This is the name of the ExPar object where this
	 * ExParValue gets its value from.
	 */
	protected String valueParam = null;
	/**
	 * If this is non-null then this ExParValue is an array of class constants.
	 * The actual values are contained in the data arrays and this array
	 * contains the names of the class constants. Objects which have class
	 * constants as their value do not have the needsEvaluation parameter set!
	 * These names are only used for toString() methods.
	 */
	protected String[] classConstant = null;
	/**
	 * If this ExParValue is a class constant then this is the class of possible
	 * values.
	 */
	protected Class valueClass = null;
	/**
	 * A map of all known class constants contained int the PXLab package.
	 */
	private static ClassConstantMap classConstantMap = new ClassConstantMap();
	/**
	 * The expression represented by this node. If this object is non-null then
	 * this ExParValue represents an expression node.
	 */
	private ExParExpression exParExpression = null;
	/**
	 * This is the first operand ExParValue of an ExParValue which represents an
	 * expression.
	 */
	private ExParValue subValue1 = null;
	/**
	 * This is the second operand ExParValue of an ExParValue which represents
	 * an expression.
	 */
	private ExParValue subValue2 = null;
	/**
	 * This is the third operand ExParValue of an ExParValue which represents an
	 * expression.
	 */
	private ExParValue subValue3 = null;
	private TreeNode parent;

	/**
	 * Get a reference to the expression of this ExParValue.
	 * 
	 * @return an ExParExpression reference or null if this ExParValue is not an
	 *         expression.
	 */
	public ExParExpression getExParExpression() {
		return exParExpression;
	}

	/**
	 * Get an expression subnode value of this ExParValue in case this is an
	 * expression.
	 * 
	 * @param i
	 *            the index of the subnode. An expression may have 0, 1, 2, or 3
	 *            subnode ExParValue objects.
	 * @return the respective subnode ExParValue.
	 */
	public ExParValue getSubValue(int i) {
		if (i == 0) {
			return subValue1;
		} else if (i == 1) {
			return subValue2;
		} else {
			return subValue3;
		}
	}

	/**
	 * Get the symbolic parameter name whose value is the value of this
	 * ExParValue.
	 * 
	 * @return a symbolic parameter name or null if this ExParValue does not
	 *         have a symbolic name as its value.
	 */
	public String getValueParam() {
		return valueParam;
	}
	/** Flag to indicate that this is an undefined value. */
	private boolean undefined = false;
	/**
	 * This parameter is only relevant for ExParValue objects which represent
	 * color values. It serves to speed up device color conversion. If this
	 * parameter is true then this ExParValue object needs a recomputation of
	 * the device color coordinates in case this ExParValue is a color value.
	 */
	private boolean colorValueModified = false;
	/**
	 * A pointer to another ExParValue which is hidden by this one because it
	 * belongs to a lower level of the design file hierarchy. This pointer is
	 * only manipulated by the push() and pop() methods of the class ExPar.
	 */
	protected ExParValue next = null;
	public static final int TYPE_STRING = 0;
	public static final int TYPE_INT = 1;
	public static final int TYPE_DOUBLE = 2;
	public static final int TYPE_CLASS_CONSTANT = 3;
	/**
	 * Describes a conjecture about the preferred type of this ExParValue. It is
	 * derived from the most recent set() method and is used only for converting
	 * this ExParValue into a string.
	 */
	protected int typeConjecture = TYPE_STRING;
	private static NumberFormat doubleExParValue = NumberFormat
			.getInstance(Locale.US);
	static {
		doubleExParValue.setMaximumFractionDigits(4);
		doubleExParValue.setGroupingUsed(false);
	}

	protected ExParValue() {
		super();
	}

	/**
	 * Constructor for creating an ExParValue object which stores an ExParValue
	 * expression node.
	 * 
	 * @param exParExpression
	 *            the expression operation of this expression node.
	 */
	public ExParValue(ExParExpression exParExpression) {
		this(exParExpression, null, null, null);
	}

	/**
	 * Constructor for creating an ExParValue object which stores an ExParValue
	 * expression node.
	 * 
	 * @param exParExpression
	 *            the expression operation of this expression node.
	 * @param subValue1
	 *            the left operand of this binary operation.
	 */
	public ExParValue(ExParExpression exParExpression, ExParValue subValue1) {
		this(exParExpression, subValue1, null, null);
	}

	/**
	 * Constructor for creating an ExParValue object which stores an ExParValue
	 * expression node.
	 * 
	 * @param exParExpression
	 *            the expression operation of this expression node.
	 * @param subValue1
	 *            the left operand of this binary operation.
	 * @param subValue2
	 *            the right operand of this binary operation.
	 */
	public ExParValue(ExParExpression exParExpression, ExParValue subValue1,
			ExParValue subValue2) {
		this(exParExpression, subValue1, subValue2, null);
	}

	/**
	 * Constructor for creating an ExParValue object which stores an ExParValue
	 * expression node.
	 * 
	 * @param exParExpression
	 *            the expression operation of this expression node.
	 * @param subValue1
	 *            the 1st operand of this expression operation.
	 * @param subValue2
	 *            the 2nd operand of this expression operation.
	 * @param subValue3
	 *            the 3rd operand of this expression operation.
	 */
	public ExParValue(ExParExpression exParExpression, ExParValue subValue1,
			ExParValue subValue2, ExParValue subValue3) {
		this.exParExpression = exParExpression;
		this.subValue1 = subValue1;
		this.subValue2 = subValue2;
		this.subValue3 = subValue3;
		needsEvaluation = true;
	}

	/**
	 * Constructor with integer valued initialization.
	 * 
	 * @param x
	 *            initial integer value of this parameter value.
	 */
	public ExParValue(int x) {
		set(x);
	}

	/**
	 * Set integer parameter value.
	 * 
	 * @param x
	 *            integer value of this parameter value.
	 */
	public void set(int x) {
		if (length != 1)
			createPrimitiveValues(1);
		i[0] = x;
		d[0] = (double) x;
		s[0] = Integer.toString(x);
		typeConjecture = TYPE_INT;
	}

	/**
	 * Constructor with integer array valued initialization.
	 * 
	 * @param x
	 *            initial integer value at index 0 of this parameter value.
	 * @param y
	 *            initial integer value at index 1 of this parameter value.
	 */
	public ExParValue(int x, int y) {
		set(x, y);
	}

	/**
	 * Set integer array parameter value.
	 * 
	 * @param x
	 *            initial integer value at index 0 of this parameter value.
	 * @param y
	 *            initial integer value at index 1 of this parameter value.
	 */
	public void set(int x, int y) {
		if (length != 2)
			createPrimitiveValues(2);
		i[0] = x;
		d[0] = (double) x;
		s[0] = Integer.toString(x);
		i[1] = y;
		d[1] = (double) y;
		s[1] = Integer.toString(y);
		typeConjecture = TYPE_INT;
	}

	/**
	 * Constructor with integer array valued initialization.
	 * 
	 * @param x
	 *            initial integer array value of this parameter value.
	 */
	public ExParValue(int[] x) {
		set(x);
	}

	/**
	 * Set integer array parameter value.
	 * 
	 * @param x
	 *            integer array value of this parameter value.
	 */
	public void set(int[] x) {
		int n = x.length;
		if (length != n)
			createPrimitiveValues(n);
		for (int j = 0; j < n; j++) {
			i[j] = x[j];
			d[j] = (double) x[j];
			s[j] = Integer.toString(x[j]);
		}
		typeConjecture = TYPE_INT;
	}

	/**
	 * Constructor with double valued initialization.
	 * 
	 * @param x
	 *            initial double value of this parameter value.
	 */
	public ExParValue(double x) {
		set(x);
	}

	/**
	 * Set double parameter value.
	 * 
	 * @param x
	 *            initial double value of this parameter value.
	 */
	public void set(double x) {
		if (length != 1)
			createPrimitiveValues(1);
		i[0] = (int) Math.round(x);
		d[0] = x;
		s[0] = Double.toString(x);
		typeConjecture = TYPE_DOUBLE;
	}

	/**
	 * Constructor with double array valued initialization.
	 * 
	 * @param x
	 *            initial double value at index 0 of this parameter value.
	 * @param y
	 *            initial double value at index 1 of this parameter value.
	 */
	public ExParValue(double x, double y) {
		set(x, y);
	}

	/**
	 * Set double array parameter value.
	 * 
	 * @param x
	 *            initial double value at index 0 of this parameter value.
	 * @param y
	 *            initial double value at index 1 of this parameter value.
	 */
	public void set(double x, double y) {
		if (length != 2)
			createPrimitiveValues(2);
		i[0] = (int) Math.round(x);
		d[0] = x;
		s[0] = Double.toString(x);
		i[1] = (int) Math.round(y);
		d[1] = y;
		s[1] = Double.toString(y);
		typeConjecture = TYPE_DOUBLE;
	}

	/**
	 * Constructor with double array valued initialization.
	 * 
	 * @param x
	 *            initial double value at index 0 of this parameter value.
	 * @param y
	 *            initial double value at index 1 of this parameter value.
	 * @param z
	 *            initial double value at index 2 of this parameter value.
	 */
	public ExParValue(double x, double y, double z) {
		set(x, y, z);
	}

	/**
	 * Set double array parameter value.
	 * 
	 * @param x
	 *            initial double value at index 0 of this parameter value.
	 * @param y
	 *            initial double value at index 1 of this parameter value.
	 * @param z
	 *            initial double value at index 2 of this parameter value.
	 */
	public void set(double x, double y, double z) {
		if (length != 3)
			createPrimitiveValues(3);
		i[0] = (int) Math.round(x);
		d[0] = x;
		s[0] = Double.toString(x);
		i[1] = (int) Math.round(y);
		d[1] = y;
		s[1] = Double.toString(y);
		i[2] = (int) Math.round(z);
		d[2] = z;
		s[2] = Double.toString(z);
		typeConjecture = TYPE_DOUBLE;
		colorValueModified = true;
	}

	/**
	 * Constructor with double array valued initialization.
	 * 
	 * @param x
	 *            initial double array value of this parameter value.
	 */
	public ExParValue(double[] x) {
		set(x);
	}

	/**
	 * Set double array parameter value.
	 * 
	 * @param x
	 *            double array value of this parameter value.
	 */
	public void set(double[] x) {
		int n = x.length;
		if (length != n)
			createPrimitiveValues(n);
		for (int j = 0; j < n; j++) {
			i[j] = (int) Math.round(x[j]);
			d[j] = x[j];
			s[j] = Double.toString(x[j]);
		}
		typeConjecture = TYPE_DOUBLE;
		colorValueModified = true;
	}

	/**
	 * Constructor with color value initialization. We always use Yxy
	 * coordinates for experimental parameters.
	 * 
	 * @param c
	 *            initial PxlColor value of this parameter value.
	 */
	public ExParValue(PxlColor c) {
		createPrimitiveValues(3);
		setColorValue(c);
	}

	/**
	 * Set parameter value to color coordinates. We always use Yxy coordinates
	 * for experimental parameters.
	 * 
	 * @param c
	 *            initial PxlColor value of this parameter value.
	 */
	public void set(PxlColor c) {
		if (length != 3) {
			createPrimitiveValues(3);
		}
		setColorValue(c);
	}

	/**
	 * This method sets a parameter value to a color value. It also precomputes
	 * the color's device coordinates for later acces. The colorValueModified
	 * parameter stores the fact that this ExParValue holds valid device color
	 * coordinates in its integer array.
	 */
	private void setColorValue(PxlColor c) {
		d[0] = c.Y;
		d[1] = c.x;
		d[2] = c.y;
		i[0] = c.dev().getRGB();
		s[0] = Double.toString(d[0]);
		s[1] = Double.toString(d[1]);
		s[2] = Double.toString(d[2]);
		typeConjecture = TYPE_DOUBLE;
		colorValueModified = false;
	}

	/**
	 * Constructor with string valued initialization.
	 * 
	 * @param x
	 *            initial String value of this parameter value.
	 */
	public ExParValue(String x) {
		set(x);
	}

	/**
	 * Set String parameter value.
	 * 
	 * @param x
	 *            String value of this parameter value.
	 */
	public void set(String x) {
		if (length != 1)
			createPrimitiveValues(1);
		try {
			d[0] = Double.valueOf(x.trim()).doubleValue();
			i[0] = (int) Math.round(d[0]);
			typeConjecture = (i[0] == (int) (d[0])) ? TYPE_INT : TYPE_DOUBLE;
		} catch (NumberFormatException e) {
			d[0] = 0.0;
			i[0] = 0;
			typeConjecture = TYPE_STRING;
		}
		s[0] = x;
	}

	public ExParValue getValueAt(int idx) {
		ExParValue a = evaluation();
		return ((idx < a.length) ? (new ExParValue(a.s[idx])) : null);
	}

	public static ExParValue arrayOf(ExParValue a, ExParValue b) {
		String[] s = new String[2];
		s[0] = a.getString();
		s[1] = b.getString();
		return new ExParValue(s);
	}

	public static ExParValue arrayOf(ExParValue a, ExParValue b, ExParValue c) {
		String[] s = new String[3];
		s[0] = a.getString();
		s[1] = b.getString();
		s[2] = c.getString();
		return new ExParValue(s);
	}

	/**
	 * Constructor with string array valued initialization.
	 * 
	 * @param x
	 *            initial String array value of this parameter value.
	 */
	public ExParValue(String[] x) {
		set(x);
	}

	/**
	 * Set String array parameter value.
	 * 
	 * @param x
	 *            String array value of this parameter value.
	 */
	public void set(String[] x) {
		int n = x.length;
		if (length != n)
			createPrimitiveValues(n);
		try {
			d[0] = Double.valueOf(x[0].trim()).doubleValue();
			i[0] = (int) Math.round(d[0]);
			typeConjecture = (i[0] == (int) (d[0])) ? TYPE_INT : TYPE_DOUBLE;
		} catch (NumberFormatException e) {
			d[0] = 0.0;
			i[0] = 0;
			typeConjecture = TYPE_STRING;
		}
		s[0] = x[0];
		for (int j = 1; j < n; j++) {
			try {
				d[j] = Double.valueOf(x[j].trim()).doubleValue();
				i[j] = (int) Math.round(d[j]);
			} catch (NumberFormatException e) {
				d[j] = 0.0;
				i[j] = 0;
			}
			s[j] = x[j];
		}
		colorValueModified = true;
	}

	/**
	 * Set this ExParValue's data elements to be a copy of those of the argument
	 * value.
	 */
	public void set(ExParValue v) {
		if (length != v.length)
			createPrimitiveValues(v.length);
		// System.out.println("Copying " + length + " elements");
		if (length > 0) {
			System.arraycopy(v.i, 0, i, 0, length);
			System.arraycopy(v.d, 0, d, 0, length);
			System.arraycopy(v.s, 0, s, 0, length);
			if (v.classConstant != null) {
				if ((classConstant == null)
						|| (classConstant.length != v.classConstant.length)) {
					classConstant = new String[v.classConstant.length];
				}
				System.arraycopy(v.classConstant, 0, classConstant, 0, length);
			}
		}
		this.expansion = v.expansion;
		this.undefined = v.undefined;
		this.needsEvaluation = v.needsEvaluation;
		this.valueParam = v.valueParam;
		this.subValue1 = v.subValue1;
		this.subValue2 = v.subValue2;
		this.subValue3 = v.subValue3;
		this.exParExpression = v.exParExpression;
		this.typeConjecture = v.typeConjecture;
		// this.next = v.next;
		colorValueModified = true;
	}

	public static ExParValue runtimeParameterValue(String s) {
		ExDesignTreeParser pp = new ExDesignTreeParser(new StringReader(s));
		ExParValue v = null;
		try {
			v = pp.runtimeParameterValue();
		} catch (ParseException mme) {
			new RuntimeParseError("string " + s, mme);
		} catch (TokenMgrError tme) {
			new RuntimeParseError("string " + s, tme);
		}
		return v;
	}

	public static ExParValue file(String dir, String fn) {
		String[] sa = FileBase.loadStrings(dir, fn);
		StringBuffer sb = new StringBuffer(sa[0]);
		for (int i = 1; i < sa.length; i++) {
			sb.append("\n" + sa[i]);
		}
		ExParValue v = assignableParameterValue(sb.toString());
		return v.getValue();
	}

	public static ExParValue assignableParameterValue(String s) {
		ExDesignTreeParser pp = new ExDesignTreeParser(new StringReader(s));
		ExParValue v = null;
		try {
			v = pp.assignableParameterValue();
		} catch (ParseException mme) {
			new RuntimeParseError("string " + s, mme);
		} catch (TokenMgrError tme) {
			new RuntimeParseError("string " + s, tme);
		}
		return v;
	}

	/**
	 * Create a copy of this object. This also copies the array elements.
	 * 
	 * @return a copy of this <code>ExPar</code> object.
	 */
	public Object clone() {
		try {
			ExParValue v = (ExParValue) super.clone();
			if (length > 0) {
				v.i = new int[length];
				v.d = new double[length];
				v.s = new String[length];
				System.arraycopy(i, 0, v.i, 0, length);
				System.arraycopy(d, 0, v.d, 0, length);
				System.arraycopy(s, 0, v.s, 0, length);
			}
			return v;
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new RuntimeException("");
		}
	}

	/** Create storage for n elements */
	private void createPrimitiveValues(int n) {
		if (n > 0) {
			i = new int[n];
			d = new double[n];
			s = new String[n];
			undefined = false;
		} else {
			i = null;
			d = null;
			s = null;
			undefined = true;
		}
		length = n;
		needsEvaluation = false;
		expansion = false;
	}

	/**
	 * Return a parameter's value with expressions evaluated.
	 * 
	 * @return a reference to this parameter's value.
	 */
	public ExParValue getValue() {
		return (evaluation());
	}

	/**
	 * Return a parameter's integer array.
	 * 
	 * @return a reference to this parameter's integer array.
	 */
	public int[] getIntArray() {
		return (evaluation().i);
	}

	/**
	 * Return a parameter's double array.
	 * 
	 * @return a reference to this parameter's double array.
	 */
	public double[] getDoubleArray() {
		return (evaluation().d);
	}

	/**
	 * Return a parameter's string array.
	 * 
	 * @return a reference to this parameter's String array.
	 */
	public String[] getStringArray() {
		return (evaluation().s);
	}

	/**
	 * Return a parameter's integer value.
	 * 
	 * @return this parameter's integer value from its internal array index 0.
	 */
	public int getInt() {
		return (evaluation().i[0]);
	}

	/**
	 * Return a parameter's double value.
	 * 
	 * @return this parameter's double value from its internal array index 0.
	 */
	public double getDouble() {
		return (evaluation().d[0]);
	}

	/**
	 * Return a parameter's string value.
	 * 
	 * @return this parameter's String value from its internal array index 0.
	 */
	public String getString() {
		return (evaluation().s[0]);
	}

	/**
	 * Return a parameter's double array interpreted as a Yxy color coordinates.
	 * 
	 * @return the PxlColor object corresponding to this parameter's double
	 *         array.
	 */
	public PxlColor getPxlColor() {
		PxlColor r = null;
		ExParValue x = evaluation();
		if ((x.d == null) || (x.length != 3)) {
			new ParameterValueError("Illegal color value: " + toString()
					+ " set to black.");
			r = PxlColor.systemColor(PxlColor.BLACK);
		} else {
			r = new YxyColor(x.d);
		}
		return r;
	}

	/**
	 * Return a parameter's double array interpreted as Yxy color converted to
	 * device coordinates.
	 * 
	 * @return the device color coordinates of the PxlColor object corresponding
	 *         to this parameter's double array.
	 */
	public Color getDevColor() {
		// / ExParValue x = evaluation();
		// / if (x.d == null) return(null);
		// / if (x.colorValueModified) {
		// System.out.println("Unknown modification: " + this);
		// YxyColor c =
		// / x.i[0] = (new YxyColor(x.d)).dev().getRGB();
		// / colorValueModified = false;
		// / }
		return (getPxlColor().dev());
		// / return(new Color(x.i[0]));
	}

	/**
	 * Identify this parameter value as an expansion array for implicit loops.
	 * 
	 * @return true if this parameter is an expansion array.
	 */
	public boolean isExpansion() {
		return (expansion);
	}

	/** Make the array an expansion array for implicit loops. */
	public void setExpansion() {
		expansion = true;
	}

	/**
	 * Set this parameter's expansion property.
	 * 
	 * @param x
	 *            the expansion property value for this parameter value.
	 */
	public void setExpansion(boolean x) {
		expansion = x;
	}

	/**
	 * Figure out whether this parameter value needs evaluation or is a literal
	 * value.
	 * 
	 * @return true if this value needs to be evaluated and false if this is a
	 *         string, a integer or a double value.
	 */
	public boolean getNeedsEvaluation() {
		return needsEvaluation;
	}

	/**
	 * Check whether this ExParValue is an expression.
	 * 
	 * @return true if this ExParValue is an expression.
	 */
	public boolean isExpression() {
		return exParExpression != null;
	}

	/**
	 * Check whether this ExParValue is a literal constant.
	 * 
	 * @return true if this ExParValue is a literal constant.
	 */
	public boolean isLiteralConstant() {
		return !needsEvaluation;
	}

	/**
	 * Check whether this ExParValue is a variable.
	 * 
	 * @return true if this ExParValue is a variable.
	 */
	public boolean isVariable() {
		return needsEvaluation && (valueParam != null);
	}

	/**
	 * If this ExParValue is an expression tree then this method evaluates the
	 * tree and stores the result in this object's data arrays. If this
	 * ExParValue is a variable then this object's data arrays are set from this
	 * variable's data.
	 */
	private ExParValue evaluation() {
		ExParValue x = this;
		if (needsEvaluation) {
			if (Debug.isActive(Debug.EXPR))
				System.out.print("ExParValue.evaluation(): " + this + " = [");
			if (isExpression()) {
				x = exParExpression.valueOf(subValue1, subValue2, subValue3);
				if (x == null) {
					new ParameterValueError(
							"ExParValue.evaluation() Expression value error for "
									+ exParExpression.toString(subValue1,
											subValue2, subValue3) + ". Using 0");
					x = new ExParValue(0);
				}
				if (Debug.isActive(Debug.EXPR))
					System.out.println(x + "]");
			} else if (valueParam != null) {
				ExPar xp = ExPar.get(valueParam, false);
				if (xp != null) {
					x = xp.getValue().evaluation();
					if (Debug.isActive(Debug.EXPR))
						System.out.println(x + "]");
				} else {
					new ParameterValueError(
							"ExParValue.evaluation() Unknown symbolic value: "
									+ valueParam + ". Using 0");
					x = new ExParValue(0);
				}
			} else {
				new InternalError(
						"ExParValue.evaluation() Internal error. ExParValue needs evaluation but has no expression operator and no symbolic name.");
			}
		}
		return x;
	}

	public void setUndefined(boolean s) {
		undefined = s;
	}

	/**
	 * Returns true if the actual value of this ExPar is not yet defined. ExPars
	 * which exist but whose values are undefined correspond to question mark
	 * place holders in argument lists. Their values are inherited from the next
	 * global level. Undefined ExPars are not allowed during initialization of
	 * experimental parameters or in assignment statements.
	 */
	public boolean isUndefined() {
		return undefined;
	}

	protected void setNotSet() {
		if (length != 1)
			createPrimitiveValues(1);
		i[0] = 0;
		d[0] = -1.0;
		s[0] = "";
		typeConjecture = TYPE_STRING;
	}

	/**
	 * Returns true if this value has not been set. A value is not set if it is
	 * an instance of ExParValueNotSet.
	 * 
	 * @see ExParValueNotSet
	 */
	public boolean isNotSet() {
		return (length == 1) && (i[0] == 0) && (d[0] == -1.0)
				&& (s[0].length() == 0);
	}

	/**
	 * Returns true if the actual value of this ExPar is empty. This means that
	 * the parameter either is undefined or is an empty string.
	 */
	public boolean isEmpty() {
		if (undefined) {
			return true;
		} else {
			String ss = getString();
			if (ss == null) {
				return true;
			} else if (ss.length() == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Compares this ExParValue to the specified object. The result is true if
	 * and only if the argument is not null and is an ExParValue object that
	 * represents the same sequence of values as this object.
	 */
	public boolean equals(Object obj) {
		boolean r = false;
		if ((obj != null) && (obj instanceof ExParValue)) {
			String[] a = this.getStringArray();
			String[] b = ((ExParValue) obj).getStringArray();
			if (a.length == b.length) {
				r = true;
				for (int ii = 0; ii < a.length; ii++) {
					if (!a[ii].equals(b[ii])) {
						r = false;
						break;
					}
				}
			}
		}
		return (r);
	}

	/**
	 * Check whether the given string is a valid class constant name.
	 * 
	 * @param n
	 *            short or full access name of the class constant.
	 * @return true if the given name is known as a class constant. Only PXLab
	 *         classes contained in package 'de.pxlab' and Java classes of the
	 *         packages 'java' and 'javax' are accepted as package names for
	 *         class constant classes.
	 * @see ClassConstantMap
	 */
	public static boolean isClassConstant(String n) {
		// System.out.println("ExParValue.isClassConstant(): Checking " + n);
		boolean r = classConstantMap.containsKey(n);
		if (!r) {
			if (n.startsWith("java.") || n.startsWith("javax.")) {
				int d = n.lastIndexOf('.');
				String cn = n.substring(0, d);
				String fn = n.substring(d + 1);
				try {
					Class cls = Class.forName(cn);
					try {
						java.lang.reflect.Field fld = cls.getField(fn);
						r = true;
					} catch (NoSuchFieldException nsfe) {
					}
				} catch (ClassNotFoundException cnfe) {
				}
			}
		}
		return r;
	}

	/**
	 * Find the value of a class constant whose full name is given as an
	 * argument. The constant must be defined with the public and static
	 * attributes.
	 * 
	 * @param n
	 *            full access name of the field including the package and class
	 *            prefix or short acces name as contained int ClassConstantMap.
	 * @return an ExParValue having the constant's value.
	 * @see ClassConstantMap
	 */
	public static ExParValue getClassConstant(String n) {
		// System.out.println("ExParValue.getClassConstant(): " + n);
		if (classConstantMap.containsKey(n)) {
			return classConstantMap.getValue(n);
		}
		int d = n.lastIndexOf('.');
		if (d < 0) {
			return null;
		}
		String cn = n.substring(0, d);
		String fn = n.substring(d + 1);
		Object obj = null;
		String err = null;
		ExParValue x = null;
		try {
			Class cls = Class.forName(cn);
			try {
				java.lang.reflect.Field fld = cls.getField(fn);
				try {
					obj = fld.get(null);
					if (obj instanceof Integer) {
						x = new ExParValue(((Integer) obj).intValue());
					} else if (obj instanceof Double) {
						x = new ExParValue(((Double) obj).doubleValue());
					} else if (obj instanceof String) {
						x = new ExParValue(((String) obj));
					} else {
						err = "Illegal type of system constant: " + n;
					}
				} catch (IllegalAccessException iae) {
					err = "Illegal access to " + n;
				}
			} catch (NoSuchFieldException nsfe) {
				err = "Class " + cn + " does not have a constant named " + fn;
			}
		} catch (ClassNotFoundException cnfe) {
			err = "Class " + cn + " for constant " + fn + " not found!";
		}
		if (err != null) {
			new ParameterValueError(err);
		}
		// System.out.println("ExParValue.getClassConstant(): Value = " + x);
		return (x);
	}

	/**
	 * Create an ExParValue from an array of constant ExParValue objects.
	 * 
	 * @param a
	 *            an array of ExParValue objects. The new ExParValue object has
	 *            the first entry of each array element as its data elements.
	 */
	public static ExParValue constantExParValue(ExParValue[] a) {
		int n = a.length;
		ExParValue x = new ExParValue();
		x.createPrimitiveValues(n);
		String[] cc = new String[n];
		boolean hasCC = false;
		for (int j = 0; j < n; j++) {
			x.i[j] = a[j].getInt();
			x.d[j] = a[j].getDouble();
			x.s[j] = a[j].getString();
			x.typeConjecture = a[j].typeConjecture;
			if ((a[j].classConstant != null) && (a[j].classConstant[0] != null)) {
				cc[j] = a[j].classConstant[0];
				hasCC = hasCC || true;
			} else {
				cc[j] = a[j].getString();
			}
		}
		if (hasCC) {
			x.classConstant = cc;
			x.typeConjecture = TYPE_CLASS_CONSTANT;
		}
		return x;
	}

	/**
	 * Create a new parameter value which is the sum of this parameter value and
	 * the parameter value given as an argument. All numerical operations are
	 * done element-wise using the double representation of a parameter value.
	 * 
	 * @param x
	 *            the parameter value which should be added to this value.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue add(ExParValue x) {
		double[] v = getDoubleArray();
		double[] vx = x.getDoubleArray();
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		double[] z = new double[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = v[j] + vx[j];
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is the difference of this parameter
	 * value and the parameter value given as an argument. All numerical
	 * operations are done element-wise using the double representation of a
	 * parameter value.
	 * 
	 * @param x
	 *            the parameter value which should be subtracted from this
	 *            value.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue sub(ExParValue x) {
		double[] v = getDoubleArray();
		double[] vx = x.getDoubleArray();
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		double[] z = new double[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = v[j] - vx[j];
			// System.out.println("ExParValue.sub() " + v[j] + " - " + vx[j] +
			// " = " + z[j]);
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is the product of this parameter value
	 * and the parameter value given as an argument. All numerical operations
	 * are done element-wise using the double representation of a parameter
	 * value.
	 * 
	 * @param x
	 *            the parameter value by which this value should be multiplied.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue mul(ExParValue x) {
		double[] v = getDoubleArray();
		double[] vx = x.getDoubleArray();
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		double[] z = new double[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = v[j] * vx[j];
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is the quotient of this parameter
	 * value and the parameter value given as an argument. All numerical
	 * operations are done element-wise using the double representation of a
	 * parameter value.
	 * 
	 * @param x
	 *            the parameter value by which this value should be divided.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue div(ExParValue x) {
		double[] v = getDoubleArray();
		double[] vx = x.getDoubleArray();
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		double[] z = new double[v.length];
		for (int j = 0; j < v.length; j++) {
			// System.out.println("ExParValue.div(): " + v[j] + " / " + vx[j]);
			z[j] = v[j] / vx[j];
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is the integer quotient of this
	 * parameter value and the parameter value given as an argument. Operations
	 * are done element-wise using the integer representation of a parameter
	 * value.
	 * 
	 * @param x
	 *            the parameter value by which this value should be divided.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue idiv(ExParValue x) {
		int[] v = getIntArray();
		int[] vx = x.getIntArray();
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		int[] z = new int[v.length];
		for (int j = 0; j < v.length; j++) {
			// System.out.println("ExParValue.div(): " + v[j] + " / " + vx[j]);
			z[j] = v[j] / vx[j];
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is the modulus of this parameter value
	 * and the parameter value given as an argument. All numerical operations
	 * are done element-wise using the integer representation of a parameter
	 * value.
	 * 
	 * @param x
	 *            the parameter value by which this value should be divided.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue mod(ExParValue x) {
		int[] v = getIntArray();
		int[] vx = x.getIntArray();
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		int[] z = new int[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = v[j] % vx[j];
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is the string concatenation of this
	 * parameter value and the parameter value given as an argument. This
	 * operation is done element-wise. Numerical values of the result are zero.
	 * 
	 * @param x
	 *            the parameter value which should be appended to this parameter
	 *            value.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue cat(ExParValue x) {
		String[] v = getStringArray();
		String[] vx = x.getStringArray();
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		String[] z = new String[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = v[j] + vx[j];
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is 1 if this parameter value and the
	 * parameter value given as an argument are equal.
	 * 
	 * @param x
	 *            the parameter value which should be compared to this value.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue eq(ExParValue x) {
		String[] v = getStringArray();
		String[] vx = x.getStringArray();
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		int[] z = new int[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = (v[j].equals(vx[j])) ? 1 : 0;
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is 1 if this parameter value and the
	 * parameter value given as an argument are not equal.
	 * 
	 * @param x
	 *            the parameter value which should be compared to this value.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue neq(ExParValue x) {
		// System.out.println("ExParValue.neq(): " + this.toString() + " [!=] "
		// + x.toString());
		String[] v = getStringArray();
		String[] vx = x.getStringArray();
		// System.out.println("ExParValue.neq(): v = " + v);
		// System.out.println("ExParValue.neq(): vx = " + vx);
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		int[] z = new int[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = (v[j].equals(vx[j])) ? 0 : 1;
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value whose numeric value is the negative of this
	 * parameter's numeric value.
	 * 
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue neg() {
		double[] v = getDoubleArray();
		double[] z = new double[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = -v[j];
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value whose numeric value is the positive of this
	 * parameter's numeric value.
	 * 
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue pos() {
		double[] v = getDoubleArray();
		double[] z = new double[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = v[j];
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value whose logical value is the negation of this
	 * parameter's logical value.
	 * 
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue not() {
		int[] v = getIntArray();
		int[] z = new int[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = (v[j] != 0) ? 0 : 1;
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is 1 if this parameter's numeric value
	 * is larger than the parameter's numeric value given as an argument.
	 * 
	 * @param x
	 *            the parameter value which should be compared to this value.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue gt(ExParValue x) {
		double[] v = getDoubleArray();
		double[] vx = x.getDoubleArray();
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		int[] z = new int[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = (v[j] > vx[j]) ? 1 : 0;
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is 1 if this parameter's numeric value
	 * is less than the parameter's numeric value given as an argument.
	 * 
	 * @param x
	 *            the parameter value which should be compared to this value.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue lt(ExParValue x) {
		double[] v = getDoubleArray();
		double[] vx = x.getDoubleArray();
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		int[] z = new int[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = (v[j] < vx[j]) ? 1 : 0;
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is 1 if this parameter's numeric value
	 * is larger than or equal to the parameter's numeric value given as an
	 * argument.
	 * 
	 * @param x
	 *            the parameter value which should be compared to this value.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue ge(ExParValue x) {
		double[] v = getDoubleArray();
		double[] vx = x.getDoubleArray();
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		int[] z = new int[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = (v[j] >= vx[j]) ? 1 : 0;
			// System.out.println("ExParValue.ge() " + v[j] + " >= " + vx[j] +
			// " = " + z[j]);
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is 1 if this parameter's numeric value
	 * is less than or equalt to the parameter's numeric value given as an
	 * argument.
	 * 
	 * @param x
	 *            the parameter value which should be compared to this value.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue le(ExParValue x) {
		double[] v = getDoubleArray();
		double[] vx = x.getDoubleArray();
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		int[] z = new int[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = (v[j] <= vx[j]) ? 1 : 0;
			// System.out.println("ExParValue.le() " + v[j] + " <= " + vx[j] +
			// " = " + z[j]);
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is 1 if this parameter's numeric value
	 * and the parameter's numeric value given as an argument both are non-zero.
	 * 
	 * @param x
	 *            the parameter value which should be compared to this value.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue and(ExParValue x) {
		int[] v = getIntArray();
		int[] vx = x.getIntArray();
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		int[] z = new int[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = ((v[j] != 0) && (vx[j] != 0)) ? 1 : 0;
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is 1 if this parameter's numeric value
	 * or the parameter's numeric value given as an argument is non-zero.
	 * 
	 * @param x
	 *            the parameter value which should be compared to this value.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue or(ExParValue x) {
		int[] v = getIntArray();
		int[] vx = x.getIntArray();
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		int[] z = new int[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = ((v[j] != 0) || (vx[j] != 0)) ? 1 : 0;
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is the binary AND pattern of this
	 * parameter's numeric value and the parameter's numeric value given as an
	 * argument.
	 * 
	 * @param x
	 *            the parameter value which should be compared to this value.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue binAnd(ExParValue x) {
		int[] v = getIntArray();
		int[] vx = x.getIntArray();
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		int[] z = new int[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = v[j] & vx[j];
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is the binary OR pattern of this
	 * parameter's numeric value and the parameter's numeric value given as an
	 * argument.
	 * 
	 * @param x
	 *            the parameter value which should be compared to this value.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue binOr(ExParValue x) {
		int[] v = getIntArray();
		int[] vx = x.getIntArray();
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		int[] z = new int[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = v[j] | vx[j];
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is the binary EXCLUSIVE OR pattern of
	 * this parameter's numeric value and the parameter's numeric value given as
	 * an argument.
	 * 
	 * @param x
	 *            the parameter value which should be compared to this value.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue binXor(ExParValue x) {
		int[] v = getIntArray();
		int[] vx = x.getIntArray();
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		int[] z = new int[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = v[j] ^ vx[j];
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is the binary COMPLEMENT pattern of
	 * this parameter's numeric value.
	 * 
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue binComplement() {
		int[] v = getIntArray();
		int[] z = new int[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = ~v[j];
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is the this parameter's numeric value
	 * SHIFTED LEFT by as many steps as given by the parameter's numeric value
	 * given as an argument.
	 * 
	 * @param x
	 *            the parameter value which should be compared to this value.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue shiftLeft(ExParValue x) {
		int[] v = getIntArray();
		int[] vx = x.getIntArray();
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		int[] z = new int[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = v[j] << vx[j];
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is the this parameter's numeric value
	 * SHIFTED RIGHT by as many steps as given by the parameter's numeric value
	 * given as an argument.
	 * 
	 * @param x
	 *            the parameter value which should be compared to this value.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue shiftRight(ExParValue x) {
		int[] v = getIntArray();
		int[] vx = x.getIntArray();
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		int[] z = new int[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = v[j] >> vx[j];
		}
		return new ExParValue(z);
	}

	/**
	 * Create a new parameter value which is the this parameter's numeric value
	 * SHIFTED RIGHT UNSIGNED by as many steps as given by the parameter's
	 * numeric value given as an argument.
	 * 
	 * @param x
	 *            the parameter value which should be compared to this value.
	 * @return a new parameter value representing the result of the operation.
	 */
	public ExParValue shiftRightUnsigned(ExParValue x) {
		int[] v = getIntArray();
		int[] vx = x.getIntArray();
		if (v.length != vx.length) {
			throw new OpArrayLengthException();
		}
		int[] z = new int[v.length];
		for (int j = 0; j < v.length; j++) {
			z[j] = v[j] >>> vx[j];
		}
		return new ExParValue(z);
	}

	/**
	 * Return a String representation of a parameter value. This method tries to
	 * figure out what the preferred type of a parameter value is and returns
	 * the respective representation.
	 * 
	 * @return a String representing this parameter value.
	 */
	public String toString() {
		if (isUndefined())
			return ("?");
		StringBuffer rs = new StringBuffer();
		if (needsEvaluation) {
			if (exParExpression != null) {
				rs.append(exParExpression.toString(subValue1, subValue2,
						subValue3));
			} else if (valueParam != null) {
				rs.append(valueParam);
			}
		} else {
			if (length == 0) {
				rs.append("?");
			} else {
				if (length == 1) {
					rs.append(sp(0, IGNORE_FMT));
				} else {
					rs.append((expansion ? "<" : "["));
					rs.append(sp(0, IGNORE_FMT));
					for (int j = 1; j < length; j++) {
						rs.append(", ");
						rs.append(sp(j, IGNORE_FMT));
					}
					rs.append((expansion ? ">" : "]"));
				}
			}
		}
		return (rs.toString());
	}
	private static int vn;
	private static int vna;
	private static HashSet vNames;

	public static void initJavaSourceGenerator() {
		vna = 1;
		vNames = new HashSet(30);
	}

	public String toJavaSource(String indent, StringBuffer b) {
		vn = 1;
		return _toJavaSource(indent, b);
	}

	private void declare(String indent, StringBuffer b, String vName) {
		if (vNames.contains(vName)) {
			b.append(indent + vName + " = ");
		} else {
			b.append(indent + "ExParValue " + vName + " = ");
			vNames.add(vName);
		}
	}

	private String _toJavaSource(String indent, StringBuffer b) {
		String nl = System.getProperty("line.separator");
		String vName = "_exParValue_" + String.valueOf(vn++);
		if (isUndefined()) {
			declare(indent, b, vName);
			b.append("new ExParValueUndefined();" + nl);
		} else {
			// not Undefined
			if (needsEvaluation) {
				if (exParExpression != null) {
					if (subValue3 != null) {
						// 3 arguments
						String v1 = subValue1._toJavaSource(indent, b);
						String v2 = subValue2._toJavaSource(indent, b);
						String v3 = subValue3._toJavaSource(indent, b);
						declare(indent, b, vName);
						b.append("new ExParValueFunction("
								+ exParExpression.getOpCode() + ", " + v1
								+ ", " + v2 + ", " + v3 + ");" + nl);
					} else if (subValue2 != null) {
						// 2 arguments
						String v1 = subValue1._toJavaSource(indent, b);
						String v2 = subValue2._toJavaSource(indent, b);
						declare(indent, b, vName);
						b.append("new ExParValueFunction("
								+ exParExpression.getOpCode() + ", " + v1
								+ ", " + v2 + ");" + nl);
					} else if (subValue1 != null) {
						// 1 argument
						String v1 = subValue1._toJavaSource(indent, b);
						declare(indent, b, vName);
						b.append("new ExParValueFunction("
								+ exParExpression.getOpCode() + ", " + v1
								+ ");" + nl);
					} else {
						// no arguments
						declare(indent, b, vName);
						b.append("new ExParValueFunction("
								+ exParExpression.getOpCode() + ");" + nl);
					}
				} else if (valueParam != null) {
					// needs evaluation but is not an expression
					// thus it must be a variable
					declare(indent, b, vName);
					b.append("new ExParValueVar(\"" + valueParam + "\");" + nl);
				}
			} else {
				// does not need evaluation
				if (length == 0) {
					// this should never happen
					declare(indent, b, vName);
					b.append("new ExParValueUndefined();" + nl);
				} else {
					if (length == 1) {
						declare(indent, b, vName);
						b.append("new ExParValue");
						appendConstant(b, 0);
						b.append(");" + nl);
					} else {
						String[] a = getStringArray();
						String aName = "_valueArray_" + String.valueOf(vna++);
						b.append(indent + "String[] " + aName + " = { "
								+ StringExt.quote(a[0]));
						for (int j = 1; j < length; j++) {
							b.append(", " + StringExt.quote(a[j]));
						}
						b.append("};" + nl);
						declare(indent, b, vName);
						b.append("new ExParValue(" + aName + ");" + nl);
						if (expansion) {
							b.append(indent + vName + ".setExpansion();" + nl);
						}
					}
				}
			}
		}
		return vName;
	}

	private void appendConstant(StringBuffer b, int i) {
		if (classConstant == null || classConstant[i] == null) {
			b.append("(" + StringExt.quote(getStringArray()[i]));
		} else {
			b.append("Constant(" + StringExt.quote(classConstant[i]));
		}
	}

	/**
	 * Return a String representation of a parameter value. This method tries to
	 * figure out what the preferred type of a parameter value is and returns
	 * the respective representation.
	 * 
	 * @return a String representing this parameter value.
	 */
	public String toFormattedString(char fmt) {
		if (isUndefined())
			return ("?");
		StringBuffer rs = new StringBuffer();
		if (needsEvaluation) {
			if (exParExpression != null) {
				rs.append(exParExpression.toString(subValue1, subValue2,
						subValue3));
			} else if (valueParam != null) {
				rs.append(valueParam);
			}
		} else {
			if (length == 0) {
				rs.append("?");
			} else {
				if (length == 1) {
					rs.append(sp(0, fmt));
				} else {
					if (fmt == SIMPLE_ARRAY_FMT) {
						rs.append(sp(0, IGNORE_FMT));
						for (int j = 1; j < length; j++) {
							rs.append(" " + sp(j, IGNORE_FMT));
						}
					} else {
						rs.append((expansion ? "<" : "[") + sp(0, IGNORE_FMT));
						for (int j = 1; j < length; j++) {
							rs.append(", " + sp(j, IGNORE_FMT));
						}
						rs.append((expansion ? ">" : "]"));
					}
				}
			}
		}
		return (rs.toString());
	}

	/**
	 * Return a String representation of a parameter value. This method tries to
	 * figure out what the preferred type of a parameter value is and returns
	 * the respective representation.
	 * 
	 * @return a String representing this parameter value.
	 */
	public String toString2() {
		ExParValue x = evaluation();
		StringBuffer rs = new StringBuffer((x.expansion ? "<" : "[") + x.i[0]);
		for (int j = 1; j < x.i.length; j++)
			rs.append(", " + x.i[j]);
		rs.append((expansion ? ">" : "]"));
		rs.append((expansion ? "<" : "[") + x.d[0]);
		for (int j = 1; j < x.d.length; j++)
			rs.append(", " + x.d[j]);
		rs.append((expansion ? ">" : "]"));
		rs.append((expansion ? "<" : "[\"") + x.s[0] + "\"");
		for (int j = 1; j < x.s.length; j++)
			rs.append(", \"" + x.s[j] + "\"");
		rs.append((expansion ? ">" : "]"));
		return (rs.toString());
	}

	/**
	 * Try to figure out the preferred type of a scalar parameter value and
	 * return the corresponding string description. Strings are quoted.
	 * 
	 * @param index
	 *            the index of this parameter's value arrays which should be
	 *            converted.
	 * @return a String representing the primitive parameter value at the given
	 *         index.
	 */
	/*
	 * private String sp(int index) { // System.out.println("ExParValue.sp(): ["
	 * + String.valueOf(i[index]) + "/" + String.valueOf(d[index]) + "/\"" +
	 * s[index] + "\"]"); if (String.valueOf(i[index]).equals(s[index])) //
	 * seems to be an integer so use its string representation return
	 * (s[index]); else if (String.valueOf(d[index]).equals(s[index])) // seems
	 * to be a double return (doubleExParValue.format(d[index])); // don't know
	 * what this is, so assume that it is a string
	 * return(StringExt.quote(s[index])); }
	 */
	private String sp(int index, int fmt) {
		if ((classConstant != null) && (classConstant.length > index)
				&& (classConstant[index] != null)) {
			return classConstant[index];
		} else {
			// System.out.println("ExParValue.sp(): [" +
			// String.valueOf(i[index]) + "/" + String.valueOf(d[index]) + "/\""
			// + s[index] + "\"]");
			if (fmt == INTEGER_FMT) {
				return String.valueOf(i[index]);
			} else if (fmt == STRING_FMT) {
				return s[index];
			} else /* if (fmt == IGNORE_FMT) */{
				if ((typeConjecture == TYPE_INT)
						|| (typeConjecture == TYPE_CLASS_CONSTANT)) {
					return s[index];
				} else if (typeConjecture == TYPE_DOUBLE) {
					return doubleExParValue.format(d[index]);
				}
				return StringExt.quote(s[index]);
			}
		}
	}

	/**
	 * Create a string of an array of ExParValue objects. No evaluation is done!
	 * This may be used to create a string representation of design file value
	 * argument lists.
	 * 
	 * @param pv
	 *            the array of ExParValue objects to be printed.
	 * @return a string representation of the array.
	 */
	public static String stringOf(ExParValue[] pv) {
		StringBuilder s = new StringBuilder("(");
		if (pv != null) {
			if (pv.length > 0) {
				if (pv[0] == null) {
					s.append(" (null)");
				} else {
					s.append(" " + pv[0].toString());
				}
			}
			if (pv.length > 1) {
				for (int i = 1; i < pv.length; i++) {
					if (pv[i] == null) {
						s.append(" (null)");
					} else {
						s.append(", " + pv[i].toString());
					}
				}
			}
		}
		s.append(")");
		return s.toString();
	}
	private static final double TOLERANCE = 0.0000001;

	/**
	 * Check whether this ExParValue is a numeric value. An ExParValue
	 * essentially is assumed to be a number whenever its String representation
	 * describes the same value as its floating point representation.
	 * 
	 * @return true if this ExParValue is - by best guess - a number.
	 */
	public boolean isNumeric() {
		boolean n = true;
		for (int i = 0; n && i < length; i++) {
			if (StringExt.nonEmpty(s[i])) {
				try {
					double x = Double.valueOf(s[i]).doubleValue();
					n = (x > (d[i] - TOLERANCE)) && (x < (d[i] + TOLERANCE));
				} catch (NumberFormatException nfx) {
					// can't be converted, so it can't be a number
					n = false;
				}
			} else {
				// string representation is empty, so this can't be a number
				n = false;
			}
		}
		return n;
	}

	/** Set this value's preferred string conversion type. */
	public void setTypeConjecture(int t) {
		if (t == TYPE_INT || t == TYPE_DOUBLE || t == TYPE_STRING
				|| t == TYPE_CLASS_CONSTANT) {
			typeConjecture = t;
		}
	}

	/** Print this parameter value to the system output stream. */
	public void print() {
		System.out.println(toString());
	}

	/**
	 * Select an input argument according to the current language.
	 * 
	 * @return the first argument if the current language is English and the
	 *         second argument if the current language is German.
	 */
	public static ExParValue getValueForLanguage(ExParValue a, ExParValue b) {
		return getValueForLanguage(a, b, a);
	}

	/**
	 * Select an input argument according to the current language.
	 * 
	 * @return the first argument if the current language is English, the second
	 *         argument if the current language is German, and the third
	 *         argument if the current language is anything else.
	 */
	public static ExParValue getValueForLanguage(ExParValue a, ExParValue b,
			ExParValue c) {
		String loc = Base.getLanguage();
		ExParValue r = null;
		if (loc.startsWith("en")) {
			r = a;
		} else if (loc.startsWith("ge") || loc.startsWith("de")) {
			r = b;
		} else {
			r = c;
		}
		return r;
	}
	/**
	 * This class captures situations where parameter value operations are
	 * requested using parameter values whose primitive value arrays do not have
	 * the same lengths.
	 */
	private class OpArrayLengthException extends RuntimeException {
		public OpArrayLengthException() {
			super("Parameter value operations require identical array lengths.");
		}
	}

	// --------------------------------------------------
	// Implementation of TreeNode
	// --------------------------------------------------
	/** Returns the children of the receiver as an Enumeration. */
	public Enumeration children() {
		Vector v = new Vector();
		if (getSubValue(0) != null)
			v.add(getSubValue(0));
		if (getSubValue(1) != null)
			v.add(getSubValue(1));
		if (getSubValue(2) != null)
			v.add(getSubValue(2));
		return v.elements();
	}

	/** Returns true if the receiver allows children. */
	public boolean getAllowsChildren() {
		return true;
	}

	/** Returns the child TreeNode at index childIndex. */
	public TreeNode getChildAt(int index) {
		return getSubValue(index);
	}

	/** Returns the number of children TreeNodes the receiver contains. */
	public int getChildCount() {
		int n = 0;
		if (getExParExpression() == null) {
			n = 0;
		} else if (getSubValue(2) != null) {
			n = 3;
		} else if (getSubValue(1) != null) {
			n = 2;
		} else if (getSubValue(0) != null) {
			n = 1;
		}
		return n;
	}

	/** Returns the index of node in the receivers children. */
	public int getIndex(TreeNode node) {
		int i = 0;
		if (getExParExpression() == null) {
			i = -1;
		} else if (((ExParValue) node).equals(getSubValue(0))) {
			i = 0;
		} else if (((ExParValue) node).equals(getSubValue(1))) {
			i = 1;
		} else if (((ExParValue) node).equals(getSubValue(2))) {
			i = 2;
		}
		return i;
	}

	/** Returns the parent TreeNode of the receiver. */
	public TreeNode getParent() {
		return parent;
	}

	/** Returns true if the receiver is a leaf. */
	public boolean isLeaf() {
		return getExParExpression() == null;
	}

	// --------------------------------------------
	// MutableTreeNode implementation
	// --------------------------------------------
	/** Adds child to the receiver at index. */
	public void insert(ExDesignNode child, int index) {
	}

	/* Removes the child at index from the receiver. */
	public void remove(int index) {
	}

	/** Removes node from the receiver. */
	public void remove(ExDesignNode node) {
	}

	/** Removes the receiver from its parent. */
	public void removeFromParent() {
	}

	/** Sets the parent of the receiver to newParent. */
	/*
	 * public void setParent(ExDesignNode newParent) { parent =
	 * (ExDesignNode)newParent; }
	 */
	/** Resets the user object of the receiver to object. */
	public void setUserObject(Object object) {
	}
	// --------------------------------------------------
	// End of MutableTreeNode implementation
	// --------------------------------------------------
}
