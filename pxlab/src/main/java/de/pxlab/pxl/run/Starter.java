package de.pxlab.pxl.run;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import de.pxlab.util.FilenameExtensionFilter;
import de.pxlab.pxl.*;

/**
 * This is a starter tool for starting PXLab experiments from a list of
 * experiments.
 * 
 * @version 0.2.2
 */
/*
 * 
 * 2004/11/08
 * 
 * 2005/12/08 added dataProcessor
 */
public class Starter extends JFrame implements ActionListener,
		ExDesignThreadStarter {
	private Dimension screenSize;
	private int fontSize;
	private JButton closeButton, stopButton;
	private ArrayList buttonList;
	private static String[] cmdlnArgs = null;
	private PresentationManager presentationManager;
	private ExDesign runningExDesign;
	private int maxButtonRows;
	private Color startButtonColor = new Color(241, 223, 35);
	private Color sxButtonColor = new Color(242, 179, 23);

	// private Color startButtonColor = new Color(224, 224, 224);
	// private Color sxButtonColor = new Color(192, 192, 192);
	public Starter() {
		super(" " + Base.getPXLabCopyright());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		de.pxlab.pxl.PXLabIcon.decorate(this);
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		ArrayList experiments = getAvailableExperiments();
		int numExp = experiments.size();
		fontSize = (int) (screenSize.getHeight() / 72);
		if (fontSize < 12) {
			fontSize = 12;
		}
		maxButtonRows = (int) (screenSize.getHeight() / (2 * fontSize));
		buttonList = new ArrayList(numExp + 2);
		JPanel contentPane = new JPanel(new BorderLayout());
		setContentPane(contentPane);
		Font tf = new Font("Sans", Font.BOLD, fontSize);
		JPanel bottomPanel = new JPanel(new GridLayout(1, 0));
		stopButton = new JButton("Stop");
		stopButton.setFocusable(false);
		stopButton.setFont(tf);
		stopButton.setBackground(sxButtonColor);
		stopButton.addActionListener(this);
		bottomPanel.add(stopButton);
		closeButton = new JButton("Close");
		closeButton.setFocusable(false);
		closeButton.setFont(tf);
		closeButton.setBackground(sxButtonColor);
		closeButton.addActionListener(this);
		bottomPanel.add(closeButton);
		contentPane.add(bottomPanel, BorderLayout.SOUTH);
		buttonList.add(stopButton);
		buttonList.add(closeButton);
		int gridColumns = numExp / maxButtonRows + 1;
		JPanel buttonPanel = new JPanel(new GridLayout(0, gridColumns));
		contentPane.add(buttonPanel, BorderLayout.CENTER);
		if (numExp > 0) {
			for (int i = 0; i < numExp; i++) {
				JButton b = new StartButton(
						((ExperimentDescriptor) (experiments.get(i))));
				b.setHorizontalAlignment(SwingConstants.LEFT);
				buttonPanel.add(b);
				buttonList.add(b);
			}
			setButtonEnabledStates(true);
			pack();
			Dimension fs = getSize();
			setLocation((int) ((screenSize.getWidth() - fs.getWidth()) / 2),
					(int) ((screenSize.getHeight() - fs.getHeight()) / 32));
			setVisible(true);
		} else {
			System.out.println("No design files found.");
			System.exit(3);
		}
	}

	private void setButtonEnabledStates(boolean s) {
		for (int i = 0; i < buttonList.size(); i++) {
			JButton b = (JButton) (buttonList.get(i));
			b.setEnabled((b == stopButton) ? !s : s);
		}
	}

	private ArrayList getAvailableExperiments() {
		ArrayList<File> fnlist = new ArrayList<File>(30);
		// System.out.println(cmdlnArgs.length);
		// for (int i = 0; i < cmdlnArgs.length; i++)
		// System.out.println(cmdlnArgs[i]);
		if ((cmdlnArgs == null) || (cmdlnArgs.length == 0)) {
			// No arguments so use default pxd-directory
			String dir = Base.getProperty("starter.dir");
			if (dir != null) {
				File fd = new File(dir);
				if (fd.isDirectory()) {
					File[] list = fd.listFiles(new FilenameExtensionFilter(
							"pxd"));
					for (int i = 0; i < list.length; i++) {
						if (list[i].isFile() && list[i].canRead()) {
							fnlist.add(list[i]);
						}
					}
				}
			}
		} else {
			for (int j = 0; j < cmdlnArgs.length; j++) {
				File fd = new File(cmdlnArgs[j]);
				if (fd.isDirectory()) {
					File[] list = fd.listFiles(new FilenameExtensionFilter(
							"pxd"));
					for (int i = 0; i < list.length; i++) {
						if (list[i].isFile() && list[i].canRead()) {
							fnlist.add(list[i]);
						}
					}
				} else {
					if (fd.isFile() && fd.canRead()) {
						fnlist.add(fd);
					}
				}
			}
		}
		ArrayList ed = new ArrayList();
		int n = fnlist.size();
		if (n > 0) {
			for (int i = 0; i < n; i++) {
				ed.add(new ExperimentDescriptor(fnlist.get(i)));
			}
		}
		return ed;
	}
	public class StartButton extends JButton {
		private ExperimentDescriptor experiment;

		public StartButton(ExperimentDescriptor experiment) {
			super("   " + experiment.getLabel() + "   ");
			Font tf = new Font("Sans", Font.BOLD, fontSize);
			setFont(tf);
			setBackground(startButtonColor);
			this.experiment = experiment;
			addActionListener(Starter.this);
			/*
			 * System.out.println("<H2>&nbsp;</H2>\n<a name=\"" +
			 * experiment.getFullPath() + "\">\n<?php startButton(\"" +
			 * experiment.getFullPath() + "\", \"" + experiment.getLabel() +
			 * "\");?>\n</a>");
			 */
		}

		public ExperimentDescriptor getExperiment() {
			return experiment;
		}
	}

	public void actionPerformed(ActionEvent e) {
		Object x = e.getSource();
		if (x instanceof StartButton) {
			startExperiment(((StartButton) x).getExperiment());
		} else {
			if (x == stopButton) {
				runningExDesign.stop();
			} else if (x == closeButton) {
				System.exit(0);
			}
		}
	}

	public void startExperiment(ExperimentDescriptor exd) {
		// System.out.println("Starting " + ex.toString());
		try {
			runningExDesign = new ExDesign(exd.getFullPath(), null);
			setButtonEnabledStates(false);
			presentationManager = new PresentationManager(this, this);
			ExDesignDataProcessor dataProcessor = new DataProcessor(this,
					runningExDesign);
			runningExDesign.runExperiment(this, presentationManager,
					dataProcessor);
		} catch (IOException ex) {
			System.out
					.println("Starter.startExperiment() Error when parsing file "
							+ exd.getFullPath());
		}
	}

	// ---------------------------------------------------------------
	// ExDesignThreadStarter implementation
	// ---------------------------------------------------------------
	/**
	 * The ExDesign experimental run thread calls this method to signal the
	 * object which sent the runExperiment() method to the ExDesign object that
	 * the experimental run is finished.
	 */
	public void experimentFinished(boolean s) {
		if (s)
			presentationManager.close();
		presentationManager.dispose();
		setButtonEnabledStates(true);
	}

	public void stopExperiment() {
		runningExDesign.stop();
	}

	/* ------------------------------------------------------- */
	/* main() entry point */
	/* ------------------------------------------------------- */
	public static void main(String[] args) {
		// Check for command line arguments
		if ((args != null) && (args.length > 0)) {
			// Handle command line options first
			ExRunOptionHandler optionHandler = new ExRunOptionHandler(
					"Starter", args);
			optionHandler.evaluate();
			cmdlnArgs = optionHandler.getNonOptionArgs();
		}
		// Set the current system's look an feel
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager
					.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		// Make sure that the GUI is created in the event queue
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Starter();
			}
		});
	}
}
