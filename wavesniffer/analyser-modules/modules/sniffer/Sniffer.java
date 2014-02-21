package modules.sniffer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JToolBar;

import common.Settings;
import common.Log;
import frame.FrameContainer;

import modules.AbstractModule;
import modules.SettingsInterface;
import modules.sniffer.gui.SettingsPanel;


public class Sniffer 	extends AbstractModule
						implements ActionListener {

	private JToolBar toolBar;
	private JButton startBut, stopBut, cfgBut;
	private Thread serialThread = null;
	
	public Sniffer() {}
	
	@Override
	public void createModule(Settings settings) {
		this.settings = settings;
		
		this.startBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/media-record.png")));
		this.startBut.setToolTipText("Start Sniffing");
		//this.suspendBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/media-playback-pause.png")));
		//this.suspendBut.setToolTipText("Suspend sniffing");
		//this.suspendBut.setEnabled(false);
		this.stopBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/process-stop.png")));
		this.stopBut.setToolTipText("Stop sniffing");
		this.stopBut.setEnabled(false);
		this.cfgBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/preferences-system.png")));
		this.cfgBut.setToolTipText("Configure serial port");
		
		this.startBut.addActionListener(this);
		this.stopBut.addActionListener(this);
		
		this.toolBar = new JToolBar("WaveSniffer capture");
		this.toolBar.add(this.startBut);
		this.toolBar.add(this.stopBut);
		this.toolBar.add(this.cfgBut);
	}

	@Override
	public JMenu getMenu() {
		return null;
	}

	@Override
	public String getModuleName() {
		return "WaveSniffer module";
	}

	@Override
	public String getModuleVersion() {
		return "0.1";
	}

	@Override
	public SettingsInterface getSettingsPanel() {
		return new SettingsPanel(this.settings);
	}

	@Override
	public JToolBar getToolBar() {
		return this.toolBar;
	}

	@Override
	public boolean hasMenu() {
		return false;
	}

	@Override
	public boolean hasSettingsPanel() {
		return true;
	}

	@Override
	public boolean hasToolBar() {
		return true;
	}

	@Override
	public Component getGUI() {
		return null;
	}

	@Override
	public boolean hasGUI() {
		return false;
	}
	
	@Override
	public void destroyModule() {
		if(serialThread != null)
			this.serialThread.interrupt();		
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource().equals(this.startBut)){
			SerialReader reader = new SerialReader(this,
													FrameContainer.getInstance(),
													this.settings.getString(Settings.SPORT_NAME),
													this.settings.getInt(Settings.SPORT_BAUD));
			this.serialThread = new Thread(reader);
			this.serialThread.start();
			
			this.startBut.setEnabled(false);
			this.stopBut.setEnabled(true);
			this.cfgBut.setEnabled(false);
			
		} else if(event.getSource().equals(this.stopBut)){
			this.serialThread.interrupt();
			
			this.startBut.setEnabled(true);
			this.stopBut.setEnabled(false);
			this.cfgBut.setEnabled(true);
			
		} else if(event.getSource().equals(this.cfgBut)){
			
		} else {
			Log.error(this.getModuleName() +": Unknown event");
		}
		
	}

}
