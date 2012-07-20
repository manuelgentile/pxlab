package de.pxlab.util;

/**
 * This class parses Unix style command line options. A command line option
 * starts with an option character, which is the minus sign ("-") or the slash
 * ("/"). The option character must immediately being followed by an option
 * character. Options may have optional arguments which follow the option
 * character with or without a separating space character. Multiple option
 * characters may be concatenated but in this case only the last option
 * character can have an argument. There may be further strings following the
 * sequence of options. These are treated as non-options and my be sent back via
 * special requests.
 * 
 * <p>
 * Option characters are defined in the same way as the Unix function getopt()
 * does: The constructor is given a String which contains all option characters.
 * If a character is followed by a double period character the it requires an
 * argument.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 03/30/00
 */
public class CommandLineParser implements CommandLineOptionHandler {
	private char optionChar1 = '-';
	private char optionChar2 = '/';
	private char optArgChar = ':';
	protected String[] args;
	protected int argsIndex = 0;
	protected boolean moreArgs = false;

	/**
	 * Create a command line parser for the given array of command line strings
	 * and the given option string. The command line option handler is called
	 * once for every command line option found.
	 */
	public CommandLineParser(String[] ag, String opt,
			CommandLineOptionHandler handler) {
		args = ag;
		char[] option = opt.toCharArray();
		if (handler == null)
			handler = this;
		String arg;
		boolean noOptChar;
		// for (int i = 0; i < args.length; i++)
		// System.out.println("args["+i+"] = "+args[i]);
		// for (int i = 0; i < option.length; i++)
		// System.out.println("option["+i+"] = "+option[i]);
		while (argsIndex < args.length) {
			arg = args[argsIndex];
			if ((arg.charAt(0) == optionChar1)
					|| (arg.charAt(0) == optionChar2)) {
				// Option leader found
				char c = arg.charAt(1);
				// System.out.println("Option char: " + c);
				noOptChar = true;
				for (int i = 0; i < option.length; i++) {
					if (c == option[i]) {
						// This is a valid option character
						noOptChar = false;
						// System.out.println("Option char is valid");
						if ((option.length > i + 1)
								&& (option[i + 1] == optArgChar)) {
							// Option requires an argument
							if (arg.length() > 2) {
								// argument follows immediately
								handler.commandLineOption(c, arg.substring(2));
							} else {
								if (args.length > (argsIndex + 1)) {
									// The argument exists
									// System.out.println("Option argument: " +
									// args[argsIndex+1]);
									handler.commandLineOption(c,
											args[++argsIndex]);
								} else {
									// The argument is missing
									handler.commandLineError(
											CommandLineOptionHandler.missingOptionArg,
											"Missing argument for option -" + c);
								}
							}
							argsIndex++;
						} else {
							// Option does not require an argument
							handler.commandLineOption(c, null);
							if (arg.length() > 2) {
								// Concatenated options
								args[argsIndex] = String.valueOf(optionChar1)
										+ arg.substring(2);
							} else {
								argsIndex++;
							}
						}
					}
				}
				if (noOptChar) {
					handler.commandLineError(
							CommandLineOptionHandler.unknownOptionChar,
							"Unknown option -" + c);
					// Make sure that we increment to the next 'option'
					// character
					if (arg.length() > 2) {
						// Concatenated options
						args[argsIndex] = String.valueOf(optionChar1)
								+ arg.substring(2);
					} else {
						argsIndex++;
					}
				}
			} else {
				// String is not an option
				break;
			}
		}
	}

	/**
	 * Returns true if the command line contains more arguments which have not
	 * been identified as options and have not yet been retrieved by the
	 * getArg() method.
	 */
	public boolean hasMoreArgs() {
		return (argsIndex < args.length);
	}

	/**
	 * Retrieve the next command line argument. This method may be called after
	 * the command line parser has been instantiated and the method
	 * hasMoreArgs() has returned true.
	 */
	public String getArg() {
		return (args[argsIndex++]);
	}

	public void commandLineError(int e, String s) {
		System.out.println("Command line error: " + s);
	}

	public void commandLineOption(char c, String arg) {
		System.out.println("  Option -" + c + " " + arg);
	}
}
