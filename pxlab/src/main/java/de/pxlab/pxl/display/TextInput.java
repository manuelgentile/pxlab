package de.pxlab.pxl.display;

import java.awt.Font;
import java.awt.event.KeyEvent;

import de.pxlab.pxl.*;

/**
 * A single line of input text used as a response.
 * 
 * @version 0.1.1
 */
/*
 * 
 * 25/11/02
 * 
 * 2007/04/25 removed initialization of StopKey
 */
public class TextInput extends TextDisplay {
	/** Labels for the single dimensions of the input space. */
	public ExPar Label = new ExPar(STRING, new ExParValue("Answer:"),
			"Input text label");

	public TextInput() {
		setTitleAndTopic("Single line text response", TEXT_PAR_DSP);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.STOP_KEY_TIMER"));
		JustInTime.set(1);
		Text.set("");
	}
	private TextInputElement textElement;
	private int inputFocus = 0;

	protected int create() {
		// System.out.println("TextInput.create()");
		textElement = new TextInputElement(Color, "", "");
		int s = enterDisplayElement(textElement, group[0]);
		defaultTiming();
		return s;
	}

	protected void computeGeometry() {
		// System.out.println("TextInput.computeGeometry()");
		textElement.setLabel(Label.getString());
		textElement.setText(Text.getString());
		textElement.setLocation(LocationX.getInt(), LocationY.getInt());
		textElement.setFont(FontFamily.getString(), FontStyle.getInt(),
				FontSize.getInt());
		textElement.setGap(FontSize.getInt());
		textElement.setFocus(true);
	}

	protected void timingGroupFinished(int grp) {
		Text.set(textElement.getText());
	}

	/**
	 * Update the system's state by moving to the next step in time and using
	 * the current input values.
	 */
	private void update() {
		Text.set(textElement.getText());
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
		/*
		 * System.out.println("TextInput.keyResponse(): code= " + keyCode);
		 * System.out.println("                         char = " + (char)keyChar
		 * + " [" + keyChar + "]");
		 * System.out.println("                         location = " +
		 * keyEvent.getKeyLocation());
		 * System.out.println("                         modifiers = " +
		 * keyEvent.getModifiers());
		 * System.out.println("                         param = " +
		 * keyEvent.paramString());
		 */
		boolean validKey = true;
		switch (keyCode) {
		case KeyEvent.VK_LEFT:
			textElement.deleteLastChar();
			break;
		/*
		 * case KeyEvent.VK_UP: inputFocus--; if (inputFocus < 0) inputFocus =
		 * 0; break; case KeyEvent.VK_DOWN: inputFocus++; if (inputFocus >=
		 * dimInput) inputFocus = dimInput-1; break; case KeyEvent.VK_TAB:
		 * inputFocus++; if (inputFocus >= dimInput) inputFocus = 0; break;
		 */
		case KeyEvent.VK_BACK_SPACE:
			textElement.deleteLastChar();
			break;
		default:
			if (keyChar != KeyEvent.CHAR_UNDEFINED) {
				textElement.append(keyChar);
			} else {
				validKey = false;
			}
			break;
		}
		/*
		 * if (validKey) { for (int i = 0; i < dimInput; i++) {
		 * textElement.setFocus(i == inputFocus); } }
		 */
		return true;
	}
}
