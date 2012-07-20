//----------------------------------------------------
// This file has been created automatically by PXLab. 
//----------------------------------------------------
package de.pxlab.exp;

import de.pxlab.pxl.ExParValue;
import de.pxlab.pxl.ExParValueVar;
import de.pxlab.pxl.ExParValueConstant;
import de.pxlab.pxl.ExParValueFunction;
import de.pxlab.pxl.ExParValueUndefined;
import de.pxlab.pxl.ExDesign;
import de.pxlab.pxl.ExDesignTreeFactory;
import de.pxlab.pxl.run.ExRun;

public class ExDesignNodeDemo extends ExDesign {
	private String[] parNames;
	private ExParValue exParValue;
	private ExParValue[] parValues;
	private String[] exParValuesValue;

	public ExDesignNodeDemo() {
		ExDesignTreeFactory exDesignTreeFactory = new ExDesignTreeFactory();
		parNames = new String[1];
		parNames[0] = "SubjectGroup";
		exDesignTree = exDesignTreeFactory.createExperimentNode(parNames);
		parNames = null;
		exDesignTreeFactory.addContextNode(parNames);
		parNames = null;
		exDesignTreeFactory.addAssignmentGroupNode(parNames);
		ExParValue _exParValue_1 = new ExParValue("pxlab");
		exDesignTreeFactory.addGlobalAssignmentNode(11, "SubjectCode",
				"SubjectCode", _exParValue_1);
		_exParValue_1 = new ExParValue("2");
		exDesignTreeFactory.addGlobalAssignmentNode(11, "TrialFactor",
				"TrialFactor", _exParValue_1);
		_exParValue_1 = new ExParValue("non");
		exDesignTreeFactory.addGlobalAssignmentNode(12, "Word", "Word",
				_exParValue_1);
		ExParValue _exParValue_2 = new ExParValueVar(
				"Procedure.Message.Execute");
		ExParValue _exParValue_3 = new ExParValue("executing");
		ExParValue _exParValue_4 = new ExParValue("not executing");
		_exParValue_1 = new ExParValueFunction(25, _exParValue_2,
				_exParValue_3, _exParValue_4);
		exDesignTreeFactory.addGlobalAssignmentNode(12, "Expression",
				"Expression", _exParValue_1);
		_exParValue_1 = new ExParValue("0");
		exDesignTreeFactory.addGlobalAssignmentNode(11,
				"SkipBoundingBlockDisplays", "SkipBoundingBlockDisplays",
				_exParValue_1);
		_exParValue_1 = new ExParValue("1");
		exDesignTreeFactory
				.addGlobalAssignmentNode(11, "DataProcessingEnabled",
						"DataProcessingEnabled", _exParValue_1);
		_exParValue_1 = new ExParValue("ExDesignNodeDemo");
		exDesignTreeFactory.addGlobalAssignmentNode(11, "JavaClassName",
				"JavaClassName", _exParValue_1);
		parNames = null;
		exDesignTreeFactory.addDisplayListNode(2, "Procedure", parNames);
		exDesignTreeFactory.addDisplayNode("Message");
		_exParValue_1 = new ExParValue("Procedure Start");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Text", "Text",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.CLOCK_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValue("500");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Duration", "Duration",
				_exParValue_1);
		parNames = null;
		exDesignTreeFactory.addDisplayListNode(6, "ProcedureEnd", parNames);
		exDesignTreeFactory.addDisplayNode("Message");
		_exParValue_1 = new ExParValue("End of Procedure");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Text", "Text",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.CLOCK_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValue("500");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Duration", "Duration",
				_exParValue_1);
		parNames = null;
		exDesignTreeFactory.addDisplayListNode(3, "Session", parNames);
		exDesignTreeFactory.addDisplayNode("Message");
		_exParValue_1 = new ExParValue("Session Start");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Text", "Text",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.CLOCK_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValue("500");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Duration", "Duration",
				_exParValue_1);
		parNames = null;
		exDesignTreeFactory.addDisplayListNode(7, "SessionEnd", parNames);
		exDesignTreeFactory.addDisplayNode("Message");
		_exParValue_1 = new ExParValue("End of Session");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Text", "Text",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.CLOCK_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValue("500");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Duration", "Duration",
				_exParValue_1);
		parNames = new String[1];
		parNames[0] = "BlockCounter";
		exDesignTreeFactory.addDisplayListNode(4, "Block", parNames);
		exDesignTreeFactory.addDisplayNode("Message");
		_exParValue_1 = new ExParValue("Block Start");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Text", "Text",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.CLOCK_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValue("500");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Duration", "Duration",
				_exParValue_1);
		parNames = null;
		exDesignTreeFactory.addDisplayListNode(8, "BlockEnd", parNames);
		exDesignTreeFactory.addDisplayNode("Message");
		_exParValue_1 = new ExParValue("End of Block");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Text", "Text",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.CLOCK_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValue("500");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Duration", "Duration",
				_exParValue_1);
		parNames = new String[5];
		parNames[0] = "TrialCounter";
		parNames[1] = "Direction";
		parNames[2] = "Color";
		parNames[3] = "Trial.Feedback.Response";
		parNames[4] = "Trial.Arrow.ResponseTime";
		exDesignTreeFactory.addDisplayListNode(5, "Trial", parNames);
		exDesignTreeFactory.addDisplayNode("ClearScreen:isi");
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.CLOCK_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValue("1000");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Duration", "Duration",
				_exParValue_1);
		exDesignTreeFactory.addDisplayNode("FixationMark");
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.CLOCK_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValue("300");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Duration", "Duration",
				_exParValue_1);
		exDesignTreeFactory.addDisplayNode("ClearScreen:wait");
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.CLOCK_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValue("300");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Duration", "Duration",
				_exParValue_1);
		exDesignTreeFactory.addDisplayNode("Arrow");
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.RESPONSE_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		String[] _valueArray_1 = { "1", "3" };
		_exParValue_1 = new ExParValue(_valueArray_1);
		exDesignTreeFactory.addLocalAssignmentNode(11, "ResponseSet",
				"ResponseSet", _exParValue_1);
		_exParValue_1 = new ExParValue("180");
		exDesignTreeFactory.addLocalAssignmentNode(11, "RightHeadWidth",
				"RightHeadWidth", _exParValue_1);
		_exParValue_1 = new ExParValue("180");
		exDesignTreeFactory.addLocalAssignmentNode(11, "RightHeadLength",
				"RightHeadLength", _exParValue_1);
		_exParValue_1 = new ExParValue("0");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LeftHeadWidth",
				"LeftHeadWidth", _exParValue_1);
		_exParValue_1 = new ExParValue("0");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LeftHeadLength",
				"LeftHeadLength", _exParValue_1);
		_exParValue_1 = new ExParValue("120");
		exDesignTreeFactory.addLocalAssignmentNode(11, "ShaftWidth",
				"ShaftWidth", _exParValue_1);
		_exParValue_1 = new ExParValue("200");
		exDesignTreeFactory.addLocalAssignmentNode(11, "ShaftLength",
				"ShaftLength", _exParValue_1);
		_exParValue_1 = new ExParValue("0");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Orientation",
				"Orientation", _exParValue_1);
		_exParValue_1 = new ExParValue("0");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LocationX",
				"LocationX", _exParValue_1);
		_exParValue_1 = new ExParValue("0");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LocationY",
				"LocationY", _exParValue_1);
		String[] _valueArray_2 = { "21.26", "0.621", "0.34" };
		_exParValue_1 = new ExParValue(_valueArray_2);
		exDesignTreeFactory.addLocalAssignmentNode(11, "Color", "Color",
				_exParValue_1);
		exDesignTreeFactory.addDisplayNode("ClearScreen:clear");
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.NO_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		exDesignTreeFactory.addDisplayNode("Feedback");
		_exParValue_1 = new ExParValue("Trial.Arrow.ResponseCode");
		exDesignTreeFactory.addLocalAssignmentNode(11, "ResponseParameter",
				"ResponseParameter", _exParValue_1);
		_exParValue_1 = new ExParValue("%Trial.Arrow.ResponseTime@i% ms");
		exDesignTreeFactory.addLocalAssignmentNode(11, "CorrectText",
				"CorrectText", _exParValue_1);
		_exParValue_1 = new ExParValue("");
		exDesignTreeFactory.addLocalAssignmentNode(11, "FalseText",
				"FalseText", _exParValue_1);
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.EvaluationCodes.COMPARE_CODE");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Evaluation",
				"Evaluation", _exParValue_1);
		_exParValue_1 = new ExParValue("%Trial.Arrow.ResponseTime% ms");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Text", "Text",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.NO_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		exDesignTreeFactory.addDisplayNode("Smiley");
		_exParValue_2 = new ExParValueVar("Trial.Feedback.Response");
		_exParValue_3 = new ExParValueConstant(
				"de.pxlab.pxl.ResponseCodes.CORRECT");
		_exParValue_1 = new ExParValueFunction(13, _exParValue_2, _exParValue_3);
		exDesignTreeFactory.addLocalAssignmentNode(11, "Execute", "Execute",
				_exParValue_1);
		_exParValue_1 = new ExParValueVar("Trial.Feedback.Response");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Mood", "Mood",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.OverlayCodes.TRANSPARENT");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Overlay", "Overlay",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.NO_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		exDesignTreeFactory.addDisplayNode("Nothing");
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.CLOCK_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValue("1000");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Duration", "Duration",
				_exParValue_1);
		parNames = new String[6];
		parNames[0] = "SubjectCode";
		parNames[1] = "TrialCounter";
		parNames[2] = "Direction";
		parNames[3] = "Color";
		parNames[4] = "Trial.Smiley.Execute";
		parNames[5] = "Trial.Arrow.ResponseTime";
		exDesignTreeFactory.addDataDisplayListNode(27, "ExperimentData",
				parNames);
		parNames = new String[4];
		parNames[0] = "TrialCounter";
		parNames[1] = "Direction";
		parNames[2] = "Color";
		parNames[3] = "Trial.Arrow.ResponseTime";
		exDesignTreeFactory.addDataDisplayNode("Export", parNames);
		_exParValue_2 = new ExParValueVar("Trial.Smiley.Execute");
		_exParValue_1 = new ExParValueFunction(16, _exParValue_2);
		exDesignTreeFactory.addLocalAssignmentNode(11, "Include", "Include",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.ExportCodes.FACTORIAL_DATA");
		exDesignTreeFactory.addLocalAssignmentNode(11, "DataType", "DataType",
				_exParValue_1);
		_exParValue_1 = new ExParValue("%SubjectCode%_experiment.dat");
		exDesignTreeFactory.addLocalAssignmentNode(11, "FileName", "FileName",
				_exParValue_1);
		parNames = new String[6];
		parNames[0] = "SubjectCode";
		parNames[1] = "TrialCounter";
		parNames[2] = "Direction";
		parNames[3] = "Color";
		parNames[4] = "Trial.Smiley.Execute";
		parNames[5] = "Trial.Arrow.ResponseTime";
		exDesignTreeFactory.addDataDisplayListNode(28, "ProcedureData",
				parNames);
		parNames = new String[4];
		parNames[0] = "TrialCounter";
		parNames[1] = "Direction";
		parNames[2] = "Color";
		parNames[3] = "Trial.Arrow.ResponseTime";
		exDesignTreeFactory.addDataDisplayNode("Export", parNames);
		_exParValue_2 = new ExParValueVar("Trial.Smiley.Execute");
		_exParValue_1 = new ExParValueFunction(16, _exParValue_2);
		exDesignTreeFactory.addLocalAssignmentNode(11, "Include", "Include",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.ExportCodes.FACTORIAL_DATA");
		exDesignTreeFactory.addLocalAssignmentNode(11, "DataType", "DataType",
				_exParValue_1);
		_exParValue_1 = new ExParValue("%SubjectCode%_procedure.dat");
		exDesignTreeFactory.addLocalAssignmentNode(11, "FileName", "FileName",
				_exParValue_1);
		parNames = new String[6];
		parNames[0] = "SubjectCode";
		parNames[1] = "TrialCounter";
		parNames[2] = "Direction";
		parNames[3] = "Color";
		parNames[4] = "Trial.Smiley.Execute";
		parNames[5] = "Trial.Arrow.ResponseTime";
		exDesignTreeFactory.addDataDisplayListNode(29, "SessionData", parNames);
		parNames = new String[4];
		parNames[0] = "TrialCounter";
		parNames[1] = "Direction";
		parNames[2] = "Color";
		parNames[3] = "Trial.Arrow.ResponseTime";
		exDesignTreeFactory.addDataDisplayNode("Anova", parNames);
		_exParValue_2 = new ExParValueVar("Trial.Smiley.Execute");
		_exParValue_1 = new ExParValueFunction(16, _exParValue_2);
		exDesignTreeFactory.addLocalAssignmentNode(11, "Include", "Include",
				_exParValue_1);
		_exParValue_1 = new ExParValue("%SubjectCode%_session.html");
		exDesignTreeFactory.addLocalAssignmentNode(11, "FileName", "FileName",
				_exParValue_1);
		parNames = new String[6];
		parNames[0] = "SubjectCode";
		parNames[1] = "BlockCounter";
		parNames[2] = "Direction";
		parNames[3] = "Color";
		parNames[4] = "Trial.Feedback.Response";
		parNames[5] = "Trial.Arrow.ResponseTime";
		exDesignTreeFactory.addDataDisplayListNode(30, "BlockData", parNames);
		parNames = null;
		exDesignTreeFactory.addDataDisplayNode("Statistics", parNames);
		_exParValue_1 = new ExParValue(
				"%SubjectCode% %BlockCounter% %BlockData.Statistics.Mean% %BlockData.Statistics.StandardDeviation%\n");
		exDesignTreeFactory.addLocalAssignmentNode(11, "ResultFormat",
				"ResultFormat", _exParValue_1);
		_exParValue_1 = new ExParValue("%SubjectCode%_block.dat");
		exDesignTreeFactory.addLocalAssignmentNode(11, "FileName", "FileName",
				_exParValue_1);
		parNames = new String[6];
		parNames[0] = "SubjectCode";
		parNames[1] = "TrialCounter";
		parNames[2] = "Direction";
		parNames[3] = "Color";
		parNames[4] = "Trial.Feedback.Response";
		parNames[5] = "Trial.Arrow.ResponseTime";
		exDesignTreeFactory.addDataDisplayListNode(31, "TrialData", parNames);
		parNames = null;
		exDesignTreeFactory.addDataDisplayNode("Export", parNames);
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.ExportCodes.RAW_DATA");
		exDesignTreeFactory.addLocalAssignmentNode(11, "DataType", "DataType",
				_exParValue_1);
		_exParValue_1 = new ExParValue("%SubjectCode%_trial.dat");
		exDesignTreeFactory.addLocalAssignmentNode(11, "FileName", "FileName",
				_exParValue_1);
		parNames = null;
		exDesignTreeFactory.addFactorsNode(parNames);
		parNames = new String[1];
		parNames[0] = "TrialCounter";
		exDesignTreeFactory.addFactorNode(20, parNames);
		parNames = new String[1];
		parNames[0] = "Age";
		exDesignTreeFactory.addFactorNode(23, parNames);
		parValues = new ExParValue[1];
		_exParValue_1 = new ExParValue("less than 30");
		parValues[0] = _exParValue_1;
		exDesignTreeFactory.addFactorLevelNode(parValues);
		parValues = new ExParValue[1];
		_exParValue_1 = new ExParValue("31 - 50");
		parValues[0] = _exParValue_1;
		exDesignTreeFactory.addFactorLevelNode(parValues);
		parValues = new ExParValue[1];
		_exParValue_1 = new ExParValue("older than 50");
		parValues[0] = _exParValue_1;
		exDesignTreeFactory.addFactorLevelNode(parValues);
		parNames = new String[1];
		parNames[0] = "SubjectGroup";
		exDesignTreeFactory.addFactorNode(23, parNames);
		parValues = new ExParValue[1];
		_exParValue_1 = new ExParValue("A");
		parValues[0] = _exParValue_1;
		exDesignTreeFactory.addFactorLevelNode(parValues);
		parValues = new ExParValue[1];
		_exParValue_1 = new ExParValue("B");
		parValues[0] = _exParValue_1;
		exDesignTreeFactory.addFactorLevelNode(parValues);
		parNames = new String[3];
		parNames[0] = "Direction";
		parNames[1] = "Trial.Arrow.Orientation";
		parNames[2] = "Trial.Feedback.CorrectCode";
		exDesignTreeFactory.addFactorNode(21, parNames);
		parValues = new ExParValue[3];
		_exParValue_1 = new ExParValue("left");
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValue("180");
		parValues[1] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[2] = _exParValue_1;
		exDesignTreeFactory.addFactorLevelNode(parValues);
		parValues = new ExParValue[3];
		_exParValue_1 = new ExParValue("right");
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[1] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[2] = _exParValue_1;
		exDesignTreeFactory.addFactorLevelNode(parValues);
		parNames = new String[2];
		parNames[0] = "Color";
		parNames[1] = "Trial.Arrow.Color";
		exDesignTreeFactory.addFactorNode(21, parNames);
		parValues = new ExParValue[2];
		_exParValue_1 = new ExParValue("red");
		parValues[0] = _exParValue_1;
		String[] _valueArray_3 = { "21.26", "0.621", "0.34" };
		_exParValue_1 = new ExParValue(_valueArray_3);
		parValues[1] = _exParValue_1;
		exDesignTreeFactory.addFactorLevelNode(parValues);
		parValues = new ExParValue[2];
		_exParValue_1 = new ExParValue("blue");
		parValues[0] = _exParValue_1;
		String[] _valueArray_4 = { "21.26", "0.1", "0.2" };
		_exParValue_1 = new ExParValue(_valueArray_4);
		parValues[1] = _exParValue_1;
		exDesignTreeFactory.addFactorLevelNode(parValues);
		parValues = new ExParValue[2];
		_exParValue_1 = new ExParValue("green");
		parValues[0] = _exParValue_1;
		String[] _valueArray_5 = { "21.26", "0.3", "0.55" };
		_exParValue_1 = new ExParValue(_valueArray_5);
		parValues[1] = _exParValue_1;
		exDesignTreeFactory.addFactorLevelNode(parValues);
		parNames = new String[1];
		parNames[0] = "Trial.Arrow.ResponseTime";
		exDesignTreeFactory.addFactorNode(22, parNames);
		parNames = new String[1];
		parNames[0] = "Trial.Feedback.Response";
		exDesignTreeFactory.addFactorNode(22, parNames);
		parValues = null;
		exDesignTreeFactory.addProcedureNode("Procedure", parValues);
		parValues = null;
		exDesignTreeFactory.addSessionNode("Session", parValues);
		parValues = new ExParValue[1];
		_exParValue_1 = new ExParValueUndefined();
		parValues[0] = _exParValue_1;
		exDesignTreeFactory.addBlockNode("Block", parValues);
		parValues = new ExParValue[5];
		_exParValue_1 = new ExParValueUndefined();
		parValues[0] = _exParValue_1;
		String[] _valueArray_6 = { "left", "right" };
		_exParValue_1 = new ExParValue(_valueArray_6);
		_exParValue_1.setExpansion();
		parValues[1] = _exParValue_1;
		String[] _valueArray_7 = { "red", "green" };
		_exParValue_1 = new ExParValue(_valueArray_7);
		_exParValue_1.setExpansion();
		parValues[2] = _exParValue_1;
		_exParValue_1 = new ExParValueUndefined();
		parValues[3] = _exParValue_1;
		_exParValue_1 = new ExParValueUndefined();
		parValues[4] = _exParValue_1;
		exDesignTreeFactory.addTrialNode("Trial", parValues);
		parValues = new ExParValue[1];
		_exParValue_1 = new ExParValueUndefined();
		parValues[0] = _exParValue_1;
		exDesignTreeFactory.addBlockNode("Block", parValues);
		parValues = new ExParValue[5];
		_exParValue_1 = new ExParValueUndefined();
		parValues[0] = _exParValue_1;
		String[] _valueArray_8 = { "left", "right" };
		_exParValue_1 = new ExParValue(_valueArray_8);
		_exParValue_1.setExpansion();
		parValues[1] = _exParValue_1;
		String[] _valueArray_9 = { "red", "green" };
		_exParValue_1 = new ExParValue(_valueArray_9);
		_exParValue_1.setExpansion();
		parValues[2] = _exParValue_1;
		_exParValue_1 = new ExParValueUndefined();
		parValues[3] = _exParValue_1;
		_exParValue_1 = new ExParValueUndefined();
		parValues[4] = _exParValue_1;
		exDesignTreeFactory.addTrialNode("Trial", parValues);
	}

	public static void main(String[] args) {
		// de.pxlab.pxl.Debug.add(de.pxlab.pxl.Debug.FACTORY);
		// de.pxlab.pxl.Debug.add(de.pxlab.pxl.Debug.CREATE_NODE);
		ExDesign x = new ExDesignNodeDemo();
		// x.print();
		new ExRun(args, x);
	}
}
