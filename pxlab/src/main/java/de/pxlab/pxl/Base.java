package de.pxlab.pxl;

import java.applet.Applet;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;

// import netscape.javascript.*;
/**
 * Static library and runtime information.
 * 
 * @version 0.3.5
 */
/*
 * 
 * 12/01/01 added getCodeBase() method.
 * 
 * 07/29/03 added methods to access the 'pxlab.properties' file.
 * 
 * 08/25/03 use screenMode and fullScreenDevice
 * 
 * 11/17/03 added singleRun
 * 
 * 2004/09/083 added exportJavaClass
 * 
 * 2005/06/20 added displayDeviceFrameDuration
 * 
 * 2005/07/14 added language option
 * 
 * 2005/09/01 designBase is added
 * 
 * 2005/09/07 did some cleaning up
 * 
 * 2005/09/28 export HTML option
 * 
 * 2005/12/09 process data file option
 * 
 * 2005/12/16 statFileName
 * 
 * 2006/09/05 Encoding option
 * 
 * 2007/03/16 debugging option for static parameters
 */
public class Base {
	/**
	 * A value for the global experimental parameter SubjectCode which should be
	 * used for demos. If the parameter SubjectCode has this value then data
	 * files derived from it will be overwritten on successive runs. Note that
	 * the default value of SubjectCode is empty.
	 */
	public static final String demoSubjectCode = "pxlab";
	/** Stores the applet we are currently running, if any. */
	private static Applet applet = null;
	/**
	 * Stores the application Frame object which currently runs the experiment,
	 * if any.
	 */
	private static Frame frame = null;
	/**
	 * True if we are running as an application and false otherwise.
	 */
	private static boolean application = true;
	/**
	 * Path name of the directory where the design file has been read from. Only
	 * used for applications. For applets the design base is identical with the
	 * document base.
	 */
	private static String designBase = null;
	private static URL designBaseContext = null;
	/** Basic PXLab properties. */
	private static Properties pxlabProperties = null;
	/** Debugging flag for properties access. */
	private static boolean debugProps = false;

	/**
	 * Get a PXLab system property. Most of the properties wil be generated
	 * automatically. Installation dependent properties may be set in the PXLab
	 * system properties file 'pxlab.properties'.
	 * 
	 * <p>
	 * PXlab system property keys and their default values.
	 * <p>
	 * <table>
	 * <tr>
	 * <td>PXLab Property Key
	 * <td>Meaning
	 * <td>Default Value
	 * </tr>
	 * <tr>
	 * <td>pxlab.version
	 * <td>PXLab Version String
	 * </tr>
	 * <tr>
	 * <td>pxlab.version.major
	 * <td>PXLab Major Version Number
	 * </tr>
	 * <tr>
	 * <td>pxlab.version.minor
	 * <td>PXLab Minor Version Number
	 * </tr>
	 * <tr>
	 * <td>pxlab.version.build
	 * <td>PXLab Build Number
	 * </tr>
	 * <tr>
	 * <td>pxlab.home
	 * <td>PXLab Installation Directory
	 * <td>Value of 'user.home'
	 * </tr>
	 * <tr>
	 * <td>pxlab.local
	 * <td>Local PXLab directory
	 * <td>Value of 'user.home'
	 * </tr>
	 * <tr>
	 * <td>starter.dir
	 * <td>PXLab Starter Application Files Directory
	 * <td>'user.home'/pxd
	 * </tr>
	 * <tr>
	 * <td>user.home
	 * <td>User Home Directory
	 * </tr>
	 * <tr>
	 * <td>user.dir
	 * <td>Current Active Directory
	 * </tr>
	 * </table>
	 * 
	 * <p>
	 * The property 'pxlab.home' gives the system wide PXLab installation
	 * directory root. This root may contain a system wide initialization file
	 * 'system.pxd'. In network and multiuser installations the property
	 * 'pxlab.local' gives the local application home directory which also may
	 * contain an initialization file named 'system.pxd' which is executed after
	 * the global system initialization file. The local initialization file may
	 * contain information about the local hardware which actually runs PXLab
	 * applications.
	 * 
	 * <p>
	 * The property 'starter.dir' contains a directory name which is used by the
	 * PXLab application de.pxlab.pxl.run.Starter. This application lists all
	 * experimental design files in the starter directory.
	 * 
	 * <p>
	 * The properties 'user.home' and 'user.dir' describe the current user's
	 * home and active directory respectively. Their values are derived from the
	 * respective Java system properties.
	 * 
	 * <p>
	 * The properties 'pxlab.version' and its sub-properties
	 * 'pxlab.version.major', 'pxlab.version.minor', and 'pxlab.version.build'
	 * describe the respective parts of the PXLab version number.
	 * 
	 * @return a string which contains the PXLab property or null if it could
	 *         not be identified.
	 */
	public static String getProperty(String key) {
		if (debugProps)
			System.out.println("Base.getProperty(): key=" + key);
		String p = getPXLabProperties().getProperty(key);
		if (debugProps)
			System.out
					.println("Base.getProperty(): key=" + key + " value=" + p);
		return p;
	}

	/**
	 * Get the PXlab properties list. This file should be in one of following
	 * places: current user directory, user home directory, java class path,
	 * java library or library extension directory, java binaries directory.
	 * 
	 * @return the PXLab system properties.
	 */
	public static Properties getPXLabProperties() {
		if (debugProps)
			System.out.println("Base.getPXLabProperties()");
		if (pxlabProperties == null) {
			pxlabProperties = new Properties(Base.getPXLabPropertiesDefaults());
			String pfn = getPXLabPropertiesFile();
			if (pfn != null) {
				try {
					InputStream ps = new URL(pfn).openStream();
					if (debugProps)
						System.out.println("Read PXLab properties file " + pfn);
					pxlabProperties.load(ps);
					ps.close();
				} catch (MalformedURLException mex) {
				} catch (IOException iox) {
				}
			}
		}
		return pxlabProperties;
	}

	/**
	 * Try to find the PXLab system properties file named 'pxlab.properties'.
	 * Search order is: 'user.dir', 'user.home', 'java.class.path',
	 * 'java.library.path', and 'java.ext.dirs'. Finally the subdirectories
	 * 'lib' and 'bin' of 'java.home' are searched.
	 * 
	 * @return a valid file path for reading the file 'pxlab.properties' or null
	 *         if the file is not found.
	 */
	private static String getPXLabPropertiesFile() {
		if (debugProps)
			System.out.println("Base.getPXLabPropertiesFile() searching ...");
		String r = null;
		if (application) {
			String p = System.getProperty("path.separator");
			String pathList = System.getProperty("user.dir") + p
					+ System.getProperty("user.home") + p
					+ System.getProperty("java.class.path") + p
					+ System.getProperty("java.library.path") + p
					+ System.getProperty("java.ext.dirs") + p
					+ System.getProperty("java.home") + p + "lib" + p
					+ System.getProperty("java.home") + p + "bin";
			File f = de.pxlab.util.FileExt.findFile("pxlab.properties",
					pathList);
			if (debugProps)
				System.out.println("Base.getPXLabPropertiesFile() found " + f);
			if (f != null)
				r = "file:///" + f.getPath();
		} else {
			if (applet != null) {
				try {
					URL url = new URL(applet.getCodeBase(), "pxlab.properties");
					r = url.toString();
				} catch (MalformedURLException mex) {
				}
			}
		}
		return r;
	}

	/**
	 * Get the PXLab system properties defaults. This method is called only
	 * once.
	 */
	private static Properties getPXLabPropertiesDefaults() {
		if (debugProps)
			System.out.println("Base.getPXLabPropertiesDefaults()");
		Properties propertiesDefaults = new Properties();
		propertiesDefaults.put("pxlab.home", System.getProperty("user.home"));
		propertiesDefaults.put("pxlab.local", System.getProperty("user.home"));
		propertiesDefaults.put("pxlab.version", Version.instance());
		propertiesDefaults.put("pxlab.version.major",
				String.valueOf(Version.getMajor()));
		propertiesDefaults.put("pxlab.version.minor",
				String.valueOf(Version.getMinor()));
		propertiesDefaults.put("pxlab.version.bugfix",
				String.valueOf(Version.getBugfix()));
		propertiesDefaults.put("pxlab.version.build",
				String.valueOf(Version.getBuild()));
		propertiesDefaults.put("starter.dir", System.getProperty("user.home")
				+ System.getProperty("file.separator") + "pxd");
		propertiesDefaults.put("user.dir", System.getProperty("user.dir"));
		propertiesDefaults.put("user.home", System.getProperty("user.home"));
		return propertiesDefaults;
	}

	/**
	 * Get the PXLab version number which contains the major and the minor
	 * version and the build number which is counted across versions.
	 * 
	 * @return a string containing three numbers: major version, minor version,
	 *         and build number.
	 */
	public static String getVersion() {
		return (Version.instance());
	}

	/**
	 * Tell the PXLab system that we currently are running as an applet. This
	 * method MUST be called befor any other PXLab class is instantiated.
	 * 
	 * @param a
	 *            the applet which is running this code.
	 */
	public static void setApplet(Applet a) {
		// System.out.println("Base.setApplet()");
		applet = a;
		frame = null;
		application = (a == null);
		reset();
		Debug.show(Debug.BASE, "Base.setApplet(): " + (!application));
	}

	/**
	 * Reset display device options which should keep its default values across
	 * multiple runs of applets on a single page. This method is only called by
	 * applets.
	 */
	private static void reset() {
		displayDeviceType = 0;
		screenWidth = -1;
		screenHeight = -1;
	}
	private static String runtimeIDs = "";
	private static int runtimeIDn = 0;
	private static int nextNumber = 0;

	/**
	 * Initialize parameters which are specific for a single experimental run.
	 */
	public static void initRuntime() {
		Random r = new Random(System.nanoTime());
		runtimeIDn = (int) (Math.abs(r.nextLong()) % 100000000L);
		runtimeIDs = "pxlab" + String.valueOf(runtimeIDn);
		nextNumber = 0;
	}

	/**
	 * Get an ID string which is unique to the current experimental run.
	 * 
	 * @return a unique runtime identifier string.
	 */
	public static String getRuntimeID() {
		return runtimeIDs;
	}

	/**
	 * Get a numeric ID which is unique to the current experimental run.
	 * 
	 * @return a unique runtime identifier number.
	 */
	public static int getRuntimeIDNumber() {
		return runtimeIDn;
	}

	/**
	 * Get a numeric ID which is unique to the current experimental run.
	 * 
	 * @return a unique runtime identifier number.
	 */
	public static int getNextNumber() {
		return ++nextNumber;
	}

	/*
	 * private static JSObject win;
	 * 
	 * 
	 * public static void setJSWin(JSObject w) { win = w; }
	 * 
	 * 
	 * public static JSObject getJSWin() { return(win); }
	 */
	/**
	 * Tell the PXLab system that we currently are running as an application.
	 * This is the default.
	 */
	public static void setApplication() {
		application = true;
		Debug.show(Debug.BASE, "Base.setApplication(): " + application);
	}

	/**
	 * Set the reference for a top level frame which can be used as a parent for
	 * Dialog objects.
	 * 
	 * @param a
	 *            the top level frame of this application.
	 */
	public static void setFrame(Frame a) {
		frame = a;
		if (frame != null) {
			Debug.show(Debug.BASE, "Base.setFrame(): " + frame.getTitle());
		} else {
			Debug.show(Debug.BASE, "Base.setFrame(): Top level frame removed.");
		}
	}

	/**
	 * Return a reference to a frame which can be used as parent frame for
	 * Dialog objects.
	 * 
	 * @return a reference to the parent Frame object or null if we are running
	 *         as an applet.
	 */
	public static Frame getFrame() {
		return (frame != null) ? frame : new Frame();
	}

	/**
	 * Return a reference to the applet we currently are running.
	 * 
	 * @return a reference to the Applet object or null if we are running as an
	 *         application.
	 */
	public static Applet getApplet() {
		return applet;
	}

	/**
	 * Return the current program's code base URL. This is the applet's URL if
	 * we are running as an applet.
	 */
	/*
	 * public static URL getCodeBase() { return (isApplication()? null:
	 * applet.getCodeBase()); }
	 */
	/**
	 * Set the directory path for the current design file. Only used by
	 * applications.
	 */
	public static void setDesignBase(String db) {
		designBase = db;
		Debug.show(Debug.BASE, "Base.setDesignBase(String): " + designBase);
	}

	public static void setDesignBase(URL urlc) {
		designBaseContext = urlc;
		Debug.show(Debug.BASE, "Base.setDesignBase(URL): " + designBaseContext);
	}

	/**
	 * Return the current design file's base URL. This is the URL of the
	 * directory where the current design file is located.
	 */
	public static URL getDesignBase() {
		URL url = null;
		if (isApplication()) {
			if (designBase != null) {
				try {
					url = new File(designBase).toURI().toURL();
				} catch (MalformedURLException mfx) {
					System.out.println("Base.getDesignBase() Malformed URL: "
							+ designBase);
				}
			}
		} else {
			url = designBaseContext;
		}
		// System.out.println("Base.getDesignBase(): " + url);
		return url;
	}

	/**
	 * Check whether we currently are running as an application.
	 * 
	 * @return true if we are running as an application and false otherwise.
	 */
	public static boolean isApplication() {
		return (application);
	}
	// ----------------------------------------------------------------
	// Encoding option '-E'
	// ----------------------------------------------------------------
	// Note the windows default charset is 'windows-1252'
	// This is more general: 'ISO-8859-1'
	// Macintosh default charset seems to be 'MacRoman'
	private static String encoding = null;

	public static void setEncoding(String s) {
		encoding = s;
		Debug.show(Debug.BASE, "Base.setEncoding(): " + encoding);
	}

	public static String getEncoding() {
		return encoding;
	}
	// ----------------------------------------------------------------
	// Language option '-L'
	// ----------------------------------------------------------------
	/** Language code for the current message language. */
	private static String language = null;

	/**
	 * Set the language code for evaluation of the ExParValue function lang2()
	 * and lang3().
	 * 
	 * @param n
	 *            a string which indicates the language to use. 'en' is for
	 *            English and 'de' is for German.
	 */
	public static void setDefaultLanguage(String n) {
		String m = n.toLowerCase();
		if (m.startsWith("en")) {
			language = Locale.US.getLanguage();
		} else if (m.startsWith("de") || m.startsWith("ge")) {
			language = Locale.GERMANY.getLanguage();
		} else {
			language = m;
		}
		Debug.show(Debug.BASE, "Base.setDefaultLanguage(): " + language);
	}

	/**
	 * Get the language code which should be used for ExParValue evaluation.
	 * 
	 * @return an ISO Language Code as defined by ISO-639 in the table at <a
	 *         href
	 *         ="http://www.loc.gov/standards/iso639-2/englangn.html">http://
	 *         www.loc.gov/standards/iso639-2/englangn.html</a>.
	 */
	public static String getLanguage() {
		return (language != null) ? language : Locale.getDefault()
				.getLanguage();
	}
	/** True if single run command line behavior is requested. */
	/*
	 * private static boolean singleRun = false;
	 * 
	 * public static void setSingleRun(boolean s) { singleRun = s; }
	 * 
	 * public static boolean getSingleRun() { return singleRun; }
	 */
	// ----------------------------------------------------------------
	// Process data file option '-Z'
	// ----------------------------------------------------------------
	/** True if all that is to do is to process a data file. */
	private static boolean processDataFile = false;

	public static void setProcessDataFile(boolean s) {
		processDataFile = s;
		Debug.show(Debug.BASE, "Base.setProcessDataFile(): " + processDataFile);
	}

	public static boolean getProcessDataFile() {
		return processDataFile;
	}
	// ----------------------------------------------------------------
	// Processed data file name: Option -t
	// ----------------------------------------------------------------
	private static String processedDataFileName = null;

	public static void setProcessedDataFileName(String s) {
		processedDataFileName = s;
		Debug.show(Debug.BASE, "Base.setProcessedDataFileName(): "
				+ processedDataFileName);
	}

	public static String getProcessedDataFileName() {
		return processedDataFileName;
	}
	// ----------------------------------------------------------------
	// Export Text options '-K' and '-k'
	// ----------------------------------------------------------------
	/** True if all that is to do is to export a pxd-file into a text file. */
	private static boolean exportText = false;
	private static String exportTextFileName = "";

	public static void setExportText(boolean s) {
		exportText = s;
		Debug.show(Debug.BASE, "Base.setExportText(): " + exportText);
	}

	public static void setExportText2(String fn) {
		exportText = true;
		exportTextFileName = fn;
		Debug.show(Debug.BASE, "Base.setExportText2(): " + fn);
	}

	public static boolean getExportText() {
		return exportText;
	}

	public static String getExportTextFileName() {
		return exportTextFileName;
	}
	// ----------------------------------------------------------------
	// Export HTML option '-H'
	// ----------------------------------------------------------------
	/**
	 * True if all that is to do is to convert a pxd-file into a HTML text file.
	 */
	private static boolean exportHTML = false;

	public static void setExportHTML(boolean s) {
		exportHTML = s;
		Debug.show(Debug.BASE, "Base.setExportHTML(): " + exportHTML);
	}

	public static boolean getExportHTML() {
		return exportHTML;
	}
	// ----------------------------------------------------------------
	// Export Java class option '-j' or '-J'
	// ----------------------------------------------------------------
	/**
	 * True if all that is to do is to convert a pxd-file into a java code
	 * class.
	 */
	private static boolean exportJavaClass = false;

	public static void setExportJavaClass(boolean s) {
		exportJavaClass = s;
		Debug.show(Debug.BASE, "Base.setExportJavaClass(): " + exportJavaClass);
	}

	public static boolean getExportJavaClass() {
		return exportJavaClass;
	}
	// ----------------------------------------------------------------
	// Print design file option '-T'
	// ----------------------------------------------------------------
	/**
	 * If true then every design file which is loaded should be printed on the
	 * command line.
	 */
	private static boolean printDesignFile = false;

	/**
	 * Set a flag such that every design file which is loaded will be echoed on
	 * the command line.
	 */
	public static void setPrintDesignFile(boolean s) {
		printDesignFile = s;
		Debug.show(Debug.BASE, "Base.setPrintDesignFile(): " + printDesignFile);
	}

	/** Get the print design file flag. */
	public static boolean getPrintDesignFile() {
		return printDesignFile;
	}
	// ----------------------------------------------------------------
	// Screen width and screen height options '-w' and '-h'
	// ----------------------------------------------------------------
	/** Width of the stimulus display window. */
	private static int screenWidth = -1;
	/** Height of stimulus display window. */
	private static int screenHeight = -1;

	public static void setScreenWidth(String a) {
		try {
			screenWidth = Integer.parseInt(a);
		} catch (NumberFormatException nfx) {
		}
		Debug.show(Debug.BASE, "Base.setScreenWidth(): " + screenWidth);
	}

	public static void setScreenWidth(int a) {
		screenWidth = a;
		Debug.show(Debug.BASE, "Base.setScreenWidth(): " + screenWidth);
	}

	public static boolean hasScreenWidth() {
		return (screenWidth != -1);
	}

	public static int getScreenWidth() {
		return screenWidth;
	}

	public static void setScreenHeight(String a) {
		try {
			screenHeight = Integer.parseInt(a);
		} catch (NumberFormatException nfx) {
		}
		Debug.show(Debug.BASE, "Base.setScreenHeight(): " + screenHeight);
	}

	public static void setScreenHeight(int a) {
		screenHeight = a;
		Debug.show(Debug.BASE, "Base.setScreenHeight(): " + screenHeight);
	}

	public static boolean hasScreenHeight() {
		return (screenHeight != -1);
	}

	public static int getScreenHeight() {
		return screenHeight;
	}
	// ----------------------------------------------------------------
	// Display device refresh rate option '-R'
	// ----------------------------------------------------------------
	private static int displayDeviceRefreshRate = -1;

	public static void setDisplayDeviceRefreshRate(String a) {
		try {
			displayDeviceRefreshRate = Integer.parseInt(a);
		} catch (NumberFormatException nfx) {
		}
		Debug.show(Debug.BASE, "Base.setDisplayDeviceRefreshRate(): "
				+ displayDeviceRefreshRate);
	}

	public static boolean hasDisplayDeviceRefreshRate() {
		return (displayDeviceRefreshRate != -1);
	}

	public static int getDisplayDeviceRefreshRate() {
		return displayDeviceRefreshRate;
	}
	// ----------------------------------------------------------------
	// Display device frame duration measurement
	// ----------------------------------------------------------------
	private static double displayDeviceFrameDuration = -1.0;

	public static void setDisplayDeviceFrameDuration(double t) {
		// System.out.println("Base.setDisplayDeviceFrameDuration(): " + t);
		displayDeviceFrameDuration = t;
		Debug.show(Debug.BASE, "Base.setDisplayDeviceFrameDuration(): "
				+ displayDeviceFrameDuration);
	}

	public static boolean hasDisplayDeviceFrameDuration() {
		return (displayDeviceFrameDuration > 0.0);
	}

	public static double getDisplayDeviceFrameDuration() {
		return displayDeviceFrameDuration;
	}
	// ----------------------------------------------------------------
	// Display device type options '-r', '-x', '-z', '-S'
	// ----------------------------------------------------------------
	private static int displayDeviceType = 0;

	/**
	 * Set the display device type.
	 * 
	 * @param md
	 *            a string containing the mode number or a verbal description of
	 *            the required display device mode.
	 * @return true if the mode argument is valid and false otherwise.
	 */
	public static boolean setDisplayDeviceType(String md) {
		boolean r = false;
		DisplayDeviceOptionCodeMap ocm = new DisplayDeviceOptionCodeMap();
		if (ocm.hasCodeFor(md)) {
			setDisplayDeviceType((int) ocm.getCode());
			r = true;
		} else if (!md.equals("?")) {
			String[] a = md.split("x");
			if (a.length > 0) {
				if (a.length == 1) {
					setDisplayDeviceRefreshRate(a[0]);
				} else if (a.length == 2) {
					setScreenWidth(a[0]);
					setScreenHeight(a[1]);
				} else if (a.length == 3) {
					setScreenWidth(a[0]);
					setScreenHeight(a[1]);
					setDisplayDeviceRefreshRate(a[2]);
				}
			}
		}
		return r;
	}

	public static void setDisplayDeviceType(int m) {
		if (ExperimentalDisplayDevice.isValidType(m)) {
			displayDeviceType = m;
		}
		Debug.show(Debug.BASE, "Base.setDisplayDeviceType(): "
				+ displayDeviceType);
	}

	public static int getDisplayDeviceType() {
		return displayDeviceType;
	}

	public static String getCopyright() {
		return "(C) H. Irtel, 2008";
	}

	public static String getPXLab() {
		return "PXLab";
	}

	public static String getPXLabCopyright() {
		return getPXLab() + " " + getCopyright();
	}
	private static Font systemFont = null;

	public static Font getSystemFont() {
		if (systemFont == null) {
			Frame f = new Frame();
			f.addNotify();
			systemFont = f.getFont();
		}
		return systemFont;
	}
}
