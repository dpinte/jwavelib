package gui;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gnu.io.CommPortIdentifier;

import common.Settings;

public class SerialSettingsDialog extends JDialog implements ActionListener {
	private JPanel serPanel, butPanel;
	private JComboBox portCombo, baudrateCombo;
	private JButton validBut, cancelBut;
	private ArrayList <String> portList;
	private String[] baudrate = {"9600", "19800", "115200"};
	private Settings settings;
	
	public SerialSettingsDialog(Component parent, Settings settings){
		this.settings = settings;
		
		this.setModal(true);
		this.setTitle("Serial port settings");
		this.setSize(300, 150);
		
		this.setLocationRelativeTo(parent);
		
		this.getContentPane().setLayout(new BorderLayout());
		
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		this.listPort();
		
		this.portCombo = new JComboBox(this.portList.toArray());
		this.portCombo.getModel().setSelectedItem(this.settings.getSerialPort());
		this.baudrateCombo = new JComboBox(this.baudrate);
		this.baudrateCombo.getModel().setSelectedItem(Integer.toString(this.settings.getBaudrate()));
		
		this.validBut = new JButton("Save");
		this.cancelBut = new JButton("Cancel");
		
		this.serPanel = new JPanel(new GridLayout(3, 2));
		this.serPanel.add(new JLabel("Port: "));
		this.serPanel.add(this.portCombo);
		this.serPanel.add(new JLabel("Baudrate: "));
		this.serPanel.add(this.baudrateCombo);
		
		this.butPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		this.butPanel.add(this.validBut);
		this.butPanel.add(this.cancelBut);
		
		this.add(this.serPanel, BorderLayout.CENTER);
		this.add(this.butPanel, BorderLayout.SOUTH);
		
		this.validBut.addActionListener(this);
		this.cancelBut.addActionListener(this);
		
		this.setVisible(true);
	}
	
	@SuppressWarnings("unchecked")
	private void listPort(){
		this.portList = new ArrayList <String> ();
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		
		while(portEnum.hasMoreElements()){
			CommPortIdentifier portID = (CommPortIdentifier) portEnum.nextElement();
			if(portID.getPortType() == CommPortIdentifier.PORT_SERIAL){
				this.portList.add(portID.getName());
			}
		}
	}
	
	public void actionPerformed(ActionEvent event){
		if(event.getSource() == this.cancelBut){
			this.dispose();
		} else if(event.getSource() == this.validBut){
			this.settings.setSerialPort(this.portCombo.getSelectedItem().toString());
			this.settings.setBaudrate(this.baudrateCombo.getSelectedItem().toString());
			this.dispose();
		}
	}
}
