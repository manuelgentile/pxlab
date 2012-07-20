import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Enumeration;

import de.pxlab.util.StringExt;
import de.pxlab.pxl.Base;
import de.pxlab.pxl.ExDesign;
import de.pxlab.pxl.run.StarterApplet;
import de.pxlab.pxl.run.ExRun;

/**
 * This Applet is used to start a PXLab Experiment as an applet from a HTML page
 * using a start button.
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
 * <li><b>FinishedButtonLabel</b> becomes the label of the start button after
 * the experiment has been finished successfully.
 * 
 * <li><b>RunOnce</b> if set to '1' then this button will be disabled after the
 * experiment has been finished successfully. The applet must be reloaded in
 * order to enable the button again.
 * 
 * <li><b>CommandLine</b> is a string which may contain additional command line
 * arguments. See de.pxlab.pxl.run.ExRunOptionHandler for command line options.
 * 
 * </li>
 * 
 * <p>
 * Here is an example of how to embed this starter applet into a HTML page using
 * the APPLET-Tag of HTML:
 * 
 * <pre>
 * &lt;APPLET codebase="." 
 *         archive="pxlabrt.jar" 
 *         code="ExRunButton.class"
 * 	width=500 height=60 mayscript&gt;
 *   &lt;param name="CommandLine" value="-S2"&gt;
 *   &lt;param name="DesignFile" value="demo.pxd"&gt;
 *   &lt;param name="StartButtonLabel" value="Start the Experiment"&gt; 
 * &lt;/APPLET&gt;
 * </pre>
 * 
 * @author H. Irtel
 * @version 0.4.0
 */
public class ExRunButton extends Applet implements ActionListener,
		StarterApplet {
	private static final boolean STOPPED = false;
	private static final boolean RUNNING = true;
	Button startButton;
	boolean buttonState;
	String startLabel;
	String finishedLabel;
	boolean runOnce = false;
	String exDesignFile;
	ExRun exRunCommand = null;
	ExDesign exDesign;

	public void init() {
		String exName = getParameter("StartButtonLabel");
		startLabel = (exName != null) ? exName : exDesignFile;
		String finished = getParameter("FinishedButtonLabel");
		finishedLabel = (finished != null) ? finished : startLabel;
		String once = getParameter("RunOnce");
		runOnce = !((once == null) || (once.equals("0")));
		exDesignFile = getParameter("DesignFile");
		setLayout(new GridLayout(1, 0));
		startButton = new Button(startLabel);
		startButton.setFont(new Font("SansSerif", Font.BOLD, 24));
		startButton.addActionListener(this);
		add(startButton);
		buttonState = STOPPED;
		validate();
		setButtonEnabled(false);
	}

	public void start() {
		setButtonEnabled(true);
	}

	public void stop() {
		if (buttonState == RUNNING) {
			exDesign.stop();
		}
	}

	public void destroy() {
		if (buttonState == RUNNING) {
			exDesign.stop();
		}
	}

	public void paint(Graphics g) {
	}

	public void actionPerformed(ActionEvent e) {
		if (buttonState == RUNNING) {
			exDesign.stop();
		} else {
			if (Base.getApplet() == null) {
				Base.setApplet(this);
				if (exDesignFile != null) {
					try {
						exDesign = new ExDesign(getDocumentBase(), exDesignFile);
					} catch (Exception ex) {
					}
				}
				if (exDesign != null) {
					exRunCommand = new ExRun(
							StringExt
									.stringArrayOfString(getParameter("CommandLine")),
							exDesign, this);
					startButton.setLabel("Stop");
					buttonState = RUNNING;
					showStatus("Experiment started.");
					// setConcurrentAppletStates(false);
				} else {
					showStatus("Can't instantiate experimental design "
							+ exDesignFile);
				}
			} else {
				showStatus("Multiple runs of PXLab in the same context are not possible!");
			}
		}
	}

	/*
	 * public void setConcurrentAppletStates(boolean s) { AppletContext ac =
	 * getAppletContext(); for (Enumeration n = ac.getApplets();
	 * n.hasMoreElements(); ) { Applet a = (Applet)(n.nextElement()); if (a
	 * instanceof ExRunButton) { // String d = a.getParameter("DesignFile"); //
	 * System.out.println("Button for " + d); if (a != this) {
	 * ((ExRunButton)a).setButtonEnabled(s); } } } }
	 */
	public void setButtonEnabled(boolean s) {
		startButton.setEnabled(s);
	}

	public void experimentFinished(boolean s) {
		startButton.setLabel(s ? startLabel : finishedLabel);
		buttonState = STOPPED;
		if (runOnce && !s)
			setButtonEnabled(false);
		showStatus(s ? "Experiment stopped asynchronously."
				: "Experiment finished.");
		exRunCommand = null;
		Base.setApplet(null);
		// setConcurrentAppletStates(true);
	}

	public String getAppletInfo() {
		return "PXLab Version 2, Copyright H. Irtel 2001-2006";
	}
}
