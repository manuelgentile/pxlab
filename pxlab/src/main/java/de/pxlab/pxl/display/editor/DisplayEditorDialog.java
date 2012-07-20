package de.pxlab.pxl.display.editor;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.pxl.*;
import de.pxlab.pxl.display.PXLabLogo;

/**
 * A Dialog which holds an editor for a single experimental display object. This
 * is used by the ExDesignEditorPanel for editing a single Display object.
 * 
 * @author H. Irtel
 * @version 0.1.2
 * @see de.pxlab.pxl.design.ExDesignEditorPanel
 * 
 * 
 *      07/17/02 Methods and Buttons to reset and save the changes made in the
 *      editor dialog. (er)
 */
public class DisplayEditorDialog extends Dialog implements
		DisplayEditorPanelController, ActionListener {
	private DisplayEditorPanel displayEditorPanel;
	private Frame parent;
	/** Copy of the original display parameters. */
	public ExParDescriptor[] originalExParFields;
	// Add the Dialog Buttons
	private Button okButton;
	private Button cancelButton;
	private Button applyButton;
	private int borderWidth = 10;

	public DisplayEditorDialog(Frame prnt, Display dsp) {
		super(prnt, "  Edit " + dsp.getInstanceName(), true);
		parent = prnt;
		// Sorry, but Dialog objects do not allow icons
		// PXLabIcon.decorate(this);
		displayEditorPanel = new DisplayEditorPanel(this);
		add(displayEditorPanel);
		displayEditorPanel.setDisplay(dsp);
		// get the Display's ExParDescriptors
		ExParDescriptor[] xpd = dsp.getExParFields();
		originalExParFields = new ExParDescriptor[xpd.length];
		setOriginalExParFields(xpd);
		cancelButton = new Button(" Cancel ");
		cancelButton.addActionListener(this);
		okButton = new Button(" OK ");
		okButton.addActionListener(this);
		applyButton = new Button(" Apply ");
		applyButton.addActionListener(this);
		Panel buttonGridPanel = new Panel(new GridLayout(1, 0, borderWidth,
				borderWidth));
		buttonGridPanel.add(okButton);
		buttonGridPanel.add(cancelButton);
		buttonGridPanel.add(applyButton);
		Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(buttonGridPanel);
		add(buttonPanel, BorderLayout.SOUTH);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				setVisible(false);
				dispose();
			}
		});
		// setVisible(true);
		pack();
	}

	/** Button actions. */
	public void actionPerformed(ActionEvent e) {
		Button src = (Button) e.getSource();
		if (src == cancelButton) {
			ExParDescriptor[] xd = displayEditorPanel.getDisplay()
					.getExParFields();
			int l = xd.length;
			for (int i = 0; i < l; i++) {
				ExPar xp = originalExParFields[i].getValue();
				ExParValue xpv = new ExParValueUndefined();
				if (xp.getValue() != null) {
					try {
						xpv = new ExParValue(xp.getValue().getStringArray());
					} catch (NullPointerException npe) {
					}
				}
				xd[i].getValue().getValue().set(xpv);
			}
			dispose();
		} else if (src == okButton) {
			dispose();
		} else if (src == applyButton) {
			ExParDescriptor[] xpd = displayEditorPanel.getDisplay()
					.getExParFields();
			setOriginalExParFields(xpd);
		}
	}

	// Set the originalExParFields to the values of ExParDescriptor[]
	// before any changes have taken place
	private void setOriginalExParFields(ExParDescriptor[] xpd) {
		int l = xpd.length;
		for (int i = 0; i < l; i++) {
			if (originalExParFields[i] == null) {
				String n = xpd[i].getName();
				ExPar xp = xpd[i].getValue();
				ExParValue xpv = new ExParValueUndefined();
				if (xp.getValue() != null) {
					try {
						xpv = new ExParValue(xp.getValue().getStringArray());
					} catch (NullPointerException npe) {
					}
				}
				originalExParFields[i] = new ExParDescriptor(n, new ExPar(
						xp.getType(), xpv, xp.getHint()));
			} else {
				originalExParFields[i].setName(xpd[i].getName());
				ExPar xp = xpd[i].getValue();
				ExParValue xpv = new ExParValueUndefined();
				if (xp.getValue() != null) {
					try {
						xpv = new ExParValue(xp.getValue().getStringArray());
					} catch (NullPointerException npe) {
					}
				}
				originalExParFields[i].getValue().getValue().set(xpv);
			}
		}
	}

	// ---------------------------------------------------------------
	// DisplayEditorPanelController implementation
	// ---------------------------------------------------------------
	public Frame getFrame() {
		return (parent);
	}

	public MenuBar getMenuBar() {
		return null;
	}

	public void setMenuBarEnabled(boolean state) {
	}
}
