package de.pxlab.pxl.sound;

import java.awt.*;
import java.awt.event.*;
import de.pxlab.awtx.*;

import javax.sound.sampled.*;

public class SoundEnvironmentDialog extends CloseButtonDialog {
	private Frame frame;

	public SoundEnvironmentDialog(Frame f) {
		super(f, "Sound Environment");
		frame = f;
		Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
		Panel infoPanel = new Panel(new GridLayout(0, 1, 0, 0));
		add(infoPanel, BorderLayout.CENTER);
		for (int i = 0; i < mixerInfo.length; i++) {
			// infoPanel.add(new Label("Mixer:", Label.RIGHT));
			infoPanel.add(new MixerButton(this, mixerInfo[i]));
		}
		pack();
		setVisible(true);
	}
	class MixerButton extends Button implements ActionListener {
		private Mixer.Info mixerInfo;
		private Dialog parent;

		public MixerButton(Dialog parent, Mixer.Info mixerInfo) {
			super(mixerInfo.getName());
			this.parent = parent;
			this.mixerInfo = mixerInfo;
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			new MixerDialog(parent, mixerInfo);
		}
	}
	class MixerDialog extends CloseButtonDialog {
		public MixerDialog(Dialog parent, Mixer.Info mixerInfo) {
			super(frame, mixerInfo.getName());
			Dimension s = parent.getSize();
			Point p = parent.getLocation();
			setLocation(p.x + s.width + 20, p.y + 20);
			Mixer mixer = AudioSystem.getMixer(mixerInfo);
			Line.Info[] sourceLineInfo = mixer.getSourceLineInfo();
			Line.Info[] targetLineInfo = mixer.getTargetLineInfo();
			Panel infoPanel = new Panel(new GridLayout(0, 1, 0, 0));
			add(infoPanel, BorderLayout.CENTER);
			for (int i = 0; i < sourceLineInfo.length; i++) {
				infoPanel.add(new LineButton(this, mixer, sourceLineInfo[i]));
			}
			for (int i = 0; i < targetLineInfo.length; i++) {
				infoPanel.add(new LineButton(this, mixer, targetLineInfo[i]));
			}
			pack();
			setVisible(true);
		}
	}
	class LineButton extends Button implements ActionListener {
		private Mixer mixer;
		private Line.Info lineInfo;
		private Dialog parent;

		public LineButton(Dialog parent, Mixer mixer, Line.Info lineInfo) {
			super(lineInfo.toString());
			this.parent = parent;
			this.mixer = mixer;
			this.lineInfo = lineInfo;
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			new LineDialog(parent, mixer, lineInfo);
		}
	}
	class LineDialog extends CloseButtonDialog {
		public LineDialog(Dialog parent, Mixer mixer, Line.Info lineInfo) {
			super(frame, lineInfo.toString());
			Dimension s = parent.getSize();
			Point p = parent.getLocation();
			setLocation(p.x + s.width + 20, p.y + 20);
			Panel infoPanel = new Panel(new GridLayout(0, 1, 0, 0));
			add(infoPanel, BorderLayout.CENTER);
			try {
				Line line = mixer.getLine(lineInfo);
				line.open();
				Control[] ctrl = line.getControls();
				for (int i = 0; i < ctrl.length; i++) {
					infoPanel.add(new Label(ctrl[i].toString()));
				}
				line.close();
			} catch (LineUnavailableException lux) {
				infoPanel.add(new Label("Line unavailable!"));
			}
			pack();
			setVisible(true);
		}
	}
}
