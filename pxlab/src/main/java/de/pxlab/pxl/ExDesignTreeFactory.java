package de.pxlab.pxl;

import java.util.*;

/**
 * This class contains methods for creating a compileable design tree.
 * Experiments can extend this class to create an experimental design.
 * 
 * @author H. Irtel
 * @version 0.1.1
 * @see ExDesignNode
 * @see de.pxlab.pxl.parser.ExDesignTreeParser
 */
public class ExDesignTreeFactory {
	/**
	 * These are node variables which store the most recently defined node of
	 * the respective type.
	 */
	private ExDesignNode experimentNode = null;
	private ExDesignNode contextNode = null;
	private ExDesignNode assignmentGroupNode = null;
	private ExDesignNode displayListNode = null;
	private ExDesignNode dataDisplayListNode = null;
	private ExDesignNode displayNode = null;
	private ExDesignNode factorsNode = null;
	private ExDesignNode factorNode = null;
	private ExDesignNode conditionTableNode = null;
	private ExDesignNode procedureNode = null;
	private ExDesignNode sessionNode = null;
	private ExDesignNode blockNode = null;
	/**
	 * Stores the arrays of procedure unit parameter names. These names are
	 * collected during display list definitions and are retrieved later for
	 * executing procedure calls.
	 */
	private HashMap procedureUnitParNames = new HashMap(10);

	private void enterProcedureUnitParNames(String n, String[] a) {
		if (a != null) {
			procedureUnitParNames.put(n, a);
		}
	}

	private String[] getProcedureUnitParNames(String n) {
		return (String[]) procedureUnitParNames.get(n);
	}

	public ExDesignTreeFactory() {
		Debug.show(Debug.FACTORY, "Instantiated ExDesignTreeFactory");
	}

	// -------------------------------------------------------------------------------------
	public ExDesignNode createExperimentNode(String[] parNames) {
		experimentNode = new ExDesignNode(ExDesignNode.ExperimentNode,
				parNames, null, 3);
		// experimentParNames = parNames;
		return (experimentNode);
	}

	public void addContextNode(String[] parNames) {
		contextNode = new ExDesignNode(ExDesignNode.ContextNode, parNames,
				null, 1);
		experimentNode.add(contextNode);
	}

	public void addAssignmentGroupNode(String[] parNames) {
		assignmentGroupNode = new ExDesignNode(
				ExDesignNode.AssignmentGroupNode, parNames, null, 1);
		contextNode.add(assignmentGroupNode);
	}

	public void addGlobalAssignmentNode(int type, String name, String parName,
			ExParValue parValue) {
		ExDesignNode node = new ExDesignNode(type, parName, parValue, 0);
		node.setName(name);
		node.setTypeModifier(ExDesignNode.ExplicitAssignment);
		assignmentGroupNode.add(node);
		if (Debug.isActive(Debug.FACTORY))
			assignmentGroupNode.print();
	}

	public void addLocalAssignmentNode(int type, String name, String parName,
			ExParValue parValue) {
		ExDesignNode node = new ExDesignNode(type, parName, parValue, 0);
		node.setName(name);
		node.setTypeModifier(ExDesignNode.ExplicitAssignment);
		displayNode.add(node);
		if (Debug.isActive(Debug.FACTORY))
			displayNode.print();
	}

	public void addDisplayListNode(int type, String name, String[] parNames) {
		displayListNode = new ExDesignNode(type, parNames, null, 1);
		displayListNode.setName(name);
		enterProcedureUnitParNames(name, parNames);
		contextNode.add(displayListNode);
		if (Debug.isActive(Debug.FACTORY))
			contextNode.print();
	}

	public void addDataDisplayListNode(int type, String name, String[] parNames) {
		dataDisplayListNode = new ExDesignNode(type, parNames, null, 1);
		dataDisplayListNode.setName(name);
		contextNode.add(dataDisplayListNode);
		if (Debug.isActive(Debug.FACTORY))
			contextNode.print();
	}

	public void addDisplayNode(String name) {
		displayNode = new ExDesignNode(ExDesignNode.DisplayNode, name, null, 1);
		displayNode.setName(name);
		displayListNode.add(displayNode);
		if (Debug.isActive(Debug.FACTORY))
			displayListNode.print();
	}

	public void addDataDisplayNode(String name, String[] parNames) {
		displayNode = new ExDesignNode(ExDesignNode.DataDisplayNode, parNames,
				null, 1);
		displayNode.setName(name);
		dataDisplayListNode.add(displayNode);
		if (Debug.isActive(Debug.FACTORY))
			dataDisplayListNode.print();
	}

	public void addFactorsNode(String[] parNames) {
		factorsNode = new ExDesignNode(ExDesignNode.FactorsNode, parNames,
				null, 1);
		experimentNode.add(factorsNode);
	}

	public void addFactorNode(int type, String[] parNames) {
		factorNode = new ExDesignNode(type, parNames, null, 1);
		factorsNode.add(factorNode);
	}

	public void addFactorLevelNode(ExParValue[] parValues) {
		ExDesignNode node = new ExDesignNode(ExDesignNode.FactorLevelNode,
				factorNode.getParNames(), parValues, 0);
		factorNode.add(node);
	}

	public void addConditionTableNode(String[] parNames) {
		conditionTableNode = new ExDesignNode(ExDesignNode.ConditionTableNode,
				parNames, null, 1);
		factorsNode.add(conditionTableNode);
	}

	public void addConditionNode(ExParValue[] parValues) {
		ExDesignNode node = new ExDesignNode(ExDesignNode.ConditionNode,
				conditionTableNode.getParNames(), parValues, 0);
		conditionTableNode.add(node);
	}

	public void addProcedureNode(String name, ExParValue[] parValues) {
		procedureNode = new ExDesignNode(ExDesignNode.ProcedureNode,
				getProcedureUnitParNames(name), parValues, 1);
		procedureNode.setName(name);
		experimentNode.add(procedureNode);
	}

	public void addSessionNode(String name, ExParValue[] parValues) {
		sessionNode = new ExDesignNode(ExDesignNode.SessionNode,
				getProcedureUnitParNames(name), parValues, 1);
		sessionNode.setName(name);
		procedureNode.add(sessionNode);
	}

	public void addBlockNode(String name, ExParValue[] parValues) {
		blockNode = new ExDesignNode(ExDesignNode.BlockNode,
				getProcedureUnitParNames(name), parValues, 1);
		blockNode.setName(name);
		sessionNode.add(blockNode);
	}

	public void addTrialNode(String name, ExParValue[] parValues) {
		ExDesignNode node = new ExDesignNode(ExDesignNode.TrialNode,
				getProcedureUnitParNames(name), parValues, 1);
		node.setName(name);
		blockNode.add(node);
	}
}
