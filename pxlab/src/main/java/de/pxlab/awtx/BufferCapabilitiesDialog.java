package de.pxlab.awtx;

import java.awt.*;
import java.awt.image.*;

public class BufferCapabilitiesDialog extends CloseButtonDialog {
	private Frame parent;

	public BufferCapabilitiesDialog(Frame f, BufferStrategy bufferStrategy) {
		super(f, "Buffer Capabilities");
		parent = f;
		BufferCapabilities caps = bufferStrategy.getCapabilities();
		Panel infoPanel = new Panel(new GridLayout(0, 2, 0, 0));
		add(infoPanel, BorderLayout.CENTER);
		infoPanel.add(new Label("Flip contents:", Label.RIGHT));
		infoPanel.add(new Label(caps.getFlipContents().toString()));
		infoPanel.add(new Label("Full Screen Rquired:", Label.RIGHT));
		infoPanel.add(new Label(caps.isFullScreenRequired() ? "yes" : "no"));
		infoPanel.add(new Label("Multibuffer available:", Label.RIGHT));
		infoPanel.add(new Label(caps.isMultiBufferAvailable() ? "yes" : "no"));
		infoPanel.add(new Label("Page flipping:", Label.RIGHT));
		infoPanel.add(new Label(caps.isPageFlipping() ? "yes" : "no"));
		pack();
		setVisible(true);
	}
}
