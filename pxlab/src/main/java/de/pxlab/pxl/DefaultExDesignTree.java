package de.pxlab.pxl;

/**
 * The default experimental design tree.
 * 
 * @author H. Irtel
 * @version 0.1.2
 * 
 *          08/05/02 changed naming of displayLists (er)
 * 
 *          08/22/02 new instance naming schema (H. I.)
 */
public class DefaultExDesignTree extends ExDesignNode {
	/** Create a default experimental design tree. */
	public DefaultExDesignTree() {
		super(ExDesignNode.ExperimentNode, 1);
		add(defaultContextTree());
		add(defaultFactorsTree());
		add(defaultProcedureTree());
	}

	/**
	 * Create a default context subtree of an experimental design.
	 * 
	 * @return the root node of the context subtree of the experimental design.
	 */
	private ExDesignNode defaultContextTree() {
		ExDesignNode context = new ExDesignNode(ExDesignNode.ContextNode, 1);
		ExDesignNode dl;
		ExDesignNode dsp;
		String dspn = "Message";
		// String[] display = {"SessionStartMessage", "SessionEndMessage",
		// "BlockStartMessage", "BlockEndMessage", "TrialMessage"};
		// String title;
		dl = new ExDesignNode(ExDesignNode.AssignmentGroupNode,
				(String[]) null, (ExParValue[]) null, 1);
		context.add(dl);
		dl = new ExDesignNode(ExDesignNode.SessionStartDisplayNode,
				(String[]) null, (ExParValue[]) null, 1);
		dl.setName(dl.getTypeName());
		dl.add(dsp = new ExDesignNode(ExDesignNode.DisplayNode, dspn, null, 1));
		dsp.setName(dspn);
		context.add(dl);
		dl = new ExDesignNode(ExDesignNode.BlockStartDisplayNode,
				(String[]) null, (ExParValue[]) null, 1);
		dl.setName(dl.getTypeName());
		dl.add(dsp = new ExDesignNode(ExDesignNode.DisplayNode, dspn, null, 1));
		dsp.setName(dspn);
		context.add(dl);
		dl = new ExDesignNode(ExDesignNode.TrialDisplayNode, (String[]) null,
				(ExParValue[]) null, 1);
		dl.setName(dl.getTypeName());
		dl.add(dsp = new ExDesignNode(ExDesignNode.DisplayNode, dspn, null, 1));
		dsp.setName(dspn);
		context.add(dl);
		dl = new ExDesignNode(ExDesignNode.BlockEndDisplayNode, 1);
		dl.setName(dl.getTypeName());
		dl.add(dsp = new ExDesignNode(ExDesignNode.DisplayNode, dspn, null, 1));
		dsp.setName(dspn);
		context.add(dl);
		dl = new ExDesignNode(ExDesignNode.SessionEndDisplayNode, 1);
		dl.setName(dl.getTypeName());
		dl.add(dsp = new ExDesignNode(ExDesignNode.DisplayNode, dspn, null, 1));
		dsp.setName(dspn);
		context.add(dl);
		return (context);
	}

	/**
	 * Create a default procedure subtree of an experimental design.
	 * 
	 * @return the root node of the procedure subtree of the experimental
	 *         design.
	 */
	private ExDesignNode defaultProcedureTree() {
		ExDesignNode procedure = new ExDesignNode(ExDesignNode.ProcedureNode, 4);
		procedure.setName("Procedure");
		ExDesignNode session = new ExDesignNode(ExDesignNode.SessionNode,
				(String[]) null, null, 1);
		session.setName("Session");
		procedure.add(session);
		ExDesignNode block = new ExDesignNode(ExDesignNode.BlockNode,
				(String[]) null, null, 1);
		block.setName("Block");
		session.add(block);
		ExDesignNode trial = new ExDesignNode(ExDesignNode.TrialNode,
				(String[]) null, null, 0);
		trial.setName("Trial");
		block.add(trial);
		return (procedure);
	}

	/**
	 * Create a default factors subtree of an experimental design.
	 * 
	 * @return the root node of the factors subtree of the experimental design.
	 */
	private ExDesignNode defaultFactorsTree() {
		ExDesignNode factors = new ExDesignNode(ExDesignNode.FactorsNode, 1);
		String[] f = { "Factor1" };
		ExDesignNode factor = new ExDesignNode(
				ExDesignNode.IndependentFactorNode, f, null, 2);
		factors.add(factor);
		ExParValue[] fl1 = { new ExParValue(1) };
		ExDesignNode factorLevel = new ExDesignNode(
				ExDesignNode.FactorLevelNode, f, fl1, 0);
		factor.add(factorLevel);
		ExParValue[] fl2 = { new ExParValue(2) };
		factorLevel = new ExDesignNode(ExDesignNode.FactorLevelNode, f, fl2, 0);
		factor.add(factorLevel);
		String[] df = { "ResponseTime" };
		factor = new ExDesignNode(ExDesignNode.DependentFactorNode, df, null, 2);
		factors.add(factor);
		return (factors);
	}
}
