package com.dipole.jwavetool.modules;

import javax.swing.JPanel;

public interface SettingsInterface {
	public void loadSettings();
	public void saveSettings();
	public JPanel display();
	public String getTitle();
}
