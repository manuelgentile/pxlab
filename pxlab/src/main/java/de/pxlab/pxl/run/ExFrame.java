package de.pxlab.pxl.run;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.pxl.*;
import de.pxlab.pxl.gui.*;

/**
 * A starter frame for experiments which serves as an owner for display windows
 * and as a dummy focus owner for the Java system's keyboard focus manager. It
 * also provides a stop button to stop a running experiment.
 * 
 * @version 0.1.1
 */
/*
 * 
 * 2004/11/09
 */
public class ExFrame extends Frame implements ActionListener {
	private Button stopButton;
	private ExDesign exDesign;

	/**
	 * Create a starter frame for running the given experimental design.
	 * 
	 * @param exd
	 *            the experiemntal design tree which will be running under
	 *            control of this frame. It is needed here since the frame can
	 *            send stop signals to the experimental design.
	 */
	public ExFrame(ExDesign exd) {
		super(" " + Base.getPXLabCopyright());
		exDesign = exd;
		PXLabIcon.decorate(this);
		stopButton = new Button(" STOP EXPERIMENT ");
		stopButton.setFocusable(false);
		stopButton.addFocusListener(new DebugFocusListener(
				"ExFrame Stop Button"));
		stopButton.addActionListener(this);
		stopButton.setFont(new Font("Sans", Font.BOLD, 32));
		add(stopButton);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				stopExperiment();
			}
		});
		pack();
	}

	public void actionPerformed(ActionEvent e) {
		Debug.show(Debug.EVENTS,
				"ExFrame.actionPerformed(): Stop button pressed.");
		stopExperiment();
	}

	private void stopExperiment() {
		exDesign.stop();
	}
}
