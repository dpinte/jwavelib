package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class ColorButtonIcon implements Icon {
	private Color color;
	private int height, width;
	
	public ColorButtonIcon(Color color) {
		this(color, 16, 16);
	}
	
	public ColorButtonIcon(Color color, int height, int width){
		this.color = color;
		this.height = height;
		this.width = width;
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getIconHeight() {
		return this.height;
	}

	public int getIconWidth() {
		return this.width;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setColor(this.color);
		g.fill3DRect(x, y, this.height, this.width, true);
	}

}
