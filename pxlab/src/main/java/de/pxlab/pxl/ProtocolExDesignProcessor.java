package de.pxlab.pxl;

import java.util.ArrayList;

/**
 * The ProtocolExDesignProcessor class simply prints a protocol of the
 * experimental nodes.
 * 
 * @author H. Irtel
 * @version 0.1.1
 * @see ExDesign
 * @see ExDesignNode
 */
public class ProtocolExDesignProcessor implements ExDesignProcessor {
	private boolean doPrint = true;
	/**
	 * If this flag becomes true then the stimulus presentation is stopped as
	 * soon as possible.
	 */
	private boolean stopPresentation = false;

	/** Create a protocolling experimental design processor. */
	public ProtocolExDesignProcessor() {
	}

	/**
	 * Create a protocolling experimental design processor.
	 * 
	 * @param s
	 *            if true then the protocol is printed if false then nothing is
	 *            done.
	 */
	public ProtocolExDesignProcessor(boolean s) {
		doPrint = s;
	}

	/** Stop the currently running presentation. */
	public void stop() {
		stopPresentation = true;
	}

	/**
	 * This method is called immediately after the design tree has been created
	 * even before the run time tree exists. Methods which rely on global
	 * parameter assignments should not be called from here but from
	 * startSession().
	 * 
	 * @param p
	 *            the ExDesignNode for this experiment node.
	 * @return a code indicating how the start of the experiment has been
	 *         evaluated.
	 */
	public int startProcedure(ExDesignNode p, ArrayList d) {
		int returnCode = 0;
		if (doPrint && !stopPresentation)
			System.out.println("Start " + p.toString());
		return (returnCode);
	}

	/**
	 * This method is called when the active session has been run and the
	 * experiment is about to be finished.
	 * 
	 * @param p
	 *            the ExDesignNode for this experiment node.
	 * @return a code indicating how the end of the experiment has been
	 *         evaluated.
	 */
	public int endProcedure(ExDesignNode p, ArrayList d) {
		int returnCode = 0;
		if (doPrint && !stopPresentation)
			System.out.println("End " + p.toString());
		return (returnCode);
	}

	/**
	 * This method starts the active session of the experiment. It is called
	 * after the run time tree has been created which means that all global
	 * parameter assignments have been executed.
	 * 
	 * @param p
	 *            the ExDesignNode for this session.
	 * @param d
	 *            the Display object list for the start of a session.
	 * @return a code indicating how the session start has been evaluated.
	 */
	public int startSession(ExDesignNode p, ArrayList d) {
		int returnCode = 0;
		if (doPrint && !stopPresentation)
			System.out.println("  Start " + p.toString());
		return (returnCode);
	}

	/**
	 * This method ends the active session of this experiment.
	 * 
	 * @param p
	 *            the ExDesignNode for this session.
	 * @param d
	 *            the Display object list for the end of a session.
	 * @return a code indicating how the session end has been evaluated.
	 */
	public int endSession(ExDesignNode p, ArrayList d) {
		int returnCode = 0;
		if (doPrint && !stopPresentation)
			System.out.println("  End " + p.toString());
		return (returnCode);
	}

	/**
	 * This method is called whenever a new block is started.
	 * 
	 * @param p
	 *            the ExDesignNode for this block.
	 * @param d
	 *            the Display object list for the start of a block.
	 * @return a code indicating how the block start has been evaluated.
	 */
	public int startBlock(ExDesignNode p, ArrayList d) {
		int returnCode = 0;
		if (doPrint && !stopPresentation)
			System.out.println("    Start " + p.toString());
		return (returnCode);
	}

	/**
	 * This method is called when a block has been finished.
	 * 
	 * @param p
	 *            the ExDesignNode for this block.
	 * @param d
	 *            the Display object list for the end of a block.
	 * @return a code indicating how the block end has been evaluated.
	 */
	public int endBlock(ExDesignNode p, ArrayList d) {
		int returnCode = 0;
		if (doPrint && !stopPresentation)
			System.out.println("    End " + p.toString());
		return (returnCode);
	}

	/**
	 * This method is called to run a single trial. Note that the trial
	 * arguments have been pushed immediately before this method is called. The
	 * caller of this method will check for adaptive fixups and also will write
	 * the trial data to the data file. It then will pop the trial arguments
	 * back. This method should return a flag to indicate whether this trial
	 * should be repeated.
	 * 
	 * @param p
	 *            the ExDesignNode for this trial.
	 * @param d
	 *            the Display object list for a trial.
	 * @return a code indicating how the trial has been evaluated.
	 */
	public int runTrial(ExDesignNode p, ArrayList d) {
		int returnCode = 0;
		if (doPrint && !stopPresentation)
			System.out.println("      " + p.toString());
		if (doPrint && !stopPresentation)
			System.out
					.println("      evaluates to " + p.toCurrentValueString());
		return (returnCode);
	}
}
