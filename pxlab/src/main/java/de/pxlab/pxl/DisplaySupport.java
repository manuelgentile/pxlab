package de.pxlab.pxl;

import java.lang.reflect.Field;
import java.awt.Rectangle;
import java.util.*;

/**
 * This class contains static support methods for experimental displays. Its
 * main use is to keep these static methods out of the definition of the class
 * Display.
 * 
 * @version 0.6.0
 */
/*
 * 06/27/01 use long for timing group patterns.
 * 
 * 09/11/02 removed code to handle modifiers like 'adj' when loading a ne
 * instance
 * 
 * 2005/12/08 moved code to inspect experimental parameters from class Display
 * into class DisplaySupport in order to use it for DataAnalysis objects
 */
abstract public class DisplaySupport {
	/** The title of this display. */
	private String title = null;
	/** The topic of this display. */
	private int topic = 0;

	/**
	 * Set this display's title and topic. Setting the display's title and topic
	 * is done by the constructor of a subclass.
	 */
	protected void setTitleAndTopic(String t, int g) {
		title = t;
		topic = g;
	}

	/**
	 * Get this display's title. Title and topic may be used for creating
	 * display menus.
	 */
	public String getTitle() {
		return ((title == null) ? getClass().getName() : title);
	}

	/** Get this display's topic id. */
	public int getTopic() {
		return (topic);
	}

	/** Set this display instance's name. */
	public void setInstanceName(String t) {
		instanceName = t;
	}

	/** Get this display instance's name. */
	public String getInstanceName() {
		return (instanceName);
	}
	/**
	 * The name of this instance. This name is used as a prefix for dynamically
	 * created ExPar objects.
	 */
	protected String instanceName = null;
	/** The ExDesignNode which created this Display object. */
	protected ExDesignNode node;

	public void setExDesignNode(ExDesignNode n) {
		node = n;
	}

	public ExDesignNode getExDesignNode() {
		return node;
	}
	/**
	 * A static array of timing group mask patterns. A display element is shown
	 * during step i if and only if its group mask pattern has bit number i set.
	 * Using this group mask pattern array the group mask pattern of a display
	 * element may simply be set by adding all group masks of theses steps which
	 * should contain the respective timing element.
	 */
	protected static long[] group;
	/** The limit for the number of timing groups in a display. */
	protected static final int timingGroupLimit = 64;
	static {
		group = new long[timingGroupLimit];
		long m = 1L;
		for (int i = 0; i < timingGroupLimit; i++) {
			group[i] = m;
			m = m << 1;
		}
	}

	protected static long getGroup(int i) {
		if (i < timingGroupLimit) {
			return group[i];
		} else {
			new InternalError("Maximum number of timing groups (63) exceeded: "
					+ i);
		}
		return 0L;
	}

	// -----------------------------------------------------------------
	// Creating a display object from its class name
	// -----------------------------------------------------------------
	/**
	 * Load and instantiate a Display object of the class whose name is given as
	 * an argument. We first try to find the Display object in PXLab's default
	 * display package de.pxlab.pxl.display and if the class is not found there
	 * then we check the package whose name is given by the parameter
	 * DisplayExtensionPackage.
	 * 
	 * @param className
	 *            the name of the class to be instantiated.
	 * @param instanceName
	 *            the name of this instance. The class and instance names are
	 *            used to address this display object's experimental parameters.
	 * @return the object which has been instantiated or null if none could be
	 *         instantiated because the respective class could not be found.
	 */
	public static Object load(String className, String instanceName, int type) {
		// System.out.println("Trying to load class " + className + " (" +
		// instanceName + ")");
		Class p = null;
		Object d = null;
		String pack = (type == ExDesignNode.DisplayNode) ? "de.pxlab.pxl.display."
				: "de.pxlab.pxl.data.";
		try {
			p = Class.forName(pack + className);
			// System.out.println("Class " + className + " found.");
		} catch (ClassNotFoundException cnf) {
			String dexp = ExPar.DisplayExtensionPackage.getString();
			if (dexp.length() > 0) {
				try {
					p = Class.forName(dexp + "." + className);
				} catch (ClassNotFoundException cnfx) {
					new DisplayError("Class " + dexp + "." + className
							+ " not found.");
				}
			} else {
				new DisplayError("Class " + pack + className + " not found.");
			}
		} catch (NoClassDefFoundError ndf) {
			String s = ndf.getMessage();
			String m = null;
			if (s.startsWith("javax/media")) {
				m = "The Java Media Framework package is missing. \nYou may get it from http://java.sun.com/products/java-media/jmf";
			} else if (s.startsWith("javax/comm")) {
				m = "The Java Communications package is missing. \nYou may get it from http://java.sun.com/products/javacomm";
			} else if (s.startsWith("org/apache/batik")) {
				m = "The Apache Batik package is missing. \nYou may get it from http://xml.apache.org/batik/";
			} else {
				m = "Failed to load class " + className;
			}
			new DisplayError(m);
		}
		if (p != null) {
			try {
				d = p.newInstance();
			} catch (InstantiationException ie) {
				new DisplayError("Class " + p.getName()
						+ " could not be instantiated.");
				d = null;
			} catch (IllegalAccessException ia) {
				new DisplayError("Illegal access to class " + p.getName() + ".");
				d = null;
			}
			((DisplaySupport) d).setInstanceName(instanceName);
		}
		return d;
	}

	abstract public void createInstance();

	abstract public void destroyInstance();

	// -----------------------------------------------------------------
	// Run Time Support for Finding the Class and Experimental Parameter Fields
	// -----------------------------------------------------------------
	/**
	 * Return the class name of this display without the package prefix.
	 * 
	 * @return the class name of this display.
	 */
	public String getClassName() {
		String n = getClass().getName();
		int p = n.lastIndexOf('.');
		return (n.substring(p + 1));
	}
	/**
	 * This array holds the experimental parameter descriptors of this Display
	 * object after it has been fully created.
	 */
	protected ExParDescriptor[] exParFields = null;

	/**
	 * Return an array which describes all public ExPar fields of this Display
	 * object.
	 * 
	 * @return an array of ExParDescriptor objects which describe public ExPar
	 *         fields of this Display object. Returns null if this object's
	 *         class has no ExPar fields.
	 */
	public ExParDescriptor[] getExParFields() {
		return (exParFields);
	}

	/**
	 * Create an array of all public ExPar fields of this Display object. This
	 * method is called by createInstance() whenever a new instance of this
	 * Display object is created.
	 */
	protected void createExParFields() {
		Field[] flds = getClass().getFields();
		int nflds = flds.length;
		if (nflds > 0) {
			ArrayList fldsa = new ArrayList();
			for (int i = 0; i < nflds; i++) {
				// System.out.println("Field " + flds[i].getName());
				try {
					Object a = flds[i].get(this);
					// System.out.println(" is class " +
					// a.getClass().getName());
					if (a instanceof ExPar) {
						int type = ((ExPar) a).getType();
						String n = flds[i].getName();
						// System.out.println("Display.createExParFields(): Check Field "
						// + n);
						// fix up some parameter types according to their names
						/*
						 * if (type == ExParType.UNKNOWN) { if
						 * (n.endsWith("Color")) ((ExPar)a).setType(type =
						 * ExPar.COLOR); else if (n.endsWith("Timer"))
						 * ((ExPar)a).setType(type = ExParType.TIMER); else if
						 * (n.endsWith("Duration")) ((ExPar)a).setType(type =
						 * ExParType.DURATION); }
						 */
						String fn = null;
						if (instanceName == null)
							fn = n;
						else
							fn = instanceName + "." + n;
						fldsa.add(new ExParDescriptor(fn, (ExPar) a,
								ExParTypeCodes.editor[type]));
						// System.out.println("Display.createExParFields(): ExPar Field "
						// + fn);
					}
				} catch (IllegalAccessException iae) {
					// System.out.println(iae.getMessage());
				}
			}
			int n = fldsa.size();
			if (n > 0) {
				exParFields = new ExParDescriptor[n];
				for (int i = 0; i < n; i++) {
					ExParDescriptor xpd = (ExParDescriptor) fldsa.get(i);
					/*
					 * ExPar p = xpd.getValue(); // Fix duration parameters
					 * which do not have a proper range if (p.getType() ==
					 * ExParType.DURATION) { if (p.getValue().length < 3) {
					 * p.set(p.getInt(), 0, 5000); } }
					 */
					exParFields[i] = xpd;
					// System.out.println(xpd);
				}
				// reportPars();
			}
		}
	}

	/** Check whether the given ExPar seems to be a geometry parameter. */
	/*
	 * private boolean isGeometryPar(ExPar x) { ExParValue v = x.getValue();
	 * boolean retval = false; // System.out.println("Checking " + x.getHint());
	 * if (v.length == 3) { double dv[] = v.getDoubleArray(); int iv[] =
	 * v.getIntArray(); retval = (((dv[1] < dv[2]) && (dv[0] >= dv[1]) && (dv[0]
	 * <= dv[2])) || ((iv[1] > MagicNumber.lowerLimit) && (iv[2] >
	 * MagicNumber.lowerLimit))); } // System.out.println(retval?
	 * "   is Geometry": "is not Geometry");
	 * 
	 * return(retval); }
	 */
	protected void reportPars() {
		int n = exParFields.length;
		int noEditPars = 0;
		int colorPars = 0;
		int geometryPars = 0;
		int timingPars = 0;
		int systemPars = 0;
		for (int i = 0; i < n; i++) {
			switch (exParFields[i].getEditorType()) {
			case ExParDescriptor.NO_EDIT:
				noEditPars++;
				break;
			case ExParDescriptor.COLOR_EDIT:
				colorPars++;
				break;
			case ExParDescriptor.GEOMETRY_EDIT:
				geometryPars++;
				break;
			case ExParDescriptor.TIMING_EDIT:
				timingPars++;
				break;
			case ExParDescriptor.SYSTEM_EDIT:
				systemPars++;
				break;
			}
		}
		System.out.println("The Display " + this.getClass().getName() + " has "
				+ n + " ExPar Fields.");
		System.out.println("   Non-Edit: " + noEditPars);
		System.out.println("   Color:    " + colorPars);
		System.out.println("   Geometry: " + geometryPars);
		System.out.println("   Timing:   " + timingPars);
		System.out.println("   System:   " + systemPars);
	}

	/**
	 * Find the name of the Display parameter which contains the given ExPar
	 * value.
	 * 
	 * @param x
	 *            the ExPar object whose name ist to be found.
	 * @return a String containing the given ExPar object's name.
	 */
	public String getExParName(ExPar x) {
		if (exParFields != null) {
			for (int i = 0; i < exParFields.length; i++) {
				if (exParFields[i].getValue().equals(x)) {
					return (exParFields[i].getName());
				}
			}
		}
		return (null);
	}

	/**
	 * Return the color parameters of this display, including the depenendent
	 * parameters which have been created during instance creation.
	 */
	public ExParDescriptor[] getColorPars() {
		ArrayList cp = new ArrayList();
		for (int i = 0; i < exParFields.length; i++) {
			if (exParFields[i].getEditorType() == ExParDescriptor.COLOR_EDIT)
				cp.add(exParFields[i]);
		}
		int n1 = cp.size();
		ExParDescriptor[] cpf = new ExParDescriptor[n1];
		int i = 0;
		for (int j = 0; j < n1; j++)
			cpf[i++] = (ExParDescriptor) cp.get(j);
		return (cpf);
	}

	public ExParDescriptor[] getTimingPars() {
		return (getXPars(ExParDescriptor.TIMING_EDIT));
	}

	public ExParDescriptor[] getGeometryPars() {
		return (getXPars(ExParDescriptor.GEOMETRY_EDIT));
	}

	/*
	 * public ExParDescriptor[] getStringPars() {
	 * return(getXPars(ExParDescriptor.STRING)); }
	 */
	protected ExParDescriptor[] getXPars(int type) {
		ArrayList cp = new ArrayList();
		for (int i = 0; i < exParFields.length; i++) {
			if (exParFields[i].getEditorType() == type)
				cp.add(exParFields[i]);
		}
		ExParDescriptor[] cpf = new ExParDescriptor[cp.size()];
		for (int i = 0; i < cpf.length; i++)
			cpf[i] = (ExParDescriptor) cp.get(i);
		return (cpf);
	}

	public void setExParFields(ExParDescriptor[] expd) {
		// Set this ExParDescriptors to the given ExParDescriptors
		if (expd == null)
			return;
		for (int i = 0; i < expd.length; i++) {
			if (exParFields != null) {
				for (int j = 0; j < exParFields.length; j++) {
					if (expd[i].getFieldName().equals(
							exParFields[j].getFieldName())) {
						int t = expd[i].getValue().getType();
						ExParValue xpv = (ExParValue) expd[i].getValue()
								.getValue().clone();
						String h = expd[i].getValue().getHint();
						exParFields[j].setValue(new ExPar(t, xpv, h));
						break;
					}
				}
			}
		}
	}

	// This is necessary if you want to copy a node in ExDesignEditorPanel
	public void setExParFields(String[] n, ExParValue[] xpv) {
		// Set this ExParDescriptors to the given values
		// System.out.println("Display.setExParFields() " + n.length + " ? " +
		// xpv.length + "  " + exParFields.length );
		if (n.length != xpv.length)
			return;
		for (int i = 0; i < n.length; i++) {
			if (exParFields != null) {
				for (int j = 0; j < exParFields.length; j++) {
					// System.out.println(" checking " +
					// exParFields[j].getFieldName());
					if (n[i].equals(exParFields[j].getFieldName())) {
						exParFields[j].getValue().getValue()
								.set((ExParValue) xpv[i].clone());
						// System.out.println(exParFields[j]);
						break;
					}
				}
			} else {
				// System.out.println("exParFields still undefined.");
			}
		}
	}

	// -----------------------------------------------------------------
	// Some static methods to compute size values
	// -----------------------------------------------------------------
	/**
	 * Compute the size of a square which fits into the given width and height
	 * and occupies the given proportion.
	 */
	public static int relSquareSize(int w, int h, double p) {
		if (p <= 0.0)
			return (0);
		if (p >= 1.0)
			return ((w > h) ? h : w);
		return ((w > h) ? (int) (p * h) : (int) (p * w));
	}

	/**
	 * Compute the size of a tiny square within a panel of the given width and
	 * height.
	 */
	public static int tinySquareSize(int w, int h) {
		return (6 * ((w < h) ? w : h) / 50);
	}

	/**
	 * Compute the size of a small square within a panel of the given width and
	 * height.
	 */
	public static int smallSquareSize(int w, int h) {
		return (12 * ((w < h) ? w : h) / 50);
	}

	/**
	 * Compute the size of a medium sized square within a panel of the given
	 * width and height.
	 */
	public static int mediumSquareSize(int w, int h) {
		return (24 * ((w < h) ? w : h) / 50);
	}

	/**
	 * Compute the size of a large square which does not touch the border of a
	 * panel of the given width and height.
	 */
	public static int largeSquareSize(int w, int h) {
		return (36 * ((w < h) ? w : h) / 50);
	}

	/**
	 * Compute the size of a huge square which does not touch the border of a
	 * panel of the given width and height.
	 */
	public static int hugeSquareSize(int w, int h) {
		return (48 * ((w < h) ? w : h) / 50);
	}

	/**
	 * Compute the size of a single square such that two of them fit within a
	 * panel of the given width and height either one on top of the other or one
	 * next to the other side by side. There also is left room for a gap between
	 * the squares.
	 */
	public static int squareSizeOfTwo(int w, int h) {
		int size;
		if (w > h) {
			// horizontal series
			size = 36 * (w / 84);
			if (size > h)
				size = 48 * (h / 50);
		} else {
			// vertical series
			size = 36 * (h / 84);
			if (size > w)
				size = 48 * (w / 50);
		}
		return (size);
	}

	/**
	 * Create a centered square of the requested size in a panel of the given
	 * width and height. The size will not be larger than the maximum of the
	 * container's width or height.
	 */
	public static Rectangle centeredSquare(int w, int h, int size) {
		if (size > w)
			size = w;
		if (size > h)
			size = h;
		return (new Rectangle(-size / 2, -size / 2, size, size));
	}

	/**
	 * Create a centered rectangle of the requested width and height in a panel
	 * of the given width and height. The size will not be larger than the
	 * maximum of the container's width or height.
	 */
	public static Rectangle centeredRect(int w, int h, int pw, int ph) {
		if (pw > w)
			pw = w;
		if (ph > h)
			ph = h;
		return (new Rectangle(-pw / 2, -ph / 2, pw, ph));
	}

	/**
	 * Create a small centered square in a panel of the given width and height.
	 */
	public static Rectangle tinySquare(int w, int h) {
		return (centeredSquare(w, h, tinySquareSize(w, h)));
	}

	/**
	 * Create a small centered square in a panel of the given width and height.
	 */
	public static Rectangle smallSquare(int w, int h) {
		return (centeredSquare(w, h, smallSquareSize(w, h)));
	}

	/**
	 * Create a medium sized centered square in a panel of the given width and
	 * height.
	 */
	public static Rectangle mediumSquare(int w, int h) {
		return (centeredSquare(w, h, mediumSquareSize(w, h)));
	}

	/**
	 * Create a medium sized centered square in a panel of the given width and
	 * height.
	 */
	public static Rectangle largeSquare(int w, int h) {
		return (centeredSquare(w, h, largeSquareSize(w, h)));
	}

	/**
	 * Create a large centered square in a panel of the given width and height
	 * with a small gap between the border and the square.
	 */
	public static Rectangle hugeSquare(int w, int h) {
		return (centeredSquare(w, h, hugeSquareSize(w, h)));
	}

	/**
	 * Create the first (top, left) of a pair of squares which fit into a panel
	 * of the given width and height either on top of each other or side by
	 * side, depending whether the panel is oriented vertical or horizontal.
	 */
	public static Rectangle firstSquareOfTwo(int w, int h, boolean gap) {
		int size = squareSizeOfTwo(w, h);
		int gap2 = gap ? size / 12 : 0;
		if (w > h) {
			// System.out.println("First Square: We are horizontal -------");
			return (new Rectangle(-gap2 - size, -size / 2, size, size));
		} else {
			// System.out.println("First Square: We are vertical |||||||");
			return (new Rectangle(-size / 2, -gap2 - size, size, size));
		}
	}

	/**
	 * Create the second (bottom, right) of a pair of squares which fit into a
	 * panel of the given width and height either on top of each other or side
	 * by side, depending whether the panel is oriented vertical or horizontal.
	 */
	public static Rectangle secondSquareOfTwo(int w, int h, boolean gap) {
		int size = squareSizeOfTwo(w, h);
		int gap2 = gap ? size / 12 : 0;
		if (w > h) {
			// System.out.println("Second Square: We are horizontal -------");
			return (new Rectangle(gap2, -size / 2, size, size));
		} else {
			// System.out.println("Second Square: We are vertical |||||||");
			return (new Rectangle(-size / 2, gap2, size, size));
		}
	}

	/**
	 * Create a square which is centered within the given square and whose side
	 * length is the given part of the original rectangle.
	 */
	public static Rectangle inner3rdRect(Rectangle r) {
		return (new Rectangle(r.x + r.width / 3, r.y + r.height / 3,
				r.width / 3, r.height / 3));
	}

	/**
	 * Create a square which is centered within the given square and whose side
	 * length is the given part of the original rectangle.
	 */
	public static Rectangle innerRect(Rectangle r, double p) {
		int w = (int) Math.round(r.width * p);
		int h = (int) Math.round(r.height * p);
		return (new Rectangle(r.x + (r.width - w) / 2,
				r.y + (r.height - h) / 2, w, h));
	}

	/**
	 * Create an array of Rectangles which make up a pattern of rectangles with
	 * the given borders and gaps.
	 */
	public static Rectangle[] rectPattern(Rectangle a, int rows, int columns,
			int vgap, int hgap, int vborder, int hborder) {
		return (rectPattern(a, rows, columns, vgap, hgap, vborder, hborder,
				-1.0));
	}

	/**
	 * Create an array of Rectangles which make up a pattern of rectangles with
	 * the given borders, gaps, and a given ratio of vertical to horizontal
	 * patch size.
	 */
	public static Rectangle[] rectPattern(Rectangle a, int rows, int columns,
			int vgap, int hgap, int vborder, int hborder, double y2xratio) {
		// Compute the width/height of a single rectangular element
		int ws = (a.width - 2 * hborder - (columns - 1) * hgap) / columns;
		int hs = (a.height - 2 * vborder - (rows - 1) * vgap) / rows;
		if (y2xratio > 0.0) {
			if ((double) hs / (double) ws > y2xratio) {
				// patches are too high
				hs = (int) ((double) ws * y2xratio);
			} else {
				// too wide
				ws = (int) ((double) hs / y2xratio);
			}
		}
		// If it does not fit then put excess pixels into the border
		hborder = (a.width - (columns - 1) * hgap - columns * ws) / 2;
		vborder = (a.height - (rows - 1) * vgap - rows * hs) / 2;
		Rectangle[] r = new Rectangle[rows * columns];
		int i = 0;
		int x, y;
		y = vborder;
		for (int row = 0; row < rows; row++) {
			x = hborder;
			for (int column = 0; column < columns; column++) {
				r[i++] = new Rectangle(x + a.x, y + a.y, ws, hs);
				x += (ws + hgap);
			}
			y += (hs + vgap);
		}
		return (r);
	}

	/**
	 * Create an array of Rectangles which make up a pattern of rectangles. Each
	 * rectangle has the specified horizontal-to-vertical ratio and there is a
	 * gap between the rectangles the size of which is the given part of the
	 * smaller rectangle sides. There also is a border equal to the gap.
	 */
	public static Rectangle[] rectPattern(Rectangle a, int rows, int columns,
			double hgapd, double vgapd, double y2xratio) {
		// Compute the width/height of a single rectangular element
		int ws = (int) ((double) a.width / ((double) columns + (double) (columns + 1)
				* hgapd));
		int hs = (int) ((double) a.height / ((double) rows + (double) (rows + 1)
				* vgapd));
		if (y2xratio > 0.0) {
			if ((double) hs / (double) ws > y2xratio) {
				// patches are too high
				hs = (int) ((double) ws * y2xratio);
			} else {
				// too wide
				ws = (int) ((double) hs / y2xratio);
			}
		}
		int hgap = (int) ((double) ws * hgapd);
		int vgap = (int) ((double) hs * vgapd);
		// If it does not fit then put excess pixels into the border
		int hborder = (a.width - (columns - 1) * hgap - columns * ws) / 2;
		int vborder = (a.height - (rows - 1) * vgap - rows * hs) / 2;
		Rectangle[] r = new Rectangle[rows * columns];
		int i = 0;
		int x, y;
		y = vborder;
		for (int row = 0; row < rows; row++) {
			x = hborder;
			for (int column = 0; column < columns; column++) {
				r[i++] = new Rectangle(x + a.x, y + a.y, ws, hs);
				x += (ws + hgap);
			}
			y += (hs + vgap);
		}
		return (r);
	}
}
