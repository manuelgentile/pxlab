package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.awtx.*;
import de.pxlab.pxl.DisplayPanel;
import de.pxlab.pxl.Version;
import de.pxlab.pxl.display.PXLabLogo;

/**
 * This is a modal dialog which is called when the 'About ...' menu entry has
 * been selected.
 * 
 * @author H. Irtel
 * @version 0.1.2
 */
/*
 * 
 * 06/08/00
 */
public class AboutDialog extends Dialog implements ActionListener {
	public AboutDialog() {
		super(new Frame(), "About PXLab", true);
		// setLayout(new GridLayout(1,2,20,20));
		setLayout(new BorderLayout(20, 0));
		setResizable(false);
		Panel textPanel = new Panel(new ColumnLayout(Orientation.LEFT,
				Orientation.CENTER));
		textPanel.add(new Label(""));
		textPanel.add(new Label("Psychological Experiments"));
		textPanel.add(new Label("Laboratory"));
		textPanel.add(new Label("Version " + Version.instance()));
		textPanel.add(new Label("Hans Irtel (" + Version.year() + ")"));
		textPanel.add(new Label(""));
		textPanel.add(new Label("Programming:"));
		textPanel.add(new Label("Hans Irtel"));
		textPanel.add(new Label("Ellen Reitmayr"));
		textPanel.add(new Label("Patrick Fischer"));
		textPanel.add(new Label("Markus Hodapp"));
		textPanel.add(new Label(""));
		Button cls = new Button(" Close ");
		cls.addActionListener(this);
		textPanel.add(cls);
		textPanel.add(new Label(""));
		DisplayPanel dspPanel = new DisplayPanel();
		dspPanel.setPreferredSize(new Dimension(300, 360));
		add(dspPanel, BorderLayout.CENTER);
		add(textPanel, BorderLayout.EAST);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				setVisible(false);
				dispose();
			}
		});
		pack();
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((ss.width - getSize().width) / 2,
				(ss.height - getSize().height) / 2);
		PXLabLogo vd = new PXLabLogo();
		vd.createInstance();
		// vd.recompute(dspPanel);
		dspPanel.setDisplay(vd);
		// dspPanel.repaint();
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		setVisible(false);
		dispose();
	}
}
