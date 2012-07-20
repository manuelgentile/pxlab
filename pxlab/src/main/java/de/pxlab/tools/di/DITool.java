package de.pxlab.tools.di;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

import de.hardcode.jxinput.directinput.*;

import de.pxlab.awtx.CloseableFrame;
import de.pxlab.awtx.InsetPanel;
import de.pxlab.util.StringExt;
import de.pxlab.util.CommandLineOptionHandler;
import de.pxlab.util.CommandLineParser;
import de.pxlab.pxl.DIDevice;
import de.pxlab.pxl.DIAxisTransform;
import de.pxlab.pxl.HiresClock;
import de.pxlab.pxl.PXLabIcon;

/**
 * Watch DirectInput devices. Based on the <a
 * href="http://www.hardcode.de/jxinput">JXInput</a> DirectInput bindings by
 * Joerg Plewe.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2007/02/14
 */
public class DITool extends CloseableFrame implements CommandLineOptionHandler,
		Runnable {
	static NumberFormat axf = NumberFormat.getInstance(Locale.US);
	static {
		axf.setMaximumFractionDigits(4);
		axf.setGroupingUsed(false);
	}
	int na;
	int nb;
	int nd;
	AxisPanel[] axisPanels;
	ButtonPanel[] buttonPanels;
	DirectionalPanel[] directionalPanels;
	DIDevice device;
	private int deviceIndex;
	private boolean useSpaceWareAxisTransform = false;
	private boolean useNoAxisTransform = false;
	private boolean showAxisDeltas = false;

	public DITool(String[] args) {
		super("");
		PXLabIcon.decorate(this);
		CommandLineParser cp = new CommandLineParser(args, options, this);
		int n = DIDevice.getNumberOfDevices();
		if (deviceIndex >= n) {
			System.out.println("Device index " + deviceIndex
					+ " is out of range.");
			System.out.println("Found only " + n + " DirectInput devices.");
			System.exit(3);
		}
		device = new DIDevice(0, deviceIndex, 1.0);
		String name = device.getName();
		setTitle(name);
		if (name.startsWith("Space")) {
			if (!useSpaceWareAxisTransform && !useNoAxisTransform) {
				useSpaceWareAxisTransform = true;
			}
		}
		int id = device.getID();
		na = device.getNumberOfAxes();
		nb = device.getNumberOfButtons();
		nd = device.getNumberOfDirectionals();
		InsetPanel p1 = new InsetPanel(new GridLayout(1, 0, 5, 5));
		p1.add(axisLabelPanel());
		axisPanels = new AxisPanel[na];
		for (int i = 0; i < na; i++) {
			axisPanels[i] = new AxisPanel(device.getAxis(i));
			p1.add(axisPanels[i]);
		}
		InsetPanel p2 = new InsetPanel(new GridLayout(0, 6, 5, 5));
		buttonPanels = new ButtonPanel[nb];
		for (int i = 0; i < nb; i++) {
			buttonPanels[i] = new ButtonPanel(device.getButton(i));
			p2.add(buttonPanels[i]);
		}
		InsetPanel p3 = new InsetPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		directionalPanels = new DirectionalPanel[nd];
		for (int i = 0; i < nd; i++) {
			directionalPanels[i] = new DirectionalPanel(
					device.getDirectional(i));
			p3.add(directionalPanels[i]);
		}
		add(p1, BorderLayout.NORTH);
		add(p2, BorderLayout.CENTER);
		add(p3, BorderLayout.SOUTH);
		pack();
		setVisible(true);
	}

	private void listDevices() {
		int n = DIDevice.getNumberOfDevices();
		if (n > 0) {
			System.out.println("\nFound the following DirectInput devices:");
			for (int i = 0; i < n; i++) {
				device = new DIDevice(0, i, 1.0);
				System.out.println("  [" + i + "]  " + device.getName());
			}
		}
	}

	public void update() {
		device.getDevice().update();
		for (int i = 0; i < na; i++) {
			axisPanels[i].update();
		}
		for (int i = 0; i < nb; i++) {
			buttonPanels[i].update();
		}
		for (int i = 0; i < nd; i++) {
			directionalPanels[i].update();
		}
	}

	private Panel axisLabelPanel() {
		Panel p = new Panel(new GridLayout(0, 1));
		p.add(new Label("", Label.LEFT));
		p.add(new Label("Minimum", Label.LEFT));
		p.add(new Label("Maximum", Label.LEFT));
		p.add(new Label("Last", Label.LEFT));
		p.add(new Label("Current", Label.LEFT));
		p.add(new Label("Delta", Label.LEFT));
		return p;
	}
	private static final int ND = 64;

	private String bin(double x) {
		long a = (long) x;
		char[] b = new char[ND];
		int m = 1;
		for (int i = 0; i < ND; i++) {
			b[i] = ((a & m) != 0) ? '1' : 'o';
			m = m << 1;
		}
		return new String(b);
	}
	private static final long B32 = 4294967295L;
	private static final long B16 = 65535L;

	private int int16(double x) {
		return (int) ((short) ((((long) x) & B16)));
	}

	private int int32(double x) {
		return (int) ((short) ((((long) x) & B32)));
	}
	/** The axis transform for SpaceWare devices. */
	class SpaceWareAxisTransform implements DIAxisTransform {
		public double valueOf(double x) {
			return (double) (int) (((long) x) & B32);
		}
	}
	/** The null axis transform. */
	class DefaultAxisTransform implements DIAxisTransform {
		public double valueOf(double x) {
			return x;
		}
	}
	/** A panel which describes a single axis. */
	class AxisPanel extends Panel {
		de.hardcode.jxinput.Axis axis;
		TextField minValueText;
		TextField maxValueText;
		TextField lastValueText;
		TextField currentValueText;
		TextField deltaValueText;
		double minValue;
		double maxValue;
		double lastValue;
		DIAxisTransform axisTransform;

		public AxisPanel(de.hardcode.jxinput.Axis a) {
			super(new GridLayout(0, 1));
			axis = a;
			axisTransform = useSpaceWareAxisTransform ? new SpaceWareAxisTransform()
					: new DefaultAxisTransform();
			Label n = new Label(a.getName(), Label.LEFT);
			add(n);
			minValueText = new TextField(10);
			add(minValueText);
			maxValueText = new TextField(10);
			add(maxValueText);
			lastValueText = new TextField(10);
			add(lastValueText);
			currentValueText = new TextField(10);
			add(currentValueText);
			deltaValueText = new TextField(10);
			add(deltaValueText);
			maxValue = 0.0;
			lastValue = axisTransform.valueOf(axis.getValue());
		}

		public void update() {
			// double currentValue = axis.getValue();
			// double currentValue = (int)(axis.getValue());
			// double currentValue = int16(axis.getValue());
			double currentValue = axisTransform.valueOf(axis.getValue());
			double deltaValue = currentValue - lastValue;
			if (currentValue < minValue)
				minValue = currentValue;
			if (currentValue > maxValue)
				maxValue = currentValue;
			minValueText.setText(axf.format(minValue));
			maxValueText.setText(axf.format(maxValue));
			lastValueText.setText(axf.format(lastValue));
			currentValueText.setText(axf.format(currentValue));
			deltaValueText.setText(axf.format(deltaValue));
			repaint();
			if (deltaValue > 100000.0 || deltaValue < -100000) {
				System.out.println(axis.getName() + ":");
				System.out.println("  " + lastValue + "  [" + int16(lastValue)
						+ "] [" + int32(lastValue) + "]  " + bin(lastValue));
				System.out.println("  " + currentValue + "  ["
						+ int16(currentValue) + "] [" + int32(currentValue)
						+ "]  " + bin(currentValue));
				System.out.println("  " + deltaValue);
			}
			lastValue = currentValue;
		}
	}
	/** A panel which describes a single button's state. */
	class ButtonPanel extends Checkbox {
		de.hardcode.jxinput.Button button;

		public ButtonPanel(de.hardcode.jxinput.Button b) {
			super(b.getName(), b.getState());
			button = b;
		}

		public void update() {
			setState(button.getState());
			repaint();
		}
	}
	/** A panel which describes a single directional. */
	class DirectionalPanel extends Panel {
		de.hardcode.jxinput.Directional dir;
		TextField valueText;

		public DirectionalPanel(de.hardcode.jxinput.Directional d) {
			super(new GridLayout(0, 1));
			dir = d;
			Label n = new Label(d.getName(), Label.LEFT);
			add(n);
			valueText = new TextField(12);
			add(valueText);
		}

		public void update() {
			valueText.setText(dir.isCentered() ? "" : String.valueOf(dir
					.getDirection()));
			repaint();
		}
	}

	/** Polls the DirectInput device for data. */
	public void run() {
		while (true) {
			update();
			HiresClock.delay(50);
		}
	}
	private String options = "d:srv?";

	/** This method is called for every command line option found. */
	public void commandLineOption(char c, String arg) {
		switch (c) {
		case 'd':
			deviceIndex = StringExt.intValue(arg, 0);
			break;
		case 's':
			useSpaceWareAxisTransform = true;
			break;
		case 'r':
			useNoAxisTransform = true;
			break;
		case 'v':
			showAxisDeltas = true;
			break;
		case '?':
			showUsage();
			System.exit(0);
			break;
		}
	}

	public void showUsage() {
		System.out.println(this.getClass().getName()
				+ ": Watch DirectInput device data");
		System.out.println("Usage: java " + this.getClass().getName()
				+ " [options]");
		System.out.println("Options are:");
		System.out.println("  -d i   select device at index i");
		System.out.println("  -s     force SpaceWare axis transform");
		System.out.println("  -r     do not use any axis transform");
		System.out.println("  -v     show axis deltas");
		System.out.println("  -?     show this help text");
		listDevices();
	}

	public void commandLineError(int e, String s) {
		System.out.println("Command line error: " + s);
		showUsage();
		System.exit(3);
	}

	public static void main(String[] args) {
		new Thread(new DITool(args)).start();
	}
}
