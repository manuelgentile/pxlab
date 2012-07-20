package de.pxlab.pxl.data;

import de.pxlab.pxl.*;
import de.pxlab.stat.*;
import de.pxlab.util.*;

/**
 * Compute monitor gamma parameters from visual matches. This object expects two
 * data columns as input: The first column is that parameter which is named by
 * parameter ReferencePar of class VisualGammaTarget and the second column is
 * that parameter which is named by parameter AdjustedPar of class
 * VisualGammaTarget. Here is an example design file for using this estimation
 * tool:
 * 
 * <pre>
 * Experiment()
 * {
 *   Context()
 *   {
 *     AssignmentGroup()
 *     {
 *       ExperimentTitle = "Visual Gamma Adjustment [2005/11/29]";
 *       AdjustableStep = 1;
 *       AdjustmentUpKey = de.pxlab.pxl.KeyCodes.RIGHT_KEY;
 *       AdjustmentDownKey = de.pxlab.pxl.KeyCodes.LEFT_KEY;
 *       RandomizeTrials = 0;
 *       new Adjusted = 255;
 *       new Reference = 255;
 *       DataFileTrialFormat = "%Trial.VisualGammaTarget.Channel% %Reference% %Adjusted%";
 *       SubjectCode = "pxlab";
 *     }
 *     Session()
 *     {
 *       Instruction()
 *       {
 *         Text = "Start";
 *       }
 *     }
 *     SessionEnd()
 *     {
 *       SessionEndMessage();
 *     }
 * 
 *     Block(Trial.VisualGammaTarget.Channel, Adjusted) {}
 * 
 *     BlockData(Trial.VisualGammaTarget.Channel, Reference, Adjusted) {
 *       VisualGammaEstimation(Reference, Adjusted) {
 * 	ResultFormat = "Estimates for channel %Trial.VisualGammaTarget.Channel%:\n  gamma = %BlockData.VisualGammaEstimation.Gamma%\n   gain = %BlockData.VisualGammaEstimation.Gain%\n";
 *       }  
 *     }
 * 
 *     Trial(VisualGammaTarget.HighDeviceValue, VisualGammaTarget.LowDeviceValue, 
 * 	VisualGammaTarget.HighColor, VisualGammaTarget.LowColor)
 *     {
 *       VisualGammaTarget()
 *       {
 *         adjustable LowDeviceValue = 128;
 *         Timer = de.pxlab.pxl.TimerCodes.RESPONSE_TIMER;
 * 	AdjustedPar = "Adjusted";
 * 	ReferencePar = "Reference";
 *         NumberOfCycles = 2;
 *       }
 *       ClearScreen()
 *       {
 *         Timer = de.pxlab.pxl.TimerCodes.CLOCK_TIMER;
 *         Duration = 300;
 *       }
 *     }
 *   }
 *   Procedure()
 *   {
 *     Session()
 *     {
 *       Block(de.pxlab.pxl.CalibrationChannelBitCodes.RED_CHANNEL, 255) {
 *         Trial(Adjusted, 188, ?, ?);
 *         Trial(Adjusted, 140, ?, ?);
 *         Trial(Adjusted, 103, ?, ?);
 *         Trial(Adjusted,  76, ?, ?);
 *         Trial(Adjusted,  58, ?, ?);
 *       }
 *       Block(de.pxlab.pxl.CalibrationChannelBitCodes.GREEN_CHANNEL, 255) {
 *         Trial(Adjusted, 191, ?, ?);
 *         Trial(Adjusted, 145, ?, ?);
 *         Trial(Adjusted, 111, ?, ?);
 *         Trial(Adjusted,  86, ?, ?);
 *         Trial(Adjusted,  67, ?, ?);
 *       }
 *       Block(de.pxlab.pxl.CalibrationChannelBitCodes.BLUE_CHANNEL, 255) {
 *         Trial(Adjusted, 188, ?, ?);
 *         Trial(Adjusted, 139, ?, ?);
 *         Trial(Adjusted, 103, ?, ?);
 *         Trial(Adjusted,  76, ?, ?);
 *         Trial(Adjusted,  55, ?, ?);
 *       }
 *     }
 *   }
 * }
 * </pre>
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see de.pxlab.pxl.display.VisualGammaTarget
 */
/*
 * 
 * 2005/12/08
 */
public class VisualGammaEstimation extends DataDisplay {
	/** Holds the result for the gamma value. */
	public ExPar Gamma = new ExPar(RTDATA, new ExParValue(0.0),
			"Estimated gamma");
	/** Holds the result for the gain value. */
	public ExPar Gain = new ExPar(RTDATA, new ExParValue(0.0), "Estimated gain");
	/**
	 * A format string for showing the results of data processing if parameter
	 * HTMLFormat is not set. By default this string is empty and the data
	 * processing objects generate HTML-formatted output. If this string is
	 * non-empty then it is used for formatting the output.
	 */
	public ExPar ResultFormat = new ExPar(STRING, new ExParValue(""),
			"Result format");

	public VisualGammaEstimation() {
		setTitleAndTopic("Gamma estimation from visual matches", Topics.DATA);
	}

	protected int processDataTable(ExDesign exDesign, String[] p,
			ExParValueTable table) {
		if (p.length >= 2) {
			double[] x = table.doubleColumn(p[0]);
			double[] y = table.doubleColumn(p[1]);
			if (x.length == y.length) {
				VisualGammaEstimator est = new VisualGammaEstimator();
				double[] par = { 2.2, 1.0 };
				double q = est.estimate(x, y, par);
				Gamma.set(par[0]);
				Gain.set(par[1]);
			} else {
				System.out
						.println("VisualGammaEstimation: Parameter error in line "
								+ getExDesignNode().getTokenLine()
								+ ". Number of entries int value arrays don't match.");
			}
		} else {
			System.out
					.println("VisualGammaEstimation: Parameter error in line "
							+ getExDesignNode().getTokenLine()
							+ ". This object expects at least two parameters.");
		}
		String f = ResultFormat.getString();
		if (StringExt.nonEmpty(f)) {
		} else {
			StringBuffer b = new StringBuffer(200);
			String nl = System.getProperty("line.separator");
			b.append(nl + "<font face=\"Arial,Helvetica\">");
			if (p.length >= 3) {
				b.append(nl + "<h3>Gamma Estimates for Channel "
						+ table.entry(0, p[2]).getString() + "</h3>");
			} else {
				b.append(nl + "<h3>Gamma Estimates</h3>");
			}
			b.append(nl + "<table cellpadding=\"4\">");
			b.append(nl + "<tr><td align=\"right\">gamma</td><td>=</td><td>"
					+ Gamma.getDouble() + "</td></tr>");
			b.append(nl + "<tr><td align=\"right\">gain</td><td>=</td><td>"
					+ Gain.getDouble() + "</td></tr>");
			b.append(nl + "</table>");
			b.append(nl + "</font>" + nl);
			f = b.toString();
		}
		showResults(f);
		return 0;
	}
}
