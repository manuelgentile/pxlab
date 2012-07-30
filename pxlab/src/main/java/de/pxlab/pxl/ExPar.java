package de.pxlab.pxl;

import java.awt.*;
import java.io.*;

import java.util.*;
import java.text.DateFormat;
import java.lang.reflect.Field;

import de.pxlab.util.StringExt;
import de.pxlab.pxl.parser.*;

/**
 * These are the PXLab system's global experimental parameters. An experimental
 * parameter has a name, a value and a hint, which is a short description of its
 * meaning. The value of an experimental parameter is of type ExParValue which
 * actually represents arrays of integer, double, or String values.
 * 
 * @version 0.4.40
 * @see ExParValue
 */
/*
 * 01/23/01 added remove() method 01/26/01 added getDevColor() method 02/24/01
 * added type code 04/14/01 added StateCodes for TrialState, BlockState,
 * SessionState 05/01/01 added StoreData 05/10/01 removed initializations by
 * ExParValueUndefined() objects 05/14/01 DesignVersion, DesignDate,
 * DesignAuthor are new parameters 05/15/01 fixed a bug with getStringArray()
 * for expressions 06/22/01 added Adjustment keys 07/09/01 added
 * ResponseErrorCount
 * 
 * 07/18/01 make output of getAllParNames() sorted
 * 
 * 07/28/01 some more constructors with min/max and type
 * 
 * 08/05/01 DisplayExtensionPackage is new did some rearrangements
 * 
 * 08/15/01 DataTreeFileExtension is new, default: ".dtr" StoreDataTree is new,
 * default: true
 * 
 * 09/19/01 Some corrections for adaptive procedures
 * 
 * 09/29/01 changed push() to push a clone of its argument such that undefined
 * and defined ExParValue objects behave identically.
 * 
 * 11/17/01 set SkipBoundingBlockDisplays true by default in order to skip the
 * first and last block messages of a session.
 * 
 * 11/22/01 added AppletSystem flag.
 * 
 * 01/24/02 added ExternalControlBox name
 * 
 * 02/12/02 reset() is new. It also resets all parameters to their initial
 * defaults.
 * 
 * 02/14/02 added DoubleBuffered parameter.
 * 
 * 02/18/02 return null for no-existing arrays in getStringArray()
 * 
 * 02/26/02 new: setDate() method
 * 
 * 07/02/02 new: Horizontal/VerticalScreenResolution
 * 
 * 03/04/03 added serial communication device name
 * 
 * 03/12/03 added the global runtime object table and its access methods.
 * 
 * 04/02/03 added HTTPRequestMethod
 * 
 * 06/21/03 changed initializers to NOT use PxlColor conversion before any
 * experiment is started.
 * 
 * 06/21/03 added ColorDevice and ColorDeviceDACScale
 * 
 * 06/26/03 new: AdaptiveParameterLimits
 * 
 * 10/30/03 new: Stereographic display mode flag
 * 
 * 11/10/03 new: AdjustableParameterLimits
 * 
 * 11/17/03 new: RunImmediately Flag
 * 
 * 2004/12/17 DataFileDestinationAddress is new.
 * 
 * 2005/01/13 added formatting character for parameter substitution.
 * 
 * 2005/01/18 use an empty string as the default value for SubjectGroup and
 * ActiveSubjectGroups. We generally consider the empty string value as
 * 'not-to-be-used' value.
 * 
 * 2005/01/19 SessionGroup, RemainingSessionGroup, SessionRuns,
 * RemainingSessionRuns, JoinDataTrees are new.
 * 
 * 2005/01/28 Parameters for factorial data file
 * 
 * 2005/02/05 Fixed bug in resetValues() which made value and defaultValue
 * identical
 * 
 * 2005/03/17 added VoiceKey parameters
 * 
 * 2005/04/26 removed parameters for factorial data file
 * 
 * 2005/06/02 added ProcedureTime, SessionTime, BlockTime, TrialTime, fixed bug
 * with substitution() for the '%' character, added checkValueStack()
 * 
 * 2005/06/18 VideoFrameDuration
 * 
 * 2005/07/05 Fixed a wide spread bug in the context of using the
 * ExPar.set(ExParValue) method. This is no longer allowed. In order to set an
 * ExPar to an ExParValue we have to use ExPar.getValue().set(ExParValue). This
 * guarantees that the ExParValue stack of values is not broken by push()/pop()
 * operations at runtime. A new but private method ExPar.setValue() has been
 * introduced for replacing the ExParValue of an ExPar. This should only be done
 * at startup or when ExPar.resetValues() is called to reset the runtime table
 * of ExPar objects.
 * 
 * 2005/07/21 ResponsePosition added
 * 
 * 2005/08/23 containsValue() is new
 * 
 * 2006/07/29 PlotDataFileExtension, PlotDataDirectory
 * 
 * 2006/10/24 CIEWhitePoint, DeviceWhitePoint
 * 
 * 2006/11/15 AskForRuntimeParameterValues
 * 
 * 2007/03/09 set default data destination subdirectories to non-null values
 * ExperimentTitle is new.
 * 
 * 2007/11/02 fixed bug in substitution() for single character parameter names
 */
public class ExPar implements ExParTypeCodes {
	/** Each ExPar object has a value. */
	private ExParValue value;
	/**
	 * Each ExPar object also has a default value. This is used for
	 * reinitializing the experimental parameter in interactive settings.
	 */
	private ExParValue defaultValue;
	/**
	 * Each ExPar object also contains a short description of its meaning.
	 */
	private String hint;
	/**
	 * Tells us something about value ranges and special uses of this parameter.
	 */
	private int type;
	private Class typeClass = null;
	/**
	 * For numeric parameter types this defines their minimum value. This limit
	 * is only considered to be a hint which can be used for interactive
	 * parameter setting. It is not a fixed restriction.
	 */
	private double minValue = 0.0;
	/**
	 * For numeric parameter types this defines their maximum value. This limit
	 * is only considered to be a hint which can be used for interactive
	 * parameter setting. It is not a fixed restriction.
	 */
	private double maxValue = 0.0;
	/**
	 * This is a table of ExPar objects which are created at runtime or which
	 * need to be accessed by their names during runtime. Runtime parameters are
	 * entered into the runtime parameter table by the following sources:
	 * 
	 * <ol>
	 * <li>The design file parser enters runtime parameters into the runtime
	 * parameter table whenever a new parameter is defined in the design file.
	 * 
	 * <li>Whenever a display object is instantiated then the parameters of this
	 * instance are entered as runtime parameters with their instance names for
	 * later access.
	 * 
	 * <li>When a new runtime context is created then the display lists and
	 * their display objects are created and global assignments of the design
	 * file are executed. This will enter runtime parameters into the table.
	 * 
	 * </ol>
	 */
	private static HashMap runtimePars = new HashMap();

	// ---------------------------------------------------------
	// Constructors
	// ---------------------------------------------------------
	/**
	 * Instantiate a new experimental parameter.
	 * 
	 * @param type
	 *            the type of this parameter.
	 * @param value
	 *            the initial value of this parameter.
	 * @param hint
	 *            a short string indicating the parameter's meaning.
	 */
	public ExPar(int type, ExParValue value, String hint) {
		this(type, 0.0, 0.0, value, hint);
	}

	/**
	 * Instantiate a new experimental parameter.
	 * 
	 * @param edit
	 *            the editor type to be used for this parameter.
	 * @param type
	 *            the type class of this parameter.
	 * @param value
	 *            the initial value of this parameter.
	 * @param hint
	 *            a short string indicating the parameter's meaning.
	 */
	public ExPar(int edit, Class type, ExParValue value, String hint) {
		this(edit, 0.0, 0.0, value, hint);
		typeClass = type;
	}

	/**
	 * Instantiate a new experimental parameter.
	 * 
	 * @param min
	 *            the minimum value for this parameter.
	 * @param max
	 *            the maximum value for this parameter.
	 * @param value
	 *            the initial value of this parameter.
	 * @param hint
	 *            a short string indicating the parameter's meaning.
	 */
	public ExPar(int min, int max, ExParValue value, String hint) {
		this(INTEGER, (double) min, (double) max, value, hint);
	}

	/**
	 * Instantiate a new experimental parameter.
	 * 
	 * @param min
	 *            the minimum value for this parameter.
	 * @param max
	 *            the maximum value for this parameter.
	 * @param value
	 *            the initial value of this parameter.
	 * @param hint
	 *            a short string indicating the parameter's meaning.
	 */
	public ExPar(double min, double max, ExParValue value, String hint) {
		this(DOUBLE, min, max, value, hint);
	}

	/**
	 * Instantiate a new experimental parameter.
	 * 
	 * @param type
	 *            the type of this parameter.
	 * @param min
	 *            the minimum value for this parameter.
	 * @param max
	 *            the maximum value for this parameter.
	 * @param value
	 *            the initial value of this parameter.
	 * @param hint
	 *            a short string indicating the parameter's meaning.
	 */
	public ExPar(int type, int min, int max, ExParValue value, String hint) {
		this(type, (double) min, (double) max, value, hint);
	}

	/**
	 * Instantiate a new experimental parameter.
	 * 
	 * @param type
	 *            the type of this parameter.
	 * @param min
	 *            the minimum value for this parameter.
	 * @param max
	 *            the maximum value for this parameter.
	 * @param value
	 *            the initial value of this parameter.
	 * @param hint
	 *            a short string indicating the parameter's meaning.
	 */
	public ExPar(int type, double min, double max, ExParValue value, String hint) {
		this.type = type;
		this.minValue = min;
		this.maxValue = max;
		this.value = value;
		this.defaultValue = (ExParValue) (value.clone());
		this.hint = hint;
	}

	// ---------------------------------------------------------
	// End of Constructors
	// ---------------------------------------------------------
	/**
	 * Get this parameter's type code.
	 * 
	 * @return the type code of this parameter.
	 */
	public int getType() {
		return (type);
	}

	/**
	 * Get this parameter's type class.
	 * 
	 * @return the type class of this parameter.
	 */
	public Class getTypeClass() {
		return (typeClass);
	}

	/**
	 * Set this parameter's type code.
	 * 
	 * @param t
	 *            the new type code of this parameter.
	 */
	public void setType(int t) {
		type = t;
	}

	/**
	 * Get this parameter's assumption about its minimum possible value.
	 */
	public double getMinValue() {
		return minValue;
	}

	/** Set this parameter's minimum value for interactive editing. */
	public void setMinValue(double x) {
		minValue = x;
	}

	/** Set this parameter's minimum value for interactive editing. */
	public void setMinValue(int x) {
		minValue = x;
	}

	/**
	 * Get this parameter's assumption about its maximum possible value.
	 */
	public double getMaxValue() {
		return maxValue;
	}

	/** Set this parameter's maximum value for interactive editing. */
	public void setMaxValue(double x) {
		maxValue = x;
	}

	/** Set this parameter's maximum value for interactive editing. */
	public void setMaxValue(int x) {
		maxValue = x;
	}

	/** Return a parameter's type group. */
	public int getTypeGroup() {
		return (0);
	}

	/**
	 * Set this ExPar object's default value which is restored everytime when
	 * the parameter should be restored to its initial default state. The
	 * default value of an experimental parameter should only be set by its
	 * constructor or by initilizations which want to modify the defaults for a
	 * complete interactive session.
	 */
	/*
	 * public void setDefault(ExParValue value) { this.defaultValue = value; }
	 * 
	 * public ExParValue getDefault() { return defaultValue; }
	 */
	/**
	 * Set this ExPar object's ExParValue to the given value. This method MUST
	 * be private sincde no other object is allowed to replace this object's
	 * value. This is necessary since otherwise the value chain of ExParValue
	 * objects at runtime may be broken by push()/pop() operations.
	 */
	private void setValue(ExParValue value) {
		this.value = value;
	}

	public ExParValue getValue() {
		return (value);
	}

	public void set(ExParValueConstant value) {
		this.value.set(value);
	}

	public void set(ExParValueFunction value) {
		this.value.set(value);
	}

	public void set(int value) {
		this.value.set(value);
	}

	public void set(int[] value) {
		this.value.set(value);
	}

	public void set(double value) {
		this.value.set(value);
	}

	public void set(double[] value) {
		this.value.set(value);
	}

	public void set(double value1, double value2) {
		this.value.set(value1, value2);
	}

	public void set(double value1, double value2, double value3) {
		this.value.set(value1, value2, value3);
	}

	public void set(String value) {
		this.value.set(value);
	}

	public void set(String[] value) {
		this.value.set(value);
	}

	public void set(PxlColor value) {
		this.value.set(value);
	}

	public String getHint() {
		return (hint);
	}

	/** Return a parameter's flag value. */
	public boolean getFlag() {
		return (value.getInt() != 0);
	}

	/** Return a parameter's integer value. */
	public int getInt() {
		return (value.getInt());
	}

	/** Return a parameter's double value. */
	public double getDouble() {
		return (value.getDouble());
	}

	/**
	 * Return a parameter's string value. Substitute parameter names enclosed
	 * between substitution characters by their current values.
	 */
	public String getString() {
		String s = value.getString();
		// System.out.println("Getting string of: " + s);
		if (s == null)
			return (s);
		return (substitution(s));
	}
	private char substitutionChar = '%';
	private char escapeChar = '\\';
	private char formatChar = '@';

	/**
	 * Return a string which corresponds to the argument but has all
	 * experimental parameter names which are enclosed between substitution
	 * characters ('%') by their current values.
	 */
	private String substitution(String s) {
		// System.out.println("ExPar.substitution() Substitution for: " + s);
		int i = s.indexOf(substitutionChar);
		if (i < 0) {
			return (s);
		} else {
			StringBuffer t = new StringBuffer();
			int i0 = 0;
			boolean finished = false;
			while (!finished) {
				// System.out.println("   Buffer = " + t);
				int i1 = s.indexOf(substitutionChar, i0);
				if (i1 < 0) {
					t.append(s.substring(i0));
					// System.out.println("   i1 = " + i1 + " Buffer = " + t);
					finished = true;
				} else if ((i1 > i0) && (s.charAt(i1 - 1) == escapeChar)) {
					t.append(s.substring(i0, i1 - 1));
					t.append(substitutionChar);
					i0 = i1 + 1;
				} else {
					// found first substitution character
					int i2 = s.indexOf(substitutionChar, i1 + 2);
					if (i2 < 0) {
						t.append(s.substring(i0));
						// System.out.println("   i2 = " + i2 + " Buffer = " +
						// t);
						finished = true;
					} else {
						// found second substitution character
						if (i0 < i1) {
							t.append(s.substring(i0, i1));
							// System.out.println("   Buffer = " + t);
						}
						// Check for trailing format character
						String name = s.substring(i1 + 1, i2);
						int n = name.length();
						char fmt = '0';
						if ((n > 2) && (name.charAt(n - 2) == formatChar)) {
							fmt = name.charAt(n - 1);
							name = name.substring(0, n - 2);
						}
						//
						// System.out.println("ExPar.substitution() Parameter = "
						// + name);
						ExPar sx = get(name);
						if (sx != null) {
							// Force parameter evaluation
							ExParValue sxv = sx.getValue().getValue();
							// t.append(sx.getString());
							// If this is a string then remove the quotes
							String ss = sxv.toFormattedString(fmt);
							// System.out.println("ExPar.substitution() Value = "
							// + ss);
							if (ss.startsWith("\"") && (ss.endsWith("\"")))
								ss = StringExt.unquote(ss);
							t.append(ss);
						} else {
							t.append(s.substring(i1, i2 + 1));
						}
						// System.out.println("   Buffer = " + t);
						i0 = i2 + 1;
						finished = (i0 >= s.length());
					}
				}
			}
			return (t.toString());
		}
	}

	/** Return a parameter's integer array. */
	public int[] getIntArray() {
		
		return (value.getIntArray());
	}

	/** Return a parameter's double array. */
	public double[] getDoubleArray() {
		return (value.getDoubleArray());
	}

	/** Return a parameter's string array. */
	public String[] getStringArray() {
		String[] a = value.getStringArray();
		if (a == null)
			return null;
		int n = a.length;
		String[] b = new String[n];
		for (int i = 0; i < n; i++)
			b[i] = substitution(a[i]);
		return (b);
	}

	/** Return a parameter which contains color coordinates. */
	public PxlColor getPxlColor() {
		// System.out.println("Getting " + n);
		return (value.getPxlColor());
	}

	/** Return a parameter which contains color coordinates. */
	public Color getDevColor() {
		// System.out.println("Getting " + n);
		return (value.getDevColor());
	}

	/** Return a parameter's integer array. */
	public Point getLocation() {
		int[] p = value.getIntArray();
		if (p.length < 2) {
			throw new RuntimeException(
					"Location parameters must contain at least 2 values");
		}
		return (new Point(p[0], p[1]));
	}

	/**
	 * Clear the system's runtime table of dynamic ExPar instances by removing
	 * all runtime generated ExPar instances from the runtime parameter table.
	 * Also reset all experimental parameters to their default values and clear
	 * the runtime object table.
	 */
	public static void reset() {
		// System.out.println("ExPar.reset()");
		// new RuntimeException().printStackTrace();
		runtimePars.clear();
		resetValues();
		GlobalAssignments.exec();
	}

	/**
	 * Create a new ExPar object and enter it into the runtime table.
	 * 
	 * @param n
	 *            the name of the new parameter. This name is entered into the
	 *            runtime table of experimental parameters.
	 * @return an ExPar object whose type, value, and hint are undefined. If a
	 *         parameter with the given name already exists then null is
	 *         returned.
	 */
	public static ExPar create(String n) {
		// System.out.println("ExPar.create(): Trying to create parameter " + n
		// + " for the runtime table. ");
		ExPar p = null;
		if (get(n, false) == null) {
			p = new ExPar(UNKNOWN, new ExParValueUndefined(), null);
			runtimePars.put(n, p);
		}
		// System.out.println("ExPar.create(): Runtime Parameter " + n +
		// " created. ");
		return (p);
	}

	/**
	 * Enter an existing experimental parameter into the runtime table.
	 * 
	 * @param n
	 *            the name which is used to access the parameter.
	 * @param p
	 *            the ExPar object which should be entered.
	 */
	public static void enter(String n, ExPar p) {
		// System.out.println("ExPar.enter(): Trying to enter parameter " + n +
		// " into the runtime table. ");
		if (runtimePars.put(n, p) != null)
			new ParameterNameError("Parameter " + n + " already exists.");
		// System.out.println("Parameter " + n +
		// " entered into the runtime table. ");
	}

	/**
	 * Remove an experimental parameter from the runtime table.
	 * 
	 * @param n
	 *            the name of the parameter which should be removed.
	 */
	public static void remove(String n) {
		if (runtimePars.remove(n) == null)
			new ParameterNameError("Parameter " + n + " does not exist.");
		// System.out.println("Parameter " + n +
		// " removed from the runtime table. ");
	}

	/**
	 * Check whether a given experimental parameter is contained in the runtime
	 * table.
	 * 
	 * @param n
	 *            the name of the parameter which should be checked.
	 * @return true if the parameter is contained in the runtime table and false
	 *         otherwise.
	 */
	public static boolean contains(String n) {
		boolean r = runtimePars.containsKey(n);
		if (!r) {
			r = (get(n, false) != null);
		}
		return (r);
	}

	/**
	 * Check whether this parameter's value array contains a give value.
	 * 
	 * @param k
	 *            the value which should be checked.
	 * @return true if this paramater's value array contains the argument value.
	 */
	public boolean containsValue(int k) {
		int[] v = getIntArray();
		for (int i = 0; i < v.length; i++) {
			if (v[i] == k) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method sets an experimental parameter whose name is given as an
	 * argument to the given ExParValue
	 * 
	 * @param n
	 *            name of the ExPar object.
	 * @param v
	 *            the experimental parameter value to set.
	 */
	public static void set(String n, ExParValue v) {
		ExPar p = get(n);
		if (p != null) {
			p.getValue().set(v);
		} else {
			new ParameterNameError("ExPar.set(): Experimental parameter " + n
					+ " does not exist.");
		}
	}

	/**
	 * This method returns a reference to the ExPar object whose name is given
	 * as an argument. It handles static and dynamic runtime parameters
	 * transparently. Parameters of objects which are created during run time
	 * are stored in the run time parameter table. Thus the run time parameter
	 * table is always searched first in order to find a parameter.
	 * 
	 * @param n
	 *            name of the parameter.
	 * @return the ExPar object whose name is given as an argument.
	 */
	public static ExPar get(String n) {
		return get(n, true);
	}

	/**
	 * This method returns a reference to the ExPar object whose name is given
	 * as an argument. It handles static and dynamic runtime parameters
	 * transparently. Parameters of objects which are created during run time
	 * are stored in the run time parameter table. Thus the run time parameter
	 * table is always searched first in order to find a parameter.
	 * 
	 * @param n
	 *            name of the parameter.
	 * @param errcheck
	 *            if true and the parameter does not exist then throw an error.
	 * @return the ExPar object whose name is given as an argument.
	 */
	public static ExPar get(String n, boolean errcheck) {
		// System.out.println("ExPar.get(String, boolean): Trying to find parameter "
		// + n);
		// Try to find the parameter in the runtime table
		Object obj = runtimePars.get(n);
		// If it was found then return it
		if (obj != null) {
			// System.out.println(" ... found " + (ExPar)obj);
			return (ExPar) obj;
		} else {
			return (ExPar) get("de.pxlab.pxl", "ExPar", n, errcheck);
		}
	}

	/**
	 * Get the value of a static field of a class.
	 * 
	 * @param packageName
	 *            the name of the package containing the class.
	 * @param className
	 *            the class name.
	 * @param fieldName
	 *            the name of the field.
	 * @param errcheck
	 *            if true then a fatal error results if the field cannot be
	 *            reflected. If false then errors are ignored and null is
	 *            returned.
	 * @return the object which is the value of the requested field or null if
	 *         the field could not be reflected.
	 */
	public static Object get(String packageName, String className,
			String fieldName, boolean errcheck) {
		// System.out.println("ExPar.get(): Trying to find parameter " +
		// packageName + "." + className + "." + fieldName);
		Object obj = null;
		String err = null;
		try {
			Class cls = Class.forName(packageName + "." + className);
			try {
				Field fld = cls.getField(fieldName);
				try {
					obj = fld.get(null);
				} catch (IllegalAccessException iae) {
					if (errcheck)
						err = "Illegal access to " + fieldName;
				}
			} catch (NoSuchFieldException nsfe) {
				if (errcheck)
					err = "Class " + packageName + "." + className
							+ " does not have a field named " + fieldName;
				// new RuntimeException().printStackTrace();
			}
		} catch (ClassNotFoundException cnfe) {
			if (errcheck)
				err = "Oh! Class " + packageName + "." + className
						+ " not found!";
		}
		if (errcheck && (err != null)) {
			new ParameterNameError("ExPar.get(): " + err);
			obj = new ExPar(UNKNOWN, new ExParValue(0), "");
		}
		return obj;
	}

	/**
	 * This method is called when entering an experimental node which has an
	 * argument list of parameters assigned to it. This means that these
	 * parameters have to be localized within the node's scope. This is done by
	 * pushing the individual parameter's value one level down in this
	 * parameter's value stack. This effectively hides the original parameter
	 * value and creates a new local value on top of the stack. If the
	 * ExParValue argument is a null reference then the value of the new top
	 * parameter is a copy of the previous top parameter.
	 * 
	 * @param x
	 *            the ExParValue object which will become then new top level
	 *            value of this ExPar object's value stack.
	 */
	public void push(ExParValue x) {
		// System.out.println("ExPar.push() currently on top " +
		// value.toString() + " pushing " + x.toString());
		ExParValue v = x.isUndefined() ? (ExParValue) value.clone()
				: (ExParValue) x.clone();
		v.next = value;
		value = v;
		// System.out.println("ExPar.push() New value: " + value);
	}

	/**
	 * This method is called when leaving an experimental node with local
	 * parameters. We pop the experimental parameter one level up in this
	 * parameter's individual value stack.
	 * 
	 * @return the ExParValue object which was popped off from this ExPar
	 *         object's value stack.
	 */
	public ExParValue pop() {
		ExParValue v = null;
		if (value.next != null) {
			v = value;
			value = value.next;
			// System.out.println("ExPar.pop() Popping " + v.toString() +
			// " now on top: " + value.toString());
		} else {
			new ParameterValueError("ExPar.pop() without preceding push()");
		}
		/*
		 * if (value == null) {
		 * System.out.println("ExPar.pop(): Value stack empty."); } else if
		 * (value.next == null) {
		 * System.out.println("ExPar.pop(): A single value is on the stack: " +
		 * value.toString()); } else {
		 * System.out.println("ExPar.pop(): Value stack: " + value.toString() +
		 * ", " + value.next.toString()); }
		 */
		return (v);
	}

	public String toString() {
		// return("ExPar[" + getName() +";" + getValue().toString() + "]");
		return (getValue().toString());
	}

	/**
	 * Return the key names of all entries in the runtime parameter table.
	 */
	public static String[] getRuntimeParNames() {
		Object[] ks = runtimePars.keySet().toArray();
		String[] pn = new String[ks.length];
		for (int i = 0; i < ks.length; i++)
			pn[i] = (String) ks[i];
		return StringExt.sort(pn);
	}

	/**
	 * Check the value stack of every experimental parameter contained in the
	 * runtime table. It is an error if the value.next pointer is non-null.
	 */
	public static void checkValueStack() {
		String[] n = getAllParNames();
		for (int i = 0; i < n.length; i++) {
			ExParValue v = get(n[i]).getValue();
			// System.out.println("ExPar.checkValueStack() " + n[i] + " = [" + v
			// + ", " + v.next + "]");
			if (v.next != null) {
				new ParameterValueError(
						"ExPar.checkValueStack() internal error: Value stack error for "
								+ n[i]);
			}
		}
	}

	public static String[] getSystemParNames() {
		ArrayList aPars = new ArrayList();
		String cn = "de.pxlab.pxl.ExPar";
		try {
			Class cls = Class.forName(cn);
			Field[] fld = cls.getFields();
			int n2 = fld.length;
			for (int j = 0; j < n2; j++) {
				Class fc = fld[j].getType();
				String fcn = fc.getName();
				if (fcn.equals("de.pxlab.pxl.ExPar"))
					aPars.add(fld[j].getName());
			}
		} catch (ClassNotFoundException cnfe) {
			new InternalError(
					"ExPar.getSystemParNames() internal error: Class " + cn
							+ " not found!");
		}
		int n = aPars.size();
		String[] sa = new String[n];
		for (int i = 0; i < n; i++) {
			sa[i] = (String) (aPars.get(i));
		}
		return (StringExt.sort(sa));
	}

	public static String[] getAllParNames() {
		String[] rtp = getRuntimeParNames();
		String[] sp = getSystemParNames();
		String[] p = new String[rtp.length + sp.length];
		int n = 0;
		for (int i = 0; i < rtp.length; i++)
			p[n++] = rtp[i];
		for (int i = 0; i < sp.length; i++)
			p[n++] = sp[i];
		return p;
	}

	/**
	 * Get only the ParNames which are relevant for the given parentNode
	 */
	public static String[] getParNamesFor(ExDesignNode parentNode) {
		String[] ap = getAllParNames();
		if (parentNode.getType() == de.pxlab.pxl.ExDesignNode.AssignmentGroupNode) {
			String[] sp = getSystemParNames();
			return sp;
		} else if (parentNode.getType() == de.pxlab.pxl.ExDesignNode.DisplayNode) {
			ArrayList l = new ArrayList(ap.length);
			for (int i = 0; i < ap.length; i++) {
				if (ap[i].startsWith(parentNode.getInstanceName())) {
					l.add(ap[i]);
				}
			}
			l.trimToSize();
			String[] pn = new String[l.size()];
			for (int i = 0; i < l.size(); i++) {
				pn[i] = (String) l.get(i);
			}
			return pn;
		} else {
			return ap;
		}
	}

	/**
	 * Set the experimental parameter Date such that it reflects the current
	 * date and time.
	 */
	public static void setDate() {
		Date.set(DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.LONG, Locale.getDefault()).format(
				new java.util.Date()));
	}
	// ---------------------------------------------------------------------
	// Here comes the list of static experimental parameters
	// ---------------------------------------------------------------------
	/** Is true at runtime if we are running as an applet. */
	public static ExPar AppletSystem = new ExPar(FLAG, new ExParValue(0),
			"Applet system flag");
	/** A string which stores the runtime assignments from the command line. */
	public static ExPar RuntimeAssignments = new ExPar(RTDATA,
			new ExParValueUndefined(), "Runtime parameter assignments string");
	/** A string which stores the command line arguments. */
	public static ExPar CommandLine = new ExPar(RTDATA,
			new ExParValueUndefined(), "Command line string");
	/** Step size for intensity changes in adjustment display methods. */
	public static ExPar AdjustableStep = new ExPar(SMALL_DOUBLE,
			new ExParValue(10), "The step size for adjustable parameters");
	/** Range limits for the adjustable parameter in adjustment display methods. */
	public static ExPar AdjustableParameterLimits = new ExPar(DOUBLE,
			new ExParValue(0.0), "Range limits for adjustable parameters");
	/**
	 * Key code for decreasing intensity in adjustment display methods.
	 * 
	 * @see KeyCodes
	 */
	public static ExPar AdjustmentDownKey = new ExPar(KEYCODE,
			new ExParValueConstant("de.pxlab.pxl.KeyCodes.DOWN_KEY"),
			"Key code for 'down' adjustments");
	/**
	 * Key code for increasing intensity in adjustment display methods.
	 * 
	 * @see KeyCodes
	 */
	public static ExPar AdjustmentUpKey = new ExPar(KEYCODE,
			new ExParValueConstant("de.pxlab.pxl.KeyCodes.UP_KEY"),
			"Key code for 'up' adjustments");
	/**
	 * Stop key for adjustment responses in adjustment display methods.
	 * 
	 * @see KeyCodes
	 */
	public static ExPar AdjustmentStopKey = new ExPar(KEYCODE,
			new ExParValueConstant("de.pxlab.pxl.KeyCodes.SPACE_KEY"),
			"Key code for stopping adjustment responses");
	/**
	 * Key code for 'no' responses.
	 * 
	 * @see KeyCodes
	 */
	public static ExPar NoKey = new ExPar(KEYCODE, new ExParValueConstant(
			"de.pxlab.pxl.KeyCodes.RIGHT_KEY"), "Key code for 'no' responses");
	/**
	 * Key code for 'yes' responses.
	 * 
	 * @see KeyCodes
	 */
	public static ExPar YesKey = new ExPar(KEYCODE, new ExParValueConstant(
			"de.pxlab.pxl.KeyCodes.LEFT_KEY"), "Key code for 'yes' responses");
	/**
	 * Stop key code for a timer which has the TimerBitCodes.STOP_KEY_TIMER_BIT
	 * set. This timer results in call-back signals to the display object via
	 * keyResponse() until the response code is identical to StopKey.
	 * 
	 * @see KeyCodes
	 */
	public static ExPar StopKey = new ExPar(KEYCODE, new ExParValueConstant(
			"de.pxlab.pxl.KeyCodes.SPACE_KEY"),
			"Key code for 'end/stop/done' responses");
	/**
	 * Stores the response time for timing intervals which span more than a
	 * single timing group.
	 */
	public static ExPar ResponseTime = new ExPar(RTDATA, new ExParValue(0),
			"Multiple display response time");
	/**
	 * Stores the response code for timing intervals which span more than a
	 * single timing group.
	 */
	public static ExPar ResponseCode = new ExPar(RTDATA, new ExParValue(0),
			"Multiple display response code");
	/**
	 * Stores the position of the mouse for mouse responses in timing intervals
	 * which span more than a single timing group.
	 */
	public static ExPar ResponsePosition = new ExPar(RTDATA, new ExParValue(0,
			0), "Multiple display mouse response position");
	/**
	 * Counts errors across trials. Errors are generated by Display objects like
	 * de.pxlab.pxl.display.Feedback.
	 */
	public static ExPar ResponseErrorCount = new ExPar(RTDATA,
			new ExParValue(0), "Response error counter");
	/** Threshold for the timer of type TimerCodes.VOICE_KEY_TIMER. */
	public static ExPar VoiceKeyThreshold = new ExPar(DOUBLE, new ExParValue(
			800.0), "Voice key timer threshold");
	/**
	 * Size of the recording and checking buffer for the
	 * TimerCodes.VOICE_KEY_TIMER type of timer.
	 */
	public static ExPar VoiceKeyRecordingBufferSize = new ExPar(SMALL_INT,
			new ExParValue(44), "Voice key recording buffer size");
	/**
	 * Flag to stop voice recording immediately after the voice key threshold
	 * has been passed.
	 */
	public static ExPar VoiceKeyStopImmediately = new ExPar(FLAG,
			new ExParValue(1),
			"Flag to stop voice key recording after threshold has been passed");
	/** Every trial is presented TrialFactor times. */
	public static ExPar TrialFactor = new ExPar(0, 1000, new ExParValue(1),
			"Trial replication factor");
	/** Every block is presented BlockFactor times. */
	public static ExPar BlockFactor = new ExPar(SMALL_INT, new ExParValue(1),
			"Block replication factor");
	/** Flag to indicate that the blocks in a session should be randomized. */
	public static ExPar RandomizeBlocks = new ExPar(FLAG, new ExParValue(0),
			"Block randomization flag");
	/** Flag to indicate that the trials in a block should be randomized. */
	public static ExPar RandomizeTrials = new ExPar(FLAG, new ExParValue(1),
			"Trial randomization flag");
	/** Active language (not yet implemented). */
	public static ExPar Language = new ExPar(SMALL_INT, new ExParValue(0),
			"Current language");
	/** Name of the experimental design file. */
	public static ExPar DesignFileName = new ExPar(STRING, new ExParValue(""),
			"Name of the experimental design file");
	/**
	 * Contains the display device mode number which is used in an experiment.
	 * Set from the command line.
	 */
	public static ExPar DisplayDeviceMode = new ExPar(RTDATA,
			new ExParValue(0), "Display device mode");
	/** Full screen mode flag. is true if we want to run int full screen mode. */
	// public static ExPar FullScreenMode = new ExPar(FLAG, new ExParValue(0),
	// "Full Screen Mode Flag");
	/** Full screen device number: Primary (1) or secondary(2) screen. */
	// public static ExPar FullScreenDevice = new ExPar(INTEGER_0_2, new
	// ExParValue(2), "Full Screen on primary (1) or secondary (2) screen");
	/**
	 * Stereographic display mode flag. Is true if we want to run on a stereo
	 * display system.
	 */
	public static ExPar Stereographic = new ExPar(FLAG, new ExParValue(0),
			"Stereographic display mode flag");
	/** Flag to hide the mouse cursor whenever possible. */
	public static ExPar HideCursor = new ExPar(FLAG, new ExParValue(0),
			"Hide the cursor if possible");
	/** Screen or display window height */
	// public static ExPar ScreenHeight = new ExPar(VERSCREENSIZE, new
	// ExParValue(600), "Display screen height");
	/** Screen or display window width */
	// public static ExPar ScreenWidth = new ExPar(HORSCREENSIZE, new
	// ExParValue(800), "Display screen width");
	/** Horizontal display screen resolution in pixel per metric unit. */
	public static ExPar HorizontalScreenResolution = new ExPar(HORSCREENSIZE,
			new ExParValue(45),
			"Horizontal display screen resolution in pixel per metric unit");
	/** Vertical display screen resolution in pixel per metric unit. */
	public static ExPar VerticalScreenResolution = new ExPar(VERSCREENSIZE,
			new ExParValue(45),
			"Vertical display screen resolution in pixel per metric unit");
	/** Screen background color. */
	public static ExPar ScreenBackgroundColor = new ExPar(COLOR,
			new ExParValue(new ExParExpression(ExParExpression.BLACK)),
			"Display screen background color");
	public static ExPar ScreenParameterFile = new ExPar(STRING, new ExParValue(
			"screen.pxd"), "Screen parameter file");
	/**
	 * Subject identification code. By default the name of the data file is
	 * derived from the subject code.
	 */
	public static ExPar SubjectCode = new ExPar(STRING, new ExParValue(""),
			"Subject identification code");
	/**
	 * Every subject belongs to a subject group and this is the code of the
	 * group to which the current subject belongs. If more than a single subject
	 * group is required then this parameter should be defined as a covariate
	 * factor with the possible subject group codes as its factor levels.
	 */
	public static ExPar SubjectGroup = new ExPar(SMALL_INT, new ExParValue(0),
			"Subject group code for current subject");
	/**
	 * The size of a subject group. If subject group sizes differ between groups
	 * then this parameter may be included in the operationalization of
	 * SubjectGroup as a covariat factor.
	 */
	public static ExPar SubjectGroupSize = new ExPar(SMALL_INT, new ExParValue(
			0), "Size of a subject group");
	/**
	 * Defines the sessions which have to be run by a single subject and which
	 * may be distributed across multiple runs of the program by using parameter
	 * SessionRuns. If the current SessionGroup is [1, 3, 4], then the current
	 * subject has to run sessions 1, 3, and 4. Session groups may be assigned
	 * to subject groups by using the parameter SubjectGroup as a Covariate
	 * parameter and SessionsGroup as its operationalization. By default
	 * SessionGroup is undefined and the program behaves as if it were equal to
	 * an array containing all session ids in sequence: [1, 2, 3, ...].
	 */
	public static ExPar SessionGroup = new ExPar(SMALL_INT, new ExParValue(0),
			"Session group assigned to a subject group");
	/**
	 * If a subject has to execute multiple session runs then this parameter
	 * contains the remaining sessions which have to be run by the current
	 * subject. The subject's data file will contain the remaining session group
	 * for the next run. This parameter should never be set int a design file.
	 */
	public static ExPar RemainingSessionGroup = new ExPar(RTDATA,
			new ExParValue(0),
			"Remaining session group for the current subject");
	/**
	 * Number of sessions in every single run for designs where a single subject
	 * has to do multiple runs with multiple sessions. If defined then this will
	 * be an array of integers. Every entry corresponds to a single run for a
	 * single subject. A single entry defines the number of sessions to be
	 * executed at the respective run. Thus [2, 1] as an example means that the
	 * first run for a given subject contains two sessions while the second run
	 * for the same subject contains a single session. The sessions assigned to
	 * a subject are defined by the parameter SessionsGroup. Thus the value [2,
	 * 1] means that the subject should run the first two sessions of its
	 * SessionGroup int the first run and the third session of its SessionGroup
	 * int the second run.
	 */
	public static ExPar SessionRuns = new ExPar(SMALL_INT, new ExParValue(0),
			"Number of sessions in a run");
	/**
	 * If a subject has to execute multiple session runs then this parameter
	 * contains the remaining session runs for the current subject. The
	 * subject's data file will contain the remaining session runs for the next
	 * run. At any single run only the first element of this array is used. It
	 * tells PXLab how many sessions of RemainingSessionGroup have to be
	 * executed. This parameter should never be set int a design file.
	 */
	public static ExPar RemainingSessionRuns = new ExPar(RTDATA,
			new ExParValue(0), "Number of sessions in a run");
	/**
	 * Join the data trees of multiple runs for a single subject. If this flag
	 * is set then the data tree of a single subject will be cumulative such
	 * that the most recent data tree file contains the data from previous
	 * sessions also.
	 */
	public static ExPar JoinDataTrees = new ExPar(FLAG, new ExParValue(0),
			"Join Data Trees of Multiple Sessions");
	/**
	 * If true and there are any runtime parameters being defined, then a dialog
	 * pops up at runtime and asks for the values of these runtime parameters.
	 * Runtime parameters are parameters of the Experiment() node and parameters
	 * defined as covariate factors in a CovariateFactor() node.
	 */
	public static ExPar AskForRuntimeParameterValues = new ExPar(FLAG,
			new ExParValue(1), "Ask for runtime parameter values at runtime");
	/**
	 * This is the set of subject group codes which should run the next block of
	 * trials. If the current subject's group code is not contained in the list
	 * of active subject groups then the next block is not presented to the
	 * current subject. This parameter is checked immediately before a block of
	 * trials is executed.
	 */
	public static ExPar ActiveSubjectGroups = new ExPar(SMALL_INT,
			new ExParValue(""), "Currently active subject groups");
	/** Flag to switch on the runtime protocol. */
	public static ExPar RuntimeProtocol = new ExPar(FLAG, new ExParValue(0),
			"Flag to switch on a runtime protocol");
	/** Runtime protocol file name. */
	public static ExPar ProtocolFileName = new ExPar(STRING,
			new ExParValue(""), "Name of the protocol data file");
	/**
	 * Flag to indicate that formatted data of blocks and trials should be
	 * stored in a data file.
	 */
	public static ExPar StoreData = new ExPar(FLAG, new ExParValue(1),
			"Store data flag");
	/**
	 * Flag to indicate that data should be stored in a data tree file.
	 */
	public static ExPar StoreDataTree = new ExPar(FLAG, new ExParValue(1),
			"Store data tree flag");
	/** Flag to indicate that data processing is enabled. */
	public static ExPar DataProcessingEnabled = new ExPar(FLAG, new ExParValue(
			0), "Enable data processing");
	/**
	 * The root directory for data files or subdirectories or the URL where data
	 * files are to be sent.
	 */
	public static ExPar DataFileDestination = new ExPar(STRING, new ExParValue(
			"."), "Destination directory or URL of data files");
	/**
	 * Trial data subdirectory of DataFileDestination. If this parameter is
	 * nonempty the it specifies a subdirectory of the DataFileDestination where
	 * to put raw trial data files.
	 */
	public static ExPar TrialDataDirectory = new ExPar(STRING, new ExParValue(
			"dat"), "Trial data subdirectory");
	/**
	 * Data tree subdirectory. If this parameter is nonempty the it specifies a
	 * subdirectory of the DataFileDestination where to put data tree files.
	 */
	public static ExPar DataTreeDirectory = new ExPar(STRING, new ExParValue(
			"dtr"), "Data tree subdirectory");
	/**
	 * Plot data subdirectory. If this parameter is nonempty the it specifies a
	 * subdirectory of the DataFileDestination where to put plot data files.
	 */
	public static ExPar PlotDataDirectory = new ExPar(STRING, new ExParValue(
			"pld"), "Plot data subdirectory");
	/**
	 * Processed data subdirectory. If this parameter is nonempty the it
	 * specifies a subdirectory of the DataFileDestination where to put data
	 * processing results.
	 */
	public static ExPar ProcessedDataDirectory = new ExPar(STRING,
			new ExParValue("pdt"), "Data processing results subdirectory");
	/**
	 * Final data file destination E-mail address for data file transfers via
	 * HTTP. The receiver of a data file via HTTP should send the data file to
	 * this E-mail address. This parameter is currently ony used by
	 * URLDataWriter objects.
	 */
	public static ExPar DataFileDestinationAddress = new ExPar(STRING,
			new ExParValue(""), "Data File Destination E-mail Address");
	/**
	 * HTTP Request method for data transfer to the data file destination
	 * directory via HTTP. Possible options are 'GET' or 'POST' (default).
	 */
	public static ExPar HTTPRequestMethod = new ExPar(STRING, new ExParValue(
			"POST"), "HTTP protocol request method for data file transfer");
	/** Data file name. */
	public static ExPar DataFileName = new ExPar(STRING, new ExParValue(""),
			"Name of the formatted data file");
	/** Statistical results file name. */
	// public static ExPar StatFileName = new ExPar(STRING, new ExParValue(""),
	// "Name of file for statistical results");
	/** Data file name extension. */
	public static ExPar DataFileExtension = new ExPar(STRING, new ExParValue(
			".dat"), "Formatted data file extension");
	/** Plot data file name extension. */
	public static ExPar PlotDataFileExtension = new ExPar(STRING,
			new ExParValue(".pld"), "Plot data files extension");
	/** Data tree file name extension. */
	public static ExPar DataTreeFileExtension = new ExPar(STRING,
			new ExParValue(".dtr"), "Data tree file name extension");
	/** Data analysis object output file name extension. */
	public static ExPar ProcessedDataFileExtension = new ExPar(STRING,
			new ExParValue(".html"),
			"Data processing results file name extension");
	/**
	 * Data file header format. This string is printed into the data file before
	 * any data are collected.
	 */
	public static ExPar DataFileHeader = new ExPar(STRING, new ExParValue(""),
			"Format of the first line in the formatted data file");
	/**
	 * Data file trial data format. This string is printed into the data file
	 * after a trial has been finished.
	 */
	public static ExPar DataFileTrialFormat = new ExPar(STRING, new ExParValue(
			""), "Format of a single line of trial data");
	/**
	 * Data file block data format. This string is printed into the data file
	 * after a block has been finished.
	 */
	public static ExPar DataFileBlockFormat = new ExPar(STRING, new ExParValue(
			""), "Format of a single line of block data");
	/**
	 * Generated name array of factorial data. This array is used to create the
	 * single trial output format in case the parameter DataFileTrialFormat is
	 * not defined. If DataFileTrialFormat is defined then this array is not
	 * used. The array is created automatically from the information contained
	 * in the Factors() node of the design file.
	 */
	public static ExPar FactorialDataFormat = new ExPar(RTDATA, new ExParValue(
			""), "Factorial data format");
	/**
	 * Session descriptor file data format. This determines the format of an
	 * experiment's entry into the experiment log file. This file collects log
	 * data on experimental runs.
	 */
	public static ExPar SessionDescriptorFormat = new ExPar(STRING,
			new ExParValue(
					"%ExperimentName% - Date: %Date% - File: %DataFileName%"),
			"Session descriptor format");
	/**
	 * Adaptive display procedure code. Selects the adaptive procedure type.
	 * 
	 * @see AdaptiveControl
	 * @see AdaptiveProcedureCodes
	 */
	public static ExPar AdaptiveProcedure = new ExPar(GEOMETRY_EDITOR,
			AdaptiveProcedureCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.AdaptiveProcedureCodes.NON_ADAPTIVE"),
			"Adaptive procedure type");
	/**
	 * Stopping rule code for adaptive procedures. Selects the stopping rule for
	 * adaptive procedures.
	 * 
	 * @see AdaptiveControl
	 * @see AdaptiveStopCodes
	 */
	public static ExPar AdaptiveStoppingRule = new ExPar(GEOMETRY_EDITOR,
			AdaptiveStopCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.AdaptiveStopCodes.DONT_STOP"),
			"Adaptive procedure stopping rule");
	/**
	 * Adaptive procedure result computation method code. Selects the type of
	 * result computation used for adaptive procedures.
	 * 
	 * @see AdaptiveControl
	 * @see AdaptiveResultCodes
	 */
	public static ExPar AdaptiveResultComputation = new ExPar(GEOMETRY_EDITOR,
			AdaptiveResultCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.AdaptiveResultCodes.NO_RESULTS"),
			"Adaptive result computation method");
	/**
	 * ID Code for trials of a single adaptive sequence. All trials in a block
	 * which have the same ID code are assumed to belong to the same adaptive
	 * sequence. This makes it possible to mix several adaptive sequences within
	 * a singlew block.
	 * 
	 * @see AdaptiveControl
	 */
	public static ExPar AdaptiveSequenceID = new ExPar(SMALL_INT,
			new ExParValue(0),
			"ID code for trials of a single adaptive sequence");
	/**
	 * Adaptive procedure state. The adaptive procedure state parameter stores
	 * the current state of an adaptive sequence.
	 * 
	 * @see AdaptiveControl
	 */
	public static ExPar AdaptiveState = new ExPar(SMALL_INT, new ExParValue(0),
			"Adaptive procedure state");
	/**
	 * Name of the adaptive parameter. This parameter is modified by the
	 * adaptive control procedure.
	 * 
	 * @see AdaptiveControl
	 */
	public static ExPar AdaptiveParameter = new ExPar(EXPARNAME,
			new ExParValue(""), "Name of the adaptive parameter");
	/**
	 * Admissible Range of values for the adaptive parameter. If this is zero
	 * then no limits are assumed.
	 * 
	 * @see AdaptiveControl
	 */
	public static ExPar AdaptiveParameterLimits = new ExPar(DOUBLE,
			new ExParValue(0),
			"Admissible range of values for the adaptive parameter");
	/**
	 * Name of the adaptive procedure response parameter. This is the parameter
	 * which contains the response controlling the adaptive procedure. Usually
	 * this will be the response parameter if a display object in the trial.
	 * 
	 * @see AdaptiveControl
	 */
	public static ExPar AdaptiveResponseParameter = new ExPar(EXPARNAME,
			new ExParValue(""), "Name of the response parameter");
	/**
	 * Counter which counts trials within a single adaptive sequence.
	 * 
	 * @see AdaptiveControl
	 */
	public static ExPar AdaptiveTrialCounter = new ExPar(RTDATA,
			new ExParValue(0), "Counts trials within an adaptive sequence");
	/**
	 * Counter which counts turn points within an adaptive sequence.
	 * 
	 * @see AdaptiveControl
	 */
	public static ExPar AdaptiveTurnPointCounter = new ExPar(RTDATA,
			new ExParValue(0), "Counts turning points");
	/**
	 * Limit for the number of turn points used by the appropriate stopping
	 * rule. A proper stopping rule is to procede until a certain even number of
	 * turn points have been found.
	 * 
	 * @see AdaptiveControl
	 * @see AdaptiveStopCodes
	 */
	public static ExPar AdaptiveTurnPointLimit = new ExPar(0, 30000,
			new ExParValue(30000), "Presentation limit for turning points");
	/**
	 * Number of points used for computing the result of an adaptive sequence.
	 * Should be an even number of points.
	 * 
	 * @see AdaptiveControl
	 * @see AdaptiveResultCodes
	 */
	public static ExPar AdaptiveComputingPoints = new ExPar(0, 30000,
			new ExParValue(30000),
			"Number of points used for computing results");
	/**
	 * Initial step size in an adaptive procedure.
	 * 
	 * @see AdaptiveControl
	 */
	public static ExPar AdaptiveStepSize = new ExPar(SMALL_DOUBLE,
			new ExParValue(1.0), "Adaptive procedure initial step size");
	/**
	 * Minimum step size in an adaptive procedure. Some stopping rules start to
	 * count only when the minimum step size has been reached.
	 * 
	 * @see AdaptiveControl
	 * @see AdaptiveStopCodes
	 */
	public static ExPar AdaptiveStepSizeMinimum = new ExPar(SMALL_DOUBLE,
			new ExParValue(0.0), "Adaptive procedure minimum step size");
	/**
	 * Divisor for changing the step size during an adaptive procedure. For each
	 * required change the step size is divided by this value.
	 * 
	 * @see AdaptiveControl
	 */
	public static ExPar AdaptiveStepDivisor = new ExPar(SMALL_DOUBLE,
			new ExParValue(1.0), "Adaptive step size divisor");
	/**
	 * Decrement for the step size divisor of an adaptive procedure. The divisor
	 * of the step size may be changed at subsequent steps.
	 * 
	 * @see AdaptiveControl
	 */
	public static ExPar AdaptiveStepDivisorDecrement = new ExPar(SMALL_DOUBLE,
			new ExParValue(0.0), "Adaptive step divisor decrement");
	/**
	 * Increment for the step size divisor of an adaptive procedure. The divisor
	 * of the step size may be changed at subsequent steps.
	 * 
	 * @see AdaptiveControl
	 */
	public static ExPar AdaptiveStepDivisorIncrement = new ExPar(SMALL_DOUBLE,
			new ExParValue(0.0), "Adaptive step divisor increment");
	/**
	 * Upward step size factor for weighted up-and-down procedure.
	 * 
	 * @see AdaptiveProcedureCodes
	 * @see AdaptiveControl
	 */
	public static ExPar AdaptiveUpwardStepFactor = new ExPar(SMALL_DOUBLE,
			new ExParValue(1.0), "Upward step size factor");
	/**
	 * Flag to remove trailing trials after an adaptive sequence is finished.
	 * 
	 * @see AdaptiveControl
	 */
	public static ExPar AdaptiveRemoveTrailingTrials = new ExPar(FLAG,
			new ExParValue(1),
			"Remove trailing trials after a sequence is finished");
	/**
	 * Flag to switch on a detailed protocol of the adaptive procedure.
	 * 
	 * @see AdaptiveControl
	 */
	public static ExPar AdaptiveProtocol = new ExPar(FLAG, new ExParValue(0),
			"Print a detailed protocol of the adaptive sequences");
	/**
	 * If this flag is true then maximum likelihood estimation is used by the
	 * estimation procedures of the adaptive stimulus control system. If the
	 * flag is false then least square error minimization is used.
	 * 
	 * @see AdaptiveControl
	 */
	public static ExPar AdaptiveMLEstimation = new ExPar(FLAG,
			new ExParValue(1), "Maximum likelihood estimation flag");
	/**
	 * Assumed guessing rate for psychometric functions estimation.
	 * 
	 * @see AdaptiveResultCodes
	 * @see AdaptiveControl
	 */
	public static ExPar AdaptiveGuessingRate = new ExPar(PROPORT,
			new ExParValue(0.0), "Guessing rate in adaptive procedure trials");
	/**
	 * Assumed lapsing rate for psychometric functions estimation.
	 * 
	 * @see AdaptiveResultCodes
	 * @see AdaptiveControl
	 */
	public static ExPar AdaptiveLapsingRate = new ExPar(PROPORT,
			new ExParValue(0.0), "Lapsing rate in adaptive procedure trials");
	/**
	 * The quantiles which should be returned after psychometric function
	 * estimation. The quantiles are returned in AdaptiveResults.
	 * 
	 * @see AdaptiveResultCodes
	 * @see AdaptiveControl
	 */
	public static ExPar AdaptiveQuantiles = new ExPar(PROPORT, new ExParValue(
			0.5), "The quantiles which should be estimated");
	/**
	 * The results array for parameter estimates after adaptive procedures.
	 * 
	 * @see AdaptiveResultCodes
	 * @see AdaptiveControl
	 */
	public static ExPar AdaptiveResults = new ExPar(RTDATA,
			new ExParValue(0.0), "Adaptive procedure parameter results");
	/** Runtime date. */
	public static ExPar Date = new ExPar(STRING, new ExParValue(
			new java.util.Date().toString()), "Date");
	/**
	 * Time of day when the most recent Procedure list had been started, given
	 * in milliseconds. Timing precision is limited.
	 */
	public static ExPar ProcedureTime = new ExPar(RTDATA, new ExParValue(0),
			"Procedure start time");
	/**
	 * Time of day when the most recent Session list had been started, given in
	 * milliseconds. Timing precision is limited.
	 */
	public static ExPar SessionTime = new ExPar(RTDATA, new ExParValue(0),
			"Session start time");
	/**
	 * Time of day when the most recent Block list had been started, given in
	 * milliseconds. Timing precision is limited.
	 */
	public static ExPar BlockTime = new ExPar(RTDATA, new ExParValue(0),
			"Block start time");
	/**
	 * Time of day when the most recent Trial had been started, given in
	 * milliseconds. Timing precision is limited.
	 */
	public static ExPar TrialTime = new ExPar(RTDATA, new ExParValue(0),
			"Trial start time");
	/**
	 * Counts the number of trials which have been run. The counter is
	 * incremented only after the trial has been finished.
	 */
	public static ExPar TrialCounter = new ExPar(RTDATA, new ExParValue(0),
			"Counts the number of trials which have been run");
	/**
	 * Counts the number of blocks which have been run. The counter is
	 * incremented only after the block has been finished.
	 */
	public static ExPar BlockCounter = new ExPar(INTEGER, new ExParValue(0),
			"Counts the number of blocks which have been run");
	/**
	 * Counts the number of sessions which have been run. The counter is
	 * incremented only after the session has been finished.
	 */
	public static ExPar SessionCounter = new ExPar(INTEGER, new ExParValue(0),
			"Counts the number of session which have been run");
	/*
	 * Limits the number of trials in a block. If set and the number of trials
	 * in a block is larger then a BlockEnd and a BlockStart display list are
	 * inserted after the given number of trials. The inserted display lists are
	 * copies of the original BlockStart and BlockEnd display lists of the
	 * block.
	 */
	// public static ExPar BlockSizeLimit = new ExPar(SMALL_INT, new
	// ExParValue(0), "Limits the number of trials in a block");
	/**
	 * Trial processing state. Display objects may set this parameter to enforce
	 * special break behavior or to enforce trial duplication.
	 * 
	 * @see StateCodes
	 */
	public static ExPar TrialState = new ExPar(GEOMETRY_EDITOR,
			StateCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.StateCodes.EXECUTE"), "Trial state");
	/**
	 * Block processing state. Display objects may set this parameter to enforce
	 * special break behavior.
	 * 
	 * @see StateCodes
	 */
	public static ExPar BlockState = new ExPar(GEOMETRY_EDITOR,
			StateCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.StateCodes.EXECUTE"), "Block state");
	/**
	 * Session processing state.
	 * 
	 * @see StateCodes
	 * @see de.pxlab.pxl.display.SessionStateControl
	 */
	public static ExPar SessionState = new ExPar(GEOMETRY_EDITOR,
			StateCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.StateCodes.EXECUTE"), "Session state");
	/**
	 * Procedure processing state.
	 * 
	 * @see StateCodes
	 * @see de.pxlab.pxl.display.SessionStateControl
	 */
	public static ExPar ProcedureState = new ExPar(GEOMETRY_EDITOR,
			StateCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.StateCodes.EXECUTE"), "Procedure state");
	/**
	 * Flag to indicate that trials with error states should be repeated. If
	 * such a trial happens then a copy of it is inserted into the remaining
	 * list of trials of the current block. Insertion is randomized if block
	 * randomization is on.
	 */
	public static ExPar RepeatErrorTrials = new ExPar(FLAG, new ExParValue(0),
			"Flag for repeating error trials");
	/**
	 * Controls whether the first block's start and the last block's end display
	 * lists are shown. If on, then the first block's start and the last block's
	 * end display lists are not shown. This is the default.
	 */
	public static ExPar SkipBoundingBlockDisplays = new ExPar(FLAG,
			new ExParValue(1), "Suppress first/last block's displays");
	/** Device name of the external control box. */
	// public static ExPar ExternalControlBox = new ExPar(STRING, new
	// ExParValue("COM1"), "External Button/Signal Control Box Name");
	/** Device name of the serial communication device. */
	// public static ExPar SerialCommunicationDevice = new ExPar(STRING, new
	// ExParValue("COM1"), "Serial Communication Device Name");
	/**
	 * Package name of user supplied display extensions.
	 * 
	 * @see DisplaySupport
	 */
	public static ExPar DisplayExtensionPackage = new ExPar(STRING,
			new ExParValue(""), "Display extensions package name");
	/** Name of the experiment. */
	public static ExPar ExperimentName = new ExPar(STRING, new ExParValue(
			"PXLab Experiment"), "Name of the experiment");
	/** Title of the experiment. */
	public static ExPar ExperimentTitle = new ExPar(STRING, new ExParValue(
			"%ExperimentName%"), "Title of the experiment");
	/** Java class name for Java source code export. */
	public static ExPar JavaClassName = new ExPar(STRING, new ExParValue(
			"PXLabExperiment"), "Java class name for this experimental design");
	/**
	 * PXLab version string.
	 * 
	 * @see Version
	 * @see BuildVersion
	 */
	public static ExPar PXLabVersion = new ExPar(STRING, new ExParValue(
			Base.getVersion()), "PXLab version number");
	/** Design file version string. */
	public static ExPar DesignVersion = new ExPar(STRING, new ExParValue(1),
			"Design file version number");
	/** Design file date string. */
	public static ExPar DesignDate = new ExPar(STRING, new ExParValue(""),
			"Design file date");
	/** Design file author name. */
	public static ExPar DesignAuthor = new ExPar(STRING, new ExParValue(""),
			"Design file author");
	/**
	 * Duration of a single video frame. This parameter is valid only after
	 * vertical retrace synchronization has been used for the first time. If
	 * video synchronization is emulated then it reflects the simulated video
	 * frame duration.
	 */
	public static ExPar VideoFrameDuration = new ExPar(RTDATA,
			new ExParValue(0), "Video frame duration");
	/**
	 * The normalizing reference color for CIELab and CIELuv related
	 * transformations.
	 */
	public static ExPar CIEWhitePoint = new ExPar(COLOR, new ExParValue(0),
			"CIE white reference");
	/**
	 * Contains the white point Yxy-coordinates of all available display
	 * screens. If there is only a single display screen then this is a 3-dim
	 * array of Yxy-values. If there is more than a single screen then
	 * successive entries contain the respective Yxy-coordinates of the
	 * respective white points. The device white point is that color which
	 * results from the maximum intensity in all three color channels. This
	 * parameter can't be set in a parameter file. Its value is computed from
	 * the respective primary coordinates.
	 */
	public static ExPar DeviceWhitePoint = new ExPar(DEPCOLOR,
			new ExParValue(0), "Device white point");
	/** This is the current device's white point. */
	public static ExPar White = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.WHITE)), "White color");
	public static ExPar LightGray = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Light gray color");
	public static ExPar Gray = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GRAY)), "Gray color");
	public static ExPar DarkGray = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.DARK_GRAY)), "Dark gray color");
	public static ExPar Black = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)), "Black color");
	public static ExPar Yellow = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.YELLOW)), "Yellow color");
	public static ExPar Cyan = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.CYAN)), "Cyan color");
	public static ExPar Magenta = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.MAGENTA)), "Magenta color");
	public static ExPar Green = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GREEN)), "Green color");
	public static ExPar Red = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.RED)), "Red color");
	public static ExPar Blue = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLUE)), "Blue color");

	/** Reset all experimental parameters to their default values. */
	public static void resetValues() {
		// System.out.println("ExPar.resetValues()");
		String cn = "de.pxlab.pxl.ExPar";
		try {
			Class cls = Class.forName(cn);
			Field[] fld = cls.getFields();
			int n2 = fld.length;
			for (int j = 0; j < n2; j++) {
				try {
					Object p = fld[j].get(null);
					if (p instanceof de.pxlab.pxl.ExPar) {
						// System.out.println("ExPar.resetValue(): " +
						// fld[j].getName() + " = " +
						// ((ExPar)p).defaultValue.toString());
						((ExPar) p)
								.setValue((ExParValue) (((ExPar) p).defaultValue
										.clone()));
					}
				} catch (NullPointerException npx) {
				} catch (IllegalAccessException iax) {
				}
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("ExPar.resetValues(): Oh! Class " + cn
					+ " not found!");
		}
		setDate();
		if (Base.hasDisplayDeviceFrameDuration()) {
			VideoFrameDuration.set(Base.getDisplayDeviceFrameDuration());
		}
	}
}
