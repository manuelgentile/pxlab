package de.pxlab.pxl;

/**
 * This is a function valued ExParValue.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see ExParExpression
 */
public class ExParValueFunction extends ExParValue {
	/**
	 * Create an ExParValue object which actually has a function as its value.
	 * The function has no arguments.
	 * 
	 * @param functionCode
	 *            the code of the function as defined in ExParExpression.
	 */
	public ExParValueFunction(int functionCode) {
		super(new ExParExpression(functionCode));
	}

	/**
	 * Create an ExParValue object which actually has a function as its value.
	 * The function has 1 argument.
	 * 
	 * @param functionCode
	 *            the code of the function as defined in ExParExpression.
	 * @param arg
	 *            function argument.
	 */
	public ExParValueFunction(int functionCode, ExParValue arg) {
		super(new ExParExpression(functionCode), arg);
	}

	/**
	 * Create an ExParValue object which actually has a function as its value.
	 * The function has 2 arguments.
	 * 
	 * @param functionCode
	 *            the code of the function as defined in ExParExpression.
	 * @param arg1
	 *            first function argument.
	 * @param arg2
	 *            second function argument.
	 */
	public ExParValueFunction(int functionCode, ExParValue arg1, ExParValue arg2) {
		super(new ExParExpression(functionCode), arg1, arg2);
	}

	/**
	 * Create an ExParValue object which actually has a function as its value.
	 * The function has 3 arguments.
	 * 
	 * @param functionCode
	 *            the code of the function as defined in ExParExpression.
	 * @param arg1
	 *            first function argument.
	 * @param arg2
	 *            second function argument.
	 * @param arg3
	 *            third function argument.
	 */
	public ExParValueFunction(int functionCode, ExParValue arg1,
			ExParValue arg2, ExParValue arg3) {
		super(new ExParExpression(functionCode), arg1, arg2, arg3);
	}
}
