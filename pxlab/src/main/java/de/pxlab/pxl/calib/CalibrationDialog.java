package de.pxlab.pxl.calib;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.awtx.*;

/**
 * Shows a modal dialog for color measurement device calibration. This dialog
 * executes a full device calibration cycle. It returns after the user has
 * pressed the 'Continue'-Button. Before calibration actually starts the user
 * has to press the 'Calibrate'-button. This makes it possible to wait until the
 * device has its proper calibration position.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class CalibrationDialog extends Dialog implements ActionListener {
	private int buttonGapSize = 10;
	private int fieldGapSize = 16;
	private int borderSize = 20;
	private Panel rightButtonPanel;
	private Button calibrateButton;
	private Button continueButton;
	private Label message;
	private String startMsg = " Press 'Calibrate' to start calibration. ";
	private String contMsg = " Press 'Continue' to goto on. ";
	private String waitMsg = " Wait until calibration is finished. ";
	private String failedMsg = " Calibration failed. Try again. ";
	private ColorMeasurementDevice colorMeasurementDevice;

	/**
	 * Create a dialog with the given parameters.
	 * 
	 * @param cmd
	 *            the color measurement device which should be calibrated.
	 */
	public CalibrationDialog(ColorMeasurementDevice cmd) {
		super(new Frame(), " Color Measurement Device Calibration", true);
		colorMeasurementDevice = cmd;
		setLayout(new BorderLayout(fieldGapSize, fieldGapSize));
		setLocation(400, 300);
		setResizable(false);
		message = new Label(startMsg);
		message.setFont(new Font("Sans", Font.PLAIN, 24));
		add(message, BorderLayout.CENTER);
		Panel buttonPanelContainer = new Panel(new BorderLayout());
		add(buttonPanelContainer, BorderLayout.SOUTH);
		rightButtonPanel = new Panel(new GridLayout(1, 0, buttonGapSize, 0));
		buttonPanelContainer.add(rightButtonPanel, BorderLayout.EAST);
		calibrateButton = new Button(" Calibrate ");
		calibrateButton.addActionListener(this);
		rightButtonPanel.add(calibrateButton);
		continueButton = new Button(" Continue ");
		continueButton.addActionListener(this);
		rightButtonPanel.add(continueButton);
		pack();
		show();
	}

	public void actionPerformed(ActionEvent e) {
		Button b = (Button) (e.getSource());
		if (b == continueButton) {
			dispose();
		} else if (b == calibrateButton) {
			continueButton.setEnabled(false);
			calibrateButton.setEnabled(false);
			message.setText(waitMsg);
			if (colorMeasurementDevice.calibrate()) {
				message.setText(contMsg);
				continueButton.requestFocus();
			} else {
				message.setText(failedMsg);
				calibrateButton.requestFocus();
			}
			continueButton.setEnabled(true);
			calibrateButton.setEnabled(true);
			repaint();
		}
	}

	public Insets getInsets() {
		Insets i = super.getInsets();
		return (new Insets(i.top + borderSize, i.left + borderSize, i.bottom
				+ borderSize, i.right + borderSize));
	}
}
