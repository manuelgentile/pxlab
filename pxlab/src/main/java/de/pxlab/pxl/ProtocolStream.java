package de.pxlab.pxl;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import de.pxlab.awtx.*;
import de.pxlab.pxl.gui.*;

/**
 * A protocol stream which behaves like System.out but can show its content in a
 * TextArea component.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class ProtocolStream extends OutputStream {
	private boolean silent = false;

	// private ProtocolFrame frame;
	// private TextArea textArea;
	public ProtocolStream() {
		super();
		/*
		 * frame = new ProtocolFrame(); textArea = new
		 * TextArea("PXLab Protocol\n", 100, 80); textArea.setEditable(true);
		 * frame.add(textArea); frame.setState(Frame.ICONIFIED);
		 * frame.setVisible(true);
		 */
	}

	public void setSilent(boolean s) {
		silent = s;
	}

	public void write(int b) throws IOException {
		if (!silent)
			System.out.write(b);
		// textArea.append(String.valueOf((char)b));
	}
	/**
	 * The ProtocolFrame class describes frames which can run applications.
	 * These frames have a menu bar with menu entries being extendable by the
	 * respective application.
	 */
	/*
	 * class ProtocolFrame extends PXLabApplicationFrame implements
	 * ActionListener {
	 * 
	 * public ProtocolFrame() { super(" PXLab Protocol"); configureMenuBar(); }
	 * 
	 * protected boolean exitApplication() { return false; }
	 * 
	 * protected Menu[] getApplicationMenus() { return null; }
	 * 
	 * protected MenuItem[] getOptionMenuItems() { return null; }
	 * 
	 * protected MenuItem[] getHelpMenuItems() { return null; }
	 * 
	 * 
	 * MenuItem saveItem, saveSelectionItem;
	 * 
	 * 
	 * protected MenuItem[] getFileMenuItems() {
	 * 
	 * MenuItem[] m = new MenuItem[2];
	 * 
	 * MenuItem saveItem = new MenuItem("Save Protocol");
	 * saveItem.addActionListener(this); m[0] = saveItem;
	 * 
	 * MenuItem saveSelectionItem = new MenuItem("Save Selected Protocol");
	 * saveSelectionItem.addActionListener(this); m[0] = saveSelectionItem;
	 * 
	 * return m; }
	 * 
	 * 
	 * public void actionPerformed(ActionEvent e) { MenuItem mi =
	 * (MenuItem)e.getSource(); if (mi == saveItem) { String t =
	 * textArea.getText(); saveText(t); } else if (mi == saveSelectionItem) {
	 * String t = textArea.getSelectedText(); saveText(t); } }
	 * 
	 * 
	 * private void saveText(String t) { SaveFileDialog fd = new
	 * SaveFileDialog(" Save Protocol", "protocol.txt", "txt"); String fp =
	 * fd.getPath(); if (fp != null) { try { FileWriter fw = new FileWriter(fp);
	 * fw.write(t); fw.close(); } catch (IOException iox) { new
	 * FileError("Error while trying to save protocol."); } } fd.dispose(); }
	 * 
	 * }
	 */
}
