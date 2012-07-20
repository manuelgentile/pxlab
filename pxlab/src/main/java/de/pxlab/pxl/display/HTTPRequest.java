package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Send a HTTP request to a given URL and collect the response. This object
 * sends a POST request to the target URL and collects the receiving script's
 * response into the experimental parameter ResponseCode. The POST message
 * contains string valued assignments to some variables which may be used by the
 * receiving script. The following parameter value names are being used:
 * 
 * <dl>
 * 
 * <dt>Source
 * <dd>may contain information about the source of the HTTP request.
 * 
 * <dt>Address
 * <dd>can be an E-mail address which may be used by a receiving script to send
 * an E-mail to this address.
 * 
 * <dt>Subject
 * <dd>can be used for the subject field of the E-mail.
 * 
 * <dt>Text
 * <dd>the main text content of the POST request.
 * 
 * </dl>
 * 
 * <p>
 * Note that the meaning of these parameters is finally determined by the
 * receiving script. Here is an example of a php script which takes the
 * information from the request to send an E-mail to the specified address:
 * 
 * <pre>
 * <?php 
 *   $SendData = "\nSending Document: ".$_SERVER['HTTP_REFERER']."\n".
 * 	"Source IP: ".$_SERVER['REMOTE_ADDR']."\n".
 * 	"Script: ".$_SERVER['SCRIPT_FILENAME']."\n".
 * 	"Request Method: ".$_SERVER['REQUEST_METHOD']."\n".
 * 	"Source: ".$_REQUEST['Source']."\n".
 * 	"Address: ".$_REQUEST['Address']."\n".
 * 	"Subject: ".$_REQUEST['Subject']."\n".
 * 	"Text: \n\n".$_REQUEST['Text']."\n";
 *   mail($_REQUEST['Address'], $_REQUEST['Subject']." ".$_REQUEST['Source'], $SendData, "From: PXLab" ); 
 * ?>
 * </pre>
 * 
 * <p>
 * This script does not generate any response text. Here is another example of a
 * php script which generates some response text:
 * 
 * <pre>
 * <?php 
 *   $s1 = "Heads up my dear ".$_REQUEST['Source']."%0AThings will get better real soon!";
 *   $s2 = "Heads up my dear ".$_REQUEST['Source']."%0AThings will stay as good as they are!";
 *   $s3 = "Heads up my dear ".$_REQUEST['Source']."%0AThings can hardly get worse!";
 *   $r = mt_rand(1, 3);
 *   if ($r==1) {
 *     echo $s1;
 *   } else if ($r==2) {
 *     echo $s2;
 *   } else {
 *     echo $s3;
 *   }
 * ?>
 * </pre>
 * 
 * 
 * <p>
 * The generated response text is assigned to the Display object's parameter
 * ResponseCode and may be used later in the experiment.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2008/03/07
 */
public class HTTPRequest extends Display {
	/** The script URL which gets this request. */
	public ExPar ScriptURL = new ExPar(
			STRING,
			new ExParValue(
					"http://www.uni-mannheim.de/fakul/psycho/irtel/pxlab/demos/request.php"),
			"URL of the receiving script");
	/** A source identifier. */
	public ExPar Source = new ExPar(STRING, new ExParValue("source"),
			"Source identifier");
	/** An E-Mail addres which may be the final target of this request. */
	public ExPar Address = new ExPar(STRING, new ExParValue(
			"webmaster@pxlab.de"), "Destination address");
	/** The 'subject' string for the E-Mail message. */
	public ExPar Subject = new ExPar(STRING, new ExParValue(
			"PXLab HTTP Request"), "Subject field");
	/** The text to be sent. */
	public ExPar Text = new ExPar(STRING, new ExParValue(
			"This is a PXLab message"), "Text content");

	public HTTPRequest() {
		setTitleAndTopic("Send a HTTP request", CONTROL_DSP | EXP);
		JustInTime.set(1);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	public boolean getCanPreload() {
		return false;
	}

	public boolean isGraphic() {
		return false;
	}
	protected HTTPElement http;

	protected int create() {
		removeDisplayElements(backgroundFieldIndex);
		backgroundFieldIndex = -1;
		http = new HTTPElement();
		enterDisplayElement(http, group[0]);
		defaultTiming(0);
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
		// System.out.println("SystemProcess.computeGeometry()");
		http.setProperties(ScriptURL.getString(), "POST", Source.getString(),
				Address.getString(), Subject.getString(), Text.getString());
	}

	protected void timingGroupFinished(int group) {
		ResponseCode.set(http.getResponse());
	}
}
