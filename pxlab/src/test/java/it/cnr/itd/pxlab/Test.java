package it.cnr.itd.pxlab;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JLabel;

public class Test extends Frame {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Test t = new Test();
		t.setSize(400, 400);
		t.setVisible(true);
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		double x = 15, y = 50, w = 70, h = 70;
		Ellipse2D e = new Ellipse2D.Double(x, y, w, h);
		g2.setStroke(new BasicStroke(8));
		// Stroke and paint.
		Color smokey = new Color(128, 128, 128, 128);
		g2.setPaint(smokey);
		g2.fill(e);
		g2.draw(e);
		// Stroke, then paint.
		e.setFrame(x + 100, y, w, h);
		g2.setPaint(Color.black);
		g2.draw(e);
		g2.setPaint(Color.gray);
		g2.fill(e);
		// Paint, then stroke.
		e.setFrame(x + 200, y, w, h);
		g2.setPaint(Color.gray);
		g2.fill(e);
		g2.setPaint(Color.black);
		g2.draw(e);
		String html = "<p style=\"margin-top: 1em\"> <font color=\"#0000ff\" size=\"6\" face=\"sans-serif\"> Example Heading </font> </p>";
		
		
	}
}
