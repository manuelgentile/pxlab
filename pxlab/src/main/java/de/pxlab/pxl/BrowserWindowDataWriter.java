package de.pxlab.pxl;

import netscape.javascript.*;

/**
 * A Text file which consumes standard PXLab data strings. It stores them until
 * the experiment is finished. Then the data are sent to a browser window which
 * is created by a small piece of JavaScript code contained in the HTML file
 * which also contains the applet. The piece of JavaScript code must look like
 * this:
 * 
 * <p>
 * &lt;SCRIPT&gt;<br>
 * function showData(s) {<br>
 * ergWin = window.open()<br>
 * ergWin.document.write(s)<br>
 * } <br>
 * &lt;/SCRIPT&gt;<br>
 * </p>
 * 
 * @author H. Irtel
 * @version 0.2.1
 * @see ExDesign
 */
/*
 * 
 * 08/12/00
 * 
 * 2005/10/19 write data only if the data file is non-empty
 */
public class BrowserWindowDataWriter extends CollectingDataWriter {
	protected String fileName;

	/**
	 * Create an browser window data writer. Note that data are not written
	 * before the data collection is complete.
	 * 
	 * @param fn
	 *            data file name. This name is sent to the browser window as a
	 *            title line.
	 */
	public BrowserWindowDataWriter(String fn) {
		super();
		fileName = fn;
		String nl = System.getProperty("line.separator");
		lineBreak = "<br>" + nl;
	}

	/**
	 * Signal this DataWriter object that data collection is complete.
	 * 
	 * @param status
	 *            the final status of the data source.
	 */
	public void dataComplete(int status) {
		if (hasData) {
			showDataDocument(createHTMLDocument("Data File " + fileName,
					data.toString()));
		}
	}

	public boolean showResults(String s) {
		showDataDocument(createHTMLDocument("Results", s));
		return true;
	}

	/** Show the given string in a new window of the browser. */
	private void showDataDocument(String ds) {
		// System.out.println("BrowserWindowDataWriter.showDataDocument()");
		// new Exception().printStackTrace();
		if (!Base.isApplication()) {
			try {
				try {
					JSObject win = JSObject.getWindow(Base.getApplet());
					String[] dsa = { ds };
					win.call("showData", dsa);
				} catch (JSException jse) {
					System.out
							.println("BrowserWindowDataWriter.showDataDocument() Failed to open browser window.");
					// jse.printStackTrace();
				}
			} catch (java.lang.NoClassDefFoundError rex) {
				System.out
						.println("BrowserWindowDataWriter.showDataDocument() failed to initialize JavaScript.");
				// rex.printStackTrace();
			}
		}
		// System.out.println("BrowserWindowDataWriter.showDataDocument() finished.");
	}
	/*
	 * This method should work, but it does not! private void
	 * showDataDocument(String ds) { if (!Base.isApplication()) { try { JSObject
	 * win = JSObject.getWindow(Base.getApplet()); Object args[] = {"",
	 * " PXLab Data ",
	 * "width=400, height=500, location=0, menubar=0, status=0, toolbar=0"};
	 * JSObject dataWin = (JSObject)win.call("open", args); JSObject doc =
	 * (JSObject)dataWin.getMember("document"); String[] dsa = {ds};
	 * doc.call("write", dsa); } catch (JSException jse) {
	 * jse.printStackTrace(); } } }
	 */
}
