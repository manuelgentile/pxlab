package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Controls some error series counting parameters. Whenever this class's
 * computeGeometry() method is called, then the following happens:
 * 
 * <ul>
 * <li>the current value of ExPar.ResponseErrorCount is added to the parameter
 * ErrorTotal;
 * 
 * <li>if the current value of ExPar.ResponseErrorCount is zero then the
 * parameters ErrorFreeTotal and ErrorFreeSeries are both incremented by 1;
 * 
 * <li>if the current value of ExPar.ResponseErrorCount is nonzero then the
 * parameter ErrorFreeSeries is set to 0.
 * 
 * </ul>
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class ErrorControl extends Display {
	/**
	 * Counts the total sum of ExPar.ResponseErrorCount for every call of this
	 * display's computeGeometry() method.
	 */
	public ExPar ErrorTotal = new ExPar(RTDATA, new ExParValue(0),
			"Total Number of Errors");
	/**
	 * Counts the total number of times when ExPar.ResponseErrorCount was zero
	 * when this display's computeGeometry() method was called.
	 */
	public ExPar ErrorFreeTotal = new ExPar(RTDATA, new ExParValue(0),
			"Total Number of Error Free Calls");
	/**
	 * Counts the total number of times when ExPar.ResponseErrorCount was zero
	 * in a consecutive series when this display's computeGeometry() method was
	 * called.
	 */
	public ExPar ErrorFreeSeries = new ExPar(RTDATA, new ExParValue(0),
			"Number of Error Free Calls in Sequence");

	public ErrorControl() {
		setTitleAndTopic("Error Control", PARAM_DSP | EXP);
		setVisible(false);
		JustInTime.set(1);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	public boolean isGraphic() {
		return false;
	}

	protected int create() {
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
		int e = ExPar.ResponseErrorCount.getInt();
		ErrorTotal.set(ErrorTotal.getInt() + e);
		if (e == 0) {
			ErrorFreeSeries.set(ErrorFreeSeries.getInt() + 1);
			ErrorFreeTotal.set(ErrorFreeTotal.getInt() + 1);
		} else {
			ErrorFreeSeries.set(0);
		}
		if (Debug.isActive(Debug.DSP_PROPS)) {
			System.out.println("ErrorControl() ResponseErrorCount = " + e);
			System.out.println("ErrorControl() ErrorTotal = "
					+ ErrorTotal.getInt());
			System.out.println("ErrorControl() ErrorFreeSeries = "
					+ ErrorFreeSeries.getInt());
			System.out.println("ErrorControl() ErrorFreeTotal = "
					+ ErrorFreeTotal.getInt());
		}
	}
}
