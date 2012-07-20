/********************************************************************
  filename:  EyeOneDemo.java
	
  purpose:	 A sample application demonstrating the usage of the EyeOne class

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

public class EyeOneDemo {
	public static void main(String[] args) {
		try {
			EyeOne i1 = EyeOne.getInstance();
			if (!i1.isConnected()) {
				System.out
						.println("Device not connected. Please connect device before trying again");
				return;
			}
			System.out.println("Device is connected...");
			System.out.println("SKD_Version: " + i1.getOption("Version"));
			System.out.println("Device serial number: "
					+ i1.getOption("SerialNumber"));
			System.out.print("calibrating...");
			i1.calibrate();
			System.out.println("done");
			System.out.print("measure...");
			i1.triggerMeasurement();
			System.out.println("done");
			float[] spectrum = i1.getSpectrum(EyeOne.SPOT_INDEX);
			printSpectrum(spectrum);
			i1.setOption("Colorimetric.WhiteBase", "Absolute");
			float[] substrate = getSubstrate();
			i1.setSubstrate(substrate);
			float[] densities = i1.getDensities(EyeOne.SPOT_INDEX);
			printDensities(densities);
			float autoDensity = i1.getDensity(EyeOne.SPOT_INDEX);
			printDensity(autoDensity);
		} catch (EyeOneException e) {
			e.printStackTrace();
			System.out.println(e);
		}
		System.out.println("\nDemo finished");
	}

	public static void printSpectrum(float[] spectrum) {
		System.out.println("\nSpectrum:");
		for (int i = 1; i < spectrum.length; i++) {
			System.out.print(spectrum[i - 1] + ((i % 6) == 0 ? "\n" : " "));
		}
		System.out.println();
	}

	public static void printDensities(float[] densities) {
		System.out.println("\nDensities:");
		String[] label = { "Cyan", "Magenta", "Yellow", "Black" };
		for (int i = 0; i < densities.length - 1; i++) {
			System.out.print(label[i] + ": " + densities[i] + "  ");
		}
		int autoDensityIndex = (int) densities[densities.length - 1];
		System.out.println("\nauto density: index = " + autoDensityIndex
				+ " name = " + label[autoDensityIndex]);
	}

	public static void printDensity(float density) {
		System.out.println("\nAutoDensity:");
		String autoDensityFilterName = EyeOne.getInstance().getOption(
				"Colorimetric.DensityFilterMode");
		System.out.println(autoDensityFilterName + ": " + density);
	}

	public static float[] getSubstrate() {
		float[] substrate = new float[36];
		for (int i = 0; i < substrate.length; i++) {
			substrate[i] = 0.5f;
		}
		return substrate;
	}
}
