package modules.sniffer.gui;

import gnu.io.CommPortIdentifier;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import modules.SettingsInterface;

import common.Settings;

public class SettingsPanel extends JPanel implements SettingsInterface {

	private JComboBox portCombo, baudrateCombo;
	private ArrayList <String> portList;
	private String[] baudrate = {"9600", "19200", "115200"};
	private Settings settings;
	
	public SettingsPanel(Settings settings) {
		this.settings = settings;
		
		this.setLayout(new GridLayout(3, 2));
		
		this.listPort();
		
		this.portCombo = new JComboBox(this.portList.toArray());
		this.baudrateCombo = new JComboBox(this.baudrate);
		
		this.loadSettings();
		
		this.add(new JLabel("Port: "));
		this.add(this.portCombo);
		this.add(new JLabel("Baudrate: "));
		this.add(this.baudrateCombo);
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
	
	@Override
	public String getTitle() {
		return "Serial port";
	}

	@Override
	public void loadSettings() {
		this.portCombo.getModel().setSelectedItem(this.settings.getString(Settings.SPORT_NAME));
		this.baudrateCombo.getModel().setSelectedItem(Integer.toString(this.settings.getInt(Settings.SPORT_BAUD)));
	}

	@Override
	public void saveSettings() {
		this.settings.setString(Settings.SPORT_NAME,
								this.portCombo.getSelectedItem().toString());
		this.settings.setString(Settings.SPORT_BAUD,
								this.baudrateCombo.getSelectedItem().toString());

	}

	
	@Override
	public JPanel display() {
		return this;
	}
}
