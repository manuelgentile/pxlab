package de.pxlab.pxl.gui;

import de.pxlab.pxl.NonFatalError;

public class RuntimeParseError extends NonFatalError {
	public RuntimeParseError(String em, Throwable mme) {
		super("Syntax Error in " + em + "\n" + mme.getMessage());
	}
}
