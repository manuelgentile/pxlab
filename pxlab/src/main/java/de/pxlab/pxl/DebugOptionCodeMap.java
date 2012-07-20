package de.pxlab.pxl;

import de.pxlab.util.OptionCodeMap;

/**
 * Debug option names, codes, and descriptions.
 * 
 * @version 0.4.2
 */
/*
 * 
 * 2006/11/12
 * 
 * 2007/03/16 Debug.BASE
 * 
 * 2007/09/23 Debug.COLDEV
 */
public class DebugOptionCodeMap extends OptionCodeMap {
	public DebugOptionCodeMap() {
		super("Debug Options", false);
		put("files", Debug.FILES, "File open/close protocol");
		put("parser", Debug.PARSER, "Parser protocol");
		put("node", Debug.CREATE_NODE, "Design node creation/modification");
		put("expression", Debug.EXPR, "Expression evaluation");
		put("assignment", Debug.ASSIGNMENT, "Assignment execution");
		put("list", Debug.DISPLAY_LIST, "Display list management");
		put("push", Debug.PUSH_POP,
				"Push/pop operations for design node arguments");
		put("argval", Debug.ARGVALUES,
				"Show display list argument values after execution");
		put("states", Debug.STATE_CTRL,
				"Procedure/Session/Block/Trial state control");
		put("adaptive", Debug.ADAPTIVE, "Adaptive procedure parameter protocol");
		put("display", Debug.DSP_PROPS,
				"Show complex display object properties");
		put("multisession", Debug.MULTISESSION,
				"Check active session list for multiple sessions");
		put("focus", Debug.FOCUS, "Focus changes");
		put("errors", Debug.ERR_STACK_TRACE, "Stack trace on errors");
		put("events", Debug.EVENTS, "Event detection and handling");
		put("timing", Debug.TIMING,
				"Check display list timing using millisecond resolution");
		put("hrtiming", Debug.HR_TIMING,
				"Check display list timing using nanosecond resolution");
		put("media", Debug.MEDIA_TIMING, "Media files access and timing");
		put("voicekey", Debug.VOICE_KEY, "Voice key levels and states");
		put("textposition", Debug.TEXTPOS,
				"Show text frames and position reference");
		put("colorgamut", Debug.COLOR_GAMUT,
				"Show violations of device color gamut");
		put("coldev", Debug.COLOR_DEVICE, "Log color device transform changes");
		put("cache", Debug.CACHE, "File cache usage");
		put("factors", Debug.EDIT, "Factor levels and condition table");
		put("edit", Debug.EDIT, "Show design tree editing operations");
		put("data", Debug.DATA, "Check data analysis computations");
		put("base", Debug.BASE, "Show static Base parameter settings");
		put("factory", Debug.FACTORY, "Design tree factory operations");
		put("symbol", Debug.SYMBOL_TABLE, "Watch symbol table generation");
		put("none", Debug.NO_DEBUGGING, "Don't add a debug option");
	}
}
