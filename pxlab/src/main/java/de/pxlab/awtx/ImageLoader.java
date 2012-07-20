package de.pxlab.awtx;

import java.awt.Image;
import java.awt.Component;
import java.awt.Toolkit;
import java.io.*;

public class ImageLoader {
	/**
	 * Utility method that creates a UIDefaults.LazyValue that creates an
	 * ImageIcon UIResource for the specified <code>gifFile</code> filename.
	 * Copy resource into a byte array. This is necessary because several
	 * browsers consider Class.getResource a security risk because it can be
	 * used to load additional classes. Class.getResourceAsStream just returns
	 * raw bytes, which we can convert to an image.
	 */
	public static Image get(final Component component, final String gifFile) {
		Class baseClass = component.getClass();
		final byte[][] buffer = new byte[1][];
		try {
			InputStream resource = baseClass.getResourceAsStream(gifFile);
			if (resource == null) {
				return null;
			}
			BufferedInputStream in = new BufferedInputStream(resource);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
			buffer[0] = new byte[1024];
			int n;
			while ((n = in.read(buffer[0])) > 0) {
				out.write(buffer[0], 0, n);
			}
			in.close();
			out.flush();
			buffer[0] = out.toByteArray();
		} catch (IOException ioe) {
			System.err.println(ioe.toString());
			return null;
		}
		if (buffer[0] == null) {
			System.err.println(baseClass.getName() + "/" + gifFile
					+ " not found.");
			return null;
		}
		if (buffer[0].length == 0) {
			System.err.println("warning: " + gifFile + " is zero-length");
			return null;
		}
		return (Toolkit.getDefaultToolkit().createImage(buffer[0]));
	}
}
