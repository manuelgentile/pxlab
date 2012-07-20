package de.pxlab.pxl;

import java.applet.AppletContext;
import java.io.*;
import java.net.*;

import de.pxlab.util.StringExt;

// import netscape.javascript.*;
/**
 * Send PXLab data to an URL after the experiment is finished.
 * 
 * @author H. Irtel
 * @version 0.2.1
 * @see ExDesign
 */
/*
 * 
 * 08/12/00
 * 
 * 04/01/03 changed HTTP protocol to POST in order to get rid of the length
 * restrictions of the GET method
 * 
 * 2004/12/17 use DataFileDestinationAddress to tell the receiver where to send
 * the data file.
 * 
 * 2005/10/19 write data only if the data file is non-empty
 */
public class URLDataWriter extends CollectingDataWriter {
	protected URL dataURL;
	protected String fileName;

	/**
	 * Create an URL data writer. Note that the URL connection is not opened
	 * before the data collection is complete.
	 * 
	 * @param dest
	 *            the desination URL for the data string. If the destination is
	 *            a script then this must be the full script URL.
	 * @param fn
	 *            data file name. This name is sent to the URL script handler as
	 *            a hint where to store the data.
	 */
	public URLDataWriter(URL dest, String fn) {
		super();
		dataURL = dest;
		fileName = fn;
	}

	/**
	 * Signal this DataWriter object that data collection is complete.
	 * 
	 * @param status
	 *            the final status of the data source.
	 */
	public void dataComplete(int status) {
		if (hasData) {
			String email = ExPar.DataFileDestinationAddress.getString();
			String ds = "DataFileName=" + fileName;
			if (StringExt.nonEmpty(email)) {
				ds = ds + "&Email=" + email;
			}
			try {
				ds = ds + "&Data="
						+ URLEncoder.encode(data.toString(), "UTF-8");
				String rm = ExPar.HTTPRequestMethod.getString();
				if (rm.equals("GET")) {
					sendDataWithGET(ds);
				} else if (rm.equals("POST")) {
					sendDataWithPOST(ds);
				}
			} catch (UnsupportedEncodingException uex) {
				System.out
						.println("URLDataWriter.dataComplete(): UTF-8 encoding unsupported. Data not sent.");
			}
		}
	}

	/**
	 * Send the current data string to the data file destination URL using the
	 * POST method. This appends two parameters to the URL: The parameter
	 * DataFileName which has the data file name as its value and the data as
	 * values of parameter Data. A script should be used to decode and store the
	 * data. here is a sample PHP script which stores the data into file
	 * 'pxlab.dat' and also echos the data back to the client:
	 * 
	 * <pre>
	 * 
	 * if (file_exists(&quot;pxlab.dat&quot;) == 1) {
	 * 	unlink(&quot;pxlab.dat&quot;);
	 * }
	 * $file = fopen(&quot;pxlab.dat&quot;, &quot;w&quot;);
	 * if ($file) {
	 * 	fputs($file, $Data);
	 * 	fclose($file);
	 * 	echo(&quot;&lt;p&gt;Your data have been stored!.&quot;);
	 * 	echo(&quot;&lt;p&gt;The data set was:&lt;br&gt;&quot;);
	 * 	echo $Data;
	 * }
	 * </pre>
	 */
	private void sendDataWithPOST(String dataString) {
		// System.out.println("URLDataWriter.sendDataWithPOST(): Trying to POST data to "
		// + dataURL);
		byte[] bytes = dataString.getBytes();
		// Create a URL pointing to the servlet or CGI script and open
		// an HttpURLConnection on that URL
		try {
			HttpURLConnection con = (HttpURLConnection) dataURL
					.openConnection();
			// Indicate that you will be doing output and input, that
			// the method is POST, and that the content length is the
			// length of the byte array
			con.setDoOutput(true);
			con.setDoInput(true);
			try {
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-length",
						String.valueOf(bytes.length));
				try {
					// Write the parameters to the URL output stream
					OutputStream out = con.getOutputStream();
					out.write(bytes);
					out.flush();
					// Read the response
					BufferedReader in = new BufferedReader(
							new InputStreamReader(con.getInputStream()));
					while (true) {
						String line = in.readLine();
						if (line == null)
							break;
						System.out.println(line);
					}
					in.close();
					out.close();
					con.disconnect();
				} catch (IOException iox2) {
					System.out.println("Error with output stream to " + dataURL
							+ " - Data are not stored.");
					con.disconnect();
				}
			} catch (ProtocolException pex) {
				System.out.println("Can't use POST method with " + dataURL
						+ " - Data are not stored.");
				con.disconnect();
			}
		} catch (IOException iox) {
			System.out.println("Can't open connection to " + dataURL
					+ " - Data are not stored.");
		}
	}

	/**
	 * Send the current data string to the data file destination URL using the
	 * GET method. This appends two parameters to the URL: The parameter
	 * DataFileName which has the data file name as its value and the data as
	 * values of parameter Data. A PHP script should be used to decode the data.
	 * here is an example for such a script:
	 * <p>
	 * echo("\nDatafile: ".$DataFileName."\n"); <br>
	 * echo("
	 * <p>
	 * \n".$Data);
	 * </p>
	 * 
	 * @param ds
	 *            the data string which should be sent.
	 */
	private void sendDataWithGET(String dataString) {
		// System.out.println("URLDataWriter.showDataWithGET(): Trying to GET data to "
		// + dataURL);
		if (!Base.isApplication()) {
			try {
				URL url = new URL(dataURL, dataURL.getFile() + "?" + dataString);
				AppletContext ac = Base.getApplet().getAppletContext();
				ac.showDocument(url, "_blank");
			} catch (MalformedURLException mue) {
				new ParameterValueError("Can't create URL.\n" + mue.toString());
			}
		}
	}
}
