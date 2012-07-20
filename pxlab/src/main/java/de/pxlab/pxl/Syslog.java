package de.pxlab.pxl;

import java.io.*;
import de.pxlab.awtx.*;

/**
 * A system log stream. It captures messages which usually would goto to
 * System.out but should be captured by a TextArea component when working in a
 * graphic environment.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class Syslog {
	private static ProtocolStream pstream = new ProtocolStream();
	public static PrintStream out = new PrintStream(pstream, true);

	public static void setSilent(boolean s) {
		pstream.setSilent(s);
	}
}
