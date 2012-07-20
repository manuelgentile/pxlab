package de.pxlab.pxl.gui;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * This class defines color adjustment panels which are able to fire
 * ColorChangeEvents. Color adjustment panels either consist of 3 sliders, one
 * for each color channel or they contain something like a chromaticity chart
 * and a luminance slider.
 * 
 * @author H. Irtel
 * @version 0.2.4
 * @see ColorChangeListener
 * @see ColorAdjustmentChart
 * @see ColorAdjustmentSliders
 * @see ColorServer
 */
/*
 * 02/06/01 added setEnabled()
 */
abstract public class ColorAdjustmentPanel extends Panel implements
		ColorChangeListener {
	protected static boolean follow = true;
	/** This is the color server where we get our colors from. */
	protected ColorServer colorServer;
	/** The currently active color in XYZ coordinates. */
	protected PxlColor currentColor;
	/** The color space type represented by this panel. */
	protected int csType;
	/** The currently active color in the local color space coordinates. */
	protected double[] localColor;
	protected static int cellWidth = 8;
	protected static int cellHeight = 16;

	/** Create a color adjustment panel with the given title. */
	public ColorAdjustmentPanel(int t, ColorServer ct) {
		csType = t;
		colorServer = ct;
		colorServer.addColorChangeListener(this);
		setBackground(PXLabGUI.background);
	}

	abstract public void setEnabled(boolean e);

	/**
	 * Set the state of this panel such that it represents this color. Note that
	 * this only affects the chart and slider models and does not change the
	 * view. Usually a repaint() message should follow in order for the panel to
	 * correctly reflect the models' states. The setValue() method is the
	 * top-down path to set a color panel's state. This path does not fire
	 * ColorCoordinateChangeEvents in order to avoid circular event firing when
	 * ColorCoordinateChangeListener objects possibly call setValue().
	 */
	abstract public void setValue(PxlColor c);

	/** Set the follow-mode flag. */
	public static void setFollow(boolean a) {
		follow = a;
	}

	/** Get the follow-mode flag. */
	public static boolean getFollow() {
		return (follow);
	}

	/** Return the color space type associated with this panel. */
	public int getCsType() {
		return (csType);
	}

	/** Return a short name of the color space for this panel. */
	public String getName() {
		return (PxlColor.getShortName(csType));
	}

	/** Return a descriptive title of the color space for this panel. */
	public String getTitle() {
		return (PxlColor.getLongName(csType));
	}

	/**
	 * Create an instance of a ColorAdjustmentPanel for the given color Space
	 * using the given ColorServer.
	 */
	public static ColorAdjustmentPanel instanceOf(int csType, ColorServer cs) {
		ColorAdjustmentPanel cap = null;
		switch (csType) {
		case PxlColor.CS_Yxy:
			cap = new YxyAdjustmentChart(cs);
			break;
		case PxlColor.CS_LabStar:
			cap = new LabAdjustmentChart(cs);
			break;
		case PxlColor.CS_LuvStar:
			cap = new LuvAdjustmentChart(cs);
			break;
		case PxlColor.CS_JCh:
			cap = new JChAdjustmentChart(cs);
			break;
		case PxlColor.CS_Yrb:
			cap = new YrbAdjustmentChart(cs);
			break;
		case PxlColor.CS_XYZ:
			cap = new XYZAdjustmentSliders(cs);
			break;
		case PxlColor.CS_LMS:
			cap = new LMSAdjustmentSliders(cs);
			break;
		case PxlColor.CS_RGB:
			cap = new RGBAdjustmentSliders(cs);
			break;
		case PxlColor.CS_Dev:
			cap = new DevRGBAdjustmentSliders(cs);
			break;
		}
		return (cap);
	}

	protected void createLayout(Component lum, Component chrom) {
		this.setLayout(new BorderLayout());
		this.add(createTitle(), BorderLayout.NORTH);
		Panel chartPanel = new Panel(new BorderLayout());
		chartPanel.add(lum, BorderLayout.NORTH);
		chartPanel.add(chrom, BorderLayout.CENTER);
		this.add(chartPanel, BorderLayout.CENTER);
		// Initialize the chart's display
		// colorServer.setCurrentColor(PxlColor.gray);
	}

	protected void createLayout(Component s1, Component s2, Component s3) {
		this.setLayout(new BorderLayout());
		this.add(createTitle(), BorderLayout.NORTH);
		Panel sliderPanel = new Panel(new GridLayout(1, 3));
		sliderPanel.add(s1);
		sliderPanel.add(s2);
		sliderPanel.add(s3);
		this.add(sliderPanel, BorderLayout.CENTER);
		// Initialize the chart's display
		// colorServer.setCurrentColor(PxlColor.gray);
	}

	private Label createTitle() {
		Label t = new Label("  " + getTitle(), Label.LEFT);
		// Font fn = t.getFont();
		// t.setFont(new Font(fn.getName(), Font.BOLD, fn.getSize()));
		return (t);
	}
}
