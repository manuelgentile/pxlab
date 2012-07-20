package de.pxlab.pxl.gui;

import java.awt.*;
import java.io.StringReader;

import de.pxlab.pxl.*;
import de.pxlab.awtx.*;
import de.pxlab.pxl.parser.*;

/**
 * A dialog for entering arbitray experimental parameter values.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 07/30/01
 */
public class ParamValueEditDialog extends CancelOKButtonDialog {
	private TextArea text;

	public ParamValueEditDialog(String s) {
		super(new Frame(), " Edit parameter value");
		text = new TextArea(s, 10, 80, TextArea.SCROLLBARS_BOTH);
		text.setEditable(true);
		add(text, BorderLayout.CENTER);
		setResizable(true);
		pack();
		/*
		 * doesn't work anyhow addNotify(); text.setCaretPosition(0);
		 */
	}

	public String getText() {
		return text.getText();
	}

	public ExParValue getExParValue() {
		ExParValue v = null;
		try {
			// ExDesignTreeParser parser = new ExDesignTreeParser(new
			// StringReader(text.getText()), Base.getEncoding());
			ExDesignTreeParser parser = new ExDesignTreeParser(
					new StringReader(text.getText()));
			v = parser.assignableParameterValue();
		} catch (ParseException pex) {
			new de.pxlab.pxl.NonFatalError(pex.getMessage());
		} catch (TokenMgrError tex) {
			new de.pxlab.pxl.NonFatalError(tex.getMessage());
		}
		return v;
	}
}
