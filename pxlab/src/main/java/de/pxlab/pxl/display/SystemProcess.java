package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Execute an external system process. If this object has a timer of type
 * de.pxlab.pxl.TimerCodes.END_OF_MEDIA_TIMER then the experiment waits until
 * the external process has been finished. If not then the external process is
 * executed concurrently.
 * 
 * <p>
 * Note that in a multitasking environment external system commands will not
 * directly be executed by the thread which is spawned by this Display object.
 * This means that in most cases the external process will return immediately
 * even if the command started by the thread has not yet been finished. And
 * there seems no way to synchronize to the end of the command started by the
 * external process.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/09/21
 */
public class SystemProcess extends Display {
	/** The system command to be executed. */
	public ExPar Command = new ExPar(STRING, new ExParValue("dir"),
			"Command to execute");
	/** An array of arguments to the command. */
	public ExPar Arguments = new ExPar(STRING, new ExParValue("/b"),
			"Arguments for the command");
	/** The working directory for the system command. */
	public ExPar WorkingDirectory = new ExPar(STRING, new ExParValue("/"),
			"Working directory for the command");
	/**
	 * Returns the exit value of the system command. This parameter is only
	 * valid if the experiment waits until the external process is finished.
	 */
	public ExPar ExitValue = new ExPar(RTDATA, new ExParValue(0),
			"Exit value of the process");

	public SystemProcess() {
		setTitleAndTopic("Execute System Process", CONTROL_DSP | EXP);
		JustInTime.set(1);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.END_OF_MEDIA_TIMER"));
	}

	public boolean getCanPreload() {
		return false;
	}

	public boolean isGraphic() {
		return false;
	}
	SystemProcessElement process;

	protected int create() {
		removeDisplayElements(backgroundFieldIndex);
		backgroundFieldIndex = -1;
		process = new SystemProcessElement();
		enterDisplayElement(process, group[0]);
		defaultTiming(0);
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
		// System.out.println("SystemProcess.computeGeometry()");
		process.setProperties(Command.getStringArray(),
				Arguments.getStringArray(), WorkingDirectory.getString(),
				((Timer.getInt() & TimerBitCodes.END_OF_MEDIA_TIMER_BIT) != 0),
				(MediaEventListener) (presentationManager.getResponseManager()));
	}

	protected void timingGroupFinished(int group) {
		ExitValue.set(process.getExitValue());
	}
}
