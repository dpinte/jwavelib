package gui;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;


public class StatusBar extends JPanel {
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
	
	public StatusBar(String message){
		this();
		this.setStatusText(message);
	}
	
	public void setStatusText(String message){
		this.statuslabel.setText(message);
	}
	
	public void setReaderText(String message){
		this.readerLabel.setText(message);
	}
}
