package de.pxlab.pxl.display;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.Locale;

import de.pxlab.pxl.*;

/**
 * This is a base class for all kinds of linear system problems. This display
 * uses an object of class <code>LinearSystemModel</code> to describe the linear
 * system.
 * 
 * @version 0.1.2
 * @see de.pxlab.pxl.LinearSystemModel
 */
/*
 * 
 * 07/11/01
 * 
 * 2007/04/25 removed initialization of StopKey
 */
public class LinearSystem extends FontDisplay {
	private static final double lowerLimit = -100.0;
	private static final double upperLimit = 100.0;
	/** Input data array. */
	public ExPar Input = new ExPar(lowerLimit, upperLimit, new ExParValue(0.0),
			"Input vector");
	/** State array holding the system's current state. */
	public ExPar State = new ExPar(lowerLimit, upperLimit, new ExParValue(0.0),
			"State vector");
	/** Output data array. */
	public ExPar Output = new ExPar(RTDATA, new ExParValue(0.0),
			"Output vector");
	/**
	 * The matrix which maps input data and states into the set of states.
	 */
	public ExPar InputMatrix = new ExPar(lowerLimit, upperLimit,
			new ExParValue(1.0), "Input to state matrix");
	/**
	 * The state transition matrix which maps the state at time t into the state
	 * at time t+1.
	 */
	public ExPar StateMatrix = new ExPar(lowerLimit, upperLimit,
			new ExParValue(1.0), "State transition matrix");
	/** The matrix which maps the current state into the output space. */
	public ExPar OutputMatrix = new ExPar(RTDATA, new ExParValue(1.0),
			"State to output matrix");
	/** Screen heading text. */
	public ExPar Heading = new ExPar(STRING, new ExParValue("Linear System"),
			"Screen heading");
	/** Labels for the single dimensions of the input space. */
	public ExPar InputLabels = new ExPar(STRING, new ExParValue("Input 1"),
			"Input value labels");
	/** Labels for the single dimensions of the output space. */
	public ExPar OutputLabels = new ExPar(STRING, new ExParValue("Output 1"),
			"Output value labels");
	/** Input text color. */
	public ExPar InputColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)), "Input color");
	/** Heading text color. */
	public ExPar HeadingColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)), "Heading color");

	public LinearSystem() {
		setTitleAndTopic("Linear System", PROBLEM_SOLVING_DSP);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.STOP_KEY_TIMER"));
		JustInTime.set(1);
	}
	private LinearSystemModel model;
	private int dimState;
	private int dimOutput;
	private int dimInput;
	private TextInputElement[] textElement;
	private int inputFocus;
	private int firstDisplayElement;
	private int lastDisplayElement = 0;
	private int heading;

	protected int create() {
		model = new LinearSystemModel();
		heading = enterDisplayElement(new TextParagraphElement(HeadingColor),
				group[0]);
		firstDisplayElement = nextDisplayElementIndex();
		defaultTiming();
		return firstDisplayElement;
	}

	protected void computeGeometry() {
		// First figure out the dimensions of the various vectors
		dimState = State.getValue().length;
		dimInput = InputMatrix.getValue().length / dimState;
		dimOutput = OutputMatrix.getValue().length / dimState;
		// Compute output data
		double[] output = model.output(State.getDoubleArray(),
				OutputMatrix.getDoubleArray());
		Output.set(output);
		// System.out.println("Output = " + Output);
		// Clear the display element list
		removeDisplayElements(firstDisplayElement, lastDisplayElement);
		// Create the parameter labels
		String[] outLabel = labels("Output", OutputLabels, dimOutput);
		String[] inLabel = labels("Input", InputLabels, dimInput);
		// Prepare geometric properties of the display
		int fs = FontSize.getInt();
		int lineSkip = 3 * fs / 2;
		int gap = lineSkip / 2;
		int x = 0;
		int y = -((dimInput + dimOutput - 1) * lineSkip) / 2;
		Font font = new Font(FontFamily.getString(), FontStyle.getInt(), fs);
		NumberFormat doubleExParValue = NumberFormat.getInstance(Locale.US);
		doubleExParValue.setMaximumFractionDigits(2);
		doubleExParValue.setGroupingUsed(false);
		((TextParagraphElement) getDisplayElement(heading)).setProperties(
				Heading.getStringArray(), "SansSerif", Font.BOLD, fs, 0, y - 3
						* lineSkip / 2, (100 * width) / 80,
				PositionReferenceCodes.BASE_CENTER, AlignmentCodes.CENTER,
				true, true, 1.0);
		// Add the output parameters to the display
		for (int i = 0; i < dimOutput; i++) {
			TextInputElement tp = new TextInputElement(Color, outLabel[i],
					doubleExParValue.format(output[i]));
			tp.setLocation(x, y);
			tp.setFont(font);
			tp.setGap(gap);
			y += lineSkip;
			enterDisplayElement(tp, group[0]);
		}
		// Add the input parameters
		textElement = new TextInputElement[dimInput];
		for (int i = 0; i < dimInput; i++) {
			textElement[i] = new TextInputElement(InputColor, inLabel[i], "");
			textElement[i].setLocation(x, y);
			textElement[i].setFont(font);
			textElement[i].setGap(gap);
			textElement[i].setFocus(i == inputFocus);
			y += lineSkip;
			enterDisplayElement(textElement[i], group[0]);
		}
		lastDisplayElement = nextDisplayElementIndex() - 1;
		// Always set the focus to the first input parameter
		inputFocus = 0;
	}

	/**
	 * Create the default labels for a set of parameters.
	 * 
	 * @param s
	 *            the default name prefix.
	 * @param p
	 *            the experimental parameter which holds the labels.
	 * @param n
	 *            the number of labels needed.
	 * @return a String array of size n which holds default parameter labels.
	 */
	private String[] labels(String s, ExPar p, int n) {
		String[] lb = new String[n];
		String[] pl = p.getStringArray();
		for (int i = 0; i < n; i++) {
			lb[i] = (i < pl.length) ? pl[i] : (s + " " + String.valueOf(i + 1));
		}
		return lb;
	}

	protected void timingGroupFinished(int grp) {
		update();
	}

	/**
	 * Update the system's state by moving to the next step in time and using
	 * the current input values.
	 */
	private void update() {
		double[] u = new double[dimInput];
		for (int i = 0; i < dimInput; i++) {
			u[i] = textElement[i].getDoubleValue();
		}
		Input.set(u);
		State.set(model.nextState(State.getDoubleArray(),
				StateMatrix.getDoubleArray(), u, InputMatrix.getDoubleArray()));
		// System.out.println("Next State = " + State);
		computeGeometry();
	}

	/**
	 * Execute the key command typed by the subject. This method is called by
	 * the response event manager whenever a key has been pressed and the key is
	 * not the stop key.
	 */
	protected boolean keyResponse(KeyEvent keyEvent) {
		int keyCode = keyEvent.getKeyCode();
		int keyChar = keyEvent.getKeyChar();
		boolean validKey = true;
		switch (keyCode) {
		case KeyEvent.VK_LEFT:
			textElement[inputFocus].deleteLastChar();
			break;
		case KeyEvent.VK_UP:
			inputFocus--;
			if (inputFocus < 0)
				inputFocus = 0;
			break;
		case KeyEvent.VK_DOWN:
			inputFocus++;
			if (inputFocus >= dimInput)
				inputFocus = dimInput - 1;
			break;
		case KeyEvent.VK_TAB:
			inputFocus++;
			if (inputFocus >= dimInput)
				inputFocus = 0;
			break;
		case KeyEvent.VK_BACK_SPACE:
			textElement[inputFocus].deleteLastChar();
			break;
		default:
			if (keyChar != KeyEvent.CHAR_UNDEFINED) {
				textElement[inputFocus].append(keyChar);
			} else {
				validKey = false;
			}
			break;
		}
		if (validKey) {
			for (int i = 0; i < dimInput; i++) {
				textElement[i].setFocus(i == inputFocus);
			}
		}
		// System.out.println("LinearSystem.keyResponse(): code= " + keyCode +
		// " char = " + (char)keyChar);
		return true;
	}
}
