package de.pxlab.pxl.run;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.StringTokenizer;

import java.util.ArrayList;
import de.pxlab.util.*;
import de.pxlab.pxl.ExDesign;
import de.pxlab.pxl.Base;

/**
 * This little Applet is used to start a PXLab Experiment as an applet from a
 * HTML page using a start button.
 * 
 * <p>
 * This applet accepts the following parameters:
 * <ul>
 * 
 * <li><b>DesignFile</b> is the name of the experimental design file. This file
 * should be contained int the same directory as the HTML-file which contains
 * the applet. Its name is relative to this directory.
 * 
 * <li><b>StartButtonLabel</b> is the label of the start button. Pressing this
 * button starts the experiment.
 * 
 * <li><b>CommandLine</b> is a string which may contain additional command line
 * arguments. See de.pxlab.pxl.run.ExRunOptionHandler for additional command
 * line options.
 * 
 * <li><b>ExDesignClass</b> is the name of an experimental design class. This
 * class must be built in into the PXLab archive and must extend the class
 * ExDesign. The name given as a parameter value must be the fully qualified
 * class name. This parameter may be used for experiments which do not want to
 * use a readable design file but use a compiled design class. If this parameter
 * is defined then it has priority against the parameter DesignFile.
 * 
 * </li>
 * 
 * <p>
 * Here is an example of how to embed this starter applet into a HTML page using
 * the APPLET-Tag of HTML:
 * 
 * <pre>
 * &lt;APPLET codebase="." 
 *         archive="pxlab.jar" 
 *         code="de.pxlab.pxl.run.ExRunStarterApplet.class"
 * 	width=500 height=60 mayscript&gt;
 *   &lt;param name="CommandLine" value="-S2"&gt;
 *   &lt;param name="DesignFile" value="demo.pxd"&gt;
 *   &lt;param name="StartButtonLabel" value="Start the Experiment"&gt; 
 * &lt;/APPLET&gt;
 * </pre>
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public class ExRunStarterApplet extends Applet implements ActionListener,
		StarterApplet {
	private static final boolean STOPPED = false;
	private static final boolean RUNNING = true;
	Button startButton;
	boolean buttonState;
	ExRun exRunCommand = null;
	String exDesignFile;
	String exDesignClassName;
	String startLabel;
	ExDesign exDesign;
	String cmdLine;
	String[] args;

	public void init() {
		Base.setApplet(this);
		exDesignClassName = getParameter("ExDesignClass");
		exDesignFile = getParameter("DesignFile");
		String exName = getParameter("StartButtonLabel");
		startLabel = (exName != null) ? exName
				: ((exDesignClassName != null) ? exDesignClassName
						: exDesignFile);
		cmdLine = getParameter("CommandLine");
		args = StringExt.stringArrayOfString(cmdLine);
		setLayout(new GridLayout(1, 0));
		startButton = new Button(startLabel);
		startButton.setFont(new Font("SansSerif", Font.BOLD, 24));
		startButton.addActionListener(this);
		add(startButton);
		buttonState = STOPPED;
		validate();
	}

	public void start() {
		// showStatus("start()");
	}

	public void stop() {
		// showStatus("stop()");
	}

	public void destroy() {
		if (exRunCommand != null) {
			// exRunCommand.dispose();
			exRunCommand = null;
		}
	}

	public void paint(Graphics g) {
	}

	public void actionPerformed(ActionEvent e) {
		if (buttonState == RUNNING) {
			exDesign.stop();
		} else {
			// First try to instantiate the given class
			if (exDesignClassName != null) {
				try {
					Class cls = Class.forName(exDesignClassName);
					try {
						exDesign = (ExDesign) (cls.newInstance());
					} catch (IllegalAccessException iaex) {
					} catch (InstantiationException iex) {
					}
				} catch (LinkageError le) {
				} catch (ClassNotFoundException cnfex) {
				}
			} else {
				// No class name given then try to load the given design file
				if (exDesignFile != null) {
					try {
						exDesign = new ExDesign(getDocumentBase(), exDesignFile);
					} catch (Exception ex) {
					}
				}
			}
			if (exDesign != null) {
				exRunCommand = new ExRun(args, exDesign, this);
				startButton.setLabel("Stop");
				buttonState = RUNNING;
				showStatus("Experiment started.");
			} else {
				showStatus("Can't instantiate experimental design "
						+ ((exDesignClassName != null) ? exDesignClassName
								: exDesignFile));
			}
		}
	}

	public void experimentFinished(boolean s) {
		startButton.setLabel(startLabel);
		buttonState = STOPPED;
		showStatus(s ? "Experiment stopped asynchronously."
				: "Experiment finished.");
	}
}
