package gui;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import common.Log;

import events.ModuleStatusListener;

public class StatusBar extends JPanel
						implements ModuleStatusListener {
	private JLabel statuslabel, readerLabel;
	
	public StatusBar() {
		
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
		
	public void setStatusText(String message){
		this.statuslabel.setText(message);
	}
	
	public void setReaderText(String message){
		this.readerLabel.setText(message);
	}

	
	@Override
	public void moduleStatus(String source, String message) {
		this.setStatusText(source +" : "+ message);
		Log.info(source +" : "+ message);
		
	}
}
