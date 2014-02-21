package com.dipole.jwavetool.modules.wavesniffer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JToolBar;

import com.dipole.jwavetool.common.Common;
import com.dipole.jwavetool.common.Log;
import com.dipole.jwavetool.common.Settings;
import com.dipole.jwavetool.frame.FrameContainer;
import com.dipole.jwavetool.gui.SettingsDialog;
import com.dipole.jwavetool.modules.AbstractModule;
import com.dipole.jwavetool.modules.SettingsInterface;
import com.dipole.jwavetool.modules.wavesniffer.gui.SettingsPanel;


public class Sniffer 	extends AbstractModule
						implements ActionListener {

	private JToolBar toolBar;
	private JButton startBut, stopBut, cfgBut;
	private Thread serialThread = null;
	
	public Sniffer() {}
	
	@Override
	public void createModule(Settings settings) {
		this.settings = settings;
		
		this.startBut = new JButton(new ImageIcon(this.getClass().getResource(Common.ICONS_22_PATH +"media-record.png")));
		this.startBut.setToolTipText("Start Sniffing");
		//this.suspendBut = new JButton(new ImageIcon(this.getClass().getResource(Common.ICONS_22_PATH +"media-playback-pause.png")));
		//this.suspendBut.setToolTipText("Suspend sniffing");
		//this.suspendBut.setEnabled(false);
		this.stopBut = new JButton(new ImageIcon(this.getClass().getResource(Common.ICONS_22_PATH +"process-stop.png")));
		this.stopBut.setToolTipText("Stop sniffing");
		this.stopBut.setEnabled(false);
		this.cfgBut = new JButton(new ImageIcon(this.getClass().getResource(Common.ICONS_22_PATH +"preferences-system.png")));
		this.cfgBut.setToolTipText("Configure serial port");
		
		this.startBut.addActionListener(this);
		this.stopBut.addActionListener(this);
		this.cfgBut.addActionListener(this);
		
		this.toolBar = new JToolBar(this.getModuleName());
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
		if(serialThread != null) {
			this.serialThread.interrupt();
			this.fireModulerError(this.getModuleName(), "thread stopped");
		}
	}
	
	@Override
	@SuppressWarnings("unused")
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
			ArrayList <SettingsInterface> list = new ArrayList <SettingsInterface> (1);
			list.add(this.getSettingsPanel());
			
			SettingsDialog setDiag = new SettingsDialog(null, list);
		} else {
			Log.error(this.getModuleName() +": Unknown event");
		}
		
	}

}
