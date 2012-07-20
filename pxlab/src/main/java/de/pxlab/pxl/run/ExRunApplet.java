package de.pxlab.pxl.run;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;

import java.util.ArrayList;
import de.pxlab.util.StringExt;

import de.pxlab.pxl.*;

/**
 * An Applet which has Start/Stop buttons in its top row and can run a PXLab
 * experiment in its frame.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 2005/12/08 added dataProcessor
 */
public class ExRunApplet extends Applet implements ActionListener,
		ExDesignThreadStarter {
	/** The currently active experimental design. */
	private ExDesign exDesign;
	/** This runs the experiment. */
	private PresentationManager presentationManager;
	/** The name of the last design file loaded. */
	private String exDesignFile;
	/** Buttons to start and stop the experiment. */
	private Button startButton, stopButton;

	public void init() {
		Base.setApplet(this);
		setBackground(Color.black);
		setLayout(new BorderLayout());
		Panel buttonPanel = new Panel(new GridLayout(0, 2));
		startButton = new Button(" Start Experiment ");
		startButton.addActionListener(this);
		buttonPanel.add(startButton);
		stopButton = new Button(" Stop Experiment");
		stopButton.addActionListener(this);
		buttonPanel.add(stopButton);
		Panel aPanel = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		aPanel.setBackground(Color.gray);
		aPanel.add(buttonPanel);
		add(aPanel, BorderLayout.NORTH);
		validate();
	}

	public void actionPerformed(ActionEvent e) {
		Button button = (Button) (e.getSource());
		if (button == stopButton) {
			if (exDesign != null) {
				exDesign.stop();
			}
		}
		if (button == startButton) {
			String[] args = null;
			String cmdLine = getParameter("CommandLine");
			String commandLineAssignments = null;
			// System.out.println("ExRunApplet.actionPerformed(): CommandLine="
			// + cmdLine);
			if (cmdLine != null) {
				args = StringExt.stringArrayOfString(cmdLine);
				if ((args != null) && (args.length > 0)) {
					ExRunOptionHandler optionHandler = new ExRunOptionHandler(
							"ExRun", args);
					optionHandler.evaluate();
					commandLineAssignments = optionHandler
							.getCommandLineAssignments();
				}
			}
			exDesignFile = getParameter("DesignFile");
			if (exDesignFile == null) {
				exDesignFile = ExPar.DesignFileName.getString();
			}
			try {
				exDesign = new ExDesign(getDocumentBase(), exDesignFile);
				showStatus("Design file " + exDesignFile + " loaded.");
				if (commandLineAssignments != null) {
					exDesign.setCommandLineAssignments(commandLineAssignments);
				}
				presentationManager = new PresentationManager(null, this, this);
				ExDesignDataProcessor dataProcessor = new DataProcessor(null,
						exDesign);
				exDesign.runExperiment(this, presentationManager, dataProcessor);
				showStatus("Experiment started.");
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
			} catch (Exception ex) {
				showStatus("Can't find design file.");
				startButton.setEnabled(false);
				stopButton.setEnabled(false);
			}
		}
	}

	public void start() {
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
	}

	public void stop() {
		startButton.setEnabled(false);
		stopButton.setEnabled(false);
	}

	public void destroy() {
	}

	// ------------------------------------------------------------------------
	// ExDesignThreadStarter implementation
	// ------------------------------------------------------------------------
	public void experimentFinished(boolean s) {
		if (s)
			presentationManager.close();
		presentationManager.dispose();
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
		showStatus(s ? "Experiment stopped asynchronously."
				: "Experiment finished.");
	}

	public void stopExperiment() {
		exDesign.stop();
	}
}
