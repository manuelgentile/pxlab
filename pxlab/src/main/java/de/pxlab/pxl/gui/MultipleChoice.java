package de.pxlab.pxl.gui;

import java.awt.*;

import java.util.ArrayList;
import de.pxlab.util.StringExt;
import de.pxlab.pxl.*;

/**
 * A Panel which contains multiple Choice objects which offer a choice from the
 * same list of items.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class MultipleChoice extends Panel {
	private static final String EMPTY = "";
	private Choice[] choices;
	private int nChoices;

	/** Create a Choice object for n parameters. */
	public MultipleChoice(int n, String[] list) {
		super(new GridLayout(0, 1));
		nChoices = n;
		choices = new Choice[nChoices];
		for (int j = 0; j < nChoices; j++) {
			Choice a = new Choice();
			for (int i = 0; i < list.length; i++) {
				a.add(list[i]);
			}
			a.add(EMPTY);
			a.select(EMPTY);
			add(a);
			choices[j] = a;
		}
	}

	public void setEnabled(boolean state) {
		for (int j = 0; j < nChoices; j++) {
			choices[j].setEnabled(state);
		}
	}

	public String[] getSelectedItems() {
		ArrayList p = new ArrayList();
		for (int j = 0; j < nChoices; j++) {
			String s = choices[j].getSelectedItem();
			if ((s != null) && (!s.equals(EMPTY))) {
				p.add(s);
			}
		}
		return StringExt.stringArrayOfList(p);
	}
}
