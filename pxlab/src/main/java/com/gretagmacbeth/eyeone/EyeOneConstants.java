package com.gretagmacbeth.eyeone;

public interface EyeOneConstants {
	public static final long SPOT_INDEX = 0;
	public static final int SPECTRUM_SIZE = 36;
	public static final int TRISTIMULUS_SIZE = 3;
	public static final int DENSITY_SIZE = 4;
	public static final String UNDEFINED = "Undefined";
	/* Illumination */
	public static final String ILLUMINATION_KEY = "Colorimetric.Illumination";
	public static final String ILLUMINATION_A = "A";
	public static final String ILLUMINATION_B = "B";
	public static final String ILLUMINATION_C = "C";
	public static final String ILLUMINATION_D50 = "D50";
	public static final String ILLUMINATION_D55 = "D55";
	public static final String ILLUMINATION_D65 = "D65";
	public static final String ILLUMINATION_D75 = "D75";
	public static final String ILLUMINATION_F2 = "F2";
	public static final String ILLUMINATION_F7 = "F7";
	public static final String ILLUMINATION_F11 = "F11";
	public static final String ILLUMINATION_EMISSION = "Emission";
	/* Observer */
	public static final String OBSERVER_KEY = "Colorimetric.Observer";
	public static final String OBSERVER_TWO_DEGREE = "TwoDegree";
	public static final String OBSERVER_TEN_DEGREE = "TenDegree";
	/* WhiteBase */
	public static final String WHITE_BASE_KEY = "Colorimetric.WhiteBase";
	public static final String WHITE_BASE_ABSOLUTE = "Absolute";
	public static final String WHITE_BASE_PAPER = "Paper";
	public static final String WHITE_BASE_AUTOMATIC = "Automatic";
	/* DensityStandard */
	public static final String DENSITY_STANDARD_KEY = "Colorimetric.DensityStandard";
	public static final String DENSITY_STANDARD_DIN = "DIN";
	public static final String DENSITY_STANDARD_DINNB = "DINNB";
	public static final String DENSITY_STANDARD_ANSIA = "ANSIA";
	public static final String DENSITY_STANDARD_ANSIE = "ANSIE";
	public static final String DENSITY_STANDARD_ANSII = "ANSII";
	public static final String DENSITY_STANDARD_ANSIT = "ANSIT";
	public static final String DENSITY_STANDARD_SPI = "SPI";
	/* DensityFilterMode */
	public static final String DENSITY_FILTER_MODE_KEY = "Colorimetric.DensityFilterMode";
	public static final String DENSITY_FILTER_MODE_BLACK = "Black";
	public static final String DENSITY_FILTER_MODE_CYAN = "Cyan";
	public static final String DENSITY_FILTER_MODE_MAGENTA = "Magenta";
	public static final String DENSITY_FILTER_MODE_YELLOW = "Yellow";
	public static final String DENSITY_FILTER_MODE_MAX = "Max";
	public static final String DENSITY_FILTER_MODE_AUTO = "Auto";
	/* Maximum / minimum wavelength */
	public static final String WAVE_LENGTH_730 = "730nm";
	public static final String WAVE_LENGTH_380 = "380nm";
	/* ColorSpace */
	public static final String COLOR_SPACE_KEY = "ColorSpaceDescription.Type";
	public static final String COLOR_SPACE_CIELab = "CIELab";
	public static final String COLOR_SPACE_CIELCh = "CIELCh";
	public static final String COLOR_SPACE_CIELuv = "CIELuv";
	public static final String COLOR_SPACE_CIELChuv = "CIELChuv";
	public static final String COLOR_SPACE_CIE_UV_Y1960 = "CIEuvY1960";
	public static final String COLOR_SPACE_CIE_UV_Y1976 = "CIEuvY1976";
	public static final String COLOR_SPACE_CIEXYZ = "CIEXYZ";
	public static final String COLOR_SPACE_CIExyY = "CIExyY";
	public static final String COLOR_SPACE_HunterLab = "HunterLab";
	public static final String COLOR_SPACE_RXRYRZ = "RxRyRz";
	public static final String COLOR_SPACE_LAB_MG = "LABmg";
	public static final String COLOR_SPACE_LCH_MG = "LCHmg";
	public static final String COLOR_SPACE_RGB = "RGB";
	public static final String I1_VERSION = "Version";
	public static final String I1_SERIAL_NUMBER = "SerialNumber";
	public static final String I1_IS_CONNECTED = "Connection";
	public static final String I1_IS_KEY_PRESSED = "IsKeyPressed";
	public static final String I1_IS_RECOGNITION_ENABLED = "Recognition";
	public static final String I1_LAST_CALIBRATION_TIME = "LastCalibrationTime";
	public static final String I1_CALIBRATION_COUNT = "LastCalibrationCounter";
	public static final String I1_LAST_ERROR = "LastError";
	public static final String I1_EXTENDED_ERROR_INFORMATION = "ExtendedErrorInformation";
	public static final String I1_NUMBER_OF_AVAILABLE_SAMPLES = "AvailableSamples";
	public static final String I1_AVAILABLE_MEASUREMENT_MODES = "AvailableMeasurementModes";
	public static final String I1_IS_BEEP_ENABLED = "Beep";
	public static final String I1_LAST_AUTO_DENSITY_FILTER = "LastAutoDensityFilter";
	public static final String I1_IS_ADAPTIVE_MODE_ENABLED = "AdaptiveMode";
	public static final String I1_PHYSICAL_FILTER = "PhysicalFilter";
	public static final String I1_UNDEFINED_FILTER = "0";
	public static final String I1_NO_FILTER = "1";
	public static final String I1_UV_FILTER = "2";
	public static final String I1_SCREEN_TYPE = "ScreenType";
	public static final String I1_LCD_SCREEN = "LCD";
	public static final String I1_CRT_SCREEN = "CRT";
	public static final String I1_PATCH_INTENSITY = "PatchIntensity";
	public static final String I1_BLEAK = "Bleak";
	public static final String I1_BRIGHT = "Bright";
	public static final String I1_AUTO = "Auto";
	public static final String I1_YES = "yes";
	public static final String I1_NO = "no";
	public static final String I1_DEVICE_TYPE = "DeviceType";
	public static final String I1_EYEONE = "EyeOne";
	public static final String I1_DISPLAY = "EyeOneDisplay";
	public static final String I1_MEASUREMENT_MODE = "MeasurementMode";
	public static final String I1_SINGLE_EMISSION = "SingleEmission";
	public static final String I1_SINGLE_REFLECTANCE = "SingleReflectance";
	public static final String I1_SINGLE_AMBIENT_LIGHT = "SingleAmbientLight";
	public static final String I1_SCANNING_REFLECTANCE = "ScanningReflectance";
	public static final String I1_SCANNING_AMBIENT_LIGHT = "ScanningAmbientLight";
	public static final String I1_RESET = "Reset";
	public static final String I1_ALL = "All";
}
