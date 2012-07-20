package de.pxlab.pxl;

import java.awt.Frame;
import java.awt.Panel;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.util.ArrayList;
import de.pxlab.awtx.OKButtonDialog;

/**
 * A dialog for entering runtime parameter values. This dialog pops up
 * immediately before an experimental session is started if the design file
 * requires runtime parameters.
 * 
 * @author H. Irtel
 * @version 0.2.1
 */
public class RuntimeParsDialog extends OKButtonDialog {
	private SingleParEntryPanel[] speps;
	private ArrayList parNames;
	private boolean oneOnly = false;

	/**
	 * Create a runtime parameter entry dialog. The dialog is closed by pressing
	 * the close button. If there is only a single parameter to be entered then
	 * the return key or the selection of one choice value closes the dialog.
	 * 
	 * @param parent
	 *            the parent frame of this dialog.
	 * @param pn
	 *            an array of experimental parameter names. These are the names
	 *            of the experimental parameters whose values are to be set with
	 *            this dialog.
	 * @param pv
	 *            an array of string arrays. Every string array contains a list
	 *            of possible values for each parameter in the array pn. If a
	 *            single entry of this array is null then the respective
	 *            parameter can get any arbitray value. If an entry is non-null
	 *            then the user has to select one of the entries in the list as
	 *            the parameter's value.
	 */
	public RuntimeParsDialog(Frame parent, ArrayList pn, ArrayList pv) {
		super(parent, " Enter Runtime Parameter Values");
		parNames = pn;
		Panel pep = new Panel(new GridLayout(0, 1, 0, 10));
		speps = new SingleParEntryPanel[pn.size()];
		for (int i = 0; i < speps.length; i++) {
			speps[i] = new SingleParEntryPanel((String) pn.get(i),
					(String[]) pv.get(i), this);
			pep.add(speps[i]);
		}
		oneOnly = (speps.length == 1);
		add(pep, BorderLayout.CENTER);
		addFocusListener(new DebugFocusListener("RuntimeParsDialog"));
		setFocusable(false);
		pack();
		speps[0].setFocus();
		setVisible(true);
		// System.out.println("RuntimeParsDialog()");
	}

	protected void closeDialog() {
		for (int i = 0; i < speps.length; i++) {
			ExPar.get((String) parNames.get(i)).getValue()
					.set(speps[i].getExParValue());
			// System.out.println("RuntimeParsDialog.closeDialog() Parameter: "
			// + (String)parNames.get(i));
			// System.out.println("RuntimeParsDialog.closeDialog()     Value: "
			// + ExPar.get((String)parNames.get(i)).getValue());
		}
		setVisible(false);
		dispose();
	}

	public void parameterSet(String exParName) {
		// System.out.println("RuntimeParsDialog.parameterSet(): " + exParName);
		if (oneOnly) {
			closeDialog();
		}
	}
}
