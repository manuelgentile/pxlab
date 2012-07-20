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

public class PXLabExperiment extends ExDesign {
	private String[] parNames;
	private ExParValue exParValue;
	private ExParValue[] parValues;
	private String[] exParValuesValue;

	public PXLabExperiment() {
		ExDesignTreeFactory exDesignTreeFactory = new ExDesignTreeFactory();
		parNames = null;
		exDesignTree = exDesignTreeFactory.createExperimentNode(parNames);
		parNames = null;
		exDesignTreeFactory.addContextNode(parNames);
		parNames = null;
		exDesignTreeFactory.addAssignmentGroupNode(parNames);
		ExParValue _exParValue_1 = new ExParValue("pxlab");
		exDesignTreeFactory.addGlobalAssignmentNode(11, "SubjectCode",
				"SubjectCode", _exParValue_1);
		_exParValue_1 = new ExParValue("1");
		exDesignTreeFactory.addGlobalAssignmentNode(11, "TrialFactor",
				"TrialFactor", _exParValue_1);
		_exParValue_1 = new ExParValueFunction(48);
		exDesignTreeFactory
				.addGlobalAssignmentNode(11, "ScreenBackgroundColor",
						"ScreenBackgroundColor", _exParValue_1);
		_exParValue_1 = new ExParValue("140");
		exDesignTreeFactory.addGlobalAssignmentNode(12, "SAMWidth", "SAMWidth",
				_exParValue_1);
		ExParValue _exParValue_2 = new ExParValue("410");
		_exParValue_1 = new ExParValueFunction(7, _exParValue_2);
		exDesignTreeFactory.addGlobalAssignmentNode(12, "SAMLeftBorder",
				"SAMLeftBorder", _exParValue_1);
		_exParValue_2 = new ExParValueVar("SAMLeftBorder");
		ExParValue _exParValue_4 = new ExParValueVar("SAMWidth");
		ExParValue _exParValue_5 = new ExParValue("2");
		ExParValue _exParValue_3 = new ExParValueFunction(4, _exParValue_4,
				_exParValue_5);
		_exParValue_1 = new ExParValueFunction(1, _exParValue_2, _exParValue_3);
		exDesignTreeFactory.addGlobalAssignmentNode(12, "ScaleLeft",
				"ScaleLeft", _exParValue_1);
		_exParValue_1 = new ExParValueConstant("AROUSAL");
		exDesignTreeFactory.addGlobalAssignmentNode(12, "SAMDimension",
				"SAMDimension", _exParValue_1);
		_exParValue_1 = new ExParValue("0");
		exDesignTreeFactory.addGlobalAssignmentNode(12, "SAMLocationY",
				"SAMLocationY", _exParValue_1);
		_exParValue_1 = new ExParValue("0");
		exDesignTreeFactory.addGlobalAssignmentNode(12, "introExecSAM",
				"introExecSAM", _exParValue_1);
		_exParValue_1 = new ExParValue("1");
		exDesignTreeFactory.addGlobalAssignmentNode(12, "introExecCenterSAM",
				"introExecCenterSAM", _exParValue_1);
		_exParValue_1 = new ExParValue("0");
		exDesignTreeFactory.addGlobalAssignmentNode(12, "introExecScale",
				"introExecScale", _exParValue_1);
		_exParValue_1 = new ExParValue("200");
		exDesignTreeFactory.addGlobalAssignmentNode(12, "introSAMLocationY",
				"introSAMLocationY", _exParValue_1);
		_exParValue_1 = new ExParValueConstant("NO_TIMER");
		exDesignTreeFactory.addGlobalAssignmentNode(12, "introScaleTimer",
				"introScaleTimer", _exParValue_1);
		_exParValue_1 = new ExParValue(
				"In diesem Experiment werden Sie in jedem Durchgang die �hnlichkeit zwischen 3 Strichzeichnungen beurteilen. Die Strichzeichnungen stellen jeweils ein Kontinuum von Stimmungen dar. Dabei stehen die beiden �u�eren Zeichnungen f�r die Endpunkte des Abschnittes den Sie beurteilen sollen. Die mittlere Zeichnung stellt immer eine Stimmung dar, die auf dem Kontinuum zwischen den beiden Endpunkten liegt.\n \nHier sehen Sie ein Beispiel f�r das Stimmungskontinuum von \"traurig\" oder \"ungl�cklich\" zu \"fr�hlich\" oder \"gl�cklich\":");
		exDesignTreeFactory.addGlobalAssignmentNode(12, "introText01",
				"introText01", _exParValue_1);
		_exParValue_1 = new ExParValue(
				"Unter den Strichzeichnungen werden Sie eine Skala sehen. Die Endpunkte der Skala entsprechen jeweils der linken bzw. der rechten Strichzeichnung. Die Skala stellt das Stimmungskontinuum zwischen dem linken und dem rechten Bild dar. \n \n Ihre Aufgabe ist es, mit der Maus auf der Skala die Position zu markieren, die das mittlere Bild darstellt.");
		exDesignTreeFactory.addGlobalAssignmentNode(12, "introText02",
				"introText02", _exParValue_1);
		_exParValue_1 = new ExParValue(
				"Sie k�nnen das Positionieren des Skalenzeigers nun ausprobieren. Zeigen sie auf den Punkt der Skala, an dem nach Ihrer Einsch�tzung das mittlere Bild liegt und dr�cken Sie eine Maustaste. Sie k�nnen den Zeiger bei gedr�ckter Maustaste auch verschieben. \n\nWenn Sie mit der Einstellung zufrieden sind, dr�cken Sie bitte die Leertaste der Tastatur.");
		exDesignTreeFactory.addGlobalAssignmentNode(12, "introText03",
				"introText03", _exParValue_1);
		_exParValue_1 = new ExParValue(
				"Die Bezugsbilder, die Sie in einem Durchgang sehen werden, sind in der Regel nicht die Extrembilder, sondern befinden sich irgendwo innerhalb dieses Bereichs. Bitte beziehen Sie Ihre Skaleneinstellung immer auf die im jeweiligen Durchgang dargestellten Bezugsbilder. Stellen Sie den Skalenzeiger so ein, dass die Abst�nde zu den Endpunkten den Abst�nden des mittleren Bildes von den Bezugsbildern entsprechen.");
		exDesignTreeFactory.addGlobalAssignmentNode(12, "introText04",
				"introText04", _exParValue_1);
		_exParValue_1 = new ExParValue(
				"Zur Erinnerung:\n \nMit der Maus positionieren Sie den Skalenzeiger und mit der Leertaste geben Sie an, dass Sie mit Ihrer Einstellung zufrieden sind.");
		exDesignTreeFactory.addGlobalAssignmentNode(12, "introText05",
				"introText05", _exParValue_1);
		_exParValue_1 = new ExParValue(
				"Falls Sie noch Fragen haben, so stellen Sie diese bitte jetzt bevor Sie weiterschalten.");
		exDesignTreeFactory.addGlobalAssignmentNode(12, "introText06",
				"introText06", _exParValue_1);
		_exParValue_1 = new ExParValue(
				"Sie bearbeiten jetzt das Kontinuum von \"traurig\" oder \"ungl�cklich\" zu \"fr�hlich\" oder \"gl�cklich\". Dies sind die Extrembilder des Kontinuums:");
		exDesignTreeFactory.addGlobalAssignmentNode(12, "introText10",
				"introText10", _exParValue_1);
		_exParValue_1 = new ExParValue(
				"Sie bearbeiten jetzt das Kontinuum von \"ruhig\" oder \"gelassen\" zu \"unruhig\" oder \"aufgeregt\". Dies sind die Extrembilder des Kontinuums:");
		exDesignTreeFactory.addGlobalAssignmentNode(12, "introText20",
				"introText20", _exParValue_1);
		_exParValue_1 = new ExParValue(
				"Sie bearbeiten jetzt das Kontinuum von \"klein\" oder \"unterdr�ckt\" zu \"gro�\" oder \"beherrschend\". Dies sind die Extrembilder des Kontinuums:");
		exDesignTreeFactory.addGlobalAssignmentNode(12, "introText30",
				"introText30", _exParValue_1);
		_exParValue_1 = new ExParValue(
				"\n\nWenn Sie bereit sind, beginnen wir jetzt mit den Einstellungen.");
		exDesignTreeFactory.addGlobalAssignmentNode(12, "introText50",
				"introText50", _exParValue_1);
		parNames = null;
		exDesignTreeFactory.addDisplayListNode(3, "Session", parNames);
		exDesignTreeFactory.addDisplayNode("Instruction");
		_exParValue_1 = new ExParValue("Einsch�tzen von Strichzeichnungen");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Text", "Text",
				_exParValue_1);
		_exParValue_1 = new ExParValueFunction(50);
		exDesignTreeFactory.addLocalAssignmentNode(11, "Color", "Color",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant("CENTER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Alignment",
				"Alignment", _exParValue_1);
		_exParValue_1 = new ExParValueConstant("NO_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		exDesignTreeFactory.addDisplayNode("TextParagraph:Next");
		_exParValue_1 = new ExParValueConstant("JOIN");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Overlay", "Overlay",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.RELEASE_RESPONSE_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValue(
				"Zum Weiterbl�ttern eine beliebige Taste dr�cken!");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Text", "Text",
				_exParValue_1);
		_exParValue_2 = new ExParValueFunction(35);
		_exParValue_3 = new ExParValue("50");
		_exParValue_1 = new ExParValueFunction(2, _exParValue_2, _exParValue_3);
		exDesignTreeFactory.addLocalAssignmentNode(11, "LocationY",
				"LocationY", _exParValue_1);
		_exParValue_1 = new ExParValueConstant("BASE_CENTER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "ReferencePoint",
				"ReferencePoint", _exParValue_1);
		_exParValue_1 = new ExParValueFunction(50);
		exDesignTreeFactory.addLocalAssignmentNode(11, "Color", "Color",
				_exParValue_1);
		parNames = null;
		exDesignTreeFactory.addDisplayListNode(7, "SessionEnd", parNames);
		exDesignTreeFactory.addDisplayNode("Instruction");
		_exParValue_1 = new ExParValue(
				"E N D E\n\nVielen Dank f�r Ihre Mitarbeit!");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Text", "Text",
				_exParValue_1);
		_exParValue_1 = new ExParValueFunction(50);
		exDesignTreeFactory.addLocalAssignmentNode(11, "Color", "Color",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant("CENTER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Alignment",
				"Alignment", _exParValue_1);
		_exParValue_1 = new ExParValueConstant("CLOCK_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValue("2000");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Duration", "Duration",
				_exParValue_1);
		parNames = new String[2];
		parNames[0] = "StoreData";
		parNames[1] = "RandomizeTrials";
		exDesignTreeFactory.addDisplayListNode(4, "Block", parNames);
		parNames = new String[10];
		parNames[0] = "introExecSAM";
		parNames[1] = "introExecCenterSAM";
		parNames[2] = "introExecScale";
		parNames[3] = "introScaleTimer";
		parNames[4] = "SAMDimension";
		parNames[5] = "Trial:Intro.SelfAssessmentManikin:Left.Level";
		parNames[6] = "Trial:Intro.SelfAssessmentManikin:Right.Level";
		parNames[7] = "Trial:Intro.SelfAssessmentManikin.Level";
		parNames[8] = "Trial:Intro.RatingScaleQuestion.ScaleValue";
		parNames[9] = "Trial:Intro.TextParagraph.Text";
		exDesignTreeFactory.addDisplayListNode(5, "Trial:Intro", parNames);
		exDesignTreeFactory.addDisplayNode("TextParagraph");
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.NO_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValueFunction(50);
		exDesignTreeFactory.addLocalAssignmentNode(11, "Color", "Color",
				_exParValue_1);
		_exParValue_1 = new ExParValue("0");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LocationY",
				"LocationY", _exParValue_1);
		_exParValue_1 = new ExParValueConstant("BASE_CENTER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "ReferencePoint",
				"ReferencePoint", _exParValue_1);
		exDesignTreeFactory.addDisplayNode("SelfAssessmentManikin:Left");
		_exParValue_1 = new ExParValueVar("introExecSAM");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Execute", "Execute",
				_exParValue_1);
		_exParValue_1 = new ExParValueVar("SAMLeftBorder");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LocationX",
				"LocationX", _exParValue_1);
		_exParValue_1 = new ExParValueVar("introSAMLocationY");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LocationY",
				"LocationY", _exParValue_1);
		_exParValue_1 = new ExParValueConstant("BASE_LEFT");
		exDesignTreeFactory.addLocalAssignmentNode(11, "ReferencePoint",
				"ReferencePoint", _exParValue_1);
		_exParValue_1 = new ExParValueVar("SAMWidth");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Width", "Width",
				_exParValue_1);
		_exParValue_1 = new ExParValueVar("SAMDimension");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Dimension",
				"Dimension", _exParValue_1);
		_exParValue_1 = new ExParValue("1");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Portrait", "Portrait",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant("NO_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant("JOIN");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Overlay", "Overlay",
				_exParValue_1);
		exDesignTreeFactory.addDisplayNode("SelfAssessmentManikin:Right");
		_exParValue_1 = new ExParValueVar("introExecSAM");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Execute", "Execute",
				_exParValue_1);
		_exParValue_2 = new ExParValueVar("SAMLeftBorder");
		_exParValue_1 = new ExParValueFunction(7, _exParValue_2);
		exDesignTreeFactory.addLocalAssignmentNode(11, "LocationX",
				"LocationX", _exParValue_1);
		_exParValue_1 = new ExParValueVar("introSAMLocationY");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LocationY",
				"LocationY", _exParValue_1);
		_exParValue_1 = new ExParValueConstant("BASE_RIGHT");
		exDesignTreeFactory.addLocalAssignmentNode(11, "ReferencePoint",
				"ReferencePoint", _exParValue_1);
		_exParValue_1 = new ExParValueVar("SAMWidth");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Width", "Width",
				_exParValue_1);
		_exParValue_1 = new ExParValueVar("SAMDimension");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Dimension",
				"Dimension", _exParValue_1);
		_exParValue_1 = new ExParValue("1");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Portrait", "Portrait",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant("NO_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant("JOIN");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Overlay", "Overlay",
				_exParValue_1);
		exDesignTreeFactory.addDisplayNode("SelfAssessmentManikin");
		_exParValue_2 = new ExParValueVar("introExecSAM");
		_exParValue_3 = new ExParValueVar("introExecCenterSAM");
		_exParValue_1 = new ExParValueFunction(14, _exParValue_2, _exParValue_3);
		exDesignTreeFactory.addLocalAssignmentNode(11, "Execute", "Execute",
				_exParValue_1);
		_exParValue_1 = new ExParValue("0");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LocationX",
				"LocationX", _exParValue_1);
		_exParValue_1 = new ExParValueVar("introSAMLocationY");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LocationY",
				"LocationY", _exParValue_1);
		_exParValue_1 = new ExParValueConstant("BASE_CENTER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "ReferencePoint",
				"ReferencePoint", _exParValue_1);
		_exParValue_1 = new ExParValueVar("SAMWidth");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Width", "Width",
				_exParValue_1);
		_exParValue_1 = new ExParValueVar("SAMDimension");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Dimension",
				"Dimension", _exParValue_1);
		_exParValue_1 = new ExParValue("1");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Portrait", "Portrait",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant("NO_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant("JOIN");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Overlay", "Overlay",
				_exParValue_1);
		exDesignTreeFactory.addDisplayNode("RatingScaleQuestion");
		_exParValue_1 = new ExParValueVar("introExecScale");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Execute", "Execute",
				_exParValue_1);
		_exParValue_1 = new ExParValue("");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Text", "Text",
				_exParValue_1);
		_exParValue_2 = new ExParValueVar("introSAMLocationY");
		_exParValue_3 = new ExParValue("60");
		_exParValue_1 = new ExParValueFunction(1, _exParValue_2, _exParValue_3);
		exDesignTreeFactory.addLocalAssignmentNode(11, "ScaleLocationY",
				"ScaleLocationY", _exParValue_1);
		_exParValue_1 = new ExParValue("");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LeftLabel",
				"LeftLabel", _exParValue_1);
		_exParValue_1 = new ExParValue("");
		exDesignTreeFactory.addLocalAssignmentNode(11, "RightLabel",
				"RightLabel", _exParValue_1);
		_exParValue_1 = new ExParValueVar("introScaleTimer");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValue("0");
		exDesignTreeFactory.addLocalAssignmentNode(11, "ScaleValue",
				"ScaleValue", _exParValue_1);
		_exParValue_1 = new ExParValue("100");
		exDesignTreeFactory.addLocalAssignmentNode(11, "RoundingFactor",
				"RoundingFactor", _exParValue_1);
		_exParValue_1 = new ExParValue("1.0");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LowestTick",
				"LowestTick", _exParValue_1);
		_exParValue_1 = new ExParValue("8.0");
		exDesignTreeFactory.addLocalAssignmentNode(11, "TickStep", "TickStep",
				_exParValue_1);
		_exParValue_1 = new ExParValue("2");
		exDesignTreeFactory.addLocalAssignmentNode(11, "NumberOfTicks",
				"NumberOfTicks", _exParValue_1);
		_exParValue_1 = new ExParValueConstant("BARS");
		exDesignTreeFactory.addLocalAssignmentNode(11, "TickType", "TickType",
				_exParValue_1);
		_exParValue_3 = new ExParValue("2");
		_exParValue_2 = new ExParValueFunction(7, _exParValue_3);
		_exParValue_4 = new ExParValueVar("ScaleLeft");
		_exParValue_1 = new ExParValueFunction(3, _exParValue_2, _exParValue_4);
		exDesignTreeFactory.addLocalAssignmentNode(11, "ScaleWidth",
				"ScaleWidth", _exParValue_1);
		_exParValue_1 = new ExParValue("16");
		exDesignTreeFactory.addLocalAssignmentNode(11, "ScaleHeight",
				"ScaleHeight", _exParValue_1);
		_exParValue_1 = new ExParValue("30");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LabelGap", "LabelGap",
				_exParValue_1);
		_exParValue_4 = new ExParValueVar("introScaleTimer");
		_exParValue_5 = new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.NO_TIMER");
		_exParValue_3 = new ExParValueFunction(12, _exParValue_4, _exParValue_5);
		_exParValue_2 = new ExParValueFunction(26, _exParValue_3);
		ExParValue _exParValue_6 = new ExParValueConstant("JOIN");
		ExParValue _exParValue_7 = new ExParValueConstant("TRANSPARENT");
		_exParValue_1 = new ExParValueFunction(25, _exParValue_2,
				_exParValue_6, _exParValue_7);
		exDesignTreeFactory.addLocalAssignmentNode(11, "Overlay", "Overlay",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant("SPACE_KEY");
		exDesignTreeFactory.addLocalAssignmentNode(11, "ResponseSet",
				"ResponseSet", _exParValue_1);
		_exParValue_1 = new ExParValueFunction(49);
		exDesignTreeFactory.addLocalAssignmentNode(11, "BarColor", "BarColor",
				_exParValue_1);
		_exParValue_1 = new ExParValueFunction(50);
		exDesignTreeFactory.addLocalAssignmentNode(11, "TickColor",
				"TickColor", _exParValue_1);
		_exParValue_1 = new ExParValueFunction(52);
		exDesignTreeFactory.addLocalAssignmentNode(11, "PointerColor",
				"PointerColor", _exParValue_1);
		exDesignTreeFactory.addDisplayNode("TextParagraph:Next");
		_exParValue_2 = new ExParValueVar("introScaleTimer");
		_exParValue_3 = new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.NO_TIMER");
		_exParValue_1 = new ExParValueFunction(12, _exParValue_2, _exParValue_3);
		exDesignTreeFactory.addLocalAssignmentNode(11, "Execute", "Execute",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant("JOIN");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Overlay", "Overlay",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.RELEASE_RESPONSE_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValue(
				"Zum Weiterbl�ttern eine beliebige Taste dr�cken!");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Text", "Text",
				_exParValue_1);
		_exParValue_2 = new ExParValueFunction(35);
		_exParValue_3 = new ExParValue("50");
		_exParValue_1 = new ExParValueFunction(2, _exParValue_2, _exParValue_3);
		exDesignTreeFactory.addLocalAssignmentNode(11, "LocationY",
				"LocationY", _exParValue_1);
		_exParValue_1 = new ExParValueConstant("BASE_CENTER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "ReferencePoint",
				"ReferencePoint", _exParValue_1);
		_exParValue_1 = new ExParValueFunction(50);
		exDesignTreeFactory.addLocalAssignmentNode(11, "Color", "Color",
				_exParValue_1);
		parNames = new String[6];
		parNames[0] = "TrialCounter";
		parNames[1] = "SAMDimension";
		parNames[2] = "Trial.SelfAssessmentManikin:Left.Level";
		parNames[3] = "Trial.SelfAssessmentManikin:Right.Level";
		parNames[4] = "Trial.SelfAssessmentManikin.Level";
		parNames[5] = "Trial.RatingScaleQuestion.ScaleValue";
		exDesignTreeFactory.addDisplayListNode(5, "Trial", parNames);
		exDesignTreeFactory.addDisplayNode("SelfAssessmentManikin:Left");
		_exParValue_1 = new ExParValueVar("SAMLeftBorder");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LocationX",
				"LocationX", _exParValue_1);
		_exParValue_1 = new ExParValueVar("SAMLocationY");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LocationY",
				"LocationY", _exParValue_1);
		_exParValue_1 = new ExParValueConstant("BASE_LEFT");
		exDesignTreeFactory.addLocalAssignmentNode(11, "ReferencePoint",
				"ReferencePoint", _exParValue_1);
		_exParValue_1 = new ExParValueVar("SAMWidth");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Width", "Width",
				_exParValue_1);
		_exParValue_1 = new ExParValueVar("SAMDimension");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Dimension",
				"Dimension", _exParValue_1);
		_exParValue_1 = new ExParValue("1");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Portrait", "Portrait",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant("NO_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		exDesignTreeFactory.addDisplayNode("SelfAssessmentManikin:Right");
		_exParValue_2 = new ExParValueVar("SAMLeftBorder");
		_exParValue_1 = new ExParValueFunction(7, _exParValue_2);
		exDesignTreeFactory.addLocalAssignmentNode(11, "LocationX",
				"LocationX", _exParValue_1);
		_exParValue_1 = new ExParValueVar("SAMLocationY");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LocationY",
				"LocationY", _exParValue_1);
		_exParValue_1 = new ExParValueConstant("BASE_RIGHT");
		exDesignTreeFactory.addLocalAssignmentNode(11, "ReferencePoint",
				"ReferencePoint", _exParValue_1);
		_exParValue_1 = new ExParValueVar("SAMWidth");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Width", "Width",
				_exParValue_1);
		_exParValue_1 = new ExParValueVar("SAMDimension");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Dimension",
				"Dimension", _exParValue_1);
		_exParValue_1 = new ExParValue("1");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Portrait", "Portrait",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant("NO_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant("JOIN");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Overlay", "Overlay",
				_exParValue_1);
		exDesignTreeFactory.addDisplayNode("SelfAssessmentManikin");
		_exParValue_1 = new ExParValue("0");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LocationX",
				"LocationX", _exParValue_1);
		_exParValue_1 = new ExParValueVar("SAMLocationY");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LocationY",
				"LocationY", _exParValue_1);
		_exParValue_1 = new ExParValueConstant("BASE_CENTER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "ReferencePoint",
				"ReferencePoint", _exParValue_1);
		_exParValue_1 = new ExParValueVar("SAMWidth");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Width", "Width",
				_exParValue_1);
		_exParValue_1 = new ExParValueVar("SAMDimension");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Dimension",
				"Dimension", _exParValue_1);
		_exParValue_1 = new ExParValue("1");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Portrait", "Portrait",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant("NO_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant("JOIN");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Overlay", "Overlay",
				_exParValue_1);
		exDesignTreeFactory.addDisplayNode("RatingScaleQuestion");
		_exParValue_1 = new ExParValue("");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Text", "Text",
				_exParValue_1);
		_exParValue_1 = new ExParValue("200");
		exDesignTreeFactory.addLocalAssignmentNode(11, "ScaleLocationY",
				"ScaleLocationY", _exParValue_1);
		_exParValue_1 = new ExParValue("");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LeftLabel",
				"LeftLabel", _exParValue_1);
		_exParValue_1 = new ExParValue("");
		exDesignTreeFactory.addLocalAssignmentNode(11, "RightLabel",
				"RightLabel", _exParValue_1);
		_exParValue_1 = new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.MOUSE_TRACKING_KEY_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValue("0");
		exDesignTreeFactory.addLocalAssignmentNode(11, "ScaleValue",
				"ScaleValue", _exParValue_1);
		_exParValue_1 = new ExParValue("100");
		exDesignTreeFactory.addLocalAssignmentNode(11, "RoundingFactor",
				"RoundingFactor", _exParValue_1);
		_exParValue_1 = new ExParValue("1.0");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LowestTick",
				"LowestTick", _exParValue_1);
		_exParValue_1 = new ExParValue("8.0");
		exDesignTreeFactory.addLocalAssignmentNode(11, "TickStep", "TickStep",
				_exParValue_1);
		_exParValue_1 = new ExParValue("2");
		exDesignTreeFactory.addLocalAssignmentNode(11, "NumberOfTicks",
				"NumberOfTicks", _exParValue_1);
		_exParValue_1 = new ExParValueConstant("BARS");
		exDesignTreeFactory.addLocalAssignmentNode(11, "TickType", "TickType",
				_exParValue_1);
		_exParValue_3 = new ExParValue("2");
		_exParValue_2 = new ExParValueFunction(7, _exParValue_3);
		_exParValue_4 = new ExParValueVar("ScaleLeft");
		_exParValue_1 = new ExParValueFunction(3, _exParValue_2, _exParValue_4);
		exDesignTreeFactory.addLocalAssignmentNode(11, "ScaleWidth",
				"ScaleWidth", _exParValue_1);
		_exParValue_1 = new ExParValue("16");
		exDesignTreeFactory.addLocalAssignmentNode(11, "ScaleHeight",
				"ScaleHeight", _exParValue_1);
		_exParValue_1 = new ExParValue("30");
		exDesignTreeFactory.addLocalAssignmentNode(11, "LabelGap", "LabelGap",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant("TRANSPARENT");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Overlay", "Overlay",
				_exParValue_1);
		_exParValue_1 = new ExParValueConstant("SPACE_KEY");
		exDesignTreeFactory.addLocalAssignmentNode(11, "ResponseSet",
				"ResponseSet", _exParValue_1);
		_exParValue_1 = new ExParValueFunction(49);
		exDesignTreeFactory.addLocalAssignmentNode(11, "BarColor", "BarColor",
				_exParValue_1);
		_exParValue_1 = new ExParValueFunction(50);
		exDesignTreeFactory.addLocalAssignmentNode(11, "TickColor",
				"TickColor", _exParValue_1);
		_exParValue_1 = new ExParValueFunction(52);
		exDesignTreeFactory.addLocalAssignmentNode(11, "PointerColor",
				"PointerColor", _exParValue_1);
		exDesignTreeFactory.addDisplayNode("ClearScreen");
		_exParValue_1 = new ExParValueConstant("CLOCK_TIMER");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Timer", "Timer",
				_exParValue_1);
		_exParValue_1 = new ExParValue("300");
		exDesignTreeFactory.addLocalAssignmentNode(11, "Duration", "Duration",
				_exParValue_1);
		parNames = null;
		exDesignTreeFactory.addAssignmentGroupNode(parNames);
		parValues = null;
		exDesignTreeFactory.addProcedureNode("Procedure", parValues);
		parValues = null;
		exDesignTreeFactory.addSessionNode("Session", parValues);
		parValues = new ExParValue[2];
		_exParValue_1 = new ExParValue("0");
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[1] = _exParValue_1;
		exDesignTreeFactory.addBlockNode("Block", parValues);
		parValues = new ExParValue[10];
		_exParValue_1 = new ExParValue("1");
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[1] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[2] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("NO_TIMER");
		parValues[3] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("VALENCE");
		parValues[4] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[5] = _exParValue_1;
		_exParValue_1 = new ExParValue("9");
		parValues[6] = _exParValue_1;
		_exParValue_1 = new ExParValue("6");
		parValues[7] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[8] = _exParValue_1;
		_exParValue_1 = new ExParValueVar("introText01");
		parValues[9] = _exParValue_1;
		exDesignTreeFactory.addTrialNode("Trial:Intro", parValues);
		parValues = new ExParValue[10];
		_exParValue_1 = new ExParValue("1");
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[1] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[2] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("NO_TIMER");
		parValues[3] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("VALENCE");
		parValues[4] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[5] = _exParValue_1;
		_exParValue_1 = new ExParValue("9");
		parValues[6] = _exParValue_1;
		_exParValue_1 = new ExParValue("6");
		parValues[7] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[8] = _exParValue_1;
		_exParValue_1 = new ExParValueVar("introText02");
		parValues[9] = _exParValue_1;
		exDesignTreeFactory.addTrialNode("Trial:Intro", parValues);
		parValues = new ExParValue[10];
		_exParValue_1 = new ExParValue("1");
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[1] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[2] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("MOUSE_TRACKING_KEY_TIMER");
		parValues[3] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("VALENCE");
		parValues[4] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[5] = _exParValue_1;
		_exParValue_1 = new ExParValue("9");
		parValues[6] = _exParValue_1;
		_exParValue_1 = new ExParValue("6");
		parValues[7] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[8] = _exParValue_1;
		_exParValue_1 = new ExParValueVar("introText03");
		parValues[9] = _exParValue_1;
		exDesignTreeFactory.addTrialNode("Trial:Intro", parValues);
		parValues = new ExParValue[10];
		_exParValue_1 = new ExParValue("1");
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[1] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[2] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("NO_TIMER");
		parValues[3] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("VALENCE");
		parValues[4] = _exParValue_1;
		_exParValue_1 = new ExParValue("3");
		parValues[5] = _exParValue_1;
		_exParValue_1 = new ExParValue("7");
		parValues[6] = _exParValue_1;
		_exParValue_1 = new ExParValue("5");
		parValues[7] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[8] = _exParValue_1;
		_exParValue_1 = new ExParValueVar("introText04");
		parValues[9] = _exParValue_1;
		exDesignTreeFactory.addTrialNode("Trial:Intro", parValues);
		parValues = new ExParValue[10];
		_exParValue_1 = new ExParValue("0");
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[1] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[2] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("NO_TIMER");
		parValues[3] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("VALENCE");
		parValues[4] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[5] = _exParValue_1;
		_exParValue_1 = new ExParValue("9");
		parValues[6] = _exParValue_1;
		_exParValue_1 = new ExParValue("7");
		parValues[7] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[8] = _exParValue_1;
		_exParValue_1 = new ExParValueVar("introText05");
		parValues[9] = _exParValue_1;
		exDesignTreeFactory.addTrialNode("Trial:Intro", parValues);
		parValues = new ExParValue[10];
		_exParValue_1 = new ExParValue("0");
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[1] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[2] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("NO_TIMER");
		parValues[3] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("VALENCE");
		parValues[4] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[5] = _exParValue_1;
		_exParValue_1 = new ExParValue("9");
		parValues[6] = _exParValue_1;
		_exParValue_1 = new ExParValue("7");
		parValues[7] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[8] = _exParValue_1;
		_exParValue_1 = new ExParValueVar("introText06");
		parValues[9] = _exParValue_1;
		exDesignTreeFactory.addTrialNode("Trial:Intro", parValues);
		parValues = new ExParValue[10];
		_exParValue_1 = new ExParValue("1");
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[1] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[2] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("NO_TIMER");
		parValues[3] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("VALENCE");
		parValues[4] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[5] = _exParValue_1;
		_exParValue_1 = new ExParValue("9");
		parValues[6] = _exParValue_1;
		_exParValue_1 = new ExParValue("7");
		parValues[7] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[8] = _exParValue_1;
		_exParValue_1 = new ExParValueVar("introText10");
		parValues[9] = _exParValue_1;
		exDesignTreeFactory.addTrialNode("Trial:Intro", parValues);
		parValues = new ExParValue[10];
		_exParValue_1 = new ExParValue("0");
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[1] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[2] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("NO_TIMER");
		parValues[3] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("VALENCE");
		parValues[4] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[5] = _exParValue_1;
		_exParValue_1 = new ExParValue("9");
		parValues[6] = _exParValue_1;
		_exParValue_1 = new ExParValue("7");
		parValues[7] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[8] = _exParValue_1;
		_exParValue_1 = new ExParValueVar("introText50");
		parValues[9] = _exParValue_1;
		exDesignTreeFactory.addTrialNode("Trial:Intro", parValues);
		parValues = new ExParValue[2];
		_exParValue_1 = new ExParValue("1");
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[1] = _exParValue_1;
		exDesignTreeFactory.addBlockNode("Block", parValues);
		parValues = new ExParValue[6];
		_exParValue_1 = new ExParValueUndefined();
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("VALENCE");
		parValues[1] = _exParValue_1;
		_exParValue_1 = new ExParValue("6");
		parValues[2] = _exParValue_1;
		_exParValue_1 = new ExParValue("9");
		parValues[3] = _exParValue_1;
		String[] _valueArray_1 = { "7", "8" };
		_exParValue_1 = new ExParValue(_valueArray_1);
		_exParValue_1.setExpansion();
		parValues[4] = _exParValue_1;
		_exParValue_1 = new ExParValueUndefined();
		parValues[5] = _exParValue_1;
		exDesignTreeFactory.addTrialNode("Trial", parValues);
		parValues = new ExParValue[2];
		_exParValue_1 = new ExParValue("0");
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[1] = _exParValue_1;
		exDesignTreeFactory.addBlockNode("Block", parValues);
		parValues = new ExParValue[10];
		_exParValue_1 = new ExParValue("1");
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[1] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[2] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("NO_TIMER");
		parValues[3] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("AROUSAL");
		parValues[4] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[5] = _exParValue_1;
		_exParValue_1 = new ExParValue("9");
		parValues[6] = _exParValue_1;
		_exParValue_1 = new ExParValue("7");
		parValues[7] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[8] = _exParValue_1;
		_exParValue_1 = new ExParValueVar("introText20");
		parValues[9] = _exParValue_1;
		exDesignTreeFactory.addTrialNode("Trial:Intro", parValues);
		parValues = new ExParValue[10];
		_exParValue_1 = new ExParValue("0");
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[1] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[2] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("NO_TIMER");
		parValues[3] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("AROUSAL");
		parValues[4] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[5] = _exParValue_1;
		_exParValue_1 = new ExParValue("9");
		parValues[6] = _exParValue_1;
		_exParValue_1 = new ExParValue("7");
		parValues[7] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[8] = _exParValue_1;
		_exParValue_1 = new ExParValueVar("introText50");
		parValues[9] = _exParValue_1;
		exDesignTreeFactory.addTrialNode("Trial:Intro", parValues);
		parValues = new ExParValue[2];
		_exParValue_1 = new ExParValue("1");
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[1] = _exParValue_1;
		exDesignTreeFactory.addBlockNode("Block", parValues);
		parValues = new ExParValue[6];
		_exParValue_1 = new ExParValueUndefined();
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("AROUSAL");
		parValues[1] = _exParValue_1;
		_exParValue_1 = new ExParValue("6");
		parValues[2] = _exParValue_1;
		_exParValue_1 = new ExParValue("9");
		parValues[3] = _exParValue_1;
		String[] _valueArray_2 = { "7", "8" };
		_exParValue_1 = new ExParValue(_valueArray_2);
		_exParValue_1.setExpansion();
		parValues[4] = _exParValue_1;
		_exParValue_1 = new ExParValueUndefined();
		parValues[5] = _exParValue_1;
		exDesignTreeFactory.addTrialNode("Trial", parValues);
		parValues = new ExParValue[2];
		_exParValue_1 = new ExParValue("0");
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[1] = _exParValue_1;
		exDesignTreeFactory.addBlockNode("Block", parValues);
		parValues = new ExParValue[10];
		_exParValue_1 = new ExParValue("1");
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[1] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[2] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("NO_TIMER");
		parValues[3] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("DOMINANCE");
		parValues[4] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[5] = _exParValue_1;
		_exParValue_1 = new ExParValue("9");
		parValues[6] = _exParValue_1;
		_exParValue_1 = new ExParValue("7");
		parValues[7] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[8] = _exParValue_1;
		_exParValue_1 = new ExParValueVar("introText30");
		parValues[9] = _exParValue_1;
		exDesignTreeFactory.addTrialNode("Trial:Intro", parValues);
		parValues = new ExParValue[10];
		_exParValue_1 = new ExParValue("0");
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[1] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[2] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("NO_TIMER");
		parValues[3] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("DOMINANCE");
		parValues[4] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[5] = _exParValue_1;
		_exParValue_1 = new ExParValue("9");
		parValues[6] = _exParValue_1;
		_exParValue_1 = new ExParValue("7");
		parValues[7] = _exParValue_1;
		_exParValue_1 = new ExParValue("0");
		parValues[8] = _exParValue_1;
		_exParValue_1 = new ExParValueVar("introText50");
		parValues[9] = _exParValue_1;
		exDesignTreeFactory.addTrialNode("Trial:Intro", parValues);
		parValues = new ExParValue[2];
		_exParValue_1 = new ExParValue("1");
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValue("1");
		parValues[1] = _exParValue_1;
		exDesignTreeFactory.addBlockNode("Block", parValues);
		parValues = new ExParValue[6];
		_exParValue_1 = new ExParValueUndefined();
		parValues[0] = _exParValue_1;
		_exParValue_1 = new ExParValueConstant("DOMINANCE");
		parValues[1] = _exParValue_1;
		_exParValue_1 = new ExParValue("6");
		parValues[2] = _exParValue_1;
		_exParValue_1 = new ExParValue("9");
		parValues[3] = _exParValue_1;
		String[] _valueArray_3 = { "7", "8" };
		_exParValue_1 = new ExParValue(_valueArray_3);
		_exParValue_1.setExpansion();
		parValues[4] = _exParValue_1;
		_exParValue_1 = new ExParValueUndefined();
		parValues[5] = _exParValue_1;
		exDesignTreeFactory.addTrialNode("Trial", parValues);
	}

	public static void main(String[] args) {
		// de.pxlab.pxl.Debug.add(de.pxlab.pxl.Debug.FACTORY);
		// de.pxlab.pxl.Debug.add(de.pxlab.pxl.Debug.CREATE_NODE);
		ExDesign x = new PXLabExperiment();
		// x.print();
		new ExRun(args, x);
	}
}
