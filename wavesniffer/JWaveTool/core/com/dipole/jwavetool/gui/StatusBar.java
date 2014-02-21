package com.dipole.jwavetool.gui;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.dipole.jwavetool.common.Log;

import com.dipole.jwavetool.events.ModuleStatusListener;

public class StatusBar extends JPanel
						implements ModuleStatusListener {
	private JLabel statuslabel, readerLabel;
	
	/**
	 * 
	 */
	public StatusBar() {
		super();
		
		this.setBorder(BorderFactory.createLoweredBevelBorder());
		this.statuslabel = new JLabel();
		this.readerLabel = new JLabel();
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setBorder(BorderFactory.createRaisedBevelBorder());
		
		this.add(this.statuslabel);
		this.add(separator);
		this.add(this.readerLabel);
		this.add(new JSeparator());
	}
	
	/**
	 * 
	 * @param message
	 */
	public void setStatusText(final String message){
		this.statuslabel.setText(message);
	}
	
	/**
	 * 
	 * @param message
	 */
	public void setReaderText(final String message){
		this.readerLabel.setText(message);
	}

	/**
	 * 
	 */
	@Override
	public void moduleStatus(final String source, final String message) {
		this.setStatusText(source +" : "+ message);
		Log.info(source +" : "+ message);
		
	}
}
