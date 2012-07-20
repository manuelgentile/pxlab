package de.pxlab.pxl;

/**
 * Experimental design tree node identifiers and type modifier patterns.
 * 
 * @author H. Irtel
 * @version 0.5.1
 */
/*
 * 
 * 03/10/04 added RandomFactorNode
 * 
 * 2005/02/11 added ProcedureStart/EndDisplayNode
 * 
 * 2005/12/08 added data analysis nodes
 */
public class ExDesignNodeSupport {
	// -------------------------------------------------------------------
	// The type values are used as indices for the type name array
	// -------------------------------------------------------------------
	/** This is the root node of the design tree. */
	public static final int ExperimentNode = 0;
	/** This node contains the definietions of sessions, blocks, and trials. */
	public static final int ContextNode = 1;
	// -------------------------------------------------------------------
	// The display list node id numbers MUST be grouped together
	// -------------------------------------------------------------------
	/**
	 * This node defines the display list for the start of the experimental
	 * procedure.
	 */
	public static final int ProcedureStartDisplayNode = 2;
	/** This node defines the display list for the start of a session. */
	public static final int SessionStartDisplayNode = 3;
	/** This node defines the display list for the start of a block. */
	public static final int BlockStartDisplayNode = 4;
	/** This node defines the display list for a trial. */
	public static final int TrialDisplayNode = 5;
	/**
	 * This node defines the display list for the end of an experimental
	 * procedure.
	 */
	public static final int ProcedureEndDisplayNode = 6;
	/** This node defines the display list for the end of a session. */
	public static final int SessionEndDisplayNode = 7;
	/** This node defines the display list for the end of a block. */
	public static final int BlockEndDisplayNode = 8;
	// -------------------------------------------------------------------
	// End of display list node id numbers
	// -------------------------------------------------------------------
	/**
	 * This node adds a display to its parent which must be a display list node.
	 */
	public static final int DisplayNode = 9;
	/**
	 * This is a group of parameter assignements. Its only purpose is to make it
	 * easier to display large lists of assignments in the design tree.
	 */
	public static final int AssignmentGroupNode = 10;
	// -------------------------------------------------------------------
	// The assignment node id numbers MUST be grouped together
	// -------------------------------------------------------------------
	/**
	 * This is an assignement where an experimental parameter is assigned a
	 * value.
	 */
	public static final int AssignmentNode = 11;
	/**
	 * This is an assignment node which simultanously creates the experimental
	 * parameter which is assigned a value.
	 */
	public static final int NewParamAssignmentNode = 12;
	/** This node defines the adaptive parameter for a display. */
	// public static final int AdaptiveParamAssignmentNode = 13;
	/** This node defines the adjustable parameter for a display. */
	public static final int AdjustableParamAssignmentNode = 14;
	// -------------------------------------------------------------------
	// The procedure unit id numbers MUST be at a constant lag to the
	// respective parameter node ids.
	// -------------------------------------------------------------------
	/** This node contains the list of sessions, blocks, and trials. */
	public static final int ProcedureNode = 15;
	/** This is an experimental session. */
	public static final int SessionNode = 16;
	/** This is a block of trials within a session. */
	public static final int BlockNode = 17;
	/** This is a single trial. */
	public static final int TrialNode = 18;
	/**
	 * This optional node contains the definition of the factorial design used
	 * for statistical analysis.
	 */
	public static final int FactorsNode = 19;
	/** These are the random factors of the experiment. */
	public static final int RandomFactorNode = 20;
	/** These are the independent factors of the experiment. */
	public static final int IndependentFactorNode = 21;
	/** These are the dependent factors of the experiment. */
	public static final int DependentFactorNode = 22;
	/** These are the covariate factors of the experiment. */
	public static final int CovariateFactorNode = 23;
	/** This is a factor level for some factor of the experiment. */
	public static final int FactorLevelNode = 24;
	/** This is the table of factorial conditions of the experiment. */
	public static final int ConditionTableNode = 25;
	/** This is a factorial condition of the experiment. */
	public static final int ConditionNode = 26;
	// -------------------------------------------------------------------
	// The data node id numbers MUST be consecutive in this order.
	// -------------------------------------------------------------------
	/** Data analysis using the procedure data tree. */
	public static final int ExperimentDataDisplayNode = 27;
	/** Data analysis using the procedure data tree. */
	public static final int ProcedureDataDisplayNode = 28;
	/** Data analysis using the session data tree. */
	public static final int SessionDataDisplayNode = 29;
	/** Data analysis using the block data tree. */
	public static final int BlockDataDisplayNode = 30;
	/** Data analysis using the trial data. */
	public static final int TrialDataDisplayNode = 31;
	/** A data analysis object node. */
	public static final int DataDisplayNode = 32;
	// -------------------------------------------------------------------
	/**
	 * This array contains the names of the nodes we know. Note that most of
	 * these names MUST be identical with the corresponding token names in the
	 * class ExDesignTreeParser. Otherwise output files may not be read back
	 * into the parser.
	 */
	public static final String typeName[] = { "Experiment", "Context",
			"Procedure", "Session", "Block", "Trial", "ProcedureEnd",
			"SessionEnd", "BlockEnd", "Display", "AssignmentGroup",
			"Assignment", "NewParameter", "AdaptiveParameter",
			"AdjustableParameter", "Procedure", "Session", "Block", "Trial",
			"Factors", "RandomFactor", "IndependentFactor", "DependentFactor",
			"CovariateFactor", "FactorLevel", "ConditionTable", "Condition",
			"ExperimentData", "ProcedureData", "SessionData", "BlockData",
			"TrialData", "Data", };
	// -------------------------------------------------------------------
	// These are values for the ExDesignNode.typeModifier parameter. Note that
	// the modifier values are valid only for that type which they are
	// intended to modify.
	// Type modifiers MUST be powers of 2 !
	// -------------------------------------------------------------------
	/**
	 * Inidicates that a block node is the first block of a session. Only
	 * applicable to nodes of type BlockNode.
	 */
	public static final int FirstBlock = 1;
	/**
	 * Inidicates that a block node is the last block of a session. Only
	 * applicable to nodes of type BlockNode.
	 */
	public static final int LastBlock = 2;
	/**
	 * Inidicates that an independent factor is a within-factor. Only applicable
	 * to nodes of type IndependentFactorNode.
	 */
	public static final int WithinFactor = 1;
	/**
	 * Inidicates that an independent factor is a between-factor. Only
	 * applicable to nodes of type IndependentFactorNode.
	 */
	public static final int BetweenFactor = 2;
	/**
	 * Inidicates that an independent factor is a random-factor. Only applicable
	 * to nodes of type IndependentFactorNode.
	 */
	public static final int RandomFactor = 4;
	/**
	 * Indicates that this assignment node was generated by an explicit
	 * assignment in the design file or that the assignment node has been
	 * created or modified by the design editor.
	 */
	public static final int ExplicitAssignment = 1;
}
