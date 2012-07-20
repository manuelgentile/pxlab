package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.awtx.*;
import de.pxlab.pxl.*;

/**
 * The superclass of all panels to define the value of an experimental
 * parameter.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 
 * 07/30/01
 * 
 * 02/13/02 allow hints to be switched on/off
 */
public class ExParControlPanel extends InsetPanel implements ActionListener {
	protected Panel cardPanel;
	protected CardLayout cardLayout;
	protected Button select;
	protected boolean showSimple;
	protected SingleParamControlPanel simplePanel;
	protected SingleParamControlPanel expressionPanel;
	private static String BUTTON_SIMPLE = "    Simple    ";
	private static String BUTTON_EXPRESSION = "Expression";

	/**
	 * Create an ExParControlPanel which sends its update messages to the given
	 * Component object and can control the ExPar object which is described by
	 * the given ExParDescriptor for the given Display object.
	 * 
	 * @param dspp
	 *            this is the Component which shows the Display object and which
	 *            receives the repaint() messages whenver a parameter value
	 *            changes. This ExParControlPanel object sends a repaint message
	 *            to this Component whenever the parameter has changed. This
	 *            parameter may be null if no visible Display is to be modified.
	 * @param dsp
	 *            the Display object whose parameter is controlled here. This
	 *            parameter may be null if no visible Display is to be modified.
	 * @param exd
	 *            the ExParDescriptor object which describes the ExPar parameter
	 *            to be controlled.
	 */
	public ExParControlPanel(Component dspp, Display dsp, ExParDescriptor exd,
			SingleParamControlPanel simplePanel) {
		// super(new BorderLayout(), new
		// Insets(PXLabGUI.smallInternalElementGap, PXLabGUI.internalBorder,
		// PXLabGUI.smallInternalElementGap, PXLabGUI.internalBorder));
		super(new BorderLayout(), new Insets(PXLabGUI.smallInternalElementGap,
				0, PXLabGUI.smallInternalElementGap, 0));
		if (Debug.layout())
			setBackground(Color.pink);
		this.simplePanel = simplePanel;
		Panel p1 = new Panel(new BorderLayout());
		if (Debug.layout())
			p1.setBackground(Color.orange);
		Label lb = new Label(exd.getName(), Label.LEFT);
		lb.setFont(Base.getSystemFont().deriveFont(Font.BOLD));
		lb.addMouseListener(new ParamVisitDetector(exd, null));
		p1.add(lb, BorderLayout.WEST);
		showSimple = initialState(exd);
		select = new Button(showSimple ? BUTTON_EXPRESSION : BUTTON_SIMPLE);
		select.addActionListener(this);
		p1.add(select, BorderLayout.EAST);
		add(p1, BorderLayout.NORTH);
		cardLayout = new CardLayout();
		cardPanel = new Panel(cardLayout);
		cardPanel.add(simplePanel, BUTTON_SIMPLE);
		expressionPanel = new ExpressionParamControlPanel(dspp, dsp, exd, false);
		cardPanel.add(expressionPanel, BUTTON_EXPRESSION);
		add(cardPanel, BorderLayout.CENTER);
		cardLayout.show(cardPanel, showSimple ? BUTTON_SIMPLE
				: BUTTON_EXPRESSION);
		// setBackground(Color.yellow);
	}

	private boolean initialState(ExParDescriptor exd) {
		ExParValue v = exd.getValue().getValue();
		return v.isLiteralConstant() && (v.length == 1);
	}

	public void actionPerformed(ActionEvent e) {
		Button s = (Button) e.getSource();
		if (s == select) {
			showSimple = !showSimple;
			cardLayout.show(cardPanel, showSimple ? BUTTON_SIMPLE
					: BUTTON_EXPRESSION);
			s.setLabel(showSimple ? BUTTON_EXPRESSION : BUTTON_SIMPLE);
			s.repaint();
		}
	}

	public void clearPanel() {
		simplePanel.clearPanel();
		expressionPanel.clearPanel();
	}

	public void closeOK() {
		simplePanel.closeOK();
		expressionPanel.closeOK();
	}

	public void closeCancel() {
		simplePanel.closeCancel();
		expressionPanel.closeCancel();
	}
}
