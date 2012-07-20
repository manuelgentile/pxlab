/********************************************************************
  filename:  EyeOne.java
	
  purpose:	 A sample implementation

  Copyright (c) 2003 by GretagMacbeth AG Switzerland.
  
  ALL RIGHTS RESERVED.  

  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS
  OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
  ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
  GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 *********************************************************************/
package com.gretagmacbeth.eyeone;

public class EyeOne implements EyeOneConstants {
	private static EyeOne theInstance = new EyeOne();

	private EyeOne() {
		System.loadLibrary("JEyeOne");
	}

	public static EyeOne getInstance() {
		return theInstance;
	}

	public native boolean isConnected() throws EyeOneException;

	public native boolean isKeyPressed() throws EyeOneException;

	public native int getNumberOfAvailableSamples();

	public native void calibrate() throws EyeOneException;

	public native void triggerMeasurement() throws EyeOneException;

	public native float[/* 36 */] getSpectrum(long index)
			throws EyeOneException;

	public native float[/* 3 */] getTriStimulus(long index)
			throws EyeOneException;

	public native float getDensity(long index) throws EyeOneException;

	public native float[/* 5 */] getDensities(long index)
			throws EyeOneException;

	public native void setSubstrate(float[] spectrum) throws EyeOneException;

	public native void setOption(String key, String value)
			throws EyeOneException;

	public native String getOption(String key);
}
