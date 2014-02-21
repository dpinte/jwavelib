package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import com.coronis.CoronisLib;

public class AboutDialog extends JDialog implements ActionListener{
	
	public static final String VERSION = "0.2";
	public static final String NAME ="WavSniffier Analyser";
	
	private JPanel butPan;
	private JTextPane textPanel;
	private JButton validBut;
	
	public AboutDialog(final Component parent) {
		this.setTitle("About WaveSniffer Analyser");
		this.setModal(true);
		this.setSize(300, 150);
		
		this.setLocationRelativeTo(parent);
		
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		this.textPanel = new JTextPane();
		this.setText();
		this.textPanel.setEditable(false);
		
		this.validBut = new JButton("OK");
		this.butPan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		this.butPan.add(this.validBut);
		
		this.add(this.textPanel, BorderLayout.CENTER);
		this.add(this.butPan, BorderLayout.SOUTH);
		
		this.validBut.addActionListener(this);
		this.setVisible(true);
	}

	private void setText(){
		//this.textPanel.setText("WaveSnifferAnalyser v0.1 with JWaveLib "+ CoronisLib.version);
		this.textPanel.setText(NAME +": "+ VERSION +"\n"+
								"JWaveLib: "+ CoronisLib.version +"\n"+
								"Icon theme: Tango (http://tango.freedesktop.org/Tango_Icon_Gallery)");
	}
	
	public void actionPerformed(final ActionEvent event){
		if(event.getSource() == this.validBut){
			this.dispose();
		}
	}
}
