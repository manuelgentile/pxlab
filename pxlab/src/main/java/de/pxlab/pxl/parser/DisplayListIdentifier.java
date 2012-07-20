package de.pxlab.pxl.parser;

import de.pxlab.pxl.*;

/**
 * An object which identifies a runtime display list instance. The identifier
 * contains a class name and an instance modifier.
 * 
 * @author H. Irtel
 * @version 0.2.1
 */
/*
 * 
 * 2005/12/08 added data nodes.
 */
public class DisplayListIdentifier extends DisplayIdentifier {
	public DisplayListIdentifier(String cls, String n, int line, int col) {
		super(cls, n, line, col);
	}

	public DisplayListIdentifier(String cls, String n) {
		super(cls, n);
	}

	/**
	 * A utility method used by the parser in order to find the node type from
	 * the node name.
	 * 
	 * @param inDeclaration
	 *            true if the name was found in the Context() section of display
	 *            List declarations and false if the name was found in the
	 *            Procedure() section of the design file.
	 * @return the respective node identifier or (-1) if the argument was not a
	 *         valid procedure name.
	 */
	public int getNodeType(boolean inDeclaration) {
		String n = getClassName();
		// System.out.println("class name: " + n);
		if (n.startsWith("TrialData"))
			return (ExDesignNode.TrialDataDisplayNode);
		if (n.startsWith("Trial"))
			return (inDeclaration ? ExDesignNode.TrialDisplayNode
					: ExDesignNode.TrialNode);
		if (n.startsWith("BlockData"))
			return (ExDesignNode.BlockDataDisplayNode);
		if (n.startsWith("BlockEnd"))
			return (ExDesignNode.BlockEndDisplayNode);
		if (n.startsWith("Block"))
			return (inDeclaration ? ExDesignNode.BlockStartDisplayNode
					: ExDesignNode.BlockNode);
		if (n.startsWith("SessionData"))
			return (ExDesignNode.SessionDataDisplayNode);
		if (n.startsWith("SessionEnd"))
			return (ExDesignNode.SessionEndDisplayNode);
		if (n.startsWith("Session"))
			return (inDeclaration ? ExDesignNode.SessionStartDisplayNode
					: ExDesignNode.SessionNode);
		if (n.startsWith("ProcedureData"))
			return (ExDesignNode.ProcedureDataDisplayNode);
		if (n.startsWith("ProcedureEnd"))
			return (ExDesignNode.ProcedureEndDisplayNode);
		if (n.startsWith("Procedure"))
			return (inDeclaration ? ExDesignNode.ProcedureStartDisplayNode
					: ExDesignNode.ProcedureNode);
		if (n.startsWith("ExperimentData"))
			return (ExDesignNode.ExperimentDataDisplayNode);
		return (-1);
	}
}
