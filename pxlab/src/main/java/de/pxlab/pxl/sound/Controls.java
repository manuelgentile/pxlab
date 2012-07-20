package de.pxlab.pxl.sound;

import java.io.*;
import java.nio.*;
import java.util.*;
import javax.sound.sampled.*;

import de.pxlab.pxl.Debug;

/**
 * Some static support methods.
 */
public class Controls {
	/**
	 * Set the volume of the given line. In most cases the line must be open to
	 * set the volume.
	 * 
	 * @param line
	 *            the Line object whose volume should be set.
	 * @param volume
	 *            the volume to set. Values have to satisfy 0 <= volume <= 100.
	 */
	public static void setVolume(Line line, double volume) {
		if (volume > 100.0)
			volume = 100.0;
		if (volume >= 0.0) {
			FloatControl ctrl = null;
			try {
				ctrl = (FloatControl) (line
						.getControl(FloatControl.Type.MASTER_GAIN));
			} catch (IllegalArgumentException iax1) {
				try {
					ctrl = (FloatControl) (line
							.getControl(FloatControl.Type.VOLUME));
				} catch (IllegalArgumentException iax2) {
					System.out.println("Controls.setVolume() not supported.");
					return;
				}
			}
			float current = ctrl.getValue();
			float minimum = ctrl.getMinimum();
			float maximum = ctrl.getMaximum();
			float newValue = (float) (minimum + volume * (maximum - minimum)
					/ 100.0F);
			ctrl.setValue(newValue);
			System.out.println("Controls.setVolume()");
			System.out.println(" current value = " + current);
			System.out.println("     new value = " + newValue);
		}
	}
}
