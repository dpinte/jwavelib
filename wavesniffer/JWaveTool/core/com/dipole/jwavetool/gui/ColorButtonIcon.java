package com.dipole.jwavetool.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class ColorButtonIcon implements Icon {
	private Color color;
	private int iconHeight, iconWidth;
	
	public ColorButtonIcon(final Color color) {
		this(color, 16, 16);
	}
	
	public ColorButtonIcon(final Color color, final int height, final int width){
		this.color = color;
		this.iconHeight = height;
		this.iconWidth = width;
	}
	
	public Color getColor() {
		return this.color;
	}

	public void setColor(final Color color) {
		this.color = color;
	}

	public int getIconHeight() {
		return this.iconHeight;
	}

	public int getIconWidth() {
		return this.iconWidth;
	}

	public void setIconHeight(final int iconHeight) {
		this.iconHeight = iconHeight;
	}

	public void setIconWidth(final int iconWidth) {
		this.iconWidth = iconWidth;
	}
	
	@Override
	public void paintIcon(final Component c, final Graphics g,
							final int x, final int y) {
		g.setColor(this.color);
		g.fill3DRect(x, y, this.iconHeight, this.iconWidth, true);
	}

}
