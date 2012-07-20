package de.pxlab.pxl;

/**
 * Codes for defining trial, block, session, and procedure states and return
 * codes.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public interface StateCodes {
	/**
	 * Tell the runtime processor to go on executing procedure units.
	 */
	public static final int EXECUTE = 0;
	/**
	 * Stop execution of the parent list of procedure units after the current
	 * procedure unit has been finished. Do not stop the whole experimental run.
	 * As an example: if any Display object in a Block or Trial sets
	 * SessionState to BREAK then the currently running Block is finished and
	 * execution of the Block list for the current Session is stopped after this
	 * Block has been finished. This means that all Trials in the current Block
	 * still are run. If BlockState is set to BREAK then the currently running
	 * Trial is executed and then the current Block is stopped and the next
	 * Block is executed.
	 */
	public static final int BREAK = 1;
	/**
	 * Stop the experimental run after the current trial has been finished. If
	 * any of ProcedureState, SessionState, BlockState, or TrialState is set to
	 * STOP then the whole experimental run is stopped after the currently
	 * running Trial has been finished. This is similar to pressing the STOP
	 * button of the control frame. The difference here is that the currently
	 * running trial will be finished before stopping. Note that stopping also
	 * means that the BlockEnd, SessionEnd, and ProcedureEnd display lists are
	 * not executed.
	 */
	public static final int STOP = 2;
	/**
	 * Tell the runtime processor that the most recent trial generated a subject
	 * error response. If the global experimental parameter RepeatErrorTrials is
	 * 1 then the respective trial should be repeated. This means that the trial
	 * in its original state (before data collection started) is copied and
	 * inserted into the sequence of trials ahead. If trials are randomized then
	 * insertion should be done at a random position. If the global experimental
	 * parameter RepeatErrorTrials is 0 then the trial is treated as if it had
	 * EXECUTE as its state.
	 */
	public static final int ERROR = 3;
	/**
	 * Tell the runtime processor that the most recent trial should be repeated.
	 * This means that the trial in its original state (before data collection
	 * started) is copied and inserted into the sequence of trials ahead. If
	 * trials are randomized then insertion should be done at a random position.
	 */
	public static final int REPEAT = 4;
	/**
	 * Tell the runtime processor that the most recent trial should be copied in
	 * the state after data collection and the copy should be inserted into the
	 * sequence of trials ahead. If trials are randomized then insertion should
	 * be done at a random position.
	 */
	public static final int COPY = 5;
}
