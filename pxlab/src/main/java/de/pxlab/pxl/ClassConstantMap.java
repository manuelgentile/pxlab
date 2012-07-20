package de.pxlab.pxl;

import java.util.HashMap;

/**
 * A map containing all known PXLab class constants which are used as parameter
 * values. The map contains both the full access key and the short access key
 * which is the field name of the constant only.
 * 
 * @author H. Irtel
 * @version 0.1.2
 */
/*
 * 
 * Latest fix:
 * 
 * 2005/09/23
 * 
 * 2006/03/20
 * 
 * 2008/01/30
 */
public class ClassConstantMap extends HashMap {
	public ClassConstantMap() {
		super(1000);
		addClass("de.pxlab.pxl.ExParTypeCodes");
		addClass("de.pxlab.pxl.AdaptiveProcedureCodes");
		addClass("de.pxlab.pxl.AdaptiveResultCodes");
		addClass("de.pxlab.pxl.AdaptiveStopCodes");
		addClass("de.pxlab.pxl.AlignmentCodes");
		addClass("de.pxlab.pxl.CalibrationChannelBitCodes");
		addClass("de.pxlab.pxl.ColorAdjustBitCodes");
		addClass("de.pxlab.pxl.ColorDeviceTransformCodes");
		addClass("de.pxlab.pxl.ColorMeasurementCodes");
		addClass("de.pxlab.pxl.ColorSpaceCodes");
		addClass("de.pxlab.pxl.DeviceCodes");
		addClass("de.pxlab.pxl.DeviceControlCodes");
		addClass("de.pxlab.pxl.DitheringCodes");
		addClass("de.pxlab.pxl.EvaluationCodes");
		addClass("de.pxlab.pxl.ExportCodes");
		addClass("de.pxlab.pxl.ExternalSignalCodes");
		addClass("de.pxlab.pxl.FixationCodes");
		addClass("de.pxlab.pxl.FontStyleCodes");
		addClass("de.pxlab.pxl.IlluminationDeviceControlCodes");
		addClass("de.pxlab.pxl.IndexArrayRetrievalCodes");
		addClass("de.pxlab.pxl.KeyCodes");
		addClass("de.pxlab.pxl.LightDistributionCodes");
		addClass("de.pxlab.pxl.LightMixtureCodes");
		addClass("de.pxlab.pxl.MinimizationFunctionCodes");
		addClass("de.pxlab.pxl.MultipoleCodes");
		addClass("de.pxlab.pxl.OverlayCodes");
		addClass("de.pxlab.pxl.PositionReferenceCodes");
		addClass("de.pxlab.pxl.PsychometricFunctionCodes");
		addClass("de.pxlab.pxl.RandomGeneratorCodes");
		addClass("de.pxlab.pxl.RecorderCodes");
		addClass("de.pxlab.pxl.RatingScaleTickCodes");
		addClass("de.pxlab.pxl.RatingScaleLabelCodes");
		addClass("de.pxlab.pxl.RatingScalePointerCodes");
		addClass("de.pxlab.pxl.ResponseCodes");
		addClass("de.pxlab.pxl.SAMScaleCodes");
		addClass("de.pxlab.pxl.ScreenSelectionCodes");
		addClass("de.pxlab.pxl.SearchPatternCodes");
		addClass("de.pxlab.pxl.SelectionTypeCodes");
		addClass("de.pxlab.pxl.SoundChannelsCodes");
		addClass("de.pxlab.pxl.SoundEnvelopeCodes");
		addClass("de.pxlab.pxl.SoundWaveCodes");
		addClass("de.pxlab.pxl.SpatialAttentionTargetCodes");
		addClass("de.pxlab.pxl.StatCodes");
		addClass("de.pxlab.pxl.StateCodes");
		addClass("de.pxlab.pxl.StateControlCodes");
		addClass("de.pxlab.pxl.StripePatternCodes");
		addClass("de.pxlab.pxl.TimerBitCodes");
		addClass("de.pxlab.pxl.TimerCodes");
		addClass("de.pxlab.pxl.TwoColorProjectionCodes");
	}

	private void addClass(String cn) {
		Object obj = null;
		try {
			Class cls = Class.forName(cn);
			java.lang.reflect.Field[] flds = cls.getDeclaredFields();
			for (int i = 0; i < flds.length; i++) {
				String n = flds[i].getName();
				try {
					obj = flds[i].get(null);
					put(cn + "." + n, obj);
					if (put(n, obj) != null) {
						System.out
								.println("ClassConstantMap.addClass(): Duplicate entry for "
										+ cn + "." + n);
					} else {
						// System.out.println("ClassConstantMap.addClass(): " +
						// cn + "." + n);
					}
				} catch (IllegalAccessException iae) {
					// "Illegal access to " + n;
				}
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("ClassConstantMap.addClass(): Class " + cn
					+ " not found.");
		}
	}

	/**
	 * Return the ExParValue of the class constant whose name is given as an
	 * argument.
	 * 
	 * @return an ExParValue. If the map contains the key then the respective
	 *         value is returned. If the map does not contain the key then null
	 *         is retuned.
	 */
	public ExParValue getValue(String key) {
		ExParValue x = null;
		Object obj = get(key);
		if (obj != null) {
			if (obj instanceof Integer) {
				x = new ExParValue(((Integer) obj).intValue());
			} else if (obj instanceof Double) {
				x = new ExParValue(((Double) obj).doubleValue());
			} else if (obj instanceof String) {
				x = new ExParValue(((String) obj));
			} else {
			}
		} else {
			new ParameterValueError("Unknown class constant: " + key
					+ " Using value 0.");
			x = new ExParValue(0);
		}
		return x;
	}
}
