package de.pxlab.awtx;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.color.*;

public class GraphicsEnvironmentDialog extends CloseButtonDialog {
	private GraphicsEnvironment graphicsEnvironment;
	private GraphicsDevice[] screenDevices;
	private GraphicsDevice defaultScreenDevice;
	private String[] fonts;
	private Toolkit toolkit;
	private ColorModel colorModel;
	private ColorSpace colorSpace;
	private int screenResolution;
	private Dimension screenSize;
	private SingleButton fontButton;
	private SingleButton screenDeviceButton;
	private Frame parent;
	private boolean print = false;

	public GraphicsEnvironmentDialog(Frame f) {
		super(f, "Graphics Environment");
		parent = f;
		toolkitProperties();
		graphicsEnvironmentProperties();
		Panel infoPanel = new Panel(new GridLayout(0, 2, 0, 0));
		add(infoPanel, BorderLayout.CENTER);
		infoPanel.add(new Label("Toolkit:", Label.RIGHT));
		infoPanel.add(new Label(toolkit.toString(), Label.LEFT));
		infoPanel.add(new Label("Color Model:", Label.RIGHT));
		infoPanel.add(new Label(colorModel.getNumComponents() + " channels, "
				+ +colorModel.getPixelSize() + " bpp", Label.LEFT));
		infoPanel.add(new Label("Color Space:", Label.RIGHT));
		infoPanel.add(new Label("Type " + colorSpace.getType(), Label.LEFT));
		infoPanel.add(new Label("Screen Resolution:", Label.RIGHT));
		infoPanel.add(new Label(screenResolution + " dpi", Label.LEFT));
		infoPanel.add(new Label("Screen Size:", Label.RIGHT));
		infoPanel.add(new Label(screenSize.width + " x " + screenSize.height,
				Label.LEFT));
		infoPanel.add(new Label("Graphics Environment:", Label.RIGHT));
		infoPanel.add(new Label(graphicsEnvironment.toString(), Label.LEFT));
		infoPanel.add(new Label("Default Screen Device:", Label.RIGHT));
		infoPanel.add(new Label(defaultScreenDevice.toString(), Label.LEFT));
		EnvButtonHandler ebh = new EnvButtonHandler();
		infoPanel.add(new Label("Available Screen Devices:", Label.RIGHT));
		screenDeviceButton = new SingleButton("Show", ebh);
		infoPanel.add(screenDeviceButton);
		infoPanel.add(new Label("Available Fonts:", Label.RIGHT));
		fontButton = new SingleButton("Show", ebh);
		infoPanel.add(fontButton);
		pack();
		setVisible(true);
	}
	private class SingleButton extends Panel {
		public Button b;

		public SingleButton(String lb, ActionListener a) {
			super(new FlowLayout(FlowLayout.LEFT));
			b = new Button(lb);
			b.addActionListener(a);
			this.add(b);
		}
	}

	private void toolkitProperties() {
		toolkit = Toolkit.getDefaultToolkit();
		colorModel = toolkit.getColorModel();
		colorSpace = colorModel.getColorSpace();
		screenResolution = toolkit.getScreenResolution();
		screenSize = toolkit.getScreenSize();
		if (!print)
			return;
		System.out.println("Toolkit: " + toolkit);
		System.out.println(colorModel);
		System.out.println("Screen Resolution: " + screenResolution + "dpi");
		System.out.println("Screen Size: " + screenSize);
	}

	private void graphicsEnvironmentProperties() {
		graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		screenDevices = graphicsEnvironment.getScreenDevices();
		defaultScreenDevice = graphicsEnvironment.getDefaultScreenDevice();
		fonts = graphicsEnvironment.getAvailableFontFamilyNames();
		if (!print)
			return;
		System.out.println("Graphics Environment: " + graphicsEnvironment);
		System.out.println("Screen Devices:");
		for (int i = 0; i < screenDevices.length; i++) {
			System.out.println("   " + screenDevices[i]);
		}
		for (int j = 0; j < screenDevices.length; j++) {
			GraphicsDevice gd = screenDevices[j];
			System.out.println("Configurations for Screen Device " + j + ":");
			GraphicsConfiguration[] gc = gd.getConfigurations();
			for (int i = 0; i < gc.length; i++) {
				System.out.println("   " + gc[i]);
			}
		}
		System.out.println("Default Screen Device: " + defaultScreenDevice);
		System.out.println("Fonts:");
		for (int i = 0; i < fonts.length; i++) {
			System.out.println("   " + fonts[i]);
		}
	}
	private class EnvButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Button b = (Button) e.getSource();
			if (b == fontButton.b) {
				// fontDialog(GraphicsEnvironmentDialog.this).show();
			} else if (b == screenDeviceButton.b) {
				screenDeviceDialog(parent).show();
			}
		}
	}
	Button[] screenDevButton;
	private class ScreenDevButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Button b = (Button) e.getSource();
			for (int i = 0; i < screenDevButton.length; i++) {
				if (b == screenDevButton[i]) {
					// screenDevConfigDialog(parent, screenDevices[i]).show();
					screenDevModesDialog(parent, screenDevices[i]).show();
					break;
				}
			}
		}
	}

	private Dialog screenDeviceDialog(Frame p) {
		Dialog sd = new CloseButtonDialog(p, "Available Screen Devices");
		Panel infoPanel = new Panel(new GridLayout(0, 2, 0, 0));
		sd.add(infoPanel, BorderLayout.CENTER);
		screenDevButton = new Button[screenDevices.length];
		ScreenDevButtonHandler sdbh = new ScreenDevButtonHandler();
		for (int i = 0; i < screenDevices.length; i++) {
			GraphicsDevice gd = screenDevices[i];
			infoPanel.add(new Label(gd.toString(), Label.RIGHT));
			// SingleButton sb = new SingleButton("Show Configurations", sdbh);
			SingleButton sb = new SingleButton("Show Display Modes", sdbh);
			screenDevButton[i] = sb.b;
			infoPanel.add(sb);
		}
		sd.pack();
		return (sd);
	}

	/*
	 * Button[] screenDevConfigButton;
	 * 
	 * private class ScreenDevConfigButtonHandler implements ActionListener {
	 * public void actionPerformed(ActionEvent e) { Button b =
	 * (Button)e.getSource(); for (int i = 0; i < screenDevConfigButton.length;
	 * i++) { if (b == screenDevConfigButton[i]) {
	 * System.out.println("Show config " + i); break; } } } }
	 */
	private Dialog screenDevConfigDialog(Frame p, GraphicsDevice gd) {
		Dialog sd = new CloseButtonDialog(p, "Configurations of Screen Device "
				+ gd.toString());
		Panel infoPanel = new Panel(new GridLayout(0, 1, 0, 0));
		sd.add(infoPanel, BorderLayout.CENTER);
		GraphicsConfiguration[] gc = gd.getConfigurations();
		for (int i = 0; i < gc.length; i++) {
			GraphicsConfiguration grc = gc[i];
			StringBuffer ds = new StringBuffer(grc.toString() + ": ");
			Rectangle r = grc.getBounds();
			ds.append(r.width + " x " + r.height + ", ");
			ColorModel cm = grc.getColorModel();
			ds.append(cm.getNumComponents() + " color channels, "
					+ +cm.getPixelSize() + " bpp");
			infoPanel.add(new Label(ds.toString(), Label.LEFT));
		}
		sd.pack();
		return (sd);
	}

	private Dialog screenDevModesDialog(Frame p, GraphicsDevice gd) {
		Dialog sd = new CloseButtonDialog(p, "Display Modes of Screen Device "
				+ gd.toString());
		Panel infoPanel = new Panel(new GridLayout(0, 1, 0, 0));
		sd.add(infoPanel, BorderLayout.CENTER);
		DisplayMode[] gc = gd.getDisplayModes();
		for (int i = 0; i < gc.length; i++) {
			if ((gc[i].getBitDepth() >= 24) && (gc[i].getRefreshRate() >= 75)) {
				DisplayMode grc = gc[i];
				StringBuffer ds = new StringBuffer(grc.toString() + ": ");
				ds.append(grc.getWidth() + "x" + grc.getHeight()
						+ " pixels at " + grc.getRefreshRate() + " Hz with "
						+ grc.getBitDepth() + " bpp");
				infoPanel.add(new Label(ds.toString(), Label.LEFT));
			}
		}
		sd.pack();
		return (sd);
	}
}
