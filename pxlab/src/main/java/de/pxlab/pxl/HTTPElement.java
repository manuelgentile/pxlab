package de.pxlab.pxl;

import java.io.*;
import java.net.*;

import de.pxlab.util.*;

/**
 * Creates a HTTP connection and sends a text string to a receiving script.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2008/03/07
 */
public class HTTPElement extends DisplayElement {
	URL scriptURL;
	String method;
	String source;
	String address;
	String subject;
	String text;
	String response = "";

	public HTTPElement() {
	}

	public boolean setProperties(String scriptURL, String method,
			String source, String address, String subject, String text) {
		boolean valid = true;
		try {
			this.scriptURL = new URL(scriptURL);
		} catch (MalformedURLException mfx) {
			valid = false;
			new NonFatalError("Malformed URL: " + scriptURL);
		}
		if (!method.equals("POST")) {
			valid = false;
			new NonFatalError("Illegal HTTP request: " + method
					+ " HTTPElement currently only supports POST");
		} else {
			this.method = method;
		}
		this.source = source;
		this.address = address;
		this.subject = subject;
		this.text = text;
		return valid;
	}

	public void show() {
		StringBuilder s = new StringBuilder(3000);
		s.append("Source=" + source);
		s.append("&Address=" + address);
		s.append("&Subject=" + subject);
		s.append("&Text=" + text);
		sendPOST(s.toString());
	}

	private void sendPOST(String s) {
		// System.out.println("HTTPElement.sendPOST(): Trying to POST request to "
		// + scriptURL);
		byte[] bytes = s.getBytes();
		// Create a URL pointing to the servlet or CGI script and open
		// an HttpURLConnection on that URL
		try {
			HttpURLConnection con = (HttpURLConnection) scriptURL
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
					StringBuilder sb = new StringBuilder(3000);
					while (true) {
						String line = in.readLine();
						if (line == null)
							break;
						sb.append(line + "\n");
					}
					in.close();
					response = sb.toString();
					out.close();
					con.disconnect();
				} catch (IOException iox2) {
					System.out.println("Error with output stream to "
							+ scriptURL + " - Data are not stored.");
					con.disconnect();
				}
			} catch (ProtocolException pex) {
				System.out.println("Can't use POST method with " + scriptURL
						+ " - Data are not stored.");
				con.disconnect();
			}
		} catch (IOException iox) {
			System.out.println("Can't open connection to " + scriptURL
					+ " - Data are not stored.");
		}
		// System.out.println("HTTPElement.sendPOST(): response is\n" +
		// response);
	}

	public String getResponse() {
		return response;
	}
}
