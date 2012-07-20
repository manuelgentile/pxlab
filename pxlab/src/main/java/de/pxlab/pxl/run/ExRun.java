package de.pxlab.pxl.run;

import java.io.File;
import java.awt.Frame;
import java.awt.event.KeyEvent;

import de.pxlab.awtx.*;
import de.pxlab.util.*;
import de.pxlab.pxl.*;

/**
 * A command line experimental run time controller.
 * 
 * @version 0.1.5
 */
/*
 * 
 * 05/20/01 moved setRuntimePars() into class ExDesign
 * 
 * 2004/09/08 fixed Java class export mechanism
 * 
 * 2005/12/08 added dataProcessor
 * 
 * 2008/03/20 added handling of options -k and -K
 */
public class ExRun implements ExDesignThreadStarter {
	/** The experimental design we are going to run. */
	private ExDesign exDesign;
	/**
	 * The presentation manager which controls the display device and runs the
	 * experimental design.
	 */
	private PresentationManager presentationManager = null;
	/** This is the applet which instantiated this ExRun object if any. */
	private StarterApplet starterApplet = null;
	/**
	 * This frame contains the Stop button and works as an owner of subwindows.
	 */
	private Frame controlFrame;

	/**
	 * Run an experiment with the given arguments.
	 * 
	 * @param args
	 *            the command line arguments as delivered by the standard main()
	 *            method.
	 * @param exDesign
	 *            an experimental design object. If this parameter is null then
	 *            the design is read from a design file which must be specified
	 *            by command line arguments or may be selected in a pop-up file
	 *            dialog. If this parameter is non-null then the given design is
	 *            executed. Usually non-null arguments will be derived from
	 *            compiled design files.
	 */
	public ExRun(String[] args, ExDesign exDesign) {
		this(args, exDesign, null);
	}

	/**
	 * Run an experiment with the given arguments.
	 * 
	 * @param args
	 *            the command line arguments as delivered by the standard main()
	 *            method.
	 * @param exDesign
	 *            an experimental design object. If this parameter is null then
	 *            the design is read from a design file which must be specified
	 *            by command line arguments or may be selected in a pop-up file
	 *            dialog. If this parameter is non-null then the given design is
	 *            executed. Usually non-null arguments will be derived from
	 *            compiled design files or from applets which instantiate this
	 *            class.
	 * @param starterApplet
	 *            an applet which has instantiated this class. If this is
	 *            non-null then we know that this class has been instantiated by
	 *            someone else, probably an applet. The applet also provides the
	 *            ExDesign object which should be executed.
	 */
	public ExRun(String[] args, ExDesign exDesign, StarterApplet starterApplet) {
		this.starterApplet = starterApplet;
		int exitCode = 0;
		// Tell the system data base that we are an application if so
		if (starterApplet == null) {
			Base.setApplication();
		}
		// Check for command line arguments
		String commandLineAssignments = null;
		if ((args != null) && (args.length > 0)) {
			// Handle command line options first
			ExRunOptionHandler optionHandler = new ExRunOptionHandler("ExRun",
					args);
			optionHandler.evaluate();
			commandLineAssignments = optionHandler.getCommandLineAssignments();
		}
		// Check whether we have a compiled design class or whether we
		// need a design file.
		if (exDesign == null) {
			// No design tree. We have to open a design file
			if (ExPar.DesignFileName.getValue().isEmpty()) {
				String fn = getExDesignFile();
				if (fn != null) {
					ExPar.DesignFileName.set(fn);
				}
			}
			// At this point we should have a valid design file name
			String exDesignFile = ExPar.DesignFileName.getString();
			if (StringExt.nonEmpty(exDesignFile)) {
				File f = new File(exDesignFile);
				// And check whether the design file may be read
				if (f.canRead()) {
					// Now create the design tree
					try {
						exDesign = new ExDesign(exDesignFile,
								commandLineAssignments);
					} catch (Exception ex) {
						System.err.println("Error when parsing file "
								+ exDesignFile);
						// In this case we already have seen an error dialog
						exitCode = 3;
					}
				} else {
					System.err
							.println("Can't read design file " + exDesignFile);
					exitCode = 3;
				}
			} else {
				// The user has refused to give us a valid design file name
				System.err.println("No experimental design file specified.");
				exitCode = 3;
			}
		} else {
			// We already have a design tree. Presumably we are run by
			// an applet.
			if (commandLineAssignments != null) {
				exDesign.setCommandLineAssignments(commandLineAssignments);
			}
		}
		if (Base.isApplication() && (exitCode != 0)) {
			System.exit(exitCode);
		}
		// At this point we shold have a valid design tree
		this.exDesign = exDesign;
		if (exDesign != null) {
			if (Base.getExportText()) {
				// That's all we have to do: export the design tree as a text
				// file
				if (Base.isApplication()) {
					String xfn = Base.getExportTextFileName();
					if (StringExt.nonEmpty(xfn)) {
						exDesign.store(xfn);
					} else {
						exDesign.print();
					}
					System.exit(exitCode);
				}
			} else if (Base.getExportJavaClass()) {
				// That's all we have to do: export the design tree as a Java
				// class
				if (Base.isApplication()) {
					convertToJavaSource(exDesign);
					System.exit(exitCode);
				}
			} else if (Base.getExportHTML()) {
				// That's all we have to do: export the design tree as a HTML
				// text file
				if (Base.isApplication()) {
					convertToHTML(exDesign);
					System.exit(exitCode);
				}
			} else if (Base.getProcessDataFile()) {
				// That's all we have to do: process a data file
				if (Base.isApplication()) {
					ExDesignDataProcessor dataProcessor = new DataProcessor(
							null, exDesign);
					exDesign.runDataSession(dataProcessor);
					System.exit(exitCode);
				}
			} else {
				// So here we are ready to run the experiment.
				controlFrame = new ExFrame(exDesign);
				controlFrame.addFocusListener(new DebugFocusListener(
						"ExRun Control Frame"));
				controlFrame.setVisible(true);
				Base.setFrame(controlFrame);
				presentationManager = new PresentationManager(controlFrame,
						this);
				ExDesignDataProcessor dataProcessor = new DataProcessor(
						controlFrame, exDesign);
				exDesign.runExperiment(this, presentationManager, dataProcessor);
			}
		} else {
			// The user has refused to give us a valid design file name
			System.err.println("No experimental design.");
			exitCode = 3;
			if (Base.isApplication()) {
				System.exit(exitCode);
			}
		}
	}

	/**
	 * Open a design file and make the respective design tree the current design
	 * tree. The design file open menu item is handled here since it both
	 * affects the procedure and the runtime control panel.
	 */
	private String getExDesignFile() {
		LoadFileDialog fd = new LoadFileDialog(" Open Design File", "pxd");
		String fn = (fd.getFile() != null) ? fd.getPath() : null;
		fd.dispose();
		return fn;
	}

	// ---------------------------------------------------------------
	// ExDesignThreadStarter implementation when running as an Application
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
		Base.setFrame(null);
		controlFrame.setVisible(false);
		controlFrame.dispose();
		if (Base.isApplication()) {
			if (s)
				System.out.println("Experiment stopped.");
			System.exit(0);
		} else {
			starterApplet.experimentFinished(s);
		}
	}

	public void stopExperiment() {
		exDesign.stop();
	}

	// ---------------------------------------------------------------
	// Design tree conversion to Java class
	// ---------------------------------------------------------------
	/** Export this experimental design tree as a Java source file. */
	private void convertToJavaSource(ExDesign exd) {
		exd.exportJava(null);
	}

	// ---------------------------------------------------------------
	// Design tree conversion to HTML text
	// ---------------------------------------------------------------
	/** Export this experimental design tree as a HTML text file. */
	private void convertToHTML(ExDesign exd) {
		String fn = ExPar.DesignFileName.getString() + ".html";
		File fd = new File(fn);
		// System.out.println("ExRun.convertToHTML(): " + fd.getPath());
		exd.exportHTML(fd.getPath());
	}

	// ---------------------------------------------------------------
	// To run this class as an application
	// ---------------------------------------------------------------
	public static void main(String[] args) {
		// Set the current system's look an feel
		/*
		 * try { javax.swing.UIManager.setLookAndFeel(
		 * javax.swing.UIManager.getSystemLookAndFeelClassName()); } catch
		 * (Exception e) {}
		 */
		/*
		 * MemoryWarningSystem.setPercentageUsageThreshold(0.6);
		 * 
		 * MemoryWarningSystem mws = new MemoryWarningSystem();
		 * mws.addListener(new MemoryWarningSystem.Listener() { public void
		 * memoryUsageLow(long usedMemory, long maxMemory) {
		 * System.out.println("Heap memory is low!"); double percentageUsed =
		 * ((double) usedMemory) / maxMemory;
		 * System.out.println("  Current maximum: " + maxMemory);
		 * System.out.println("  Current usage:   " + usedMemory);
		 * System.out.println
		 * ("Try to use option '-Xmx256m' to increase heap memory.");
		 * MemoryWarningSystem.setPercentageUsageThreshold(0.8); } });
		 */
		new ExRun(args, null);
	}
}
