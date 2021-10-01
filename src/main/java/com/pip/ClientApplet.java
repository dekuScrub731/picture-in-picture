package com.pip;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class ClientApplet extends Applet
{

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		double scaleFactor = 2d;
		// Calculate the scaling factor to apply
		// based on the "default" size and the
		// current size...
		g2d.scale(scaleFactor, scaleFactor);
		//super.paint(g2d);
		super.paint(g2d);
		g2d.dispose();
	}
}
