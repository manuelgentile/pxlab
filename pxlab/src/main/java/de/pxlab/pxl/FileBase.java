package de.pxlab.pxl;

import java.awt.*;
import java.awt.image.*;
import java.net.*;
import java.io.*;
import java.util.*;

import de.pxlab.util.StringExt;

/**
 * Static methods for resource file handling, including an image cache.
 * 
 * @version 0.1.6
 */
/*
 * 
 * 2004/12/15
 * 
 * 2005/01/27 implement image caching
 * 
 * 2006/02/15 fixed bug in createResourcePath() when an application accessed the
 * local file system.
 * 
 * 2006/03/02 cache debugging options
 * 
 * 2006/06/19 removed URI creation in createResourcePath() for applications when
 * acessing the local file system. This did not work with the audio software.
 */
public class FileBase {
	private static HashMap imgBuf = null;
	private static long cacheMemory = 0L;
	private static long totalMemory = 0L, imageMemory = 0L, freeMemory1 = 0L,
			freeMemory2 = 0L;

	/** Clear the image cache. */
	public static void clear() {
		if (imgBuf != null) {
			imgBuf.clear();
		}
		cacheMemory = 0L;
	}

	/**
	 * Create a full access path for resource files. This may be images, sounds
	 * or other display resources.
	 * 
	 * <p>
	 * If the directory argument starts with a '.'-character then the current
	 * design file's location ('design base') is used instead of the
	 * '.'-character as the directory. This holds for both applications and
	 * applets.
	 * 
	 * <p>
	 * If the director argument is empty and the file name argument starts with
	 * the '/'-character, then the resulting path is identical to the file name
	 * argument.
	 * 
	 * <p>
	 * If the directory argument is empty while the file name argument does not
	 * start with the '/'-character or if the directory argument is a path name
	 * which does not have any special leading characters or strings, then the
	 * resulting path is considered to be relative to the current working
	 * directory for applications and relative to the document base for applets.
	 * 
	 * <p>
	 * If the directory argument is something like 'tmp' or 'temp', then the
	 * value of the respective environment variable is used as a directory. This
	 * works for applications only.
	 * 
	 * @param dir
	 *            the directory part of the path. May be empty.
	 * @param fn
	 *            the file name part of the path.
	 * @return a full access path.
	 */
	public static String createResourcePath(String dir, String fn) {
		Debug.show(Debug.FILES, "FileBase.createResourcePath(" + dir + ", "
				+ fn + ")");
		String dp = dir;
		// First construct the directory path ending in '/'
		if (StringExt.nonEmpty(dir)) {
			// we have a directory string
			// both for applets and applications "." refers to the design base
			if (dir.startsWith(".")) {
				if (dir.equals(".") || dir.equals("./")) {
					// no subdirectory given, the design base is it
					dp = designBase();
				} else if (dir.startsWith("./")) {
					// we have a subdirectory, like ./images
					dp = designBase() + dir.substring(2);
				} else {
					// we have a subdirectory, like .images
					dp = designBase() + dir.substring(1);
				}
			} else {
				if (Base.isApplication()) {
					// we are an application and are not starting with "."
					// try {
					if (dir.equalsIgnoreCase("tmp")
							|| dir.equalsIgnoreCase("temp")) {
						// the directory is something like 'tmp' so use
						// the respective environment variable as a
						// directory if it exists
						String d = System.getenv(dir);
						if (d != null) {
							dp = d + File.separator;
							// does not work with audio:
							// dp = new File(d +
							// File.separator).toURI().toURL().toString();
						}
					} else {
						// already set: dp = dir
						// does not work with audio:
						// dp = new File(dir).toURI().toURL().toString();
					}
					/*
					 * } catch (MalformedURLException mfx) { }
					 */
				} else {
					// we are an applet and are not starting with "."
					if (!dir.startsWith("http://") && !dir.startsWith("/")
							&& !dir.startsWith("\\")) {
						dp = Base.getApplet().getDocumentBase().toString();
					}
				}
			}
			dp = dp.replace('\\', '/');
			// Make sure that we end in '/'
			if (!dp.endsWith("/")) {
				dp = dp + "/";
			}
		} else {
			// no directory, so use the document base as directory if
			// the file name does not start with '/'.
			if (!fn.startsWith("/")) {
				if (Base.isApplication()) {
					// empty dir part, so use current working directory
				} else {
					// empty dir part, so use document base
					dp = Base.getApplet().getDocumentBase().toString();
				}
			} else {
				// dp is an empty string here
			}
		}
		String p = StringExt.nonEmpty(dp) ? dp + fn : fn;
		Debug.show(Debug.FILES, "FileBase.createResourcePath(): " + p);
		return p;
	}

	private static String designBase() {
		URL db = Base.getDesignBase();
		Debug.show(Debug.FILES, "FileBase.designBase(): " + db);
		String dbrs = "";
		if (db != null) {
			String dbs = db.toString();
			dbrs = dbs.substring(0, dbs.lastIndexOf('/') + 1);
		}
		return dbrs;
	}

	/**
	 * Create an URL for the given directory and file name. Works like the
	 * method createResourcePath() but returns an URL.
	 * 
	 * @param dir
	 *            the directory part of the path. May be null.
	 * @param fn
	 *            the file name part of the path.
	 * @return an URL for the path.
	 * @see #createResourcePath
	 */
	public static URL url(String dir, String fn) {
		String fileName = FileBase.createResourcePath(dir, fn);
		URL url = null;
		try {
			if (!fileName.startsWith("http://")
					&& !fileName.startsWith("file:/")) {
				url = new URL("file:///" + fileName);
			} else {
				url = new URL(fileName);
			}
		} catch (MalformedURLException mue2) {
			new FileError("FileBase.url(): Can't create URL for " + fileName);
		}
		return url;
	}

	/**
	 * Load an image from the given location. The location is defined by the
	 * directory and the file name argument.
	 * 
	 * <ul>
	 * 
	 * <li>If any of the directory argument or the file name argument starts
	 * with the '@'-character then the image is cached in a local image cache
	 * for later re-use. The '@'-character is removed before the image location
	 * path is being built.
	 * 
	 * <li>The access path is built as described for createResourcePath() after
	 * optional '@'-characters have been removed.
	 * 
	 * <li>If any of the directory argument or the file name argument starts
	 * with the '@'-character and an image with the resulting access path is in
	 * the image cache then it is used.
	 * 
	 * <li>If the directory argument is empty then the file name argument is
	 * used as the location.
	 * 
	 * <li>If the location defines a valid URL which starts with 'http://' after
	 * optional '@'-characters have been removed, then an URL is created and the
	 * image is loaded from that URL.
	 * 
	 * <li>If the location starts with '/' after optional '@'-characters have
	 * been removed, then this object's class loader is used to locate the image
	 * in the classpath.
	 * 
	 * <li>If the location path neither starts with 'http://' nore with a
	 * '/'-character and the image can not be found at the given location then
	 * an additional directory prefix is added to the image location. This
	 * additional prefix is identical to the PXLab property pxlab.home. Thus as
	 * an example if the directory is 'memory', the file name is 'set1.jpg' and
	 * the property pxlab.home has the value 'x:/pxlab' then the resulting
	 * location will be 'x:/pxlab/memory/set1.jpg'.
	 * 
	 * </ul>
	 * 
	 * <p>
	 * <em>Note that the PXLab design file grammar uses the
	'\'-character in a string as an escape sequence. This means
	that strings like 'c:\Images' are NOT allowed and MUST be
	written as 'c:/Images'.

	<p>The debugging option '-D files' may be used to log the
	creation of file access paths and the loading of files. Option
	'-D cache' will log cache memory usage.
	 * 
	 * @param dir
	 *            the directory part of the image path. This will generally be
	 *            the value of the experimental parameter 'Directory'. It may be
	 *            an empty string.
	 * @param fn
	 *            the file name of the image file. This will generally be the
	 *            value of the parameter 'FileName'. It must not be empty.
	 * @param w
	 *            screen with of the image. This parameter is only used for
	 *            images whose size is not implicit in the file like scalable
	 *            vector graphic images.
	 * @param h
	 *            screen height of the image. This parameter is only used for
	 *            files whose size is not implicit inj the file like scalable
	 *            vector graphic images.
	 * @return the image contained in the given file or null if the image could
	 *         not be found.
	 * @see #createResourcePath
	 */
	public static BufferedImage loadImage(String dir, String fn, int w, int h) {
		Debug.show(Debug.FILES, "FileBase.loadImage(" + dir + ", " + fn + ")");
		BufferedImage slide = null;
		boolean buffering = false;
		if (fn.startsWith("@")) {
			buffering = true;
			fn = fn.substring(1);
		}
		if (dir.startsWith("@")) {
			buffering = true;
			dir = dir.substring(1);
		}
		String fp = null;
		boolean useFactory = false;
		if (dir.startsWith("SAMImageFactory")) {
			fp = dir + "-" + fn + "@" + String.valueOf(w) + "x"
					+ String.valueOf(h);
			useFactory = true;
		} else {
			fp = createResourcePath(dir, fn);
		}
		if (buffering) {
			if (imgBuf == null) {
				imgBuf = new HashMap(10);
			}
			slide = (BufferedImage) (imgBuf.get(fp));
			if (slide != null) {
				Debug.show(Debug.FILES,
						"FileBase.loadImage(): Get image from cache: " + fp);
				return slide;
			} else {
				Debug.show(Debug.FILES,
						"FileBase.loadImage(): Image not in cache: " + fp);
			}
		}
		if (Debug.isActive(Debug.CACHE)) {
			System.gc();
			Runtime runtime = Runtime.getRuntime();
			totalMemory = runtime.totalMemory();
			freeMemory1 = runtime.freeMemory();
		}
		if (useFactory) {
			slide = new SAMImageFactory().instance(fp, w, h);
		} else {
			slide = fp.toLowerCase().endsWith(".svg") ? loadSVGImage(fp, w, h)
					: loadBitmapImage(fp);
		}
		if (Debug.isActive(Debug.CACHE)) {
			System.gc();
			freeMemory2 = Runtime.getRuntime().freeMemory();
			imageMemory = slide.getWidth() * slide.getHeight() * 4;
			cacheMemory += imageMemory;
		}
		if (buffering && (slide != null)) {
			imgBuf.put(fp, slide);
			Debug.show(Debug.FILES | Debug.CACHE,
					"FileBase.loadImage(): Moved image to cache: " + fp);
			if (Debug.isActive(Debug.CACHE)) {
				cacheLog();
			}
		}
		return slide;
	}

	/**
	 * Load an image from the given location. The location is defined by the
	 * directory and the file name argument.
	 * 
	 * @param dir
	 *            the directory part of the image path. This will generally be
	 *            the value of the experimental parameter 'Directory'. It may be
	 *            an empty string.
	 * @param fn
	 *            the file name of the image file. This will generally be the
	 *            value of the parameter 'FileName'. It must not be empty.
	 * @return the image contained in the given file or null if the image could
	 *         not be found.
	 * @see #createResourcePath
	 */
	public static BufferedImage loadImage(String dir, String fn) {
		return loadImage(dir, fn, 0, 0);
	}

	private static void cacheLog() {
		System.out.println("  Total memory: " + totalMemory / 1024L + " KB");
		System.out.println("  Free memory: " + freeMemory1 / 1024L + " KB");
		System.out.println("  Used by this image: " + imageMemory / 1024L
				+ " KB");
		System.out.println("  Total cache memory used: " + cacheMemory / 1024L
				+ " KB");
		System.out.println("  Remaining free memory: " + freeMemory2 / 1024L
				+ " KB");
	}

	private static BufferedImage loadSVGImage(String fp, int w, int h) {
		try {
			SVGLoader svg = new SVGLoader();
			return svg.load(fp, w, h);
		} catch (Exception ex) {
			System.out
					.println("Can't instantiate SVGLoader. Probable reason: The batik package is missing.");
		}
		return null;
	}

	private static BufferedImage loadBitmapImage(String fp) {
		BufferedImage slide = null;
		/*
		 * Thre options are remaining now: The filepath starts with
		 * 
		 * http:// - we use an URL loader.
		 * 
		 * /... - we use a class resource loader.
		 * 
		 * anything else - we try to read the file from the file system. If this
		 * fails then the file is searched in the directory specified by the
		 * property 'pxlab.home'.
		 */
		URL url = null;
		try {
			url = new URL(fp);
		} catch (MalformedURLException mfx) {
		}
		if (url != null) {
			Debug.show(Debug.FILES, "FileBase.loadImage(): From URL = " + url);
			try {
				slide = javax.imageio.ImageIO.read(url);
				Debug.show(Debug.FILES, "FileBase.loadImage(): Image loaded.");
			} catch (IOException iox) {
				Debug.show(Debug.FILES, "FileBase.loadImage(): URL failed.");
			}
		} else if (fp.startsWith("/")) {
			Debug.show(Debug.FILES,
					"FileBase.loadImage(): Trying class resource loader.");
			// Does not work - don't know why !!!
			// url = ClassLoader.getSystemResource(fp);
			url = FileBase.class.getResource(fp);
			if (url == null) {
				Debug.show(Debug.FILES,
						"FileBase.loadImage(): Class resource loader failed.");
			} else {
				try {
					slide = javax.imageio.ImageIO.read(url);
					Debug.show(Debug.FILES,
							"FileBase.loadImage(): Image loaded.");
				} catch (IOException iox) {
					Debug.show(Debug.FILES,
							"FileBase.loadImage(): Image file reader failed. Image not loaded.");
				}
			}
		} else {
			File f = new File(fp);
			if (!f.canRead()) {
				fp = Base.getProperty("pxlab.home") + File.separator + fp;
				f = new File(fp);
			}
			Debug.show(Debug.FILES,
					"FileBase.loadImage(): Trying image file reader for " + fp);
			try {
				slide = javax.imageio.ImageIO.read(f);
				Debug.show(Debug.FILES, "FileBase.loadImage(): Image loaded.");
			} catch (IOException iox) {
				Debug.show(Debug.FILES,
						"FileBase.loadImage(): Image file reader failed. Image not loaded");
			}
		}
		return slide;
	}

	/**
	 * Load an array of strings from the given location. The location is defined
	 * by the directory and the file name argument.
	 * 
	 * @param dir
	 *            the directory part of the file path. It may be an empty
	 *            string.
	 * @param fn
	 *            the file name of the text file. It must not be empty.
	 * @return an array of strings which contains one entry for every line of
	 *         text in the file.
	 * @see #createResourcePath
	 */
	public static String[] loadStrings(String dir, String fn) {
		Debug.show(Debug.FILES, "FileBase.loadStrings(" + dir + ", " + fn + ")");
		String fp = createResourcePath(dir, fn);
		BufferedReader reader = null;
		URL url;
		if (fp.startsWith("http://") || fp.startsWith("file:/")) {
			Debug.show(Debug.FILES, "FileBase.loadString(): Try URL " + fp);
			try {
				url = new URL(fp);
				Debug.show(Debug.FILES,
						"FileBase.loadString(): Wellformed URL = " + url);
				try {
					Debug.show(Debug.FILES,
							"FileBase.loadStrings(): Open stream " + url);
					reader = new BufferedReader(new InputStreamReader(
							url.openStream()));
					Debug.show(Debug.FILES,
							"FileBase.loadStrings(): Stream opened.");
				} catch (IOException iox) {
					Debug.show(Debug.FILES,
							"FileBase.loadStrings(): Connection failed.");
				}
			} catch (MalformedURLException mfx) {
				Debug.show(Debug.FILES,
						"FileBase.loadStrings(): Malformed URL.");
			}
		} else {
			Debug.show(Debug.FILES, "FileBase.loadStrings(): Try file " + fp);
			File file = new File(fp);
			if (file.isFile() && file.canRead()) {
				Debug.show(Debug.FILES, "FileBase.loadStrings(): Read file "
						+ fp);
				try {
					reader = new BufferedReader(new FileReader(file));
				} catch (IOException iox) {
					Debug.show(Debug.FILES,
							"FileBase.loadStrings(): Can't open file " + fp);
				}
			} else {
				Debug.show(Debug.FILES,
						"FileBase.loadStrings(): Can't read file " + fp);
			}
		}
		String[] strings = null;
		if (reader != null) {
			String str;
			ArrayList colist = new ArrayList(100);
			try {
				while ((str = reader.readLine()) != null) {
					colist.add(str);
				}
				reader.close();
				Debug.show(Debug.FILES, "FileBase.loadStrings(): Closed " + fp);
				strings = StringExt.stringArrayOfList(colist);
			} catch (IOException iox) {
				Debug.show(Debug.FILES,
						"FileBase.loadStrings(): Error while reading data from file "
								+ fp);
			}
		}
		return strings;
	}

	/**
	 * Load a string from the given location. The location is defined by the
	 * directory and the file name argument.
	 * 
	 * @param dir
	 *            the directory part of the file path. It may be an empty
	 *            string.
	 * @param fn
	 *            the file name of the text file. It must not be empty.
	 * @return a string which contains the text in the file.
	 * @see #createResourcePath
	 */
	public static String loadString(String dir, String fn) {
		Debug.show(Debug.FILES, "FileBase.loadString(" + dir + ", " + fn + ")");
		String[] a = loadStrings(dir, fn);
		StringBuilder b = new StringBuilder(1000);
		if (a != null) {
			b.append(a[0]);
			for (int i = 1; i < a.length; i++) {
				b.append('\n');
				b.append(a[i]);
			}
			b.append('\n');
		}
		return b.toString();
	}
}
