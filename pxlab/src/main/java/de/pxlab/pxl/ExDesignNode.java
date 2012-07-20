package de.pxlab.pxl;

import java.io.*;
import java.util.*;

import javax.swing.tree.TreeNode;
import javax.swing.tree.MutableTreeNode;

import de.pxlab.util.*;

/**
 * This class describes experimental design elements which are nodes of an
 * experimental design tree. Here is the experimental design tree node
 * structure:
 * 
 * <pre>
 * 
 * ExperimentNode
 *    |
 *    |--- ContextNode
 *    |       |
 *    |       |--- AssignmentGroupNode
 *    |       |       |
 *    |       |       |--- AssignmentNode, NewParamAssignmentNode,
 *    |       |            AdjustableParamAssignmentNode
 *    |       |
 *    |       |--- ProcedureStartDisplayNode, ProcedureEndDisplayNode,
 *    |       |    SessionStartDisplayNode, SessionEndDisplayNode,
 *    |       |    BlockStartDisplayNode, BlockEndDisplayNode, 
 *    |       |    TrialDisplayNode
 *    |       |       |
 *    |       |       |--- DisplayNode
 *    |       |               |
 *    |       |               |--- AssignmentNode,
 *    |       |                    NewParamAssignmentNode,
 *    |       |                    AdjustableParamAssignmentNode
 *    |       |
 *    |       |--- ExperimentDataDisplayNode, ProcedureDataDisplayNode, 
 *    |            SessionDataDisplayNode, BlockDataDisplayNode, 
 *    |            TrialDataDisplayNode
 *    |               |
 *    |               |--- DataDisplayNode
 *    |                       |
 *    |                       |--- AssignmentNode,
 *    |
 *    |--- FactorsNode
 *    |       |
 *    |       |--- RandomFactorNode, CovariateFactorNode, IndependentFactorNode, 
 *    |       |    DependentFactorNode
 *    |       |       |
 *    |       |       |--- FactorLevelNode
 *    |       |
 *    |       |--- ConditionTableNode
 *    |               |
 *    |               |--- ConditionNode
 *    |
 *    |--- ProcedureNode
 *            |
 *            |--- SessionNode
 *                    |
 *                    |--- BlockNode
 *                            |
 *                            |--- TrialNode
 * </pre>
 * 
 * @author H. Irtel
 * @version 0.7.5
 */
/*
 * 02/26/01 added AdaptiveParamAssignmentNode, AdjustableParamAssignmentNode
 * 02/26/01 added type modifiers 02/27/01 introduced Context and Procedure nodes
 * 03/05/01 introduced ParameterGroup nodes 03/14/01 restrict tree iterator to
 * the initial node's subtree
 * 
 * 07/11/01 replaced multiplyTrials() by multiply() which works for blocks and
 * trials.
 * 
 * 07/11/01 fixed copying bug in method dupArrayOfExParValues() which did not
 * copy ExParValue objects correctly.
 * 
 * 08/16/01 moved store() into this class
 * 
 * 08/27/01 added setChildrenList(ArrayList childrenList);
 * 
 * 09/09/01 allow set/getTypeModifier(). This is used for nodes of type
 * ConditionTableNode in order to set the typeModifier to the number of factors
 * in the condition table's argument list.
 * 
 * 12/31/01 added HTML export
 * 
 * 01/16/02 fixed bug in isDisplayList()
 * 
 * 06/20/02 allow multiple procedure unit nodes.
 * 
 * 06/27/02 added method getTypeName() (er)
 * 
 * 08/20/02 modifications for using full instance names
 * 
 * 08/21/02 in print(): Ignore assignments which are not explicitly defined
 * 
 * 11/04/02 removed previous change since full output is required.
 * 
 * 11/15/02 fixed bug when adding assignments of adjustable parameters
 * 
 * 02/04/03 modified printHTML() to not use PHP output.
 * 
 * 03/10/04 added RandomFactorNode
 * 
 * 2004/12/11 some more error checking in push() and pop()
 * 
 * 2005/01/26 call setName() by default when creating an AssignmentNode
 * 
 * 2005/01/28 modified print() such that those children of a node which are
 * assignments with undefined parameter values are not printed.
 * 
 * 2005/06/02 fixed serious bug int pushArgs()
 * 
 * 2005/09/28 made printHTML print only necessary assignments.
 * 
 * 2006/02/17 fixed Java source output.
 */
public class ExDesignNode extends ExDesignNodeSupport implements TreeNode, // MutableTreeNode,
		Cloneable {
	/** This stores the type of the design node. */
	private int type;
	/**
	 * This is a binary pattern which modifies the type of the design node. This
	 * is used for the following purposes:
	 * 
	 * <p>
	 * For nodes of type ConditionTableNode the typeModifier contains the number
	 * of factors in the condition table's argument list.
	 * 
	 * <p>
	 * We mark the first and last block of a session with FirstBlock and
	 * LastBlock.
	 * 
	 * <p>
	 * Indicates the type of an independent factor node: WithinFactor,
	 * BetweenFactor, or RandomFactor.
	 * 
	 * <p>
	 * Indicates that an assignment node is explicitly defined in the design
	 * file.
	 * 
	 * <p>
	 */
	private int typeModifier;
	/**
	 * The name of this node. Generally this is derived from the type or the
	 * class name using an optional instance modifier as postfix.
	 */
	private String name;
	/**
	 * This array contains the names of the experimental parameters of this
	 * node.
	 */
	private String[] parNames = null;
	/**
	 * This array contains the values of the experimental parameters of this
	 * node.
	 */
	private ExParValue[] parValues = null;
	/**
	 * Experimental design nodes of type Experiment, Session, and Block have a
	 * list of children which itself also are experimental design nodes.
	 */
	private ArrayList childrenList = null;
	/**
	 * Every experimental design node also has a parent node. Only the
	 * Experiment root does not have a parent.
	 */
	private ExDesignNode parent = null;
	/**
	 * We store the line and column number of the token which generated this
	 * node in order to be able to report semantic errors later.
	 */
	private int tokenLine, tokenColumn;
	/**
	 * This is the comment which immediately precedes this node in the design
	 * file. This variable is null if there was no comment.
	 */
	private String precedingComment = null;

	/**
	 * Set the comment field of this node. If the comment field is non-empty
	 * then the argument is added to the current comment with a new-line
	 * character inserted between successive comments.
	 */
	public void setComment(String s) {
		if (precedingComment != null) {
			String nl = System.getProperty("line.separator");
			precedingComment = precedingComment + nl + s;
		} else {
			precedingComment = s;
		}
	}

	/**
	 * Creates an experimental design node and defines the type, the parameter
	 * names and values of the design node, and its initial children list size.
	 * The array of parameter values is created by duplicating its argument
	 * array.
	 */
	public ExDesignNode(int t, String[] n, ExParValue[] p, int k) {
		setType(t);
		parNames = n;
		if (p != null)
			parValues = dupArrayOfExParValues(p);
		if (k > 0)
			childrenList = new ArrayList(k);
		Debug.show(Debug.CREATE_NODE, nodeConstructorString());
	}

	/**
	 * Creates a typed node with a single entry in the parameter array. It is
	 * used by Assignment and Parameter nodes only, has no children and its
	 * parameter array has only a single entry.
	 */
	public ExDesignNode(int t, String n, ExParValue p, int k) {
		setType(t);
		if (t == AssignmentNode) {
			setName(n);
		}
		parNames = new String[1];
		parNames[0] = n;
		if (p != null) {
			parValues = new ExParValue[1];
			parValues[0] = (ExParValue) p.clone();
		}
		if (k > 0)
			childrenList = new ArrayList(k);
		Debug.show(Debug.CREATE_NODE, nodeConstructorString());
	}

	/**
	 * Creates a typed node with a single entry in the parameter array. It is
	 * used by Assignment and Parameter nodes only, has no children and its
	 * parameter array has only a single entry.
	 */
	public ExDesignNode(int t, int tm, String n, ExParValue p, int k) {
		this(t, n, p, k);
		addTypeModifier(tm);
	}

	/**
	 * Creates a node which does not yet have parameter values but is known to
	 * get children. This is the case for Experiment nodes.
	 */
	public ExDesignNode(int t, int k) {
		setType(t);
		if (k > 0)
			childrenList = new ArrayList(k);
		Debug.show(Debug.CREATE_NODE, nodeConstructorString());
	}

	/**
	 * Create a copy of this object.
	 * 
	 * @return a copy of this <code>ExDesignNode</code> object.
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new RuntimeException("");
		}
	}

	/**
	 * Create a clone of this node with a newly allocated array of parameter
	 * values.
	 */
	public ExDesignNode dupNode() {
		// System.out.println("ExDesignNode.dupNode(): " + this);
		// print();
		ExDesignNode dup = (ExDesignNode) clone();
		// System.out.println("ExDesignNode.dupNode(): dup = " + dup);
		if (parValues != null) {
			dup.parValues = dupArrayOfExParValues(parValues);
		}
		return (dup);
	}

	/**
	 * Create an almost deep clone of this node. The clone is not complete since
	 * the parameter name array is not copied. All other substructures are
	 * duplicated.
	 */
	public ExDesignNode dupTree() {
		// System.out.println("ExDesignNode.dupTree(): " + this);
		// print();
		ExDesignNode dup = dupNode();
		// System.out.println("ExDesignNode.dupTree(): dup = " + dup);
		if (childrenList != null) {
			int n = childrenList.size();
			dup.childrenList = new ArrayList(n);
			for (int i = 0; i < n; i++) {
				ExDesignNode dupChild = ((ExDesignNode) childrenList.get(i))
						.dupTree();
				dup.add(dupChild);
			}
		}
		return (dup);
	}

	public void destroy() {
		int n = childrenList.size();
		for (int i = 0; i < n; i++)
			((ExDesignNode) childrenList.get(i)).destroy();
		childrenList.clear();
	}

	/** Add a child node to this design node. */
	public void add(ExDesignNode t) {
		t.setParent(this);
		if (childrenList == null)
			childrenList = new ArrayList();
		childrenList.add(t);
		// t.describe("Adding node:");
		Debug.show(Debug.CREATE_NODE, "Adding child " + t.getName() + " to "
				+ this.getName());
		// Debug.show(Debug.CREATE_NODE, "  Node has " + childrenList.size() +
		// " children now.");
		// print();
	}

	/** Exchange the children at index aa and bb. */
	public void exchangeChildren(int aa, int bb) {
		int a, b;
		if (aa < bb) {
			a = aa;
			b = bb;
		} else {
			a = bb;
			b = aa;
		}
		if ((a == b) || (childrenList == null) || (b >= childrenList.size()))
			return;
		Object xa = childrenList.remove(a);
		if (b > (a + 1)) {
			Object xb = childrenList.remove(b);
			childrenList.add(a, xb);
		}
		childrenList.add(b, xa);
	}

	/** Set this node's type. */
	public void setType(int t) {
		type = t;
		if (!(isDisplayList() || isDisplay() || isDataDisplay()
				|| isAssignment() || isProcedureUnit()))
			setName(getTypeName());
	}

	/** Get the type of this experimental design node. */
	public int getType() {
		return (type);
	}

	/** Get the name of this node's type */
	public String getTypeName() {
		return typeName[type];
	}

	/**
	 * Set the name of this node. The name of a node is the node's type or class
	 * name with an optional appendix added by the user in order to identify
	 * this instance of a type or class against other instances of the same type
	 * or class. A colon separates the type or class name from the instance
	 * appendix:
	 * <p>
	 * class_name[:instance_modifier].
	 */
	public void setName(String n) {
		name = n;
	}

	/**
	 * Get the name of this node. The name of a node is the node's type or class
	 * name with an appendix added by the user in order to identify this
	 * instance of a type or class against other instances of the same type or
	 * class. A colon separates the type or class name from the instance
	 * appendix.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the full runtime instance name of this node. The full runtime
	 * instance name of a node is that name which must be entered into the
	 * runtime symbol table to uniquely identify the node. Note that in most
	 * cases the instance name is identical to the node name. Exceptions are
	 * assignment parameters and display objects. Assignments are possible only
	 * within AssignmentGroup nodes and Display nodes. In both cases the
	 * instance name is built by adding an instance prefix to the node name. The
	 * instance prefix for display objects is the display list name and the
	 * instance prefix for the assignment parameter is the instance name of the
	 * display object which it belongs to. Assignments to global parameters are
	 * contained within AssignmentGroup nodes and do not get an instance prefix.
	 */
	public String getInstanceName() {
		String s = getName();
		if (isAssignment()) {
			if (parent.isDisplay() || parent.isDataDisplay()) {
				s = parent.getInstanceName() + "." + s;
				// System.out.println("ExDesignNode.getInstanceName(): Display.Parameter: "
				// + s);
			} else {
				// this is an assignment in an AssignmentGroup node
				// System.out.println("ExDesignNode.getInstanceName(): AssignmentGroup.Parameter: "
				// + parent.getInstanceName() + "." + s);
			}
		} else if (isDisplay() || isDataDisplay()) {
			s = parent.getInstanceName() + "." + s;
			// System.out.println("ExDesignNode.getInstanceName(): DisplayList.Display: "
			// + s);
		} else {
			// System.out.println("ExDesignNode.getInstanceName(): Node: " + s);
		}
		return s;
	}

	/** Modify this node's type. */
	public void addTypeModifier(int t) {
		typeModifier = typeModifier | t;
	}

	/** Modify this node's type. */
	public void setTypeModifier(int t) {
		typeModifier = t;
	}

	/** Remove the given type modifier from this node's type. */
	public void removeTypeModifier(int t) {
		typeModifier = typeModifier & ~t;
	}

	/** Get the type of this experimental design node. */
	public int getTypeModifier() {
		return (typeModifier);
	}

	/** Get the type of this experimental design node. */
	public boolean containsTypeModifier(int t) {
		return ((typeModifier & t) != 0);
	}

	/**
	 * Returns true if this node requires parameter name checking of its
	 * arguments (NOT its children names). Nodes which do not require parameter
	 * name checking may contain parameter names which are not contained in the
	 * global parameter table.
	 */
	public boolean requiresParChecking() {
		int typ = getType();
		return ((typ == AssignmentNode) || (typ == NewParamAssignmentNode)
				|| (typ == AdjustableParamAssignmentNode)
				|| (typ == ExperimentNode) || (typ == ContextNode)
				|| (typ == AssignmentGroupNode) || (typ == FactorsNode)
				|| (typ == RandomFactorNode) || (typ == IndependentFactorNode)
				|| (typ == DependentFactorNode) || (typ == CovariateFactorNode)
				|| (typ == ConditionTableNode)
				|| (typ == ProcedureStartDisplayNode)
				|| (typ == SessionStartDisplayNode)
				|| (typ == BlockStartDisplayNode) || (typ == TrialDisplayNode));
	}

	/**
	 * Returns true if this node requires parameter value checking of its
	 * arguments.
	 */
	public boolean requiresValueChecking() {
		int typ = getType();
		return (/*
				 * (typ == AssignmentNode) || (typ == NewParamAssignmentNode) ||
				 * (typ == AdjustableParamAssignmentNode) ||
				 */(typ == FactorLevelNode) || (typ == ConditionNode)
				|| (typ == ProcedureNode) || (typ == SessionNode)
				|| (typ == BlockNode) || (typ == TrialNode));
	}

	public boolean hasParNames() {
		return (parNames != null) && (parNames.length > 0);
	}

	/** Get this node's list of experimental parameter names. */
	public String[] getParNames() {
		return (parNames);
	}

	/** Set this node's list of parameter names. */
	public void setParNames(String[] n) {
		parNames = n;
	}

	/**
	 * Check whether the given parameter is a parameeter of this node.
	 * 
	 * @param n
	 *            name of the parameter to check.
	 * @return true if the parameter is contained in this node's parameter
	 *         array.
	 */
	public boolean hasParameter(String n) {
		return (StringExt.indexOf(n, parNames) >= 0);
	}

	/** Get this design node's parameter array. */
	public ExParValue[] getParValues() {
		return (parValues);
	}

	/** Set the parameter array of this node. */
	public void setParValues(ExParValue[] p) {
		parValues = (p == null) ? null : dupArrayOfExParValues(p);
	}

	/* Get the parameter value at a given index position. */
	public ExParValue getParValue(int idx) {
		return (((parValues == null) || (idx >= parValues.length)) ? null
				: parValues[idx]);
	}

	/*
	 * Figure out the index of the given node parameter. Returns (-1) if the
	 * parameter is not an experimental parameter.
	 */
	public int getParIndex(String n) {
		return (StringExt.indexOf(n, parNames));
	}

	/*
	 * Get the value of a parameter with a given name. Returns null if the named
	 * parameter is not a design node parameter.
	 */
	public ExParValue getParValue(String n) {
		int i = getParIndex(n);
		if (i == (-1)) {
			return (null);
		}
		return (getParValue(i));
	}

	/**
	 * Check whether this not does have children.
	 * 
	 * @return true if this node does have at least one child and false
	 *         otherwise.
	 */
	public boolean hasChildren() {
		return ((childrenList != null) && (childrenList.size() > 0));
	}

	/** Get the children of this node. */
	public ArrayList getChildrenList() {
		return childrenList;
	}

	/** Set the children of this node. */
	public void setChildrenList(ArrayList children) {
		childrenList = children;
	}

	/** Set the child at the given index. */
	public void setChildAt(int i, ExDesignNode child) {
		childrenList.set(i, child);
	}

	/** Get the child at the given index. */
	/*
	 * contained in TreeNode implementation public ExDesignNode getChildAt(int
	 * i) { return((ExDesignNode)childrenList.get(i)); }
	 */
	/** Get the number of children of this node. */
	/*
	 * contained in TreeNode implementation public int getChildCount() { int r =
	 * 0; if (childrenList != null) r = childrenList.size(); return(r); }
	 */
	/** Set this design node's parent design node. */
	public void setParent(ExDesignNode p) {
		parent = p;
	}

	/** Return this design node's parent node. */
	/*
	 * contained in TreeNode implementation public ExDesignNode getParent() {
	 * return(parent); }
	 */
	/** Return the given node's index in this node's list of children. */
	public int getIndex(ExDesignNode nd) {
		return (childrenList.indexOf(nd));
	}

	/**
	 * Set the token position parameters to the given line and column of the
	 * design input file. This method is called by the parser only.
	 */
	public void setTokenPosition(int ln, int col) {
		tokenLine = ln;
		tokenColumn = col;
	}

	/**
	 * Return the line number where this node's first token was found in the
	 * design input file.
	 */
	public int getTokenLine() {
		return (tokenLine);
	}

	/**
	 * Return the column number where this node's first token was found in the
	 * design input file.
	 */
	public int getTokenColumn() {
		return (tokenColumn);
	}

	/**
	 * Create a path from the tree's root to this node.
	 * 
	 * @return an array of nodes which represent the path from the root node to
	 */
	public ExDesignNode[] pathTo() {
		ArrayList p = new ArrayList();
		ExDesignNode nd = this;
		// System.out.println("ExDesignNode.pathTo() " + this + " is");
		do {
			p.add(nd);
			nd = (ExDesignNode) nd.getParent();
		} while (nd != null);
		int n = p.size();
		ExDesignNode[] pp = new ExDesignNode[n];
		int k = 0;
		for (int i = n - 1; i >= 0; i--) {
			pp[k++] = (ExDesignNode) p.get(i);
		}
		// for (int i = 0; i < n; i++) System.out.println("   " + pp[i]);
		return (pp);
	}

	/**
	 * Create a string which describes this node with all experimental
	 * parameters showing their current value in the parameter table.
	 * 
	 * @return a string describing this node's current parameter values.
	 */
	public String toCurrentValueString() {
		return (toStringLocal(true));
	}

	/**
	 * Create a string which describes this node with the parameter values as
	 * defined in the design file.
	 * 
	 * @return a string describing this node as it is defined in the design
	 *         file.
	 */
	public String toString() {
		return (toStringLocal(false));
	}

	/**
	 * Return a String representation of this node. Trials, Blocks, and Sessions
	 * return a representation of their parameter values. Experiments and node
	 * parameter definitions return the list of argument names. Assignments are
	 * shown as usually written. New parameters are indicated by the keyword
	 * new.
	 * 
	 * @param uc
	 *            if true then the current values of the respective node
	 *            parameters are printed. If false then this node's argument
	 *            values are printed.
	 * @return a string representation of this node
	 */
	private String toStringLocal(boolean uc) {
		String s = "";
		switch (getType()) {
		case FactorLevelNode:
		case ConditionNode:
		case TrialNode:
		case BlockNode:
		case SessionNode:
		case ProcedureNode:
			s = getName() + parValuesToString(uc);
			break;
		case ExperimentNode:
		case ContextNode:
		case AssignmentGroupNode:
		case FactorsNode:
		case RandomFactorNode:
		case IndependentFactorNode:
		case DependentFactorNode:
		case CovariateFactorNode:
		case ConditionTableNode:
		case ExperimentDataDisplayNode:
		case ProcedureDataDisplayNode:
		case SessionDataDisplayNode:
		case BlockDataDisplayNode:
		case TrialDataDisplayNode:
		case DataDisplayNode:
			s = getName() + parNamesToString();
			break;
		case ProcedureStartDisplayNode:
		case SessionStartDisplayNode:
		case BlockStartDisplayNode:
		case TrialDisplayNode:
			s = getName() + parNamesToStringReduced();
			break;
		case DisplayNode:
			s = getName() + "()";
			break;
		case ProcedureEndDisplayNode:
		case SessionEndDisplayNode:
		case BlockEndDisplayNode:
			s = getName() + "()";
			break;
		case AssignmentNode:
			s = getShortAssignmentParName() + " = " + parValues[0].toString();
			break;
		case NewParamAssignmentNode:
			s = "new " + getShortAssignmentParName() + " = "
					+ parValues[0].toString();
			break;
		case AdjustableParamAssignmentNode:
			s = "adjustable " + getShortAssignmentParName() + " = "
					+ parValues[0].toString();
			break;
		default:
			s = "???";
		}
		return (s);
	}

	/**
	 * This returns the short version of an assignment's parameter name if this
	 * is a subnode of a display.
	 */
	public String getShortAssignmentParName() {
		/*
		 * String r = parNames[0]; int d = r.lastIndexOf('.'); if (d >= 0) { r =
		 * r.substring(d+1); } return(r);
		 */
		return getName();
	}

	/**
	 * This returns the long version of an assignment's parameter name if this
	 * is a subnode of a display.
	 */
	/*
	 * public String getLongAssignmentParName() { String r = parNames[0];
	 * return(r); }
	 */
	/**
	 * Convert this design node's parameter names to a string which looks like
	 * an argument definition. This is used for converting nodes to string
	 * representations.
	 */
	private String parNamesToString() {
		StringBuffer s = new StringBuffer("(");
		if (parNames != null) {
			if (parNames.length > 0) {
				s.append(" " + parNames[0]);
			}
			if (parNames.length > 1) {
				for (int i = 1; i < parNames.length; i++) {
					s.append(", " + parNames[i]);
				}
			}
		}
		s.append(")");
		return (s.toString());
	}

	/**
	 * Convert this display list node's parameter names to a string which looks
	 * like an argument definition. Prefix strings which are identical to this
	 * display list's instance name are removed.
	 */
	private String parNamesToStringReduced() {
		StringBuffer s = new StringBuffer("(");
		if (parNames != null) {
			if (parNames.length > 0) {
				s.append(" " + parNameReduced(parNames[0]));
			}
			if (parNames.length > 1) {
				for (int i = 1; i < parNames.length; i++) {
					s.append(", " + parNameReduced(parNames[i]));
				}
			}
		}
		s.append(")");
		return (s.toString());
	}

	/**
	 * Remove this node's instance name prefix from the given parameter name.
	 */
	private String parNameReduced(String pn) {
		int p = 0;
		String n = getName() + ".";
		if (pn.startsWith(n)) {
			p = n.length();
		}
		return pn.substring(p);
	}
	/**
	 * Converts this node's array of parameter values to a string
	 * representation. This is used for converting nodes to string
	 * representations.
	 * 
	 * @param uc
	 *            if true then the current values of the respective node
	 *            parameters are printed. If false then this node's argument
	 *            values are printed.
	 * @return a string representation of this node's parameter array.
	 */
	private boolean xtnd = false;

	private String parValuesToString(boolean uc) {
		// System.out.println("ExDesignNode.parValuesToString(): " + getName());
		StringBuffer s = new StringBuffer("(");
		if (parValues != null) {
			if (parValues.length > 0) {
				if (uc)
					s.append(" "
							+ (ExPar.get(parNames[0])).getValue().getValue()
									.toString());
				else {
					if (parValues[0] == null) {
						s.append(" (null)");
					} else {
						if (xtnd) {
							s.append(" " + parNames[0] + "="
									+ parValues[0].toString());
						} else {
							s.append(" " + parValues[0].toString());
						}
					}
				}
			}
			if (parValues.length > 1) {
				for (int i = 1; i < parValues.length; i++) {
					if (uc) {
						s.append(", "
								+ (ExPar.get(parNames[i])).getValue()
										.getValue().toString());
					} else {
						if (parValues[i] == null) {
							s.append(" (null)");
						} else {
							if (xtnd) {
								s.append(", " + parNames[i] + "="
										+ parValues[i].toString());
							} else {
								s.append(", " + parValues[i].toString());
							}
						}
					}
				}
			}
		}
		s.append(")");
		return (s.toString());
	}

	/**
	 * Create a full copy of an array of experimental parameter values.
	 */
	private ExParValue[] dupArrayOfExParValues(ExParValue[] p) {
		ExParValue pp[] = new ExParValue[p.length];
		for (int i = 0; i < p.length; i++) {
			pp[i] = (ExParValue) (p[i].clone());
		}
		return (pp);
	}

	/**
	 * Execute an implicit or explicit assignment statement. This simply sets
	 * the current value of an experimental parameter in the global experimental
	 * parameter table. This method is only called when the runtime context is
	 * being created and initialized.
	 */
	public void doAssignment() {
		if (isAssignment()) {
			// get full parameter name
			String n = getInstanceName();
			if (isClassAttribute(n)) {
				// wa have a parameter of a named class other than ExPar
				Debug.show(Debug.ASSIGNMENT, "Assignment to class attribute "
						+ n);
				if (!parValues[0].isUndefined()) {
					// System.out.println("ExDesignNode.doAssignment() class attribute: "
					// + n);
					int p = n.lastIndexOf('.');
					String cn = n.substring(0, p);
					String pn = n.substring(p + 1);
					ExPar xp = ((ExPar) (ExPar
							.get("de.pxlab.pxl", cn, pn, true)));
					if (xp != null) {
						xp.getValue().set(parValues[0]);
					} else {
						// System.out.println("ExDesignNode.doAssignment() Can't find "
						// + n);
					}
				}
			} else {
				// System.out.println("ExDesignNode.doAssignment() instance name:"
				// + n);
				if ((getType() == NewParamAssignmentNode)
						&& (!ExPar.contains(n))) {
					Debug.show(Debug.ASSIGNMENT, "Creating parameter " + n);
					ExPar.create(n);
				} else {
					Debug.show(Debug.ASSIGNMENT, "Assignment to parameter " + n);
				}
				Debug.show(Debug.ASSIGNMENT, "  " + n + " = " + parValues[0]);
				if (!parValues[0].isUndefined()) {
					ExPar.set(n, parValues[0]);
				}
			}
		} else {
			throw new RuntimeException(
					"ExDesignNode.doAssignment(): Illegal use for non-Assignment node "
							+ toString());
		}
	}

	/**
	 * Check whether the given string is a class attribute. This is a name of
	 * the form 'm.n' where m is a string which does not start with a procedure
	 * unit name and n is any string.
	 * 
	 * @param nn
	 *            the string name to check.
	 * @return true if the name is of the form 'm.n' where m is not a procedure
	 *         unit name and false otherwise.
	 */
	public static boolean isClassAttribute(String nn) {
		int p = nn.indexOf('.');
		if (p < 0)
			return false;
		String cn = nn.substring(0, p);
		int q = cn.indexOf(':');
		int m = (q < 0) ? p : q;
		String n = nn.substring(0, m);
		boolean r = !(n.equals("Procedure") || n.equals("ProcedureEnd")
				|| n.equals("ProcedureData") || n.equals("Session")
				|| n.equals("SessionEnd") || n.equals("SessionData")
				|| n.equals("Block") || n.equals("BlockEnd")
				|| n.equals("BlockData") || n.equals("Trial")
				|| n.equals("TrialData") || n.equals("ExperimentData"));
		// System.out.println("ExDesignNode.isClassAttribute(): " + nn + " = " +
		// (r? "true": "false"));
		return r;
	}

	/**
	 * Store the tree rooting in this node to the given file.
	 * 
	 * @param filePath
	 *            full filename and path where to store the tree. If this is a
	 *            null string then the tree is printed to the default output
	 *            stream.
	 */
	public void store(String filePath) {
		// System.out.println("ExDesignNode.store(): " + filePath);
		if (filePath == null) {
			print();
		} else {
			File f = new File(filePath);
			if (f.exists()) {
				FileExt.backupFile(f);
			}
			try {
				Debug.show(Debug.FILES, "ExDesignNode.store(): Open file "
						+ filePath);
				PrintWriter ps = new PrintWriter(new FileOutputStream(f));
				print(ps);
				ps.close();
				Debug.show(Debug.FILES, "ExDesignNode.store(): Closed file "
						+ filePath);
			} catch (IOException ioex) {
				new FileError("ExDesignNode.store(): Error writing file "
						+ filePath);
			}
		}
	}
	private static int depth = 0;

	/**
	 * Print a command line protocol of this node and its children if there are
	 * some. This creates a recursive display of all nodes rooted in this node.
	 */
	public void print() {
		print(System.out);
	}

	/**
	 * Print a protocol of this node and its children if there are some. This
	 * creates a recursive display of all nodes rooted in this node.
	 * 
	 * @param ps
	 *            the PrintStream where to print to.
	 */
	public void print(PrintStream ps) {
		// System.out.println("ExDesignNode.print() Printing to PrintStream ...");
		// print(new PrintWriter(ps));
		String b = "                    ";
		if (precedingComment != null)
			ps.println((depth > 0) ? (b.substring(0, depth) + precedingComment)
					: precedingComment);
		ps.print((depth > 0) ? (b.substring(0, depth) + toString())
				: toString());
		if (this.hasChildren()) {
			ps.println("");
			ps.println((depth > 0) ? (b.substring(0, depth) + "{") : "{");
			depth += 2;
			for (int i = 0; i < childrenList.size(); i++) {
				ExDesignNode nd = (ExDesignNode) (childrenList.get(i));
				if (nd.isAssignment()) {
					if (nd.containsTypeModifier(ExplicitAssignment)) {
						ExParValue v = nd.getParValues()[0];
						if (!v.isUndefined()) {
							nd.print(ps);
						}
					}
				} else {
					nd.print(ps);
				}
			}
			depth -= 2;
			ps.println((depth > 0) ? (b.substring(0, depth) + "}") : "}");
		} else {
			ps.println(";");
		}
	}

	/**
	 * Print a protocol of this node and its children if there are some. This
	 * creates a recursive display of all nodes rooted in this node.
	 * 
	 * @param ps
	 *            the PrintWriter where to print to.
	 */
	public void print(PrintWriter ps) {
		// System.out.println("ExDesignNode.print() Printing to PrintWriter ...");
		String b = "                    ";
		// Ignore assignments which are not explicitly defined
		// if (!isAssignment() || containsTypeModifier(ExplicitAssignment)) {
		if (precedingComment != null)
			ps.println((depth > 0) ? (b.substring(0, depth) + precedingComment)
					: precedingComment);
		ps.print((depth > 0) ? (b.substring(0, depth) + toString())
				: toString());
		if (this.hasChildren()) {
			ps.println("");
			ps.println((depth > 0) ? (b.substring(0, depth) + "{") : "{");
			depth += 2;
			for (int i = 0; i < childrenList.size(); i++) {
				ExDesignNode nd = (ExDesignNode) (childrenList.get(i));
				if (nd.isAssignment()) {
					if (nd.containsTypeModifier(ExplicitAssignment)) {
						ExParValue v = nd.getParValues()[0];
						if (!v.isUndefined()) {
							nd.print(ps);
						}
					}
				} else {
					nd.print(ps);
				}
			}
			depth -= 2;
			ps.println((depth > 0) ? (b.substring(0, depth) + "}") : "}");
		} else {
			ps.println(";");
		}
		// }
	}

	/**
	 * Print a HTML description of this node and its children if there are some.
	 * This creates a recursive HTML description of all nodes rooted in this
	 * node.
	 * 
	 * @param ps
	 *            the PrintWriter where to print to.
	 * @param pnn
	 *            parent node name in case this is a local assignment within a
	 *            Display object definition.
	 */
	public void printHTML(PrintWriter ps, String pnn) {
		if (precedingComment != null)
			ps.println(precedingComment + "<br>");
		if (pnn != null) {
			// ps.print("<a href=\"" + pnn + "#" + getShortAssignmentParName() +
			// "\">" + toString() + "</a>");
			String pns = getShortAssignmentParName();
			String pps = toString();
			// This creates a link supported by PHP
			// String ns = StringExt.replace(pps, pns, "<?php DD(\"" + pnn +
			// ".html#" + pns + "\", \"" + pns + "\");?>");
			// This is the simple unlinked version
			String ns = pps;
			ps.print(ns);
		} else {
			ps.print(toString());
		}
		if (this.hasChildren()) {
			String sp = null;
			if (isDisplay()) {
				sp = getName();
			}
			ps.println("{<blockquote>");
			for (int i = 0; i < childrenList.size(); i++) {
				ExDesignNode nd = (ExDesignNode) (childrenList.get(i));
				if (nd.isAssignment()) {
					if (nd.containsTypeModifier(ExplicitAssignment)) {
						ExParValue v = nd.getParValues()[0];
						if (!v.isUndefined()) {
							nd.printHTML(ps, sp);
						}
					}
				} else {
					nd.printHTML(ps, sp);
				}
				// ((ExDesignNode)(childrenList.get(i))).printHTML(ps, sp);
			}
			ps.println("</blockquote>}<br>");
		} else {
			ps.println(";<br>");
		}
	}

	/**
	 * Print a Java source file of this node and its children if there are some.
	 * If this is the root node than this creates a compilable Java class file
	 * which extends the ExDesign class and when compiled and instantiated
	 * creates the same design tree as this one.
	 * 
	 * @param cfn
	 *            the class file name which should be used for the generated
	 *            class.
	 * @param ps
	 *            the PrintWriter where to print to.
	 */
	public void printJavaSource(String cfn, PrintWriter ps) {
		// PrintWriter ps = new PrintWriter(System.out);
		String cn = cfn;
		int idx = cfn.lastIndexOf(".java");
		if (idx >= 0) {
			cn = cfn.substring(0, idx);
		}
		if (getType() == ExDesignNode.ExperimentNode) {
			ExParValue.initJavaSourceGenerator();
			ps.print(experimentHeader(cn));
		}
		ps.print(toJavaSource());
		if (this.hasChildren()) {
			for (int i = 0; i < childrenList.size(); i++) {
				ExDesignNode nd = (ExDesignNode) (childrenList.get(i));
				if (nd.isAssignment()) {
					if (nd.containsTypeModifier(ExplicitAssignment)) {
						ExParValue v = nd.getParValues()[0];
						if (!v.isUndefined()) {
							nd.printJavaSource(cfn, ps);
						}
					}
				} else {
					nd.printJavaSource(cfn, ps);
				}
			}
		}
		if (getType() == ExDesignNode.ExperimentNode) {
			ps.print(experimentTrailer(cn));
		}
	}

	/**
	 * Convert this node into a Java representation such that when compiled
	 * produces the same node.
	 */
	private String toJavaSource() {
		StringBuffer s = new StringBuffer();
		String nl = System.getProperty("line.separator");
		// String nl = "\n";
		switch (getType()) {
		case ExperimentNode:
			s.append(parNamesDeclaration()
					+ "\texDesignTree = exDesignTreeFactory.createExperimentNode(parNames);"
					+ nl);
			break;
		case ContextNode:
			s.append(parNamesDeclaration()
					+ "\texDesignTreeFactory.addContextNode(parNames);" + nl);
			break;
		case TrialDisplayNode:
		case BlockStartDisplayNode:
		case BlockEndDisplayNode:
		case SessionStartDisplayNode:
		case SessionEndDisplayNode:
		case ProcedureStartDisplayNode:
		case ProcedureEndDisplayNode:
			s.append(parNamesDeclaration()
					+ "\texDesignTreeFactory.addDisplayListNode(" + getType()
					+ ", \"" + getName() + "\", parNames);" + nl);
			break;
		case DisplayNode:
			s.append("\texDesignTreeFactory.addDisplayNode(\"" + getName()
					+ "\");" + nl);
			break;
		case ExperimentDataDisplayNode:
		case ProcedureDataDisplayNode:
		case SessionDataDisplayNode:
		case BlockDataDisplayNode:
		case TrialDataDisplayNode:
			s.append(parNamesDeclaration()
					+ "\texDesignTreeFactory.addDataDisplayListNode("
					+ getType() + ", \"" + getName() + "\", parNames);" + nl);
			break;
		case DataDisplayNode:
			s.append(parNamesDeclaration()
					+ "\texDesignTreeFactory.addDataDisplayNode(" + "\""
					+ getName() + "\", parNames);" + nl);
			break;
		case AssignmentGroupNode:
			s.append(parNamesDeclaration()
					+ "\texDesignTreeFactory.addAssignmentGroupNode(parNames);"
					+ nl);
			break;
		case AssignmentNode:
		case NewParamAssignmentNode:
		case AdjustableParamAssignmentNode:
			String n = getParValues()[0].toJavaSource("\t", s);
			// s.append(exParValueDeclaration(getParValues()[0]));
			if (parent.isDisplay() || parent.isDataDisplay()) {
				s.append("\texDesignTreeFactory.addLocalAssignmentNode(");
			} else if (parent.isAssignmentGroup()) {
				s.append("\texDesignTreeFactory.addGlobalAssignmentNode(");
			} else {
			}
			s.append(getType() + ", \"" + getName() + "\", \""
					+ getParNames()[0] + "\", " + n + ");" + nl);
			break;
		case FactorsNode:
			s.append(parNamesDeclaration()
					+ "\texDesignTreeFactory.addFactorsNode(parNames);" + nl);
			break;
		case RandomFactorNode:
		case IndependentFactorNode:
		case DependentFactorNode:
		case CovariateFactorNode:
			s.append(parNamesDeclaration()
					+ "\texDesignTreeFactory.addFactorNode(" + getType()
					+ ", parNames);" + nl);
			break;
		case FactorLevelNode:
			s.append(parValuesDeclaration()
					+ "\texDesignTreeFactory.addFactorLevelNode(parValues);"
					+ nl);
			break;
		case ConditionTableNode:
			s.append(parNamesDeclaration()
					+ "\texDesignTreeFactory.addFactorsNode(parNames);" + nl);
			break;
		case ConditionNode:
			s.append(parValuesDeclaration()
					+ "\texDesignTreeFactory.addConditionNode(parValues);" + nl);
			break;
		case ProcedureNode:
			s.append(parValuesDeclaration()
					+ "\texDesignTreeFactory.addProcedureNode( \"" + getName()
					+ "\", parValues);" + nl);
			break;
		case SessionNode:
			s.append(parValuesDeclaration()
					+ "\texDesignTreeFactory.addSessionNode( \"" + getName()
					+ "\", parValues);" + nl);
			break;
		case BlockNode:
			s.append(parValuesDeclaration()
					+ "\texDesignTreeFactory.addBlockNode( \"" + getName()
					+ "\", parValues);" + nl);
			break;
		case TrialNode:
			s.append(parValuesDeclaration()
					+ "\texDesignTreeFactory.addTrialNode( \"" + getName()
					+ "\", parValues);" + nl);
			break;
		default:
			s.append("--- unknown node type found: " + getType() + "" + nl);
			System.out.println("--- unknown node type found: " + getType());
		}
		return (s.toString());
	}

	private String experimentHeader(String cn) {
		StringBuffer b = new StringBuffer();
		String nl = System.getProperty("line.separator");
		b.append("//----------------------------------------------------" + nl);
		b.append("// This file has been created automatically by PXLab. " + nl);
		b.append("//----------------------------------------------------" + nl);
		b.append("package de.pxlab.exp;" + nl);
		b.append("import de.pxlab.pxl.ExParValue;" + nl);
		b.append("import de.pxlab.pxl.ExParValueVar;" + nl);
		b.append("import de.pxlab.pxl.ExParValueConstant;" + nl);
		b.append("import de.pxlab.pxl.ExParValueFunction;" + nl);
		b.append("import de.pxlab.pxl.ExParValueUndefined;" + nl);
		b.append("import de.pxlab.pxl.ExDesign;" + nl);
		b.append("import de.pxlab.pxl.ExDesignTreeFactory;" + nl);
		b.append("import de.pxlab.pxl.run.ExRun;" + nl);
		b.append("public class " + cn + " extends ExDesign {" + nl);
		b.append("    private String[] parNames;" + nl);
		b.append("    private ExParValue exParValue;" + nl);
		b.append("    private ExParValue[] parValues;" + nl);
		b.append("    private String[] exParValuesValue;" + nl);
		b.append("    public " + cn + "() {" + nl);
		b.append("\tExDesignTreeFactory exDesignTreeFactory = new ExDesignTreeFactory();"
				+ nl);
		return (b.toString());
	}

	private String experimentTrailer(String cn) {
		StringBuffer b = new StringBuffer();
		String nl = System.getProperty("line.separator");
		// b.append("\texDesignTree.print();" + nl);
		b.append("    }" + nl);
		b.append("    public static void main(String[] args) {" + nl);
		b.append("        // de.pxlab.pxl.Debug.add(de.pxlab.pxl.Debug.FACTORY);"
				+ nl);
		b.append("        // de.pxlab.pxl.Debug.add(de.pxlab.pxl.Debug.CREATE_NODE);"
				+ nl);
		b.append("        ExDesign x = new " + cn + "();" + nl);
		b.append("        // x.print();" + nl);
		b.append("        new ExRun(args, x);" + nl);
		b.append("    }" + nl);
		b.append("}" + nl);
		return (b.toString());
	}

	private String parNamesDeclaration() {
		String nl = System.getProperty("line.separator");
		if ((parNames == null) || (parNames.length == 0))
			return ("\tparNames = null;" + nl);
		StringBuffer b = new StringBuffer();
		b.append("\tparNames = new String[" + parNames.length + "];" + nl);
		for (int i = 0; i < parNames.length; i++)
			b.append("\tparNames[" + i + "] = \"" + parNames[i] + "\";" + nl);
		return b.toString();
	}

	/**
	 * Prepares a parameter value argument list for a
	 * Procedure/Session/Block/Trial unit.
	 */
	private String parValuesDeclaration() {
		String nl = System.getProperty("line.separator");
		if ((parValues == null) || (parValues.length == 0))
			return ("\tparValues = null;" + nl);
		StringBuffer b = new StringBuffer();
		b.append("\tparValues = new ExParValue[" + parValues.length + "];" + nl);
		for (int i = 0; i < parValues.length; i++) {
			ExParValue v = parValues[i];
			String n = v.toJavaSource("\t", b);
			b.append("\tparValues[" + i + "] = " + n + ";" + nl);
		}
		return b.toString();
	}

	/**
	 * Expand those Trial children of a Block node which contain expansion
	 * parameter values as arguments.
	 */
	public void expandTrials() {
		if (!isBlock())
			return;
		if (!hasChildren())
			return;
		for (int childIdx = 0; childIdx < childrenList.size(); childIdx++) {
			boolean hasExpansion = false;
			ExDesignNode trial = (ExDesignNode) childrenList.get(childIdx);
			ExParValue[] pv = trial.getParValues();
			ExParValue[] pve = null;
			int[] set = null;
			if (pv != null) {
				// pve is the same as pv, but expansions are evaluated
				pve = new ExParValue[pv.length];
				// Check for expansions and create the set array
				set = new int[pv.length];
				for (int i = 0; i < pv.length; i++) {
					// Need to evaluate here
					ExParValue v = pv[i].getValue();
					if (v.isExpansion()) {
						set[i] = v.length;
						pve[i] = v;
						hasExpansion = true;
					} else {
						set[i] = 1;
						pve[i] = pv[i];
					}
				}
			}
			if (hasExpansion) {
				// Remove the node which will be expanded
				childrenList.remove(childIdx);
				for (ExpansionIterator ex = new ExpansionIterator(set); ex
						.hasNext();) {
					int[] idx = (int[]) ex.next();
					// Create the new node's array of parameter values
					ExParValue[] pVal = new ExParValue[pve.length];
					for (int j = 0; j < pve.length; j++) {
						if (set[j] == 1) {
							// System.out.println("ExDesignNode.expandTrials() Original: "
							// + pve[j]);
							pVal[j] = (ExParValue) pve[j].clone();
							// System.out.println("ExDesignNode.expandTrials() Clone:    "
							// + pVal[j]);
						} else {
							pVal[j] = pve[j].getValueAt(idx[j]);
						}
					}
					ExDesignNode newTrial = new ExDesignNode(TrialNode, 0);
					newTrial.setName(trial.getName());
					newTrial.setParNames(trial.getParNames());
					newTrial.setParValues(pVal);
					newTrial.setParent((ExDesignNode) trial.getParent());
					childrenList.add(childIdx, newTrial);
				}
			}
		}
	}

	/**
	 * Multiply the children of a Session or Block node by the factor parameter
	 * given as an argument.
	 */
	public void multiply(int factor) {
		if (!isBlock() && !isSession())
			return;
		if (!hasChildren())
			return;
		if (factor < 2)
			return;
		ArrayList newChildrenList = new ArrayList(factor * childrenList.size());
		for (int childIdx = 0; childIdx < childrenList.size(); childIdx++) {
			ExDesignNode child = (ExDesignNode) childrenList.get(childIdx);
			newChildrenList.add(child);
			for (int i = 1; i < factor; i++) {
				newChildrenList.add(child.dupTree());
			}
		}
		newChildrenList.trimToSize();
		childrenList = newChildrenList;
	}

	/** Randomize the order of children nodes. */
	public void randomizeChildrenList() {
		Collections.shuffle(childrenList);
	}

	/**
	 * Return an Iterator for walking through the design tree starting at this
	 * node.
	 */
	public Iterator iterator() {
		return new IteratorImp();
	}
	/**
	 * This class is the local implementation of the Iterator interface for
	 * walking through the design tree.
	 * 
	 * >>>> NOTE: Must be an error for those cases where the root node does not
	 * have children.
	 */
	private class IteratorImp implements Iterator {
		private boolean hNxt = false;
		private ExDesignNode root;
		private ExDesignNode nxt;

		/**
		 * Create a new design tree iterator starting from this node.
		 */
		public IteratorImp() {
			root = ExDesignNode.this;
			nxt = ExDesignNode.this;
			hNxt = true;
		}

		/** Returns true if there is another node in the tree. */
		public boolean hasNext() {
			return (hNxt);
		}

		/** Returns the next node in the tree. */
		public Object next() {
			// We already know that we will return the node nxt but we
			// do the checking for the next request already now
			ExDesignNode retNode = nxt;
			// System.out.println("ExDesignNode.next() = " + nxt);
			// If this node is not a leave then its first
			// child is the next object to be returned.
			if (nxt.hasChildren()) {
				hNxt = true;
				nxt = (ExDesignNode) nxt.childrenList.get(0);
				return (retNode);
			}
			// If this node does not have a parent we have no more
			// object to return
			if (nxt.parent == null) {
				hNxt = false;
				return (retNode);
			}
			// If this node is not the last child of its parent then
			// its next child will be the next node to be returned
			int nxtidx = nxt.parent.getIndex(nxt);
			if ((nxtidx >= 0)
					&& (nxtidx < (nxt.parent.childrenList.size() - 1))) {
				hNxt = true;
				nxt = (ExDesignNode) nxt.parent.childrenList.get(nxtidx + 1);
				return (retNode);
			}
			// If this node is the last child of its parent we have to
			// move up until we find a node which has another sibling
			do {
				nxt = nxt.parent;
				// System.out.println("ExDesignNode.next(): moving up to " +
				// nxt);
			} while ((nxt != root)
			// && (nxt.parent != null)
					&& !(nxt.parent.getIndex(nxt) < (nxt.parent.childrenList
							.size() - 1)));
			// We have another node if we found a parent which has a sibling
			if ((nxt != root) /* && (nxt.parent != null) */) {
				hNxt = true;
				nxt = (ExDesignNode) nxt.parent.childrenList.get(nxt.parent
						.getIndex(nxt) + 1);
			} else {
				hNxt = false;
			}
			// System.out.println("ExDesignNode.next(): next element is " +
			// nxt);
			return (retNode);
		}

		/** Not supportet by this Iterator. */
		public void remove() {
			throw new RuntimeException(
					"ExDesignNode.IteratorImp: remove() not supported.");
		}
	}

	/**
	 * Push the values of all experimental parameters which are parameters of
	 * this node down onto their value stack and set the top level values to
	 * this node's argument values. This method is called whenever a node is
	 * entered during experimental processing. It guarantees that an ExPar
	 * objects value is localized if this ExPar object is a node parameter.
	 */
	public void pushArgs() {
		if ((parNames != null) && (parValues != null)) {
			// System.out.println("ExDesignNode.pushArgs() Node pars before push(): "
			// + toString());
			if (parNames.length == parValues.length) {
				for (int i = 0; i < parNames.length; i++) {
					String n = parNames[i];
					// System.out.println("ExDesignNode.pushArgs() pushing argument "
					// + i + ": " + n);
					// Shouldn't here be used get(n, false) ?
					ExPar x = ExPar.get(n);
					if (x == null) {
						new ParameterNameError(
								"ExDesignNode.pushArgs(): Parameter " + n
										+ " does not exist: " + toString());
					} else {
						ExParValue xn = parValues[i];
						// System.out.println("ExDesignNode.pushArgs() new value: "
						// + xn);
						// Note: it is important here, that we push a
						// value which is NOT evaluated in order to
						// preserve expressions
						x.push(xn);
					}
				}
			} else {
				new ParameterNameError(
						"ExDesignNode.pushArgs(): Parameter name and value array differ in length: "
								+ toString());
			}
		} else {
			// System.out.println("ExDesignNode.pushArgs() Node does not have parameters: "
			// + toString());
		}
		Debug.show(Debug.PUSH_POP, "Push " + toString());
	}

	/**
	 * Pop back all node parameter values given in the argument list of this
	 * node and copy the current parameter values of the argument ExPar objects
	 * to this node's parameter value array.
	 */
	public void popArgs() {
		if ((parNames != null) && (parValues != null)) {
			// System.out.println("Node before pop(): " + toString());
			if (parNames.length == parValues.length) {
				for (int i = 0; i < parNames.length; i++) {
					String n = parNames[i];
					// System.out.println("ExDesignNode.popArgs() popping argument "
					// + i + ": " + n);
					ExPar x = ExPar.get(n);
					if (x == null) {
						new ParameterNameError(
								"ExDesignNode.popArgs(): Parameter " + n
										+ " does not exist: " + toString());
					} else {
						parValues[i] = x.pop();
					}
					// parValues[i] = ExPar.get(parNames[i]).pop();
				}
			} else {
				new ParameterNameError(
						"ExDesignNode.popArgs(): Parameter name and value array differ in length: "
								+ toString());
			}
		}
		Debug.show(Debug.PUSH_POP, " Pop " + toString());
	}

	/**
	 * Modify the parameter value array of this node such that it contains the
	 * current values of its parameters. The previous parameter value array is
	 * destroyed.
	 */
	public void setParValuesToCurrentValues() {
		// if this node does not have parameters we don't have to do
		// anything
		if (parNames != null) {
			parValues = new ExParValue[parNames.length];
			for (int i = 0; i < parNames.length; i++) {
				ExPar x = ExPar.get(parNames[i]);
				if (x == null) {
					new ParameterValueError("Parameter does not exist: "
							+ parNames[i]);
				} else {
					parValues[i] = (ExParValue) (x.getValue().clone());
				}
			}
		}
	}

	/**
	 * Find the first child of this node which is a node of the given type t.
	 */
	public ExDesignNode getFirstChildOfType(int t) {
		int n = childrenList.size();
		for (int k = 0; k < n; k++) {
			ExDesignNode node = (ExDesignNode) childrenList.get(k);
			if (node.getType() == t) {
				return node;
			}
		}
		return null;
	}

	/** Find the first child of this node whith the given name. */
	public ExDesignNode getFirstChildOfName(String name) {
		int n = childrenList.size();
		for (int k = 0; k < n; k++) {
			ExDesignNode node = (ExDesignNode) childrenList.get(k);
			if (node.getName().equals(name)) {
				return (node);
			}
		}
		return (null);
	}

	/**
	 * Modify this node such that it gets the new parameters but keeps as much
	 * as possible from its current parameter values.
	 */
	public void modifyParValues(String[] newParNames) {
		int n = newParNames.length;
		ExParValue[] newParValues = new ExParValue[n];
		for (int i = 0; i < n; i++) {
			int k = StringExt.indexOf(newParNames[i], parNames);
			if (k >= 0) {
				newParValues[i] = parValues[k];
			} else {
				// System.out.println("ExDesignNode.modifyParValues(): searching value of "
				// + newParNames[i]);
				ExPar p = ExPar.get(newParNames[i], false);
				if (p != null) {
					newParValues[i] = p.getValue();
				} else {
					newParValues[i] = new ExParValueUndefined();
				}
			}
		}
		parValues = newParValues;
		parNames = newParNames;
	}

	/*
	 * Add the given display's parameters as assignment node children to the
	 * given design node if they do not already exist there.
	 */
	public int addParameterAssignmentNodes(DisplaySupport dsp) {
		// First figure out whether this node already has parameter
		// assignments
		// new RuntimeException().printStackTrace();
		int nn = 0;
		String[] nodeParNames = null;
		ArrayList nPN = null;
		if (this.hasChildren()) {
			nPN = new ArrayList();
			for (int i = 0; i < childrenList.size(); i++) {
				ExDesignNode node = (ExDesignNode) childrenList.get(i);
				if (node.isAssignment()) {
					nPN.add(node.getName());
					if (node.getType() == AdjustableParamAssignmentNode) {
						if (dsp instanceof Display) {
							((Display) dsp).setAdjustable(true);
							((Display) dsp).setDynExPar(ExPar.get(node
									.getInstanceName()));
						}
					}
				}
			}
			nodeParNames = new String[nPN.size()];
			for (int i = 0; i < nPN.size(); i++) {
				nodeParNames[i] = (String) nPN.get(i);
				// System.out.println("Have Parameter " + nodeParNames[i]);
			}
		}
		// Now run through the list of display parameters
		ExParDescriptor[] xpd = dsp.getExParFields();
		for (int i = 0; i < xpd.length; i++) {
			String parName = xpd[i].getName();
			parName = parName.substring(parName.lastIndexOf('.') + 1);
			// System.out.print("Parameter " + parName + ": ");
			if ((!this.hasChildren())
					|| (StringExt.indexOf(parName, nodeParNames) < 0)) {
				ExDesignNode newNode = new ExDesignNode(
						ExDesignNode.AssignmentNode, parName, xpd[i].getValue()
								.getValue(), 0);
				newNode.setName(parName);
				this.add(newNode);
				nn++;
				// System.out.println("Node " + newNode + " added to display.");
				// System.out.println(" added.");
			} else {
				// System.out.println(" exists.");
			}
		}
		return (nn);
	}

	private String nodeConstructorString() {
		StringBuffer b = new StringBuffer();
		if (childrenList == null) {
			b.append("New " + getTypeName() + " node named \'" + getName()
					+ "\' with no children and ");
		} else {
			b.append("New " + getTypeName() + " node named \'" + getName()
					+ "\' with " + childrenList.size() + " children and ");
		}
		if (parValues == null) {
			b.append("no parameter values");
		} else {
			b.append(parValues.length + " values of node parameters.");
			// printParValues();
		}
		return (b.toString());
	}

	public boolean isTopLevel() {
		int typ = getType();
		return ((typ == ExperimentNode) || (typ == ContextNode) || (typ == ProcedureNode));
	}

	public boolean isExperiment() {
		return ((getType() == ExperimentNode));
	}

	public boolean isContext() {
		return ((getType() == ContextNode));
	}

	public boolean isDisplayList() {
		int typ = getType();
		return ((typ >= ProcedureStartDisplayNode) && (typ <= BlockEndDisplayNode));
	}

	public boolean isDataDisplayList() {
		int typ = getType();
		return ((typ >= ExperimentDataDisplayNode) && (typ <= TrialDataDisplayNode));
	}

	public boolean isExperimentDataDisplayList() {
		return getType() == ExperimentDataDisplayNode;
	}

	public boolean isProcedureStartDisplay() {
		return ((getType() == ProcedureStartDisplayNode));
	}

	public boolean isSessionStartDisplay() {
		return ((getType() == SessionStartDisplayNode));
	}

	public boolean isBlockStartDisplay() {
		return ((getType() == BlockStartDisplayNode));
	}

	public boolean isProcedureEndDisplay() {
		return ((getType() == ProcedureEndDisplayNode));
	}

	public boolean isSessionEndDisplay() {
		return ((getType() == SessionEndDisplayNode));
	}

	public boolean isBlockEndDisplay() {
		return ((getType() == BlockEndDisplayNode));
	}

	public boolean isTrialDisplay() {
		return ((getType() == TrialDisplayNode));
	}

	public boolean isDisplay() {
		return ((getType() == DisplayNode));
	}

	public boolean isDataDisplay() {
		return ((getType() == DataDisplayNode));
	}

	public boolean isAssignmentGroup() {
		return ((getType() == AssignmentGroupNode));
	}

	public boolean isAssignment() {
		int typ = getType();
		return ((typ >= AssignmentNode) && (typ <= AdjustableParamAssignmentNode));
	}

	public boolean isNewParamAssignment() {
		return (getType() == NewParamAssignmentNode);
	}

	public boolean isAdjustParamAssignment() {
		return (getType() == AdjustableParamAssignmentNode);
	}

	public boolean isProcedure() {
		return ((getType() == ProcedureNode));
	}

	public boolean isParamDefinition() {
		int typ = getType();
		return ((typ >= SessionStartDisplayNode) && (typ <= TrialDisplayNode))
				|| (typ == ExperimentNode);
	}

	public boolean isProcedureUnit() {
		int typ = getType();
		return ((typ >= ProcedureNode) && (typ <= TrialNode));
	}

	public boolean isSession() {
		return ((getType() == SessionNode));
	}

	public boolean isBlock() {
		return ((getType() == BlockNode));
	}

	public boolean isTrial() {
		return ((getType() == TrialNode));
	}

	public boolean isFactor() {
		int typ = getType();
		return ((typ >= RandomFactorNode) && (typ <= CovariateFactorNode));
	}

	public boolean isRandomFactor() {
		return ((getType() == RandomFactorNode));
	}

	public boolean isIndependentFactor() {
		return ((getType() == IndependentFactorNode));
	}

	public boolean isDependentFactor() {
		return ((getType() == DependentFactorNode));
	}

	public boolean isCovariateFactor() {
		return ((getType() == CovariateFactorNode));
	}

	public boolean isFactors() {
		return ((getType() == FactorsNode));
	}

	public boolean isFactorLevel() {
		return ((getType() == FactorLevelNode));
	}

	public boolean isCondition() {
		return ((getType() == ConditionNode));
	}

	public boolean isConditionTable() {
		return ((getType() == ConditionTableNode));
	}

	/**
	 * Returns true if this node is a leaf. public boolean isLeaf() { return(
	 * isAssignment() || isTrial() || isFactorLevel() || isCondition() ); }
	 */
	/** Used for debugging purposes only. */
	private void describe(String c) {
		System.out.println(c);
		System.out.println("  type = " + type + " (" + getTypeName() + ")");
		System.out.println("  typeModifier = " + typeModifier);
		System.out.println("  name = " + name);
		if (parNames == null) {
			System.out.println("  parNames = null");
		} else if (parNames.length == 0) {
			System.out.println("  parNames = []");
		} else if (parNames.length == 1) {
			System.out.println("  parNames = [" + parNames[0] + "]");
		} else {
			System.out.print("  parNames = [" + parNames[0]);
			for (int i = 1; i < parNames.length; i++)
				System.out.print(", " + parNames[i]);
			System.out.println("]");
		}
		if (parValues == null) {
			System.out.println("  parValues = null");
		} else if (parValues.length == 0) {
			System.out.println("  parValues = []");
		} else if (parValues.length == 1) {
			System.out.println("  parValues = [" + parValues[0] + "]");
		} else {
			System.out.print("  parValues = [" + parValues[0]);
			for (int i = 1; i < parValues.length; i++)
				System.out.print(", " + parValues[i]);
			System.out.println("]");
		}
	}

	/**
	 * Checks whether the receiver allows the given node as its child.
	 * 
	 * @param n
	 *            the node which should be checked.
	 * @return true if the node given as an argument is allowed as a child of
	 *         this node.
	 */
	public boolean allowsAsChild(ExDesignNode n) {
		boolean r = false;
		switch (getType()) {
		case ExperimentNode:
			r = (n.isContext() || n.isProcedure() || n.isFactors());
			break;
		case ContextNode:
			r = (n.isAssignment() || n.isAssignmentGroup() || n.isDisplayList());
			break;
		case ProcedureStartDisplayNode:
		case ProcedureEndDisplayNode:
		case SessionStartDisplayNode:
		case SessionEndDisplayNode:
		case BlockStartDisplayNode:
		case BlockEndDisplayNode:
		case TrialDisplayNode:
			r = (n.isDisplay());
			break;
		case DisplayNode:
			r = (n.isAssignment());
			break;
		case AssignmentGroupNode:
			r = (n.isAssignment() && !n.isAdjustParamAssignment());
			break;
		case NewParamAssignmentNode:
			break;
		case AdjustableParamAssignmentNode:
			break;
		case AssignmentNode:
			break;
		case ProcedureNode:
			r = (n.isParamDefinition() || n.isSession());
			break;
		case SessionNode:
			r = (n.isBlock());
			break;
		case BlockNode:
			r = (n.isTrial());
			break;
		case TrialNode:
			break;
		case FactorsNode:
			r = (n.isFactor() || n.isConditionTable());
			break;
		case RandomFactorNode:
		case IndependentFactorNode:
		case DependentFactorNode:
		case CovariateFactorNode:
			r = (n.isFactorLevel());
			break;
		case ConditionTableNode:
			r = (n.isCondition());
			break;
		case FactorLevelNode:
		case ConditionNode:
			break;
		}
		return (r);
	}

	// --------------------------------------------------
	// Implementation of TreeNode
	// --------------------------------------------------
	/** Returns the children of the receiver as an Enumeration. */
	public Enumeration children() {
		return (new EnumerationAdapter(childrenList.iterator()));
	}

	/** Returns true if the receiver allows children. */
	public boolean getAllowsChildren() {
		return !isLeaf();
	}

	/** Returns the child TreeNode at index childIndex. */
	public TreeNode getChildAt(int index) {
		TreeNode d = null;
		if (isAssignment() && (index == 0)) {
			d = getParValue(0);
		} else {
			d = (TreeNode) (getChildrenList().get(index));
		}
		return d;
	}

	/** Returns the number of children TreeNodes the receiver contains. */
	public int getChildCount() {
		int i = 0;
		if (isAssignment()) {
			i = 1;
		} else {
			ArrayList a = getChildrenList();
			if (a != null) {
				i = a.size();
			}
		}
		return i;
	}

	/** Returns the index of node in the receivers children. */
	public int getIndex(TreeNode node) {
		int i = -1;
		if (isAssignment() && (node instanceof ExParValue)) {
			if (((ExParValue) node).equals(getParValue(0))) {
				i = 0;
			}
		} else {
			i = getChildrenList().indexOf(node);
		}
		return i;
	}

	/** Returns the parent TreeNode of the receiver. */
	public TreeNode getParent() {
		return parent;
	}

	/**
	 * Returns true if the receiver is a leaf. A leaf is a node which does not
	 * allow children.
	 */
	public boolean isLeaf() {
		return isTrial() || isFactorLevel() || isCondition()
				|| isRandomFactor() || isDependentFactor();
	}

	// --------------------------------------------
	// MutableTreeNode implementation
	// --------------------------------------------
	/** Adds child to the receiver at index. */
	public void insert(ExDesignNode child, int index) {
		child.setParent(this);
		childrenList.add(index, child);
	}

	/* Removes the child at index from the receiver. */
	public void remove(int index) {
		childrenList.remove(index);
	}

	/** Removes node from the receiver. */
	public void remove(ExDesignNode node) {
		childrenList.remove(childrenList.indexOf(node));
	}

	/** Removes the receiver from its parent. */
	public void removeFromParent() {
		ArrayList cld = parent.getChildrenList();
		cld.remove(cld.indexOf(this));
	}

	/** Sets the parent of the receiver to newParent. */
	/*
	 * public void setParent(ExDesignNode newParent) { parent =
	 * (ExDesignNode)newParent; }
	 */
	/** Resets the user object of the receiver to object. */
	public void setUserObject(Object object) {
	}
	// --------------------------------------------------
	// End of MutableTreeNode implementation
	// --------------------------------------------------
}
