
/* BIAT il più possibile simile a quello di inquisit disponibile
nel sito di Millisecond.com */

Experiment() {

	Context() {
		AssignmentGroup() 
		{
		 
			ExperimentName = "Brief Implicit Associations Test";
			SubjectCode ="";
			SubjectGroup="";
			DataFileDestination= "/Users/alberto/Documents/workspace/pxlab/pxlab/";
			new aggettivi_positivi = "Felicita' Gioia Amicizia Allegria";
			new cittadini_settentrionali = "Milanesi Torinesi Veneziani Genovesi";
			new cittadini_meridionali = "Napoletani Palermitani Baresi Catanzaresi";
			new cat_cittadini_settentrionali = "SETTENTRIONALI";
			new cat_cittadini_meridionali = "MERIDIONALI";
			new cat_aggettivi_positivi = "POSITIVI";
			new cat_aggettivi_negativi = "NEGATIVI";
			new categoria1="";
			new categoria2="";
			new categories="";
			new descrizione_categoria1="des_categoria1";
			new descrizione_categoria2="des_categoria2";
			new categories = 0;
			new categories1 = 0;
			new item = 0;
			new col=0;
			new agcol = yellow();
			new catcol= cyan();
			new cat_cittadini_settentrionaliPosition = 0;
			new cat_cittadini_meridionaliPosition = 0;
			new cat_aggettivi_positiviPosition = 0;
			new cat_aggettivi_negativiPosition = 0;
			new testo1 = 0;
			new blocco = 0;
			SkipBoundingBlockDisplays = 0;
			DataFileTrialFormat = "%SubjectCode%	%SubjectGroup%	%item%	%blocco%	%Trial:T.Feedback.CorrectCode%	%Trial:T.Feedback.Response%	%Trial:T.Feedback.ResponseTime%	%Trial:T.Message.ResponseTime%";
			
		}
		Session() 
		{
			Instruction() 
			{
				Text = [ "In questa sezione farai un compito per valutare la tua capacita' di categorizzare degli stimoli nella maniera piu'  VELOCE ED ACCURATA possibile:",
				"termini '%cat_aggettivi_positivi%', termini '%cat_aggettivi_negativi%', abitanti di citta' '%cat_cittadini_settentrionali%' o abitanti di citta' '%cat_cittadini_meridionali%'.",
				"Per svolgere la prova il piu' accuratamente possibile posiziona l'indice della mano Sinistra sul tasto ~ E ~  e quello della mano Destra sul tasto ~ I ~.",
				"Dovrai premere il tasto I o il tasto E a seconda di quale termine comparira'. Le prime due prove che vedrai servono a renderti familiare il tipo di compito da svolgere.",
				"Ricorda: il tuo compito e' quello di  provare a classificare gli stimoli che riceverai il piu' Velocemente ed Accuratamente possibile.",
				"Se dovessi andare troppo lentemente o fare troppi errori la prova sara' invalidata. Non preoccuparti per qualche errore occasionale. Se farai un errore apparira' una ~ X ~.",
				"Correggi rapidamente l'errore premendo il tasto corretto (E o I).",
				" ",
				"Per continuare premi la barra spaziatrice."]; 
				Alignment = de.pxlab.pxl.AlignmentCodes.CENTER;
				FontFamily="Arial";
				FontSize= 20;
				LocationY=50;
			}
			
			TextParagraph:cat1() 
			{
				Color = yellow();
				
				LocationY=-200;
				Alignment = de.pxlab.pxl.AlignmentCodes.CENTER;
				Text = ["%cat_aggettivi_positivi%",
				"%aggettivi_positivi%"];
				Overlay = de.pxlab.pxl.OverlayCodes.JOIN;
				FontFamily="Arial";
				FontSize= 30;
				ResponseSet = de.pxlab.pxl.KeyCodes.SPACE_KEY;
			}
		}
		SessionEnd() 
		{
			SessionEndMessage()
			{
				Text="FINE!";
			}
		}
			  //**************PARTE DI APPRENDIMENTO***********************************
			  
			  
		Block(TrialFactor,ActiveSubjectGroups,categories,categoria1,descrizione_categoria1,categoria2,descrizione_categoria2, 
			cat_cittadini_settentrionaliPosition, cat_cittadini_meridionaliPosition, cat_aggettivi_positiviPosition, cat_aggettivi_negativiPosition) 
		{
			
			
		}
			  
		Block:A()
		{
			BlockStartMessage()
			{
				Text= [
				"Premi il tasto ~ I ~ ogni volta che appare un termine della categoria %cat_aggettivi_positivi% sopra indicata. Premi il tasto ~ E ~ per ogni altro termine.",
				" ",
				"Svolgi il compito il piu' velocemente possibile",
				" ",
				"Premi la barra spaziatrice per incominciare."];
				Alignment = de.pxlab.pxl.AlignmentCodes.CENTER;
				FontFamily="Arial";
				FontSize= 30;  
				LocationY=100;         
			}
			TextParagraph:cat1() 
			{
				Color = yellow();
				
				LocationY=-200;
				Alignment = de.pxlab.pxl.AlignmentCodes.CENTER;
				Text = ["%cat_aggettivi_positivi%",
				"%aggettivi_positivi%"];
				Overlay = de.pxlab.pxl.OverlayCodes.JOIN;
				FontFamily="Arial";
				FontSize= 30;
				ResponseSet = de.pxlab.pxl.KeyCodes.SPACE_KEY;
				
			}
		}
				   
		Block:APP(TrialFactor, categoria1, cat_aggettivi_positiviPosition, cat_aggettivi_negativiPosition) {}
		
		Block:B(ActiveSubjectGroups)
		{
			BlockStartMessage()
			{
				Text= [
				"Premi il tasto ~ I ~ ogni volta che appare un termine delle categorie '%cat_aggettivi_positivi%' o '%cat_cittadini_settentrionali%' sopra indicate. Premi il tasto ~ E ~  per tutti gli altri termini.",
				" ",
				"Svolgi il compito il piu' velocemente possibile",
				" ",
				"Premi la barra spaziatrice per incominciare."];
				Alignment = de.pxlab.pxl.AlignmentCodes.CENTER;
				FontFamily="Arial";
				FontSize= 30;
				LocationY=180; 
			}
			
				TextParagraph:cat2() 
			{
				Color = cyan();
				
				LocationY=-220;
				Alignment = de.pxlab.pxl.AlignmentCodes.CENTER;
				Text = ["%cat_cittadini_settentrionali%",
				"%cittadini_settentrionali%"];
				Overlay = de.pxlab.pxl.OverlayCodes.JOIN;
				FontFamily="Arial";
				FontSize= 30;
				ResponseSet = de.pxlab.pxl.KeyCodes.SPACE_KEY;
			}
			
			TextParagraph:cat1() 
			{
				Color = yellow();
				
				LocationY=-130;
				Alignment = de.pxlab.pxl.AlignmentCodes.CENTER;
				Text = ["%cat_aggettivi_positivi%",
				"%aggettivi_positivi%"];
				Overlay = de.pxlab.pxl.OverlayCodes.JOIN;
				FontFamily="Arial";
				FontSize= 30;
				ResponseSet = de.pxlab.pxl.KeyCodes.SPACE_KEY;
			}
			
		}
		Block:C(ActiveSubjectGroups)
		{
			BlockStartMessage()
			{
				Text= [
				"Premi il tasto ~ I ~ ogni volta che appare un termine delle categorie '%cat_aggettivi_positivi%' o '%cat_cittadini_meridionali%' sopra indicate. Premi il tasto ~ E ~ per tutti gli altri termini.",
				" ",
				"Svolgi il compito il piu' velocemente possibile",
				" ",
				"Premi la barra spaziatrice per incominciare."];
				Alignment = de.pxlab.pxl.AlignmentCodes.CENTER;
				FontFamily="Arial";
				FontSize= 30;
				LocationY=180; 
			}
				TextParagraph:cat2() 
			{
				Color = cyan();
				
				LocationY=-220;
				Alignment = de.pxlab.pxl.AlignmentCodes.CENTER;
				Text = ["%cat_cittadini_meridionali%",
				"%cittadini_meridionali%"];
				Overlay = de.pxlab.pxl.OverlayCodes.JOIN;
				FontFamily="Arial";
				FontSize= 30;
				ResponseSet = de.pxlab.pxl.KeyCodes.SPACE_KEY;
			}
			
			TextParagraph:cat1() 
			{
				Color = yellow();
				
				LocationY=-130;
				Alignment = de.pxlab.pxl.AlignmentCodes.CENTER;
				Text = ["%cat_aggettivi_positivi%",
				"%aggettivi_positivi%"];
				Overlay = de.pxlab.pxl.OverlayCodes.JOIN;
				FontFamily="Arial";
				FontSize= 30;
				ResponseSet = de.pxlab.pxl.KeyCodes.SPACE_KEY;
			}
		}
		
		Trial:AP(blocco, item, Feedback.CorrectCode, Feedback.Response, Feedback.ResponseTime, Message.ResponseTime) 
		{
			TextParagraph:cat1() 
			{
				LocationX = 0;
				LocationY =  -250;
				Color = yellow();
				ReferencePoint = de.pxlab.pxl.PositionReferenceCodes.TOP_CENTER;
				Alignment = de.pxlab.pxl.AlignmentCodes.CENTER;
				Text = categoria1;
				Timer = de.pxlab.pxl.TimerCodes.NO_TIMER;
				FontFamily="Arial";
				FontSize= 30;
				Overlay = de.pxlab.pxl.OverlayCodes.DISPLAY_LIST;
			}
			
			
			 
			Message() 
			{
				//Overlay = de.pxlab.pxl.OverlayCodes.JOIN;
				Text = item;
				Color= yellow();
				Timer = de.pxlab.pxl.TimerCodes.RESPONSE_TIMER;
				ResponseSet = [de.pxlab.pxl.KeyCodes.E_KEY, de.pxlab.pxl.KeyCodes.I_KEY];
						FontFamily="Arial";
						FontSize= 70;
			}
			Feedback() 
			{
				ResponseParameter = Trial:AP.Message.ResponseCode;
				Evaluation = CHECK_NOGO;
				FalseText = "X";
				Color = red();
				Timer = de.pxlab.pxl.TimerCodes.RESPONSE_TIMER;
				ResponseSet = (Trial:AP.Feedback.CorrectCode == 0)?
					   de.pxlab.pxl.KeyCodes.E_KEY: de.pxlab.pxl.KeyCodes.I_KEY;
				//Overlay = de.pxlab.pxl.OverlayCodes.TRANSPARENT;
				FontSize = 72;	
				LocationY = 250;
			}
			ClearScreen() 
			{
				Timer = de.pxlab.pxl.TimerCodes.CLOCK_TIMER;
				Duration = 250;
			}
		}
		Trial:T(blocco, item,col, Feedback.CorrectCode, Feedback.Response, Feedback.ResponseTime, Message.ResponseTime) 
		{
			TextParagraphMultiple:A() {
				Overlay = de.pxlab.pxl.OverlayCodes.DISPLAY_LIST;
				LocationX = [0,0];
				LocationY =  [-250,-200];
				Color = [53,52];
				ReferencePoint = de.pxlab.pxl.PositionReferenceCodes.TOP_CENTER;
				Alignment = de.pxlab.pxl.AlignmentCodes.CENTER;
				Text = categories;
				Timer = de.pxlab.pxl.TimerCodes.NO_TIMER;
				
				FontFamily="Arial";
				FontSize= 35;
			}
		
			
			
			Message() {
				
				Text = item;
				Color= col;
				Timer = de.pxlab.pxl.TimerCodes.RESPONSE_TIMER;
				ResponseSet = [de.pxlab.pxl.KeyCodes.E_KEY, de.pxlab.pxl.KeyCodes.I_KEY];
				FontFamily="Arial";
				FontSize= 70;
			}
			Feedback() {
				ResponseParameter = Trial:T.Message.ResponseCode;
				Evaluation = CHECK_NOGO;
				FalseText = "X";
				Color = red();
				Timer = de.pxlab.pxl.TimerCodes.RESPONSE_TIMER;
				ResponseSet = (Trial:T.Feedback.CorrectCode == 0)?
				de.pxlab.pxl.KeyCodes.E_KEY: de.pxlab.pxl.KeyCodes.I_KEY;
				//Overlay = de.pxlab.pxl.OverlayCodes.TRANSPARENT;
				FontSize = 72;
				LocationY = 250;
			}
			ClearScreen() {
				Timer = de.pxlab.pxl.TimerCodes.CLOCK_TIMER;
				Duration = 250;
			}
		}
	}
 
	Procedure() {
		Session() 
		{
		
		
		
		Block:A(){}// SOLO POSITIVI APPRENDIMENTO
		Block:APP(3, "%cat_aggettivi_positivi%",1, 0)
            {
                /* Positivi */
                Trial:AP("PositiviApp","Felicita'", cat_aggettivi_positiviPosition, ?, ?, ?);
                Trial:AP("PositiviApp","Gioia", cat_aggettivi_positiviPosition, ?, ?, ?);
                Trial:AP("PositiviApp","Amicizia", cat_aggettivi_positiviPosition, ?, ?, ?);
                Trial:AP("PositiviApp","Allegria", cat_aggettivi_positiviPosition, ?, ?, ?);
                /* Negativi */
                Trial:AP("PositiviApp","Guerra", cat_aggettivi_negativiPosition, ?, ?, ?);
                Trial:AP("PositiviApp","Odio", cat_aggettivi_negativiPosition, ?, ?, ?);
                Trial:AP("PositiviApp","Violenza", cat_aggettivi_negativiPosition, ?, ?, ?);
                Trial:AP("PositiviApp","Morte", cat_aggettivi_negativiPosition, ?, ?, ?);
               
      
            }
			
			Block:B(["A","B"]){}// COERENTI NORD APPRENDIMENTO
			Block(1,["A","B"],["%cat_cittadini_settentrionali%","%cat_aggettivi_positivi%"],"%cat_cittadini_settentrionali%","%cittadini_settentrionali%", "%cat_aggettivi_positivi%","%aggettivi_positivi%",1, 0, 1, 0) 
			{
				/* Meridionali */
				Trial:T("NordApp","Napoletani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("NordApp","Palermitani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("NordApp","Baresi", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("NordApp","Catanzaresi", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				/* Settentrionali */
				Trial:T("NordApp","Milanesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("NordApp","Torinesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("NordApp","Veneziani", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("NordApp","Genovesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				/* Positivi */
				Trial:T("NordApp","Felicita'", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("NordApp","Gioia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("NordApp","Amicizia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("NordApp","Allegria", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				/* Negativi */
				Trial:T("NordApp","Guerra", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("NordApp","Odio", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("NordApp","Violenza", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("NordApp","Morte", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				
				
				Trial:T("NordApp","Napoletani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("NordApp","Milanesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("NordApp","Gioia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("NordApp","Odio", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
			}

			Block:C(["A","B"]){} // COERENTI SUD APPRENDIMENTO
			Block(1,["A","B"], ["%cat_cittadini_meridionali%","%cat_aggettivi_positivi%"],"%cat_cittadini_meridionali%","%cittadini_meridionali%","%cat_aggettivi_positivi%","%aggettivi_positivi%", 0, 1, 1, 0)
			{
				/* Meridionali */
				Trial:T("SudApp","Napoletani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("SudApp","Palermitani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("SudApp","Baresi", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("SudApp","Catanzaresi", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				/* Settentrionali */
				Trial:T("SudApp","Milanesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("SudApp","Torinesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("SudApp","Veneziani", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("SudApp","Genovesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				/* Positivi */
				Trial:T("SudApp","Felicita'", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("SudApp","Gioia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("SudApp","Amicizia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("SudApp","Allegria", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				/* Negativi */
				Trial:T("SudApp","Guerra", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("SudApp","Odio", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("SudApp","Violenza", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("SudApp","Morte", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				
				Trial:T("SudApp","Napoletani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("SudApp","Milanesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("SudApp","Gioia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("SudApp","Odio", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
			}
			Block:B("A"){}// COERENTI NORD 
			Block(1,"A",["%cat_cittadini_settentrionali%","%cat_aggettivi_positivi%"], "%cat_cittadini_settentrionali%","%cittadini_settentrionali%", "%cat_aggettivi_positivi%","%aggettivi_positivi%",1, 0, 1, 0) 
			{
				/* Meridionali */
				Trial:T("NordTest", "Napoletani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("NordTest","Palermitani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("NordTest","Baresi", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("NordTest","Catanzaresi", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				/* Settentrionali */
				Trial:T("NordTest","Milanesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("NordTest","Torinesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("NordTest","Veneziani", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("NordTest","Genovesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				/* Positivi */
				Trial:T("NordTest","Felicita'", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("NordTest","Gioia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("NordTest","Amicizia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("NordTest","Allegria", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				/* Negativi */
				Trial:T("NordTest","Guerra", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("NordTest","Odio", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("NordTest","Violenza", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("NordTest","Morte", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				
				
				Trial:T("NordTest","Napoletani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("NordTest","Milanesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("NordTest","Gioia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("NordTest","Odio", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
			}

			Block:C("A"){} // COERENTI SUD 
			Block(1,"A", ["%cat_cittadini_meridionali%","%cat_aggettivi_positivi%"],"%cat_cittadini_meridionali%","%cittadini_meridionali%","%cat_aggettivi_positivi%","%aggettivi_positivi%", 0, 1, 1, 0)
			{
				/* Meridionali */
				Trial:T("SudTest","Napoletani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("SudTest","Palermitani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("SudTest","Baresi", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("SudTest","Catanzaresi", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				/* Settentrionali */
				Trial:T("SudTest","Milanesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("SudTest","Torinesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("SudTest","Veneziani", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("SudTest","Genovesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				/* Positivi */
				Trial:T("SudTest","Felicita'", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("SudTest","Gioia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("SudTest","Amicizia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("SudTest","Allegria", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				/* Negativi */
				Trial:T("SudTest","Guerra", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("SudTest","Odio", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("SudTest","Violenza", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("SudTest","Morte", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				
				Trial:T("SudTest","Napoletani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("SudTest","Milanesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("SudTest","Gioia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("SudTest","Odio", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
			}
		
		
		Block:C("B"){} // COERENTI SUD 
			Block(1,"B", ["%cat_cittadini_meridionali%","%cat_aggettivi_positivi%"],"%cat_cittadini_meridionali%","%cittadini_meridionali%","%cat_aggettivi_positivi%","%aggettivi_positivi%", 0, 1, 1, 0)
			{
				/* Meridionali */
				Trial:T("SudTest","Napoletani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("SudTest","Palermitani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("SudTest","Baresi", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("SudTest","Catanzaresi", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				/* Settentrionali */
				Trial:T("SudTest","Milanesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("SudTest","Torinesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("SudTest","Veneziani", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("SudTest","Genovesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				/* Positivi */
				Trial:T("SudTest","Felicita'", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("SudTest","Gioia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("SudTest","Amicizia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("SudTest","Allegria", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				/* Negativi */
				Trial:T("SudTest","Guerra", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("SudTest","Odio", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("SudTest","Violenza", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("SudTest","Morte", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				
				Trial:T("SudTest","Napoletani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("SudTest","Milanesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("SudTest","Gioia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("SudTest","Odio", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
			}
		
		
		
		
		Block:B("B"){}// COERENTI NORD 
			Block(1,"B",["%cat_cittadini_settentrionali%","%cat_aggettivi_positivi%"],"%cat_cittadini_settentrionali%","%cittadini_settentrionali%", "%cat_aggettivi_positivi%","%aggettivi_positivi%",1, 0, 1, 0) 
			{
				/* Meridionali */
				Trial:T("NordTest","Napoletani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("NordTest","Palermitani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("NordTest","Baresi", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("NordTest","Catanzaresi", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				/* Settentrionali */
				Trial:T("NordTest","Milanesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("NordTest","Torinesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("NordTest","Veneziani", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("NordTest","Genovesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				/* Positivi */
				Trial:T("NordTest","Felicita'", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("NordTest","Gioia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("NordTest","Amicizia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("NordTest","Allegria", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				/* Negativi */
				Trial:T("NordTest","Guerra", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("NordTest","Odio", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("NordTest","Violenza", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("NordTest","Morte", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				
				
				Trial:T("NordTest","Napoletani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("NordTest","Milanesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("NordTest","Gioia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("NordTest","Odio", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
			}

			
		
	
		}
	}
}
