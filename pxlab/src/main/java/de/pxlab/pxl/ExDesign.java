package de.pxlab.pxl;

import java.io.*;
import java.net.*;
import java.lang.reflect.Field;
import java.util.*;

import de.pxlab.util.*;

import de.pxlab.pxl.parser.*;

/**
 * This class creates the design tree from a design file and provides methods
 * for handling the design tree and for running an experiment.
 * 
 * <h3>The runtime object naming strategy</h3>
 * 
 * The runtime design tree may contain multiple instances of some node classes.
 * Here we describe how runtime instance naming is derived.
 * 
 * <h4>Session, SessionEnd, Block, BlockEnd, Trial</h4>
 * 
 * <p>
 * The design tree may contain multiple instances of these. If this is the case
 * then the respective declaration must append an instance modifier to the
 * respective node type name like in 'Session:Study' where 'Session' is the node
 * type and 'Study' is the instance modifier. The full instance name must be
 * unique within the design file.
 * 
 * <h4>Display Subclasses</h4>
 * 
 * <p>
 * A single display list may contain multiple instances of a display class.
 * These also are declared with an instance modifier being appended to the
 * display class name like in 'ClearScreen:Wait' where 'ClearScreen' is the
 * display class name and 'Wait' ist the instance modifier. Multiple instances
 * of a display object may, however, also come into existence by multiple
 * display list instances conatining the same display objects. Since it is
 * absolutely necessary to have a unique instance name for every display object
 * the full instance name is created by the runtime design controller by
 * appending the display object instance name to the display list instance name
 * and making this the full runtime instance name of the display object. This
 * runtime instance name is entered into a runtime symbol table which makes sure
 * that these names are unique. Thus the full runtime instance display object
 * name of our example is 'Session:Study.ClearScreen:Wait'.
 * 
 * <h4>Experimental Parameters</h4>
 * 
 * <p>
 * There are two sets of experimental parameters: global parameters and display
 * object parameters. Global parameters are those which are defined in class
 * ExPar or those which are defined within the design file using the 'new'
 * prefix in front of an assignment. Display object parameters are those which
 * are defined in the class Display or in one of its subclasses. Global
 * parameters are unique and have only a single instance throughout the whole
 * design file. Thus their instance name is identical to their declaration name.
 * Display object parameters belong to a display object instance and thus need
 * an instance modifier to be addresses properly. The full runtime instance name
 * of a display parameter is built by appending the parameter declaration name
 * to the full runtime instance name of its display object. Thus we get
 * 'Session:Study.ClearScreen:Wait.Duration' as the full runtime instance
 * parameter name of the parameter 'Duration' of this instance of class
 * 'ClearScreen' in display list 'Session:Wait'.
 * 
 * <p>
 * There are some cases where it seems odd to fully give the runtime instance
 * name of an experimental parameter in the design file since the context makes
 * clear what the instance name must be. Thus to simplify typing there are some
 * rules which make the design more readible and simplify typing.
 * 
 * <ul>
 * <li>Assignments. It is not necessary -- in fact it is not even allowed -- to
 * give the full instance name of an experimental parameter on the left side of
 * an assignment. Since explicit assignments are always executed before data
 * collection starts they are effectively 'global' and thus all explicit
 * assignments have to be done within the Context section of a design file. And
 * there the context always uniquely identifies parameter instances which are
 * used as assignment targets. This is not true for experimental parameter names
 * which appear as variable names in expressions at the right side of an
 * assignment. These are evaluated only at runtime in the Procedure section when
 * a display list is executed. <b>Thus experimental parameters which appear as
 * variables at the right side of an assignment must always be specified with
 * their full runtime instance names</b>. This also holds for experimental
 * parameters which appear between %-signs in strings.
 * 
 * <li>Procedure Unit Argument Lists. Frequently experimental parameters of
 * display objects contained in a display list will be members of the argument
 * list of this procedure unit. In this case the instance prefix specifying the
 * display list instance may be omitted. The design tree controller will add the
 * display list instance name to every argument which has a display instance
 * name prefix but has no display list instance name prefix. Note that display
 * instance name prefixes of experimental parameters appearing in a display list
 * argument list must not be omitted.
 * 
 * </ul>
 * 
 * @version 0.7.7
 * @see de.pxlab.pxl.parser.ExDesignTreeParser
 * @see ExDesignNode
 * @see ExDesignProcessor
 */
/*
 * 
 * 02/26/01 Moved Run timeContext() into this file
 * 
 * 05/09/01 evaluate trial return codes and possibly repeat trials
 * 
 * 05/14/01 check for undefined parameter names and values, and check argument
 * lists before running a session.
 * 
 * 05/20/01 moved setRuntimePars() into runExperiment()
 * 
 * 05/20/01 use ActiveSubjectGroups and check whether the current group code is
 * contained in the list of this parameter's values.
 * 
 * 05/29/01 allow empty blocks for Session() and Bock()
 * 
 * 07/09/01 allow the SessionState parameter to stop a session after the current
 * block has been finished.
 * 
 * 07/11/01 allow multiple sessions being run.
 * 
 * 08/15/01 allow for storing the data tree after execution
 * 
 * 08/16/01 added the runtimeExpansion flag
 * 
 * 08/27/01 new: getCurrentlyDefinedIndependentFactorNames()
 * 
 * 09/15/01 send currentBlock to the data writer
 * 
 * 09/16/01 added adaptive procedure control
 * 
 * 09/26/01 Removed Session/Block/Trial parameter nodes: 0.4.0
 * 
 * 11/17/01 respect SkipBoundingBlockDisplays
 * 
 * 12/31/01 added HTML export
 * 
 * 02/12/02 moved the code which created display list instances into this file
 * in order to prepare for save execution of multiple experimental designs in a
 * single session.
 * 
 * 08/20/02 allow multiple procedure unit nodes named by adding a postfix to the
 * original node names.
 * 
 * 10/01/02 fixed adjustment method after recent changes of the input syntax.
 * The :adj postfix is no longer used.
 * 
 * 12/03/02 moved DataWriter creation into the runSession() method in order to
 * evaluate runtime parameter dialogs correctly.
 * 
 * 06/21/03 include initialization of primaries and gamma of the current color
 * device into initRuntimeContext()
 * 
 * 09/12/03 method idCode() is new.
 * 
 * 04/23/04 fixed tree structure error when adding runtime assignments from the
 * command line: commandLineAssignmentsGroup()
 * 
 * 06/28/04 if trialReturnCode != StateCodes.EXECUTE and the paramter
 * RepeatErrorTrials is set then the current trial is added to the trial list
 * after completion. runTrial() gets its return code from
 * displayPanel.showDisplayList(). This sets it to TRIAL_OK by default. It sets
 * it to TRIAL_BREAK only under if the display list contains an active
 * DisplayListControl object and the display's displayListControlState() method
 * returns false.
 * 
 * A mechanism was added which looks at ExPar.TrialState and also repeats a
 * trial if this parameter contains an error code and ExPar.RepeatErrorTrials is
 * set to 1.
 * 
 * 2004/12/11 modified active session list creation method in order to allow for
 * specifying multiple sessions in experimental parameter ActiveSession.
 * 
 * 2004/12/12 modified the method setRuntimePars() such that only those
 * parameter names enter the runtime pars dialog whose values are not yet
 * defined in the Experiment() node.
 * 
 * 2004/12/13 some reorganization in the runSession() method in order to enable
 * argument values in the Experiment() node. Also restricted the execution of
 * Session nodes to cases where the current subject group is in the current
 * ActiveSubjectGroups list. The method initRuntimeContext() has been added to
 * runExperiment() and removed from ExRun().
 * 
 * 2005/01/15 modified subjectGroupIsActive() such that it returns true if the
 * experimental parameter ActiveSubjectGroups is undefined. This should tell us
 * that no subject group restriction via ActiveSubjectGroups is intended.
 * 
 * 2005/01/28 added factorial data writing
 * 
 * 2005/06/01 fixed procedure unit state handling.
 * 
 * 2005/10/28 added FACTORS debugging
 * 
 * 2005/12/09 removed TrialCounter from the FactorialDataFormat
 * 
 * 2006/10/31 setDeviceWhitePoint()
 * 
 * 2006/11/15 use AskForRuntimeParameterValues
 * 
 * 2006/11/17 fixed bug in method getActiveSessionList()
 * 
 * 2007/05/30 fixed bug when setting the designBase.
 */
public class ExDesign implements Cloneable {
	/** The root node of the experimental design tree. */
	protected ExDesignNode exDesignTree = null;
	/** The name of the design file. */
	protected String fileName;
	/**
	 * The root node of the experimental data tree. The data tree is built while
	 * the data are collected. It is a copy of the design tree but all node
	 * parameter values are replaced with those parameter values that were
	 * current immediately after the node has been executed. Thus the data tree
	 * contains the actual data as its parameter values.
	 */
	protected ExDesignNode exDesignDataTree = null;
	/**
	 * This flag my be used to stop run time session execution in an orderly
	 * manner.
	 */
	private boolean stopExRun;
	/**
	 * The collection of display list instances for the various procedure nodes.
	 * The existence of this map indicates a valid runtime context.
	 */
	private HashMap displayListMap = null;
	/**
	 * A map which contains the adaptive control objects for all adaptive
	 * sequences which are mixed within a block.
	 */
	private HashMap adaptiveControlMap = new HashMap(20);
	/**
	 * If true then nodes are expanded, multiplied and randomized at runtime.
	 * Must be set to false by postprocessors of design trees.
	 */
	private boolean runtimeExpansion = true;
	/**
	 * When an experiment is running then this is the active ExDesignProcessor.
	 */
	private ExDesignProcessor exDesignProcessor;
	/**
	 * When an experiment is running then this is the active
	 * ExDesignDataProcessor.
	 */
	private ExDesignDataProcessor dataProcessor;

	/** Create the default experimental design tree. */
	public ExDesign() {
		setDefaultExDesign();
	}

	/**
	 * Create an experimental design tree from the given design file and
	 * optional runtime assignments. This constructor is used by applications.
	 * 
	 * @param fn
	 *            full access path relative to the current working directory.
	 * @param as
	 *            a string containing a sequence of runtime parameter
	 *            assignments which may be derived from command line arguments.
	 */
	public ExDesign(String fn, String as) throws IOException {
		File fp = new File(fn);
		fileName = fp.getAbsolutePath();
		Base.setDesignBase(fp.getCanonicalFile().getParent());
		Debug.show(Debug.FILES, "ExDesign(): Open design file " + fileName);
		FileInputStream rdr = new FileInputStream(fp);
		exDesignTree = loadExDesignTree(rdr, fn);
		rdr.close();
		Debug.show(Debug.FILES, "ExDesign(): Closed design file " + fileName);
		if (as != null) {
			addCommandLineAssignmentsGroup(as);
		}
		// System.out.println("ExDesign(String, String): File " + fp);
		if (Base.getPrintDesignFile())
			exDesignTree.print();
	}

	/**
	 * Create an experimental design tree from the design file at the given URL.
	 * This constructor is used by applets to instantiate an ExDesign object.
	 * 
	 * @param urlc
	 *            the URL context for the name of the design file.
	 * @param fn
	 *            name of a pxl control file.
	 */
	public ExDesign(URL urlc, String fn) throws IOException {
		URL exDesignFileURL = null;
		try {
			exDesignFileURL = new URL(urlc, fn);
			fileName = exDesignFileURL.toString();
			Base.setDesignBase(exDesignFileURL);
		} catch (MalformedURLException e1) {
			throw new IOException("Malformed URL: (" + urlc.toString() + ","
					+ fn + ")");
		}
		exDesignTree = createExDesignTree(exDesignFileURL);
		if (Base.getPrintDesignFile())
			exDesignTree.print();
	}

	/**
	 * Create the experimental design tree from the design file at the given
	 * URL.
	 * 
	 * @param exDesignFileURL
	 *            the URL pointing to the design file location.
	 */
	/*
	 * public ExDesign(URL exDesignFileURL) throws IOException { fileName =
	 * exDesignFileURL.toString(); exDesignTree =
	 * createExDesignTree(exDesignFileURL); // initRuntimeContext(); if
	 * (Base.getPrintDesignFile()) exDesignTree.print(); }
	 */
	/**
	 * Create a copy of this object.
	 * 
	 * @return a copy of this <code>ExDesignNode</code> object.
	 */
	public Object clone() {
		try {
			ExDesign a = (ExDesign) super.clone();
			a.exDesignTree = exDesignTree.dupTree();
			return a;
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new RuntimeException("");
		}
	}

	protected ExDesignNode createExDesignTree(URL exDesignFileURL)
			throws IOException {
		ExDesignNode tree = null;
		InputStream ins = null;
		StringBuffer sb = new StringBuffer(1000);
		try {
			ins = exDesignFileURL.openStream();
		} catch (IOException e2) {
			System.out.println("ExDesign: IO Error: Can't open stream "
					+ fileName);
			throw e2;
		}
		try {
			tree = loadExDesignTree(ins, exDesignFileURL.toString());
			ins.close();
		} catch (IOException e2) {
			System.out.println("ExDesign: IO Error!");
			throw e2;
		}
		return tree;
	}

	/**
	 * Get the experimental design tree from the given Reader object. Every
	 * access to a complete design file is via this method. This also means that
	 * this method handles parse error reporting.
	 * 
	 * @param ins
	 *            a Reader which delivers the design file content.
	 * @param fn
	 *            the name of the design file source. This parameter is only
	 *            used for debugging purpose.
	 * @return the root node of the experimental design tree or null of no tree
	 *         could be created.
	 */
	private ExDesignNode loadExDesignTree(InputStream ins, String fn)
			throws IOException {
		// Debug.add(Debug.PARSER);
		// System.out.println("ExDesign.loadExDesignTree() from " + fn);
		ExDesignNode dt = null;
		ExDesignTreeParser pp = new ExDesignTreeParser(ins, Base.getEncoding());
		// Debug.show(Debug.PARSER, this, "Parser created.");
		Debug.show(Debug.FILES,
				"ExDesign.loadExDesignTree(): Read design file " + fn);
		Debug.show(Debug.PARSER, this, "Parse file " + fn + " ...");
		// May throw a ParseException
		try {
			dt = pp.experimentDeclaration();
		} catch (ParseException pex) {
			new SyntaxError(StringExt.fixedParserErrorMessage(pex, fn));
			throw new IOException("Syntax error in file " + fn);
		} catch (TokenMgrError tme) {
			new SyntaxError(StringExt.fixedParserErrorMessage(tme, fn));
			throw new IOException("Token error in file " + fn);
		}
		Debug.show(Debug.PARSER, this, "File " + fn + " parsed successfully.");
		// System.out.println("ExDesign.loadExDesignTree() finished.");
		return dt;
	}

	private void addCommandLineAssignmentsGroup(String commandLineAssignments) {
		if (commandLineAssignments != null) {
			ExDesignNode dt = getExDesignTree();
			ExDesignNode contextTree = null;
			if (dt != null) {
				contextTree = dt.getFirstChildOfType(ExDesignNode.ContextNode);
			} else {
				new ExDesignTreeError(
						"ExDesign.addCommandLineAssignmentsGroup(): No design tree!");
				return;
			}
			ArrayList contextChildrenList = contextTree.getChildrenList();
			contextChildrenList.add(commandLineAssignmentsGroup(contextTree,
					commandLineAssignments));
		}
	}

	/**
	 * Initialize the runtime context for this experimental design tree. The
	 * runtime context contains the DisplayList and Display instances of all
	 * stimuli and the experimental parameters of all Display instances. The
	 * Display instances are stored in the static hash map runtimeDisplays of
	 * class DisplayList which strictly enforces uniqueness of Display instance
	 * names. The experimental parameters of these Display instances are stored
	 * in the static list runtimePars of class ExPar. This makes it possible for
	 * ExPar to guarantee that experimental parameter names are unique too.
	 * 
	 * <p>
	 * This method creates the runtime context. This means that all Display
	 * instances and their experimental parameters are instantiated and
	 * registered. The following steps are involved:
	 * 
	 * <ol>
	 * <li>An existing runtime context is destroyed.
	 * 
	 * <li>Any new parameter assignment statements in the Context() tree of the
	 * design file are executed. This creates instances of these experimental
	 * parameters and registers them with the runtime context.
	 * 
	 * <li>Parameter assignments from the command line are appended to the
	 * Context() block without executing them.
	 * 
	 * <li>The Context() tree is iterated through and all its statements are
	 * executed. This executes all parameter assignments and creates all Display
	 * instances. Executing a parameter assignments means that the global value
	 * of the respective experimental parameter is set to the value given in the
	 * assignment statement.
	 * 
	 * <li>The Factors() tree is iterated through and all independent and
	 * covariate factor names are checked whether they exist as experimental
	 * parameters in the runtime context. If not then they are created and
	 * registered.
	 * 
	 * </ol>
	 * 
	 * 
	 * <p>
	 * This method is called in the following situations:
	 * 
	 * <ol>
	 * <li>whenever an ExDesign object is instantiated.
	 * 
	 * <li>whenever the runtime parameter assignments of an ExDesign object are
	 * set by setRuntimeAssignments().
	 * 
	 * <li>whenever an ExDesign object is cloned then its runtime properties
	 * have to be set. This is done by the ExDesignEditorPanel class since the
	 * experiment is run on a clone of the ExDesign object which is currently
	 * edited.
	 * 
	 * <li>whenver a non-active ExDesign object is made active then its runtime
	 * properties have to be created. This is done by the ExDesignEditorPanel
	 * class after an experiment has been run since the experiment is run on a
	 * clone of the ExDesign object which is currently edited.
	 * 
	 * </ol>
	 */
	public void initRuntimeContext() {
		// System.out.println("ExDesign.initRuntimeContext() for file " +
		// fileName);
		// new RuntimeException().printStackTrace();
		// Remove earlier display list objects if they exist.
		// System.out.println("ExDesign.initRuntimeContext() clearing earlier list ...");
		if (displayListMap != null) {
			// Destroy all display objects
			for (Iterator it = displayListMap.entrySet().iterator(); it
					.hasNext();) {
				((ArrayList) (((java.util.Map.Entry) it.next()).getValue()))
						.clear();
			}
			displayListMap.clear();
			displayListMap = null;
		}
		// Reset the static hash map of display instances
		DisplayList.reset();
		// Clear runtime objects created by Display instances
		RuntimeRegistry.clear();
		// Clear the image cache
		FileBase.clear();
		// Reset the static runtime parameter table
		ExPar.reset();
		ExPar.AppletSystem.set(Base.isApplication() ? 0 : 1);
		Base.initRuntime();
		setDeviceWhitePoint(0);
		// Now create the display lists
		// Create the runtime map of DisplayList instances
		displayListMap = new HashMap(10);
		// Now iterate through the tree, create the display lists and
		// their parameters and execute local assignments
		// Get the context definition tree
		ExDesignNode dt = getExDesignTree();
		ExDesignNode contextTree = null;
		if (dt != null) {
			contextTree = dt.getFirstChildOfType(ExDesignNode.ContextNode);
		} else {
			new ExDesignTreeError(
					"ExDesign.initRuntimeContext(): No design tree!");
			return;
		}
		if (contextTree == null) {
			new ExDesignTreeError(
					"ExDesign.initRuntimeContext(): No context node!");
			return;
		}
		// System.out.println("ExDesign.initRuntimeContext() Iterate through all assignments in the Contect node ...");
		// Cycle through the Context() node and (1) execute all
		// assignments, (2) set adjustable Display objects, (3) create
		// runtime HashMap of DisplayList objects, (4) implicitly
		// create the runtime list of Display objects and their
		// experimental parameters.
		for (Iterator i = contextTree.iterator(); i.hasNext();) {
			ExDesignNode node = (ExDesignNode) i.next();
			// System.out.println("ExDesign.initRuntimeContext(): Node type " +
			// node.getName() + " (" + node.getType() + ") found.");
			if (node.isAssignment()) {
				node.doAssignment();
				int type = node.getType();
				if (type == ExDesignNode.AdjustableParamAssignmentNode) {
					ExDesignNode p = (ExDesignNode) node.getParent();
					if (p.isDisplay()) {
						String nm = p.getInstanceName();
						DisplaySupport dsp = DisplayList.getDisplayInstance(nm);
						if (dsp instanceof Display) {
							((Display) dsp).setAdjustable(true);
							String pnm = node.getInstanceName();
							// System.out.println("ExDesign.initRuntimeContext(): Setting "
							// + nm + " to be adjustable in " + pnm);
							((Display) dsp).setDynExPar(ExPar.get(pnm));
						}
					}
				}
			} else if (node.isDisplayList() || node.isDataDisplayList()) {
				Object d = displayListMap.put(node.getName(), new DisplayList(
						node));
				// System.out.println("ExDesign.initRuntimeContext(): Enter node "
				// + node.getName() + " into runtime table.");
				if (d != null) {
					new DisplayError("Duplicate DisplayList names: "
							+ node.getName());
				}
			}
			// specialNodeHandling(e);
		}
		// Check the first parameter of each independent and covariate
		// factor node and enter it into the runtime table if it does
		// not exist, since this is the factor name. This must be done
		// before the context tree is analyzed since it creates new
		// experimental parameters which may be used in assignments.
		ExDesignNode factorsTree = getFactorsTree();
		if (factorsTree != null) {
			for (Enumeration enfs = factorsTree.children(); enfs
					.hasMoreElements();) {
				ExDesignNode factor = (ExDesignNode) enfs.nextElement();
				if (factor.isIndependentFactor() || factor.isCovariateFactor()) {
					String[] fpna = factor.getParNames();
					// System.out.print("ExDesign.initRuntimeContext() creating factor "
					// + fpna[0]);
					ExPar p = ExPar.create(fpna[0]);
					// Set the type if the parameter did not exist before
					if (p != null)
						p.setType(ExParTypeCodes.EXPFACTOR);
					// System.out.println(" OK");
				}
			}
		}
		// System.out.println("ExDesign.initRuntimeContext() - done.");
	}

	/**
	 * Set the experimental parameter ExPar.DeviceWhitePoint from the current
	 * values of screen parameters. This method should only be used for ExDesign
	 * objects which do not have an active ExDesignProcessor, since these
	 * already have set the device white point when the display device had been
	 * created.
	 * 
	 * @param k
	 *            is 1 for the primary screen parameters and 2 for the secondary
	 *            screen parameters. If k is 0 then the device white point is
	 *            only set if this has not yet been done after the most recent
	 *            ExPar initialization.
	 */
	public void setDeviceWhitePoint(int k) {
		if ((k > 0) || (ExPar.DeviceWhitePoint.getDoubleArray().length < 3)) {
			ScreenColorTransform colorDeviceTransform = null;
			if (k == 0 || k == 1) {
				colorDeviceTransform = new ScreenColorTransform(
						PrimaryScreen.RedPrimary, PrimaryScreen.GreenPrimary,
						PrimaryScreen.BluePrimary, PrimaryScreen.RedGamma,
						PrimaryScreen.GreenGamma, PrimaryScreen.BlueGamma,
						PrimaryScreen.ColorDeviceDACRange,
						PrimaryScreen.DeviceColorTableFile);
			} else {
				colorDeviceTransform = new ScreenColorTransform(
						SecondaryScreen.RedPrimary,
						SecondaryScreen.GreenPrimary,
						SecondaryScreen.BluePrimary, SecondaryScreen.RedGamma,
						SecondaryScreen.GreenGamma, SecondaryScreen.BlueGamma,
						SecondaryScreen.ColorDeviceDACRange,
						SecondaryScreen.DeviceColorTableFile);
			}
			ExPar.DeviceWhitePoint.set(new PxlColor(colorDeviceTransform
					.getWhitePoint()));
			// System.out.println("ExDesign.setDeviceWhitePoint(" + k +
			// ") DeviceWhitePoint = " + ExPar.DeviceWhitePoint);
		} else {
			System.out.println("ExDesign.setDeviceWhitePoint(" + k
					+ ") DeviceWhitePoint not changed. Current value is "
					+ ExPar.DeviceWhitePoint);
		}
	}
	private Stack contextStack = new Stack();

	public void pushContext() {
		// System.out.println("ExDesign.pushContext()");
		// Get the context definition tree
		ExDesignNode dt = getExDesignTree();
		ExDesignNode contextTree = null;
		if (dt != null) {
			contextTree = dt.getFirstChildOfType(ExDesignNode.ContextNode);
		} else {
			return;
		}
		if (contextTree == null) {
			return;
		}
		// contextTree.print();
		for (Iterator i = contextTree.iterator(); i.hasNext();) {
			ExDesignNode node = (ExDesignNode) i.next();
			if (node.isAssignment()) {
				String n = node.getInstanceName();
				// System.out.println("ExDesign.pushContext(): Assignment to " +
				// n);
				ExParValue v = node.getParValues()[0];
				if (ExDesignNode.isClassAttribute(n)) {
					int p = n.lastIndexOf('.');
					String cn = n.substring(0, p);
					String pn = n.substring(p + 1);
					ExPar xp = ((ExPar) (ExPar
							.get("de.pxlab.pxl", cn, pn, true)));
					if (xp != null) {
						xp.push(v);
						contextStack.push(xp);
						Debug.show(Debug.PUSH_POP, "  de.pxlab.pxl" + cn + "."
								+ pn + " pushed " + v);
					}
				} else {
					ExPar xp = ExPar.get(n);
					if (xp != null) {
						xp.push(v);
						contextStack.push(xp);
						Debug.show(Debug.PUSH_POP, "  " + n + " pushed " + v);
					}
				}
			}
		}
	}

	public void popContext() {
		while (!contextStack.empty()) {
			((ExPar) contextStack.pop()).pop();
		}
	}

	/**
	 * Create a list of assignment nodes from the runtime command line
	 * assignments. Note that these assignments have the type modifier
	 * ExplicitAssignment already set.
	 */
	protected ExDesignNode commandLineAssignmentsGroup(ExDesignNode parent,
			String commandLineAssignments) {
		// System.out.println("ExDesign.commandLineAssignmentsGroup()");
		// new RuntimeException().printStackTrace();
		ExDesignNode rta = null;
		if (commandLineAssignments != null) {
			String nl = System.getProperty("line.separator");
			rta = new ExDesignNode(ExDesignNode.AssignmentGroupNode, 5);
			rta.setParent(parent);
			rta.setComment("// Command line assignments" + nl);
			ExDesignTreeParser pp = new ExDesignTreeParser(new StringReader(
					commandLineAssignments));
			try {
				pp.createListOfAssignments(rta);
			} catch (ParseException e) {
				new SyntaxError(
						"Encountered syntax error while parsing the command line arguments.");
			} catch (TokenMgrError tme) {
				new SyntaxError(
						"Encountered token error while parsing the command line arguments.");
			}
		}
		return (rta);
	}

	/**
	 * Run this experiment. Since this method will usually be executed from
	 * within the event handling thread we have to create our own Thread to run
	 * the experiment. Otherwise the experiment's wait periods would block the
	 * event handling thread and the response events would never be noticed.
	 * 
	 * <p>
	 * The first step is to initialize the runtime context which executes all
	 * global parameter assignments.
	 * 
	 * <p>
	 * Then we check whether a valid SubjectCode exists. If yes then we create
	 * the DataWriter and check for a previous data tree in order to set the
	 * session parameters. If no SubjectCode is defined then we ask for the
	 * session parameters and check for the SubjectCode once more. If still
	 * there is no SubjectCode we ask for it and create the DataWriter.
	 * 
	 * <p>
	 * Finally we start the experimental thread with the given ExDesignProcessor
	 * and the DataWriter which has been created.
	 * 
	 * @param threadStarter
	 *            the object which starts this thread.
	 * @param exProc
	 *            the design processor which actually executes the design nodes.
	 */
	public void runExperiment(ExDesignThreadStarter threadStarter,
			ExDesignProcessor exProc, ExDesignDataProcessor dataProc) {
		// System.out.println("Running experiment ...");
		// Recreate the runtime context
		initRuntimeContext();
		DataDestination dataDestination = null;
		// System.out.println("ExDesign.runExperiment() SubjectCode = " +
		// ExPar.SubjectCode.getString());
		if (StringExt.nonEmpty(ExPar.SubjectCode.getString())) {
			// We get here if we have a valid SubjectCode
			dataDestination = new DataDestination();
			String previousDataTree = dataDestination
					.getMostRecentDataFileName();
			if (previousDataTree != null) {
				try {
					setSessionParametersFromPreviousDataTree(previousDataTree);
				} catch (IOException iex) {
					new FileError("ExDesign file " + previousDataTree
							+ " not found.");
				}
			} else {
				if (ExPar.AskForRuntimeParameterValues.getFlag()) {
					setSessionParametersFromDialog();
				}
			}
		} else {
			// We get here if we do not yet have a valid SubjectCode
			setSessionParametersFromDialog();
			// Another try for the SubjectCode
			validateSubjectCode();
			dataDestination = new DataDestination();
		}
		dataProc.setDataDestination(dataDestination);
		ExRunThread exRunThread = new ExRunThread("ExDesignRun", threadStarter,
				exProc, dataProc, dataDestination);
		exRunThread
				.setUncaughtExceptionHandler(new UncaughtExRunExceptionHandler());
		exRunThread.start();
	}
	class UncaughtExRunExceptionHandler implements
			java.lang.Thread.UncaughtExceptionHandler {
		public void uncaughtException(Thread t, Throwable e) {
			if (e instanceof OutOfMemoryError) {
				System.out
						.println("\nOut of heap memory!\nUse command line option '-Xmx256m' to enlarge heap memory!");
				System.exit(3);
			} else if (e instanceof java.lang.NoClassDefFoundError) {
				String n = e.getMessage();
				System.out.println("\nFatal runtime error.");
				System.out.println("Class definition not found: " + n);
				if (n.startsWith("org/apache/batik")) {
					System.out
							.println("The Apache Batik toolkit for handling SVG graphics is missing.");
					System.out
							.println("You may get it from http://xml.apache.org/batik/");
				} else if (n.startsWith("javax/media")) {
					System.out
							.println("The Java Media Framework package is missing.");
					System.out
							.println("You may get it from http://java.sun.com/products/java-media/jmf/");
				} else if (n.startsWith("javax/comm")) {
					System.out
							.println("The Java Communications package is missing.");
					System.out
							.println("You may get it from http://java.sun.com/products/javacomm/");
				} else {
					e.printStackTrace();
				}
				System.exit(3);
			} else {
				System.out.println("Exception in thread \"" + t.getName()
						+ "\" " + e);
				e.printStackTrace();
				System.exit(3);
			}
		}
	}

	/** Make sure that a valid subject code is defined. */
	private void validateSubjectCode() {
		if (!StringExt.nonEmpty(ExPar.SubjectCode.getString())) {
			ArrayList pn = new ArrayList();
			ArrayList pv = new ArrayList();
			pn.add("SubjectCode");
			pv.add(null);
			// System.out.println("ExDesign.validateSubjectCode(): starting Dialog.");
			new RuntimeParsDialog(Base.getFrame(), pn, pv);
		} else {
			// System.out.println("ExDesign.validateSubjectCode(): SubjectCode = "
			// + ExPar.SubjectCode.getString());
		}
		// System.out.println("ExDesign.validateSubjectCode() finished. SubjectCode = "
		// + ExPar.SubjectCode.getString());
	}
	private ArrayList previousData = null;

	/**
	 * Set session parameters from the most recent data tree file of the current
	 * subject. This method looks into the previous data tree and searches for
	 * AssignmentGroups. It then checks whether any of those parameters which
	 * are returned by the method sessionParameterNames() is assigned a value.
	 * If yes, then this parameter's value is set from the value in the data
	 * tree.
	 */
	private void setSessionParametersFromPreviousDataTree(String mostRecentFile)
			throws IOException {
		Debug.show(Debug.FILES,
				"ExDesign.setSessionParameters(): Most recent data file: "
						+ mostRecentFile);
		if (mostRecentFile != null) {
			ExDesignNode mrdf;
			mrdf = loadExDesignTree(new FileInputStream(
					new File(mostRecentFile)), mostRecentFile);
			String[] pars = sessionParameterNames();
			ExDesignNode contextTree = mrdf
					.getFirstChildOfType(ExDesignNode.ContextNode);
			ArrayList ctns = contextTree.getChildrenList();
			for (int k = 0; k < ctns.size(); k++) {
				ExDesignNode contextChild = (ExDesignNode) (ctns.get(k));
				if (contextChild.isAssignmentGroup()) {
					// System.out.println("ExDesign.setSessionParameters() found AssignmentGroup");
					for (Iterator i = contextChild.iterator(); i.hasNext();) {
						ExDesignNode node = (ExDesignNode) i.next();
						if (node.isAssignment()) {
							// System.out.println("ExDesign.setSessionParameters() found Assignment: "
							// + node);
							String p = node.getParNames()[0];
							int idx = StringExt.indexOf(p, pars);
							if (idx >= 0) {
								ExPar.set(p, node.getParValues()[0]);
								// System.out.println("ExDesign.setSessionParameters(): "
								// + p + " = " + node.getParValues()[0]);
							}
						}
					}
				}
			}
			if (ExPar.JoinDataTrees.getFlag()) {
				previousData = mrdf.getFirstChildOfType(
						ExDesignNode.ProcedureNode).getChildrenList();
			}
		} else {
			ExPar.RemainingSessionGroup.getValue().set(
					ExPar.SessionGroup.getValue());
			ExPar.RemainingSessionRuns.getValue().set(
					ExPar.SessionRuns.getValue());
		}
		// At this point we have set SubjectGroup,
		// RemainingSessionGroup, RemainingSessionRuns to the
		// values of the most recent data file of of the current SubjectCode
	}

	/**
	 * Get the name of the session parameters. These are parameters whose value
	 * depend on the subject parameter. This includes all covariate factors, all
	 * parameters of the Experiment() node, RemainingSessionGroup,
	 * RemainingSessionRuns, and SubjectGroup.
	 */
	private String[] sessionParameterNames() {
		ArrayList n = new ArrayList();
		ExDesignNode factorsTree = getFactorsTree();
		if (factorsTree != null) {
			ArrayList factors = factorsTree.getChildrenList();
			for (int i = 0; i < factors.size(); i++) {
				ExDesignNode factor = (ExDesignNode) (factors.get(i));
				if (factor.isCovariateFactor()) {
					n.add(factor.getParNames()[0]);
				}
			}
		}
		String[] pn = getExDesignTree().getParNames();
		if ((pn != null) && (pn.length > 0)) {
			for (int i = 0; i < pn.length; i++) {
				if (n.indexOf(pn[i]) < 0) {
					n.add(pn[i]);
				}
			}
		}
		n.add("RemainingSessionGroup");
		n.add("RemainingSessionRuns");
		if (n.indexOf("SubjectGroup") < 0) {
			n.add("SubjectGroup");
		}
		return StringExt.stringArrayOfList(n);
	}

	/**
	 * Check whether we have to ask for runtime parameters and do so if
	 * necessary. If yes then open the runtime parameters dialog and force the
	 * user to enter the respective parameter values.
	 * 
	 * <p>
	 * Runtime parameters to be requested are parameters which either are
	 * arguments of the Experiment node or which have been defined as covariate
	 * factors.
	 * 
	 * <p>
	 * Parameters of the Experiment() node are added to the list of runtime
	 * parameters only if neither the Experiment() node nor the Procedure() node
	 * contain a defined value for the respective parameter.
	 * 
	 * <p>
	 * Covariate factors are entered into the runtime parameters table only if
	 * they are not contained in the argument list of the Experiment() node.
	 * Covariate factors as runtime parameters may also have a set of factor
	 * levels being defined which may be used to allow the user to select the
	 * appropriate factor level from a choice dialog.
	 */
	public void setSessionParametersFromDialog() {
		// System.out.println("ExDesign.setSessionParametersFromDialog()");
		ArrayList pn = new ArrayList();
		ArrayList pv = new ArrayList();
		ArrayList known = new ArrayList();
		ExDesignNode experimentNode = getExDesignTree();
		// Enter Experiment() node parameters if the
		// Experiment() node has no value defined.
		String[] expPars = experimentNode.getParNames();
		if ((expPars != null) && (expPars.length > 0)) {
			for (int i = 0; i < expPars.length; i++) {
				// System.out.println("ExDesign.setSessionParametersFromDialog(): Paremeter "
				// + procPars[i]);
				ExParValue vx = experimentNode.getParValue(expPars[i]);
				// System.out.println("ExDesign.setSessionParametersFromDialog():  Experiment = "
				// + vx.toString());
				if ((vx == null) || vx.isUndefined() || vx.isNotSet()) {
					// System.out.println("ExDesign.setSessionParametersFromDialog(): add "
					// + procPars[i]);
					pn.add(expPars[i]);
					pv.add(null);
				} else {
					known.add(expPars[i]);
				}
			}
		}
		// Now search for covariate factors
		ExDesignNode factorsTree = getFactorsTree();
		if (factorsTree != null) {
			ArrayList factors = factorsTree.getChildrenList();
			if (factors != null) {
				// We have a factors node with children, so search vor
				// covariate factors
				for (int i = 0; i < factors.size(); i++) {
					ExDesignNode covariate = (ExDesignNode) factors.get(i);
					if (covariate.getType() == ExDesignNode.CovariateFactorNode) {
						// This is a covariate factor
						String pnn = covariate.getParNames()[0];
						// System.out.println("ExDesign.setSessionParametersFromDialog():  Covariate Factor "
						// + pnn);
						// System.out.println("ExDesign.setSessionParametersFromDialog():  Enter covariate Factor "
						// + pnn);
						if (known.indexOf(pnn) < 0) {
							String[] lev = null;
							ArrayList levels = covariate.getChildrenList();
							if ((levels != null) && (levels.size() > 0)) {
								// Its levels are the possible values
								lev = new String[levels.size()];
								for (int j = 0; j < levels.size(); j++) {
									ExDesignNode val = (ExDesignNode) levels
											.get(j);
									lev[j] = val.getParValues()[0].getString();
								}
							}
							// Prameter value is unknown
							int k = pn.indexOf(pnn);
							if (k < 0) {
								// Parameter is not yet on the list
								pn.add(pnn);
								pv.add(lev);
							} else {
								// Parameter is on the list, add levels
								pv.set(k, lev);
							}
						} else {
							// Parameter value is already known
						}
					}
				}
			}
		}
		if (pn.size() > 0) {
			// System.out.println("ExDesign.setSessionParametersFromDialog()");
			new RuntimeParsDialog(Base.getFrame(), pn, pv);
		}
	}
	/**
	 * An instance of this thread actually runs the experiment. We have to
	 * create a private thread here for running the experiment since otherwise
	 * we would run in the main event thread whenever the experiment has been
	 * started by an event listener running in the event thread. If the
	 * experiment itself runs in the event thread then response events would no
	 * longer be registered.
	 */
	private class ExRunThread extends Thread {
		/** This object did start the thread. */
		private ExDesignThreadStarter threadStarter;
		/**
		 * Accepts the experimental data collected during an experimental run.
		 * This DataDestination creates all output files.
		 */
		private DataDestination dw;

		/**
		 * Create a thread for running the experiment.
		 * 
		 * @param n
		 *            name of the thread.
		 * @param ts
		 *            a reference of the object which started the experiment.
		 *            This reference is used to signal the started that the
		 *            experiment has been finished.
		 */
		public ExRunThread(String n, ExDesignThreadStarter ts,
				ExDesignProcessor edp, ExDesignDataProcessor dtp,
				DataDestination dw) {
			super(n);
			threadStarter = ts;
			exDesignProcessor = edp;
			dataProcessor = dtp;
			this.dw = dw;
			setPriority(Thread.MAX_PRIORITY);
		}

		/**
		 * This runs the experimental session in 'real' time and signals the
		 * starter that the session has been finished.
		 */
		public void run() {
			stopExRun = false;
			runSession(dw);
			threadStarter.experimentFinished(stopExRun);
			exDesignProcessor = null;
			dataProcessor = null;
		}
	}

	/**
	 * Run the active session(s) of this experimental design. This method is
	 * called from the thread which runs the experiment. It runs in 'real' time.
	 */
	public void runSession(DataDestination dataDestination) {
		// System.out.println("ExDesign.runSession()");
		// exDesignTree.print();
		ExDesignNode exDesignTreeProcedureNode = getProcedureTree();
		if (exDesignTreeProcedureNode == null) {
			return;
		}
		// Create the root and the top level nodes of the data tree
		exDesignDataTree = new ExDesignNode(ExDesignNode.ExperimentNode,
				exDesignTree.getParNames(), exDesignTree.getParValues(), 3);
		ExDesignNode exDesignDataContextTree = null;
		ExDesignNode nd = getContextTree();
		if (nd != null) {
			exDesignDataContextTree = nd.dupTree();
			exDesignDataTree.add(exDesignDataContextTree);
		}
		nd = getFactorsTree();
		if (nd != null) {
			exDesignDataTree.add(nd.dupTree());
		}
		ExDesignNode currentProcedureData = new ExDesignNode(
				ExDesignNode.ProcedureNode,
				exDesignTreeProcedureNode.getParNames(),
				exDesignTreeProcedureNode.getParValues(), 3);
		currentProcedureData.setName(exDesignTreeProcedureNode.getName());
		if (ExPar.JoinDataTrees.getFlag() && (previousData != null)) {
			for (int i = 0; i < previousData.size(); i++) {
				currentProcedureData.add((ExDesignNode) (previousData.get(i)));
			}
		}
		exDesignDataTree.add(currentProcedureData);
		// exDesignDataTree.print();
		ExPar.checkValueStack();
		// Begin of Experiment
		// ---------------------------------------------------------------------------
		// Push parameters of the top level nodes
		exDesignTree.pushArgs();
		exDesignTreeProcedureNode.pushArgs();
		// and run the Procedure node
		exDesignProcessor.startProcedure(exDesignTreeProcedureNode,
				getDisplayList(exDesignTreeProcedureNode.getInstanceName()));
		stopExRun = stopExRun || dataDrivenStop();
		// Create the internal table of factorial conditions. This
		// must be done after the Procedure node has been run (why?) and
		// before the existence of experimental parameters is
		// tested. It is inserted here since the Experiment node
		// arguments might affect the experimental condition table.
		createConditionTable();
		// Now all parameters of our design should be well defined. So
		// we check it.
		if (designTreeError()) {
			return;
		}
		// This sets all parameters derived from SubjectGroup as a covariate
		// factor
		pushCovariateFactors();
		// Prepare the Session loop using information about previous
		// sessions of the same subject
		ArrayList activeSessions = getActiveSessionList();
		if (activeSessions.size() == 0) {
			new ExDesignTreeError("Empty list of sessions for subject "
					+ ExPar.SubjectCode.getString() + "!");
		}
		ExDesignNode currentSession;
		ExDesignNode currentSessionData;
		dataDestination.setDataFileHeader();
		// Begin of Session loop
		// ---------------------------------------------------------------------------
		for (int currentSessionIndex = 0; (currentSessionIndex < activeSessions
				.size())
		// A Session is executed if the ProcedureState is EXECUTE
				&& (ExPar.ProcedureState.getInt() == StateCodes.EXECUTE)
				// and the experimental run has not been stopped
				&& !stopExRun; currentSessionIndex++) {
			Debug.show(Debug.STATE_CTRL,
					"ExDesign.runSession() SessionCounter = "
							+ ExPar.SessionCounter.getInt());
			// Push the arguments of the active Session
			currentSession = (ExDesignNode) activeSessions
					.get(currentSessionIndex);
			currentSession.pushArgs();
			// If we get here, then we have to assume that the Session should be
			// run
			ExPar.SessionState.set(StateCodes.EXECUTE);
			// System.out.println("ExDesign.runSession() PointA");
			// currentSession.print();
			// Check whether the current subject group has to run this session
			// -------------------------------
			if (subjectGroupIsActive()) {
				// Create runtime expansions and block randomizations
				if (runtimeExpansion) {
					// Still to be done:
					// BlockNode expansion
					// Currently there is no expansion for blocks
					// currentSession.expandChildrenList();
					// Multiply blocks if necessary
					currentSession.multiply(ExPar.BlockFactor.getInt());
					if (ExPar.RandomizeBlocks.getInt() != 0)
						currentSession.randomizeChildrenList();
				}
				// showAllPars();
				// runtimeContext.setActive(true);
				// The application starts the session now
				exDesignProcessor.startSession(currentSession,
						getDisplayList(currentSession.getInstanceName()));
				stopExRun = stopExRun || dataDrivenStop();
				// Create session data node
				currentSessionData = new ExDesignNode(ExDesignNode.SessionNode,
						currentSession.getParNames(), null, 10);
				currentSessionData.setName(currentSession.getName());
				// This is done after SessionEnd()
				// currentSessionData.setParValuesToCurrentValues();
				currentProcedureData.add(currentSessionData);
				// exDesignDataTree.print();
				// Prepare the block loop
				ArrayList blockList = currentSession.getChildrenList();
				int nb = blockList.size();
				ExDesignNode currentBlockData;
				// System.out.println("ExDesign.runSession() PointB");
				// System.out.println("ExDesign.runSession() Session contains "
				// + nb + " Blocks");
				// currentSession.print();
				if ((nb > 0) && (ExPar.SkipBoundingBlockDisplays.getFlag())) {
					// Modify the first and last block node type to allow
					// special treatment
					((ExDesignNode) blockList.get(0))
							.addTypeModifier(ExDesignNode.FirstBlock);
					((ExDesignNode) blockList.get(nb - 1))
							.addTypeModifier(ExDesignNode.LastBlock);
				}
				// System.out.println("ExDesign.runSession() PointC");
				// currentSession.print();
				int trialReturnCode = 0;
				AdaptiveControl adaptiveControl = null;
				// Begin of Block loop
				// ------------------------------------------------------------------
				for (int i = 0; (i < nb)
				// A Block is executed if the SessionState is EXECUTE
						&& (ExPar.SessionState.getInt() == StateCodes.EXECUTE)
						// and the experimental run has not been stopped
						&& !stopExRun; i++) {
					Debug.show(Debug.STATE_CTRL,
							"ExDesign.runSession() BlockCounter = "
									+ ExPar.BlockCounter.getInt());
					// Push block arguments
					ExDesignNode currentBlock = (ExDesignNode) blockList.get(i);
					currentBlock.pushArgs();
					// If we get here, then we have to assume that the Block
					// should be run
					ExPar.BlockState.set(StateCodes.EXECUTE);
					// Check whether the current block should be run by
					// subjects with the current subject's group code
					// System.out.println("ExDesign.runSession(): SubjectGroup = "
					// + ExPar.SubjectGroup);
					// System.out.println("ExDesign.runSession(): ActiveSubjectGroups = "
					// + ExPar.ActiveSubjectGroups);
					// Check whether the current subject group executes this
					// bloc
					if (subjectGroupIsActive()) {
						// Expand and randomize this block's trials
						if (runtimeExpansion) {
							currentBlock.expandTrials();
							currentBlock.multiply(ExPar.TrialFactor.getInt());
							// currentBlock.print();
							if (ExPar.RandomizeTrials.getInt() != 0)
								currentBlock.randomizeChildrenList();
							// currentBlock.print();
						}
						// System.out.println("ExDesign.runSession() PointD");
						// currentSession.print();
						clearAdaptiveControlMap();
						// The application now starts the block
						exDesignProcessor.startBlock(currentBlock,
								getDisplayList(currentBlock.getInstanceName()));
						stopExRun = stopExRun || dataDrivenStop();
						// Move data to the data tree
						currentBlockData = new ExDesignNode(
								ExDesignNode.BlockNode,
								currentBlock.getParNames(), null, 10);
						currentBlockData.setName(currentBlock.getName());
						// this is done after BlockEnd()
						// currentBlockData.setParValuesToCurrentValues();
						currentSessionData.add(currentBlockData);
						// exDesignDataTree.print();
						ArrayList trialList = currentBlock.getChildrenList();
						ExDesignNode currentTrialData;
						// Begin of Trial loop
						// ------------------------------------------------------------------------
						for (int j = 0; (j < trialList.size())
								&& (ExPar.BlockState.getInt() == StateCodes.EXECUTE)
								&& !stopExRun; j++) {
							Debug.show(Debug.STATE_CTRL,
									"ExDesign.runSession() TrialCounter = "
											+ ExPar.TrialCounter.getInt());
							ExPar.TrialState.set(StateCodes.EXECUTE);
							// Push the current trial arguments and the
							// operationalization parameters
							ExDesignNode currentTrial = (ExDesignNode) trialList
									.get(j);
							currentTrial.pushArgs();
							pushExtendedFactorPars(currentTrial);
							// System.out.println("1"); currentBlock.print();
							// currentTrial.print();
							// Check whether this trial is to be executed
							adaptiveControl = getAdaptiveControl(currentTrial);
							if (adaptiveControl.execTrial()) {
								// Run the garbage collector before starting a
								// trial
								System.gc();
								// Then execute the trial
								trialReturnCode = exDesignProcessor.runTrial(
										currentTrial,
										getDisplayList(currentTrial
												.getInstanceName()));
								// and fix up the next trial of this adaptive
								// sequence
								adaptiveControl.fixForward(currentTrial);
							}
							stopExRun = stopExRun || dataDrivenStop();
							// System.out.println("2"); currentBlock.print();
							// Send the trial to the data file
							if (ExPar.StoreData.getInt() != 0)
								dataDestination.store(currentTrial);
							currentTrialData = new ExDesignNode(
									ExDesignNode.TrialNode,
									currentTrial.getParNames(), null, 0);
							currentTrialData.setName(currentTrial.getName());
							currentTrialData.setParValuesToCurrentValues();
							currentBlockData.add(currentTrialData);
							// Check the trial return code whether we have to
							// copy the trial
							int trialState = ExPar.TrialState.getInt();
							// System.out.println("ExDesign.runSession() trialState = "
							// + trialState);
							if (trialState == StateCodes.COPY) {
								Debug.show(Debug.DSP_PROPS,
										"ExDesign.runSession(): Copy last trial.");
								ExDesignNode newTrial = currentTrial.dupTree();
								// System.out.println("Trial = " + newTrial);
								trialList.add(j + 1, newTrial);
							}
							// Check the trial return code whether we have to
							// repeat the trial
							if ((((trialReturnCode == StateCodes.ERROR) || (trialState == StateCodes.ERROR)) && ExPar.RepeatErrorTrials
									.getFlag())
									|| (trialState == StateCodes.REPEAT)) {
								Debug.show(Debug.DSP_PROPS,
										"ExDesign.runSession(): Repeat last trial.");
								addTrialDuplicate(currentTrial, trialList, j);
							}
							if (ExPar.DataProcessingEnabled.getFlag())
								dataProcessor.processData(
										getDataDisplayList(currentTrial
												.getInstanceName()),
										currentTrialData);
							// Pop local parameters
							popExtendedFactorPars(currentTrial);
							currentTrial.popArgs();
							// System.out.println("ExDesign.runSession(): after pop");
							// currentTrial.print();
							// System.out.println("5"); currentBlock.print();
							ExPar.TrialCounter
									.set(ExPar.TrialCounter.getInt() + 1);
							// System.out.println("6"); currentBlock.print();
							Debug.show(Debug.STATE_CTRL,
									"ExDesign.runSession() TrialState after Trial = "
											+ ExPar.TrialState.getInt());
							Debug.show(Debug.STATE_CTRL,
									"ExDesign.runSession() BlockState after Trial = "
											+ ExPar.BlockState.getInt());
						} // End of trial loop
							// -----------------------------------------------------------------
						// If necessary compute the results of the
						// adaptive sequences in this block
						if (ExPar.AdaptiveResultComputation.getInt() != AdaptiveResultCodes.NO_RESULTS) {
							computeAdaptiveResults(currentBlockData);
						}
						// Check whether we should stop
						if (!stopExRun) {
							exDesignProcessor.endBlock(currentBlock,
									getDisplayList2(currentBlock
											.getInstanceName()));
							stopExRun = stopExRun || dataDrivenStop();
						}
						currentBlockData.setParValuesToCurrentValues();
						if (ExPar.DataProcessingEnabled.getFlag())
							dataProcessor.processData(
									getDataDisplayList(currentBlock
											.getInstanceName()),
									currentBlockData);
					} // End of subjectGroupIsActive() clause for this Block
						// --------------------------------------
					// Send the block to the data file
					if (ExPar.StoreData.getInt() != 0) {
						dataDestination.store(currentBlock);
					}
					// Pop block arguments
					currentBlock.popArgs();
					ExPar.BlockCounter.set(ExPar.BlockCounter.getInt() + 1);
					Debug.show(Debug.STATE_CTRL,
							"ExDesign.runSession() BlockState after Block = "
									+ ExPar.BlockState.getInt());
					Debug.show(Debug.STATE_CTRL,
							"ExDesign.runSession() SessionState after Block = "
									+ ExPar.SessionState.getInt());
					// Check whether we have to stop the current session
					// if (ExPar.SessionState.getInt() == StateCodes.BREAK) {
					// This stops the current session
					// break;
					// }
				} // End of Block loop
					// --------------------------------------------------------------------------
				if (!stopExRun) {
					exDesignProcessor.endSession(currentSession,
							getDisplayList2(currentSession.getInstanceName()));
					stopExRun = stopExRun || dataDrivenStop();
				}
				currentSessionData.setParValuesToCurrentValues();
				if (ExPar.DataProcessingEnabled.getFlag())
					dataProcessor
							.processData(getDataDisplayList(currentSession
									.getInstanceName()), currentSessionData);
			} // End of subjectGroupIsActive() clause for this Session
				// ----------------------------------------
			// Pop back the node arguments of the active session
			currentSession.popArgs();
			ExPar.SessionCounter.set(ExPar.SessionCounter.getInt() + 1);
			// This ends the current session, others may follow if
			// there are more than a single active sessions.
			Debug.show(Debug.STATE_CTRL,
					"ExDesign.runSession() SessionState after Session = "
							+ ExPar.SessionState.getInt());
			Debug.show(Debug.STATE_CTRL,
					"ExDesign.runSession() ProcedureState after Session = "
							+ ExPar.ProcedureState.getInt());
		} // End of Session loop
		// runtimeContext.setActive(false);
		// System.out.println("ExDesign.runSession() PointE");
		// currentSession.print();
		// Give the application a chance to do finalizations
		if (!stopExRun) {
			exDesignProcessor
					.endProcedure(exDesignTreeProcedureNode,
							getDisplayList2(exDesignTreeProcedureNode
									.getInstanceName()));
			stopExRun = stopExRun || dataDrivenStop();
		}
		currentProcedureData.setParValuesToCurrentValues();
		// Tell the data writer that we are done
		dataDestination.dataComplete(0);
		// Store the data tree
		if (ExPar.StoreDataTree.getFlag()) {
			exDesignDataContextTree.add(sessionAssignmentsGroup());
			dataDestination.storeDataTree(exDesignDataTree);
		}
		if (ExPar.DataProcessingEnabled.getFlag())
			dataProcessor.processData(
					getDataDisplayList(exDesignTreeProcedureNode
							.getInstanceName()), currentProcedureData);
		// This resets all parameters derived from SubjectGroup as a covariate
		// factor
		popCovariateFactors();
		// Pop back the top level node arguments
		exDesignTreeProcedureNode.popArgs();
		exDesignTree.popArgs();
		// End of Experiment
		// ---------------------------------------------------------------------------
		Debug.show(Debug.STATE_CTRL,
				"ExDesign.runSession() ProcedureState after Procedure = "
						+ ExPar.ProcedureState.getInt());
		ExPar.checkValueStack();
	}

	public void runDataSession(ExDesignDataProcessor dataProcessor) {
		// System.out.println("ExDesign.runDataSession()");
		initRuntimeContext();
		dataProcessor.setDataDestination(new DataDestination());
		ExPar.checkValueStack();
		ExDesignNode exDesignTreeProcedureNode = getProcedureTree();
		// Begin of Experiment
		// ---------------------------------------------------------------------------
		exDesignTree.pushArgs();
		exDesignTreeProcedureNode.pushArgs();
		createConditionTable();
		if (designTreeError()) {
			return;
		}
		pushCovariateFactors();
		ArrayList activeSessions = exDesignTreeProcedureNode.getChildrenList();
		ExDesignNode currentSession;
		// Begin of Session loop
		// ---------------------------------------------------------------------------
		for (int currentSessionIndex = 0; (currentSessionIndex < activeSessions
				.size()); currentSessionIndex++) {
			currentSession = (ExDesignNode) activeSessions
					.get(currentSessionIndex);
			currentSession.pushArgs();
			ArrayList blockList = currentSession.getChildrenList();
			int nb = blockList.size();
			// Begin of Block loop
			// ------------------------------------------------------------------
			for (int i = 0; i < nb; i++) {
				ExDesignNode currentBlock = (ExDesignNode) blockList.get(i);
				currentBlock.pushArgs();
				ArrayList trialList = currentBlock.getChildrenList();
				// Begin of Trial loop
				// ------------------------------------------------------------------------
				for (int j = 0; j < trialList.size(); j++) {
					ExDesignNode currentTrial = (ExDesignNode) trialList.get(j);
					currentTrial.pushArgs();
					pushExtendedFactorPars(currentTrial);
					dataProcessor.processData(
							getDataDisplayList(currentTrial.getInstanceName()),
							currentTrial);
					popExtendedFactorPars(currentTrial);
					currentTrial.popArgs();
				} // End of trial loop
					// -----------------------------------------------------------------
				dataProcessor.processData(
						getDataDisplayList(currentBlock.getInstanceName()),
						currentBlock);
				currentBlock.popArgs();
			} // End of Block loop
				// --------------------------------------------------------------------------
			dataProcessor.processData(
					getDataDisplayList(currentSession.getInstanceName()),
					currentSession);
			currentSession.popArgs();
		} // End of Session loop
		dataProcessor
				.processData(getDataDisplayList(exDesignTreeProcedureNode
						.getInstanceName()), exDesignTreeProcedureNode);
		popCovariateFactors();
		exDesignTreeProcedureNode.popArgs();
		exDesignTree.popArgs();
		// End of Experiment
		// ---------------------------------------------------------------------------
		ExPar.checkValueStack();
	}

	/**
	 * Check whether the experimental run has to be stopped as a result of data
	 * collection.
	 * 
	 * @return true if the experimental run should be stopped.
	 */
	private boolean dataDrivenStop() {
		return ExPar.ProcedureState.getInt() == StateCodes.STOP
				|| ExPar.SessionState.getInt() == StateCodes.STOP
				|| ExPar.BlockState.getInt() == StateCodes.STOP
				|| ExPar.TrialState.getInt() == StateCodes.STOP;
	}

	/**
	 * Replay a design file which already contains data and send the data to the
	 * given data writer.
	 */
	public void replay(DataDestination dataDestination) {
		// System.out.println("ExDesign.replay()");
		ExDesignProcessor exDesignProcessor = new ProtocolExDesignProcessor(
				false);
		ExDesignNode exDesignTreeProcedureNode = getProcedureTree();
		// Begin of Experiment
		// ---------------------------------------------------------------------------
		exDesignTreeProcedureNode.pushArgs();
		exDesignTree.pushArgs();
		exDesignProcessor.startProcedure(exDesignTreeProcedureNode,
				getDisplayList(exDesignTreeProcedureNode.getInstanceName()));
		createConditionTable();
		// if (designTreeError()) return;
		pushCovariateFactors();
		ArrayList activeSessions = getActiveSessionList();
		if (activeSessions.size() == 0) {
			new ExDesignTreeError("Empty list of sessions for subject "
					+ ExPar.SubjectCode.getString() + "!");
		}
		ExDesignNode currentSession;
		dataDestination.setDataFileHeader();
		// Begin of Session loop
		// ---------------------------------------------------------------------------
		for (int currentSessionIndex = 0; currentSessionIndex < activeSessions
				.size(); currentSessionIndex++) {
			currentSession = (ExDesignNode) activeSessions
					.get(currentSessionIndex);
			currentSession.pushArgs();
			exDesignProcessor.startSession(currentSession,
					getDisplayList(currentSession.getInstanceName()));
			ArrayList blockList = currentSession.getChildrenList();
			int nb = blockList.size();
			// Begin of Block loop
			// ------------------------------------------------------------------
			for (int i = 0; i < nb; i++) {
				ExDesignNode currentBlock = (ExDesignNode) blockList.get(i);
				currentBlock.pushArgs();
				exDesignProcessor.startBlock(currentBlock,
						getDisplayList(currentBlock.getInstanceName()));
				ArrayList trialList = currentBlock.getChildrenList();
				// Start the trial loop
				for (int j = 0; j < trialList.size(); j++) {
					ExDesignNode currentTrial = (ExDesignNode) trialList.get(j);
					currentTrial.pushArgs();
					pushExtendedFactorPars(currentTrial);
					exDesignProcessor.runTrial(currentTrial,
							getDisplayList(currentTrial.getInstanceName()));
					if (ExPar.StoreData.getInt() != 0)
						dataDestination.store(currentTrial);
					popExtendedFactorPars(currentTrial);
					currentTrial.popArgs();
				} // End of trial loop
				exDesignProcessor.endBlock(currentBlock,
						getDisplayList2(currentBlock.getInstanceName()));
				dataDestination.store(currentBlock);
				currentBlock.popArgs();
			} // End of block loop
			exDesignProcessor.endSession(currentSession,
					getDisplayList2(currentSession.getInstanceName()));
			currentSession.popArgs();
		} // End of session loop
		exDesignProcessor.endProcedure(exDesignTreeProcedureNode,
				getDisplayList2(exDesignTreeProcedureNode.getInstanceName()));
		dataDestination.dataComplete(0);
		popCovariateFactors();
		exDesignTree.popArgs();
		exDesignTreeProcedureNode.popArgs();
	}

	/**
	 * Set the runtime parameters of the current factor level of every covariate
	 * factor.
	 */
	public void pushCovariateFactors() {
		ArrayList cvn = getCovariateFactorNames();
		if (cvn != null) {
			int n = cvn.size();
			for (int i = 0; i < n; i++) {
				String m = (String) (cvn.get(i));
				// System.out.println("ExDesign.pushCovariateFactors(): pushing factor "
				// + m);
				ExDesignNode cvf = getCovariateFactorLevelNode(m, ExPar.get(m)
						.getValue());
				if (cvf != null)
					cvf.pushArgs();
			}
		}
	}

	/**
	 * Restore the runtime parameters of the current factor level of every
	 * covariate factor.
	 */
	public void popCovariateFactors() {
		ArrayList cvn = getCovariateFactorNames();
		if (cvn != null) {
			int n = cvn.size();
			for (int i = n - 1; i >= 0; i--) {
				String m = (String) (cvn.get(i));
				// System.out.println("ExDesign.popCovariateFactors(): popping factor "
				// + m);
				ExDesignNode cvf = getCovariateFactorLevelNode(m, ExPar.get(m)
						.getValue());
				if (cvf != null)
					cvf.popArgs();
			}
		}
	}

	private ExDesignNode getCovariateFactorLevelNode(String factor,
			ExParValue level) {
		ExDesignNode factorsTree = getFactorsTree();
		if (factorsTree != null) {
			ArrayList factors = factorsTree.getChildrenList();
			if (factors != null) {
				for (int i = 0; i < factors.size(); i++) {
					ExDesignNode covariate = (ExDesignNode) factors.get(i);
					if (covariate.getType() == ExDesignNode.CovariateFactorNode) {
						String pnn = covariate.getParNames()[0];
						if (pnn.equals(factor)) {
							String[] lev = null;
							ArrayList levels = covariate.getChildrenList();
							if ((levels != null) && (levels.size() > 0)) {
								for (int j = 0; j < levels.size(); j++) {
									ExDesignNode val = (ExDesignNode) levels
											.get(j);
									if (level.equals(val.getParValues()[0])) {
										return val;
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Get the DisplayList instance whose name is given as an argument.
	 * 
	 * @param n
	 *            the name of the ExDesignNode.
	 * @return the requested DisplayList object or null if it does not exist.
	 */
	public DisplayList getDisplayList(String n) {
		return (DisplayList) displayListMap.get(n);
	}

	public HashMap getDisplayListMap() {
		return displayListMap;
	}

	/**
	 * Get the End-version of the DisplayList node whose name is given as an
	 * argument. The argument is the name of a Session or Block node. The
	 * DisplayList returned will be the corresponding SessionEnd or BlockEnd
	 * list. If no special instance End-node exists then the default End-node is
	 * returned.
	 * 
	 * @param n
	 *            the name of the ExDesignNode. This must be a Session or Block
	 *            node.
	 * @return the requested DisplayList object or null if it does not exist.
	 */
	private DisplayList getDisplayList2(String n) {
		DisplayList r = null;
		String n1 = null;
		String n2 = null;
		int m = 0;
		// Figure out the node type and the corresponding End node type
		if (n.startsWith("Procedure")) {
			n1 = "ProcedureEnd";
			m = "Procedure".length();
		} else if (n.startsWith("Session")) {
			n1 = "SessionEnd";
			m = "Session".length();
		} else if (n.startsWith("Block")) {
			n1 = "BlockEnd";
			m = "Block".length();
		}
		// Check whether this node has an instance name and try to
		// find the corresponding DisplayList object.
		if (n.length() > m) {
			r = getDisplayList(n1 + n.substring(m));
		}
		// If the DisplayList object is not yet defined then we use
		// the default.
		if (r == null) {
			r = getDisplayList(n1);
		}
		return r;
	}

	public DisplayList getDataDisplayList(String n) {
		DisplayList r = null;
		String n1 = null;
		String n2 = null;
		int m = 0;
		// Figure out the node type and the corresponding Data node type
		if (n.startsWith("Experiment")) {
			n1 = "ExperimentData";
			m = "Experiment".length();
		} else if (n.startsWith("Procedure")) {
			n1 = "ProcedureData";
			m = "Procedure".length();
		} else if (n.startsWith("Session")) {
			n1 = "SessionData";
			m = "Session".length();
		} else if (n.startsWith("Block")) {
			n1 = "BlockData";
			m = "Block".length();
		} else if (n.startsWith("Trial")) {
			n1 = "TrialData";
			m = "Trial".length();
		}
		// Check whether this node has an instance name and try to
		// find the corresponding DisplayList object.
		if (n.length() > m) {
			r = getDisplayList(n1 + n.substring(m));
		}
		// If the DisplayList object is not yet defined then we use
		// the default.
		if (r == null) {
			r = getDisplayList(n1);
		}
		return r;
	}

	/**
	 * Check whether the currently active subject group list contains the
	 * current subject's group code. If yes, then the current subject should run
	 * the currently active node.
	 * 
	 * @return true if the parameter ExPar.ActiveSubjectGroups is undefined or
	 *         if current value of ExPar.SubjectGroup is contained in the array
	 *         ExPar.ActiveSubjectGroups and thus the current subject belongs to
	 *         a subject group which should run the next node. It returns fals
	 *         if the parameter ActiveSubjectGroups is non-empty but does not
	 *         contain the current value of SubjectGroup.
	 */
	private boolean subjectGroupIsActive() {
		boolean retval = true;
		String[] activeGroups = ExPar.ActiveSubjectGroups.getStringArray();
		if (StringExt.nonEmpty(activeGroups[0])) {
			retval = (StringExt.indexOf(ExPar.SubjectGroup.getString(),
					activeGroups) >= 0);
		}
		return retval;
	}

	/**
	 * Find the active sessions for the current subject's run and return a list
	 * of clones of the active session nodes. Active sessions are defined by the
	 * parameters RemainingSessionGroup and RemainingSessionRuns. For the first
	 * experimental run of a subject these parameters get the same value as
	 * SessionGroup and SessionRuns respectively. If multiple runs are executed
	 * for a single subject then the parameters RemainingSessionGroup and
	 * RemainingSessionRuns describe the sessions which have not yet been run.
	 * SessionGroup is the list of session numbers which have to be run for
	 * every subject. SessionRuns has as many entries as there are runs for a
	 * single subject. Every single entry describes how many of the first
	 * entries of SessionGroup have to be executed at the respective run.
	 * 
	 * <p>
	 * If SessionGroup is undefined (SessionGroup == 0), then the method behaves
	 * as if SessionGroup were an array containing all defined sessions like [1,
	 * 2, ...] .
	 * 
	 * <p>
	 * If SessionRuns is undefined (SessionRuns = 0), then the method behaves as
	 * if the value of SessionRuns were equal to the number of sessions being
	 * defined.
	 * 
	 * <p>
	 * If RemainingSessionRuns has a negative value, then no sessions are
	 * created. This is the signal that the current subject already has run all
	 * sessions required.
	 * 
	 * <p>
	 * As a side effect the current value of RemainingSessionGroup and
	 * RemainingSessionRuns are adjusted for the next session at the end of this
	 * method.
	 * 
	 * @return a list of clones of the active session nodes or null if
	 *         RemainingSessionRuns is negative when this method is called.
	 */
	private ArrayList getActiveSessionList() {
		Debug.show(Debug.MULTISESSION, "ExDesign.getActiveSessionList()");
		Debug.show(Debug.MULTISESSION,
				"  SubjectGroup = " + ExPar.SubjectGroup.toString());
		Debug.show(Debug.MULTISESSION,
				"  SessionGroup = " + ExPar.SessionGroup.toString());
		Debug.show(Debug.MULTISESSION,
				"  SessionRuns = " + ExPar.SessionRuns.toString());
		Debug.show(Debug.MULTISESSION, "  RemainingSessionGroup = "
				+ ExPar.RemainingSessionGroup.toString());
		Debug.show(Debug.MULTISESSION, "  RemainingSessionRuns = "
				+ ExPar.RemainingSessionRuns.toString());
		// The array list of sessions defined in the design file
		ArrayList procChildrenList = exDesignTree.getFirstChildOfType(
				ExDesignNode.ProcedureNode).getChildrenList();
		int nss = procChildrenList.size();
		Debug.show(Debug.MULTISESSION, "  design file contains " + nss
				+ " Session() nodes.");
		// The output list
		ArrayList activeSessions = new ArrayList();
		int[] remSessionGroup = ExPar.RemainingSessionGroup.getIntArray();
		int org_nrsg = remSessionGroup.length;
		int[] sessionGroup = null;
		if (remSessionGroup[0] <= 0) {
			Debug.show(Debug.MULTISESSION,
					"  'RemainingSessionGroup' is not defined. Use 'SessionGroup'.");
			remSessionGroup = ExPar.SessionGroup.getIntArray();
			org_nrsg = remSessionGroup.length;
		}
		if (remSessionGroup[0] <= 0) {
			Debug.show(
					Debug.MULTISESSION,
					"  'Both RemainingSessionGroup' and 'SessionGroup' are undefined. Use all defined sessions.");
			org_nrsg = nss;
			remSessionGroup = new int[nss];
			for (int i = 0; i < nss; i++) {
				remSessionGroup[i] = i + 1;
			}
		}
		int[] sr = ExPar.RemainingSessionRuns.getIntArray();
		if (sr[0] == 0) {
			Debug.show(Debug.MULTISESSION,
					"  'RemainingSessionRuns' is not defined. Use 'SessionRuns'.");
			sr = ExPar.SessionRuns.getIntArray();
		}
		int ns = sr[0];
		if (ns > 0) {
			Debug.show(Debug.MULTISESSION, "  Run the first " + ns
					+ " sessions.");
			if (sr.length == 1) {
				ExPar.RemainingSessionRuns.set(-1);
			} else {
				int[] rsr = new int[sr.length - 1];
				for (int i = 0; i < rsr.length; i++) {
					rsr[i] = sr[i + 1];
				}
				ExPar.RemainingSessionRuns.set(rsr);
			}
		} else if (ns == 0) {
			Debug.show(Debug.MULTISESSION, "  Run all remaining sessions.");
			ns = remSessionGroup.length;
		} else if (ns < 0) {
			Debug.show(Debug.MULTISESSION, "  No more sessions to run.");
			return activeSessions;
		}
		sessionGroup = new int[ns];
		for (int i = 0; (i < ns) && (i < remSessionGroup.length); i++) {
			sessionGroup[i] = remSessionGroup[i];
		}
		int nusg = 0;
		Debug.show(Debug.MULTISESSION, "  Create list of active sessions:");
		for (int i = 0; i < sessionGroup.length; i++) {
			int si = sessionGroup[i] - 1;
			if (si < procChildrenList.size()) {
				Debug.show(Debug.MULTISESSION, "    add session " + (si + 1));
				activeSessions.add(((ExDesignNode) procChildrenList.get(si))
						.clone());
				nusg++;
			}
		}
		// Compute the number of sessions in the remaining session
		// group and update RemainingSessionGroup
		int k = org_nrsg - nusg;
		if (k > 0) {
			int[] rsg = new int[k];
			for (int i = 0; i < k; i++) {
				rsg[i] = remSessionGroup[nusg + i];
			}
			ExPar.RemainingSessionGroup.set(rsg);
		} else {
			ExPar.RemainingSessionGroup.set(0);
		}
		Debug.show(Debug.MULTISESSION, "  next RemainingSessionGroup = "
				+ ExPar.RemainingSessionGroup.toString());
		Debug.show(Debug.MULTISESSION, "  next RemainingSessionRuns = "
				+ ExPar.RemainingSessionRuns.toString());
		return activeSessions;
	}

	/**
	 * Get an adaptive control object for the current trial. The trial's
	 * adaptive sequence is identified by the parameter AdaptiveSequenceID. If
	 * there already is an adaptive control object for this trial in the current
	 * adaptive control map then it is returned. If there is none then a new
	 * adaptive control object for this trial is created and entered into the
	 * adaptive control map. The adaptive control map must be cleared at the
	 * start of a block.
	 */
	private AdaptiveControl getAdaptiveControl(ExDesignNode currentTrial) {
		Object key = new Integer(ExPar.AdaptiveSequenceID.getInt());
		AdaptiveControl adc = (AdaptiveControl) adaptiveControlMap.get(key);
		if (adc == null) {
			adc = new AdaptiveControl(currentTrial);
			adaptiveControlMap.put(key, adc);
			Debug.show(Debug.ADAPTIVE,
					"ExDesign() Entered adaptive sequence ID = " + key);
		}
		return adc;
	}

	/**
	 * Walk through all adaptive control objects in the current adaptive control
	 * map and compute the results of the adaptive procedure.
	 * 
	 * @param blockData
	 *            the data tree for the current block.
	 */
	private void computeAdaptiveResults(ExDesignNode blockData) {
		for (Iterator adcm = adaptiveControlMap.values().iterator(); adcm
				.hasNext();) {
			((AdaptiveControl) adcm.next()).computeResult(blockData
					.getChildrenList());
		}
	}

	/**
	 * Clear the adaptive control map which contains the adaptive control
	 * objects for the adaptive sequences mixed within a block.
	 */
	private void clearAdaptiveControlMap() {
		adaptiveControlMap.clear();
	}

	/**
	 * Add a duplicate of the trial contained at index i in the given list to
	 * the section following index i. This is used when a trial has been
	 * executed and returns a value which is not TRIAL_OK and the
	 * RepeatErrorTrials flag is set.
	 */
	private void addTrialDuplicate(ExDesignNode trial, ArrayList trialList,
			int i) {
		ExDesignNode dup = trial.dupTree();
		// System.out.println("ExDesign.addTrialDuplicate():"); dup.print();
		int p = i + 1;
		if (ExPar.RandomizeTrials.getFlag()) {
			int n = trialList.size() - i;
			Randomizer rnd = new Randomizer();
			p = rnd.nextInt(n);
		}
		trialList.add(p, dup);
	}

	/**
	 * This method my be used to stop run time session execution in an orderly
	 * manner. It stops only after a stimulus display has been finished. Note
	 * that we take care to also have the parameter push/pop operations be in a
	 * proper state when the run is stopped.
	 */
	public void stop() {
		Debug.show(Debug.EVENTS, "ExDesign.stop()");
		stopExRun = true;
		if (exDesignProcessor != null) {
			exDesignProcessor.stop();
		}
	}

	/**
	 * Check the complete design tree for errors at run time. These are
	 * undefined parameter names, argument list errors, and invalid expression
	 * errors.
	 * 
	 * @return true if there is an error found.
	 */
	private boolean designTreeError() {
		boolean r = false;
		int k = 0;
		Iterator it = exDesignTree.iterator();
		while (it.hasNext()) {
			ExDesignNode e = (ExDesignNode) it.next();
			// System.out.println("ExDesign.designTreeError(): Checking " + e);
			if (e.requiresParChecking() && e.hasParNames()) {
				String[] n = e.getParNames();
				for (int i = 0; i < n.length; i++) {
					// System.out.println("ExDesign.designTreeError() Checking parameter "
					// + n[i]);
					String nm = e.isAssignment() ? e.getInstanceName() : n[i];
					if (!ExPar.contains(nm)) {
						int p = nm.lastIndexOf('.');
						if (p > 0) {
							String cn = nm.substring(0, p);
							String pn = nm.substring(p + 1);
							ExPar xp = ((ExPar) (ExPar.get("de.pxlab.pxl", cn,
									pn, false)));
							if (xp != null) {
								// This is a static class field
							} else {
								new ParameterNameError(e.getInstanceName()
										+ " at input line " + e.getTokenLine()
										+ " contains invalid parameter: " + nm);
								k++;
							}
						} else {
							k++;
						}
					}
				}
			}
			if (e.isProcedureUnit()) {
				// System.out.println("ExDesign.designTreeError(): Checking " +
				// e);
				if (argumentError(e))
					k++;
			}
			if (e.requiresValueChecking()) {
				ExParValue[] v = e.getParValues();
				if (v != null) {
					for (int i = 0; i < v.length; i++) {
						// System.out.println("Checking parameter " +
						// n[i].getName());
						ExParValue x = v[i].getValue();
						if (x == null) {
							new ParameterValueError(e.getInstanceName()
									+ " at input line " + e.getTokenLine()
									+ " contains undefined paramer value.");
							k++;
						}
					}
				}
			}
		}
		if (k > 0) {
			System.out.println("ExDesign.designTreeError(): " + k + " Errors.");
			r = true;
		}
		return (r);
	}

	/**
	 * Check whether there is an error in a procedure node's argument list.
	 */
	private boolean argumentError(ExDesignNode node) {
		boolean r = false;
		String[] pars = node.getParNames();
		ExParValue[] p = node.getParValues();
		if ((pars == null) && (p == null)) {
			return r;
		}
		int plng = (p == null) ? 0 : p.length;
		int parslng = (pars == null) ? 0 : pars.length;
		if (plng != parslng) {
			r = true;
			StringBuffer err = new StringBuffer(node.getInstanceName()
					+ " at input line " + node.getTokenLine());
			if (((pars == null) && (p != null)) || (plng > parslng)) {
				err.append(" has too many arguments.");
			} else {
				err.append(" has missing arguments.");
			}
			if (plng == 0)
				System.out.println("no argument values");
			for (int i = 0; i < plng; i++)
				System.out.println("p[" + i + "] = " + p[i]);
			if (parslng == 0)
				System.out.println("no argument parameters");
			for (int i = 0; i < parslng; i++)
				System.out.println("pars[" + i + "] = " + pars[i]);
			// System.out.println("ExDesign.argumentError(): " + err);
			new ExDesignTreeError(err.toString());
		}
		return (r);
	}
	private int nFactors = 0;
	private int nFactorPars = 0;
	private int nExtFactorPars = 0;
	private String[] factorNames = null;
	private String[] factorParNames = null;
	private String[] extFactorParNames = null;
	private ExDesignNode conditionTable = null;
	/**
	 * These are the lists of factor names which may later be used to retrieve
	 * factor values for statistical analyses.
	 */
	private ArrayList randomFactorNames;
	private ArrayList covariateFactorNames;
	private ArrayList independentFactorNames;
	private ArrayList dependentFactorNames;

	/**
	 * The conditon table is a list of factor level combinations and their
	 * associated experimental parameter values.
	 */
	public void createConditionTable() {
		ExDesignNode factorsTree = getFactorsTree();
		if (factorsTree == null) {
			// This design does not contain a factors tree so return.
			return;
		}
		randomFactorNames = new ArrayList();
		covariateFactorNames = new ArrayList();
		independentFactorNames = new ArrayList();
		dependentFactorNames = new ArrayList();
		// The first step is to create a tree of factor levels such
		// that every level of every factor is combined with every
		// level of every other factor. This step implicitly also
		// creates the array of factor names and the array of
		// experimental parameters which are connected to the factors.
		ExDesignNode factorLevels = new ExDesignNode(
				ExDesignNode.FactorLevelNode, 1);
		ArrayList fp = new ArrayList();
		ArrayList fpp = new ArrayList();
		// Children of the factors tree are factors
		for (Enumeration enfs = factorsTree.children(); enfs.hasMoreElements();) {
			ExDesignNode factor = (ExDesignNode) enfs.nextElement();
			if (factor.isFactor()) {
				if (factor.isRandomFactor()) {
					randomFactorNames.add(factor.getParNames()[0]);
				}
				if (factor.isCovariateFactor()) {
					covariateFactorNames.add(factor.getParNames()[0]);
				}
				// We are only interested in the independent factors
				if (factor.isIndependentFactor()) {
					// This is an independent factor. So add its name
					// to the independent factors list and its
					// parameters to the respective parameter list
					String[] fpnn = factor.getParNames();
					fp.add(fpnn[0]);
					independentFactorNames.add(fpnn[0]);
					for (int i = 1; i < fpnn.length; i++)
						fpp.add(fpnn[i]);
					// Now iterate through the current factorLevels
					// and append this factor's levels to any node
					// which does not yet have a child
					for (Iterator ft = factorLevels.iterator(); ft.hasNext();) {
						ExDesignNode n = (ExDesignNode) ft.next();
						if (!n.hasChildren()) {
							for (Enumeration fc = factor.children(); fc
									.hasMoreElements();) {
								// We need to add a clone in order to
								// preserve paths to different parents.
								n.add((ExDesignNode) (((ExDesignNode) fc
										.nextElement()).clone()));
							}
						}
					}
				}
				if (factor.isDependentFactor()) {
					dependentFactorNames.add(factor.getParNames()[0]);
				}
			}
		}
		// This created the factor levels tree
		if (Debug.isActive(Debug.FACTORS)) {
			factorLevels.print();
		}
		Debug.show(Debug.FACTORS, "");
		// We also have the independent factors array and the
		// corresponding experimental parameters as far as these are
		// contained in the factors definition
		nFactors = fp.size();
		factorNames = new String[nFactors];
		for (int i = 0; i < nFactors; i++) {
			factorNames[i] = (String) fp.get(i);
			Debug.show(Debug.FACTORS, "Independent factor: " + factorNames[i]);
		}
		Debug.show(Debug.FACTORS, "");
		// Now check whether there is a condition table defined
		ExDesignNode conTable = factorsTree
				.getFirstChildOfType(ExDesignNode.ConditionTableNode);
		if (conTable != null) {
			// There is a condition table defined in the design
			// tree. We add the condition table's experimental
			// parameters to our list of operationalization
			// parameters.
			String[] cpn = conTable.getParNames();
			for (int i = 0; i < cpn.length; i++) {
				if (StringExt.indexOf(cpn[i], factorNames) < 0) {
					fpp.add(cpn[i]);
					Debug.show(Debug.FACTORS,
							"Condition table operationalization parameter: "
									+ cpn[i]);
				}
			}
			Debug.show(Debug.FACTORS, "");
		}
		// We now can create the array of experimental parameters
		// which operationalize the experimental factors.
		nFactorPars = fpp.size();
		factorParNames = new String[nFactorPars];
		for (int i = 0; i < nFactorPars; i++) {
			factorParNames[i] = (String) fpp.get(i);
			Debug.show(Debug.FACTORS, "Final operationalization parameter: "
					+ factorParNames[i]);
		}
		Debug.show(Debug.FACTORS, "");
		// If we have the condition table defined in the design file
		// then we are almost done
		if (conTable != null) {
			// conTable.print();
			conditionTable = conTable;
			createExtendedFactorParNames();
			return;
		}
		// We now create the array of parameter names for independent
		// factors and the corresponding experimental parameters. To
		// do this we walk down to the first factor levels node which
		// does not have children and retrieve all parameter names we meet.
		ArrayList pn = new ArrayList();
		int lastFactorIdx = 0;
		for (Iterator ft = factorLevels.iterator(); ft.hasNext();) {
			ExDesignNode n = (ExDesignNode) ft.next();
			String[] pna = n.getParNames();
			if (pna != null) {
				// The first parameter name is the factor name
				pn.add(lastFactorIdx++, pna[0]);
				// The following parameter names are the corresponding
				// experimental parameters
				for (int i = 1; i < pna.length; i++) {
					pn.add(pna[i]);
				}
				// If we are at a childless node then we have
				// collected all parameter names and stop
				if (!n.hasChildren()) {
					break;
				}
			}
		}
		// This gave us the parameter name array for the condition
		// table node.
		String[] condParNames = new String[pn.size()];
		for (int i = 0; i < condParNames.length; i++) {
			condParNames[i] = (String) pn.get(i);
			Debug.show(Debug.FACTORS, "Final condition parameter: "
					+ condParNames[i]);
		}
		Debug.show(Debug.FACTORS, "");
		conTable = new ExDesignNode(ExDesignNode.ConditionTableNode,
				condParNames, null, 1);
		// Now we create a path to every childless leaf node of the factor
		// levels tree. Each of these paths corresponds to a possible
		// experimental condition.
		for (Iterator ft = factorLevels.iterator(); ft.hasNext();) {
			ExDesignNode n = (ExDesignNode) ft.next();
			// System.out.println("Iterator: " + n);
			if (!n.hasChildren()) {
				// This is a childless node so create a path to it
				// n.print();
				ExDesignNode[] path = n.pathTo();
				ArrayList pv = new ArrayList();
				lastFactorIdx = 0;
				// We collect the factor level and parameter values
				// for every node in the path
				for (int i = 0; i < path.length; i++) {
					// Don't use evaluated values here
					ExParValue[] pva = path[i].getParValues();
					if (pva != null) {
						// The first parameter name is the factor name
						pv.add(lastFactorIdx++, pva[0]);
						// The following parameter names are the corresponding
						// experimental parameters
						for (int ii = 1; ii < pva.length; ii++) {
							pv.add(pva[ii]);
						}
					}
				}
				// This gave us the parameter array for a single
				// possible experimental condition
				ExParValue[] cpv = new ExParValue[pv.size()];
				for (int i = 0; i < cpv.length; i++)
					cpv[i] = (ExParValue) pv.get(i);
				ExDesignNode condition = new ExDesignNode(
						ExDesignNode.ConditionNode, condParNames, cpv, 0);
				// We add it to the condition table node
				conTable.add(condition);
				if (Debug.isActive(Debug.FACTORS)) {
					condition.print();
				}
			}
		}
		Debug.show(Debug.FACTORS, "");
		// System.out.println("ExDesign.createConditionTable(): ");
		// conTable.print();
		conditionTable = conTable;
		createExtendedFactorParNames();
		createFactorialDataFormat();
	}

	/**
	 * Create a table of all possible factor level combinations for the
	 * independent factors and their factor levels for the given factors tree.
	 * This method is used by the class de.pxlab.pxl.design.FactorDialog.
	 * 
	 * @param factorsTree
	 *            a node of type FactorsNode which has the independent factors
	 *            whose levels are to be combined as its children.
	 * @return a node of type ConditionTableNode which has the list of
	 *         independent factors as its arguments and has all possible factor
	 *         level combinations of the respective factors as its children.
	 */
	public static ExDesignNode factorLevelCombinations(ExDesignNode factorsTree) {
		if ((factorsTree == null) || (!factorsTree.isFactors())) {
			return null;
		}
		// The first step is to create a tree of factor levels such
		// that every level of every factor is combined with every
		// level of every other factor. This step implicitly also
		// creates the array of factor names and the array of
		// experimental parameters which are connected to the factors.
		ExDesignNode factorLevels = new ExDesignNode(
				ExDesignNode.FactorLevelNode, 1);
		ArrayList fp = new ArrayList();
		ArrayList fpp = new ArrayList();
		// Children of the factors tree are factors
		for (Enumeration enfs = factorsTree.children(); enfs.hasMoreElements();) {
			ExDesignNode factor = (ExDesignNode) enfs.nextElement();
			// We are only interested in the independent factors
			if (factor.isIndependentFactor()) {
				// This is an independent factor. So add its name
				// to the independent factors list and its
				// parameters to the respective parameter list
				String[] fpnn = factor.getParNames();
				fp.add(fpnn[0]);
				for (int i = 1; i < fpnn.length; i++)
					fpp.add(fpnn[i]);
				// Now iterate through the current factorLevels
				// and append this factor's levels to any node
				// which does not yet have a child
				for (Iterator ft = factorLevels.iterator(); ft.hasNext();) {
					ExDesignNode n = (ExDesignNode) ft.next();
					if (!n.hasChildren()) {
						for (Enumeration fc = factor.children(); fc
								.hasMoreElements();) {
							// We need to add a clone in order to
							// preserve paths to different parents.
							n.add((ExDesignNode) (((ExDesignNode) fc
									.nextElement()).clone()));
						}
					}
				}
			}
		}
		// This created the factor levels tree
		// factorLevels.print();
		// We also have the independent factors array and the
		// corresponding experimental parameters as far as these are
		// contained in the factors definition
		int nFactors = fp.size();
		String[] factorNames = new String[nFactors];
		for (int i = 0; i < nFactors; i++) {
			factorNames[i] = (String) fp.get(i);
		}
		// We now can create the array of experimental parameters
		// which operationalize the experimental factors.
		int nFactorPars = fpp.size();
		String[] factorParNames = new String[nFactorPars];
		for (int i = 0; i < nFactorPars; i++) {
			factorParNames[i] = (String) fpp.get(i);
		}
		// We now create the array of parameter names for independent
		// factors and the corresponding experimental parameters. To
		// do this we walk down to the first factor levels node which
		// does not have children and retrieve all parameter names we meet.
		ArrayList pn = new ArrayList();
		int lastFactorIdx = 0;
		for (Iterator ft = factorLevels.iterator(); ft.hasNext();) {
			ExDesignNode n = (ExDesignNode) ft.next();
			String[] pna = n.getParNames();
			if (pna != null) {
				// The first parameter name is the factor name
				pn.add(lastFactorIdx++, pna[0]);
				// The following parameter names are the corresponding
				// experimental parameters
				for (int i = 1; i < pna.length; i++) {
					pn.add(pna[i]);
				}
				// If we are at a childless node then we have
				// collected all parameter names and stop
				if (!n.hasChildren()) {
					break;
				}
			}
		}
		// This gave us the parameter name array for the condition
		// table node.
		String[] condParNames = new String[pn.size()];
		for (int i = 0; i < condParNames.length; i++)
			condParNames[i] = (String) pn.get(i);
		ExDesignNode conTable = new ExDesignNode(
				ExDesignNode.ConditionTableNode, condParNames, null, 1);
		conTable.setTypeModifier(nFactors);
		// Now we create a path to childless leaf node of the factor
		// levels tree. Each of these paths corresponds to a possible
		// experimental condition.
		for (Iterator ft = factorLevels.iterator(); ft.hasNext();) {
			ExDesignNode n = (ExDesignNode) ft.next();
			// System.out.println("Iterator: " + n);
			if (!n.hasChildren()) {
				// This is a childless node so create a ptha to it
				// n.print();
				ExDesignNode[] path = n.pathTo();
				ArrayList pv = new ArrayList();
				lastFactorIdx = 0;
				// We collect the factor level and parameter values
				// for every node in the path
				for (int i = 0; i < path.length; i++) {
					// Don't use evaluated values here
					ExParValue[] pva = path[i].getParValues();
					if (pva != null) {
						// The first parameter name is the factor name
						pv.add(lastFactorIdx++, pva[0]);
						// The following parameter names are the corresponding
						// experimental parameters
						for (int ii = 1; ii < pva.length; ii++) {
							pv.add(pva[ii]);
						}
					}
				}
				// This gave us the parameter array for a single
				// possible experimental condition
				ExParValue[] cpv = new ExParValue[pv.size()];
				for (int i = 0; i < cpv.length; i++)
					cpv[i] = (ExParValue) pv.get(i);
				ExDesignNode condition = new ExDesignNode(
						ExDesignNode.ConditionNode, condParNames, cpv, 0);
				// We add it to the condition table node
				conTable.add(condition);
				// condition.print();
			}
		}
		// System.out.println("ExDesign.factorLevelCombinations():");
		// conTable.print();
		return conTable;
	}

	/**
	 * Get the names of all independent and covariate factors which currently
	 * are defined by this design.
	 */
	public String[] getCurrentlyDefinedIndependentFactorNames() {
		ExDesignNode factorsTree = getFactorsTree();
		if (factorsTree == null) {
			// This design does not contain a factors tree so return.
			return null;
		}
		return getCurrentlyDefinedIndependentFactorNames(factorsTree);
	}

	/**
	 * Get the names of all independent and covariate factors which currently
	 * are defined by this design.
	 */
	public static String[] getCurrentlyDefinedIndependentFactorNames(
			ExDesignNode factorsTree) {
		ArrayList factorNames = new ArrayList();
		for (Enumeration enfs = factorsTree.children(); enfs.hasMoreElements();) {
			ExDesignNode factor = (ExDesignNode) enfs.nextElement();
			if (factor.isIndependentFactor() || factor.isCovariateFactor()) {
				factorNames.add(factor.getParNames()[0]);
			}
		}
		return StringExt.stringArrayOfList(factorNames);
	}

	private ExDesignNode getConditionTable() {
		return (conditionTable);
		// return(getFactorsTree().getFirstChildOfType(ExDesignNode.ConditionTableNode));
	}

	/**
	 * Get the list of random factor names defined by this design. Note that
	 * this method returns valid results only if it is called after the design
	 * tree has been executed.
	 * 
	 * @return an ArrayList object which contains the list of names which have
	 *         been defined as random factors.
	 */
	public ArrayList getRandomFactorNames() {
		return randomFactorNames;
	}

	/**
	 * Get the list of covariate factor names defined by this design. Note that
	 * this method returns valid results only if it is called after the design
	 * tree has been executed.
	 * 
	 * @return an ArrayList object which contains the list of names which have
	 *         been defined as covariate factors.
	 */
	public ArrayList getCovariateFactorNames() {
		return covariateFactorNames;
	}

	/**
	 * Get the list of independent factor names defined by this design. Note
	 * that this method returns valid results only if it is called after the
	 * design tree has been executed.
	 * 
	 * @return an ArrayList object which contains the list of names which have
	 *         been defined as independent factors.
	 */
	public ArrayList getIndependentFactorNames() {
		return independentFactorNames;
	}

	/**
	 * Get the list of dependent factor names defined by this design. Note that
	 * this method returns valid results only if it is called after the design
	 * tree has been executed.
	 * 
	 * @return an ArrayList object which contains the list of names which have
	 *         been defined as dependent factors.
	 */
	public ArrayList getDependentFactorNames() {
		return dependentFactorNames;
	}

	/**
	 * Find that condition node which contains the experimental condition
	 * realized in the given trial.
	 */
	private ExDesignNode getConditionForTrial(ExDesignNode trial) {
		// System.out.println("ExDesign.getConditionForTrial(): " + trial);
		if (conditionTable == null)
			return (null);
		// Get the factor level values from the trial arguments list
		ExParValue[] trialPars = new ExParValue[nFactors];
		for (int i = 0; i < nFactors; i++) {
			trialPars[i] = trial.getParValue(factorNames[i]);
		}
		for (Enumeration ct = conditionTable.children(); ct.hasMoreElements();) {
			ExDesignNode condition = (ExDesignNode) ct.nextElement();
			// System.out.println("   testing " + condition);
			boolean found = true;
			for (int i = 0; i < nFactors; i++) {
				found = found
						&& (trialPars[i].equals(condition
								.getParValue(factorNames[i])));
			}
			if (found) {
				// System.out.println("ExDesign.getConditionForTrial(): found "
				// + condition);
				return (condition);
			} else {
			}
		}
		// System.out.println("ExDesign.getConditionForTrial(): no condition found.");
		return (null);
	}

	/**
	 * Find those experimental parameters of a condition node which are not
	 * trial parameters. These have to be pushed when entering a trial which
	 * represents a certain condition.
	 */
	private void createExtendedFactorParNames() {
		// System.out.println("ExDesign.createExtendedFactorParNames()");
		ArrayList tp = new ArrayList();
		for (int i = 0; i < getContextTree().getChildCount(); i++) {
			if (((ExDesignNode) getContextTree().getChildAt(i)).getName()
					.startsWith("Trial")) {
				String[] t = getTrialParNames(((ExDesignNode) getContextTree()
						.getChildAt(i)).getName());
				tp.add(t);
			}
		}
		ArrayList pn = new ArrayList();
		for (int i = 0; i < nFactorPars; i++) {
			for (int j = 0; j < tp.size(); j++) {
				if (StringExt.indexOf(factorParNames[i], (String[]) tp.get(j)) < 0) {
					pn.add(factorParNames[i]);
				}
			}
		}
		nExtFactorPars = pn.size();
		extFactorParNames = new String[nExtFactorPars];
		// System.out.println("ExDesign.createExtendedFactorParNames(): Non-Trial condition parameters: ");
		for (int i = 0; i < nExtFactorPars; i++) {
			extFactorParNames[i] = (String) pn.get(i);
			// System.out.println("ExDesign.createExtendedFactorParNames():   "
			// + extFactorParNames[i]);
			Debug.show(Debug.FACTORS, "Extended Factor parameter: "
					+ extFactorParNames[i]);
		}
		Debug.show(Debug.FACTORS, "");
	}

	/** Push all those. */
	private void pushExtendedFactorPars(ExDesignNode trial) {
		// System.out.println("ExDesign.pushExtendedFactorPars()");
		if (nExtFactorPars == 0)
			return;
		ExDesignNode condition = getConditionForTrial(trial);
		if (condition == null)
			return;
		for (int i = 0; i < nExtFactorPars; i++) {
			// System.out.println("ExDesign.pushExtendedFactorPars(): " +
			// extFactorParNames[i]);
			// System.out.println("ExDesign.pushExtendedFactorPars(): to value "
			// + condition.getParValue(extFactorParNames[i]));
			ExPar.get(extFactorParNames[i]).push(
					condition.getParValue(extFactorParNames[i]));
		}
	}

	/** Push all those */
	private void popExtendedFactorPars(ExDesignNode trial) {
		// System.out.println("ExDesign.popExtendedFactorPars()");
		if (nExtFactorPars == 0)
			return;
		ExDesignNode condition = getConditionForTrial(trial);
		if (condition == null)
			return;
		for (int i = 0; i < nExtFactorPars; i++) {
			// System.out.println("Popping parameter " + extFactorParNames[i]);
			ExPar.get(extFactorParNames[i]).pop();
		}
	}

	private void createFactorialDataFormat() {
		ArrayList a = new ArrayList(30);
		a.addAll(getRandomFactorNames());
		a.addAll(getCovariateFactorNames());
		a.addAll(getIndependentFactorNames());
		a.addAll(getDependentFactorNames());
		String[] fd = StringExt.stringArrayOfList(a);
		ExPar.FactorialDataFormat.set(fd);
		Debug.show(Debug.FACTORS, "FactorialDataFormat = "
				+ ExPar.FactorialDataFormat);
	}

	public String[] getControlFactors() {
		ArrayList a = new ArrayList(30);
		a.addAll(getRandomFactorNames());
		a.addAll(getCovariateFactorNames());
		a.addAll(getIndependentFactorNames());
		return StringExt.stringArrayOfList(a);
	}

	public String[] getDependentFactors() {
		return StringExt.stringArrayOfList(getDependentFactorNames());
	}

	/**
	 * Return an id code which is unique for all data trees derived from this
	 * design.
	 * 
	 * @return a code which is unique for all data tree files which have been
	 *         derived from this experimental design.
	 */
	public int idCode() {
		int cnc = 0;
		ExDesignNode nd = getContextTree();
		for (Iterator it = nd.iterator(); it.hasNext();) {
			ExDesignNode n = (ExDesignNode) (it.next());
			if (!n.isAssignmentGroup()) {
				cnc++;
			}
		}
		nd = getFactorsTree();
		if (nd != null) {
			for (Iterator it = nd.iterator(); it.hasNext();) {
				Object n = it.next();
				cnc++;
			}
		}
		// System.out.print("ExDesign.idCode(): " + cnc + " -> ");
		char[] a = ExPar.ExperimentName.getString().toCharArray();
		for (int i = 0; i < a.length; i++) {
			cnc += 1000 * a[i];
		}
		// System.out.println(cnc);
		return cnc;
	}

	protected void showAllPars() {
		String[] ap = ExPar.getAllParNames();
		int n = ap.length;
		for (int i = 0; i < n; i++) {
			System.out.println(ap[i]);
		}
	}

	/**
	 * Store the design tree to the given file.
	 * 
	 * @param filePath
	 *            full filename and path where to store the tree. If this is a
	 *            null string then the tree is printed to the System.out stream.
	 */
	public void store(String filePath) {
		exDesignTree.store(filePath);
	}

	/**
	 * Export this experimental design to a Java source file which can be
	 * compiled to generate the same ExDesign as this one.
	 * 
	 * @param filePath
	 *            the name to be used for the generated file and class.
	 */
	public void exportJava(String filePath) {
		initRuntimeContext();
		if (filePath == null) {
			filePath = ExPar.JavaClassName.getString() + ".java";
		}
		File f = new File(filePath);
		try {
			Debug.show(Debug.FILES, "ExDesign.exportJava(): Open " + filePath);
			PrintWriter ps = new PrintWriter(new FileOutputStream(f));
			exDesignTree.printJavaSource(f.getName(), ps);
			ps.close();
			Debug.show(Debug.FILES, "ExDesign.exportJava(): Closed " + filePath);
		} catch (IOException ioex) {
			System.out.println("ExDesign.exportJava(): Error writing file "
					+ filePath);
		}
	}

	/**
	 * Export this experimental design to a HTML source file.
	 * 
	 * @param filePath
	 *            the name to be used for the generated file.
	 */
	public void exportHTML(String filePath) {
		initRuntimeContext();
		if (filePath == null) {
			Debug.show(Debug.FILES,
					"ExDesign.exportHTML(): Printing to System.out");
			exDesignTree.printHTML(new PrintWriter(System.out), null);
		} else {
			File f = new File(filePath);
			try {
				Debug.show(Debug.FILES, "ExDesign.exportHTML(): Open "
						+ filePath);
				String nl = System.getProperty("line.separator");
				PrintWriter ps = new PrintWriter(new FileOutputStream(f));
				ps.println("<HTML>" + nl + "<TITLE> PXLab Experiment: "
						+ getFileName() + " </TITLE>");
				ps.println("<?php include(\"dir_header.php\"); ?>");
				ps.println("<BODY>" + nl
						+ "<FONT face=\"Verdana\",\"Helvetica\">" + nl);
				exDesignTree.printHTML(ps, null);
				ps.println(nl + "</FONT></BODY></HTML>" + nl);
				ps.close();
				Debug.show(Debug.FILES, "ExDesign.exportHTML(): Closed "
						+ filePath);
			} catch (IOException ioex) {
				System.out.println("ExDesign.export(): Error writing file "
						+ filePath);
			}
		}
	}

	/**
	 * Print a protocol of the parameter values of the given node with respect
	 * to the given parameter table.
	 * 
	 * @param e
	 *            the design node whose parameter values should be printed.
	 */
	protected void showNodeParValues(ExDesignNode e) {
		System.out.println(e.toString());
		String[] a = e.getParNames();
		for (int i = 0; i < a.length; i++) {
			System.out.println("   " + a[i] + " = "
					+ ExPar.get(a[i]).toString());
		}
	}

	public void print() {
		exDesignTree.print();
	}

	/**
	 * Print a protocol of all parameter values in the design. The parameter
	 * values printed represent the parameter states at run time.
	 */
	/*
	 * public void showParValues() { Iterator it = exDesignTree.iterator();
	 * while (it.hasNext()) { ExDesignNode e = (ExDesignNode)it.next();
	 * switch(e.getType()) { case ExDesignNode.ExperimentNode: case
	 * ExDesignNode.SessionNode: case ExDesignNode.BlockNode: if
	 * (e.isStartNode()) { e.pushArgs(); showNodeParValues(e); } else {
	 * showNodeParValues(e); e.popArgs(); } break; case ExDesignNode.TrialNode:
	 * // Note that Trials don't get false beginOfNode // parameters
	 * e.pushArgs(); showNodeParValues(e); e.popArgs(); break; // 'Parameter' is
	 * an Assignment to a new variable case ExDesignNode.NewParamNode: case
	 * ExDesignNode.AssignmentNode: break; } } }
	 */
	/**
	 * Create a list of assignment nodes for session dependent parameters. These
	 * are inserted into data trees.
	 */
	protected ExDesignNode sessionAssignmentsGroup() {
		ExDesignNode rta = new ExDesignNode(ExDesignNode.AssignmentGroupNode,
				30);
		String nl = System.getProperty("line.separator");
		rta.setComment("// Session dependent assignments" + nl);
		ArrayList cvn = getCovariateFactorNames();
		if (cvn != null) {
			int n = cvn.size();
			for (int i = 0; i < n; i++) {
				String m = (String) (cvn.get(i));
				rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
						ExDesignNode.ExplicitAssignment, m, ExPar.get(m)
								.getValue(), 0));
			}
		}
		ExDesignNode procTree = getProcedureTree();
		String[] pn = procTree.getParNames();
		if ((pn != null) && (pn.length > 0)) {
			for (int i = 0; i < pn.length; i++) {
				rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
						ExDesignNode.ExplicitAssignment, pn[i], ExPar
								.get(pn[i]).getValue(), 0));
			}
		}
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment, "SubjectCode",
				ExPar.SubjectCode.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment, "RemainingSessionGroup",
				ExPar.RemainingSessionGroup.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment, "RemainingSessionRuns",
				ExPar.RemainingSessionRuns.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment, "Date", ExPar.Date.getValue(),
				0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment, "PXLabVersion",
				ExPar.PXLabVersion.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment, "PrimaryScreen.ColorDevice",
				PrimaryScreen.ColorDevice.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment,
				"PrimaryScreen.ColorDeviceDACRange",
				PrimaryScreen.ColorDeviceDACRange.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment, "PrimaryScreen.RedPrimary",
				PrimaryScreen.RedPrimary.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment, "PrimaryScreen.GreenPrimary",
				PrimaryScreen.GreenPrimary.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment, "PrimaryScreen.BluePrimary",
				PrimaryScreen.BluePrimary.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment, "PrimaryScreen.RedGamma",
				PrimaryScreen.RedGamma.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment, "PrimaryScreen.GreenGamma",
				PrimaryScreen.GreenGamma.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment, "PrimaryScreen.BlueGamma",
				PrimaryScreen.BlueGamma.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment, "SecondaryScreen.ColorDevice",
				SecondaryScreen.ColorDevice.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment,
				"SecondaryScreen.ColorDeviceDACRange",
				SecondaryScreen.ColorDeviceDACRange.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment, "SecondaryScreen.RedPrimary",
				SecondaryScreen.RedPrimary.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment,
				"SecondaryScreen.GreenPrimary", SecondaryScreen.GreenPrimary
						.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment, "SecondaryScreen.BluePrimary",
				SecondaryScreen.BluePrimary.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment, "SecondaryScreen.RedGamma",
				SecondaryScreen.RedGamma.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment, "SecondaryScreen.GreenGamma",
				SecondaryScreen.GreenGamma.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment, "SecondaryScreen.BlueGamma",
				SecondaryScreen.BlueGamma.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment, "DeviceWhitePoint",
				ExPar.DeviceWhitePoint.getValue(), 0));
		rta.add(new ExDesignNode(ExDesignNode.AssignmentNode,
				ExDesignNode.ExplicitAssignment, "CIEWhitePoint",
				ExPar.CIEWhitePoint.getValue(), 0));
		// System.out.println("ExDesign.sessionAssignmentsGroup() Created node:");
		// rta.print();
		return rta;
	}

	/**
	 * Set the runtime expansion flag. If this flag is true then nodes are
	 * expanded, multiplied and randomized at runtime. The flag must be set to
	 * false by postprocessors of design trees.
	 */
	public void setRuntimeExpansion(boolean state) {
		runtimeExpansion = state;
	}

	/**
	 * Create the default experimental design tree which is based on the default
	 * ExPar values.
	 */
	private void setDefaultExDesign() {
		fileName = null;
		exDesignTree = new DefaultExDesignTree();
	}

	/**
	 * Set this experimental design's runtime assignments to the given string.
	 * 
	 * @param ra
	 *            a string containing a sequence of colon separated parameter
	 *            assignments to be executed at runtime.
	 */
	public void setCommandLineAssignments(String ra) {
		addCommandLineAssignmentsGroup(ra);
		initRuntimeContext();
	}

	/** Return the root node of this ExDesign's design tree. */
	public ExDesignNode getExDesignTree() {
		return (exDesignTree);
	}

	/**
	 * Get the root node of the data tree. Note that the data tree does only
	 * exist after the design tree has been executed.
	 */
	public ExDesignNode getExDesignDataTree() {
		return exDesignDataTree;
	}

	/**
	 * Return the name of the design file which defined this design.
	 */
	public String getFileName() {
		return (fileName);
	}

	/**
	 * Get this design's context tree.
	 * 
	 * @return the root node of this design's context tree.
	 */
	public ExDesignNode getContextTree() {
		return (exDesignTree.getFirstChildOfType(ExDesignNode.ContextNode));
	}

	/**
	 * Get this design's factors tree.
	 * 
	 * @return the root node of this design's factors tree.
	 */
	public ExDesignNode getFactorsTree() {
		return (exDesignTree.getFirstChildOfType(ExDesignNode.FactorsNode));
	}

	/**
	 * Get this design's procedure tree.
	 * 
	 * @return the root node of this design's procedure tree.
	 */
	public ExDesignNode getProcedureTree() {
		return (exDesignTree.getFirstChildOfType(ExDesignNode.ProcedureNode));
	}

	/**
	 * Get this design's session parameter names.
	 * 
	 * @param n
	 *            the name of the required session
	 * @return the array of session parameter names of this design.
	 */
	public String[] getSessionParNames(String n) {
		return getContextNodeParNames(n);
	}

	/**
	 * Get this design's block parameter names.
	 * 
	 * @param n
	 *            the name of the required block
	 * @return the array of block parameter names of this design.
	 */
	public String[] getBlockParNames(String n) {
		return getContextNodeParNames(n);
	}

	/**
	 * Get this design's trial parameter names.
	 * 
	 * @param n
	 *            the name of the required trial
	 * @return the array of trial parameter names of this design.
	 */
	public String[] getTrialParNames(String n) {
		return getContextNodeParNames(n);
	}

	/**
	 * Get this design's parameter names for the given procedure unit.
	 * 
	 * @param n
	 *            the name of the procedure unit
	 * @return the array of parameter names of this procedure unit.
	 */
	public String[] getParNames(String n) {
		return getContextNodeParNames(n);
	}

	/**
	 * Get the parameter names for a node of the given type.
	 * 
	 * @param name
	 *            the name of the node whose parameter names are requested.
	 * @return the array of parameter names for a node of the given type of this
	 *         design.
	 */
	private String[] getContextNodeParNames(String name) {
		return getContextTree().getFirstChildOfName(name).getParNames();
	}

	public void setProcedureNode(ExDesignNode p) {
		ArrayList childs = exDesignTree.getChildrenList();
		int n = childs.size();
		for (int k = 0; k < n; k++) {
			ExDesignNode node = (ExDesignNode) childs.get(k);
			if (node.getType() == ExDesignNode.ProcedureNode) {
				ExDesignNode pp = (ExDesignNode) childs.remove(k);
				p.setParent((ExDesignNode) pp.getParent());
				childs.add(k, p);
				Object[] path = { exDesignTree };
				structureChanged(this, path);
				break;
			}
		}
		// exDesignTree.print();
	}

	/**
	 * Create a new runtime procedure unit. The number of runtime arguments are
	 * preserved but all argument values are set to unknown. Default children
	 * are also created if necessary.
	 * 
	 * @param type
	 *            the type of the procedure unit to be created. This may be a
	 *            trial, block, or session type.
	 * @param name
	 *            the name of the kind of procedure unit to be created.
	 */
	public ExDesignNode getNewProcedureUnit(int type, String name) {
		ExDesignNode context = getContextTree();
		if (type == ExDesignNode.TrialNode) {
			String[] p = getTrialParNames(name);
			ExParValue[] v = null;
			if ((p != null) && (p.length > 0)) {
				v = new ExParValue[p.length];
				for (int i = 0; i < p.length; i++) {
					v[i] = new ExParValueUndefined();
				}
			}
			ExDesignNode trl = new ExDesignNode(ExDesignNode.TrialNode, p, v, 0);
			trl.setName(name);
			return (trl);
		}
		if (type == ExDesignNode.BlockNode) {
			String[] p = getBlockParNames(name);
			ExParValue[] v = null;
			if ((p != null) && (p.length > 0)) {
				v = new ExParValue[p.length];
				for (int i = 0; i < p.length; i++) {
					v[i] = new ExParValueUndefined();
				}
			}
			ExDesignNode blk = new ExDesignNode(ExDesignNode.BlockNode, p, v, 1);
			blk.setName(name);
			return (blk);
		}
		if (type == ExDesignNode.SessionNode) {
			String[] p = getSessionParNames(name);
			ExParValue[] v = null;
			if ((p != null) && (p.length > 0)) {
				v = new ExParValue[p.length];
				for (int i = 0; i < p.length; i++) {
					v[i] = new ExParValueUndefined();
				}
			}
			ExDesignNode ses = new ExDesignNode(ExDesignNode.SessionNode, p, v,
					1);
			ses.setName(name);
			return (ses);
		}
		return null;
	}

	/**
	 * Editable experimental designs may use this method to signal a change in
	 * the design tree's structure.
	 */
	public void structureChanged(Object src, Object[] path) {
	}
}
