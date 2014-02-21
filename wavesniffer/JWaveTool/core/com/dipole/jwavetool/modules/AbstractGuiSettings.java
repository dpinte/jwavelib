package com.dipole.jwavetool.modules;

import javax.swing.JPanel;

import com.dipole.jwavetool.common.Settings;

public abstract class AbstractGuiSettings 	extends JPanel
											implements 	SettingsInterface {

	protected Settings settings;
	
	public AbstractGuiSettings(final Settings settings){
		this.settings = settings;
	}
}
