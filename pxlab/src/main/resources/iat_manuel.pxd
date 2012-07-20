
/* BIAT il più possibile simile a quello di inquisit disponibile
nel sito di Millisecond.com */

Experiment() {

	Context() {
		AssignmentGroup() 
		{
			ExperimentName = "Brief Implicit Associations Test";
			SubjectCode = "pxlab";
			new aggettivi_positivi = "Felicita' Gioia Amicizia Allegria";
			new cittadini_settentrionali = "Milanesi Torinesi Veneziani Genovesi";
			new cittadini_meridionali = "Napoletani Palermitani Baresi Catanzaresi";
			new cat_cittadini_settentrionali = "SETTENTRIONALI";
			new cat_cittadini_meridionali = "MERIDIONALI";
			new cat_aggettivi_positivi = "POSITIVI";
			new cat_aggettivi_negativi = "NEGATIVI";
			new categoria1="categoria1";
			new categoria2="categoria2";
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
			SkipBoundingBlockDisplays = 0;
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
				FontSize= 25;
			}
			
			TextParagraph:cat1() 
			{
				Color = yellow();
				
				LocationY=-300;
				Alignment = de.pxlab.pxl.AlignmentCodes.CENTER;
				Text = ["%cat_aggettivi_positivi%",
				"%aggettivi_positivi%"];
				Overlay = de.pxlab.pxl.OverlayCodes.JOIN;
				FontFamily="Arial";
				FontSize= 30;
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
			  
			  
		Block(categoria1,descrizione_categoria1,categoria2,descrizione_categoria2, 
			cat_cittadini_settentrionaliPosition, cat_cittadini_meridionaliPosition, cat_aggettivi_positiviPosition, cat_aggettivi_negativiPosition) 
		{
			
			
		}
			  
		Block:A()
		{
			BlockStartMessage()
			{
				Text= [
				"Premi il tasto I ogni volta che appare un termine della categoria %cat_aggettivi_positivi% sopra indicata. Premi il tasto E  per ogni altro termine.",
				" ",
				"Svolgi il compito il piu' velocemente possibile",
				" ",
				"Premi la barra spaziatrice per incominciare."];
				Alignment = de.pxlab.pxl.AlignmentCodes.CENTER;
				FontFamily="Arial";
				FontSize= 30;           
			}
		}
				   
		Block:APP(categoria1, cat_aggettivi_positiviPosition, cat_aggettivi_negativiPosition) {}
		Block:B()
		{
			BlockStartMessage()
			{
				Text= [
				"Premi il tasto I ogni volta che appare un termine delle categorie %cat_aggettivi_positivi% o %cat_cittadini_settentrionali% sopra indicate. Premi il tasto E  per tutti gli altri termini.",
				" ",
				"Svolgi il compito il più velocemente possibile",
				" ",
				"Premi la barra spaziatrice per incominciare."];
				Alignment = de.pxlab.pxl.AlignmentCodes.CENTER;
				FontFamily="Arial";
				FontSize= 30;
			}
			TextParagraph:cat1() 
			{
				Color = yellow();
				ReferencePoint = de.pxlab.pxl.PositionReferenceCodes.MIDDLE_LEFT;
				
				Text = "prova";
				Overlay = de.pxlab.pxl.OverlayCodes.JOIN;
				FontFamily="Arial";
				FontSize= 30;
			}
			
			
		}
		Block:C()
		{
			BlockStartMessage()
			{
				Text= [
				"Premi il tasto I ogni volta che appare un termine delle categorie %cat_aggettivi_positivi% o %cat_cittadini_meridionali% sopra indicate. Premi il tasto E  per tutti gli altri termini.",
				" ",
				"Svolgi il compito il più velocemente possibile",
				" ",
				"Premi la barra spaziatrice per incominciare."];
				Alignment = de.pxlab.pxl.AlignmentCodes.CENTER;
				FontFamily="Arial";
				FontSize= 30;
			}
		}
		
		Trial:AP(item, Feedback.CorrectCode, Feedback.Response, Feedback.ResponseTime, Message.ResponseTime) 
		{
			TextParagraph:cat1() 
			{
				LocationX = 0;
				LocationY =  -300;
				Color = yellow();
				ReferencePoint = de.pxlab.pxl.PositionReferenceCodes.TOP_CENTER;
				Alignment = de.pxlab.pxl.AlignmentCodes.CENTER;
				Text = categoria1;
				Timer = de.pxlab.pxl.TimerCodes.NO_TIMER;
				FontFamily="Arial";
				FontSize= 30;
			}
			
			
			 
			Message() 
			{
				Overlay = de.pxlab.pxl.OverlayCodes.JOIN;
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
				Overlay = de.pxlab.pxl.OverlayCodes.TRANSPARENT;
				FontSize = 72;	
				LocationY = 280;
			}
			ClearScreen() 
			{
				Timer = de.pxlab.pxl.TimerCodes.CLOCK_TIMER;
				Duration = 500;
			}
		}
		Trial:T(item,col, Feedback.CorrectCode, Feedback.Response, Feedback.ResponseTime, Message.ResponseTime) 
		{
			MyTextParagraph:A() {
				Overlay = de.pxlab.pxl.OverlayCodes.DISPLAY_LIST;
				LocationX = 0;
				LocationY =  -400;
				Color = cyan();
				ReferencePoint = de.pxlab.pxl.PositionReferenceCodes.TOP_CENTER;
				Alignment = de.pxlab.pxl.AlignmentCodes.CENTER;
				Text = categoria1;
				Timer = de.pxlab.pxl.TimerCodes.NO_TIMER;
				
				FontFamily="Arial";
				FontSize= 30;
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
				Overlay = de.pxlab.pxl.OverlayCodes.TRANSPARENT;
				FontSize = 72;
				LocationY = 280;
			}
			ClearScreen() {
				Timer = de.pxlab.pxl.TimerCodes.CLOCK_TIMER;
				Duration = 500;
			}
		}
	}
 
	Procedure() {
		Session() 
		{
			
			Block:B(){}
			Block("%cat_cittadini_settentrionali%","%cittadini_settentrionali%", "%cat_aggettivi_positivi%","%aggettivi_positivi%",1, 0, 1, 0) 
			{
				/* Meridionali */
				Trial:T("Napoletani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("Palermitani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("Baresi", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("Catanzaresi", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				/* Settentrionali */
				Trial:T("Milanesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("Torinesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("Veneziani", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("Genovesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				/* Positivi */
				Trial:T("Felicita'", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("Gioia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("Amicizia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("Allegria", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				/* Negativi */
				Trial:T("Guerra", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("Odio", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("Violenza", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("Morte", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
			}

			Block:C(){}
			Block("%cat_cittadini_meridionali%","%cittadini_meridionali%","%cat_aggettivi_positivi%","%aggettivi_positivi%", 0, 1, 1, 0)
			{
				/* Meridionali */
				Trial:T("Napoletani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("Palermitani", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("Baresi", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				Trial:T("Catanzaresi", catcol, cat_cittadini_meridionaliPosition, ?, ?, ?);
				/* Settentrionali */
				Trial:T("Milanesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("Torinesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("Veneziani", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				Trial:T("Genovesi", catcol, cat_cittadini_settentrionaliPosition, ?, ?, ?);
				/* Positivi */
				Trial:T("Felicita'", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("Gioia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("Amicizia", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				Trial:T("Allegria", agcol, cat_aggettivi_positiviPosition, ?, ?, ?);
				/* Negativi */
				Trial:T("Guerra", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("Odio", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("Violenza", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
				Trial:T("Morte", agcol, cat_aggettivi_negativiPosition, ?, ?, ?);
			}
		}
	}
}
