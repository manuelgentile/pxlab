package de.pxlab.pxl;

import java.util.ArrayList;

/**
 * The ExDesignProcessor class controls the run time behavior of a single
 * experimental session. Its main task is to execute the start and end methods
 * of the single nodes of the current experimental design tree.
 * 
 * @author H. Irtel
 * @version 0.4.1
 * @see ExDesign
 * @see ExDesignNode
 */
public interface ExDesignProcessor {
	/**
	 * The return value of display list processing methods which run as
	 * expeceted.
	 */
	public static final int DSPL_OK = 0;
	/**
	 * The return value of display list processing methods which have got a
	 * signal to stop processing.
	 */
	public static final int DSPL_STOPPED = 1;
	/**
	 * The return value of display list processing methods which have got a
	 * signal to stop list processing which originates from display list control
	 * object.
	 */
	public static final int DSPL_CTRL = 2;

	/**
	 * Stop processing the experimental design file as soon as possible. This
	 * method may be called asynchronously from a thread which is different from
	 * the design processing thread. Usually it will be the event queue which
	 * calls this method.
	 */
	public void stop();

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
	public int startProcedure(ExDesignNode p, ArrayList d);

	/**
	 * This method is called when the active session has been run and the
	 * experiment is about to be finished.
	 * 
	 * @param p
	 *            the ExDesignNode for this experiment node.
	 * @return a code indicating how the end of the experiment has been
	 *         evaluated.
	 */
	public int endProcedure(ExDesignNode p, ArrayList d);

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
	public int startSession(ExDesignNode p, ArrayList d);

	/**
	 * This method ends the active session of this experiment.
	 * 
	 * @param p
	 *            the ExDesignNode for this session.
	 * @param d
	 *            the Display object list for the end of a session.
	 * @return a code indicating how the session end has been evaluated.
	 */
	public int endSession(ExDesignNode p, ArrayList d);

	/**
	 * This method is called whenever a new block is started.
	 * 
	 * @param p
	 *            the ExDesignNode for this block.
	 * @param d
	 *            the Display object list for the start of a block.
	 * @return a code indicating how the block start has been evaluated.
	 */
	public int startBlock(ExDesignNode p, ArrayList d);

	/**
	 * This method is called when a block has been finished.
	 * 
	 * @param p
	 *            the ExDesignNode for this block.
	 * @param d
	 *            the Display object list for the end of a block.
	 * @return a code indicating how the block end has been evaluated.
	 */
	public int endBlock(ExDesignNode p, ArrayList d);

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
	public int runTrial(ExDesignNode p, ArrayList d);
}
