package de.pxlab.pxl.gui;

import java.util.ArrayList;

import de.pxlab.pxl.Topics;
import de.pxlab.pxl.Display;
import de.pxlab.util.FileList;

/**
 * An abstract list of experimental displays, demos, or data processing objects.
 * The displays can be entered manually or they can be entered automatically by
 * calling the addPlugins() method which will search the directory 'plugins' for
 * display class files and load them.
 * 
 * @version 0.2.0
 */
public class DisplayCollection extends ArrayList {
	protected int mask = Topics.EXP | Topics.DEMO;

	public int getTopicMask() {
		return mask;
	}

	public DisplayCollection() {
	}

	public DisplayCollection(int mask) {
		this.mask = mask;
	}

	/**
	 * Add displays from the plugin directory given as an argument.
	 */
	public void addPlugins(String path) {
		FileList fn = null;
		try {
			fn = new FileList(path, "class");
		} catch (java.io.FileNotFoundException fnf) {
			// System.out.println("Plugin path " + path + " not found.");
			return;
		}
		int n = fn.size();
		for (int i = 0; i < n; i++) {
			String pn = new String((String) fn.get(i));
			pn = pn.replace('\\', '.');
			pn = pn.substring(0, pn.length() - 6);
			pn = "de.pxlab.cvd.plugins." + pn.substring(8);
			try {
				Class p = Class.forName(pn);
				Object d = p.newInstance();
				add(d);
				System.out.println("Plugin " + pn + " added.");
			} catch (ClassNotFoundException cnf) {
				System.out.println("Plugin " + pn + " not found.");
			} catch (InstantiationException ie) {
				System.out.println("Plugin file " + pn
						+ " could not be instantiated.");
			} catch (IllegalAccessException ia) {
				System.out.println("Illegal access to plugin " + pn + ".");
			}
		}
	}

	/**
	 * Add a Display object to the list if (a) it has an unrestricted Topics id
	 * or (b) it has a Topics id which is restricted to the current mask of
	 * Topics.
	 */
	protected boolean add(Display d) {
		boolean r = true;
		if (mask != 0) {
			if (mask == Topics.DATA) {
				// DATA mask set
				if ((d.getTopic() & Topics.DATA) != 0) {
					super.add(d);
				} else {
					r = false;
				}
			} else {
				// non-DATA mask set
				int m = d.getTopic() & (Topics.EXP | Topics.DEMO);
				if ((m == 0) || ((m & mask) != 0)) {
					super.add(d);
				} else {
					r = false;
				}
			}
		} else {
			// no mask specified. Add all which do not have DATA set.
			if ((d.getTopic() & Topics.DATA) != 0) {
				r = false;
			} else {
				super.add(d);
			}
		}
		return r;
	}
}
