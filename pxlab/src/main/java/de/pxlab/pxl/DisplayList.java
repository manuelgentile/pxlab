package de.pxlab.pxl;

import java.lang.reflect.Field;

import java.util.*;

/**
 * A list of Display objects which are run during an experimental procedure
 * unit.
 * 
 * <p>
 * This class also manages a runtime control list of Display instances such that
 * duplicate Display instance names are excluded. This is necessary since
 * experimental parameters of Display objects have the instance name of their
 * Display object as a name prefix in the runtime control table of experimental
 * parameters.
 * 
 * @author H. Irtel
 * @version 0.4.4
 */
/*
 * 
 * 01/29/01 send the instance name to DisplaySupport.load()
 * 
 * 02/07/01 added clear() method and made some smaller fixes
 * 
 * 02/12/02 prepared for multiple execution of different experiments within a
 * single session.
 * 
 * 06/20/02 allow multiple procedure unit nodes.
 * 
 * 09/03/02 removed call to dspNode.addParameterAssignmentNodes(dsp)in the
 * constructor such that only those assignments are contained in the runtime
 * tree which are already contained in the design tree.
 * 
 * 09/11/02 Cleaned up some of the naming code, removed display modifyer
 * postfixes like "adj" ...
 * 
 * 10/31/02 re-inserted call to dspNode.addParameterAssignmentNodes(dsp) since
 * otherwise we get problems with showing the results of a call to the
 * DisplayEditor.
 */
public class DisplayList extends ArrayList {
	/**
	 * Contains the static list of all Display objects contained in any of the
	 * DisplayList objects created during run time. The keys in the map are
	 * created by the runtime instance names of the Display objects.
	 */
	private static HashMap runtimeDisplays = new HashMap();
	/**
	 * This is the name of this DisplayList object. All Display instances in
	 * this list will get this name as a prefix when being entered into the
	 * static runtime list of Display instances.
	 */
	private String name;
	/** Stores the node which created this DisplayList object. */
	private ExDesignNode node;

	/**
	 * Create a runtime DisplayList containing all Display objects whose
	 * instance names are given in the String array argument. This creates a
	 * runtime instance of every Display object and enters it into the runtime
	 * list of Display object instances. It also implicitly creates the Display
	 * object's experimental parameters and enters these into the runtime list
	 * of experimental parameters and adds an assignment node to the Display
	 * node for every parameter.
	 * 
	 * @param dspNam
	 *            and array of Display instance names which describe the Display
	 *            objects for this DisplayList.
	 * @param nm
	 *            the instance name of the DisplayList node to be created.
	 */
	/*
	 * public DisplayList (String[] dspNam, String nm) { name = nm; int n =
	 * dspNam.length; for (int i = 0; i < n; i++) { addDisplay(dspNam[i]); } }
	 */
	/**
	 * Create a runtime DisplayList from the Display children of the given
	 * DisplayList node. This creates a runtime instance of every Display object
	 * and enters it into the runtime list of Display object instances. It also
	 * implicitly creates the Display object's experimental parameters and
	 * enters these into the runtime list of experimental parameters and adds an
	 * assignment node to the Display node for every parameter.
	 * 
	 * @param displayListNode
	 *            the experimental design node for the DisplayList to be
	 *            created.
	 */
	public DisplayList(ExDesignNode displayListNode) {
		node = displayListNode;
		name = displayListNode.getInstanceName();
		// System.out.println("DisplayList(): " + name);
		Debug.show(Debug.DISPLAY_LIST, this, "Creating " + name);
		ArrayList dspList = displayListNode.getChildrenList();
		int n = dspList.size();
		for (int i = 0; i < n; i++) {
			// Get the Display node and its name
			ExDesignNode dspNode = (ExDesignNode) dspList.get(i);
			// Add the named Display instance to this DisplayList
			DisplaySupport dsp = addDisplay(dspNode);
			if (dsp != null) {
				// Add the display's parameters to the design node
				dspNode.addParameterAssignmentNodes(dsp);
			}
		}
	}

	/**
	 * Create and add a named Display object to this DisplayList object.
	 * 
	 * @param dspNode
	 *            the Display object node to add.
	 * @return the Display object created.
	 */
	public DisplaySupport addDisplay(ExDesignNode dspNode) {
		return addDisplay(size(), dspNode);
	}

	/**
	 * Create and add a named Display object to this DisplayList object at the
	 * given position.
	 * 
	 * @param idx
	 *            the index where to add the Display object.
	 * @param dspNode
	 *            the Display node object to be added.
	 * @return the Display object created.
	 */
	public DisplaySupport addDisplay(int idx, ExDesignNode dspNode) {
		DisplaySupport dsp = addDisplay(idx, dspNode.getName(),
				dspNode.getType());
		if (dsp != null) {
			dsp.setExDesignNode(dspNode);
		}
		return dsp;
	}

	/**
	 * Create and add a named Display object to this DisplayList object at the
	 * given position.
	 * 
	 * @param idx
	 *            the index where to add the Display object.
	 * @param dspName
	 *            the Display object instance name which contains a Display
	 *            class name and an optional instance modifier separated by a
	 *            colon.
	 * @return the Display object created.
	 */
	public DisplaySupport addDisplay(int idx, String dspName, int type) {
		// System.out.println("DisplayList size = " + size());
		// Figure out the name of the Display object's class
		String clsName = getDisplayClassName(dspName);
		// The display's instance name has this list's instance name as a prefix
		String instanceName = getDisplayInstanceName(dspName);
		// System.out.println("DisplayList.addDisplay(): " + dspName);
		Debug.show(Debug.DISPLAY_LIST, this, "Adding " + instanceName);
		// Load and instantiate the display object
		DisplaySupport dsp = (DisplaySupport) DisplaySupport.load(clsName,
				instanceName, type);
		if (dsp != null) {
			// Initialize the display's display elements and parameters
			dsp.createInstance();
			// Finally add it to the control table and check whether there
			// is a duplicate
			if (runtimeDisplays.put(instanceName, dsp) == null) {
				// Add the display object to this list
				add(idx, dsp);
				// Enter this display's parameters into the run time table.
				registerExParFields(dsp);
			} else {
				new DisplayError("Multiple instances of Display "
						+ instanceName + " requested");
				dsp = null;
			}
		}
		return dsp;
	}

	/**
	 * Clear this DisplayList, destroy all Display objects in the list and
	 * remove all parameters of the Display objects from the runtime parameter
	 * control lists.
	 */
	public void clear() {
		// System.out.println("DisplayList.clear()");
		Debug.show(Debug.DISPLAY_LIST, this, "Clearing " + getName());
		for (Iterator it = iterator(); it.hasNext();) {
			DisplaySupport dsp = (DisplaySupport) it.next();
			String dspInstanceName = dsp.getInstanceName();
			// System.out.println("DisplayList.clear() " + displayInstanceName);
			runtimeDisplays.remove(dspInstanceName);
			unregisterExParFields(dsp);
			dsp.destroyInstance();
		}
		super.clear();
	}

	/**
	 * Clear the list of runtime Display instances. This is necessary to allow
	 * for multiple experimental runs within a single session.
	 */
	public static void reset() {
		runtimeDisplays.clear();
	}

	/**
	 * Return the Display object with the given name from this DisplayList.
	 * 
	 * @param name
	 *            the name of the Display instance. This may include an instance
	 *            modifier suffix but must not include the display list prefix.
	 * @return the runtime Display object with the given instance name.
	 */
	public DisplaySupport getDisplay(String name) {
		String dspInstanceName = getDisplayInstanceName(name);
		DisplaySupport dsp = (DisplaySupport) runtimeDisplays
				.get(dspInstanceName);
		return (dsp);
	}

	/**
	 * Return the Display object with the given instance name from the list of
	 * runtime display instance objects.
	 * 
	 * @param name
	 *            the instance name of the display.
	 * @return the runtime Display object with the given instance name.
	 */
	public static DisplaySupport getDisplayInstance(String name) {
		DisplaySupport dsp = (DisplaySupport) runtimeDisplays.get(name);
		return (dsp);
	}

	/**
	 * Remove the named Display instance from this DisplayList and remove all
	 * parameters of the Display object from the runtime parameter control list.
	 * 
	 * @param name
	 *            the name of the Display instance to be removed.
	 */
	public void removeDisplay(String name) {
		// Figure out the name of the Display object's class
		String dspInstanceName = getDisplayInstanceName(name);
		DisplaySupport dsp = (DisplaySupport) runtimeDisplays
				.get(dspInstanceName);
		if (dsp == null) {
			new DisplayError("Can't remove display object " + dspInstanceName
					+ ": it does not exist!");
		} else {
			Debug.show(Debug.DISPLAY_LIST, this, "Removing " + dspInstanceName);
			// Remove it from this list and the runtime control list
			this.remove(dsp);
			runtimeDisplays.remove(dspInstanceName);
			// Also remove all experimental parameters of this display
			// from the runtime parameter list
			unregisterExParFields(dsp);
			// dsp.destroyInstance();
		}
	}

	/**
	 * Exchange the two Displays instances in this DisplayList at position k and
	 * k+1.
	 * 
	 * @param k
	 *            gives the position to be excanged.
	 */
	public void exchangeDisplaysAt(int k) {
		Debug.show(Debug.DISPLAY_LIST, this,
				"Exchanging Display objects at position " + k + " and "
						+ (k + 1));
		DisplaySupport x = (DisplaySupport) remove(k);
		super.add(k + 1, x);
	}

	/**
	 * Add the ExPar field objects of the given Display instance to the run time
	 * control list of experimental parameters.
	 * 
	 * @param dsp
	 *            Display object whose ExPar fields should be registered.
	 */
	private void registerExParFields(DisplaySupport dsp) {
		// System.out.println("DisplayList.registerExParFields() for " + dsp);
		ExParDescriptor[] xpd = dsp.getExParFields();
		for (int j = 0; j < xpd.length; j++) {
			Debug.show(Debug.DISPLAY_LIST, this,
					"register ExPar " + xpd[j].getName());
			ExPar.enter(xpd[j].getName(), xpd[j].getValue());
		}
	}

	/*
	 * public ExParDescriptor[] getExParDescriptors() { int n = size(); int m =
	 * 0; for (int i = 0; i < n; i++) { m +=
	 * ((Display)get(i)).getExParFields().length; } ExParDescriptor[] xpd = new
	 * ExParDescriptor[m]; int k = 0; for (int i = 0; i < n; i++) {
	 * ExParDescriptor[] xpds = ((Display)get(i)).getExParFields(); for (int j =
	 * 0; j < xpds.length; j++) { xpd[k++] = xpds[j]; } } return(xpd); }
	 */
	/**
	 * Remove the ExPar field objects of the given Display object from the run
	 * time control list of experimental parameters.
	 * 
	 * @param dsp
	 *            Display object whose ExPar fields should be unregistered.
	 */
	private void unregisterExParFields(DisplaySupport dsp) {
		ExParDescriptor[] xpd = dsp.getExParFields();
		for (int j = 0; j < xpd.length; j++) {
			ExPar.remove(xpd[j].getName());
			// System.out.println("ExPar " + xpd[j].getName() +
			// " removed from runtime list.");
		}
	}

	/** Get the ExDesignNode object which created this DisplayList. */
	public ExDesignNode getExDesignNode() {
		return node;
	}

	/** Return this display list's instance name. */
	public String getName() {
		return name;
	}

	/**
	 * Get the argument or modifier postfix of the given Display instance name.
	 * 
	 * @param dns
	 *            a Display object instance name which possibly may have an
	 *            argument postfix.
	 * @return the argument string if there is one or null otherwise.
	 */
	/*
	 * private String getArgument(String dns) { int colonIdx = dns.indexOf(':');
	 * return ((colonIdx >= 0)? dns.substring(colonIdx+1): null); }
	 */
	/**
	 * Get the instance name part of the given Display name string. Modifiers
	 * are removed.
	 * 
	 * @param name
	 *            the Display object name with optional instance modifier
	 * @return the full instance name including this display list's instance
	 *         prefix.
	 */
	private String getDisplayInstanceName(String name) {
		return (getName() + "." + name);
	}

	/**
	 * Get the class name part of the given Display instance name string.
	 * Instance parts and modifiers are removed.
	 * 
	 * @param dns
	 *            a Display object instance name which possibly may have an
	 *            argument postfix.
	 * @return the class name string with instance name and argument postfix
	 *         being removed.
	 */
	private String getDisplayClassName(String name) {
		int colon = name.indexOf(':');
		return ((colon >= 0) ? name.substring(0, colon) : name);
	}

	/** Convert this DisplayList into String. */
	public String toString() {
		StringBuffer s = new StringBuffer();
		String nl = System.getProperty("line.separator");
		for (int i = 0; i < size(); i++) {
			s.append(((DisplaySupport) get(i)).toString() + nl);
		}
		return (s.toString());
	}
}
