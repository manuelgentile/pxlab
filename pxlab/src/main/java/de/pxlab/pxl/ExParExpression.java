package de.pxlab.pxl;

/** A class to store and compute expressions and functions of
 ExParValue objects in an expression tree.

 @version 0.1.8

 */
/*

 2005/01/10 added BRACKET_OP and removed printing brackets from all other expressions. 

 2005/02/18 functions for screen border positions and random integer values

 2005/07/01 screenWidth() and screenHeight()

 2005/07/13 lang2() and lang3()

 2005/07/22 arrayOf2() and arrayOf3()

 2005/08/03 color functions

 2005/10/16 added idiv() function for integer division

 2007/02/09 expressionFile()

 2007/06/22 undefined()

 2007/11/01 fromXYZ()

 */
import de.pxlab.util.StringExt;

public class ExParExpression {
	// opCode values
	public static final int NO_EXPRESSION = 0;
	public static final int ADD_OP = 1;
	public static final int SUB_OP = 2;
	public static final int MUL_OP = 3;
	public static final int DIV_OP = 4;
	public static final int MOD_OP = 5;
	public static final int POS_OP = 6;
	public static final int NEG_OP = 7;
	public static final int GT_OP = 8;
	public static final int GE_OP = 9;
	public static final int LT_OP = 10;
	public static final int LE_OP = 11;
	public static final int EQ_OP = 12;
	public static final int NE_OP = 13;
	public static final int AND_OP = 14;
	public static final int OR_OP = 15;
	public static final int NOT_OP = 16;
	public static final int SHIFT_RIGHT_OP = 17;
	public static final int SHIFT_LEFT_OP = 18;
	public static final int SHIFT_RIGHT_UNSIGNED_OP = 19;
	public static final int BIN_AND_OP = 20;
	public static final int BIN_OR_OP = 21;
	public static final int BIN_XOR_OP = 22;
	public static final int BIN_COMPLEMENT_OP = 23;
	public static final int CAT_OP = 24;
	public static final int CONDITIONAL_OP = 25;
	public static final int BRACKET_OP = 26;
	private static final int FIRST_FUNCTION = 27;
	public static final int FROM_CIELAB = FIRST_FUNCTION + 0;
	public static final int TO_CIELAB = FIRST_FUNCTION + 1;
	public static final int FROM_CIELABLCH = FIRST_FUNCTION + 2;
	public static final int TO_CIELABLCH = FIRST_FUNCTION + 3;
	public static final int ADD_COLORS = FIRST_FUNCTION + 4;
	public static final int MIX_COLORS = FIRST_FUNCTION + 5;
	public static final int SCREEN_TOP = FIRST_FUNCTION + 6;
	public static final int SCREEN_RIGHT = FIRST_FUNCTION + 7;
	public static final int SCREEN_BOTTOM = FIRST_FUNCTION + 8;
	public static final int SCREEN_LEFT = FIRST_FUNCTION + 9;
	public static final int SCREEN_WIDTH = FIRST_FUNCTION + 10;
	public static final int SCREEN_HEIGHT = FIRST_FUNCTION + 11;
	public static final int RANDOM_INT = FIRST_FUNCTION + 12;
	public static final int VALUE_OF = FIRST_FUNCTION + 13;
	public static final int LANG2 = FIRST_FUNCTION + 14;
	public static final int LANG3 = FIRST_FUNCTION + 15;
	public static final int CONCAT = FIRST_FUNCTION + 16;
	public static final int ARRAY_OF_2 = FIRST_FUNCTION + 17;
	public static final int ARRAY_OF_3 = FIRST_FUNCTION + 18;
	public static final int COLOR_AT_LUM = FIRST_FUNCTION + 19;
	public static final int WHITE = FIRST_FUNCTION + 20;
	public static final int LIGHT_GRAY = FIRST_FUNCTION + 21;
	public static final int GRAY = FIRST_FUNCTION + 22;
	public static final int DARK_GRAY = FIRST_FUNCTION + 23;
	public static final int BLACK = FIRST_FUNCTION + 24;
	public static final int YELLOW = FIRST_FUNCTION + 25;
	public static final int CYAN = FIRST_FUNCTION + 26;
	public static final int MAGENTA = FIRST_FUNCTION + 27;
	public static final int GREEN = FIRST_FUNCTION + 28;
	public static final int RED = FIRST_FUNCTION + 29;
	public static final int BLUE = FIRST_FUNCTION + 30;
	public static final int IDIV = FIRST_FUNCTION + 31;
	public static final int SIN = FIRST_FUNCTION + 32;
	public static final int COS = FIRST_FUNCTION + 33;
	public static final int TAN = FIRST_FUNCTION + 34;
	public static final int ATAN = FIRST_FUNCTION + 35;
	public static final int SQRT = FIRST_FUNCTION + 36;
	public static final int LOG = FIRST_FUNCTION + 37;
	public static final int LOG10 = FIRST_FUNCTION + 38;
	public static final int EXP = FIRST_FUNCTION + 39;
	public static final int POW = FIRST_FUNCTION + 40;
	public static final int ABS = FIRST_FUNCTION + 41;
	public static final int CEIL = FIRST_FUNCTION + 42;
	public static final int FLOOR = FIRST_FUNCTION + 43;
	public static final int ROUND = FIRST_FUNCTION + 44;
	public static final int FROM_DEV_RGB = FIRST_FUNCTION + 45;
	public static final int TO_DEV_RGB = FIRST_FUNCTION + 46;
	public static final int TO_HEX_DEV_RGB = FIRST_FUNCTION + 47;
	public static final int RUNTIME_ID = FIRST_FUNCTION + 48;
	public static final int NEXT_NUMBER = FIRST_FUNCTION + 49;
	public static final int FROM_YXY = FIRST_FUNCTION + 50;
	public static final int EXPRESSION_FILE = FIRST_FUNCTION + 51;
	public static final int UNDEFINED_VALUE = FIRST_FUNCTION + 52;
	public static final int FROM_XYZ = FIRST_FUNCTION + 53;
	public static final int TO_XYZ = FIRST_FUNCTION + 54;
	// The index of any function name in this array MUST be identical
	// to its (opCode-FIRST_FUNCTION).
	private static String[] function = { "fromCIELab", "toCIELab",
			"fromCIELabLCh", "toCIELabLCh", "addColors", "mixColors",
			"screenTop", "screenRight", "screenBottom", "screenLeft",
			"screenWidth", "screenHeight", "randomInt", "valueOf", "lang2",
			"lang3", "cat", "arrayOf2", "arrayOf3", "colorAtLum", "white",
			"lightGray", "gray", "darkGray", "black", "yellow", "cyan",
			"magenta", "green", "red", "blue", "idiv", "sin", "cos", "tan",
			"atan", "sqrt", "log", "log10", "exp", "pow", "abs", "ceil",
			"floor", "round", "fromDevRGB", "toDevRGB", "toDevRGBHex",
			"runtimeID", "nextNumber", "fromYxy", "expressionFile",
			"undefined", "fromXYZ", "toXYZ", };
	private static java.util.Random rand = new java.util.Random(
			System.currentTimeMillis());

	/**
	 * Get the op code of the given function.
	 * 
	 * @param fn
	 *            function name
	 * @return the op code of the given function or (-1) if the argument is not
	 *         contained in the list of known function names.
	 */
	public static int opCodeFor(String fn) {
		int i = StringExt.indexOf(fn, function);
		if (i >= 0) {
			i += FIRST_FUNCTION;
		}
		return i;
	}
	private int opCode;

	public ExParExpression(int opCode) {
		this.opCode = opCode;
	}

	public ExParValue valueOf(ExParValue[] a) {
		ExParValue r = null;
		if (a.length >= 3) {
			r = valueOf(a[0], a[1], a[2]);
		} else if (a.length == 2) {
			r = valueOf(a[0], a[1], (ExParValue) null);
		} else if (a.length == 1) {
			r = valueOf(a[0], (ExParValue) null, (ExParValue) null);
		}
		return r;
	}

	public ExParValue valueOf(ExParValue a, ExParValue b, ExParValue c) {
		// Debug.show(Debug.EXPR, "ExParExpression.valueOf() opCode = " + opCode
		// + " a=" + a + " b=" + b + " c=" + c);
		switch (opCode) {
		case ADD_OP:
			return a.add(b);
		case SUB_OP:
			return a.sub(b);
		case MUL_OP:
			return a.mul(b);
		case DIV_OP:
			return a.div(b);
		case MOD_OP:
			return a.mod(b);
		case POS_OP:
			return a.pos();
		case NEG_OP:
			return a.neg();
		case GT_OP:
			return a.gt(b);
		case GE_OP:
			return a.ge(b);
		case LT_OP:
			return a.lt(b);
		case LE_OP:
			return a.le(b);
		case EQ_OP:
			return a.eq(b);
		case NE_OP:
			return a.neq(b);
		case AND_OP:
			return a.and(b);
		case OR_OP:
			return a.or(b);
		case NOT_OP:
			return a.not();
		case SHIFT_RIGHT_OP:
			return a.shiftRight(b);
		case SHIFT_LEFT_OP:
			return a.shiftLeft(b);
		case SHIFT_RIGHT_UNSIGNED_OP:
			return a.shiftRightUnsigned(b);
		case BIN_AND_OP:
			return a.binAnd(b);
		case BIN_OR_OP:
			return a.binOr(b);
		case BIN_XOR_OP:
			return a.binXor(b);
		case BIN_COMPLEMENT_OP:
			return a.binComplement();
		case CAT_OP:
			return a.cat(b);
		case CONDITIONAL_OP:
			return (a.getInt() != 0) ? b.getValue() : c.getValue();
		case BRACKET_OP:
			return a.getValue();
		case FROM_CIELAB:
			return new ExParValue(PxlColor.instance(PxlColor.CS_LabStar,
					a.getDoubleArray()));
		case TO_CIELAB:
			return new ExParValue(a.getPxlColor()
					.transform(PxlColor.CS_LabStar));
		case FROM_CIELABLCH:
			return new ExParValue(PxlColor.instance(PxlColor.CS_LabLChStar,
					a.getDoubleArray()));
		case TO_CIELABLCH:
			return new ExParValue(a.getPxlColor().transform(
					PxlColor.CS_LabLChStar));
		case ADD_COLORS:
			return new ExParValue(a.getPxlColor().add(b.getPxlColor()));
		case MIX_COLORS:
			return new ExParValue(a.getPxlColor().mix(b.getPxlColor()));
		case SCREEN_TOP:
			return new ExParValue(-Base.getScreenHeight() / 2);
		case SCREEN_RIGHT:
			return new ExParValue(Base.getScreenWidth() / 2);
		case SCREEN_BOTTOM:
			return new ExParValue(Base.getScreenHeight() / 2);
		case SCREEN_LEFT:
			return new ExParValue(-Base.getScreenWidth() / 2);
		case SCREEN_WIDTH:
			return new ExParValue(Base.getScreenWidth());
		case SCREEN_HEIGHT:
			return new ExParValue(Base.getScreenHeight());
		case RANDOM_INT:
			return new ExParValue(rand.nextInt(a.getInt()));
		case VALUE_OF:
			return a.getValueAt(b.getInt());
		case LANG2:
			return ExParValue.getValueForLanguage(a, b);
		case LANG3:
			return ExParValue.getValueForLanguage(a, b, c);
		case CONCAT:
			return a.cat(b);
		case ARRAY_OF_2:
			return ExParValue.arrayOf(a, b);
		case ARRAY_OF_3:
			return ExParValue.arrayOf(a, b, c);
		case COLOR_AT_LUM:
			PxlColor x = a.getPxlColor();
			x.setY(b.getDouble());
			return new ExParValue(x);
		case WHITE:
		case LIGHT_GRAY:
		case GRAY:
		case DARK_GRAY:
		case BLACK:
		case YELLOW:
		case CYAN:
		case MAGENTA:
		case GREEN:
		case RED:
		case BLUE:
			return new ExParValue(PxlColor.systemColor(opCode - WHITE));
		case IDIV:
			return a.idiv(b);
		case SIN:
			return new ExParValue(Math.sin(a.getDouble()));
		case COS:
			return new ExParValue(Math.cos(a.getDouble()));
		case TAN:
			return new ExParValue(Math.tan(a.getDouble()));
		case ATAN:
			return new ExParValue((b == null) ? Math.atan(a.getDouble())
					: Math.atan2(a.getDouble(), b.getDouble()));
		case SQRT:
			return new ExParValue(Math.sqrt(a.getDouble()));
		case LOG:
			return new ExParValue(Math.log(a.getDouble()));
		case LOG10:
			return new ExParValue(Math.log10(a.getDouble()));
		case EXP:
			return new ExParValue(Math.exp(a.getDouble()));
		case POW:
			return new ExParValue(Math.pow(a.getDouble(), b.getDouble()));
		case CEIL:
			return new ExParValue(Math.ceil(a.getDouble()));
		case FLOOR:
			return new ExParValue(Math.floor(a.getDouble()));
		case ROUND:
			return new ExParValue((int) Math.round(a.getDouble()));
		case FROM_DEV_RGB:
			return new ExParValue(PxlColor.instance(PxlColor.CS_Dev,
					a.getDoubleArray()));
		case TO_DEV_RGB:
			java.awt.Color z = a.getPxlColor().dev();
			int[] y = { z.getRed(), z.getGreen(), z.getBlue() };
			return new ExParValue(y);
		case TO_HEX_DEV_RGB:
			java.awt.Color u = a.getPxlColor().dev();
			String s = "#" + hex(u.getRed()) + hex(u.getGreen())
					+ hex(u.getBlue());
			return new ExParValue(s);
		case RUNTIME_ID:
			return new ExParValue(Base.getRuntimeID());
		case NEXT_NUMBER:
			return new ExParValue(Base.getNextNumber());
		case FROM_YXY:
			return new ExParValue(PxlColor.instance(PxlColor.CS_Yxy,
					a.getDoubleArray()));
		case EXPRESSION_FILE:
			return ExParValue.file(a.getString(), b.getString());
		case UNDEFINED_VALUE:
			return new ExParValueUndefined();
		case FROM_XYZ:
			return new ExParValue(PxlColor.instance(PxlColor.CS_XYZ,
					a.getDoubleArray()));
		case TO_XYZ:
			return new ExParValue(a.getPxlColor().transform(PxlColor.CS_XYZ));
		default:
			return null;
		}
	}

	private static String hex(int i) {
		String s = Integer.toHexString(i).toUpperCase();
		if (i < 16) {
			s = "0" + s;
		}
		return s;
	}

	public String toString(ExParValue a, ExParValue b, ExParValue c) {
		switch (opCode) {
		case ADD_OP:
			return a.toString() + "+" + b.toString();
		case SUB_OP:
			return a.toString() + "-" + b.toString();
		case MUL_OP:
			return a.toString() + "*" + b.toString();
		case DIV_OP:
			return a.toString() + "/" + b.toString();
		case MOD_OP:
			return a.toString() + "%" + b.toString();
		case POS_OP:
			return "+" + a.toString();
		case NEG_OP:
			return "-" + a.toString();
		case GT_OP:
			return a.toString() + ">" + b.toString();
		case GE_OP:
			return a.toString() + ">=" + b.toString();
		case LT_OP:
			return a.toString() + "<" + b.toString();
		case LE_OP:
			return a.toString() + "<=" + b.toString();
		case EQ_OP:
			return a.toString() + "==" + b.toString();
		case NE_OP:
			return a.toString() + "!=" + b.toString();
		case AND_OP:
			return a.toString() + "&&" + b.toString();
		case OR_OP:
			return a.toString() + "||" + b.toString();
		case NOT_OP:
			return "!" + a.toString();
		case SHIFT_RIGHT_OP:
			return a.toString() + ">>" + b.toString();
		case SHIFT_LEFT_OP:
			return a.toString() + "<<" + b.toString();
		case SHIFT_RIGHT_UNSIGNED_OP:
			return a.toString() + ">>>" + b.toString();
		case BIN_AND_OP:
			return a.toString() + "&" + b.toString();
		case BIN_OR_OP:
			return a.toString() + "|" + b.toString();
		case BIN_XOR_OP:
			return a.toString() + "^" + b.toString();
		case BIN_COMPLEMENT_OP:
			return "~" + a.toString();
		case CAT_OP:
			return "\"" + a.toString() + "\"+\"" + b.toString() + "\"";
		case CONDITIONAL_OP:
			return a.toString() + "? " + b.toString() + ": " + c.toString();
		case BRACKET_OP:
			return "(" + a.toString() + ")";
		case SCREEN_TOP:
		case SCREEN_RIGHT:
		case SCREEN_BOTTOM:
		case SCREEN_LEFT:
		case SCREEN_WIDTH:
		case SCREEN_HEIGHT:
		case WHITE:
		case LIGHT_GRAY:
		case GRAY:
		case DARK_GRAY:
		case BLACK:
		case YELLOW:
		case CYAN:
		case MAGENTA:
		case GREEN:
		case RED:
		case BLUE:
		case RUNTIME_ID:
		case NEXT_NUMBER:
		case UNDEFINED_VALUE:
			return function[opCode - FIRST_FUNCTION] + "()";
		case FROM_CIELAB:
		case TO_CIELAB:
		case FROM_CIELABLCH:
		case TO_CIELABLCH:
		case RANDOM_INT:
		case SIN:
		case COS:
		case TAN:
		case SQRT:
		case LOG:
		case LOG10:
		case EXP:
		case ABS:
		case CEIL:
		case FLOOR:
		case ROUND:
		case FROM_DEV_RGB:
		case TO_DEV_RGB:
		case TO_HEX_DEV_RGB:
		case FROM_YXY:
		case FROM_XYZ:
		case TO_XYZ:
			return function[opCode - FIRST_FUNCTION] + "(" + a.toString() + ")";
		case ADD_COLORS:
		case MIX_COLORS:
		case VALUE_OF:
		case LANG2:
		case CONCAT:
		case ARRAY_OF_2:
		case COLOR_AT_LUM:
		case IDIV:
		case ATAN:
		case POW:
		case EXPRESSION_FILE:
			return function[opCode - FIRST_FUNCTION] + "(" + a.toString()
					+ ", " + b.toString() + ")";
		case LANG3:
		case ARRAY_OF_3:
			return function[opCode - FIRST_FUNCTION] + "(" + a.toString()
					+ ", " + b.toString() + ", " + c.toString() + ")";
		default:
			return "";
		}
	}

	public int getOpCode() {
		return opCode;
	}

	public String toString() {
		return ("(op)");
	}
}
